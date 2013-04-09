package tup.dota2recipe;

import tup.dota2recipe.util.AbstractTabsAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 
 * @author tupunco
 * 
 */
public class MainActivity extends SherlockFragmentActivity {
    TabsAdapter mTabsAdapter;
    ViewPager mViewPager;

    protected ImageLoader imageLoader = ImageLoader.getInstance();
    private boolean instanceStateSaved;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_main);

        final ActionBar actionBar = this.getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        mViewPager = (ViewPager) findViewById(R.id.pager);
        mTabsAdapter = new TabsAdapter(this, mViewPager);

        mTabsAdapter = new TabsAdapter(this, mViewPager);
        mTabsAdapter.addTab(actionBar.newTab().setText(R.string.main_actionBar_tab_hero),
                HeroListFragment.class, null);
        mTabsAdapter.addTab(actionBar.newTab().setText(R.string.main_actionBar_tab_item),
                ItemsListFragment.class, null);
        // mTabsAdapter.addTab(actionBar.newTab().setText(R.string.main_actionBar_tab_skill),
        // DummySectionFragment.class, null);
        mTabsAdapter.addTab(actionBar.newTab().setText(R.string.main_actionBar_tab_favorite),
                DummySectionFragment.class, null);

        // if (savedInstanceState != null) {
        // actionBar.setSelectedNavigationItem(savedInstanceState.getInt(
        // "tab", 0));
        // }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getSupportMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.menu_about:
            this.startActivity(new Intent(this, AboutActivity.class));
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // outState.putInt("tab", getSupportActionBar()
        // .getSelectedNavigationIndex());

        instanceStateSaved = true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.actionbarsherlock.app.SherlockFragmentActivity#onDestroy()
     */
    @Override
    protected void onDestroy() {
        if (!instanceStateSaved) {
            imageLoader.stop();
        }
        super.onDestroy();
    }

    /**
     * 
     * @author tupunco
     * 
     */
    public static class TabsAdapter extends AbstractTabsAdapter {

        public TabsAdapter(SherlockFragmentActivity activity, ViewPager pager) {
            super(activity, pager);
        }

        @Override
        public void onTabUnselected(Tab tab, FragmentTransaction ft) {
        }

        @Override
        public void onTabReselected(Tab tab, FragmentTransaction ft) {
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }
    }

    /**
     * A dummy fragment representing a section of the app, but that simply
     * displays dummy text.
     */
    public static class DummySectionFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        public static final String ARG_SECTION_NUMBER = "section_number";

        public DummySectionFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            // Create a new TextView and set its text to the fragment's section
            // number argument value.
            // TextView textView = new TextView(getActivity());
            // textView.setGravity(Gravity.CENTER);
            // textView.setText(Integer.toString(getArguments().getInt(ARG_SECTION_NUMBER)));
            // return textView;
            return inflater.inflate(R.layout.fragment_main, container, false);
        }
    }
}
