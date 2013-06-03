package at.tugraz.examreminder.ui;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import at.tugraz.examreminder.adapter.SimpleCoursesAdapter;
import at.tugraz.examreminder.core.Course;
import at.tugraz.examreminder.core.CourseContainer;
import at.tugraz.examreminder.crawler.Crawler;
import at.tugraz.examreminder.crawler.SimpleMockCrawler;
import at.tugraz.examreminder.service.CourseListSerializer;
import at.tugraz.examreminder.service.UpdateService;
import com.actionbarsherlock.view.Menu;
import at.tugraz.examreminder.R;
import com.actionbarsherlock.app.SherlockListActivity;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.widget.SearchView;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;


public class AddCourseActivity extends SherlockListActivity implements SearchView.OnQueryTextListener, AdapterView.OnItemClickListener {
    private static String BUNDLE_COURSES_KEY="COURSES_KEY";
    private static String BUNDLE_SEARCH_TERM_KEY="SEARCH_TERM_KEY";

    MenuItem searchViewItem;
    SearchView searchView;
    ProgressBar progressBar;
    List<Course> courses = new ArrayList<Course>();
    SimpleCoursesAdapter adapter;
    GregorianCalendar lastSearchSubmit = new GregorianCalendar();

    String restored_search_term = null;

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
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        List<Course> tmp_courses = CourseListSerializer.jsonToCourseList(savedInstanceState.getString(BUNDLE_COURSES_KEY));
        if(tmp_courses != null) {
            courses.addAll(tmp_courses);
            adapter.notifyDataSetChanged();
        }
        restored_search_term = savedInstanceState.getString(BUNDLE_SEARCH_TERM_KEY);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(BUNDLE_COURSES_KEY, CourseListSerializer.courseListToJson(courses));
        outState.putString(BUNDLE_SEARCH_TERM_KEY, searchView.getQuery().toString());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getSupportMenuInflater().inflate(R.menu.add_course_menu, menu);
        searchViewItem = menu.findItem(R.id.menu_search);
        searchView = (SearchView) searchViewItem.getActionView();
        searchView.setIconifiedByDefault(false);
        if(restored_search_term != null){
            searchView.setQuery(restored_search_term, false);
            restored_search_term = null;
        }
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
        GregorianCalendar now = new GregorianCalendar();

        // Dirty hack to prevent double submit android bug: https://code.google.com/p/android/issues/detail?id=24599
        if( (now.getTimeInMillis()-lastSearchSubmit.getTimeInMillis()) < 1000 ){
            return true;
        }
        lastSearchSubmit = now;

        if(getCurrentFocus()!=null){
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
            getListView().requestFocus();
        }

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
            if(courses==null) {
                Log.d("Debug", "Crawler returned null, showing error");
                String error_title = getString(R.string.error);
                String error_message = getString(R.string.search_error);
                DialogHelper.showErrorDialog(AddCourseActivity.this, error_title, error_message);
            }
        }
    }
}
