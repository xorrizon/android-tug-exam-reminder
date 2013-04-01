package at.tugraz.examreminder.service;

import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.test.InstrumentationTestCase;
import at.tugraz.examreminder.R;
import com.commonsware.cwac.wakeful.AlarmReceiver;

public class DailyListenerTest extends InstrumentationTestCase {
	private Context context;
	private AlarmReceiver alarmReceiver;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		context = getInstrumentation().getTargetContext();
		alarmReceiver = new AlarmReceiver();
		init();
	}

	@Override
	protected void tearDown() throws Exception {
		init();
		super.tearDown();
	}

	public void testBootCompleted() {
		assertNull("There should be nothing scheduled", DailyListener.currentPendingIntent);
		Intent intent = new Intent(Intent.ACTION_BOOT_COMPLETED);
		alarmReceiver.onReceive(context, intent);
		assertNotNull("There should now be something scheduled", DailyListener.currentPendingIntent);
	}

	public void testDoWakeFullWork() {
		Intent intent = new Intent(); // No action should be interpreted as AlarmManager broadcast
		alarmReceiver.onReceive(context, intent);

		long last_update = PreferenceManager.getDefaultSharedPreferences(context).getLong("pref_last_update", 0);
		assertFalse("Last update preference should have been set", last_update == 0);

		long time_difference =  System.currentTimeMillis() - last_update;
		assertTrue("Last update preference should not be older than a second", time_difference <= 1000);

	}

	private void init(){
		PreferenceManager.getDefaultSharedPreferences(context).edit().clear().commit(); //Reset default preferences!!
		PreferenceManager.setDefaultValues(context, R.xml.preferences, true);
		DailyListener.setNewPendingIntentAndCancelOld(context, null); //Cancel schedule
	}

}
