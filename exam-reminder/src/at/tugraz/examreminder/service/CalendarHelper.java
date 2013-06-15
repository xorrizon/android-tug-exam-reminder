package at.tugraz.examreminder.service;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.util.Log;
import at.tugraz.examreminder.ExamReminderApplication;
import at.tugraz.examreminder.core.Exam;

public class CalendarHelper {

    private static String CALENDAR_ID = null;
    private final static String LOGCAT_TAG = "CalendarHelper";
    private final static Uri CALENDAR_URI = CalendarContract.Calendars.CONTENT_URI;
    private final static String INTERNAL_CALENDAR_NAME = "tug-exam-reminder";
    private final static String EXTERNAL_CALENDAR_NAME = "TUG Exam Reminder";
    private final static String CALENDAR_COLOR = "red";
    private final static String CALENDAR_TYPE = CalendarContract.ACCOUNT_TYPE_LOCAL;


    private final static int EVENT_STATUS_TENTATIVE = 0;
    private final static int EVENT_STATUS_CONFIRMED = 1;
    private final static int EVENT_STATUS_CANCELED = 2;
    private final static int EVENT_VISIBILITY_DEFAULT = 0;
    private final static int EVENT_VISIBILITY_CONFIDENTIAL = 1;
    private final static int EVENT_VISIBILITY_PRIVATE = 2;
    private final static int EVENT_VISIBILITY_PUBLIC = 3;
    private final static int EVENT_TRANSPARENCY_OPAQUE = 0;
    private final static int EVENT_TRANSPARENCY_TRANSPARENT = 1;
    private final static int EVENT_HASALARM_FALSE = 0;
    private final static int EVENT_HASALARM_TRUE = 1;


    private static final int PROJECTION_ID_INDEX = 0;
    private static final int PROJECTION_ACCOUNT_NAME_INDEX = 1;
    private static final int PROJECTION_DISPLAY_NAME_INDEX = 2;
    private static final int PROJECTION_OWNER_ACCOUNT_INDEX = 3;


    public static void addExamEvent(Exam exam) {

        ContentValues event = new ContentValues();

        if (CALENDAR_ID == null) CALENDAR_ID = getLocalCalendarId();
        event.put(CalendarContract.Events.CALENDAR_ID, Long.valueOf(CALENDAR_ID));
        event.put(CalendarContract.Events.TITLE, exam.course.toString());
        event.put(CalendarContract.Events.DESCRIPTION, exam.course.type);
        event.put(CalendarContract.Events.EVENT_LOCATION,exam.place);
        long startTime = exam.getFrom().getTimeInMillis();
        long endTime = exam.getTo().getTimeInMillis();
        event.put(CalendarContract.Events.DTSTART, startTime);
        event.put(CalendarContract.Events.DTEND, endTime);
        event.put(CalendarContract.Events.EVENT_TIMEZONE, "America/Los_Angeles");
//        event.put("eventStatus", EVENT_STATUS_CONFIRMED);
//        event.put("visibility", EVENT_VISIBILITY_PRIVATE);
//        event.put("transparency", EVENT_TRANSPARENCY_TRANSPARENT);
 //       event.put("hasAlarm", EVENT_HASALARM_FALSE);

        //Uri eventsUri = CalendarContract.Events.CONTENT_URI;
        Uri eventsUri = Uri.parse("content://com.android.calendar/events");
        Uri url = ExamReminderApplication.getAppContext().getContentResolver().insert(eventsUri, event);
        Log.v(LOGCAT_TAG, url.toString());
    }

    private static void createNewLocalCalendar() {
        Log.v(LOGCAT_TAG, "create new calendar");
        ContentValues contentValues = new ContentValues();
        contentValues.put(CalendarContract.Calendars.NAME, INTERNAL_CALENDAR_NAME);
        contentValues.put(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME, EXTERNAL_CALENDAR_NAME);
        contentValues.put(CalendarContract.Calendars.CALENDAR_COLOR, CALENDAR_COLOR);
        contentValues.put(CalendarContract.Calendars.VISIBLE, 1);

        final Uri calUri = CalendarContract.Calendars.CONTENT_URI;

        final Uri result = ExamReminderApplication.getAppContext().getContentResolver().insert(calUri, contentValues);
        Log.v(LOGCAT_TAG, "YOLO"+result);
        Log.v(LOGCAT_TAG, "created new calendar");
    }


    private static String getLocalCalendarId() {
        Cursor cursor = null;
        ContentResolver contentResolver = ExamReminderApplication.getAppContext().getContentResolver();
        Uri uri = CalendarContract.Calendars.CONTENT_URI;
        String[] eventProjection = new String[] {
                CalendarContract.Calendars._ID,
                CalendarContract.Calendars.NAME,
                CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,
                CalendarContract.Calendars.ACCOUNT_TYPE
        };
        String selection = "((" + CalendarContract.Calendars.NAME + " = ?) AND ("
                        + CalendarContract.Calendars.CALENDAR_DISPLAY_NAME + " = ?))";
        String[] selectionArgs = new String[] {INTERNAL_CALENDAR_NAME, EXTERNAL_CALENDAR_NAME };
        cursor = contentResolver.query(uri, eventProjection, selection, selectionArgs, null);
        if (cursor.getCount() == 0) {
            createNewLocalCalendar();
            cursor = contentResolver.query(uri, eventProjection, selection, selectionArgs, null);
            if (cursor.getCount() == 0) {
                Log.v(LOGCAT_TAG, "Error: Couldn't create new local Calendar");
                return null;
            }
        }
        if(cursor.moveToNext()) {
            String _id = cursor.getString(0);
            String name = cursor.getString(1);
            String displayName = cursor.getString(2);
            String accountType = cursor.getString(3);
            return _id;
        }
        return null;
    }

}
