package at.tugraz.examreminder.service;


import android.content.Intent;
import android.preference.PreferenceManager;
import at.tugraz.examreminder.core.Exam;
import at.tugraz.examreminder.crawler.Crawler;
import at.tugraz.examreminder.crawler.TuGrazSearchCrawler;
import com.commonsware.cwac.wakeful.WakefulIntentService;

import java.util.Set;
import java.util.SortedSet;

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

    /**
     *
     * @param local_exams
     * @param new_exams
     * @return true if there are new or changed exams in the new_exams set compared to local_exams
     */
    public static boolean compareExamList(SortedSet<Exam> local_exams, SortedSet<Exam> new_exams){
        if(new_exams.size() > local_exams.size())
            return true;
        for(Exam exam : new_exams) {
            if(!local_exams.contains(exam))
                return true;
        }
        return false;
    }

	@Override
	protected void doWakefulWork(Intent intent) {
        Crawler crawler = getCrawlerInstance();
		PreferenceManager.getDefaultSharedPreferences(this).edit().putLong("pref_last_update", System.currentTimeMillis()).commit();
	}
}
