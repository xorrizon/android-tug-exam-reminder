package at.tugraz.examreminder.service;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.CalendarContract;
import android.test.InstrumentationTestCase;
import at.tugraz.examreminder.core.Course;
import at.tugraz.examreminder.core.Exam;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class CalendarHelperTest extends InstrumentationTestCase {
    private Context context;
    public static final long CALENDAR_ID=1; //Default google calendar on most devices

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        context = getInstrumentation().getTargetContext();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();    //To change body of overridden methods use File | Settings | File Templates.
    }

    public void testAddAndDeleteEvent() {
        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            return;
        }
        Course course = new Course();
        course.name = "UnitTest Course #1";
        Exam exam = new Exam(course);
        GregorianCalendar calendar = new GregorianCalendar(2015,1,1,12,0,0);
        exam.setFrom((GregorianCalendar) calendar.clone());

        calendar.set(Calendar.HOUR_OF_DAY, 15);
        exam.setTo((GregorianCalendar) calendar.clone());

        assertEquals("Before adding an event, the exam shoudl not have a valid event_id", -1, exam.event_id);

        CalendarHelper calendarHelper = new CalendarHelper(context);
        calendarHelper.addExamEvent(CALENDAR_ID, exam);

        long event_id = exam.event_id;

        assertFalse("Exam should now contain an event_id after adding the event", exam.event_id == -1);
        assertTrue("Event should exist after adding it", eventExists(event_id));

        calendarHelper.deleteExamEvent(exam);
        assertFalse("Event should now be deleted", eventExists(event_id));
        assertEquals("After deleting an event, the exam shoudl not have a valid event_id", -1, exam.event_id);

    }


    private boolean eventExists(long event_id) {
        Uri.Builder builder = CalendarContract.Events.CONTENT_URI.buildUpon();
        Uri uri = builder.appendPath(String.valueOf(event_id)).build();
        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
        return cursor.getCount() != 0;
    }

}
