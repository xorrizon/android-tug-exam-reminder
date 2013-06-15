package at.tugraz.examreminder.service;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.test.InstrumentationTestCase;
import at.tugraz.examreminder.R;
import at.tugraz.examreminder.crawler.SimpleMockCrawler;
import com.commonsware.cwac.wakeful.AlarmReceiver;

public class DailyListenerTest extends InstrumentationTestCase {
	private Context context;
	private AlarmReceiver alarmReceiver;
	private static final int SLEEP_TIME=400;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		context = getInstrumentation().getTargetContext();
		alarmReceiver = new AlarmReceiver();
		init();
        UpdateService.setCrawlerToUse(SimpleMockCrawler.class);
	}

	@Override
	protected void tearDown() throws Exception {
		init();
        UpdateService.setCrawlerToUse(null);
		super.tearDown();
	}

	public void testBootCompleted() {
		assertNull("There should be nothing scheduled", DailyListener.currentPendingIntent);
		Intent intent = new Intent(Intent.ACTION_BOOT_COMPLETED);
		alarmReceiver.onReceive(context, intent);
		assertNotNull("There should now be something scheduled", DailyListener.currentPendingIntent);
	}

	public void testDoWakeFullWork() throws InterruptedException {
		Intent intent = new Intent(); // No action should be interpreted as AlarmManager broadcast
		alarmReceiver.onReceive(context, intent);
		Thread.sleep(SLEEP_TIME);

		long last_update = PreferenceManager.getDefaultSharedPreferences(context).getLong("pref_last_update", 0);
		assertFalse("Last update preference should have been set", last_update == 0);

		long time_difference =  System.currentTimeMillis() - last_update;
		assertTrue("Last update preference should not be older than a second", time_difference <= 1000);

	}

	public void testNetworkStates() throws InterruptedException {
		DailyListener dailyListener = new DailyListener();

		PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean("pref_update_wifi_only", false).commit();
		dailyListener.sendWakefulWork(context, false, ConnectivityManager.TYPE_WIFI);
		Thread.sleep(SLEEP_TIME);
		assertEquals("Should have done nothing without internet connection", 0, PreferenceManager.getDefaultSharedPreferences(context).getLong("pref_last_update", 0));
		assertTrue("ConnectivityReceiver should have been activated", checkIfConnectivityReceiverHasBeenEnabled(context));
		init();

		PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean("pref_update_wifi_only", false).commit();
		dailyListener.sendWakefulWork(context, true, ConnectivityManager.TYPE_WIFI);
		Thread.sleep(SLEEP_TIME);
		assertTrue("Last update preference should have been set", PreferenceManager.getDefaultSharedPreferences(context).getLong("pref_last_update", 0) != 0);
		assertFalse("ConnectivityReceiver should not have been activated", checkIfConnectivityReceiverHasBeenEnabled(context));
		init();


		PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean("pref_update_wifi_only", true).commit();
		dailyListener.sendWakefulWork(context, true, ConnectivityManager.TYPE_WIFI);
		Thread.sleep(SLEEP_TIME);
		assertTrue("Last update preference should have been set", PreferenceManager.getDefaultSharedPreferences(context).getLong("pref_last_update", 0) != 0);
		assertFalse("ConnectivityReceiver should not have been activated", checkIfConnectivityReceiverHasBeenEnabled(context));
		init();

		PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean("pref_update_wifi_only", true).commit();
		dailyListener.sendWakefulWork(context, true, ConnectivityManager.TYPE_MOBILE);
		Thread.sleep(SLEEP_TIME);
		assertEquals("Should have done nothing without WIFI connection", 0, PreferenceManager.getDefaultSharedPreferences(context).getLong("pref_last_update", 0));
		assertTrue("ConnectivityReceiver should have been activated", checkIfConnectivityReceiverHasBeenEnabled(context));

	}

	private void init(){
		PreferenceManager.getDefaultSharedPreferences(context).edit().clear().commit(); //Reset default preferences!!
		PreferenceManager.setDefaultValues(context, R.xml.preferences, true);
		DailyListener.setNewPendingIntentAndCancelOld(context, null); //Cancel schedule
		ConnectivityReceiver.disableReceiver(context);
	}

	private boolean checkIfConnectivityReceiverHasBeenEnabled(Context context) {
		ComponentName component = new ComponentName(context, ConnectivityReceiver.class);
		return context.getPackageManager().getComponentEnabledSetting(component) == PackageManager.COMPONENT_ENABLED_STATE_ENABLED;
	}

}
