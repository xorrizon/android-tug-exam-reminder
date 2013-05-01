package at.tugraz.examreminder.crawler;

import android.test.AndroidTestCase;

public class TuGrazSearchCrawlerTest extends AndroidTestCase {

	public void testCrawler1() {
        TuGrazSearchCrawler crawler = new TuGrazSearchCrawler();

        assertEquals(crawler.generateSearchUrl(), true, false);

	}

}
