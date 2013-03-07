package at.tugraz.examreminder.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import at.tugraz.examreminder.R;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
	private Context context;

	private ListPreference pref_updateFrequency;
	private Preference pref_updateNow;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = getActivity();
		addPreferencesFromResource(R.xml.preferences);
		pref_updateFrequency = (ListPreference) findPreference("pref_update_frequency");
		pref_updateNow = findPreference("pref_update_now");
		update_updateFrequencySummery();

		pref_updateNow.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				handleUpdateNow();
				return true;
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

	@Override
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
