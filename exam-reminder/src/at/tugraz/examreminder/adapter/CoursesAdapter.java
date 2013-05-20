package at.tugraz.examreminder.adapter;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import at.tugraz.examreminder.R;
import at.tugraz.examreminder.core.Course;
import at.tugraz.examreminder.core.CourseContainer;
import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.manuelpeinado.multichoiceadapter.MultiChoiceBaseAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class CoursesAdapter extends MultiChoiceBaseAdapter {

    public CoursesAdapter(Bundle savedInstanceState) {
        super(savedInstanceState);
    }

    @Override
    public int getCount() {
        return CourseContainer.instance().size();
    }

    @Override
    public Course getItem(int position) {
        return CourseContainer.instance().get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    protected View getViewImpl(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            int layout = R.layout.course_item;
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(layout, parent, false);
        }
        ViewGroup group = (ViewGroup)convertView;
        Course course = getItem(position);
        ((TextView)group.findViewById(android.R.id.text1)).setText(course.name);
        ((TextView)group.findViewById(android.R.id.text2)).setText(course.number);
        return group;
    }

    private void discardSelectedItems() {
        Set<Long> selection = getCheckedItems();
        Course[] courses = new Course[selection.size()];
        int i = 0;
        for (long position : selection) {
            courses[i++] = getItem((int)position);
        }
        for (Course course : courses) {
            CourseContainer.instance().remove(course);
        }
        CourseContainer.instance().notifyObservers();
        notifyDataSetChanged();
        finishActionMode();
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        mode.getMenuInflater().inflate(R.menu.courses_action_menu, menu);
        return true;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        if(item.getItemId() == R.id.delete){
            Toast.makeText(getContext(), "Delete", Toast.LENGTH_SHORT).show();
            discardSelectedItems();
            return true;
        }
        return false;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }
}
