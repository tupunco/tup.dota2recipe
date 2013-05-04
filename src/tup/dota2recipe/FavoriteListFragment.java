package tup.dota2recipe;

import java.util.List;

import tup.dota2recipe.adapter.DBAdapter;
import tup.dota2recipe.entity.FavoriteItem;
import tup.dota2recipe.util.AbstractArrayAdapter;
import tup.dota2recipe.util.AbstractAsyncTaskLoader;
import tup.dota2recipe.util.Utils;
import android.content.Context;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockListFragment;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 物品数据列表 Fragment
 * 
 * @author tupunco
 * 
 */
public class FavoriteListFragment extends SherlockListFragment
        implements OnItemClickListener,
        LoaderManager.LoaderCallbacks<List<FavoriteItem>> {
    private static final String TAG = "FavoriteListFragment";

    private FavoriteListAdapter mAdapter = null;
    private DisplayImageOptions mImageLoadOptions;

    private boolean mFavoriteDataReload = false;
    private final DataSetObserver mFavoriteDataSetObserver = new DataSetObserver() {
        @Override
        public void onChanged() {
            mFavoriteDataReload = true;
        }
    };

    public FavoriteListFragment() {
        super();

        DBAdapter.getInstance()
                .registerFavoriteObserver(mFavoriteDataSetObserver);
    }

    @Override
    protected void finalize() throws Throwable {
        Log.v(TAG, "FavoriteListFragment-finalize...");

        DBAdapter.getInstance()
                .unregisterFavoriteObserver(mFavoriteDataSetObserver);
        super.finalize();
    }

    @Override
    public void onStart() {
        super.onStart();

        if (mFavoriteDataReload) {
            getLoaderManager().restartLoader(0, null, this);
            mFavoriteDataReload = false;
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mAdapter = new FavoriteListAdapter(this.getSherlockActivity());
        this.setListAdapter(mAdapter);
        this.setEmptyText(this.getResources().getString(R.string.text_favorite_emptylist));
        this.getListView().setOnItemClickListener(this);

        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mImageLoadOptions = Utils.createDisplayImageOptions();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final FavoriteItem cItem = (FavoriteItem) parent.getItemAtPosition(position);
        if (cItem.type == FavoriteItem.KEY_TYPE_HERO) {
            Utils.startHeroDetailActivity(this.getSherlockActivity(), cItem.heroData);
        } else if (cItem.type == FavoriteItem.KEY_TYPE_ITEMS) {
            Utils.startItemsDetailActivity(this.getSherlockActivity(), cItem.itemsData);
        }
    }

    @Override
    public Loader<List<FavoriteItem>> onCreateLoader(int arg0, Bundle arg1) {
        this.getSherlockActivity().setSupportProgressBarIndeterminateVisibility(true);
        this.setListShown(false);
        return new ItemsListLoader(this.getSherlockActivity());
    }

    @Override
    public void onLoadFinished(Loader<List<FavoriteItem>> loader,
            List<FavoriteItem> data) {
        mAdapter.setData(data);
        this.getSherlockActivity().setSupportProgressBarIndeterminateVisibility(false);
        this.setListShown(true);
    }

    @Override
    public void onLoaderReset(Loader<List<FavoriteItem>> arg0) {
        mAdapter.setData(null);
    }

    /**
     * 
     */
    final class FavoriteListAdapter extends
            AbstractArrayAdapter<FavoriteItem> {
        private final class ViewHolder {
            public TextView text;
            public ImageView image;
        }

        private final LayoutInflater mInflater;

        public FavoriteListAdapter(Context context) {
            super(context);
            mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            setNotifyOnChange(true);
        }

        public void setData(List<FavoriteItem> data) {
            clear();
            if (data != null) {
                addAll(data);
            }
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;

            final ViewHolder holder;
            if (convertView == null) {
                view = mInflater.inflate(R.layout.fragment_itemsdata_grid_item, parent, false);

                holder = new ViewHolder();
                holder.text = (TextView) view.findViewById(R.id.text_items_name);
                holder.image = (ImageView) view.findViewById(R.id.image_items);

                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }

            final FavoriteItem item = getItem(position);
            String imgUrl = null, title = null;
            switch (item.type)
            {
                case FavoriteItem.KEY_TYPE_HERO:
                    imgUrl = Utils.getHeroImageUri(item.keyName);
                    if (item.heroData != null) {
                        title = item.heroData.name_l;
                    }
                    else {
                        title = item.keyName;
                    }
                    break;
                case FavoriteItem.KEY_TYPE_ITEMS:
                    imgUrl = Utils.getItemsImageUri(item.keyName);
                    if (item.itemsData != null) {
                        title = item.itemsData.dname_l;
                    }
                    else {
                        title = item.keyName;
                    }
                    break;
            }
            if (imgUrl != null) {
                ImageLoader.getInstance().displayImage(imgUrl, holder.image, mImageLoadOptions);
            }
            holder.text.setText(title);
            // TODO--------------
            return view;
        }
    }

    /**
     * 
     */
    public static class ItemsListLoader extends
            AbstractAsyncTaskLoader<FavoriteItem> {

        public ItemsListLoader(Context context) {
            super(context);
        }

        @Override
        public List<FavoriteItem> loadInBackground() {
            cDataList = DBAdapter.getInstance().getFavorites();
            return cDataList;
        }
    }
}
