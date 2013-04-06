package tup.dota2recipe.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.widget.ListAdapter;

/**
 * Simple GridView
 * 
 * @author tupunco
 */
public class SimpleGridView extends FlowLayout {
    /**
     * Interface definition for a callback to be invoked when an item in this
     * AdapterView has been clicked.
     */
    public interface OnItemClickListener {

        /**
         * Callback method to be invoked when an item in this AdapterView has
         * been clicked.
         * <p>
         * Implementers can call getItemAtPosition(position) if they need to
         * access the data associated with the selected item.
         * 
         * @param parent
         *            The AdapterView where the click happened.
         * @param view
         *            The view within the AdapterView that was clicked (this
         *            will be a view provided by the adapter)
         * @param position
         *            The position of the view in the adapter.
         * @param id
         *            The row id of the item that was clicked.
         */
        void onItemClick(ListAdapter parent, View view, int position, long id);
    }

    ListAdapter mAdapter;

    public SimpleGridView(Context context) {
        super(context);
    }

    public SimpleGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @SuppressLint("NewApi")
    public SimpleGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * The listener that receives notifications when an item is clicked.
     */
    private OnItemClickListener mOnItemClickListener;

    /**
     * Register a callback to be invoked when an item in this AdapterView has
     * been clicked.
     * 
     * @param listener
     *            The callback that will be invoked.
     */
    public void setOnItemClickListener(OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    /**
     * Call the OnItemClickListener, if it is defined.
     * 
     * @param view
     *            The view within the AdapterView that was clicked.
     * @param position
     *            The position of the view in the adapter.
     * @param id
     *            The row id of the item that was clicked.
     * @return True if there was an assigned OnItemClickListener that was
     *         called, false otherwise is returned.
     */
    public boolean performItemClick(View view, int position, long id) {
        if (mOnItemClickListener != null) {
            playSoundEffect(SoundEffectConstants.CLICK);
            if (view != null) {
                view.sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_CLICKED);
            }
            mOnItemClickListener.onItemClick(this.mAdapter, view, position, id);
            return true;
        }

        return false;
    }

    public ListAdapter getAdapter() {
        return mAdapter;
    }

    public void setAdapter(ListAdapter adapter) {
        this.mAdapter = adapter;

        if (mAdapter != null)
            initViews();
    }

    private void initViews() {
        removeAllViews();
        if (mAdapter == null)
            return;

        final int count = mAdapter.getCount();
        View cView = null;
        for (int i = 0; i < count; i++) {
            final int cIndex = i;
            cView = mAdapter.getView(i, null, this);
            cView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    performItemClick(v, cIndex, v.getId());
                }
            });
            this.addView(cView);
        }
    }
}
