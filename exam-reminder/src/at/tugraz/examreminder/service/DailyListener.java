package at.tugraz.examreminder.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.util.Log;
import at.tugraz.examreminder.R;
import com.commonsware.cwac.wakeful.WakefulIntentService;

import java.util.Calendar;

public class DailyListener implements WakefulIntentService.AlarmListener {
	private static final int HOUR_OF_DAY_TO_RUN = 12;

	//@TODO Note to self: Schedule the alarm on app start with scheduleMe()
	//@TODO And don't forget to reset it if settings have changed!

	private static PendingIntent currentPendingIntent;

	public static void scheduleMe(Context context) {
		WakefulIntentService.scheduleAlarms(new DailyListener(), context, false);
	}

	@Override
	public void scheduleAlarms(AlarmManager alarmManager, PendingIntent pendingIntent, Context context) {
		PreferenceManager.setDefaultValues(context, R.xml.preferences, false);
		int update_interval = Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(context).getString("pref_update_frequency", "-1"));
		if(update_interval <= 0)
			return;

		Calendar calendar = Calendar.getInstance();

		// if it's already after the target time, schedule first run for the next day
		if(calendar.get(Calendar.HOUR_OF_DAY) >= HOUR_OF_DAY_TO_RUN){
			calendar.add(Calendar.DAY_OF_YEAR, 1);
		}
		calendar.set(Calendar.HOUR_OF_DAY, HOUR_OF_DAY_TO_RUN);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);

		setNewPendingIntentAndCancelOld(context, pendingIntent);

		alarmManager.setInexactRepeating(AlarmManager.RTC, calendar.getTimeInMillis(),
				AlarmManager.INTERVAL_DAY, pendingIntent);
	}

	@Override
	public void sendWakefulWork(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();

		// only when connected or while connecting...
		if (netInfo != null && netInfo.isConnectedOrConnecting()) {

			boolean updateOnlyOnWifi = PreferenceManager.getDefaultSharedPreferences(context).getBoolean("pref_update_wifi_only", false);

			// if we have mobile or wifi connectivity...
			if (((netInfo.getType() == ConnectivityManager.TYPE_MOBILE) && updateOnlyOnWifi == false)
					|| (netInfo.getType() == ConnectivityManager.TYPE_WIFI)) {
				Log.d("DailyListener", "We have internet, start update check directly now!");

				Intent backgroundIntent = new Intent(context, UpdateService.class);
				WakefulIntentService.sendWakefulWork(context, backgroundIntent);
			} else {
				Log.d("DailyListener", "We have no internet, enable ConnectivityReceiver!");

				// enable receiver to schedule update when internet is available!
				ConnectivityReceiver.enableReceiver(context);
			}
		} else {
			Log.d("DailyListener", "We have no internet, enable ConnectivityReceiver!");

			// enable receiver to schedule update when internet is available!
			ConnectivityReceiver.enableReceiver(context);
		}
	}

	@Override
	public long getMaxAge() {
		return (AlarmManager.INTERVAL_DAY + 60 * 1000);
	}

	private synchronized static void setNewPendingIntentAndCancelOld(Context context, PendingIntent pendingIntent) {
		AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
		if(currentPendingIntent != null) {
			alarmManager.cancel(currentPendingIntent);
		}
		currentPendingIntent = pendingIntent;
	}
}
