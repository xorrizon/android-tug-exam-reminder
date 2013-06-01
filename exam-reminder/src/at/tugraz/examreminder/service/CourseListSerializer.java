package at.tugraz.examreminder.service;

import android.os.Environment;
import android.util.Log;
import at.tugraz.examreminder.core.Course;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class CourseListSerializer implements Observer {
    private static final String LOGCAT_TAG = "CourseListSerializer";
    private static final String FILENAME = "saved_course_container.json";

    public void saveCourseListToFile(List<Course> courseList) {
        File file = new File(Environment.getExternalStorageDirectory(), FILENAME);
        String json = courseListToJson(courseList);
        FileWriter fileWriter;
        try {
            fileWriter = new FileWriter(file, false);
            fileWriter.write(json);
            fileWriter.flush();
            fileWriter.close();
            Log.i(LOGCAT_TAG, "Finished saving courseContainer");

        } catch (IOException e) {

        }
    }

    public static String courseListToJson(List<Course> courseList) {

        return new Gson().toJson(courseList);
    }

    @Override
    public void update(Observable observable, Object o) {
        SaveCourseContainerTask saveCourseContainerTask = new SaveCourseContainerTask(this);
        saveCourseContainerTask.execute();
    }

    public List<Course> loadCourseListFromFile(File file) {
        if(file == null || !file.exists()) {
            return new ArrayList<Course>();
        }
        String json = null;
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader((file)));
            StringBuffer stringBuffer = new StringBuffer();
            String line = bufferedReader.readLine();
            while (line != null) {
                stringBuffer.append(line);
                stringBuffer.append("\n");
                line = bufferedReader.readLine();
            }
            json = stringBuffer.toString();
        } catch (Exception e) {

        }
        if(json == null || json.length() == 0) {
            return new ArrayList<Course>();
        }
        return jsonToCourseList(json);
    }

    public List<Course> loadCourseListFromFile() {
        return loadCourseListFromFile(new File(Environment.getExternalStorageDirectory(), FILENAME));
    }

    public static List<Course> jsonToCourseList(String json) {
        Type collectionType = new TypeToken<ArrayList<Course>>(){}.getType();
        return new Gson().fromJson(json, collectionType);
    }
}
