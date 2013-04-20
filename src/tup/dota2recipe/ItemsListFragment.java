package tup.dota2recipe;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.json2.JSONException;

import tup.dota2recipe.entity.ItemsItem;
import tup.dota2recipe.util.AbstractArrayAdapter;
import tup.dota2recipe.util.AbstractArrayAdapter.ArrayFilterAccepter;
import tup.dota2recipe.util.AbstractAsyncTaskLoader;
import tup.dota2recipe.util.Utils;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.SubMenu;
import com.actionbarsherlock.widget.SearchView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 物品数据列表 Fragment
 * 
 * @author tupunco
 * 
 */
public class ItemsListFragment extends SherlockFragment
        implements SearchView.OnQueryTextListener, OnItemClickListener,
        LoaderManager.LoaderCallbacks<List<ItemsItem>> {

    private static final String TAG = "ItemsListFragment";

    private static final int KEY_MENU_ITEMS_QUAL = 0;
    private static final int KEY_MENU_ITEMS_ITEMCAT = 1;

    private MenuItem menu_items_qual = null;
    private String[] menu_items_qual_values = null;
    private MenuItem menu_items_itemcat = null;
    private String[] menu_items_itemcat_values = null;

    private String[] menu_items_query_keys = new String[2];
    private static final String KEY_MENU_ITEMS_QUERY_ALL = "all";
    private static final String KEY_STATE_MENU_ITEMS_QUERY_KEYS = "KEY_STATE_MENU_ITEMS_QUERY_KEYS";

    private ItemsListAdapter mAdapter = null;
    private GridView mGridView = null;
    private DisplayImageOptions mImageLoadOptions;
    private SearchView mSearchView = null;

    public ItemsListFragment() {
        super();

        menu_items_query_keys[KEY_MENU_ITEMS_QUAL] = KEY_MENU_ITEMS_QUERY_ALL;
        menu_items_query_keys[KEY_MENU_ITEMS_ITEMCAT] = KEY_MENU_ITEMS_QUERY_ALL;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setHasOptionsMenu(true);
        Loader<Object> cLoader = getLoaderManager().getLoader(0);
        if (cLoader != null && savedInstanceState != null) {
            menu_items_query_keys = savedInstanceState
                    .getStringArray(KEY_STATE_MENU_ITEMS_QUERY_KEYS);
        }
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mImageLoadOptions = Utils.createDisplayImageOptions();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_itemsdata, container, false);

        mAdapter = new ItemsListAdapter(this.getSherlockActivity());
        mAdapter.setFilterAccepter(mItemsListFilterAccepter);

        mGridView = ((GridView) v.findViewById(R.id.itemsdata_grid));
        mGridView.setAdapter(mAdapter);
        mGridView.setTextFilterEnabled(true);
        mGridView.setOnItemClickListener(this);
        return v;
    }

    /**
     * 物品网格项 点击动作
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Utils.startItemsDetailActivity(this.getSherlockActivity(),
                (ItemsItem) parent.getItemAtPosition(position));
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArray(KEY_STATE_MENU_ITEMS_QUERY_KEYS, this.menu_items_query_keys);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.fragment_items, menu);

        final Resources cRes = this.getResources();
        // 按品质
        menu_items_qual = menu.findItem(R.id.menu_items_qual);
        menu_items_qual_values = cRes.getStringArray(R.array.menu_items_qual_values);
        createHeroOptionsSubMenu(menu_items_qual, KEY_MENU_ITEMS_QUAL,
                R.array.menu_items_qual_keys, menu_items_qual_values,
                mMenuItemsQualMenuItemClickListener);
        // 按分类
        menu_items_itemcat = menu.findItem(R.id.menu_items_itemcat);
        menu_items_itemcat_values = cRes.getStringArray(R.array.menu_items_itemcat_values);
        createHeroOptionsSubMenu(menu_items_itemcat, KEY_MENU_ITEMS_ITEMCAT,
                R.array.menu_items_itemcat_keys, menu_items_itemcat_values,
                mMenuItemsItemcatMenuItemClickListener);

        // items search view
        setupItemsSearchView(menu.findItem(R.id.action_search));
    }

    /**
     * setup Items Searchable SearchView
     * 
     * @param searchItem
     */
    private void setupItemsSearchView(MenuItem searchItem) {
        mSearchView = (SearchView) searchItem.getActionView();
        // TODO SearchableInfo
        if (mSearchView != null)
            mSearchView.setOnQueryTextListener(this);
    }

    /**
     * 创建英雄属性筛选子菜单
     * 
     * @param cMenu
     *            当前菜单项
     * @param cSubMenuKeysId
     *            当前菜单关联的子菜单项资源ID
     * @param menuItemClickListener
     *            创建后的子菜单单击监听
     */
    private void createHeroOptionsSubMenu(MenuItem cMenu, int cMenuQueryKeyId, int cSubMenuKeysId,
            String[] cSubMenuValues, MenuItem.OnMenuItemClickListener menuItemClickListener) {
        if (cMenu == null || menuItemClickListener == null) {
            return;
        }

        final SubMenu cSubMenu = cMenu.getSubMenu();
        final String[] cSubMenuKeys = this.getResources().getStringArray(cSubMenuKeysId);
        MenuItem cMenuItem = null;
        int cIndex = 0;
        for (String cMenuKey : cSubMenuKeys) {
            cMenuItem = cSubMenu.add(0, cIndex++, 0, cMenuKey);
            cMenuItem.setOnMenuItemClickListener(menuItemClickListener);
        }

        // 设置菜单默认值
        final String queryKey = menu_items_query_keys[cMenuQueryKeyId];
        if (!queryKey.equals(KEY_MENU_ITEMS_QUERY_ALL) && cSubMenuKeys != null) {
            final int queryValueIndex = Utils.indexOf(cSubMenuValues, queryKey);
            if (queryValueIndex >= 0 && cSubMenuKeys.length > queryValueIndex) {
                cMenu.setTitle(cSubMenuKeys[queryValueIndex]);
            }
        }
    }

    /**
     * 英雄筛选子菜单单击监听动作触发
     * 
     * @param cItem
     *            当前子菜单项
     * @param cParentItem
     *            当前子菜单所属父菜单项
     * @param cHeroMenuItemKeyId
     *            所属父菜单资源Key
     * @param cMenuHeroValues
     *            所属父菜单所有子项的值数组
     */
    private void doMenuHeroMenuItemClickListener(MenuItem cItem, MenuItem cParentItem,
            int cHeroMenuItemKeyId, String[] cMenuHeroValues, int cMenuItemDefaultTitleId) {
        cParentItem.setTitle(cItem.getTitle());

        final String cMenuQueryValue = menu_items_query_keys[cHeroMenuItemKeyId];
        final String cMenuValue = cMenuHeroValues[cItem.getItemId()];
        Log.v(TAG, String.format(
                "MenuItemsMenuItemClickListener-Key:%s-ID:%d-OldValue:%s-NewValue:%s",
                cHeroMenuItemKeyId,
                cItem.getItemId(), cMenuQueryValue, cMenuValue));

        if (!cMenuQueryValue.equals(cMenuValue)) {
            menu_items_query_keys[cHeroMenuItemKeyId] = cMenuValue;
            getLoaderManager().restartLoader(0, null, this);
        }
        if (cMenuValue.equals(KEY_MENU_ITEMS_QUERY_ALL)) {
            cParentItem.setTitle(cMenuItemDefaultTitleId);
        }
    }

    /**
     * 按物品品质筛选子菜单单击监听
     */
    private MenuItem.OnMenuItemClickListener mMenuItemsQualMenuItemClickListener = new MenuItem.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            doMenuHeroMenuItemClickListener(item, menu_items_qual, KEY_MENU_ITEMS_QUAL,
                    menu_items_qual_values, R.string.menu_items_qual);

            return true;
        }
    };
    /**
     * 按物品分类筛选子菜单单击监听
     */
    private MenuItem.OnMenuItemClickListener mMenuItemsItemcatMenuItemClickListener = new MenuItem.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            doMenuHeroMenuItemClickListener(item, menu_items_itemcat, KEY_MENU_ITEMS_ITEMCAT,
                    menu_items_itemcat_values, R.string.menu_items_itemcat);

            return true;
        }
    };

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (TextUtils.isEmpty(newText)) {
            mGridView.clearTextFilter();
        } else {
            mGridView.setFilterText(newText);
        }
        return false;
    }

    @Override
    public Loader<List<ItemsItem>> onCreateLoader(int arg0, Bundle arg1) {
        this.getSherlockActivity().setSupportProgressBarIndeterminateVisibility(true);
        return new ItemsListLoader(this.getSherlockActivity(), this.menu_items_query_keys);
    }

    @Override
    public void onLoadFinished(Loader<List<ItemsItem>> loader, List<ItemsItem> data) {
        mAdapter.setData(data);
        this.getSherlockActivity().setSupportProgressBarIndeterminateVisibility(false);
    }

    @Override
    public void onLoaderReset(Loader<List<ItemsItem>> arg0) {
        mAdapter.setData(null);
    }

    /**
     * ItemsListAdapter item filter accepter
     */
    private final ArrayFilterAccepter<ItemsItem> mItemsListFilterAccepter = new ArrayFilterAccepter<ItemsItem>() {
        /**
         * 搜索物品名称 Accepter
         * 
         * @param cDataItem
         * @param constraint
         * @return
         */
        @SuppressLint("DefaultLocale")
        @Override
        public boolean Accept(ItemsItem cDataItem, CharSequence constraint) {
            if (cDataItem == null || TextUtils.isEmpty(cDataItem.dname_l)) {
                return false;
            }
            if (TextUtils.isEmpty(constraint)) {
                return true;
            }

            final String prefixString = constraint.toString().toLowerCase();
            if (cDataItem.dname_l.indexOf(prefixString) > -1) {
                return true;
            }

            if (!TextUtils.isEmpty(cDataItem.dname)) {
                return cDataItem.dname.startsWith(prefixString);
            }
            return false;
        }
    };

    /**
     * 
     */
    final class ItemsListAdapter extends AbstractArrayAdapter<ItemsItem> {
        private final class ViewHolder {
            public TextView text;
            public ImageView image;
        }

        private final LayoutInflater mInflater;

        public ItemsListAdapter(Context context) {
            super(context);
            mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            setNotifyOnChange(true);
        }

        public void setData(List<ItemsItem> data) {
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

            final ItemsItem item = getItem(position);
            ImageLoader.getInstance().displayImage(Utils.getItemsImageUri(item.keyName),
                    holder.image, mImageLoadOptions);
            holder.text.setText(item.dname_l);

            return view;
        }
    }

    /**
     * 
     */
    public static class ItemsListLoader extends AbstractAsyncTaskLoader<ItemsItem> {
        final String[] menu_items_query_keys;

        public ItemsListLoader(Context context, String[] queryKeys) {
            super(context);
            menu_items_query_keys = queryKeys;
        }

        @Override
        public List<ItemsItem> loadInBackground() {
            try {
                cDataList = DataManager.getItemsList(getContext());
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            cDataList = buildItemsQuery(cDataList);
            return cDataList;
        }

        /**
         * 根据菜单选择项对物品数据进行过滤
         */
        private List<ItemsItem> buildItemsQuery(List<ItemsItem> mOriginalList) {
            final String[] cQueryKeys = this.menu_items_query_keys;
            if (mOriginalList == null || mOriginalList.size() <= 0
                    || cQueryKeys == null || cQueryKeys.length <= 0) {
                return mOriginalList;
            }

            final String cQueryKey_qual = cQueryKeys[KEY_MENU_ITEMS_QUAL];
            final String cQueryKey_itemcat = cQueryKeys[KEY_MENU_ITEMS_ITEMCAT];

            if (!cQueryKey_qual.equals(KEY_MENU_ITEMS_QUERY_ALL)
                    || !cQueryKey_itemcat.equals(KEY_MENU_ITEMS_QUERY_ALL))
            {
                return (ArrayList<ItemsItem>) CollectionUtils.select(mOriginalList,
                        new Predicate<ItemsItem>() {
                            @Override
                            public boolean evaluate(ItemsItem cObject) {
                                int cQuery = -1; //
                                final String key_all = KEY_MENU_ITEMS_QUERY_ALL;
                                if (!cQueryKey_qual.equals(key_all)
                                        && !TextUtils.isEmpty(cObject.qual)) {
                                    cQuery = cObject.qual.equals(cQueryKey_qual) ? 1
                                            : 0;
                                }
                                if (cQuery != 0 && !cQueryKey_itemcat.equals(key_all)
                                        && !TextUtils.isEmpty(cObject.itemcat)) {
                                    cQuery = (cObject.itemcat.equals(cQueryKey_itemcat)
                                            || cObject.itembasecat.equals(cQueryKey_itemcat))
                                            ? 1 : 0;
                                }
                                return cQuery == 1;
                            }
                        });
            }
            return mOriginalList;
        }
    }
}
