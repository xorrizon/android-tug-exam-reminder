package at.tugraz.examreminder.crawler;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.SortedSet;

import android.os.AsyncTask;
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

    final static String FieldStartTagCourseName = "<Field name=\"courseName\">";
    final static String FieldStartTagCourseCode = "<Field name=\"courseCode\">";
    final static String FieldEndTag = "</Field>";

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

    public String getResponseString(String searchUrl) {
        String uri = generateSearchUrl(searchUrl);
        HttpClient httpclient = new DefaultHttpClient();
        HttpResponse response;
        String responseString = null;
        try {
            response = httpclient.execute(new HttpGet(uri));
            StatusLine statusLine = response.getStatusLine();
            if(statusLine.getStatusCode() == HttpStatus.SC_OK){
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                response.getEntity().writeTo(out);
                responseString = out.toString();
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
        return responseString;
    }

	@Override
	public List<Course> getCourseList(String searchTerm) {
        Map<String, String> coursemap = new HashMap<String, String>();
        String response = getResponseString(searchTerm);
        String lines[] = response.split("\\r?\\n");
        int i = 0;
        String currentCourseName = "";
        String currentCourseID = "";
        Log.v("SPAM", ""+lines.length);
        for(String line :lines) {
            if(line.contains("<MODULE_RESULT>")) {
                i++;
            }

            if(line.contains(FieldStartTagCourseName)) {
                currentCourseName = line.substring(line.indexOf(FieldStartTagCourseName)+FieldStartTagCourseName.length(), line.indexOf(FieldEndTag));

            }
            if(line.contains(FieldStartTagCourseCode)) {
                currentCourseID = line.substring(line.indexOf(FieldStartTagCourseCode)+FieldStartTagCourseCode.length(), line.indexOf(FieldEndTag));
                if(!coursemap.containsKey(currentCourseID)) {
                    coursemap.put(currentCourseID, currentCourseName);
                }
                Log.v("SPAM", i + ": " + currentCourseID + ", " + currentCourseName);
            }
        }
        Log.v("courseName", ""+i);
        return null;
	}

	@Override
	public SortedSet<Exam> getExams(Course course) {
		// TODO Auto-generated method stub
		return null;
	}


}
