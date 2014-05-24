package tup.dota2recipe;

import tup.dota2recipe.util.Utils;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.readystatesoftware.systembartint.SystemBarTintManager;

/**
 * @author Yrom
 */
public class SwipeBackAppCompatFragmentActivity extends ActionBarActivity {
    // implements SwipeBackActivityBase {
    // private SwipeBackActivityHelper mHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // mHelper = new SwipeBackActivityHelper(this);
        // mHelper.onActivityCreate();

        final SystemBarTintManager tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setStatusBarTintResource(R.color.actionbar_bg);
        if (!Utils.hasSmartBar()) {
            tintManager.setNavigationBarTintEnabled(true);
            tintManager.setNavigationBarTintResource(R.color.statusbar_bg);
        }
    }

    /*
     * @Override
     * protected void onPostCreate(Bundle savedInstanceState) {
     * super.onPostCreate(savedInstanceState);
     * mHelper.onPostCreate();
     * }
     * 
     * @Override
     * public View findViewById(int id) {
     * View v = super.findViewById(id);
     * if (v != null)
     * return v;
     * return mHelper.findViewById(id);
     * }
     * 
     * @Override
     * public SwipeBackLayout getSwipeBackLayout() {
     * return mHelper.getSwipeBackLayout();
     * }
     * 
     * @Override
     * public void setSwipeBackEnable(boolean enable) {
     * getSwipeBackLayout().setEnableGesture(enable);
     * }
     * 
     * @Override
     * public void scrollToFinishActivity() {
     * getSwipeBackLayout().scrollToFinishActivity();
     * }
     */

}
