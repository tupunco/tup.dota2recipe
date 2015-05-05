package tup.dota2recipe.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import tup.dota2recipe.HeroDetailActivity;
import tup.dota2recipe.ItemsDetailActivity;
import tup.dota2recipe.R;
import tup.dota2recipe.entity.HeroItem;
import tup.dota2recipe.entity.ItemsItem;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.text.Html;
import android.text.Html.ImageGetter;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;

/**
 * Utils
 * 
 * @author tupunco
 * 
 */
public final class Utils {
    private final static String TAG = "Utils";
    private final static String s_ItemsImage_Format = "assets://items_images/%s_lg.jpg";
    private final static String s_HeroImage_Format = "assets://heroes_images/%s_full.jpg";
    // private final static String s_HeroIcon_Format =
    // "assets://heroes_icons/%s_icon.jpg";
    private final static String s_AbilitiesImage_Format = "assets://abilities_images/%s_hp1.jpg";

    /**
     * get Hero image url
     * 
     * @param keyName
     * @return
     */
    public static String getHeroImageUri(String keyName) {
        return String.format(s_HeroImage_Format, keyName);
    }

    /**
     * get Hero icon url
     * 
     * @param keyName
     * @return
     */
    // public static String getHeroIconUri(String keyName) {
    // return String.format(s_HeroIcon_Format, keyName);
    // }

    /**
     * get items image url
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
            menu.setIcon(R.drawable.ic_action_favorite);
            menu.setTitle(R.string.menu_removefavorite);
        } else {
            menu.setIcon(R.drawable.ic_action_favorite2);
            menu.setTitle(R.string.menu_addfavorite);
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
                .showStubImage(R.drawable.abs__progress_medium_holo)
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
        return exists(collection, predicate, false);
    }

    /**
     * 字符串数组内指定项存在与否
     * 
     * @param collection
     * @param predicate
     * @param ignoreCase
     * @return
     */
    public static boolean exists(String[] collection, String predicate, boolean ignoreCase) {
        if (collection != null && predicate != null) {
            for (String cItem : collection) {
                if (ignoreCase) {
                    if (predicate.equalsIgnoreCase(cItem))
                        return true;
                } else {
                    if (predicate.equals(cItem))
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

    /**
     * 
     * @param activity
     * @param id
     * @return
     */
    @SuppressWarnings({ "unchecked" })
    public static <T extends View> T findById(Activity activity, int id) {
        return (T) activity.findViewById(id);
    }

    /**
     * 
     * @param activity
     * @param id
     * @return
     */
    @SuppressWarnings({ "unchecked" })
    public static <T extends View> T findById(View view, int id) {
        return (T) view.findViewById(id);
    }

    /**
     * bind HtmlTextView value
     * 
     * @param text
     * @param fieldValue
     */
    public static void bindHtmlTextView(TextView text, String fieldValue) {
        bindHtmlTextView(text, fieldValue, null);
    }

    /**
     * bind HtmlTextView value
     * 
     * @param text
     * @param fieldValue
     * @param cImageGetter
     */
    public static void bindHtmlTextView(TextView text, String fieldValue, ImageGetter cImageGetter) {
        if (!TextUtils.isEmpty(fieldValue)) {
            text.setText(Html.fromHtml(fieldValue, cImageGetter, null));
        } else {
            text.setVisibility(View.GONE);
        }
    }

    /**
     * FROM FlymeAPI.Lib
     * Meizu-获取方法
     * 
     * @param method
     * @param clazz
     * @param name
     * @param parameterTypes
     * @return method
     */
    protected static Method getMethod(Method method, Class<?> clazz, String name,
            Class<?>... parameterTypes) {
        if (method == null) {
            try {
                method = clazz.getMethod(name, parameterTypes);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
        return method;
    }

    /**
     * FROM FlymeAPI.Lib
     * Meizu-执行方法
     * 
     * @param method
     *            方法
     * @param obj
     *            对像
     * @param args
     *            参数
     * @return boolean 执行结果
     */
    protected static boolean invoke(Method method, Object obj, Object... args) {
        if (method != null) {
            try {
                method.invoke(obj, args);
                return true;
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * FROM FlymeAPI.Lib
     * 判断设备是否支持smart bar
     * 
     * @return boolean true支持,false不支持
     */
    public static boolean hasSmartBar() {
        try {
            Method method = Class.forName("android.os.Build").getMethod("hasSmartBar");
            return ((Boolean) method.invoke(null)).booleanValue();
        } catch (Exception e) {
        }
        return false;
    }

    private static Class<?> sIMMClass = InputMethodManager.class;
    private static Method sSetMzInputThemeLight;

    /**
     * FROM FlymeAPI.Lib
     * 
     * 设置导航栏和输入法背景颜色，在App启动第一个Actiity onCreate方法中调用该方法，
     * 执行成功后，App中使用系统输入法都是白色样式
     * 
     * @param context
     *            上下文
     * @param light
     *            是否把导航栏和输入法背景设置为白色
     * @return boolean 执行结果，成功执行返回true
     */
    public static boolean setInputThemeLight(Context context, boolean light) {
        sSetMzInputThemeLight = getMethod(sSetMzInputThemeLight, sIMMClass, "setMzInputThemeLight",
                boolean.class);
        InputMethodManager imm = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            return invoke(sSetMzInputThemeLight, imm, light);
        }
        return false;
    }

    /**
     * FROM FlymeAPI.Lib
     * 
     * 设置状态栏图标为深色和魅族特定的文字风格
     * 
     * @param window
     *            需要设置的窗口
     * @param dark
     *            是否把状态栏颜色设置为深色
     * @return boolean 成功执行返回true
     */
    public static boolean setStatusBarDarkIcon(Window window, boolean dark) {
        boolean result = false;
        if (window != null) {
            try {
                WindowManager.LayoutParams lp = window.getAttributes();
                Field darkFlag = WindowManager.LayoutParams.class
                        .getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON");
                Field meizuFlags = WindowManager.LayoutParams.class
                        .getDeclaredField("meizuFlags");
                darkFlag.setAccessible(true);
                meizuFlags.setAccessible(true);
                int bit = darkFlag.getInt(null);
                int value = meizuFlags.getInt(lp);
                if (dark) {
                    value |= bit;
                } else {
                    value &= ~bit;
                }
                meizuFlags.setInt(lp, value);
                window.setAttributes(lp);
                result = true;
            } catch (Exception e) {
                Log.e(TAG, "setStatusBarDarkIcon: failed");
            }
        }
        return result;
    }

    /**
     * FROM FlymeAPI.Lib
     * 设置沉浸式窗口，设置成功后，状态栏则透明显示
     * 
     * @param window
     *            需要设置的窗口
     * @param immersive
     *            是否把窗口设置为沉浸
     * @return boolean 成功执行返回true
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static boolean setImmersedWindow(Window window, boolean immersive) {
        boolean result = false;
        if (window != null) {
            WindowManager.LayoutParams lp = window.getAttributes();
            int trans_status = 0;
            Field flags;
            if (android.os.Build.VERSION.SDK_INT < 19) {
                try {
                    trans_status = 1 << 6;
                    flags = lp.getClass().getDeclaredField("meizuFlags");
                    flags.setAccessible(true);
                    int value = flags.getInt(lp);
                    if (immersive) {
                        value = value | trans_status;
                    } else {
                        value = value & ~trans_status;
                    }
                    flags.setInt(lp, value);
                    result = true;
                } catch (Exception e) {
                    Log.e(TAG, "setImmersedWindow: failed");
                }
            } else {
                lp.flags |= WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
                window.setAttributes(lp);
                result = true;
            }
        }
        return result;
    }
}
