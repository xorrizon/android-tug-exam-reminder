package at.tugraz.examreminder.ui;

import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Display;
import android.view.Surface;
import android.view.WindowManager;
import at.tugraz.examreminder.R;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.mobidevelop.widget.SplitPaneLayout;
import com.viewpagerindicator.TabPageIndicator;


public class MainActivity extends SherlockFragmentActivity {
    SplitPaneLayout splitLayout;

    public void onCreate(android.os.Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        splitLayout = (SplitPaneLayout) findViewById(R.id.split_layout);

        FragmentAdapter adapter = new FragmentAdapter(getSupportFragmentManager());

        ViewPager pager = (ViewPager)findViewById(R.id.pager);
        pager.setAdapter(adapter);

        TabPageIndicator indicator = (TabPageIndicator) findViewById(R.id.indicator);
        indicator.setViewPager(pager);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(MainActivity.useTabletMode(this))
            splitLayout.setSplitterPositionPercent(0.4f);
        else
            splitLayout.setSplitterPositionPercent(1.f);
    }

    public static boolean useTabletMode(Context context) {
        //@Todo move to application class once it is merged
        int use_tablet_mode = Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(context).getString("pref_use_tablet_layout", "0"));
        switch (use_tablet_mode) {
            case 0: return context.getResources().getBoolean(R.bool.isTablet);
            case 1: return true;
            case 2:
                Display display = ((WindowManager) context.getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
                return display.getRotation() == Surface.ROTATION_90 || display.getRotation() == Surface.ROTATION_270;
            default: return false;
        }
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
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    class FragmentAdapter extends FragmentPagerAdapter {
        private final String[] CONTENT = new String[] { "Courses", "Exams", "Test3" };

        public FragmentAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0: return new CoursesFragment();
                case 1: return new ExamsFragment();
                default: return TestFragment.newInstance(CONTENT[position % CONTENT.length]);
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