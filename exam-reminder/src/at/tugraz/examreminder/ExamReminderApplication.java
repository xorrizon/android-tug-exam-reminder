package at.tugraz.examreminder;

import android.app.Application;
import android.content.Context;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.WindowManager;

public class ExamReminderApplication extends Application{

	private static Context context;

    public static boolean useTabletMode(Context context) {
        //@Todo move to application class once it is merged
        int use_tablet_mode = Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(context).getString("pref_use_tablet_layout", "0"));
        switch (use_tablet_mode) {
            case 0: return context.getResources().getBoolean(R.bool.isTablet);
            case 1: return true;
            case 2:
                Display display = ((WindowManager) context.getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
                return display.getRotation() == Surface.ROTATION_90 || display.getRotation() == Surface.ROTATION_270;
            default: return false;
        }
    }

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