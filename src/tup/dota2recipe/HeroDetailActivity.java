package tup.dota2recipe;

import java.io.IOException;
import java.util.List;

import org.json2.JSONException;

import tup.dota2recipe.adapter.DBAdapter;
import tup.dota2recipe.adapter.ItemsImagesAdapter;
import tup.dota2recipe.entity.AbilityItem;
import tup.dota2recipe.entity.FavoriteItem;
import tup.dota2recipe.entity.HeroDetailItem;
import tup.dota2recipe.entity.HeroItem;
import tup.dota2recipe.entity.ItemsItem;
import tup.dota2recipe.util.Utils;
import tup.dota2recipe.view.SimpleGridView;
import tup.dota2recipe.view.SimpleListView;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.text.Html;
import android.text.Html.ImageGetter;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TableLayout;
import android.widget.TableRow;
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
 * 英雄详细 Activity
 * 
 * @author tupunco
 */
public class HeroDetailActivity extends SherlockFragmentActivity {
    private static final String TAG = "HeroDetailActivity";
    /**
     * 英雄名称 Intent 参数
     */
    public final static String KEY_HERO_DETAIL_KEY_NAME = "KEY_HERO_DETAIL_KEY_NAME";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Utils.fillFragment(this, HeroDetailFragment.newInstance(
                this.getIntent().getStringExtra(KEY_HERO_DETAIL_KEY_NAME)));
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
     * 英雄详细 Fragment
     */
    public static class HeroDetailFragment extends SherlockFragment
            implements SimpleGridView.OnItemClickListener {
        private DisplayImageOptions mImageLoadOptions;
        private HeroDetailItem mHeroDetailItem;

        /**
         * 
         * @param hero_keyName
         * @return
         */
        static HeroDetailFragment newInstance(String hero_keyName) {
            final HeroDetailFragment f = new HeroDetailFragment();
            final Bundle b = new Bundle();
            b.putString(KEY_HERO_DETAIL_KEY_NAME, hero_keyName);
            f.setArguments(b);
            return f;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            setHasOptionsMenu(true);
            mImageLoadOptions = Utils.createDisplayImageOptions();

            final String hero_keyName = this.getArguments().getString(
                    KEY_HERO_DETAIL_KEY_NAME);
            Log.v(TAG, "arg.hero_keyName=" + hero_keyName);
            if (!TextUtils.isEmpty(hero_keyName)) {
                Utils.executeAsyncTask(mLoaderTask, hero_keyName);
            }
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_herodetail, container,
                    false);
        }

        @Override
        public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
            super.onCreateOptionsMenu(menu, inflater);

            inflater.inflate(R.menu.fragment_herodetail, menu);
        }

        @Override
        public void onPrepareOptionsMenu(Menu menu) {
            super.onPrepareOptionsMenu(menu);

            // ----加收藏按钮---
            if (mHeroDetailItem == null) {
                return;
            }
            final MenuItem check =
                    menu.findItem(R.id.menu_check_addcollection);
            if (check == null) {
                return;
            }

            check.setChecked(mHeroDetailItem.hasFavorite == 1);
            Utils.configureStarredMenuItem(check);
            check.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                @SuppressLint("NewApi")
                public boolean onMenuItemClick(MenuItem item) {
                    final boolean isChecked = !item.isChecked();
                    final HeroDetailItem hero = mHeroDetailItem;
                    item.setChecked(isChecked);

                    Utils.configureStarredMenuItem(item);
                    hero.hasFavorite = isChecked ? 1 : 0;
                    if (isChecked) {
                        final FavoriteItem c = new FavoriteItem();
                        c.keyName = hero.keyName;
                        c.type = FavoriteItem.KEY_TYPE_HERO;
                        DBAdapter.getInstance().addFavorite(c);
                    } else {
                        DBAdapter.getInstance().deleteFavorite(hero.keyName);
                    }
                    return true;
                }
            });
        }

        /**
         * 绑定视图
         * 
         * @param cHeroItem
         */
        @SuppressLint("NewApi")
        private void bindHeroItemView(final HeroDetailItem cItem) {
            if (cItem == null) {
                return;
            }

            mHeroDetailItem = cItem;
            final SherlockFragmentActivity cContext = this.getSherlockActivity();
            cContext.invalidateOptionsMenu();
            cContext.setTitle(cItem.name_l);

            final View v = this.getView();
            bindHeroItemSimpleView(v, cItem, mImageLoadOptions);

            // cItem.stats
            bindStatsView(v, cItem);
            // cItem.detailstats
            bindDetailstatsView(v, cItem);

            ((TextView) v.findViewById(R.id.text_hero_bio)).setText(cItem.bio_l);

            // cItem.itembuilds
            bindItembuildsItems(v, cItem, "Starting",
                    R.id.llayout_hero_itembuilds_starting,
                    R.id.grid_hero_itembuilds_starting);
            bindItembuildsItems(v, cItem, "Early",
                    R.id.llayout_hero_itembuilds_early,
                    R.id.grid_hero_itembuilds_early);
            bindItembuildsItems(v, cItem, "Core",
                    R.id.llayout_hero_itembuilds_core,
                    R.id.grid_hero_itembuilds_core);
            bindItembuildsItems(v, cItem, "Luxury",
                    R.id.llayout_hero_itembuilds_luxury,
                    R.id.grid_hero_itembuilds_luxury);

            // abilities
            if (cItem.abilities != null && cItem.abilities.size() > 0) {
                final HeroAbilitiesAdapter adapter = new HeroAbilitiesAdapter(
                        cContext, cItem.abilities);

                final SimpleListView list = (SimpleListView) v
                        .findViewById(R.id.list_hero_abilities);
                list.setAdapter(adapter);
                // list.setOnItemClickListener(this);
            }
            else {
                v.findViewById(R.id.llayout_hero_abilities)
                        .setVisibility(View.GONE);
            }
        }

        /**
         * 绑定视图-英雄简单数据信息
         * 
         * @param v
         * @param cItem
         * @param cImageLoadOptions
         */
        public static void bindHeroItemSimpleView(final View v, final HeroItem cItem,
                final DisplayImageOptions cImageLoadOptions) {
            if (v == null || cItem == null || cImageLoadOptions == null) {
                return;
            }

            final Resources cRes = v.getResources();
            ImageLoader.getInstance().displayImage(
                    Utils.getHeroImageUri(cItem.keyName),
                    ((ImageView) v.findViewById(R.id.image_hero)),
                    cImageLoadOptions);

            final String division = cRes.getString(R.string.text_division_label);
            final String name = (cItem.nickname_l != null && cItem.nickname_l.length > 0)
                    ? TextUtils.join(division, cItem.nickname_l) : cItem.name;
            ((TextView) v.findViewById(R.id.text_hero_name)).setText(name);
            ((TextView) v.findViewById(R.id.text_hero_name_l)).setText(cItem.name_l);

            if (cItem.roles_l != null && cItem.roles_l.length > 0) {
                ((TextView) v.findViewById(R.id.text_hero_roles))
                        .setText(TextUtils.join(division, cItem.roles_l));
            } else {
                ((TextView) v.findViewById(R.id.text_hero_roles)).setVisibility(View.GONE);
            }

            final String hp_faction_atk = String.format("%s/%s/%s",
                    Utils.getMenuValue(cRes, R.array.menu_hero_type_keys,
                            R.array.menu_hero_type_values, cItem.hp),
                    Utils.getMenuValue(cRes, R.array.menu_hero_factions_keys,
                            R.array.menu_hero_factions_values, cItem.faction),
                    cItem.atk_l);
            ((TextView) v.findViewById(R.id.text_hero_hp_faction_atk)).setText(hp_faction_atk);
        }

        /**
         * 绑定视图-统计信息
         * 
         * @param cView
         * @param cItem
         */
        private void bindStatsView(View cView, HeroDetailItem cItem) {
            if (cItem == null || cItem.stats1 == null
                    || cItem.stats1.size() != 6) {
                return;
            }

            final LinearLayout layoutStats1 = (LinearLayout) cView
                    .findViewById(R.id.layout_hero_stats1);
            final LinearLayout layoutStats2 = (LinearLayout) cView
                    .findViewById(R.id.layout_hero_stats2);
            if (layoutStats1 == null || layoutStats2 == null) {
                return;
            }

            final Context context = cView.getContext();
            final Resources res = context.getResources();
            final String[] labels = res.getStringArray(R.array.array_hero_stats);
            final int[] resIds = new int[] {
                    R.drawable.overviewicon_int, R.drawable.overviewicon_agi,
                    R.drawable.overviewicon_str, R.drawable.overviewicon_attack,
                    R.drawable.overviewicon_speed, R.drawable.overviewicon_defense
            };

            final LayoutInflater inflater = LayoutInflater.from(context);
            final int hpIndex = (cItem.hp.equals("intelligence") ? 0
                    : (cItem.hp.equals("agility") ? 1 : 2));
            ViewGroup cParent = layoutStats1;
            View view = null;
            TextView text = null;
            ImageView image = null;
            for (int i = 0; i < cItem.stats1.size(); i++) {
                cParent = (i <= 2 ? layoutStats1 : layoutStats2);
                view = inflater.inflate(
                        R.layout.fragment_herodetail_stats_list_item, cParent,
                        false);

                text = (TextView) view.findViewById(R.id.text_hero_stats_label);
                text.setText(labels[i]);

                image = (ImageView) view.findViewById(R.id.image_hero_stats_icon);
                image.setImageResource(resIds[i]);

                text = (TextView) view.findViewById(R.id.text_hero_stats_value);
                text.setText(cItem.stats1.get(i)[2]);
                if (hpIndex == i) {
                    image = (ImageView) view.findViewById(R.id.image_hero_stats_icon_primary);
                    image.setVisibility(View.VISIBLE);
                }
                cParent.addView(view);
            }
        }

        /**
         * 绑定视图-详细统计信息
         * 
         * @param cView
         * @param cItem
         */
        @SuppressWarnings("deprecation")
        private void bindDetailstatsView(View cView, HeroDetailItem cItem) {
            if (cItem == null || cItem.detailstats1 == null
                    || cItem.detailstats1.size() != 5
                    || cItem.detailstats2 == null
                    || cItem.detailstats2.size() != 3) {
                return;
            }

            final TableLayout table = (TableLayout) cView.findViewById(R.id.table_hero_detailstats);
            if (table == null) {
                return;
            }

            final Context context = cView.getContext();
            final TableRow.LayoutParams rowLayout = new TableRow.LayoutParams();
            rowLayout.weight = 1f;
            final TableLayout.LayoutParams tableLayout = new TableLayout.LayoutParams();
            final String[] detailstatsLabel = context.getResources()
                    .getStringArray(R.array.array_hero_detailstats);
            final Drawable rowBg = context.getResources()
                    .getDrawable(R.drawable.hero_detailstats_table_bg);
            int count = cItem.detailstats1.size();
            int iCount = 0;
            String[] iItem = null;
            TableRow row = null;
            TextView text = null;
            // --detailstats1
            for (int i = 0; i < count; i++) {
                row = new TableRow(context);
                iItem = cItem.detailstats1.get(i);
                iCount = iItem.length;
                for (int ii = 0; ii < iCount; ii++) {
                    text = new TextView(context);
                    text.setPadding(0, 3, 0, 3);
                    if (i <= 0) {
                        text.setText(ii == 0 ? detailstatsLabel[i] : iItem[ii]);
                    } else { // INFO:源数据颠倒
                        text.setText(ii == 0 ? detailstatsLabel[i] : iItem[iCount - ii]);
                    }
                    row.addView(text, rowLayout);
                }
                if (i % 2 == 1) {
                    row.setBackgroundDrawable(rowBg);
                }
                table.addView(row, tableLayout);
            }
            // --detailstats2
            count = cItem.detailstats2.size();
            for (int i = 0; i < count; i++) {
                row = new TableRow(context);
                iItem = cItem.detailstats2.get(i);
                iCount = iItem.length;
                for (int ii = 0; ii < iCount; ii++) {
                    text = new TextView(context);
                    text.setPadding(0, 3, 0, 3);
                    text.setText(ii == 0 ? detailstatsLabel[i + 5] : iItem[ii]);
                    row.addView(text, rowLayout);
                }
                if (i % 2 == 0) {
                    row.setBackgroundDrawable(rowBg);
                }
                table.addView(row, tableLayout);
            }
        }

        /**
         * 绑定视图-推荐出装
         * 
         * @param key
         * @param layoutResId
         * @param itemsGridResId
         */
        private void bindItembuildsItems(View cView, HeroDetailItem cItem,
                String cItembuildsKey,
                int layoutResId, int itemsGridResId) {
            if (cItem.itembuilds_i == null || cItem.itembuilds_i.size() <= 0
                    || TextUtils.isEmpty(cItembuildsKey))
                return;

            final List<ItemsItem> cItembuilds = cItem.itembuilds_i
                    .get(cItembuildsKey);
            if (cItembuilds == null || cItembuilds.size() <= 0)
                return;

            final SimpleGridView grid = (SimpleGridView) cView
                    .findViewById(itemsGridResId);
            final ItemsImagesAdapter adapter = new ItemsImagesAdapter(
                    this.getSherlockActivity(),
                    mImageLoadOptions, cItembuilds);
            grid.setAdapter(adapter);
            grid.setOnItemClickListener(this);

            cView.findViewById(layoutResId).setVisibility(View.VISIBLE);
        }

        /**
         * 
         * 
         */
        private final ImageGetter mImageGetter = new ImageGetter() {
            @Override
            public Drawable getDrawable(String source) {
                final Resources res = getSherlockActivity().getResources();
                Drawable drawable = null;
                if (source.equals("mana"))
                    drawable = res.getDrawable(R.drawable.mana);
                else if (source.equals("cooldown"))
                    drawable = res.getDrawable(R.drawable.cooldown);

                if (drawable != null) {
                    drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
                            drawable.getIntrinsicHeight());
                    return drawable;
                } else {
                    return null;
                }
            }
        };

        /**
         * 
         */
        private final AsyncTask<String, Void, HeroDetailItem> mLoaderTask = new AsyncTask<String, Void, HeroDetailItem>() {
            @Override
            protected void onPreExecute() {
                HeroDetailFragment.this.getSherlockActivity()
                        .setSupportProgressBarIndeterminateVisibility(true);

                super.onPreExecute();
            }

            @Override
            protected void onCancelled() {
                super.onCancelled();

                HeroDetailFragment.this.getSherlockActivity()
                        .setSupportProgressBarIndeterminateVisibility(false);
            }

            @Override
            protected HeroDetailItem doInBackground(String... params) {
                try {
                    final HeroDetailItem date = DataManager.getHeroDetailItem(
                            HeroDetailFragment.this.getSherlockActivity(),
                            params[0]);
                    if (date != null && date.hasFavorite < 0) {
                        final boolean has = DBAdapter.getInstance()
                                .hasFavorite(params[0]);
                        date.hasFavorite = has ? 1 : 0;
                    }
                    return date;
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(HeroDetailItem result) {
                super.onPostExecute(result);

                HeroDetailFragment.this.bindHeroItemView(result);
                HeroDetailFragment.this.getSherlockActivity()
                        .setSupportProgressBarIndeterminateVisibility(false);
            }
        };

        /**
         * 技能 List Adapter
         */
        private final class HeroAbilitiesAdapter extends BaseAdapter {
            private final class ViewHolder {
                public ImageView image;
                public TextView dname;
                public TextView affects;
                public TextView attrib;
                public TextView desc;
                public TextView cmb;
                public TextView dmg;
                public TextView lore;
            }

            private final LayoutInflater mInflater;
            private final List<AbilityItem> mAbilities;

            public HeroAbilitiesAdapter(Context context,
                    List<AbilityItem> abilities) {
                super();

                mInflater = (LayoutInflater) context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                mAbilities = abilities;
            }

            @Override
            public int getCount() {
                return mAbilities.size();
            }

            @Override
            public Object getItem(int position) {
                return mAbilities.get(position);
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = convertView;

                final ViewHolder holder;
                if (convertView == null) {
                    view = mInflater.inflate(
                            R.layout.fragment_herodetail_abilities_list_item,
                            parent, false);

                    holder = new ViewHolder();
                    holder.affects = (TextView) view
                            .findViewById(R.id.text_abilities_affects);
                    holder.attrib = (TextView) view
                            .findViewById(R.id.text_abilities_attrib);
                    holder.dname = (TextView) view
                            .findViewById(R.id.text_abilities_dname);
                    holder.cmb = (TextView) view
                            .findViewById(R.id.text_abilities_cmb);
                    holder.desc = (TextView) view
                            .findViewById(R.id.text_abilities_desc);
                    holder.dmg = (TextView) view
                            .findViewById(R.id.text_abilities_dmg);
                    holder.lore = (TextView) view
                            .findViewById(R.id.text_abilities_lore);
                    holder.image = (ImageView) view
                            .findViewById(R.id.image_abilities);

                    view.setTag(holder);
                } else
                    holder = (ViewHolder) view.getTag();

                final AbilityItem item = (AbilityItem) getItem(position);
                ImageLoader.getInstance().displayImage(
                        Utils.getAbilitiesImageUri(item.keyName),
                        holder.image, mImageLoadOptions);

                holder.dname.setText(item.dname);
                bindHtmlTextView(holder.affects, item.affects);
                bindHtmlTextView(holder.attrib, item.attrib);
                bindHtmlTextView(holder.cmb, item.cmb, mImageGetter);
                bindHtmlTextView(holder.dmg, item.dmg);
                bindHtmlTextView(holder.desc, item.desc);
                bindHtmlTextView(holder.lore, item.lore);

                return view;
            }

            /**
             * 
             * @param text
             * @param fieldValue
             */
            private void bindHtmlTextView(TextView text, String fieldValue) {
                bindHtmlTextView(text, fieldValue, null);
            }

            /**
             * 
             * @param text
             * @param fieldValue
             * @param cImageGetter
             */
            private void bindHtmlTextView(TextView text, String fieldValue,
                    ImageGetter cImageGetter) {
                if (!TextUtils.isEmpty(fieldValue)) {
                    text.setText(Html.fromHtml(fieldValue, cImageGetter, null));
                } else {
                    text.setVisibility(View.GONE);
                }
            }
        }

        @Override
        public void onItemClick(ListAdapter parent, View view, int position,
                long id) {
            // Utils.startHeroDetailActivity(this.getSherlockActivity(),
            // (HeroDetailItem) parent.getItemAtPosition(position));
            final Object cItem = parent.getItem(position);
            if (cItem instanceof ItemsItem) {
                Utils.startItemsDetailActivity(this.getSherlockActivity(),
                        (ItemsItem) cItem);
            }
        };
    }
}
