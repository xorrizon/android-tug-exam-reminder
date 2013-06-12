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

import java.util.List;

public class SimpleCoursesAdapter extends BaseAdapter {
    List<Course> courses;
    Context context;
    boolean markCoursesInContainer;

    public SimpleCoursesAdapter(Context context, List<Course> courses){
        super();
        this.courses = courses;
        this.context = context;
        this.markCoursesInContainer = false;
    }

    public void setMarkCoursesInContainer(boolean greyOut) {
        this.markCoursesInContainer = greyOut;
    }

    @Override
    public int getCount() {
        return courses.size();
    }

    @Override
    public Course getItem(int position) {
        return courses.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            int layout = R.layout.course_item;
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(layout, parent, false);
        }
        ViewGroup group = (ViewGroup)convertView;
        Course course = getItem(position);
        TextView text1 = (TextView)group.findViewById(android.R.id.text1);
        TextView text2 = (TextView)group.findViewById(android.R.id.text2);
        if(markCoursesInContainer && CourseContainer.instance().contains(course))
            text1.setText("[X] " + course.name);
        else
            text1.setText(course.name);
        text2.setText(course.number + " " + course.type + " " + course.term);


        group.findViewById(android.R.id.checkbox).setVisibility(View.GONE);
        return group;
    }
}
