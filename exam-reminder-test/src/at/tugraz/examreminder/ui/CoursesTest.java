package at.tugraz.examreminder.ui;

import android.test.ActivityInstrumentationTestCase2;
import at.tugraz.examreminder.R;
import at.tugraz.examreminder.core.Course;
import at.tugraz.examreminder.core.CourseContainer;
import at.tugraz.examreminder.core.Exam;
import at.tugraz.examreminder.service.DailyListener;
import com.jayway.android.robotium.solo.Solo;

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
        DailyListener.setNewPendingIntentAndCancelOld(getActivity(), null); //Cancel schedule
        solo = new Solo(getInstrumentation(), getActivity());
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testDeleteCourses() {
        solo.clickOnCheckBox(1);
        solo.clickOnCheckBox(3);
        solo.clickOnCheckBox(5);
        solo.clickOnCheckBox(7);
        solo.clickOnView(getActivity().findViewById(R.id.delete));
        for(int i=0; i < 9; i++) {
            if(i%2 == 0){
                assertTrue(solo.searchText("Course #"+i));
            } else {
                assertFalse(solo.searchText("Course #"+i));
            }
        }
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

}
