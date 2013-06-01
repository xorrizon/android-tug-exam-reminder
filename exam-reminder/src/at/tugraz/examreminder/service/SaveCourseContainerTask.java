package at.tugraz.examreminder.service;

import android.os.AsyncTask;
import at.tugraz.examreminder.core.Course;
import at.tugraz.examreminder.core.CourseContainer;

import java.util.List;

public class SaveCourseContainerTask extends AsyncTask<Void, Void, Void> {
    private CourseListSerializer courseListSerializer;

    public SaveCourseContainerTask(CourseListSerializer courseListSerializer) {
        this.courseListSerializer = courseListSerializer;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        List<Course> courseList = CourseContainer.instance().getCourseList();
        courseListSerializer.saveCourseListToFile(courseList);
        return null;
    }
}
