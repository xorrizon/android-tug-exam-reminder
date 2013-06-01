package at.tugraz.examreminder.crawler;

import java.io.*;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import android.os.Environment;
import android.util.Log;
import at.tugraz.examreminder.core.Course;
import at.tugraz.examreminder.core.Exam;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;


public class TuGrazSearchCrawler implements Crawler {

    private final static String LOGCAT_TAG = "TuGrazSearchCrawler";

    private final static String SEARCH_MACHINE_URI = "http://search.tugraz.at/search";
    private HashMap<String, String> SEARCH_MACHINE_URLI_ATTRIBUTES = new HashMap<String, String>() {{
        put("q", ""); // Searchstring
        put("site", "Alle");
        put("btnG", "Suchen");
        put("client", "tug_portal");
        put("output", "xml_no_dtd");
        put("sort", "date%3AD%3AL%3Ad1");
        put("entqr", "3");
        put("entqrm", "0");
        put("entsp", "a");
        put("oe", "UTF-8");
        put("ie", "UTF-8");
        put("ud", "1");
        put("filter", "1");
        put("hl", "de"); // Language
    }};

    private final static SimpleDateFormat SEARCH_MACHINE_RESULTS_DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy HH:mm");

    private final static String tempCoursesSearchDataXmlFilename = "courses.tmp";
    private final static String tempExamsSearchDataXmlFilename = "exams.tmp";


    private String generateSearchUrl(String searchTerm) throws UnsupportedEncodingException {
        String searchUrl = "";
        searchUrl += SEARCH_MACHINE_URI;
        SEARCH_MACHINE_URLI_ATTRIBUTES.remove("q");
        SEARCH_MACHINE_URLI_ATTRIBUTES.put("q", searchTerm);

        boolean isFirstAttribute = true;
        for (Map.Entry<String, String> entry : SEARCH_MACHINE_URLI_ATTRIBUTES.entrySet()) {
            if (isFirstAttribute) {
                searchUrl += "?" + entry.getKey() + "=" + URLEncoder.encode(entry.getValue(), "UTF8");
                isFirstAttribute = false;
            }
            searchUrl += "&" + entry.getKey() + "=" + URLEncoder.encode(entry.getValue(), "UTF8");
        }
        return searchUrl;
    }

    private void getResponseXmlAndWriteToFile(String searchTerm, File file) {
        String searchUrl;
        try {
            searchUrl = generateSearchUrl(searchTerm);
        } catch (UnsupportedEncodingException e) {
            Log.v(LOGCAT_TAG, "UnsupportedEncodingException");
            return;
        }
        HttpClient httpClient = new DefaultHttpClient();
        HttpResponse httpResponse;
        try {
            httpResponse = httpClient.execute(new HttpGet(searchUrl));
            StatusLine statusLine = httpResponse.getStatusLine();
            if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                httpResponse.getEntity().writeTo(fileOutputStream);
                fileOutputStream.close();
            } else {
                httpResponse.getEntity().getContent().close();
                throw new IOException(statusLine.getReasonPhrase());
            }
        } catch (ClientProtocolException e) {
            Log.v(LOGCAT_TAG, e.toString());
        } catch (IOException e) {
            Log.v(LOGCAT_TAG, e.toString());
        }
    }

    @Override
    public List<Course> getCourseList(String searchTerm) {
        File tempFileOnDevice = new File(Environment.getExternalStorageDirectory(), tempCoursesSearchDataXmlFilename);
        getResponseXmlAndWriteToFile(searchTerm, tempFileOnDevice);
        List<Course> foundCourse;
        try {
            foundCourse = getCourseListFromFile(new FileInputStream(tempFileOnDevice));
        } catch (IOException e) {
            Log.v(LOGCAT_TAG, e.toString());
            return null;
        }
        return foundCourse;
    }

    @Override
    public SortedSet<Exam> getExams(Course course) {
        File tempFileOnDevice = new File(Environment.getExternalStorageDirectory(), tempExamsSearchDataXmlFilename);
        getResponseXmlAndWriteToFile(course.name, tempFileOnDevice);
        SortedSet<Exam> foundExams;

        try {
            foundExams = getExamsFromFile(new FileInputStream(tempFileOnDevice), course);
            } catch (IOException e) {
            Log.v(LOGCAT_TAG, e.toString());
            return null;
        }
        return foundExams;
    }


    public List<Course> getCourseListFromFile(InputStream inputstream) throws IOException {
        List<Course> foundCourse = new ArrayList<Course>();
        Map<String, String> currentModuleMap = new HashMap<String, String>();
        DataInputStream in = new DataInputStream(inputstream);
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        String currentTagValue;
        String currentTagAttribute;
        String currentLine;
        Course currentCourse;
        while ((currentLine = br.readLine()) != null) {
            if (currentLine.contains("<MODULE_RESULT>")) {
                currentModuleMap.clear();
                while ((currentLine = br.readLine()) != null) {
                    if (currentLine.contains("</MODULE_RESULT>")) {
                        if (currentModuleMap.containsKey("WEB SERVICE") && (currentModuleMap.get("WEB SERVICE").toString().equals("CBO"))) {
                            currentCourse = new at.tugraz.examreminder.core.Course();
                            currentCourse.name = currentModuleMap.get("courseName");
                            currentCourse.number = currentModuleMap.get("courseCode");
                            currentCourse.term = currentModuleMap.get("teachingTerm");
                            currentCourse.type = currentModuleMap.get("teachingActivityID");
                            if (currentModuleMap.containsKey("persons_name")) {
                                currentCourse.lecturer = currentModuleMap.get("persons_name");
                            } else if (currentModuleMap.containsKey("persons_name1")) {
                                currentCourse.lecturer = currentModuleMap.get("persons_name1");
                            }
                            foundCourse.add(currentCourse);
                        }
                        currentModuleMap.clear();
                    }
                    if (currentLine.contains("<Field") && currentLine.contains("</Field>")) {
                        currentTagAttribute = currentLine.substring(currentLine.indexOf("=\"") + 2, currentLine.indexOf("\">"));
                        currentTagValue = currentLine.substring(currentLine.indexOf("\">") + 2, currentLine.indexOf("</Field>"));
                        currentModuleMap.put(currentTagAttribute, currentTagValue);
                    } else if (currentLine.contains("<Field>")) {
                        throw new IOException("Format of returned data not recognized!");
                    }
                }
            }
        }
        in.close();
        return foundCourse;
    }

    public SortedSet<Exam> getExamsFromFile(InputStream inputstream, Course course) throws IOException{
        SortedSet<Exam> foundExams = new TreeSet<Exam>();
        Map<String, String> currentModuleMap = new HashMap<String, String>();
        DataInputStream in = new DataInputStream(inputstream);
        BufferedReader br = new BufferedReader(new InputStreamReader(in));

        String currentTagValue;
        String currentTagAttribute;
        String currentLine;
        Exam currentExam;
        String currentCourseId;
        String currentCourseName;


        while (((currentLine = br.readLine()) != null)) {
            if (currentLine.contains("<MODULE_RESULT>")) {
                currentModuleMap.clear();
                while (((currentLine = br.readLine()) != null)) {
                    if (currentLine.contains("</MODULE_RESULT>")) {
                        if (currentModuleMap.containsKey("WEB SERVICE") && (currentModuleMap.get("WEB SERVICE").toString().equals("EBO"))) {
                            currentCourseId = currentModuleMap.get("courseID");
                            currentCourseName = currentModuleMap.get("courseCode");

                            if(currentCourseName.equals(course.name) && currentCourseId.equals(course.number)) {
                                currentExam = new Exam(course);

                                try {
                                    currentExam.from = SEARCH_MACHINE_RESULTS_DATE_FORMAT.parse(currentModuleMap.get("examStart"));
                                    currentExam.to = SEARCH_MACHINE_RESULTS_DATE_FORMAT.parse(currentModuleMap.get("examEnd"));
                                } catch (ParseException e) {
                                    throw new IOException("Dateformat of returned data not recognized!");
                                }

                                currentExam.place = currentModuleMap.get("examLocation");
                                currentExam.term = currentModuleMap.get("teachingTerm");
                                currentExam.lecturer = currentModuleMap.get("lecturer");
                                currentExam.examinar = currentModuleMap.get("examinerName");
                                currentExam.registrationStart = null;

                                //TODO: add cancel Deadline data

                                try {
                                    currentExam.registrationEnd = SEARCH_MACHINE_RESULTS_DATE_FORMAT.parse(currentModuleMap.get("registerDeadline"));
                                } catch (ParseException e) {
                                    throw new IOException("Dateformat of returned data not recognized!");
                                }

                                currentExam.participants = Integer.parseInt(currentModuleMap.get("numberOfParticipants"));
                                currentExam.participants_max = Integer.parseInt(currentModuleMap.get("maximumNumberOfParticipants"));
                                currentExam.updated_at = null;
                                foundExams.add(currentExam);
                            }
                        }
                        currentModuleMap.clear();
                    }
                    if (currentLine.contains("<Field") && currentLine.contains("</Field>")) {
                        currentTagAttribute = currentLine.substring(currentLine.indexOf("=\"") + 2, currentLine.indexOf("\">"));
                        currentTagValue = currentLine.substring(currentLine.indexOf("\">") + 2, currentLine.indexOf("</Field>"));
                        currentModuleMap.put(currentTagAttribute, currentTagValue);
                    } else if (currentLine.contains("<Field>")) {
                        throw new IOException("Format of returned data not recognized!");
                    }
                }
            }
        }
        in.close();
        return foundExams;
    }
}
