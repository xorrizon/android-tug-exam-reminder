package at.tugraz.examreminder.ui;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import at.tugraz.examreminder.R;
import at.tugraz.examreminder.adapter.CheckableCoursesAdapter;
import at.tugraz.examreminder.core.CourseContainer;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import java.util.Observable;
import java.util.Observer;

public class CoursesFragment extends SherlockFragment {

    private ListView courses_list_view;
    private CheckableCoursesAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.courses_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.add){
            Intent intent = new Intent(getActivity(), AddCourseActivity.class);
            startActivity(intent);
            return true;
        }
        return false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.courses_fragment, container, false);
        courses_list_view = (ListView) layout.findViewById(R.id.courses_list);
        adapter = new CheckableCoursesAdapter(savedInstanceState);
        adapter.setAdapterView(courses_list_view);
        adapter.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Toast.makeText(getActivity(), "Item click: " + adapter.getItem(position).name, Toast.LENGTH_SHORT).show();
            }
        });
        return layout;
    }

}
