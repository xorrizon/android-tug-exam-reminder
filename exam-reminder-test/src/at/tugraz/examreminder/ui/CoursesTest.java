package at.tugraz.examreminder.ui;

import android.os.Build;
import android.preference.PreferenceManager;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import at.tugraz.examreminder.R;
import at.tugraz.examreminder.adapter.CheckableCoursesAdapter;
import at.tugraz.examreminder.core.Course;
import at.tugraz.examreminder.core.CourseContainer;
import at.tugraz.examreminder.core.Exam;
import at.tugraz.examreminder.crawler.Crawler;
import at.tugraz.examreminder.crawler.NullCrawler;
import at.tugraz.examreminder.crawler.SimpleMockCrawler;
import at.tugraz.examreminder.service.DailyListener;
import at.tugraz.examreminder.service.UpdateService;
import com.jayway.android.robotium.solo.Solo;

import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;


public class CoursesTest extends ActivityInstrumentationTestCase2<MainActivity> {
    private Solo solo;

    public CoursesTest() {
        super(MainActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        populateCourseList();
        UpdateService.setCrawlerToUse(SimpleMockCrawler.class);
        solo = new Solo(getInstrumentation(), getActivity());
        DailyListener.setNewPendingIntentAndCancelOld(getActivity(), null); //Cancel schedule
        CourseContainer.instance().deleteObservers();
        PreferenceManager.getDefaultSharedPreferences(getActivity()).edit().clear().commit(); //Reset default preferences!!
        PreferenceManager.setDefaultValues(getActivity(), R.xml.preferences, true);
    }

    @Override
    protected void tearDown() throws Exception {
        UpdateService.setCrawlerToUse(null);
        PreferenceManager.getDefaultSharedPreferences(getActivity()).edit().clear().commit(); //Reset default preferences!!
        PreferenceManager.setDefaultValues(getActivity(), R.xml.preferences, true);
        super.tearDown();
    }

    public void testDeleteCourses() {
        solo.clickOnCheckBox(1);
        solo.clickOnCheckBox(3);
        solo.clickOnCheckBox(5);
        solo.clickOnCheckBox(7);
        solo.clickOnView(getActivity().findViewById(R.id.delete));
        ListView listView = (ListView)solo.getView(R.id.courses_list);
        assertEquals("Listview should only contain 6 courses", 5, listView.getCount());
        for(int i=0; i < 9; i++) {
            if(i%2 == 0){
                assertTrue(solo.searchText("Course #"+i));
            }
        }
    }

    public void testAddCourse() {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            PreferenceManager.getDefaultSharedPreferences(getActivity()).edit()
                    .putBoolean("pref_use_android_calendar", true)
                    .putString("pref_android_calendar_to_use", "1")
                    .commit();
        }
        UpdateService.setCrawlerToUse(SimpleMockCrawler.class);
        solo.clickOnView(getActivity().findViewById(R.id.add));
        int oldsize = CourseContainer.instance().size();
        solo.clickOnEditText(0);
        solo.enterText(0, "Course");
        solo.clickOnEditText(0);
        solo.sendKey(Solo.ENTER);
        solo.waitForText("THE COURSE #2", 1, 5);
        solo.clickOnText("THE COURSE #2");
        solo.waitForText("Courses", 1, 5);
        assertEquals(oldsize+1, CourseContainer.instance().size());
        assertTrue(solo.searchText("THE COURSE #2", 1, true));
        Exam exam = CourseContainer.instance().get(CourseContainer.instance().size()-1).exams.first();
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            assertFalse("Exam of added course should have a created event", exam.event_id == -1);
        }

        oldsize = CourseContainer.instance().size();
        solo.clickOnView(getActivity().findViewById(R.id.add));
        solo.enterText(0, "Course");
        solo.clickOnEditText(0);
        solo.sendKey(Solo.ENTER);
        solo.waitForText("THE COURSE #2", 1, 5);
        solo.clickOnText("THE COURSE #2");
        assertEquals("You should not be able to add the same course twice", oldsize, CourseContainer.instance().size());

        solo.goBack();
        solo.goBack();
    }

    public void testAddCourseCrawlerError() {
        UpdateService.setCrawlerToUse(NullCrawler.class);
        solo.clickOnView(getActivity().findViewById(R.id.add));
        solo.enterText(0, "Course");
        solo.clickOnEditText(0);
        solo.sendKey(Solo.ENTER);
        assertTrue("Error dialog not shown", solo.searchText(solo.getString(R.string.error)));
        assertTrue("Error message not shown", solo.searchText(solo.getString(R.string.search_error)));
        solo.clickOnButton(0);
        assertFalse("Error shows even after clicking ok", solo.searchText(solo.getString(R.string.error)));
        solo.goBack();
        solo.goBack();
    }

    public void testAddCourseScreenOriantationChange() {
        ListView listView;
        solo.clickOnView(getActivity().findViewById(R.id.add));
        solo.enterText(0, "Course");
        solo.clickOnEditText(0);
        solo.sendKey(Solo.ENTER);
        solo.waitForText("THE COURSE #2", 1, 5);
        listView = (ListView) solo.getView(android.R.id.list);
        assertEquals("List view should contain 2 items before doing anything", 2, listView.getCount());
        solo.setActivityOrientation(Solo.LANDSCAPE);
        solo.sleep(500);
        listView = (ListView) solo.getView(android.R.id.list);
        solo.sleep(3000);
        assertEquals("List view should't change size after oriantation change", 2, listView.getCount());
        assertEquals("SearchView should not reset.", "Course", solo.getEditText(0).getText().toString());
        solo.goBack();
        solo.goBack();
    }

    public void testEmptyCourseListClick() {
        ListView listView = (ListView) solo.getView(R.id.courses_list);
        View emptyview = listView.getEmptyView();
        assertEquals("EmptyView shoudl not be visible", View.GONE, emptyview.getVisibility());
        deleteAllCourses();
        assertEquals(0, listView.getCount());
        assertEquals("Empty view should be visible", View.VISIBLE, emptyview.getVisibility());
        solo.clickOnView(emptyview);
        solo.assertCurrentActivity("Current activity shoudl Be the AddCourse Activity", AddCourseActivity.class);
        solo.goBack();
    }

    public void testCourseDetails() {
        Course course = CourseContainer.instance().get(0);
        course.exams = SimpleMockCrawler.createExams(course);

        solo.clickOnText("Course #0");

        assertEquals(course.name, ((TextView)solo.getView(R.id.course_name,0)).getText());

        View exam_view;

        exam_view = solo.getView(R.id.exam_item, 0);
        assertEquals("Course #0", ((TextView)exam_view.findViewById(R.id.course_text)).getText());

        solo.goBack();
    }


    private void populateCourseList(){
        CourseContainer.instance().clear();
        for(int i=0; i < 9; i++){
            Course course = new Course();
            course.name = "Course #"+i;
            course.lecturer = "Our leader";
            course.number = "course."+i;
            course.term = "SS";
            course.type = "VO";
            CourseContainer.instance().add(course);
        }
    }

    private void deleteAllCourses() {
        for(int i=0; i < 4; i++){
            solo.clickOnCheckBox(i);
        }
        solo.clickOnView(getActivity().findViewById(R.id.delete));
        for(int i=0; i < 5; i++){
            solo.clickOnCheckBox(i);
        }
        solo.clickOnView(getActivity().findViewById(R.id.delete));
    }

}
