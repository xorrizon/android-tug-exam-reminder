package at.tugraz.examreminder;

import android.app.Application;
import android.content.Context;
import android.util.Log;

public class ExamReminderApplication extends Application{

	private static Context context;
    @Override
    public void onCreate(){
        super.onCreate();
        context=getApplicationContext();
        Log.v("EXAM_REMINDER_APPLICATION", "ONCREATED");
    }

    public static Context getAppContext(){
        Log.v("EXAM_REMINDER_APPLICATION", "GIVE ME THE SWEET CONTEXT");
        return context;
    }
}