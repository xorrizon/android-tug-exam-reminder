package at.tugraz.examreminder.ui;

import android.app.Activity;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import at.tugraz.examreminder.ExamReminderApplication;
import at.tugraz.examreminder.R;

public class AboutActivity extends Activity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_activity);

        TextView version = (TextView) findViewById(R.id.version);
        version.setText(getString(R.string.version_with_param, ExamReminderApplication.getAppVersion()));

        ViewGroup container = (ViewGroup) findViewById(R.id.about_details_container);

        for(int i = 0; i < container.getChildCount(); i++) {
            View view = container.getChildAt(i);
            if(!(view instanceof TextView))
                continue;
            TextView textView = (TextView)view;
            if(textView.getLinksClickable()) {
                textView.setMovementMethod(LinkMovementMethod.getInstance());
            }
        }


    }

}