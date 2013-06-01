package at.tugraz.examreminder.crawler;

import android.test.InstrumentationTestCase;
import at.tugraz.examreminder.core.Course;
import at.tugraz.examreminder.core.Exam;

import java.io.IOException;
import java.util.List;
import java.util.SortedSet;

public class TuGrazSearchCrawlerTest extends InstrumentationTestCase {

    public void testCourseSearchDefault() throws IOException {
        TuGrazSearchCrawler crawler = new TuGrazSearchCrawler();
        List<Course> courses;
        SortedSet<Exam> exams;
        courses = crawler.getCourseListFromFile(getInstrumentation().getContext().getAssets().open("searchterm_analysis.xml"));
        exams = crawler.getExamsFromFile(getInstrumentation().getContext().getAssets().open("searchterm_analysis.xml"), courses.get(0));
        assertTrue("No valid data returned with this crawl!", courses.size() == 130);
        assertTrue(exams.size() != 0);
    }

}
