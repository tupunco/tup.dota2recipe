package tup.dota2recipe.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.ListAdapter;

/**
 * 简单 ListView
 * 
 * @author tupunco
 */
public class SimpleListView extends LinearLayout {
    ListAdapter mAdapter;

    public SimpleListView(Context context) {
        super(context);
    }

    public SimpleListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @SuppressLint("NewApi")
    public SimpleListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public ListAdapter getAdapter() {
        return mAdapter;
    }

    public void setAdapter(ListAdapter adapter) {
        this.mAdapter = adapter;

        if (mAdapter != null)
            initViews();
    }

    @SuppressLint("InlinedApi")
    private void initViews() {
        removeAllViews();
        if (mAdapter == null)
            return;

        final int count = mAdapter.getCount();
        for (int i = 0; i < count; i++) {
            if (i > 0 && i < count) {
                this.addView(new Divider(this.getContext()));
            }
            this.addView(mAdapter.getView(i, null, this));
        }
    }
}
