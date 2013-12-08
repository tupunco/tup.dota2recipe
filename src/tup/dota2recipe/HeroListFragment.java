package tup.dota2recipe;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.json2.JSONException;

import tup.dota2recipe.entity.HeroItem;
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
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.SubMenu;
import com.actionbarsherlock.widget.SearchView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 英雄数据列表 Fragment
 * 
 * @author tupunco
 * 
 */
public class HeroListFragment extends SherlockFragment
        implements SearchView.OnQueryTextListener, OnItemClickListener,
        LoaderManager.LoaderCallbacks<List<HeroItem>> {

    private static final String TAG = "HeroDataFragment";

    private static final int KEY_MENU_HERO_ROLE = 0;
    private static final int KEY_MENU_HERO_TYPE = 1;
    private static final int KEY_MENU_HERO_ATTACK = 2;
    private static final int KEY_MENU_HERO_FACTIONS = 3;
    private static final int KEY_MENU_HERO_STATSALL = 4;

    private MenuItem menu_hero_role = null;
    private String[] menu_hero_role_values = null;
    private MenuItem menu_hero_type = null;
    private String[] menu_hero_type_values = null;
    private MenuItem menu_hero_attack = null;
    private String[] menu_hero_attack_values = null;
    private MenuItem menu_hero_factions = null;
    private String[] menu_hero_factions_values = null;
    private MenuItem menu_hero_statsall = null;
    private String[] menu_hero_statsall_values = null;

    private String[] menu_hero_query_keys = new String[5];
    private static final String KEY_MENU_HERO_QUERY_ALL = "all";
    private static final String KEY_MENU_HERO_QUERY_DEFAULT = "default";
    private static final String KEY_STATE_MENU_HERO_QUERY_KEYS = "KEY_STATE_MENU_HERO_QUERY_KEYS";

    private HeroListAdapter mAdapter = null;
    private GridView mGridView = null;
    private DisplayImageOptions mImageLoadOptions;
    private SearchView mSearchView = null;

    public HeroListFragment() {
        super();

        menu_hero_query_keys[KEY_MENU_HERO_ROLE] = KEY_MENU_HERO_QUERY_ALL;
        menu_hero_query_keys[KEY_MENU_HERO_TYPE] = KEY_MENU_HERO_QUERY_ALL;
        menu_hero_query_keys[KEY_MENU_HERO_ATTACK] = KEY_MENU_HERO_QUERY_ALL;
        menu_hero_query_keys[KEY_MENU_HERO_FACTIONS] = KEY_MENU_HERO_QUERY_ALL;
        menu_hero_query_keys[KEY_MENU_HERO_STATSALL] = KEY_MENU_HERO_QUERY_DEFAULT;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setHasOptionsMenu(true);

        Loader<Object> cLoader = getLoaderManager().getLoader(0);
        if (cLoader != null && savedInstanceState != null) {
            menu_hero_query_keys = savedInstanceState
                    .getStringArray(KEY_STATE_MENU_HERO_QUERY_KEYS);
        }
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_herodata, container,
                false);

        mAdapter = new HeroListAdapter(this.getSherlockActivity());
        mAdapter.setFilterAccepter(mHeroListFilterAccepter);

        mGridView = ((GridView) v.findViewById(R.id.herodata_grid));
        mGridView.setAdapter(mAdapter);
        mGridView.setTextFilterEnabled(true);
        mGridView.setOnItemClickListener(this);
        return v;
    }

    /**
     * 物品网格项 点击动作
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
            long id) {
        Utils.startHeroDetailActivity(this.getSherlockActivity(),
                (HeroItem) parent.getItemAtPosition(position));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mImageLoadOptions = Utils.createDisplayImageOptions();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArray(KEY_STATE_MENU_HERO_QUERY_KEYS,
                this.menu_hero_query_keys);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.fragment_hero, menu);

        final Resources cRes = this.getResources();
        // 按定位
        menu_hero_role = menu.findItem(R.id.menu_hero_role);
        menu_hero_role_values = cRes.getStringArray(R.array.menu_hero_role_values);
        createHeroOptionsSubMenu(menu_hero_role, KEY_MENU_HERO_ROLE,
                R.array.menu_hero_role_keys, menu_hero_role_values,
                mMenuHeroRoleMenuItemClickListener);

        // 按属性类型
        menu_hero_type = menu.findItem(R.id.menu_hero_type);
        menu_hero_type_values = cRes.getStringArray(R.array.menu_hero_type_values);
        createHeroOptionsSubMenu(menu_hero_type, KEY_MENU_HERO_TYPE,
                R.array.menu_hero_type_keys, menu_hero_type_values,
                mMenuHeroTypeMenuItemClickListener);

        // 按攻击类型
        menu_hero_attack = menu.findItem(R.id.menu_hero_attack);
        menu_hero_attack_values = cRes.getStringArray(R.array.menu_hero_attack_values);
        createHeroOptionsSubMenu(menu_hero_attack, KEY_MENU_HERO_ATTACK,
                R.array.menu_hero_attack_keys, menu_hero_attack_values,
                mMenuHeroAttackMenuItemClickListener);

        // 按阵营
        menu_hero_factions = menu.findItem(R.id.menu_hero_factions);
        menu_hero_factions_values = cRes.getStringArray(R.array.menu_hero_factions_values);
        createHeroOptionsSubMenu(menu_hero_factions, KEY_MENU_HERO_FACTIONS,
                R.array.menu_hero_factions_keys, menu_hero_factions_values,
                mMenuHeroFactionsMenuItemClickListener);

        // 排序-按英雄统计参数
        menu_hero_statsall = menu.findItem(R.id.menu_hero_statsall);
        menu_hero_statsall_values = cRes.getStringArray(R.array.menu_hero_statsall_values);
        createHeroOptionsSubMenu(menu_hero_statsall, KEY_MENU_HERO_STATSALL,
                R.array.menu_hero_statsall_keys, menu_hero_statsall_values,
                mMenuHeroStatsAllMenuItemClickListener);

        // hero search view
        setupHeroSearchView(menu.findItem(R.id.action_search));
    }

    /**
     * setup Hero Searchable SearchView
     * 
     * @param searchItem
     */
    private void setupHeroSearchView(MenuItem searchItem) {
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
     * @param cMenuQueryKeyId
     * @param cSubMenuKeysId
     *            当前菜单关联的子菜单项资源ID
     * @param cSubMenuValues
     *            当前菜单关联的子菜单项 Values
     * @param menuItemClickListener
     *            创建后的子菜单单击监听
     */
    private void createHeroOptionsSubMenu(MenuItem cMenu, int cMenuQueryKeyId,
            int cSubMenuKeysId, String[] cSubMenuValues,
            MenuItem.OnMenuItemClickListener menuItemClickListener) {
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
        final String queryKey = menu_hero_query_keys[cMenuQueryKeyId];
        if (!queryKey.equals(KEY_MENU_HERO_QUERY_ALL)
                && !queryKey.equals(KEY_MENU_HERO_QUERY_DEFAULT)
                && cSubMenuKeys != null) {
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
    private void doMenuHeroMenuItemClickListener(MenuItem cItem,
            MenuItem cParentItem,
            int cHeroMenuItemKeyId, String[] cMenuHeroValues,
            int cMenuItemDefaultTitleId) {
        cParentItem.setTitle(cItem.getTitle());

        final String cMenuQueryValue = menu_hero_query_keys[cHeroMenuItemKeyId];
        final String cMenuValue = cMenuHeroValues[cItem.getItemId()];

        Log.v(TAG,
                String.format(
                        "MenuHeroMenuItemClickListener-Key:%s-ID:%d-OldValue:%s-NewValue:%s",
                        cHeroMenuItemKeyId, cItem.getItemId(), cMenuQueryValue, cMenuValue));

        if (!cMenuQueryValue.equals(cMenuValue)) {
            menu_hero_query_keys[cHeroMenuItemKeyId] = cMenuValue;
            getLoaderManager().restartLoader(0, null, this);
        }

        if (cMenuValue.equals(KEY_MENU_HERO_QUERY_ALL)) {
            cParentItem.setTitle(cMenuItemDefaultTitleId);
        } else if (cMenuValue.equals(KEY_MENU_HERO_QUERY_DEFAULT)) {
            cParentItem.setTitle(cMenuItemDefaultTitleId);
        }
    }

    /**
     * 按英雄定位筛选子菜单单击监听
     */
    private MenuItem.OnMenuItemClickListener mMenuHeroRoleMenuItemClickListener = new MenuItem.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            doMenuHeroMenuItemClickListener(item, menu_hero_role,
                    KEY_MENU_HERO_ROLE,
                    menu_hero_role_values, R.string.menu_hero_role);

            return true;
        }
    };
    /**
     * 按属性类型筛选子菜单单击监听
     */
    private MenuItem.OnMenuItemClickListener mMenuHeroTypeMenuItemClickListener = new MenuItem.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            doMenuHeroMenuItemClickListener(item, menu_hero_type,
                    KEY_MENU_HERO_TYPE,
                    menu_hero_type_values, R.string.menu_hero_type);

            return true;
        }
    };
    /**
     * 按攻击类型筛选子菜单单击监听
     */
    private MenuItem.OnMenuItemClickListener mMenuHeroAttackMenuItemClickListener = new MenuItem.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            doMenuHeroMenuItemClickListener(item, menu_hero_attack,
                    KEY_MENU_HERO_ATTACK,
                    menu_hero_attack_values, R.string.menu_hero_attack);

            return true;
        }
    };
    /**
     * 按阵容筛选子菜单单击监听
     */
    private MenuItem.OnMenuItemClickListener mMenuHeroFactionsMenuItemClickListener = new MenuItem.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            doMenuHeroMenuItemClickListener(item, menu_hero_factions,
                    KEY_MENU_HERO_FACTIONS,
                    menu_hero_factions_values, R.string.menu_hero_factions);

            return true;
        }
    };
    /**
     * 排序-按英雄统计参数子菜单单击监听
     */
    private MenuItem.OnMenuItemClickListener mMenuHeroStatsAllMenuItemClickListener = new MenuItem.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            doMenuHeroMenuItemClickListener(item, menu_hero_statsall,
                    KEY_MENU_HERO_STATSALL,
                    menu_hero_statsall_values, R.string.menu_hero_statsall);

            return true;
        }
    };

    @Override
    public Loader<List<HeroItem>> onCreateLoader(int arg0, Bundle arg1) {
        final SherlockFragmentActivity c = this.getSherlockActivity();
        c.setSupportProgressBarIndeterminateVisibility(true);
        return new HeroListLoader(c, this.menu_hero_query_keys);
    }

    @Override
    public void onLoadFinished(Loader<List<HeroItem>> loader,
            List<HeroItem> data) {
        mAdapter.setData(data);
        this.getSherlockActivity().setSupportProgressBarIndeterminateVisibility(false);
    }

    @Override
    public void onLoaderReset(Loader<List<HeroItem>> arg0) {
        mAdapter.setData(null);
    }

    /**
     * HeroSearchView onQueryTextSubmit
     */
    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    /**
     * HeroSearchView onQueryTextChange
     */
    @Override
    public boolean onQueryTextChange(String newText) {
        if (TextUtils.isEmpty(newText)) {
            mGridView.clearTextFilter();
        } else {
            mGridView.setFilterText(newText);
        }
        return false;
    }

    /**
     * HeroListAdapter item filter accepter
     */
    private final ArrayFilterAccepter<HeroItem> mHeroListFilterAccepter = new ArrayFilterAccepter<HeroItem>() {
        /**
         * 搜索英雄名称 Accepter
         * 
         * @param cDataItem
         * @param constraint
         * @return
         */
        @SuppressLint("DefaultLocale")
        @Override
        public boolean Accept(HeroItem cDataItem, CharSequence constraint) {
            if (cDataItem == null || TextUtils.isEmpty(cDataItem.name_l)) {
                return false;
            }
            if (TextUtils.isEmpty(constraint)) {
                return true;
            }

            final String prefixString = constraint.toString().toLowerCase();
            if (cDataItem.name_l.indexOf(prefixString) > -1) {
                return true;
            }

            if (!TextUtils.isEmpty(cDataItem.name)
                    && cDataItem.name.toLowerCase().startsWith(prefixString)) {
                return true;
            }

            if (cDataItem.nickname_l != null && cDataItem.nickname_l.length > 0
                    && Utils.exists(cDataItem.nickname_l, prefixString, true)) {
                return true;
            }
            return false;
        }
    };

    /**
     * 
     */
    final class HeroListAdapter extends AbstractArrayAdapter<HeroItem> {
        private final class ViewHolder {
            public TextView text;
            public ImageView image;
        }

        private final LayoutInflater mInflater;

        public HeroListAdapter(Context context) {
            super(context);
            mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            setNotifyOnChange(true);
        }

        public void setData(List<HeroItem> data) {
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
                view = mInflater.inflate(R.layout.fragment_herodata_grid_item,
                        parent, false);

                holder = new ViewHolder();
                holder.text = (TextView) view.findViewById(R.id.text_hero_name);
                holder.image = (ImageView) view.findViewById(R.id.image_hero);

                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }

            final HeroItem item = getItem(position);
            ImageLoader.getInstance().displayImage(
                    Utils.getHeroImageUri(item.keyName),
                    holder.image, mImageLoadOptions);
            holder.text.setText(item.name_l);

            return view;
        }
    }

    /**
     * 
     */
    public static class HeroListLoader extends AbstractAsyncTaskLoader<HeroItem> {
        final String[] menu_hero_query_keys;

        public HeroListLoader(Context context, String[] queryKeys) {
            super(context);
            menu_hero_query_keys = queryKeys;
        }

        @Override
        public List<HeroItem> loadInBackground() {
            try {
                cDataList = DataManager.getHeroList(getContext());
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            cDataList = buildHeroQuery(cDataList);
            return cDataList;
        }

        /**
         * 根据菜单选择项对英雄数据进行过滤
         */
        private List<HeroItem> buildHeroQuery(List<HeroItem> mOriginalList) {
            final String[] cQueryKeys = this.menu_hero_query_keys;
            if (mOriginalList == null || mOriginalList.size() <= 0
                    || cQueryKeys == null || cQueryKeys.length <= 0) {
                return mOriginalList;
            }

            final String cQueryKey_role = cQueryKeys[KEY_MENU_HERO_ROLE];
            final String cQueryKey_type = cQueryKeys[KEY_MENU_HERO_TYPE];
            final String cquerykey_attack = cQueryKeys[KEY_MENU_HERO_ATTACK];
            final String cquerykey_factions = cQueryKeys[KEY_MENU_HERO_FACTIONS];
            final String cquerykey_statsall = cQueryKeys[KEY_MENU_HERO_STATSALL];

            if (!cQueryKey_role.equals(KEY_MENU_HERO_QUERY_ALL)
                    || !cQueryKey_type.equals(KEY_MENU_HERO_QUERY_ALL)
                    || !cquerykey_attack.equals(KEY_MENU_HERO_QUERY_ALL)
                    || !cquerykey_factions.equals(KEY_MENU_HERO_QUERY_ALL))
            {
                mOriginalList = (ArrayList<HeroItem>) CollectionUtils.select(
                        mOriginalList,
                        new Predicate<HeroItem>() {
                            @Override
                            public boolean evaluate(HeroItem cObject) {
                                int cQuery = -1; //
                                final String key_all = KEY_MENU_HERO_QUERY_ALL;
                                if (!cQueryKey_role.equals(key_all)) {
                                    cQuery = Utils.exists(cObject.roles, cQueryKey_role) ? 1 : 0;
                                }
                                if (cQuery != 0 && !cquerykey_attack.equals(key_all)) {
                                    cQuery = cObject.atk.equals(cquerykey_attack) ? 1 : 0;
                                }
                                if (cQuery != 0 && !cQueryKey_type.equals(key_all)) {
                                    cQuery = cObject.hp.equals(cQueryKey_type) ? 1 : 0;
                                }
                                if (cQuery != 0 && !cquerykey_factions.equals(key_all)) {
                                    cQuery = cObject.faction.equals(cquerykey_factions) ? 1 : 0;
                                }
                                return cQuery == 1;
                            }
                        });
            }

            if (!cquerykey_statsall.equals(KEY_MENU_HERO_QUERY_DEFAULT)) {
                mOriginalList = (ArrayList<HeroItem>) CollectionUtils.cloneEx(mOriginalList);
                DataManager.sortHeroList(mOriginalList, cquerykey_statsall);
            }

            return mOriginalList;
        }
    }
}
