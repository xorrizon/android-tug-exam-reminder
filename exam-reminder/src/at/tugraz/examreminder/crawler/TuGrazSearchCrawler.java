package at.tugraz.examreminder.crawler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import android.util.Log;
import at.tugraz.examreminder.ExamReminderApplication;
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

    final static String SEARCH_MACHINE_URI = "http://search.tugraz.at/search";
    HashMap<String, String> SEARCH_MACHINE_URLI_ATTRIBUTES = new HashMap<String, String>() {{
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

    final static SimpleDateFormat SEARCH_MACHINE_RESULTS_DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy HH:mm");

    final static String tempSearchDataXmlFilename = "examreminder.tmp";


    public String generateSearchUrl(String searchTerm) {
        String searchUrl = "";
        searchUrl += SEARCH_MACHINE_URI;
        SEARCH_MACHINE_URLI_ATTRIBUTES.remove("q");
        SEARCH_MACHINE_URLI_ATTRIBUTES.put("q", searchTerm);

        boolean isFirstAttribute = true;
        for (Map.Entry<String, String> entry : SEARCH_MACHINE_URLI_ATTRIBUTES.entrySet()) {
            if (isFirstAttribute) {
                searchUrl += "?" + entry.getKey() + "=" + entry.getValue();
                isFirstAttribute = false;
            }
            searchUrl += "&" + entry.getKey() + "=" + entry.getValue();
        }
        return searchUrl;
    }

    public void getResponseXmlAndWriteToFile(String searchTerm) {
        String searchUrl = generateSearchUrl(searchTerm);
        HttpClient httpClient = new DefaultHttpClient();
        HttpResponse httpResponse;
        try {
            httpResponse = httpClient.execute(new HttpGet(searchUrl));
            StatusLine statusLine = httpResponse.getStatusLine();
            if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                File file = new File(ExamReminderApplication.getAppContext().getExternalFilesDir(null), tempSearchDataXmlFilename);
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                httpResponse.getEntity().writeTo(fileOutputStream);
                fileOutputStream.close();
            } else {
                httpResponse.getEntity().getContent().close();
                throw new IOException(statusLine.getReasonPhrase());
            }
        } catch (ClientProtocolException e) {
            Log.v("TuGrazSearchCrawler", e.toString());
        } catch (IOException e) {
            Log.v("TuGrazSearchCrawler", e.toString());
        }
    }

    @Override
    public List<Course> getCourseList(String searchTerm) {
        List<Course> foundCourse = new ArrayList<Course>();
        getResponseXmlAndWriteToFile("");
        Map<String, String> currentModuleMap = new HashMap<String, String>();
        File tempfile = new File(ExamReminderApplication.getAppContext().getExternalFilesDir(null), tempSearchDataXmlFilename);
        Scanner scanner = null;

        try {
            scanner = new Scanner(tempfile);
        } catch (FileNotFoundException e) {
            Log.v("TuGrazSearchCrawler", e.toString());
        }

        String currentTagValue;
        String currentTagAttribute;
        String currentLine;
        Course currentCourse;

        assert scanner != null;
        while (scanner.hasNextLine()) {
            currentLine = scanner.nextLine();
            if (currentLine.contains("<MODULE_RESULT>")) {
                currentModuleMap.clear();
                while (scanner.hasNextLine()) {
                    currentLine = scanner.nextLine();
                    if (currentLine.contains("</MODULE_RESULT>")) {
                        if (currentModuleMap.containsKey("WEB SERVICE") && (currentModuleMap.get("WEB SERVICE").toString().equals("CBO"))) {
                            currentCourse = new Course();
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
                        // TODO: for very very long descriptions;
                    }
                }
            }
        }

        return foundCourse;
    }

    @Override
    public SortedSet<Exam> getExams(Course course) {
        SortedSet<Exam> foundExams = new TreeSet<Exam>();
        getResponseXmlAndWriteToFile("");
        Map<String, String> currentModuleMap = new HashMap<String, String>();
        File tempfile = new File(ExamReminderApplication.getAppContext().getExternalFilesDir(null), tempSearchDataXmlFilename);
        Scanner scanner = null;

        try {
            scanner = new Scanner(tempfile);
        } catch (FileNotFoundException e) {
            Log.v("TuGrazSearchCrawler", e.toString());
        }

        String currentTagValue;
        String currentTagAttribute;
        String currentLine;
        Exam currentExam;

        while (scanner.hasNextLine()) {
            currentLine = scanner.nextLine();
            if (currentLine.contains("<MODULE_RESULT>")) {
                currentModuleMap.clear();
                while (scanner.hasNextLine()) {
                    currentLine = scanner.nextLine();
                    if (currentLine.contains("</MODULE_RESULT>")) {
                        if (currentModuleMap.containsKey("WEB SERVICE") && (currentModuleMap.get("WEB SERVICE").toString().equals("EBO"))) {
                            currentExam = new Exam();

                            try {
                                currentExam.from = SEARCH_MACHINE_RESULTS_DATE_FORMAT.parse(currentModuleMap.get("examStart"));
                            } catch (ParseException e) {
                                currentExam.from = null;
                            }

                            try {
                                currentExam.to = SEARCH_MACHINE_RESULTS_DATE_FORMAT.parse(currentModuleMap.get("examEnd"));
                            } catch (ParseException e) {
                                currentExam.to = null;
                            }
                            currentExam.place = currentModuleMap.get("examLocation");
                            currentExam.term = currentModuleMap.get("teachingTerm");
                            currentExam.lecturer = currentModuleMap.get("lecturer");
                            currentExam.examinar = currentModuleMap.get("examinerName");
                            currentExam.registrationStart = null;
                            try {
                                currentExam.to = SEARCH_MACHINE_RESULTS_DATE_FORMAT.parse(currentModuleMap.get("examStart"));
                            } catch (ParseException e) {
                                currentExam.to = null;
                            }
                            currentExam.participants = Integer.parseInt(currentModuleMap.get("numberOfParticipants"));
                            currentExam.participants_max = Integer.parseInt(currentModuleMap.get("maximumNumberOfParticipants"));
                            currentExam.updated_at = null;
                            foundExams.add(currentExam);

                        }
                        currentModuleMap.clear();
                    }
                    if (currentLine.contains("<Field") && currentLine.contains("</Field>")) {
                        currentTagAttribute = currentLine.substring(currentLine.indexOf("=\"") + 2, currentLine.indexOf("\">"));
                        currentTagValue = currentLine.substring(currentLine.indexOf("\">") + 2, currentLine.indexOf("</Field>"));
                        currentModuleMap.put(currentTagAttribute, currentTagValue);

                    } else if (currentLine.contains("<Field>")) {
                        // TODO: for very very long descriptions;
                    }
                }
            }
        }

        return foundExams;
    }
}
