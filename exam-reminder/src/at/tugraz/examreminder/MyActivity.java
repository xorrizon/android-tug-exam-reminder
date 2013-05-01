package at.tugraz.examreminder;

import android.app.Notification;
import android.view.View;
import at.tugraz.examreminder.ui.NotificationFactory;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import android.content.Intent;
import android.os.Bundle;
import at.tugraz.examreminder.ui.SettingsActivity;

public class MyActivity extends SherlockFragmentActivity implements View.OnClickListener {
	/**
	 * Called when the activity is first created.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

        findViewById(R.id.btn_test_notification).setOnClickListener(this);

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
           case R.id.btn_test_notification:
               NotificationFactory notificationFactory = new NotificationFactory(this);
               Notification notification = notificationFactory.createNewOrChangedExamsNotification();
               notificationFactory.sendNotification(notification);
               break;
       }
    }
}
