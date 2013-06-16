package at.tugraz.examreminder.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import at.tugraz.examreminder.R;
import at.tugraz.examreminder.adapter.ExamsAdapter;
import at.tugraz.examreminder.core.Course;
import at.tugraz.examreminder.core.Exam;
import com.actionbarsherlock.app.SherlockFragment;


public class ExamDetailsFragment extends SherlockFragment {
    TextView exam_course_name;
    TextView exam_date;
    TextView exam_time;
    TextView exam_place;
    TextView exam_id;
    TextView exam_type;
    TextView exam_term;
    TextView exam_lecturer;
    TextView exam_register_deadline;
    TextView exam_cancel_deadline;
    TextView exam_participants;

    Button btn_open_in_browser;
    //ListView exams_list;

    //ExamsAdapter adapter;

    Exam exam = null;
    private Uri courseExamInfoURL;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public void onResume() {
        super.onResume();
        if(exam != null && exam.course.name != null) {
            exam_course_name.setText(exam.course.name);
            exam_place.setText(exam.place);

            // TODO: slip and format From/To
            exam_date.setText("");
            exam_time.setText("");

            exam_id.setText(exam.course.number);
            exam_type.setText(exam.course.type);
            exam_term.setText(exam.course.term);
            exam_lecturer.setText(exam.lecturer);
            exam_register_deadline.setText(exam.getRegisterDeadline());
            exam_cancel_deadline.setText(exam.getCancelDeadline());
            exam_participants.setText(exam.participants+"/"+exam.participants_max);
        }
    }

    public void setValuesFromExam(Exam exam) {
        this.exam = exam;
        this.courseExamInfoURL = Uri.parse("https://online.tugraz.at/tug_online/wbregisterexam.lv_termine?cheader=J&cstp_sp_nr=" + exam.course.id);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.exam_details_fragment, container, false);
        //exams_list = (ListView) layout.findViewById(android.R.id.list);
        //View layout = inflater.inflate(R.layout.courses_fragment, container, false);
        //exams_list = (ListView) layout.findViewById(R.id.courses_list);

        //View course_details = inflater.inflate(R.layout.course_details_header, null);
        //exams_list.addHeaderView(course_details);

        exam_course_name = (TextView) layout.findViewById(R.id.exam_course_name);
        exam_date = (TextView) layout.findViewById(R.id.exam_date);
        exam_time = (TextView) layout.findViewById(R.id.exam_time);
        exam_place = (TextView) layout.findViewById(R.id.exam_place);
        exam_id = (TextView) layout.findViewById(R.id.exam_id);
        exam_type = (TextView) layout.findViewById(R.id.exam_type);
        exam_term = (TextView) layout.findViewById(R.id.exam_term);
        exam_lecturer = (TextView) layout.findViewById(R.id.exam_lecturer);
        exam_register_deadline = (TextView) layout.findViewById(R.id.exam_register_deadline);
        exam_cancel_deadline = (TextView) layout.findViewById(R.id.exam_cancel_deadline);
        btn_open_in_browser = (Button) layout.findViewById(R.id.btn_open_in_browser);
        btn_open_in_browser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW, courseExamInfoURL);
                startActivity(intent);
            }
        });

        return layout;
    }


}
