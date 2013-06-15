package at.tugraz.examreminder.service;


import android.test.InstrumentationTestCase;
import at.tugraz.examreminder.core.Course;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CourseListSerializerTest extends InstrumentationTestCase{

    public void testFromJasonToListToJason() throws IOException {
    List<Course> courses = new ArrayList<Course>();
        CourseListSerializer courseListSerializer = new CourseListSerializer();
        courses = courseListSerializer.loadCourseListFromFile(getInstrumentation().getContext().getAssets().open("saved_course_container_sample.json"));
        assertEquals("Wrong number of courses serialized.", 2, courses.size());
        assertEquals("Wrong number of exams serialized.", 1, courses.get(0).exams.size());
        assertEquals("Wrong number of exams serialized.", 1, courses.get(1).exams.size());
        assertEquals("Other coursename expected.", "IT-Sicherheit", courses.get(0).name);
        courseListSerializer.saveCourseListToFile(courses);
        List<Course> courses_new = courseListSerializer.loadCourseListFromFile();
        assertEquals("Deserialization error.", courses.size(), courses_new.size());
        assertEquals("Deserialization error.", courses.get(0), courses.get(0));
        courses.clear();
        courses_new.clear();
    }
}

