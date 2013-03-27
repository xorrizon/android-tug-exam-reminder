package at.tugraz.examreminder.ui;

import android.preference.PreferenceManager;
import android.test.ActivityInstrumentationTestCase2;
import at.tugraz.examreminder.R;
import at.tugraz.examreminder.service.DailyListener;
import com.jayway.android.robotium.solo.Solo;


public class SettingsActivityTest extends ActivityInstrumentationTestCase2<SettingsActivity> {
	private Solo solo;

	public SettingsActivityTest() {
		super(SettingsActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		solo = new Solo(getInstrumentation(), getActivity());
		PreferenceManager.getDefaultSharedPreferences(getActivity()).edit().clear().commit(); //Reset default preferences!!
		PreferenceManager.setDefaultValues(getActivity(), R.xml.preferences, true);
		solo.clickOnActionBarItem(R.id.settings);
		DailyListener.setNewPendingIntentAndCancelOld(getActivity(), null); //Cancel schedule
	}

	@Override
	protected void tearDown() throws Exception {
		solo.finishOpenedActivities();
		DailyListener.setNewPendingIntentAndCancelOld(getActivity(), null); //Cancel schedule
		PreferenceManager.getDefaultSharedPreferences(getActivity()).edit().clear().commit(); //Reset default preferences!!
		PreferenceManager.setDefaultValues(getActivity(), R.xml.preferences, true);
		super.tearDown();
		solo = null;
	}

	public void testUpdateFrequency() {
		int updateFrequency = Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(getActivity()).getString("pref_update_frequency", "-1"));
		assertEquals("The update frequency preference default value is not every day", 1, updateFrequency);
		assertTrue("Update frequency description should be the description of the default value", solo.searchText(getActivity().getString(R.string.once_a_day), true) );

		solo.clickOnText(getActivity().getString(R.string.pref_update_frequency));
		solo.clickOnText(getActivity().getString(R.string.every_2_days));
		assertTrue("Update frequency description should be 'every 2 days'", solo.searchText(getActivity().getString(R.string.every_2_days), true) );

		assertNotNull("There should be a new PendingRequest in DailyListener", DailyListener.currentPendingIntent);


	}

}
