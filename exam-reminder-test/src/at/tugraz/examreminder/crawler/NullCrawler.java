package at.tugraz.examreminder.crawler;

import at.tugraz.examreminder.core.Course;
import at.tugraz.examreminder.core.Exam;

import java.util.List;
import java.util.SortedSet;

public class NullCrawler implements Crawler {

    public NullCrawler(){
    }

    @Override
    public List<Course> getCourseList(String searchTerm) {
        return null;
    }

    @Override
    public SortedSet<Exam> getExams(Course course) {
        return null;
    }
}