package tup.dota2recipe;

import java.io.IOException;

import org.json2.JSONException;

import tup.dota2recipe.adapter.DBAdapter;
import tup.dota2recipe.adapter.ItemsImagesAdapter;
import tup.dota2recipe.entity.FavoriteItem;
import tup.dota2recipe.entity.ItemsItem;
import tup.dota2recipe.util.Utils;
import tup.dota2recipe.view.SimpleGridView;
import android.annotation.SuppressLint;
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
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;
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
    /**
     * 父物品名称(合成卷轴使用) Intent 参数
     */
    public final static String KEY_ITEMS_DETAIL_PARENT_KEY_NAME = "KEY_ITEMS_DETAIL_PARENT_KEY_NAME";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Utils.fillFragment(this, ItemsDetailFragment.newInstance(
                this.getIntent().getStringExtra(KEY_ITEMS_DETAIL_KEY_NAME),
                this.getIntent().getStringExtra(KEY_ITEMS_DETAIL_PARENT_KEY_NAME)));
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
    public static class ItemsDetailFragment extends SherlockFragment implements
            SimpleGridView.OnItemClickListener {
        private DisplayImageOptions mImageLoadOptions;
        private ItemsItem mItemsItem;

        static ItemsDetailFragment newInstance(String items_keyName) {
            return newInstance(items_keyName, null);
        }

        /**
         * 
         * @param items_keyName
         * @param items_parent_keyName
         * @return
         */
        static ItemsDetailFragment newInstance(String items_keyName,
                String items_parent_keyName) {
            final ItemsDetailFragment f = new ItemsDetailFragment();
            final Bundle b = new Bundle();
            b.putString(KEY_ITEMS_DETAIL_KEY_NAME, items_keyName);
            if (!TextUtils.isEmpty(items_parent_keyName)) {
                b.putString(KEY_ITEMS_DETAIL_PARENT_KEY_NAME,
                        items_parent_keyName);
            }
            f.setArguments(b);
            return f;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            setHasOptionsMenu(true);
            mImageLoadOptions = Utils.createDisplayImageOptions();

            final Bundle arg = this.getArguments();
            final String items_keyName = arg.getString(KEY_ITEMS_DETAIL_KEY_NAME);
            final String items_parent_keyName =
                    arg.containsKey(KEY_ITEMS_DETAIL_PARENT_KEY_NAME) ?
                            arg.getString(KEY_ITEMS_DETAIL_PARENT_KEY_NAME) : null;

            Log.v(TAG, "arg.items_keyName=" + items_keyName
                    + " arg.items_parent_keyName" + items_parent_keyName);

            if (!TextUtils.isEmpty(items_keyName)) {
                Utils.executeAsyncTask(mLoaderTask, items_keyName,
                        items_parent_keyName);
            }
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_itemsdetail, container,
                    false);
        }

        @Override
        public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
            super.onCreateOptionsMenu(menu, inflater);

            inflater.inflate(R.menu.fragment_itemsdetail, menu);
        }

        @Override
        public void onPrepareOptionsMenu(Menu menu) {
            super.onPrepareOptionsMenu(menu);

            // ----加收藏按钮---
            if (mItemsItem == null) {
                return;
            }
            final MenuItem check =
                    menu.findItem(R.id.menu_check_addcollection);
            if (check == null) {
                return;
            }

            check.setChecked(mItemsItem.hasFavorite == 1);
            Utils.configureStarredMenuItem(check, mItemsItem.isrecipe);
            if (mItemsItem.isrecipe) {
                return;
            }

            check.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                @SuppressLint("NewApi")
                public boolean onMenuItemClick(MenuItem item) {
                    final boolean isChecked = !item.isChecked();
                    final ItemsItem items = mItemsItem;
                    item.setChecked(isChecked);

                    Utils.configureStarredMenuItem(item, items.isrecipe);
                    items.hasFavorite = isChecked ? 1 : 0;
                    if (isChecked) {
                        final FavoriteItem c = new FavoriteItem();
                        c.keyName = items.keyName;
                        c.type = FavoriteItem.KEY_TYPE_ITEMS;
                        DBAdapter.getInstance().addFavorite(c);
                    } else {
                        DBAdapter.getInstance().deleteFavorite(items.keyName);
                    }
                    return true;
                }
            });
        }

        /**
         * 绑定物品视图
         * 
         * @param cItem
         */
        @SuppressLint("NewApi")
        private void bindItemsItemView(ItemsItem cItem) {
            if (cItem == null) {
                return;
            }

            mItemsItem = cItem;
            final SherlockFragmentActivity cContext = this
                    .getSherlockActivity();
            cContext.invalidateOptionsMenu();
            cContext.setTitle(cItem.dname_l);
            final View v = this.getView();
            bindItemsItemSimpleView(v, cItem, mImageLoadOptions);

            // 合成卷轴处理
            if (cItem.isrecipe) {
                final View layout_items_desc = v
                        .findViewById(R.id.layout_items_desc);
                layout_items_desc.setVisibility(View.GONE);

                final View layout_items_desc1 = v
                        .findViewById(R.id.layout_items_desc1);
                if (layout_items_desc1 != null) {
                    layout_items_desc1.setVisibility(View.GONE);
                }

                return;
            }

            ((TextView) v.findViewById(R.id.text_items_desc)).setText(Html
                    .fromHtml(cItem.desc));
            ((TextView) v.findViewById(R.id.text_items_lore))
                    .setText(cItem.lore);
            ((TextView) v.findViewById(R.id.text_items_attrib)).setText(Html
                    .fromHtml(cItem.attrib));
            // mc
            if (!TextUtils.isEmpty(cItem.mc)) {
                ((TextView) v.findViewById(R.id.text_items_mana))
                        .setText(cItem.mc);
            } else {
                v.findViewById(R.id.rlayout_items_mana)
                        .setVisibility(View.GONE);
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
                        cContext, mImageLoadOptions, cItem.components_i);

                final SimpleGridView grid = (SimpleGridView) v
                        .findViewById(R.id.grid_items_components);
                grid.setAdapter(adapter);
                grid.setOnItemClickListener(this);
            } else {
                v.findViewById(R.id.llayout_items_components)
                        .setVisibility(View.GONE);
            }
            // tocomponents
            if (cItem.tocomponents != null && cItem.tocomponents.length > 0) {
                final ItemsImagesAdapter adapter = new ItemsImagesAdapter(
                        cContext, mImageLoadOptions, cItem.tocomponents_i);

                final SimpleGridView grid = (SimpleGridView) v
                        .findViewById(R.id.grid_items_tocomponents);
                grid.setAdapter(adapter);
                grid.setOnItemClickListener(this);
            } else {
                v.findViewById(R.id.llayout_items_tocomponents)
                        .setVisibility(View.GONE);
            }
        }

        /**
         * 绑定视图-物品简单数据信息
         * 
         * @param v
         * @param cItem
         * @param cImageLoadOptions
         */
        public static void bindItemsItemSimpleView(final View v, final ItemsItem cItem,
                final DisplayImageOptions cImageLoadOptions) {
            if (v == null || cItem == null || cImageLoadOptions == null) {
                return;
            }
            
            ImageLoader.getInstance().displayImage(
                    Utils.getItemsImageUri(cItem.keyName),
                    ((ImageView) v.findViewById(R.id.image_items)),
                    cImageLoadOptions);

            ((TextView) v.findViewById(R.id.text_items_dname)).setText(cItem.dname);
            ((TextView) v.findViewById(R.id.text_items_dname_l)).setText(cItem.dname_l);
            ((TextView) v.findViewById(R.id.text_items_cost)).setText(String.valueOf(cItem.cost));
        }

        /**
         * 物品详细 LoaderTask
         */
        private final AsyncTask<String, Void, ItemsItem> mLoaderTask = new AsyncTask<String, Void, ItemsItem>() {
            @Override
            protected void onPreExecute() {
                ItemsDetailFragment.this.getSherlockActivity()
                        .setSupportProgressBarIndeterminateVisibility(true);

                super.onPreExecute();
            }

            @Override
            protected void onCancelled() {
                super.onCancelled();

                ItemsDetailFragment.this.getSherlockActivity()
                        .setSupportProgressBarIndeterminateVisibility(false);
            }

            @Override
            protected ItemsItem doInBackground(String... params) {
                try {
                    String keyName = params[0];
                    final boolean isrecipe = keyName
                            .equals(DataManager.KEY_NAME_RECIPE_ITEMS_KEYNAME);
                    if (isrecipe) {
                        keyName = params[1];
                    }
                    final ItemsItem cItem = DataManager.getItemsItem(
                            ItemsDetailFragment.this.getSherlockActivity(),
                            keyName);
                    if (!isrecipe && cItem != null && cItem.hasFavorite < 0) {
                        final boolean has = DBAdapter.getInstance()
                                .hasFavorite(keyName);
                        cItem.hasFavorite = has ? 1 : 0;
                    }
                    // 合成卷轴数据合并
                    if (isrecipe) {
                        final ItemsItem recipeItem = cItem.components_i
                                .get(cItem.components_i.size() - 1);
                        final ItemsItem resRecipeItem = new ItemsItem();
                        resRecipeItem.cost = recipeItem.cost;
                        resRecipeItem.dname = cItem.dname + " "
                                + recipeItem.dname;
                        resRecipeItem.dname_l = cItem.dname_l
                                + recipeItem.dname_l;
                        resRecipeItem.isrecipe = true;
                        resRecipeItem.keyName = recipeItem.keyName;
                        resRecipeItem.parent_keyName = recipeItem.parent_keyName;
                        return resRecipeItem;
                    }
                    return cItem;
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
                ItemsDetailFragment.this.getSherlockActivity()
                        .setSupportProgressBarIndeterminateVisibility(false);
            }
        };

        @Override
        public void onItemClick(ListAdapter parent, View view, int position,
                long id) {
            Utils.startItemsDetailActivity(this.getSherlockActivity(),
                    (ItemsItem) parent.getItem(position));
        };
    }
}
