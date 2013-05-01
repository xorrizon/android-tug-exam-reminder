package at.tugraz.examreminder.service;


import android.content.Intent;
import android.preference.PreferenceManager;
import at.tugraz.examreminder.crawler.Crawler;
import at.tugraz.examreminder.crawler.TuGrazSearchCrawler;
import com.commonsware.cwac.wakeful.WakefulIntentService;

public class UpdateService extends WakefulIntentService {
	private static Class<? extends Crawler> crawler_to_use;

	public UpdateService() {
		super(UpdateService.class.getName());
        if(crawler_to_use == null)
            crawler_to_use = TuGrazSearchCrawler.class;
	}

	/**
	 * Sets the crawler_to_use to use. Default is the TuGrazSearchCrawler.class
	 * @param crawler
	 */
	public static void setCrawlerToUse(Class<? extends Crawler> crawler) {
        crawler_to_use = crawler;
	}

    protected static Crawler getCrawlerInstance(){
        Crawler crawler = null;
        try {
            if(crawler_to_use != null)
                crawler = crawler_to_use.newInstance();
        } catch (Exception e) {
            // Damn you java reflection
        }
        if(crawler == null)
            crawler = new TuGrazSearchCrawler();
        return crawler;
    }


	@Override
	protected void doWakefulWork(Intent intent) {
        Crawler crawler = getCrawlerInstance();
		PreferenceManager.getDefaultSharedPreferences(this).edit().putLong("pref_last_update", System.currentTimeMillis()).commit();
	}
}
