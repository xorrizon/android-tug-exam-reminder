package at.tugraz.examreminder.ui;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.actionbarsherlock.app.SherlockPreferenceActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceManager;
import at.tugraz.examreminder.R;

public class SettingsActivity extends SherlockPreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener{
	
	private Context context;

	private ListPreference pref_updateFrequency;
	private Preference pref_updateNow;	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = getApplicationContext();
		PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
		addPreferencesFromResource(R.xml.preferences);
		pref_updateFrequency = (ListPreference) findPreference("pref_update_frequency");
		pref_updateNow = findPreference("pref_update_now");
		update_updateFrequencySummery();

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
	}

	@Override
	public void onPause() {
		super.onPause();
		PreferenceManager.getDefaultSharedPreferences(context).unregisterOnSharedPreferenceChangeListener(this);
	}

	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		if(key.equals("pref_update_frequency")) {
			update_updateFrequencySummery();
		}
	}

	private void update_updateFrequencySummery() {
		pref_updateFrequency.setSummary(pref_updateFrequency.getEntry());
	}

	private void handleUpdateNow() {
		SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy hh:mm:ss");
		String datetime = format.format(new Date());
		String prefix = getString(R.string.pref_update_now_summery);
		pref_updateNow.setSummary(prefix + datetime + " (didn't actually do anything)");
	}
}
