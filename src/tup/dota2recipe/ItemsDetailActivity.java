package tup.dota2recipe;

import java.io.IOException;

import org.json.JSONException;

import tup.dota2recipe.adapter.ItemsImagesAdapter;
import tup.dota2recipe.entity.ItemsItem;
import tup.dota2recipe.util.Utils;
import tup.dota2recipe.view.SimpleGridView;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 物品详细 Activity
 * 
 * @author tupunco
 */
public class ItemsDetailActivity extends SherlockFragmentActivity {
    private static final String TAG = "ItemsDetailActivity";
    /**
     * 物品名称 Intent 参数
     */
    public final static String KEY_ITEMS_DETAIL_KEY_NAME = "KEY_ITEMS_DETAIL_KEY_NAME";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Utils.fillFragment(this, ItemsDetailFragment.newInstance(
                this.getIntent().getStringExtra(KEY_ITEMS_DETAIL_KEY_NAME)));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 物品详细 Fragment
     */
    public static class ItemsDetailFragment extends SherlockFragment
            implements SimpleGridView.OnItemClickListener {
        private DisplayImageOptions mImageLoadOptions;

        /**
         * 
         * @param items_keyName
         * @return
         */
        static ItemsDetailFragment newInstance(String items_keyName) {
            final ItemsDetailFragment f = new ItemsDetailFragment();
            final Bundle b = new Bundle();
            b.putString(KEY_ITEMS_DETAIL_KEY_NAME, items_keyName);
            f.setArguments(b);
            return f;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            mImageLoadOptions = Utils.createDisplayImageOptions();

            final String items_keyName = this.getArguments().getString(KEY_ITEMS_DETAIL_KEY_NAME);
            Log.v(TAG, "arg.items_keyName=" + items_keyName);
            if (!TextUtils.isEmpty(items_keyName)) {
                Utils.executeAsyncTask(mLoaderTask, items_keyName);
            }
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_itemsdetail, container, false);
        }

        /**
         * 
         * @param cItem
         */
        private void bindItemsItemView(ItemsItem cItem) {
            if (cItem == null) {
                return;
            }

            this.getSherlockActivity().setTitle(cItem.dname_l);

            final View v = this.getView();

            ImageLoader.getInstance().displayImage(Utils.getItemsImageUri(cItem.keyName),
                    ((ImageView) v.findViewById(R.id.image_items)), mImageLoadOptions);

            ((TextView) v.findViewById(R.id.text_items_dname)).setText(cItem.dname);
            ((TextView) v.findViewById(R.id.text_items_dname_l)).setText(cItem.dname_l);
            ((TextView) v.findViewById(R.id.text_items_cost)).setText(String.valueOf(cItem.cost));
            ((TextView) v.findViewById(R.id.text_items_desc)).setText(Html.fromHtml(cItem.desc));
            ((TextView) v.findViewById(R.id.text_items_lore)).setText(cItem.lore);
            ((TextView) v.findViewById(R.id.text_items_attrib))
                    .setText(Html.fromHtml(cItem.attrib));
            // mc
            if (!TextUtils.isEmpty(cItem.mc)) {
                ((TextView) v.findViewById(R.id.text_items_mana)).setText(cItem.mc);
            } else {
                v.findViewById(R.id.rlayout_items_mana).setVisibility(View.GONE);
            }
            // cd
            if (cItem.cd > 0) {
                ((TextView) v.findViewById(R.id.text_items_cd)).setText(String
                        .valueOf(cItem.cd));
            } else {
                v.findViewById(R.id.rlayout_items_cd).setVisibility(View.GONE);
            }
            // components
            if (cItem.components != null && cItem.components.length > 0) {
                final ItemsImagesAdapter adapter = new ItemsImagesAdapter(
                        this.getSherlockActivity(), mImageLoadOptions, cItem.components_i);

                final SimpleGridView grid = (SimpleGridView) v
                        .findViewById(R.id.grid_items_components);
                grid.setAdapter(adapter);
                grid.setOnItemClickListener(this);
            }
            else {
                v.findViewById(R.id.llayout_items_components).setVisibility(View.GONE);
            }
            // tocomponents
            if (cItem.tocomponents != null && cItem.tocomponents.length > 0) {
                final ItemsImagesAdapter adapter = new ItemsImagesAdapter(
                        this.getSherlockActivity(), mImageLoadOptions, cItem.tocomponents_i);

                final SimpleGridView grid = (SimpleGridView) v
                        .findViewById(R.id.grid_items_tocomponents);
                grid.setAdapter(adapter);
                grid.setOnItemClickListener(this);
            }
            else {
                v.findViewById(R.id.llayout_items_tocomponents).setVisibility(View.GONE);
            }
        }

        /**
         * 
         */
        private final AsyncTask<String, Void, ItemsItem> mLoaderTask = new AsyncTask<String, Void, ItemsItem>() {

            @Override
            protected ItemsItem doInBackground(String... params) {
                try {
                    return DataManager.getItemsItem(ItemsDetailFragment.this.getSherlockActivity(),
                            params[0]);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(ItemsItem result) {
                super.onPostExecute(result);
                ItemsDetailFragment.this.bindItemsItemView(result);
            }
        };

        @Override
        public void onItemClick(ListAdapter parent, View view, int position, long id) {
            Utils.startItemsDetailActivity(this.getSherlockActivity(),
                    (ItemsItem) parent.getItem(position));
        };
    }
}
