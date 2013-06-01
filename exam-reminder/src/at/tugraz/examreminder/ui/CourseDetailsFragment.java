package at.tugraz.examreminder.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import at.tugraz.examreminder.R;
import at.tugraz.examreminder.adapter.CheckableCoursesAdapter;
import at.tugraz.examreminder.adapter.ExamsAdapter;
import at.tugraz.examreminder.core.Course;
import com.actionbarsherlock.app.SherlockFragment;

public class CourseDetailsFragment extends SherlockFragment {
    TextView course_name;
    TextView course_id;
    TextView course_type;
    TextView course_term;
    TextView course_lecturer;

    Button btn_open_in_browser;
    ListView exams_list;

    ExamsAdapter adapter;

    Course course = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public void onResume() {
        super.onResume();
        if(course != null && course_name != null) {
            course_name.setText(course.name);
            course_id.setText(course.number);
            course_type.setText(course.type);
            course_term.setText(course.term);
            course_lecturer.setText(course.lecturer);
            adapter = new ExamsAdapter(getActivity(), course);
            exams_list.setAdapter(adapter);
        }
    }

    public void setValuesFromCourse(Course course) {
        this.course = course;

        //@TODO Open URL Button
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.course_details_fragment, container, false);
        course_name = (TextView) layout.findViewById(R.id.course_name);
        course_id = (TextView) layout.findViewById(R.id.course_id);
        course_type = (TextView) layout.findViewById(R.id.course_type);
        course_term = (TextView) layout.findViewById(R.id.course_term);
        course_lecturer = (TextView) layout.findViewById(R.id.course_lecturer);
        btn_open_in_browser = (Button) layout.findViewById(R.id.btn_open_in_browser);
        exams_list = (ListView) layout.findViewById(android.R.id.list);


        return layout;
    }
}
