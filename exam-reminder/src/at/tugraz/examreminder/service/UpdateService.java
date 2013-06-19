package at.tugraz.examreminder.service;


import android.app.Notification;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.util.Log;
import at.tugraz.examreminder.core.Course;
import at.tugraz.examreminder.core.CourseContainer;
import at.tugraz.examreminder.core.Exam;
import at.tugraz.examreminder.crawler.Crawler;
import at.tugraz.examreminder.crawler.TuGrazSearchCrawler;
import at.tugraz.examreminder.ui.NotificationFactory;
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

    public static Class<? extends Crawler> getCrawlerToUse() {
        return crawler_to_use;
    }

    public static Crawler getCrawlerInstance(){
        Crawler crawler = null;
        try {
            if(crawler_to_use != null)
                crawler = crawler_to_use.newInstance();
        } catch (Exception e) {
            // Damn you java reflection
            Log.wtf("UpdateService", "Exception while creating crawler instance: " + e);
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
        boolean new_exams = false;
        CalendarHelper calendarHelper = null;
        boolean use_calendar = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("pref_use_android_calendar", false);
        if(use_calendar)
            calendarHelper = new CalendarHelper(this);

        for(int i = 0; i < CourseContainer.instance().size(); i++) {
            Course course = CourseContainer.instance().get(i);
            SortedSet<Exam> exams = crawler.getExams(course);
            if(exams == null)
                continue;
            if(!new_exams && compareExamList(course.exams, exams)){
                new_exams = true;
            }
            if(use_calendar && calendarHelper != null) {
                calendarHelper.deleteExamEvents(course.exams);
                calendarHelper.addExamEvents(exams);
            }
            course.exams = exams;
        }
        CourseContainer.instance().setChanged();
        CourseContainer.instance().notifyObservers();
        boolean show_notifications = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("pref_show_exam_notifications", true);
        if(new_exams && show_notifications) {
            NotificationFactory notificationFactory = new NotificationFactory(this);
            Notification notification = notificationFactory.createNewOrChangedExamsNotification();
            notificationFactory.sendNotification(notification);
        }
		PreferenceManager.getDefaultSharedPreferences(this).edit().putLong("pref_last_update", System.currentTimeMillis()).commit();
	}
}
