package tup.dota2recipe.adapter;

import java.util.ArrayList;
import java.util.List;

import tup.dota2recipe.DefaultApplication;
import tup.dota2recipe.entity.CollectionItem;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
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
    private static final int DATABASE_VERSION = 0;
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
            final DBHelper helper = new DBHelper(DefaultApplication.getInstance(),
                    DATABASE_NAME, null, DATABASE_VERSION);
            singleton = new DBAdapter();
            singleton.wsd = helper.getWritableDatabase();
            singleton.rsd = helper.getReadableDatabase();
        }

        return singleton;
    }

    /**
     * 获取收藏列表
     * 
     * @return
     */
    public List<CollectionItem> getCollections() {
        final List<CollectionItem> list = new ArrayList<CollectionItem>();
        final Cursor c = rsd.query(TABLE_NAME_COLLECTIONS, SELECT_COLLECTION_COLUMNS,
                null, null, null, null, null);
        while (c.moveToNext()) {
            list.add(extractCollectionItem(c));
        }
        c.close();
        return list;
    }

    /**
     * 获取收藏项
     * 
     * @param keyName
     * @return
     */
    public CollectionItem getCollectionByKeyName(String keyName) {
        if (!TextUtils.isEmpty(keyName)) {
            final Cursor c = rsd.query(TABLE_NAME_COLLECTIONS, SELECT_COLLECTION_COLUMNS,
                    KEY_KEYNAME + "=?", new String[] { keyName }, null, null, null);
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

        return wsd.delete(TABLE_NAME_COLLECTIONS, KEY_KEYNAME + "=?", new String[] { keyName });
    }

    /**
     * 添加收藏项
     * 
     * @param keyName
     * @param collect_type
     * @return
     */
    public long addCollection(CollectionItem cItem) {
        if (cItem == null || TextUtils.isEmpty(cItem.keyName)
                || (cItem.type != CollectionItem.KEY_TYPE_HERO
                && cItem.type != CollectionItem.KEY_TYPE_ITEMS)) {
            return -1L;
        }

        ContentValues values = new ContentValues();
        values.put(KEY_KEYNAME, cItem.keyName);
        values.put(KEY_COLLECT_TYPE, cItem.type);
        return wsd.insert(TABLE_NAME_COLLECTIONS, null, values);
    }

    /**
     * 
     * @param c
     * @return
     */
    private CollectionItem extractCollectionItem(Cursor c) {
        final CollectionItem item = new CollectionItem();

        int colid = c.getColumnIndex(KEY_ID);
        item.id = c.getInt(colid);

        colid = c.getColumnIndex(KEY_KEYNAME);
        item.keyName = c.getString(colid);

        colid = c.getColumnIndex(KEY_COLLECT_TYPE);
        item.type = c.getInt(colid);
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
            Log.w(TAG, "Upgrading from version " + oldVersion + " to " + newVersion + ".");
        }
    }
}
