package at.tugraz.examreminder.service;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.CalendarContract;
import android.text.format.Time;
import android.util.Log;
import at.tugraz.examreminder.ExamReminderApplication;
import at.tugraz.examreminder.core.Exam;

import java.util.*;

public class CalendarHelper {
    private Context context;

    private static String CALENDAR_ID = null;
    private final static String TAG = "CalendarHelper";

    public CalendarHelper(Context context) {
        this.context = context;
    }


    public void addExamEvent(long calendar_id, Exam exam) {
        String title = exam.course.name + " " + exam.course.type;
        String location = exam.place;
        String description = "Registration deadline"; //@TODO after crawled
        long event_id = addEvent(calendar_id, exam.course.name, exam.getFrom(), exam.getTo(), description, location);
        exam.event_id = event_id;
    }

    public void addExamEvents(SortedSet<Exam> exams) {
        long calendar_id = Long.parseLong(PreferenceManager.getDefaultSharedPreferences(context).getString("pref_android_calendar_to_use", "-1"));
        if(calendar_id < 0)
            return;

        for(Exam exam : exams) {
            addExamEvent(calendar_id, exam);
        }
    }

    public void deleteExamEvents(SortedSet<Exam> exams) {
        for(Exam exam : exams){
            deleteExamEvent(exam);
        }
    }

    public void deleteExamEvent(Exam exam) {
        if(exam.event_id >= 0) {
            deleteEvent(exam.event_id);
            exam.event_id = -1;
        }
    }

    public long addEvent(long calendar_id, String title, GregorianCalendar start, GregorianCalendar end, String description, String location) {
        ContentValues event = new ContentValues();

        event.put(CalendarContract.Events.CALENDAR_ID, calendar_id);
        event.put(CalendarContract.Events.TITLE, title);
        if(description != null)
            event.put(CalendarContract.Events.DESCRIPTION, description);
        if(location != null)
            event.put(CalendarContract.Events.EVENT_LOCATION, location);
        event.put(CalendarContract.Events.DTSTART, start.getTimeInMillis());
        event.put(CalendarContract.Events.DTEND, end.getTimeInMillis());
        event.put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().getID());

        Uri eventsUri = CalendarContract.Events.CONTENT_URI;
        Uri result = context.getContentResolver().insert(eventsUri, event);
        long id = Long.parseLong(result.getLastPathSegment());
        Log.v(TAG, "Created Calendar event with id: " + id + " in calendar with id: " + calendar_id);
        return id;
    }

    public void deleteEvent(long event_id) {
        Uri.Builder builder = CalendarContract.Events.CONTENT_URI.buildUpon();
        Uri uri = builder.appendPath(String.valueOf(event_id)).build();
        context.getContentResolver().delete(uri, null, null);
        Log.v(TAG, "Deleted Calendar event with id: " + event_id);
    }

    public List<Calendar> getLocalCalendars() {
        Cursor cursor = null;
        List<Calendar> calendars = new ArrayList<Calendar>();
        ContentResolver contentResolver = ExamReminderApplication.getAppContext().getContentResolver();
        Uri uri = CalendarContract.Calendars.CONTENT_URI;
        String[] eventProjection = new String[] {
                CalendarContract.Calendars._ID,
                CalendarContract.Calendars.NAME,
                CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,
                CalendarContract.Calendars.ACCOUNT_NAME
        };
        cursor = contentResolver.query(uri, eventProjection, null, null, null);

        while(cursor.moveToNext()) {
            Calendar calendar = new Calendar();
            calendar.ID = Long.parseLong(cursor.getString(0));
            calendar.name = cursor.getString(1);
            calendar.displayName = cursor.getString(2);
            calendar.accountName = cursor.getString(3);
            if(!(calendar.name == null || calendar.accountName == null))
                calendars.add(calendar);
        }
        return calendars;
    }

    public class Calendar {
        public long ID;
        public String name;
        public String displayName;
        public String accountName;
    }

}
