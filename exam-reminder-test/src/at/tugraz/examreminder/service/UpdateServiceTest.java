package at.tugraz.examreminder.service;


import android.content.Context;
import android.preference.PreferenceManager;
import android.test.InstrumentationTestCase;
import at.tugraz.examreminder.R;
import at.tugraz.examreminder.core.CourseContainer;
import at.tugraz.examreminder.core.Exam;
import at.tugraz.examreminder.crawler.SimpleMockCrawler;

import java.util.GregorianCalendar;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

public class UpdateServiceTest extends InstrumentationTestCase {
    private Context context;
    private UpdateService updateService;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        context = getInstrumentation().getTargetContext();
        init();
        UpdateService.setCrawlerToUse(SimpleMockCrawler.class);
        updateService = new UpdateService();
    }

    @Override
    protected void tearDown() throws Exception {
        init();
        UpdateService.setCrawlerToUse(null);
        updateService = null;
        super.tearDown();
    }

    public void testCompareExamList() {

        CourseContainer.instance().add(SimpleMockCrawler.createCourses().get(0));
        SortedSet<Exam> exams = SimpleMockCrawler.createExams();
        boolean new_exams;

        new_exams = UpdateService.compareExamList(CourseContainer.instance().get(0).exams, exams);
        assertFalse("If nothing has changed, compareExamList should return false", new_exams);

        exams.first().from = (new GregorianCalendar(2014,1,1,13,0)).getTime();
        exams.first().to = (new GregorianCalendar(2014,1,1,14,0)).getTime();
        new_exams = UpdateService.compareExamList(CourseContainer.instance().get(0).exams, exams);
        assertTrue("After changing the times compareExamList should return true", new_exams);

        exams = SimpleMockCrawler.createExams();
        Exam another_exam = exams.first().clone();
        another_exam.from = (new GregorianCalendar(2014,1,3,13,0)).getTime();
        another_exam.to = (new GregorianCalendar(2014,1,3,14,0)).getTime();
        exams.add(another_exam);

        new_exams = UpdateService.compareExamList(CourseContainer.instance().get(0).exams, exams);
        assertTrue("After adding an exam, compareExamList should return true", new_exams);

    }

    public void init(){
        PreferenceManager.getDefaultSharedPreferences(context).edit().clear().commit(); //Reset default preferences!!
        PreferenceManager.setDefaultValues(context, R.xml.preferences, true);
        DailyListener.setNewPendingIntentAndCancelOld(context, null); //Cancel schedule
        ConnectivityReceiver.disableReceiver(context);
        CourseContainer.instance().clear();
    }

}
