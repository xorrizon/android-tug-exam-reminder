package at.tugraz.examreminder;

import android.app.Notification;
import android.view.View;
import android.widget.Button;
import at.tugraz.examreminder.core.Course;
import at.tugraz.examreminder.core.CourseContainer;
import at.tugraz.examreminder.crawler.SimpleMockCrawler;
import at.tugraz.examreminder.crawler.TuGrazSearchCrawler;
import at.tugraz.examreminder.service.UpdateService;
import at.tugraz.examreminder.ui.MainActivity;
import at.tugraz.examreminder.ui.NotificationFactory;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import at.tugraz.examreminder.crawler.TuGrazSearchCrawler;
import at.tugraz.examreminder.ui.SettingsActivity;

public class MyActivity extends SherlockFragmentActivity implements View.OnClickListener {
	/**
	 * Called when the activity is first created.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

        findViewById(R.id.btn_start_main_activity).setOnClickListener(this);
        findViewById(R.id.btn_test_notification).setOnClickListener(this);
        findViewById(R.id.btn_change_crawler).setOnClickListener(this);

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
       }
    }
}
