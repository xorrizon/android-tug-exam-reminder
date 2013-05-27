package at.tugraz.examreminder.ui;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Toast;
import at.tugraz.examreminder.adapter.SimpleCoursesAdapter;
import at.tugraz.examreminder.core.Course;
import at.tugraz.examreminder.core.CourseContainer;
import at.tugraz.examreminder.crawler.Crawler;
import at.tugraz.examreminder.crawler.SimpleMockCrawler;
import at.tugraz.examreminder.service.UpdateService;
import com.actionbarsherlock.view.Menu;
import at.tugraz.examreminder.R;
import com.actionbarsherlock.app.SherlockListActivity;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.widget.SearchView;

import java.util.ArrayList;
import java.util.List;


public class AddCourseActivity extends SherlockListActivity implements SearchView.OnQueryTextListener, AdapterView.OnItemClickListener {
    MenuItem searchViewItem;
    SearchView searchView;
    ProgressBar progressBar;
    List<Course> courses = new ArrayList<Course>();
    SimpleCoursesAdapter adapter;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_course_activity);
        progressBar = (ProgressBar)findViewById(R.id.progress);
        adapter = new SimpleCoursesAdapter(this, courses);
        setListAdapter(adapter);
        getListView().setOnItemClickListener(this);
        getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getSupportMenuInflater().inflate(R.menu.add_course_menu, menu);
        searchViewItem = menu.findItem(R.id.menu_search);
        searchView = (SearchView) searchViewItem.getActionView();
        searchView.setIconifiedByDefault(false);
//        searchView.setSubmitButtonEnabled(true);
        searchView.setOnQueryTextListener(this);
        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Course course = adapter.getItem(position);
        CourseContainer.instance().add(course);
        CourseContainer.instance().notifyObservers();
        finish();
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        Crawler crawler = UpdateService.getCrawlerInstance();
        new SearchCoursesTask(query, crawler).execute();
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }


    private class SearchCoursesTask extends AsyncTask<Void, Void, List<Course>> {
        private String query;
        private Crawler crawler;

        public SearchCoursesTask(String query, Crawler crawler) {
            this.query = query;
            this.crawler = crawler;
        }

        @Override
        protected void onPreExecute() {
            AddCourseActivity.this.searchView.setEnabled(false);
            AddCourseActivity.this.progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected List<Course> doInBackground(Void... params) {
            return crawler.getCourseList(query);
        }

        @Override
        protected void onPostExecute(List<Course> courses) {
            AddCourseActivity.this.courses.clear();
            if(courses != null)
                AddCourseActivity.this.courses.addAll(courses);
            AddCourseActivity.this.adapter.notifyDataSetChanged();
            AddCourseActivity.this.searchView.setEnabled(true);
            AddCourseActivity.this.progressBar.setVisibility(View.GONE);
        }
    }
}
