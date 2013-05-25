package at.tugraz.examreminder.ui;

import android.test.ActivityInstrumentationTestCase2;
import android.view.View;
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
        DailyListener.setNewPendingIntentAndCancelOld(getActivity(), null); //Cancel schedule
        UpdateService.setCrawlerToUse(SimpleMockCrawler.class);
        solo = new Solo(getInstrumentation(), getActivity());
    }

    @Override
    protected void tearDown() throws Exception {
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

    protected void init() {
        CourseContainer.instance().clear();
        Course course = new Course();
        course.name = "Course #1";
        course.lecturer = "Our leader";
        course.number = "course.101";
        course.term = "SS";
        course.type = "VO";

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
        exam.from = calender.getTime();
        calender.set(year, month, day+2,14,0);
        exam.to = calender.getTime();
        exams.add(exam.clone());

        calender.set(year, month, day+12,12,0);
        exam.from = calender.getTime();
        calender.set(year, month, day+12,14,0);
        exam.to = calender.getTime();
        exams.add(exam.clone());

        calender.set(year, month, day,10,0);
        exam.from = calender.getTime();
        calender.set(year, month, day,11,0);
        exam.to = calender.getTime();
        exams.add(exam.clone());

        course.exams = exams;

        CourseContainer.instance().add(course);
    }
}
