package at.tugraz.examreminder.ui;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import at.tugraz.examreminder.R;
import at.tugraz.examreminder.adapter.CoursesAdapter;
import at.tugraz.examreminder.core.CourseContainer;
import com.actionbarsherlock.app.SherlockFragment;

import java.util.Observable;
import java.util.Observer;

public class CoursesFragment extends SherlockFragment implements Observer {

    private ListView courses_list_view;
    private CoursesAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CourseContainer.instance().addObserver(this);
    }

    @Override
    public void onDestroy() {
        CourseContainer.instance().deleteObserver(this);
        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.courses_fragment, container, false);
        courses_list_view = (ListView) layout.findViewById(R.id.courses_list);
        adapter = new CoursesAdapter(savedInstanceState);
        adapter.setAdapterView(courses_list_view);
        adapter.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Toast.makeText(getActivity(), "Item click: " + adapter.getItem(position).name, Toast.LENGTH_SHORT).show();
            }
        });
        return layout;
    }

    @Override
    public void update(Observable observable, Object data) {
        if(adapter != null)
            adapter.notifyDataSetChanged();
    }
}
