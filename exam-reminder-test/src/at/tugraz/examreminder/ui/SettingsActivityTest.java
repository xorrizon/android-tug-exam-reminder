package at.tugraz.examreminder.ui;

import android.preference.PreferenceManager;
import android.test.ActivityInstrumentationTestCase2;
import at.tugraz.examreminder.R;
import at.tugraz.examreminder.service.CalendarHelper;
import at.tugraz.examreminder.service.DailyListener;
import com.jayway.android.robotium.solo.Solo;

import java.util.List;


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

    public void testCalendarPreferences() {
        assertFalse("Android Calendar should not be used per default", PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean("pref_use_android_calendar", false));
        solo.clickOnCheckBox(1);
        CalendarHelper calendarHelper = new CalendarHelper(getActivity());
        List<CalendarHelper.Calendar> calendarList = calendarHelper.getLocalCalendars();
        assertTrue("This test only works if there are at least 2 calendars", calendarList.size() >= 2);

        solo.clickOnText(solo.getString(R.string.pref_android_calendar_to_use));
        for(CalendarHelper.Calendar calendar : calendarList) {
            assertTrue(solo.searchText(calendar.displayName));
        }
        solo.clickOnText(calendarList.get(1).displayName);
        assertTrue("Selected calendar should show in summery", solo.searchText(calendarList.get(1).displayName));

    }

}
