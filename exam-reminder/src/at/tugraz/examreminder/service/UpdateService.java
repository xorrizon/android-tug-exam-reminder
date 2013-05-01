package at.tugraz.examreminder.service;


import android.content.Intent;
import android.preference.PreferenceManager;
import at.tugraz.examreminder.crawler.Crawler;
import at.tugraz.examreminder.crawler.TuGrazSearchCrawler;
import com.commonsware.cwac.wakeful.WakefulIntentService;

public class UpdateService extends WakefulIntentService {
	private Crawler crawler;

	public UpdateService() {
		super(UpdateService.class.getName());
		crawler = new TuGrazSearchCrawler();
	}

	/**
	 * Sets the crawler to use. Default is the TuGrazSearchCrawler
	 * @param crawler
	 */
	public void setCrawler(Crawler crawler) {
		if(crawler != null)
			this.crawler = crawler;
	}

	@Override
	protected void doWakefulWork(Intent intent) {
		//@TODO
		PreferenceManager.getDefaultSharedPreferences(this).edit().putLong("pref_last_update", System.currentTimeMillis()).commit();
	}
}
