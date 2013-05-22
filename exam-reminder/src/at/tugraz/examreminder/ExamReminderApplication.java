package at.tugraz.examreminder;

import android.app.Application;
import android.content.Context;

public class ExamReminderApplication extends Application{

	private static Context context;
    public void onCreate(){
        context=getApplicationContext();
    }

    public static Context getAppContext(){
        return context;
    }
}