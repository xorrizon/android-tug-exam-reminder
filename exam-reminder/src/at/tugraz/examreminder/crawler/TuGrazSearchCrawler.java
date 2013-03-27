package at.tugraz.examreminder.crawler;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.os.AsyncTask;
import android.util.Log;
import at.tugraz.examreminder.core.Course;
import at.tugraz.examreminder.core.Exam;

public class TuGrazSearchCrawler implements Crawler {

		final static String searchmachineUrl = "http://search.tugraz.at/search";
		HashMap<String, String> attributeList = new HashMap<String, String>() {{
			put("q", "hello");    
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
		}};
		
	public String generateSearchUrl() {
		String urlstring = searchmachineUrl;
		Set entrySet= attributeList.entrySet();
		boolean isFirstAttribute = true; 
		for (Map.Entry<String, String> entry : attributeList.entrySet()) {
			if(isFirstAttribute) {
				urlstring += "?" + entry.getKey() + "=" + entry.getValue();
				isFirstAttribute = false;
			}
			urlstring +=  "&" + entry.getKey() + "=" + entry.getValue();
		}
    
		RequestTask reqtask = new RequestTask();
	    Log.v("url", urlstring);
		reqtask.execute(urlstring);
		return "0";
    
	}
		
		

	@Override
	public List<Course> getCourseList(String searchTerm) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Exam> getExams(Course course) {
		// TODO Auto-generated method stub
		return null;
	}

	private List<String> getResultXml(String searchterm) {
		System.out.println(generateSearchUrl());
		return null;
	}
	
	class RequestTask extends AsyncTask<String, String, String> {
		@Override
		protected String doInBackground(String... uri) {
    	HttpClient httpclient = new DefaultHttpClient();
			HttpResponse response;
			String responseString = null;
      try {
        response = httpclient.execute(new HttpGet(uri[0]));
        StatusLine statusLine = response.getStatusLine();
        if(statusLine.getStatusCode() == HttpStatus.SC_OK){
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        response.getEntity().writeTo(out);
        out.close();
        responseString = out.toString();
        } else{
        //Closes the connection.
        response.getEntity().getContent().close();
                                      throw new IOException(statusLine.getReasonPhrase());
        }
      } catch (ClientProtocolException e) {
        //TODO Handle problems..
      } catch (IOException e) {
        //TODO Handle problems..
      }
      return responseString;
    } 

    @Override
    protected void onPostExecute(String result) {
      super.onPostExecute(result);
     Log.v("SPAM SPAM SPAM", result);
    	}

	}	
}
