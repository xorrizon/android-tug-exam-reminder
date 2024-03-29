package at.tugraz.examreminder.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import at.tugraz.examreminder.R;
import at.tugraz.examreminder.core.Course;
import at.tugraz.examreminder.core.CourseContainer;
import at.tugraz.examreminder.core.Exam;

import java.util.*;

public class ExamsAdapter extends BaseAdapter{
    private Context context;
    private Course course;
    private List<Exam> exams = new ArrayList<Exam>();
    private OnItemClickListener onItemClickListener;

    /**
     * Use this Constructor if you want to use the CourseContainer instead of a single Course
     * @param context
     */
    public ExamsAdapter(Context context) {
        this.context = context;
        this.course = null;
        onItemClickListener = null;
        loadExams();
    }

    /**
     *
     * @param context
     * @param course The Course to use for the exams, if null the CourseContainer is used
     */
    public ExamsAdapter(Context context, Course course) {
        this.context = context;
        this.course = course;
        onItemClickListener = null;
        loadExams();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    protected void loadExams() {
        exams.clear();
        if(course != null){
            exams.addAll(course.exams);
        } else {
            for(int i = 0; i < CourseContainer.instance().size(); i++) {
                exams.addAll(CourseContainer.instance().get(i).exams);
            }
        }
        Collections.sort(exams);
    }

    @Override
    public void notifyDataSetChanged() {
        loadExams();
        super.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return exams.size();
    }

    @Override
    public Exam getItem(int position) {
        return exams.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            int layout = R.layout.exam_item;
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(layout, parent, false);
        }
        ViewGroup group = (ViewGroup)convertView;
        Exam exam = getItem(position);

        ((TextView)group.findViewById(R.id.course_text)).setText(exam.course.name);

        ((TextView)group.findViewById(R.id.start_text)).setText(exam.getFromFormated());

        Calendar now = new GregorianCalendar();
        int year = now.get(Calendar.YEAR);
        int month = now.get(Calendar.MONTH);
        int day = now.get(Calendar.DAY_OF_MONTH);
        now.set(year, month, day, 0, 0, 0);

        long days = (exam.getFrom().getTime().getTime() - now.getTime().getTime()) / 1000 / 60 / 60 / 24;
        ((TextView)group.findViewById(R.id.days_text)).setText(String.valueOf(days));

        group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onItemClickListener != null) {
                    onItemClickListener.onItemClick(position);
                }
            }
        });

        return group;
    }

    public interface OnItemClickListener {
        public void onItemClick(int position);
    };
}
