package tup.dota2recipe.util;

import tup.dota2recipe.HeroDetailActivity;
import tup.dota2recipe.ItemsDetailActivity;
import tup.dota2recipe.R;
import tup.dota2recipe.entity.HeroItem;
import tup.dota2recipe.entity.ItemsItem;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;

import com.actionbarsherlock.view.MenuItem;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

/**
 * Utils
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
     * 
     * @param menu
     */
    public static void configureStarredMenuItem(MenuItem menu) {
        configureStarredMenuItem(menu, false);
    }

    /**
     * 
     * @param menu
     * @param isRecipe
     *            物品页面,当前物品是否是合成卷轴
     */
    public static void configureStarredMenuItem(MenuItem menu, boolean isRecipe) {
        if (menu == null || !menu.isCheckable()) {
            return;
        }
        if (isRecipe) {
            menu.setVisible(false);
            return;
        }

        if (menu.isChecked()) {
            menu.setIcon(R.drawable.btn_star_on_normal_holo_dark);
            menu.setTitle(R.string.menu_removecollection);
        } else {
            menu.setIcon(R.drawable.btn_star_off_normal_holo_dark);
            menu.setTitle(R.string.menu_addcollection);
        }
    }

    /**
     * start HeroDetail Activity
     * 
     * @param packageContext
     * @param cItem
     */
    public static void startHeroDetailActivity(Context packageContext, HeroItem cItem) {
        if (cItem == null) {
            return;
        }

        startHeroDetailActivity(packageContext, cItem.keyName);
    }

    /**
     * start HeroDetail Activity
     * 
     * @param packageContext
     * @param cItem
     */
    private static void startHeroDetailActivity(Context packageContext, String cItemKeyName) {
        if (packageContext == null || cItemKeyName == null) {
            return;
        }

        final Intent intent = new Intent(packageContext, HeroDetailActivity.class);
        intent.putExtra(HeroDetailActivity.KEY_HERO_DETAIL_KEY_NAME, cItemKeyName);
        packageContext.startActivity(intent);
    }

    /**
     * start ItemsDetail Activity
     * 
     * @param packageContext
     * @param cItem
     */
    public static void startItemsDetailActivity(Context packageContext, ItemsItem cItem) {
        if (cItem == null) {
            return;
        }

        startItemsDetailActivity(packageContext, cItem.keyName, cItem.parent_keyName);
    }

    /**
     * start ItemsDetail Activity
     * 
     * @param packageContext
     * @param cItem
     */
    private static void startItemsDetailActivity(Context packageContext, String cItemKeyName,
            String cItemParentKeyName) {
        if (packageContext == null || cItemKeyName == null) {
            return;
        }

        final Intent intent = new Intent(packageContext, ItemsDetailActivity.class);
        intent.putExtra(ItemsDetailActivity.KEY_ITEMS_DETAIL_KEY_NAME, cItemKeyName);
        if (!TextUtils.isEmpty(cItemParentKeyName)) {
            intent.putExtra(ItemsDetailActivity.KEY_ITEMS_DETAIL_PARENT_KEY_NAME,
                    cItemParentKeyName);
        }
        packageContext.startActivity(intent);
    }

    /**
     * fill Fragment to FragmentActivity
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
     * execute AsyncTask
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
     * 
     * @param collection
     * @param predicate
     * @return
     */
    public static boolean exists(String[] collection, String predicate) {
        if (collection != null && predicate != null) {
            for (String cItem : collection) {
                if (predicate.equals(cItem)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 字符串数组内指定项的索引
     * 
     * @param collection
     * @param predicate
     * @return
     */
    public static int indexOf(String[] collection, String predicate) {
        if (collection != null && collection.length > 0 && predicate != null) {
            for (int i = 0; i < collection.length; i++) {
                if (collection[i].equals(predicate)) {
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
        if (keys == null || values == null || TextUtils.isEmpty(value) || keys.length <= 0
                || keys.length != values.length) {
            return null;
        }
        for (int i = 0; i < values.length; i++) {
            if (values[i].equals(value)) {
                return keys[i];
            }
        }
        return null;
    }
}
