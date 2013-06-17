package at.tugraz.examreminder.ui;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import at.tugraz.examreminder.ExamReminderApplication;
import at.tugraz.examreminder.R;
import at.tugraz.examreminder.adapter.CheckableCoursesAdapter;
import at.tugraz.examreminder.adapter.ExamsAdapter;
import at.tugraz.examreminder.core.Course;
import at.tugraz.examreminder.core.CourseContainer;
import at.tugraz.examreminder.core.Exam;
import at.tugraz.examreminder.service.CourseListSerializer;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockListFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

public class ExamsFragment extends SherlockListFragment implements ExamsAdapter.OnItemClickListener {
    private ExamsAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        adapter = new ExamsAdapter(getActivity());
        setListAdapter(adapter);
        adapter.setOnItemClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.exams_fragment, container, false);
        return layout;
    }

    @Override
    public void onItemClick(int position) {
        if(ExamReminderApplication.useTabletMode(getActivity())) {
            updateExamDetailFragment(position);
        } else {
            Exam exam = adapter.getItem(position);
            String exam_dump = CourseListSerializer.examToJson(exam);
            String course_dump = CourseListSerializer.courseToJson((Course)exam.course.clone());
            Intent intent = new Intent(getActivity(), ExamDetailsActivity.class);
            intent.putExtra(ExamDetailsActivity.INTENT_EXAM_DUMP, exam_dump);
            intent.putExtra(ExamDetailsActivity.INTENT_COURSE_DUMP, course_dump);
            startActivity(intent);
        }
    }

    protected void updateExamDetailFragment(int position) {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Exam exam = adapter.getItem(position);
        ExamDetailsFragment newFragment = new ExamDetailsFragment();
        newFragment.setValuesFromExam(exam, exam.course);
        ft.replace(R.id.details_fragment_container, newFragment);
        ft.commit();
    }

}
