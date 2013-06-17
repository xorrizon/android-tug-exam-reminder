package at.tugraz.examreminder.ui;


import android.os.Bundle;
import at.tugraz.examreminder.R;
import at.tugraz.examreminder.core.Course;
import at.tugraz.examreminder.core.CourseContainer;
import at.tugraz.examreminder.core.Exam;
import at.tugraz.examreminder.service.CourseListSerializer;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;

public class ExamDetailsActivity extends SherlockFragmentActivity {
    // TODO: change to
    public static final String INTENT_EXAM_DUMP = "ExamDetailsActivity.INTENT_EXAM_DUMP";
    public static final String INTENT_COURSE_DUMP = "ExamDetailsActivity.INTENT_COURSE_DUMP";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.exam_details_activity);
        ExamDetailsFragment examDetailsFragment = (ExamDetailsFragment) getSupportFragmentManager().findFragmentById(R.id.exam_details_fragment);

        String exam_dump = getIntent().getStringExtra(INTENT_EXAM_DUMP);
        String course_dump = getIntent().getStringExtra(INTENT_COURSE_DUMP);
        Exam exam = CourseListSerializer.jsonToExam(exam_dump);
        Course course = CourseListSerializer.jsonToCourse(course_dump);

        if(examDetailsFragment != null && examDetailsFragment.isInLayout())
            examDetailsFragment.setValuesFromExam(exam, course);
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
