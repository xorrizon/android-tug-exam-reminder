package at.tugraz.examreminder.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import at.tugraz.examreminder.R;
import at.tugraz.examreminder.adapter.ExamsAdapter;
import at.tugraz.examreminder.core.Course;
import com.actionbarsherlock.app.SherlockFragment;


public class ExamDetailsFragment extends SherlockFragment {
    TextView course_name;
    TextView course_id;
    TextView course_type;
    TextView course_term;
    TextView course_lecturer;

    Button btn_open_in_browser;
    ListView exams_list;

    ExamsAdapter adapter;

    Course course = null;
    private Uri courseinfoSiteURL;

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
        this.courseinfoSiteURL = Uri.parse("https://online.tugraz.at/tug_online/lv.detail?clvnr=" + course.id);
//        this.btn_open_in_browser.setU
        //@TODO Open URL Button
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //View layout = inflater.inflate(R.layout.course_details_header, container, false);
        //exams_list = (ListView) layout.findViewById(android.R.id.list);
        View layout = inflater.inflate(R.layout.courses_fragment, container, false);
        exams_list = (ListView) layout.findViewById(R.id.courses_list);

        View course_details = inflater.inflate(R.layout.course_details_header, null);
        exams_list.addHeaderView(course_details);

        course_name = (TextView) layout.findViewById(R.id.course_name);
        course_id = (TextView) layout.findViewById(R.id.course_id);
        course_type = (TextView) layout.findViewById(R.id.course_type);
        course_term = (TextView) layout.findViewById(R.id.course_term);
        course_lecturer = (TextView) layout.findViewById(R.id.course_lecturer);
        btn_open_in_browser = (Button) layout.findViewById(R.id.btn_open_in_browser);
        btn_open_in_browser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW, courseinfoSiteURL);
                startActivity(intent);
            }
        });


        return layout;
    }


}
