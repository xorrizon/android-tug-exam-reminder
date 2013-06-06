package at.tugraz.examreminder.service;

import android.content.ContentValues;
import android.net.Uri;
import at.tugraz.examreminder.ExamReminderApplication;
import at.tugraz.examreminder.core.Exam;

public class CalendarHelper {

    public final static String CALENDAR_ID = "examreminder";

    public final static int EVENT_STATUS_TENTATIVE = 0;
    public final static int EVENT_STATUS_CONFIRMED = 1;
    public final static int EVENT_STATUS_CANCELED = 2;

    public final static int EVENT_VISIBILITY_DEFAULT = 0;
    public final static int EVENT_VISIBILITY_CONFIDENTIAL = 1;
    public final static int EVENT_VISIBILITY_PRIVATE = 2;
    public final static int EVENT_VISIBILITY_PUBLIC = 3;

    public final static int EVENT_TRANSPARENCY_OPAQUE = 0;
    public final static int EVENT_TRANSPARENCY_TRANSPARENT = 1;

    public final static int EVENT_HASALARM_FALSE = 0;
    public final static int EVENT_HASALARM_TRUE = 1;


    public static void addExamEvent(Exam exam) {

        ContentValues event = new ContentValues();
        event.put("calendar_id", CALENDAR_ID);
        event.put("title", exam.course.toString());
        event.put("description", exam.course.type);
        event.put("eventLocation", exam.place);
        long startTime = exam.getFrom().getTimeInMillis();
        long endTime = exam.getTo().getTimeInMillis();
        event.put("dtstart", startTime);
        event.put("dtend", endTime);
        event.put("eventStatus", EVENT_STATUS_CONFIRMED);
        event.put("visibility", EVENT_VISIBILITY_PRIVATE);
        event.put("transparency", EVENT_TRANSPARENCY_TRANSPARENT);
        event.put("hasAlarm", EVENT_HASALARM_FALSE);

        Uri eventsUri = Uri.parse("content://calendar/events");
        Uri url = ExamReminderApplication.getAppContext().getContentResolver().insert(eventsUri, event);
    }
}
