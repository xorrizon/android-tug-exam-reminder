package at.tugraz.examreminder.crawler;

import android.test.AndroidTestCase;
import android.util.Log;

public class TuGrazSearchCrawlerTest extends AndroidTestCase {

	public void testCrawler1() {
        TuGrazSearchCrawler crawler = new TuGrazSearchCrawler();
        crawler.getCourseList("");

        assertEquals(true, false);
    }
}
