/**
 * 
 */
package tup.dota2recipe;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.json2.JSONArray;
import org.json2.JSONException;
import org.json2.JSONObject;

import tup.dota2recipe.entity.AbilityItem;
import tup.dota2recipe.entity.HeroDetailItem;
import tup.dota2recipe.entity.HeroItem;
import tup.dota2recipe.entity.HeroSkillupItem;
import tup.dota2recipe.entity.HeroStatsItem;
import tup.dota2recipe.entity.ItemsItem;
import tup.dota2recipe.entity.StatsallBaseComparator;
import android.content.Context;
import android.support.v4.util.LruCache;
import android.text.TextUtils;
import android.util.Log;

/**
 * 英雄物品数据访问类
 * 
 * @author tupunco
 */
public final class DataManager {
    /**
     * 合成卷轴物品 keyname
     */
    public final static String KEY_NAME_RECIPE_ITEMS_KEYNAME = "recipe";
    /**
     * 英雄列表数据 JSON 保存文件
     */
    private final static String KEY_FILE_JOSN_HEROLIST = "herolist.json";
    /**
     * 物品列表数据 JSON 保存文件
     */
    private final static String KEY_FILE_JSON_ITEMSLIST = "itemslist.json";
    /**
     * 英雄详细数据 JSON 保存文件
     */
    private final static String KEY_FILE_JSON_HERODETAIL_FROMART = "hero_detail/hero-%s.json";

    private final static List<HeroItem> mHeroList = new ArrayList<HeroItem>();
    private final static Map<String, HeroItem> mHeroMap = new HashMap<String, HeroItem>();
    private final static LruCache<String, HeroDetailItem> mHeroDetailCache =
            new LruCache<String, HeroDetailItem>(50);
    private final static List<ItemsItem> mItemsList = new ArrayList<ItemsItem>();
    private final static Map<String, ItemsItem> mItemsMap = new HashMap<String, ItemsItem>();

    /**
     * HeroItem Name 排序 Comparator
     */
    private static final Comparator<HeroItem> mHeroItemDefaultNameComparator = new Comparator<HeroItem>() {
        private final Collator sCollator = Collator.getInstance();

        @Override
        public int compare(HeroItem object1, HeroItem object2) {
            return sCollator.compare(object1.name_l, object2.name_l);
        }
    };

    /**
     * ItemsItem Name 排序 Comparator
     */
    private static final Comparator<ItemsItem> mItemsItemDefaultNameComparator = new Comparator<ItemsItem>() {
        private final Collator sCollator = Collator.getInstance();

        @Override
        public int compare(ItemsItem object1, ItemsItem object2) {
            return sCollator.compare(object1.dname_l, object2.dname_l);
        }
    };

    /**
     * HeroItem statsall.* 排序 Comparator Map
     */
    private static final HashMap<String, Comparator<HeroItem>> mHeroItemStatsallComparatorMap = new
            HashMap<String, Comparator<HeroItem>>();

    static {
        mHeroItemStatsallComparatorMap.put("default", mHeroItemDefaultNameComparator);
        mHeroItemStatsallComparatorMap.put("init_str", new StatsallBaseComparator() {
            @Override
            protected int compare(HeroStatsItem stat1, HeroStatsItem stat2) {
                return Double.compare(stat1.init_str, stat2.init_str);
            }
        });
        mHeroItemStatsallComparatorMap.put("lv_str", new StatsallBaseComparator() {
            @Override
            protected int compare(HeroStatsItem stat1, HeroStatsItem stat2) {
                return Double.compare(stat1.lv_str, stat2.lv_str);
            }
        });
        mHeroItemStatsallComparatorMap.put("init_agi", new StatsallBaseComparator() {
            @Override
            protected int compare(HeroStatsItem stat1, HeroStatsItem stat2) {
                return Double.compare(stat1.init_agi, stat2.init_agi);
            }
        });
        mHeroItemStatsallComparatorMap.put("lv_agi", new StatsallBaseComparator() {
            @Override
            protected int compare(HeroStatsItem stat1, HeroStatsItem stat2) {
                return Double.compare(stat1.lv_agi, stat2.lv_agi);
            }
        });
        mHeroItemStatsallComparatorMap.put("init_int", new StatsallBaseComparator() {
            @Override
            protected int compare(HeroStatsItem stat1, HeroStatsItem stat2) {
                return Double.compare(stat1.init_int, stat2.init_int);
            }
        });
        mHeroItemStatsallComparatorMap.put("lv_int", new StatsallBaseComparator() {
            @Override
            protected int compare(HeroStatsItem stat1, HeroStatsItem stat2) {
                return Double.compare(stat1.lv_int, stat2.lv_int);
            }
        });
        mHeroItemStatsallComparatorMap.put("init_hp", new StatsallBaseComparator() {
            @Override
            protected int compare(HeroStatsItem stat1, HeroStatsItem stat2) {
                return Double.compare(stat1.init_hp, stat2.init_hp);
            }
        });
        mHeroItemStatsallComparatorMap.put("init_mp", new StatsallBaseComparator() {
            @Override
            protected int compare(HeroStatsItem stat1, HeroStatsItem stat2) {
                return Double.compare(stat1.init_mp, stat2.init_mp);
            }
        });
        mHeroItemStatsallComparatorMap.put("init_armor", new StatsallBaseComparator() {
            @Override
            protected int compare(HeroStatsItem stat1, HeroStatsItem stat2) {
                return Double.compare(stat1.init_armor, stat2.init_armor);
            }
        });
        mHeroItemStatsallComparatorMap.put("init_min_dmg", new StatsallBaseComparator() {
            @Override
            protected int compare(HeroStatsItem stat1, HeroStatsItem stat2) {
                return Double.compare(stat1.init_min_dmg, stat2.init_min_dmg);
            }
        });
        mHeroItemStatsallComparatorMap.put("init_max_dmg", new StatsallBaseComparator() {
            @Override
            protected int compare(HeroStatsItem stat1, HeroStatsItem stat2) {
                return Double.compare(stat1.init_max_dmg, stat2.init_max_dmg);
            }
        });
        mHeroItemStatsallComparatorMap.put("lv_dmg", new StatsallBaseComparator() {
            @Override
            protected int compare(HeroStatsItem stat1, HeroStatsItem stat2) {
                return Double.compare(stat1.lv_dmg, stat2.lv_dmg);
            }
        });
        mHeroItemStatsallComparatorMap.put("init_ms", new StatsallBaseComparator() {
            @Override
            protected int compare(HeroStatsItem stat1, HeroStatsItem stat2) {
                return Double.compare(stat1.init_ms, stat2.init_ms);
            }
        });
    }

    /**
     * 获取英雄列表数据
     * 
     * @param cContext
     * @param comparatorType
     * 
     * @return
     * @throws JSONException
     * @throws IOException
     */
    public synchronized static List<HeroItem> getHeroList(Context cContext)
            throws JSONException, IOException {
        tryLoadHeroData(cContext);
        return mHeroList;
    }

    /**
     * 英雄列表排序
     * 
     * @param heroList
     * @param comparatorType
     */
    public synchronized static void sortHeroList(List<HeroItem> heroList, String comparatorType) {
        if (heroList == null || TextUtils.isEmpty(comparatorType))
            return;

        Comparator<HeroItem> cComparator = null;
        if ((cComparator = mHeroItemStatsallComparatorMap.get(comparatorType)) == null) {
            cComparator = mHeroItemDefaultNameComparator;
        }
        Collections.sort(heroList, cComparator);
    }

    /**
     * 获取物品列表数据
     * 
     * @param cContext
     * @return
     * @throws JSONException
     * @throws IOException
     */
    public synchronized static List<ItemsItem> getItemsList(Context cContext)
            throws JSONException, IOException {
        tryLoadItemsData(cContext);
        return (List<ItemsItem>) CollectionUtils.select(mItemsList,
                new Predicate<ItemsItem>() {
                    @Override
                    public boolean evaluate(ItemsItem cObject) {
                        return cObject.ispublic;
                    }
                });
    }

    /**
     * 获取指定英雄数据
     * 
     * @param cContext
     * @param keyName
     * @return
     * @throws IOException
     * @throws JSONException
     */
    public synchronized static HeroItem getHeroItem(
            Context cContext, String keyName) throws IOException, JSONException {
        tryLoadHeroData(cContext);

        if (TextUtils.isEmpty(keyName))
            return null;

        return mHeroMap.get(keyName);
    }

    /**
     * 获取指定英雄详细数据
     * 
     * @param cContext
     * @param keyName
     * @return
     * @throws IOException
     * @throws JSONException
     */
    public synchronized static HeroDetailItem getHeroDetailItem(
            Context cContext, String keyName) throws IOException, JSONException {
        tryLoadHeroDetailItem(cContext, keyName);
        return mHeroDetailCache.get(keyName);
    }

    /**
     * 获取指定物品数据
     * 
     * @param cContext
     * @param keyName
     * @return
     * @throws IOException
     * @throws JSONException
     */
    public synchronized static ItemsItem getItemsItem(Context cContext,
            String keyName) throws IOException, JSONException {
        tryLoadItemsData(cContext);

        if (TextUtils.isEmpty(keyName))
            return null;

        return mItemsMap.get(keyName);
    }

    /**
     * 加载英雄列表数据
     * 
     * @param cContext
     * @throws IOException
     * @throws JSONException
     */
    private static void tryLoadHeroData(Context cContext)
            throws IOException, JSONException {
        if (mHeroList.size() > 0)
            return;

        final JSONObject json = loadJsonObjectFromAssets(cContext, KEY_FILE_JOSN_HEROLIST);
        final Iterator<String> keyIterator = json.keys();
        HeroItem cItem = null;
        String cKeyName = null;
        while (keyIterator.hasNext()) {
            cKeyName = keyIterator.next();
            cItem = extractHeroItem(cKeyName, json.getJSONObject(cKeyName), null);
            if (cItem != null) {
                mHeroList.add(cItem);
                mHeroMap.put(cItem.keyName, cItem);
            }
        }

        Collections.sort(mHeroList, mHeroItemDefaultNameComparator);
    }

    /**
     * 加载英雄详细数据
     * 
     * @param cContext
     * @param keyName
     */
    private static void tryLoadHeroDetailItem(Context cContext, String keyName)
            throws IOException, JSONException {
        if (TextUtils.isEmpty(keyName) || mHeroDetailCache.get(keyName) != null)
            return;

        final String path = String.format(KEY_FILE_JSON_HERODETAIL_FROMART, keyName);
        final JSONObject json = loadJsonObjectFromAssets(cContext, path);

        if (json == null || json.length() <= 0)
            return;

        final HeroDetailItem cItem = new HeroDetailItem();
        extractHeroItem(keyName, json, cItem);
        extractHeroDetailItem(json, cItem);
        mHeroDetailCache.put(keyName, cItem);
    }

    /**
     * 加载物品列表数据
     * 
     * @param cContext
     * @throws IOException
     * @throws JSONException
     */
    private static void tryLoadItemsData(Context cContext)
            throws IOException, JSONException {
        if (mItemsList.size() > 0)
            return;

        final JSONObject json = loadJsonObjectFromAssets(cContext, KEY_FILE_JSON_ITEMSLIST);

        final Iterator<String> keyIterator = json.keys();
        ItemsItem cItem = null;
        String cKeyName = null;
        while (keyIterator.hasNext()) {
            cKeyName = keyIterator.next();
            cItem = extractItemsItem(cKeyName, json.getJSONObject(cKeyName));
            if (cItem != null) {
                mItemsList.add(cItem);
                mItemsMap.put(cItem.keyName, cItem);
            }
        }
        // ------------fill components
        if (mItemsList != null && mItemsList.size() > 0) {
            for (ItemsItem ccItem : mItemsList) {
                ccItem.components_i = fillItemsInfo(cContext, ccItem, ccItem.components, true);
                ccItem.tocomponents_i = fillItemsInfo(ccItem.tocomponents);
                ccItem.toheros_i = fillItemsToHeroInfo(cContext, ccItem.toheros);
            }
        }

        Collections.sort(mItemsList, mItemsItemDefaultNameComparator);
    }

    /**
     * 
     * @return
     * @throws JSONException
     * @throws IOException
     */
    private static List<HeroItem> fillItemsToHeroInfo(Context cContext, String[] cToHeros)
            throws IOException, JSONException {
        if (cContext == null || cToHeros == null || cToHeros.length <= 0)
            return null;

        tryLoadHeroData(cContext);

        final List<HeroItem> outList = new ArrayList<HeroItem>(cToHeros.length);
        HeroItem tHeroItem = null;
        for (String ccKeyName : cToHeros) {
            tHeroItem = mHeroMap.get(ccKeyName);
            if (tHeroItem != null) {
                outList.add(tHeroItem);
            } else {
                Log.e("DataManager", "-fillItemsToHeroInfo-----NULL-" + ccKeyName);
            }
        }
        return outList;
    }

    /**
     * 填充物品详细信息
     * 
     * @param cFillItems
     * @return
     */
    private static List<ItemsItem> fillItemsInfo(String[] cFillItems) {
        return fillItemsInfo(null, null, cFillItems, false);
    }

    /**
     * 填充物品详细信息
     * 
     * @param cItem
     * @param cFillItems
     * @param calcRecipe
     *            是否计算合成卷轴
     * @return
     */
    private static List<ItemsItem> fillItemsInfo(Context cContext,
            ItemsItem cItem, String[] cFillItems, boolean calcRecipe) {
        if (cFillItems == null || cFillItems.length <= 0)
            return null;

        // TODO----------------
        // recipe_necronomicon:死灵书 卷轴
        // recipe_dagon:达贡之神力 卷轴
        final List<ItemsItem> outList = new ArrayList<ItemsItem>(cFillItems.length);
        ItemsItem tItems = null;
        int totalCost = 0;
        for (String ccKeyName : cFillItems) {
            tItems = mItemsMap.get(ccKeyName);
            if (tItems != null) {
                outList.add(tItems);
                totalCost += tItems.cost;
            } else {
                Log.e("DataManager", "-fillItemsInfo-----NULL-" + ccKeyName);
            }
        }

        // 计算合成卷轴
        if (cContext != null && cItem != null && calcRecipe
                && cItem.cost > totalCost) {
            final ItemsItem recipeItems = new ItemsItem();
            recipeItems.cost = cItem.cost - totalCost;
            recipeItems.isrecipe = true;
            recipeItems.keyName = recipeItems.dname = KEY_NAME_RECIPE_ITEMS_KEYNAME;
            recipeItems.dname_l = cContext.getResources()
                    .getString(R.string.text_items_repice_name);
            recipeItems.parent_keyName = cItem.keyName;
            outList.add(recipeItems);
        }
        return outList;
    }

    /**
     * 反序列化 JSON HeroItem(英雄) 项
     * 
     * @param cHeroKey
     * @param cJsonObj
     * @param inItem
     * @return
     */
    private static HeroItem extractHeroItem(String cHeroKey,
            JSONObject cJsonObj, HeroItem inItem) {
        if (cJsonObj == null || TextUtils.isEmpty(cHeroKey))
            return null;

        final HeroItem cItem = inItem == null ? (new HeroItem()) : inItem;
        cItem.keyName = cHeroKey;
        cItem.hp = cJsonObj.optString("hp");
        cItem.faction = cJsonObj.optString("faction");

        cItem.name = cJsonObj.optString("name");
        cItem.name_l = cJsonObj.optString("name_l");

        cItem.atk = cJsonObj.optString("atk");
        cItem.atk_l = cJsonObj.optString("atk_l");

        cItem.roles = toStringArray(cJsonObj.optJSONArray("roles"));
        cItem.roles_l = toStringArray(cJsonObj.optJSONArray("roles_l"));

        cItem.nickname_l = toStringArray(cJsonObj.optJSONArray("nickname_l"));
        extractHeroStatsItem(cJsonObj.optJSONObject("statsall"), cItem);
        return cItem;
    }

    /**
     * 反序列化 JSON HeroItem.statsall 项
     * 
     * @return
     */
    private static void extractHeroStatsItem(JSONObject cJsonObj, HeroItem cItem) {
        if (cJsonObj == null || cItem == null)
            return;

        final HeroStatsItem s = new HeroStatsItem();
        s.lv_str = cJsonObj.optDouble("lv_str");
        s.init_str = cJsonObj.optDouble("init_str");
        s.lv_agi = cJsonObj.optDouble("lv_agi");
        s.init_agi = cJsonObj.optDouble("init_agi");
        s.lv_int = cJsonObj.optDouble("lv_int");
        s.init_int = cJsonObj.optDouble("init_int");

        s.lv_dmg = cJsonObj.optDouble("lv_dmg");
        s.init_max_dmg = cJsonObj.optDouble("init_max_dmg");
        s.init_min_dmg = cJsonObj.optDouble("init_min_dmg");

        s.lv_hp = cJsonObj.optDouble("lv_hp");
        s.init_hp = cJsonObj.optDouble("init_hp");
        s.lv_mp = cJsonObj.optDouble("lv_mp");
        s.init_mp = cJsonObj.optDouble("init_mp");

        s.init_armor = cJsonObj.optDouble("init_armor");
        s.lv_armor = cJsonObj.optDouble("lv_armor");
        s.init_ms = cJsonObj.optDouble("init_ms");
        cItem.statsall = s;
    }

    /**
     * 反序列化 JSON HeroDetailItem(英雄详细) 项
     * 
     * @param cJsonObj
     * @param inItem
     */
    private static void extractHeroDetailItem(JSONObject cJsonObj,
            HeroDetailItem inItem) {
        if (cJsonObj == null || inItem == null)
            return;

        inItem.bio = cJsonObj.optString("bio");
        inItem.bio_l = cJsonObj.optString("bio_l");
        inItem.stats = cJsonObj.optString("stats");
        inItem.stats1 = toStringArray2(cJsonObj.optJSONArray("stats1"));
        inItem.detailstats = cJsonObj.optString("detailstats");
        inItem.detailstats1 = toStringArray2(cJsonObj.optJSONArray("detailstats1"));
        inItem.detailstats1.add(0, new String[] { "Level", "1", "15", "25" });
        inItem.detailstats2 = toStringArray2(cJsonObj.optJSONArray("detailstats2"));

        // ---------itembuilds------------
        inItem.itembuilds = toStringArray(cJsonObj.optJSONObject("itembuilds"));
        if (inItem.itembuilds != null && inItem.itembuilds.size() > 0) {
            inItem.itembuilds_i = new HashMap<String, List<ItemsItem>>();
            for (String cItembuildsKey : inItem.itembuilds.keySet()) {
                inItem.itembuilds_i.put(cItembuildsKey,
                        fillItemsInfo(inItem.itembuilds.get(cItembuildsKey)));
            }
        }

        // ---------abilities------------
        JSONArray cJsonArray = cJsonObj.optJSONArray("abilities");
        if (cJsonArray != null && cJsonArray.length() > 0) {
            final int len = cJsonArray.length();
            final List<AbilityItem> outList = new ArrayList<AbilityItem>(len);
            AbilityItem cAbilityItem = null;
            for (int index = 0; index < len; index++) {
                cAbilityItem = extractHeroAbilityItem(cJsonArray.optJSONObject(index));
                if (cAbilityItem != null)
                    outList.add(cAbilityItem);
            }
            inItem.abilities = outList;
        }

        // ---------skillup------------
        cJsonArray = cJsonObj.optJSONArray("skillup");
        if (cJsonArray != null && cJsonArray.length() > 0) {
            final int len = cJsonArray.length();
            final List<HeroSkillupItem> outList = new ArrayList<HeroSkillupItem>(len);
            HeroSkillupItem cSkillupItem = null;
            for (int index = 0; index < len; index++) {
                cSkillupItem = extractHeroSkillupItem(cJsonArray.optJSONObject(index));
                if (cSkillupItem != null)
                    outList.add(cSkillupItem);
            }
            inItem.skillup = outList;
        }
    }

    /**
     * 反序列化 JSON AbilityItem(技能) 项
     * 
     * @param cJsonObj
     * @return
     */
    private static AbilityItem extractHeroAbilityItem(JSONObject cJsonObj) {
        if (cJsonObj == null)
            return null;

        final AbilityItem cItem = new AbilityItem();
        cItem.keyName = cJsonObj.optString("key_name");
        cItem.dname = cJsonObj.optString("dname");
        cItem.affects = cJsonObj.optString("affects");
        cItem.attrib = cJsonObj.optString("attrib");
        cItem.desc = cJsonObj.optString("desc");
        cItem.dmg = cJsonObj.optString("dmg");
        cItem.cmb = cJsonObj.optString("cmb");
        cItem.notes = cJsonObj.optString("notes");
        cItem.lore = cJsonObj.optString("lore");
        cItem.hurl = cJsonObj.optString("hurl");
        return cItem;
    }

    /**
     * 反序列化 JSON HeroSkillupItem(技能加点) 项
     * 
     * @param cJsonObj
     * @return
     */
    private static HeroSkillupItem extractHeroSkillupItem(JSONObject cJsonObj) {
        if (cJsonObj == null)
            return null;

        final HeroSkillupItem cItem = new HeroSkillupItem();
        cItem.groupName = cJsonObj.optString("groupName");
        cItem.desc = cJsonObj.optString("desc");
        cItem.abilityKeys = toStringArray(cJsonObj.optJSONArray("abilityKeys"));
        return cItem;
    }

    /**
     * 反序列化 JSON ItemsItem(物品) 项
     * 
     * @param cItemsKey
     * @param cJsonObj
     * @return
     */
    private static ItemsItem extractItemsItem(String cItemsKey,
            JSONObject cJsonObj) {
        if (cJsonObj == null || TextUtils.isEmpty(cItemsKey))
            return null;

        final ItemsItem cItem = new ItemsItem();
        cItem.keyName = cItemsKey;
        cItem.dname = cJsonObj.optString("dname");
        cItem.dname_l = cJsonObj.optString("dname_l");

        cItem.qual = cJsonObj.optString("qual");
        cItem.qual_l = cJsonObj.optString("qual_l");

        cItem.itembasecat = cJsonObj.optString("itembasecat");
        cItem.itemcat = cJsonObj.optString("itemcat");
        cItem.itemcat_l = cJsonObj.optString("itemcat_l");

        cItem.created = cJsonObj.optBoolean("created");
        cItem.ispublic = cJsonObj.optBoolean("ispublic");

        // --------------ItemsDetail----------
        cItem.cost = cJsonObj.optInt("cost");
        cItem.desc = cJsonObj.optString("desc");
        cItem.attrib = cJsonObj.optString("attrib");
        cItem.mc = cJsonObj.optString("mc");
        cItem.cd = cJsonObj.optInt("cd");
        cItem.notes = cJsonObj.optString("notes");
        cItem.lore = cJsonObj.optString("lore");
        cItem.components = toStringArray(cJsonObj.optJSONArray("components"));
        cItem.tocomponents = toStringArray(cJsonObj.optJSONArray("tocomponents"));
        cItem.toheros = toStringArray(cJsonObj.optJSONArray("toheros"));
        return cItem;
    }

    /**
     * 从 Assets 内加载指定 JSON 文件
     * 
     * @param cContext
     * @return
     * @throws IOException
     * @throws JSONException
     */
    private static JSONObject loadJsonObjectFromAssets(Context cContext,
            String fileName) throws IOException, JSONException {
        final InputStream in = cContext.getAssets().open(fileName);
        return new JSONObject(streamToString(in));
    }

    /**
     * Stream To String
     * 
     * @param in
     * @return
     */
    private static String streamToString(InputStream in) {
        if (in == null)
            return null;

        final BufferedReader reader = new BufferedReader(new InputStreamReader(
                in));
        final StringBuffer sb = new StringBuffer();
        final char[] buffer = new char[1024];
        int len = -1;
        try {
            while ((len = reader.read(buffer)) > 0) {
                sb.append(buffer, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    /**
     * JSONArray to StringArray
     * 
     * @param jsonArray
     * @return
     */
    private static String[] toStringArray(JSONArray jsonArray) {
        if (jsonArray == null || jsonArray.length() <= 0)
            return null;

        final int len = jsonArray.length();
        final String[] outList = new String[len];
        for (int index = 0; index < len; index++) {
            outList[index] = jsonArray.optString(index);
        }
        return outList;
    }

    /**
     * JSONArray to StringArray List
     * 
     * @param jsonArray
     * @return
     */
    private static List<String[]> toStringArray2(JSONArray jsonArray) {
        if (jsonArray == null || jsonArray.length() <= 0)
            return null;

        final int len = jsonArray.length();
        final List<String[]> outList = new ArrayList<String[]>(len);
        for (int index = 0; index < len; index++) {
            outList.add(toStringArray(jsonArray.optJSONArray(index)));
        }
        return outList;
    }

    /**
     * 
     * @param jsonObject
     * @return
     */
    private static Map<String, String[]> toStringArray(JSONObject jsonObject) {
        if (jsonObject == null || jsonObject.length() <= 0)
            return null;

        final Iterator<String> keyIterator =
                (Iterator<String>) jsonObject.keys();
        final Map<String, String[]> outMap = new HashMap<String, String[]>(jsonObject.length());
        String cKeyName = null;
        while (keyIterator.hasNext()) {
            cKeyName = keyIterator.next();
            outMap.put(cKeyName, toStringArray(jsonObject.optJSONArray(cKeyName)));
        }
        return outMap;
    }
}
