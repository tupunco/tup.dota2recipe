package tup.dota2recipe.util;

import tup.dota2recipe.HeroDetailActivity;
import tup.dota2recipe.ItemsDetailActivity;
import tup.dota2recipe.R;
import tup.dota2recipe.entity.HeroItem;
import tup.dota2recipe.entity.ItemsItem;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;

import com.nostra13.universalimageloader.core.DisplayImageOptions;

/**
 * 工具类
 * 
 * @author tupunco
 * 
 */
public final class Utils {
    private final static String s_ItemsImage_Format = "assets://items_images/%s_lg.jpg";
    private final static String s_HeroImage_Format = "assets://heroes_images/%s_full.jpg";
    private final static String s_AbilitiesImage_Format = "assets://abilities_images/%s_hp1.jpg";

    /**
     * 
     * @param keyName
     * @return
     */
    public static String getHeroImageUri(String keyName) {
        return String.format(s_HeroImage_Format, keyName);
    }

    /**
     * 
     * @param keyName
     * @return
     */
    public static String getItemsImageUri(String keyName) {
        return String.format(s_ItemsImage_Format, keyName);
    }

    /**
     * 
     * @param keyName
     * @return
     */
    public static String getAbilitiesImageUri(String keyName) {
        return String.format(s_AbilitiesImage_Format, keyName);
    }

    /**
     * 开始 HeroDetail Activity
     * 
     * @param activity
     * @param cItem
     */
    public static void startHeroDetailActivity(Activity activity, HeroItem cItem) {
        if (cItem == null) {
            return;
        }

        startHeroDetailActivity(activity, cItem.keyName);
    }

    /**
     * 开始 HeroDetail Activity
     * 
     * @param activity
     * @param cItem
     */
    public static void startHeroDetailActivity(Activity activity, String cItemKeyName) {
        if (activity == null || cItemKeyName == null) {
            return;
        }

        final Intent intent = new Intent(activity, HeroDetailActivity.class);
        intent.putExtra(HeroDetailActivity.KEY_HERO_DETAIL_KEY_NAME, cItemKeyName);
        activity.startActivity(intent);
    }

    /**
     * 开始 ItemsDetail Activity
     * 
     * @param activity
     * @param cItem
     */
    public static void startItemsDetailActivity(Activity activity, ItemsItem cItem) {
        if (cItem == null) {
            return;
        }

        startItemsDetailActivity(activity, cItem.keyName);
    }

    /**
     * 开始 ItemsDetail Activity
     * 
     * @param activity
     * @param cItem
     */
    public static void startItemsDetailActivity(Activity activity, String cItemKeyName) {
        if (activity == null || cItemKeyName == null) {
            return;
        }

        final Intent intent = new Intent(activity, ItemsDetailActivity.class);
        intent.putExtra(ItemsDetailActivity.KEY_ITEMS_DETAIL_KEY_NAME, cItemKeyName);
        activity.startActivity(intent);
    }

    /**
     * 填充 Fragment 到 FragmentActivity
     * 
     * @param fragmentActivity
     * @param cFragment
     */
    public static void fillFragment(FragmentActivity fragmentActivity, Fragment cFragment) {
        if (fragmentActivity == null || cFragment == null) {
            return;
        }

        final FragmentManager fm = fragmentActivity.getSupportFragmentManager();
        if (fm.findFragmentById(android.R.id.content) == null) {
            fm.beginTransaction().add(android.R.id.content, cFragment).commit();
        }
    }

    /**
     * 
     * @return
     */
    public static DisplayImageOptions createDisplayImageOptions() {
        return new DisplayImageOptions.Builder()
                .showStubImage(com.actionbarsherlock.R.drawable.abs__progress_medium_holo)
                .showImageForEmptyUri(R.drawable.hero_for_empty_url)
                .cacheInMemory()
                .build();
    }

    /**
     * 执行 AsyncTask
     * 
     * @param task
     */
    @SuppressLint("NewApi")
    public static <Params, Progress, Result> void executeAsyncTask(
            AsyncTask<Params, Progress, Result> loaderTask, Params... params) {
        if (loaderTask == null) {
            return;
        }

        if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.GINGERBREAD_MR1) {
            loaderTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params);
        } else {
            loaderTask.execute(params);
        }
    }

    /**
     * 字符串数组内指定项存在与否
     * @param collection
     * @param predicate
     * @return
     */
    public static boolean exists(String[] collection, String predicate) {
        if (collection != null && predicate != null) {
            for (String cItem : collection) {
                if (predicate.contentEquals(cItem)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 字符串数组内指定项的索引
     * @param collection
     * @param predicate
     * @return
     */
    public static int indexOf(String[] collection, String predicate) {
        if (collection != null && collection.length > 0 && predicate != null) {
            for (int i = 0; i < collection.length; i++) {
                if (collection[i].contentEquals(predicate)) {
                    return i;
                }
            }
        }
        return -1;
    }

    /**
     * 英雄/物品 分类菜单项指定值获取
     * 
     * @param cResources
     * @param keys
     * @param values
     * @param value
     * @return
     */
    public static String getMenuValue(Resources cResources, int keys_resId, int values_resId,
            String value) {
        if (cResources == null) {
            return null;
        }

        return getMenuValue(cResources.getStringArray(keys_resId),
                cResources.getStringArray(values_resId), value);
    }

    /**
     * 英雄/物品 分类菜单项指定值获取
     * 
     * @param keys
     * @param values
     * @param value
     * @return
     */
    public static String getMenuValue(String[] keys, String[] values, String value) {
        if (keys == null || values == null || TextUtils.isEmpty(value)
                || keys.length <= 0 || keys.length != values.length) {
            return null;
        }
        for (int i = 0; i < values.length; i++) {
            if (values[i].contentEquals(value)) {
                return keys[i];
            }
        }
        return null;
    }
}
