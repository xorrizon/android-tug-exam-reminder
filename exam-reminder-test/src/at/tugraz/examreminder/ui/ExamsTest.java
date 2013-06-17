package at.tugraz.examreminder.ui;

import android.test.ActivityInstrumentationTestCase2;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import at.tugraz.examreminder.R;
import at.tugraz.examreminder.core.Course;
import at.tugraz.examreminder.core.CourseContainer;
import at.tugraz.examreminder.core.Exam;
import at.tugraz.examreminder.crawler.SimpleMockCrawler;
import at.tugraz.examreminder.service.DailyListener;
import at.tugraz.examreminder.service.UpdateService;
import com.jayway.android.robotium.solo.Solo;

import java.util.*;


public class ExamsTest extends ActivityInstrumentationTestCase2<MainActivity> {
    private Solo solo;

    public ExamsTest() {
        super(MainActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        init();
        UpdateService.setCrawlerToUse(SimpleMockCrawler.class);
        solo = new Solo(getInstrumentation(), getActivity());
        DailyListener.setNewPendingIntentAndCancelOld(getActivity(), null); //Cancel schedule
    }

    @Override
    protected void tearDown() throws Exception {
        UpdateService.setCrawlerToUse(null);
        super.tearDown();
    }

    public void testExamsList() {
        solo.clickOnText("Exams");
        View exam_view;

        exam_view = solo.getView(R.id.exam_item, 0);
        assertEquals("Course #1", ((TextView)exam_view.findViewById(R.id.course_text)).getText());
        assertEquals("0", ((TextView)exam_view.findViewById(R.id.days_text)).getText());

        exam_view = solo.getView(R.id.exam_item, 1);
        assertEquals("Course #1", ((TextView)exam_view.findViewById(R.id.course_text)).getText());
        assertEquals("2", ((TextView)exam_view.findViewById(R.id.days_text)).getText());

        exam_view = solo.getView(R.id.exam_item, 2);
        assertEquals("Course #1", ((TextView)exam_view.findViewById(R.id.course_text)).getText());
        assertEquals("12", ((TextView)exam_view.findViewById(R.id.days_text)).getText());


    }

    public void testExamDetailList() {
        solo.clickInList(1);
        solo.goBack();
        solo.clickOnText("Exams");
        solo.sleep(100);
        //solo.clickInList(1);
        solo.clickOnText("Course #1", 2);
        solo.sleep(100);
        assertEquals("Mr. Professor", ((TextView)solo.getView(R.id.exam_lecturer)).getText());
        assertEquals("Der Höhrsaal", ((TextView)solo.getView(R.id.exam_place)).getText());
            }

    protected void init() {
        CourseContainer.instance().clear();
        Course course = new Course();
        course.name = "Course #1";
        course.lecturer = "Our leader";
        course.number = "course.101";
        course.term = "SS";
        course.type = "VO";

        Course course2 = new Course();
        course2.name = "Course #2";
        course2.lecturer = "Our leader";
        course2.number = "course.102";
        course2.term = "SS";
        course2.type = "VO";

        Calendar now = Calendar.getInstance();
        int year = now.get(Calendar.YEAR);
        int month = now.get(Calendar.MONTH);
        int day = now.get(Calendar.DAY_OF_MONTH);

        SortedSet<Exam> exams = new TreeSet<Exam>();
        Calendar calender = new GregorianCalendar();
        Exam exam = new Exam(course);
        exam.lecturer = "Mr. Professor";
        exam.examinar = "Mr. Aufsicht";
        exam.place = "Der Höhrsaal";
        calender.set(year, month, day+2,12,0);
        exam.setFrom((GregorianCalendar)calender.clone());
        calender.set(year, month, day+2,14,0);
        exam.setTo((GregorianCalendar)calender.clone());
        exams.add(exam.clone());

        calender.set(year, month, day+12,12,0);
        exam.setFrom((GregorianCalendar)calender.clone());
        calender.set(year, month, day+12,14,0);
        exam.setTo((GregorianCalendar)calender.clone());
        exams.add(exam.clone());

        calender.set(year, month, day,10,0);
        exam.setFrom((GregorianCalendar)calender.clone());
        calender.set(year, month, day,11,0);
        exam.setTo((GregorianCalendar)calender.clone());
        exams.add(exam.clone());

        course.exams = exams;

        course2.exams = new TreeSet<Exam>();
        CourseContainer.instance().add(course);
        CourseContainer.instance().add(course2);
    }
}
