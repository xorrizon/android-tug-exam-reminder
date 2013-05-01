package at.tugraz.examreminder.crawler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
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

		final static String searchmachineUrl = "http://search.tugraz.at/search";
		HashMap<String, String> attributeList = new HashMap<String, String>() {{
			put("q", "analysis"); // Searchstring
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

    final static String fieldStartTagCourseName = "<Field name=\"courseName\">";
    final static String fieldStartTagCourseCode = "<Field name=\"courseCode\">";
    final static String fieldEndTag = "</Field>";

    final static String tempFilename = "examreminder.tmp";

	public String generateSearchUrl(String SearchTerm) {
		String urlstring = searchmachineUrl;
		//Set entrySet= attributeList.entrySet();
		boolean isFirstAttribute = true;
		for (Map.Entry<String, String> entry : attributeList.entrySet()) {
			if(isFirstAttribute) {
				urlstring += "?" + entry.getKey() + "=" + entry.getValue();
				isFirstAttribute = false;
			}
			urlstring +=  "&" + entry.getKey() + "=" + entry.getValue();
		}
		return urlstring;
	}

    public void getResponseAndWriteToFile(String searchUrl) {
        String uri = generateSearchUrl(searchUrl);
        HttpClient httpclient = new DefaultHttpClient();
        HttpResponse response;
        String responseString = null;
        try {
            response = httpclient.execute(new HttpGet(uri));
            StatusLine statusLine = response.getStatusLine();
            if(statusLine.getStatusCode() == HttpStatus.SC_OK){
                File file = new File(ExamReminderApplication.getAppContext().getExternalFilesDir(null), tempFilename);
                FileOutputStream out = new FileOutputStream(file);
                response.getEntity().writeTo(out);
                out.close();
            } else{
                //Closes the connection.
                response.getEntity().getContent().close();
                throw new IOException(statusLine.getReasonPhrase());
            }
        } catch (ClientProtocolException e) {
            Log.v("SPAM", e.toString());
        } catch (IOException e) {
            Log.v("SPAM", e.toString());
        }
        return;
    }

	@Override
	public List<Course> getCourseList(String searchTerm) {
        List<Course> foundCourse = new ArrayList<Course>();
        getResponseAndWriteToFile("");
        Map<String, String> coursemap = new HashMap<String, String>();
        Map<String, String> currentModuleMap = new HashMap<String, String>();
        File tempfile = new File(ExamReminderApplication.getAppContext().getExternalFilesDir(null), tempFilename);
        Scanner scanner = null;
        try {
            scanner = new Scanner(tempfile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        int i = 0;
        String currentTagValue = "";
        String currentTagAttribute = "";
        String line;
        Course currentCourse = null;
        while (scanner.hasNextLine()) {
            line = scanner.nextLine();
            if(line.contains("<MODULE_RESULT>")) {
                currentModuleMap.clear();
                while(scanner.hasNextLine()) {
                    line = scanner.nextLine();
                    if(line.contains("</MODULE_RESULT>")) {
                        if(currentModuleMap.containsKey("WEB SERVICE") && (currentModuleMap.get("WEB SERVICE").toString().equals("CBO"))) {
                            currentCourse = new Course();
                            currentCourse.name = currentModuleMap.get("courseName");
                            currentCourse.number = currentModuleMap.get("courseCode");
                            currentCourse.term = currentModuleMap.get("teachingTerm");
                            currentCourse.type = currentModuleMap.get("teachingActivityID");
                            if(currentModuleMap.containsKey("persons_name")) {
                                currentCourse.lecturer = currentModuleMap.get("persons_name");
                            }
                            else if(currentModuleMap.containsKey("persons_name1")) {
                                currentCourse.lecturer = currentModuleMap.get("persons_name1");
                            }
                            Log.v("TuGrazSearchCrawler", foundCourse.size()+" courses added");

                            foundCourse.add(currentCourse);

                        }
                        currentModuleMap.clear();
                    }
                    if(line.contains("<Field") && line.contains("</Field>")) {
                        currentTagAttribute = line.substring(line.indexOf("=\"")+ 2, line.indexOf("\">"));
                        currentTagValue = line.substring(line.indexOf("\">")+ 2, line.indexOf("</Field>"));
                        currentModuleMap.put(currentTagAttribute, currentTagValue);

                    }
                    else if(line.contains("<Field>")) {
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
        getResponseAndWriteToFile("");
        Map<String, String> coursemap = new HashMap<String, String>();
        Map<String, String> currentModuleMap = new HashMap<String, String>();
        File tempfile = new File(ExamReminderApplication.getAppContext().getExternalFilesDir(null), tempFilename);
        Scanner scanner = null;
        try {
            scanner = new Scanner(tempfile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        int i = 0;
        String currentTagValue = "";
        String currentTagAttribute = "";
        String line;
        Exam currentExam = null;
        while (scanner.hasNextLine()) {
            line = scanner.nextLine();
            if(line.contains("<MODULE_RESULT>")) {
                currentModuleMap.clear();
                while(scanner.hasNextLine()) {
                    line = scanner.nextLine();
                    if(line.contains("</MODULE_RESULT>")) {
                        if(currentModuleMap.containsKey("WEB SERVICE") && (currentModuleMap.get("WEB SERVICE").toString().equals("EBO"))) {
                            currentExam = new Exam();

                            //currentExam.from = new Date(currentModuleMap.get("examStart"));
                            //currentExam.to = new Date(currentModuleMap.get("examEnd"));
                            currentExam.place = currentModuleMap.get("examLocation");
                            currentExam.term = currentModuleMap.get("teachingTerm");
                            currentExam.lecturer = currentModuleMap.get("lecturer");
                            currentExam.examinar = currentModuleMap.get("examinerName");
                            currentExam.registrationStart = null;
                            //currentExam.registrationEnd = new Date(currentModuleMap.get("examStart"));
                            currentExam.participants = Integer.parseInt(currentModuleMap.get("numberOfParticipants"));
                            currentExam.participants_max = Integer.parseInt(currentModuleMap.get("maximumNumberOfParticipants"));
                            currentExam.updated_at = null;
                            foundExams.add(currentExam);

                        }
                        currentModuleMap.clear();
                    }
                    if(line.contains("<Field") && line.contains("</Field>")) {
                        currentTagAttribute = line.substring(line.indexOf("=\"")+ 2, line.indexOf("\">"));
                        currentTagValue = line.substring(line.indexOf("\">")+ 2, line.indexOf("</Field>"));
                        currentModuleMap.put(currentTagAttribute, currentTagValue);

                    }
                    else if(line.contains("<Field>")) {
                        // TODO: for very very long descriptions;
                    }
                }
            }
        }

        for(Exam exam : foundExams) {
            Log.v("SPAM", "=================");
            Log.v("SPAM", "exam.lecturer = " + exam.lecturer);
            Log.v("SPAM", "exam.from = " + exam.from);
        }
        return foundExams;
	}


}
