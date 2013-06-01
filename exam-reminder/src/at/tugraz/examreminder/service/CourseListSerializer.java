package at.tugraz.examreminder.service;

import android.util.Log;
import at.tugraz.examreminder.ExamReminderApplication;
import at.tugraz.examreminder.core.CourseContainer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class CourseListSerializer {
    private static final String LogcatTag = "CourseListSerializer";
    private static final String filename = "saved_course_container.json";


    public void saveCourseListToFile(CourseContainer courseContainer) {
        File file = new File(ExamReminderApplication.getAppContext().getExternalFilesDir(null), filename);
        String json = courseContainerToJson(courseContainer);
        FileWriter fileWriter;
        try {
            fileWriter = new FileWriter(file, false);
            fileWriter.write(json);
            fileWriter.flush();
            fileWriter.close();
            Log.i(LogcatTag, "Finished saving courseContainer");

        } catch (IOException e) {

        }
    }

    private String courseContainerToJson(CourseContainer courseContainer) {
        return null;  //To change body of created methods use File | Settings | File Templates.
    }
}
