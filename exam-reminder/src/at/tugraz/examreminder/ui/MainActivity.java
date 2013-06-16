package at.tugraz.examreminder.ui;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import at.tugraz.examreminder.ExamReminderApplication;
import at.tugraz.examreminder.R;
import at.tugraz.examreminder.adapter.ExamsAdapter;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.mobidevelop.widget.SplitPaneLayout;
import com.viewpagerindicator.TabPageIndicator;


public class MainActivity extends SherlockFragmentActivity implements ViewPager.OnPageChangeListener {
    SplitPaneLayout splitLayout;
    FragmentAdapter adapter;

    public void onCreate(android.os.Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        splitLayout = (SplitPaneLayout) findViewById(R.id.split_layout);

        adapter = new FragmentAdapter(getSupportFragmentManager());

        ViewPager pager = (ViewPager)findViewById(R.id.pager);
        pager.setAdapter(adapter);

        TabPageIndicator indicator = (TabPageIndicator) findViewById(R.id.indicator);
        indicator.setViewPager(pager);
        indicator.setOnPageChangeListener(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(ExamReminderApplication.useTabletMode(this))
            splitLayout.setSplitterPositionPercent(0.4f);
        else
            splitLayout.setSplitterPositionPercent(1.f);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getSupportMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings :
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            case R.id.about:
                startActivity(new Intent(this, AboutActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onPageScrolled(int i, float v, int i2) {
    }

    @Override
    public void onPageSelected(int i) {
        if(adapter.getPageTitle(i).equals(FragmentAdapter.EXAMS)){
            ViewGroup container = (ViewGroup)splitLayout.findViewById(R.id.exams_fragment_container);
            if(container != null){
                ListView examslistview = (ListView)container.getChildAt(0);
                if(examslistview != null) {
                    ((ExamsAdapter)examslistview.getAdapter()).notifyDataSetChanged();
                }
            }
        }
    }

    @Override
    public void onPageScrollStateChanged(int i) {
    }

    class FragmentAdapter extends FragmentPagerAdapter {
        public final static String EXAMS = "Exams";
        public final static String COURSES = "Courses";
        public final String[] CONTENT = new String[] { COURSES, EXAMS };

        public FragmentAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0: return new CoursesFragment();
                case 1: return new ExamsFragment();
                default: return null;
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return CONTENT[position % CONTENT.length];
        }

        @Override
        public int getCount() {
            return CONTENT.length;
        }
    }

}