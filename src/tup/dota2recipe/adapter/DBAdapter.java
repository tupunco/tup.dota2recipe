package tup.dota2recipe.adapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json2.JSONException;

import tup.dota2recipe.DataManager;
import tup.dota2recipe.DefaultApplication;
import tup.dota2recipe.entity.FavoriteItem;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DataSetObservable;
import android.database.DataSetObserver;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Log;

/**
 * DBAdapter
 * 
 * @author tupunco
 */
public class DBAdapter {
    private static final String TAG = "DBAdapter";
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "dota2recipe.db";

    public static final String KEY_ID = "id";
    public static final String KEY_KEYNAME = "keyname";
    public static final String KEY_COLLECT_TYPE = "collect_type";

    public static final String TABLE_NAME_COLLECTIONS = "collections";

    private static final String TABLE_PRIMARY_KEY = KEY_ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT ,";

    private static final String CREATE_TABLE_COLLECTIONS = "CREATE TABLE "
            + TABLE_NAME_COLLECTIONS + " (" + TABLE_PRIMARY_KEY
            + KEY_KEYNAME + " TEXT,"
            + KEY_COLLECT_TYPE + " INTEGER)";
    private static final String[] SELECT_COLLECTION_COLUMNS = new String[] {
            KEY_ID, KEY_KEYNAME, KEY_COLLECT_TYPE };

    private DataSetObservable mDataSetObservable = new DataSetObservable();
    private static DBAdapter singleton = null;
    private SQLiteDatabase wsd = null;
    private SQLiteDatabase rsd = null;

    DBAdapter() {
    }

    /**
     * DBAdapter Instance
     * 
     * @return
     */
    public synchronized static DBAdapter getInstance() {
        if (singleton == null) {
            final DBHelper helper = new DBHelper(
                    DefaultApplication.getInstance(),
                    DATABASE_NAME, null, DATABASE_VERSION);
            singleton = new DBAdapter();
            singleton.wsd = helper.getWritableDatabase();
            singleton.rsd = helper.getReadableDatabase();
        }

        return singleton;
    }

    /**
     * 注册收藏列表改变监视器
     * 
     * @param observer
     */
    public void registerFavoriteObserver(DataSetObserver observer) {
        if (observer != null)
            mDataSetObservable.registerObserver(observer);
    }

    /**
     * 取消注册收藏列表改变监视器
     * 
     * @param observer
     */
    public void unregisterFavoriteObserver(DataSetObserver observer) {
        if (observer != null)
            mDataSetObservable.unregisterObserver(observer);
    }

    /**
     * 获取收藏列表
     * 
     * @return
     */
    public List<FavoriteItem> getFavorites() {
        final List<FavoriteItem> list = new ArrayList<FavoriteItem>();
        final Cursor c = rsd.query(TABLE_NAME_COLLECTIONS,
                SELECT_COLLECTION_COLUMNS,
                null, null, null, null, null);
        while (c.moveToNext()) {
            list.add(extractCollectionItem(c));
        }
        c.close();
        return list;
    }

    /**
     * 
     * @param keyName
     * @return
     */
    public boolean hasCollection(String keyName) {
        final FavoriteItem c = getCollectionByKeyName(keyName);
        return c != null;
    }

    /**
     * 获取收藏项
     * 
     * @param keyName
     * @return
     */
    public FavoriteItem getCollectionByKeyName(String keyName) {
        if (!TextUtils.isEmpty(keyName)) {
            final Cursor c = rsd.query(TABLE_NAME_COLLECTIONS,
                    SELECT_COLLECTION_COLUMNS,
                    KEY_KEYNAME + "=?", new String[] { keyName }, null, null,
                    null);
            if (c.moveToNext()) {
                return extractCollectionItem(c);
            }
            c.close();
        }
        return null;
    }

    /**
     * 删除收藏项
     * 
     * @param keyName
     * @return
     */
    public int deleteCollection(String keyName) {
        if (TextUtils.isEmpty(keyName))
            return -1;

        final int res = wsd.delete(TABLE_NAME_COLLECTIONS, KEY_KEYNAME + "=?",
                new String[] { keyName });
        if (res > 0) {
            mDataSetObservable.notifyChanged();
        }
        return res;
    }

    /**
     * 添加收藏项
     * 
     * @param cItem
     * @return
     */
    public long addCollection(FavoriteItem cItem) {
        if (cItem == null || TextUtils.isEmpty(cItem.keyName)
                || (cItem.type != FavoriteItem.KEY_TYPE_HERO
                && cItem.type != FavoriteItem.KEY_TYPE_ITEMS)) {
            return -1L;
        }

        ContentValues values = new ContentValues();
        values.put(KEY_KEYNAME, cItem.keyName);
        values.put(KEY_COLLECT_TYPE, cItem.type);
        final long resId = wsd.insert(TABLE_NAME_COLLECTIONS, null, values);
        if (resId >= 0L) {
            mDataSetObservable.notifyChanged();
        }
        return resId;
    }

    /**
     * 
     * @param c
     * @return
     */
    private FavoriteItem extractCollectionItem(Cursor c) {
        final FavoriteItem item = new FavoriteItem();

        int colid = c.getColumnIndex(KEY_ID);
        item.id = c.getInt(colid);

        colid = c.getColumnIndex(KEY_KEYNAME);
        item.keyName = c.getString(colid);

        colid = c.getColumnIndex(KEY_COLLECT_TYPE);
        item.type = c.getInt(colid);

        try {
            if (item.type == FavoriteItem.KEY_TYPE_HERO) {
                item.heroData =
                        DataManager.getHeroItem(
                                DefaultApplication.getInstance(), item.keyName);
            } else if (item.type == FavoriteItem.KEY_TYPE_ITEMS) {
                item.itemsData =
                        DataManager.getItemsItem(
                                DefaultApplication.getInstance(), item.keyName);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return item;
    }

    /**
     * 
     */
    private static class DBHelper extends SQLiteOpenHelper {
        public DBHelper(final Context context, final String name,
                final CursorFactory factory, final int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(final SQLiteDatabase db) {
            db.execSQL(CREATE_TABLE_COLLECTIONS);
        }

        @Override
        public void onUpgrade(final SQLiteDatabase db, final int oldVersion,
                final int newVersion) {
            Log.w(TAG, "Upgrading from version " + oldVersion + " to "
                    + newVersion + ".");
        }
    }
}
