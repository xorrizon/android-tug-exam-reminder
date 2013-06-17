package at.tugraz.examreminder.ui;


import android.os.Bundle;
import at.tugraz.examreminder.R;
import at.tugraz.examreminder.core.Course;
import at.tugraz.examreminder.core.CourseContainer;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;

public class ExamDetailsActivity extends SherlockFragmentActivity {
    // TODO: change to
    public static final String INTENT_COURSE_ID = "CourseDetailsActivity.intent_course_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.course_details_activity);
        CourseDetailsFragment courseDetailsFragment = (CourseDetailsFragment) getSupportFragmentManager().findFragmentById(R.id.course_details_fragment);

        int course_id = getIntent().getIntExtra(INTENT_COURSE_ID, -1);
        if(course_id >= 0 && course_id < CourseContainer.instance().size()) {
            Course course = CourseContainer.instance().get(course_id);

            if(courseDetailsFragment != null && courseDetailsFragment.isInLayout())
                courseDetailsFragment.setValuesFromCourse(course);
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
