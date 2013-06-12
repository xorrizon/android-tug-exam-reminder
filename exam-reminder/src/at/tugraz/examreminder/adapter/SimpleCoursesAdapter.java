package at.tugraz.examreminder.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import at.tugraz.examreminder.R;
import at.tugraz.examreminder.core.Course;

import java.util.List;

public class SimpleCoursesAdapter extends BaseAdapter {
    List<Course> courses;
    Context context;

    public SimpleCoursesAdapter(Context context, List<Course> courses){
        super();
        this.courses = courses;
        this.context = context;
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
        ((TextView)group.findViewById(android.R.id.text1)).setText(course.name);
        ((TextView)group.findViewById(android.R.id.text2)).setText(course.number + " " + course.type + " " + course.term);
        group.findViewById(android.R.id.checkbox).setVisibility(View.GONE);
        return group;
    }
}
