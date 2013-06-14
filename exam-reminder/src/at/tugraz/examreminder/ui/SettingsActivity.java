package at.tugraz.examreminder.ui;

import android.content.Intent;
import android.widget.Toast;
import at.tugraz.examreminder.service.DailyListener;
import at.tugraz.examreminder.service.UpdateService;
import at.tugraz.examreminder.ui.custompreferences.TimePreference;
import com.actionbarsherlock.app.SherlockPreferenceActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceManager;
import at.tugraz.examreminder.R;
import com.commonsware.cwac.wakeful.WakefulIntentService;

import java.util.Date;

public class SettingsActivity extends SherlockPreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener{
	
	private Context context;

	private ListPreference pref_updateFrequency;
    private ListPreference pref_useTabletLayout;
    private TimePreference pref_updateTime;
	private Preference pref_updateNow;	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = getApplicationContext();
		PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
		addPreferencesFromResource(R.xml.preferences);
		pref_updateFrequency = (ListPreference) findPreference("pref_update_frequency");
        pref_useTabletLayout = (ListPreference) findPreference("pref_use_tablet_layout");
        pref_updateTime = (TimePreference) findPreference("pref_update_time");
		pref_updateNow = findPreference("pref_update_now");
		updateSummaries();

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		pref_updateNow.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
			
			@Override
			public boolean onPreferenceClick(Preference preference) {
				handleUpdateNow();
				return false;
			}
			});
	}
	
	@Override
	public void onResume() {
		super.onResume();
		PreferenceManager.getDefaultSharedPreferences(context).registerOnSharedPreferenceChangeListener(this);
        updateSummaries();
	}

	@Override
	public void onPause() {
		super.onPause();
		PreferenceManager.getDefaultSharedPreferences(context).unregisterOnSharedPreferenceChangeListener(this);
	}

	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		updateSummaries();
        DailyListener.scheduleMe(context); //Always reschedule since most preferences affect this
	}

	private void updateSummaries() {
		pref_updateFrequency.setSummary(pref_updateFrequency.getEntry());
        pref_useTabletLayout.setSummary(pref_useTabletLayout.getEntry());
        pref_updateTime.setSummary(pref_updateTime.toString());
        Date time = new Date(PreferenceManager.getDefaultSharedPreferences(context).getLong("pref_last_update",0));
        pref_updateNow.setSummary(getString(R.string.pref_update_now_summery) + time.toString());
	}

	private void handleUpdateNow() {
		//SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy hh:mm:ss");
		//String datetime = format.format(new Date());
		//String prefix = getString(R.string.pref_update_now_summery);
		//pref_updateNow.setSummary(prefix + datetime + " (didn't actually do anything)");
        Intent intent = new Intent(getApplicationContext(), UpdateService.class);
        intent.setAction("Blub");
        Toast.makeText(this, R.string.update_started, Toast.LENGTH_SHORT).show();
        WakefulIntentService.sendWakefulWork(getApplicationContext(), UpdateService.class);
	}
}
