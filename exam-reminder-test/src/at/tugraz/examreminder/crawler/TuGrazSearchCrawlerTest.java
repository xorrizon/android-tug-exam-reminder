package at.tugraz.examreminder.crawler;

import android.test.AndroidTestCase;
import at.tugraz.examreminder.core.Course;
import java.util.ArrayList;
import java.util.List;

public class TuGrazSearchCrawlerTest extends AndroidTestCase {
  public void testCourseSearchDefault() {
    TuGrazSearchCrawler crawler = new TuGrazSearchCrawler();
    int numberOfCoursesWithoutCrawl = -1;
    int numberOfCoursesWithCrawl = -1;
    List<Course> courses = new ArrayList<Course>();
    numberOfCoursesWithoutCrawl = courses.size();

    crawler.getCourseList("Analysis");

    numberOfCoursesWithCrawl = courses.size();

    assertTrue("No valid data returned with this crawl!", numberOfCoursesWithCrawl != numberOfCoursesWithoutCrawl);
  }



}
