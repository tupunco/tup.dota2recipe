package tup.dota2recipe;

import java.io.IOException;
import java.util.List;

import org.json.JSONException;

import tup.dota2recipe.adapter.ItemsImagesAdapter;
import tup.dota2recipe.entity.AbilityItem;
import tup.dota2recipe.entity.HeroDetailItem;
import tup.dota2recipe.entity.ItemsItem;
import tup.dota2recipe.util.Utils;
import tup.dota2recipe.view.SimpleGridView;
import tup.dota2recipe.view.SimpleListView;
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
import android.widget.ListAdapter;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
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

            mImageLoadOptions = Utils.createDisplayImageOptions();

            final String hero_keyName = this.getArguments().getString(KEY_HERO_DETAIL_KEY_NAME);
            Log.v(TAG, "arg.hero_keyName=" + hero_keyName);
            if (!TextUtils.isEmpty(hero_keyName)) {
                Utils.executeAsyncTask(mLoaderTask, hero_keyName);
            }
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_herodetail, container, false);
        }

        /**
         * 
         * @param cHeroItem
         */
        private void bindHeroItemView(HeroDetailItem cItem) {
            if (cItem == null) {
                return;
            }

            this.getSherlockActivity().setTitle(cItem.name_l);

            final View v = this.getView();
            final Resources cRes = this.getResources();

            ImageLoader.getInstance().displayImage(Utils.getHeroImageUri(cItem.keyName),
                    ((ImageView) v.findViewById(R.id.image_hero)), mImageLoadOptions);

            ((TextView) v.findViewById(R.id.text_hero_name)).setText(cItem.name);
            ((TextView) v.findViewById(R.id.text_hero_name_l)).setText(cItem.name_l);

            if (cItem.roles_l != null && cItem.roles_l.length > 0) {
                ((TextView) v.findViewById(R.id.text_hero_roles)).setText(TextUtils.join(
                        cRes.getString(R.string.text_division_label), cItem.roles_l));
            } else {
                ((TextView) v.findViewById(R.id.text_hero_roles)).setVisibility(View.GONE);
            }

            ((TextView) v.findViewById(R.id.text_hero_atk)).setText(cItem.atk_l);
            ((TextView) v.findViewById(R.id.text_hero_faction))
                    .setText(Utils.getMenuValue(cRes,
                            R.array.menu_hero_factions_keys,
                            R.array.menu_hero_factions_values, cItem.faction));
            ((TextView) v.findViewById(R.id.text_hero_hp))
                    .setText(Utils.getMenuValue(cRes,
                            R.array.menu_hero_type_keys,
                            R.array.menu_hero_type_values, cItem.hp));

            ((TextView) v.findViewById(R.id.text_hero_stats))
                    .setText(Html.fromHtml(cItem.stats, mImageGetter, null));
            // ((TextView) v.findViewById(R.id.text_hero_detailstats))
            // .setText(Html.fromHtml(cItem.detailstats));
            ((TextView) v.findViewById(R.id.text_hero_bio)).setText(cItem.bio_l);

            // cItem.itembuilds
            bindItembuildsItems(v, cItem, "Starting",
                    R.id.llayout_hero_itembuilds_starting, R.id.grid_hero_itembuilds_starting);
            bindItembuildsItems(v, cItem, "Early",
                    R.id.llayout_hero_itembuilds_early, R.id.grid_hero_itembuilds_early);
            bindItembuildsItems(v, cItem, "Core",
                    R.id.llayout_hero_itembuilds_core, R.id.grid_hero_itembuilds_core);
            bindItembuildsItems(v, cItem, "Luxury",
                    R.id.llayout_hero_itembuilds_luxury, R.id.grid_hero_itembuilds_luxury);

            // abilities
            if (cItem.abilities != null && cItem.abilities.size() > 0) {
                final HeroAbilitiesAdapter adapter = new HeroAbilitiesAdapter(
                        this.getSherlockActivity(), cItem.abilities);

                final SimpleListView list = (SimpleListView) v
                        .findViewById(R.id.list_hero_abilities);
                list.setAdapter(adapter);
                // list.setOnItemClickListener(this);
            }
            else {
                v.findViewById(R.id.llayout_hero_abilities).setVisibility(View.GONE);
            }

        }

        /**
         * 
         * @param key
         * @param layoutResId
         * @param itemsGridResId
         */
        private void bindItembuildsItems(View cView, HeroDetailItem cItem, String cItembuildsKey,
                int layoutResId, int itemsGridResId) {
            if (cItem.itembuilds_i == null || cItem.itembuilds_i.size() <= 0
                    || TextUtils.isEmpty(cItembuildsKey))
                return;

            final List<ItemsItem> cItembuilds = cItem.itembuilds_i.get(cItembuildsKey);
            if (cItembuilds == null || cItembuilds.size() <= 0)
                return;

            final SimpleGridView grid = (SimpleGridView) cView.findViewById(itemsGridResId);
            final ItemsImagesAdapter adapter = new ItemsImagesAdapter(this.getSherlockActivity(),
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
                if (source.contentEquals("Int"))
                    drawable = res.getDrawable(R.drawable.overviewicon_int);
                else if (source.contentEquals("Agi"))
                    drawable = res.getDrawable(R.drawable.overviewicon_agi);
                else if (source.contentEquals("Str"))
                    drawable = res.getDrawable(R.drawable.overviewicon_str);
                else if (source.contentEquals("Attack"))
                    drawable = res.getDrawable(R.drawable.overviewicon_attack);
                else if (source.contentEquals("Speed"))
                    drawable = res.getDrawable(R.drawable.overviewicon_speed);
                else if (source.contentEquals("Defense"))
                    drawable = res.getDrawable(R.drawable.overviewicon_defense);
                else if (source.contentEquals("mana"))
                    drawable = res.getDrawable(R.drawable.mana);
                else if (source.contentEquals("cooldown"))
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
            protected HeroDetailItem doInBackground(String... params) {
                try {
                    return DataManager.getHeroDetailItem(
                            HeroDetailFragment.this.getSherlockActivity(),
                            params[0]);
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

            public HeroAbilitiesAdapter(Context context, List<AbilityItem> abilities) {
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
                            R.layout.fragment_herodetail_abilities_list_item, parent, false);

                    holder = new ViewHolder();
                    holder.affects = (TextView) view.findViewById(R.id.text_abilities_affects);
                    holder.attrib = (TextView) view.findViewById(R.id.text_abilities_attrib);
                    holder.dname = (TextView) view.findViewById(R.id.text_abilities_dname);
                    holder.cmb = (TextView) view.findViewById(R.id.text_abilities_cmb);
                    holder.desc = (TextView) view.findViewById(R.id.text_abilities_desc);
                    holder.dmg = (TextView) view.findViewById(R.id.text_abilities_dmg);
                    holder.lore = (TextView) view.findViewById(R.id.text_abilities_lore);
                    holder.image = (ImageView) view.findViewById(R.id.image_abilities);

                    view.setTag(holder);
                } else
                    holder = (ViewHolder) view.getTag();

                final AbilityItem item = (AbilityItem) getItem(position);
                ImageLoader.getInstance().displayImage(Utils.getAbilitiesImageUri(item.keyName),
                        holder.image, mImageLoadOptions);

                bindHtmlTextView(holder.affects, item.affects);
                bindHtmlTextView(holder.attrib, item.attrib);
                bindHtmlTextView(holder.cmb, item.cmb, mImageGetter);
                bindHtmlTextView(holder.dmg, item.dmg);
                holder.dname.setText(item.dname);
                holder.desc.setText(item.desc);
                holder.lore.setText(item.lore);

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
            private void bindHtmlTextView(TextView text, String fieldValue, ImageGetter cImageGetter) {
                if (!TextUtils.isEmpty(fieldValue)) {
                    text.setText(Html.fromHtml(fieldValue, cImageGetter, null));
                } else {
                    text.setVisibility(View.GONE);
                }
            }
        }

        @Override
        public void onItemClick(ListAdapter parent, View view, int position, long id) {
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
