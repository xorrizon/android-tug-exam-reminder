package at.tugraz.examreminder.crawler;

import android.test.InstrumentationTestCase;
import at.tugraz.examreminder.core.Course;
import at.tugraz.examreminder.core.Exam;
import junit.framework.Assert;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.SortedSet;

public class TuGrazSearchCrawlerTest extends InstrumentationTestCase {

    public void testCourseSearchDefault() throws IOException {
        TuGrazSearchCrawler crawler = new TuGrazSearchCrawler();
        List<Course> courses;
        SortedSet<Exam> exams;

        courses = crawler.getCourseListFromFile(getInstrumentation().getContext().getAssets().open("searchterm_analysis.xml"));
        exams = crawler.getExamsFromFile(getInstrumentation().getContext().getAssets().open("searchterm_analysis.xml"), courses.get(6));
        assertEquals("Wrong number of courses found!", courses.size(), 130);
        assertEquals("Wrong number of exams found!", exams.size(), 1);
        exams = crawler.getExamsFromFile(getInstrumentation().getContext().getAssets().open("searchterm_analysis.xml"), courses.get(0));
        assertEquals("Wrong number of exams found!", exams.size(), 0);

        courses = crawler.getCourseListFromFile(getInstrumentation().getContext().getAssets().open("searchterm_operating.xml"));
        exams = crawler.getExamsFromFile(getInstrumentation().getContext().getAssets().open("searchterm_operating.xml"), courses.get(0));
        assertEquals("Wrong number of courses found!", courses.size(), 6);
        assertEquals("Wrong number of exams found!", exams.size(), 0);
        exams = crawler.getExamsFromFile(getInstrumentation().getContext().getAssets().open("searchterm_operating.xml"), courses.get(5));
        assertEquals("Wrong number of exams found!", exams.size(), 0);
    }

    public void testSearchFileErrorHandling() throws IOException {
        TuGrazSearchCrawler crawler = new TuGrazSearchCrawler();
        List<Course> courses = null;
        SortedSet<Exam> exams = null;
        try{
            courses = crawler.getCourseListFromFile(getInstrumentation().getContext().getAssets().open(""));
            Assert.fail("No FileNotFoundException thrown!");
        }
        catch(FileNotFoundException e) {
            assertEquals("Courses found, even when there was no valid data file!", courses == null ? 0 : courses.size(), 0);
        }
        try{
            exams = crawler.getExamsFromFile(getInstrumentation().getContext().getAssets().open(""), null);
            Assert.fail("No FileNotFoundException thrown!");
        }
        catch(FileNotFoundException e) {
            assertEquals("Exams found, even when there was no valid data file!", exams == null ? 0 : exams.size(), 0);
        }
    }

}
