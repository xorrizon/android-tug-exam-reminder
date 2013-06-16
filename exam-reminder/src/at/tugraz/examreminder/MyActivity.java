package at.tugraz.examreminder;

import android.app.Notification;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import at.tugraz.examreminder.core.Course;
import at.tugraz.examreminder.core.CourseContainer;
import at.tugraz.examreminder.core.Exam;
import at.tugraz.examreminder.crawler.SimpleMockCrawler;
import at.tugraz.examreminder.crawler.TuGrazSearchCrawler;
import at.tugraz.examreminder.service.CalendarHelper;
import at.tugraz.examreminder.service.UpdateService;
import at.tugraz.examreminder.ui.MainActivity;
import at.tugraz.examreminder.ui.NotificationFactory;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import android.content.Intent;
import android.os.Bundle;
import at.tugraz.examreminder.ui.SettingsActivity;

import java.util.GregorianCalendar;
import java.util.List;

public class MyActivity extends SherlockFragmentActivity implements View.OnClickListener {
	/**
	 * Called when the activity is first created.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.test_activity);

        ViewGroup container = (ViewGroup)findViewById(R.id.test_button_container);

        for(int i = 0; i < container.getChildCount(); i++){
            View view = container.getChildAt(i);
            if(view instanceof Button){
                ((Button)view).setOnClickListener(this);
            }
        }

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.main_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.settings :
				startActivity(new Intent(this, SettingsActivity.class));
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

    @Override
    public void onClick(View v) {
       switch (v.getId()){
           case R.id.btn_start_main_activity:
               Intent intent = new Intent(this, MainActivity.class);
               startActivity(intent);
               break;
           case R.id.btn_test_notification:
               NotificationFactory notificationFactory = new NotificationFactory(this);
               Notification notification = notificationFactory.createNewOrChangedExamsNotification();
               notificationFactory.sendNotification(notification);
               break;
           case R.id.btn_change_crawler:
               if(UpdateService.getCrawlerToUse() == null || UpdateService.getCrawlerToUse().equals(TuGrazSearchCrawler.class)){
                   UpdateService.setCrawlerToUse(SimpleMockCrawler.class);
                   Course testCourse = SimpleMockCrawler.createCourses().get(0);
                   testCourse.exams.clear();
                   CourseContainer.instance().clear();
                   CourseContainer.instance().add(testCourse);
               } else {
                   UpdateService.setCrawlerToUse(TuGrazSearchCrawler.class);
               }
               Button btn = (Button)v;
               btn.setText("Change Crawler (currently "+UpdateService.getCrawlerToUse().getSimpleName()+")");
               break;
           case R.id.btn_calendar_test:
               CalendarHelper calendarHelper = new CalendarHelper(this);
               List<CalendarHelper.Calendar> calendars = calendarHelper.getLocalCalendars();
               GregorianCalendar from = new GregorianCalendar(2013, 06, 16, 17, 0);
               GregorianCalendar to = new GregorianCalendar(2013, 06, 16, 18, 0);
               Course course = new Course();
               course.name = "Blub42";
               course.type = "VO";
               Exam exam = new Exam(course);
               exam.setFrom(from);
               exam.setTo(to);
               calendarHelper.addExamEvent(calendars.get(0).ID, exam);
               break;
       }
    }
}
