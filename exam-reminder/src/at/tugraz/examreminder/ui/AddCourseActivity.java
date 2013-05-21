package at.tugraz.examreminder.ui;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import at.tugraz.examreminder.adapter.SimpleCoursesAdapter;
import at.tugraz.examreminder.core.Course;
import at.tugraz.examreminder.crawler.SimpleMockCrawler;
import com.actionbarsherlock.view.Menu;
import at.tugraz.examreminder.R;
import com.actionbarsherlock.app.SherlockListActivity;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.widget.SearchView;

import java.util.ArrayList;
import java.util.List;


public class AddCourseActivity extends SherlockListActivity {
    MenuItem searchViewItem;
    SearchView searchView;
    List<Course> courses = new ArrayList<Course>();
    SimpleCoursesAdapter adapter;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_course_activity);
        courses.addAll(SimpleMockCrawler.createCourses());
        adapter = new SimpleCoursesAdapter(this, courses);
        setListAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getSupportMenuInflater().inflate(R.menu.add_course_menu, menu);
        searchViewItem = menu.findItem(R.id.menu_search);
        searchView = (SearchView) searchViewItem.getActionView();
        searchView.setIconifiedByDefault(false);
        return true;
    }
}
