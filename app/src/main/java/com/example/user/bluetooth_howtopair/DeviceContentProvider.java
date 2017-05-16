package com.example.user.bluetooth_howtopair;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import java.util.HashMap;

import com.example.user.bluetooth_howtopair.DevicesSetProvider.DevicesSetColumns;
import com.example.user.bluetooth_howtopair.utils.Constants;

public class DeviceContentProvider extends ContentProvider {
    private static final int DEVICES = 1;
    private static final int DEVICESID = 2;
    private static final UriMatcher sUriMatcher;
    private static HashMap<String, String> sprogrammersProjectionMap;
    private DatabaseDevicesConnectHelper mOpenHelper;

    static {
        sUriMatcher = new UriMatcher(-1);
        sprogrammersProjectionMap = new HashMap();
        sprogrammersProjectionMap.put("_id", "_id");
        sprogrammersProjectionMap.put(DevicesSetColumns.DEVICES_NAME, DevicesSetColumns.DEVICES_NAME);
        sprogrammersProjectionMap.put(DevicesSetColumns.DEVICES_NICK, DevicesSetColumns.DEVICES_NICK);
        sprogrammersProjectionMap.put(DevicesSetColumns.DEVICES_SYSTEMID, DevicesSetColumns.DEVICES_SYSTEMID);
        sprogrammersProjectionMap.put(DevicesSetColumns.DEVICES_VERSION, DevicesSetColumns.DEVICES_VERSION);
        sprogrammersProjectionMap.put(DevicesSetColumns.DEVICES_IMG, DevicesSetColumns.DEVICES_IMG);
        sprogrammersProjectionMap.put(DevicesSetColumns.DEVICES_ONLINE, DevicesSetColumns.DEVICES_ONLINE);
        sprogrammersProjectionMap.put(DevicesSetColumns.DEVICES_LASTTIME, DevicesSetColumns.DEVICES_LASTTIME);
        sprogrammersProjectionMap.put(DevicesSetColumns.DEVICES_TYPE, DevicesSetColumns.DEVICES_TYPE);
        sprogrammersProjectionMap.put(DevicesSetColumns.DEVICES_ID1, DevicesSetColumns.DEVICES_ID1);
        sprogrammersProjectionMap.put(DevicesSetColumns.DEVICES_IR1, DevicesSetColumns.DEVICES_IR1);
        sprogrammersProjectionMap.put(DevicesSetColumns.DEVICES_TY1, DevicesSetColumns.DEVICES_TY1);
        sprogrammersProjectionMap.put(DevicesSetColumns.DEVICES_TW1, DevicesSetColumns.DEVICES_TW1);
        sprogrammersProjectionMap.put(DevicesSetColumns.DEVICES_DL1, DevicesSetColumns.DEVICES_DL1);
        sprogrammersProjectionMap.put(DevicesSetColumns.DEVICES_DISTYPE1, DevicesSetColumns.DEVICES_DISTYPE1);
        sprogrammersProjectionMap.put(DevicesSetColumns.DEVICES_ID2, DevicesSetColumns.DEVICES_ID2);
        sprogrammersProjectionMap.put(DevicesSetColumns.DEVICES_IR2, DevicesSetColumns.DEVICES_IR2);
        sprogrammersProjectionMap.put(DevicesSetColumns.DEVICES_TY2, DevicesSetColumns.DEVICES_TY2);
        sprogrammersProjectionMap.put(DevicesSetColumns.DEVICES_TW2, DevicesSetColumns.DEVICES_TW2);
        sprogrammersProjectionMap.put(DevicesSetColumns.DEVICES_DL2, DevicesSetColumns.DEVICES_DL2);
        sprogrammersProjectionMap.put(DevicesSetColumns.DEVICES_DISTYPE2, DevicesSetColumns.DEVICES_DISTYPE2);
        sprogrammersProjectionMap.put(DevicesSetColumns.DEVICES_ID3, DevicesSetColumns.DEVICES_ID3);
        sprogrammersProjectionMap.put(DevicesSetColumns.DEVICES_IR3, DevicesSetColumns.DEVICES_IR3);
        sprogrammersProjectionMap.put(DevicesSetColumns.DEVICES_TY3, DevicesSetColumns.DEVICES_TY3);
        sprogrammersProjectionMap.put(DevicesSetColumns.DEVICES_TW3, DevicesSetColumns.DEVICES_TW3);
        sprogrammersProjectionMap.put(DevicesSetColumns.DEVICES_DL3, DevicesSetColumns.DEVICES_DL3);
        sprogrammersProjectionMap.put(DevicesSetColumns.DEVICES_DISTYPE3, DevicesSetColumns.DEVICES_DISTYPE3);
        sprogrammersProjectionMap.put(DevicesSetColumns.DEVICES_ID4, DevicesSetColumns.DEVICES_ID4);
        sprogrammersProjectionMap.put(DevicesSetColumns.DEVICES_IR4, DevicesSetColumns.DEVICES_IR4);
        sprogrammersProjectionMap.put(DevicesSetColumns.DEVICES_TY4, DevicesSetColumns.DEVICES_TY4);
        sprogrammersProjectionMap.put(DevicesSetColumns.DEVICES_TW4, DevicesSetColumns.DEVICES_TW4);
        sprogrammersProjectionMap.put(DevicesSetColumns.DEVICES_DL4, DevicesSetColumns.DEVICES_DL4);
        sprogrammersProjectionMap.put(DevicesSetColumns.DEVICES_DISTYPE4, DevicesSetColumns.DEVICES_DISTYPE4);
    }

    public boolean onCreate() {
        this.mOpenHelper = new DatabaseDevicesConnectHelper(getContext());
        String str = getContext().getPackageName();
        sUriMatcher.addURI(str, Constants.DEVICES, DEVICES);
        sUriMatcher.addURI(str, "devices/#", DEVICESID);
        DevicesProvider.setUriString(str);
        return true;
    }

    public String getType(Uri uri) {
        switch (sUriMatcher.match(uri)) {
            case DEVICES /*1*/:
                return DevicesProvider.CONTENT_TYPE;
            case DEVICESID /*2*/:
                return DevicesProvider.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        switch (sUriMatcher.match(uri)) {
            case DEVICES /*1*/:
            case DEVICESID /*2*/:
                String orderBy;
                qb.setTables(Constants.DEVICES);
                if (TextUtils.isEmpty(sortOrder)) {
                    orderBy = DevicesSetColumns.DEFAULT_SORT_ORDER;
                } else {
                    orderBy = sortOrder;
                }
                Cursor c = qb.query(this.mOpenHelper.getReadableDatabase(), projection, selection, selectionArgs, null, null, orderBy);
                c.setNotificationUri(getContext().getContentResolver(), uri);
                return c;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    public Uri insert(Uri uri, ContentValues initialValues) {
        ContentValues values;
        if (initialValues != null) {
            values = new ContentValues(initialValues);
        } else {
            values = new ContentValues();
        }
        String tableName = "";
        switch (sUriMatcher.match(uri)) {
            case DEVICES /*1*/:
                tableName = Constants.DEVICES;
                if (!values.containsKey(DevicesSetColumns.DEVICES_NAME)) {
                    values.put(DevicesSetColumns.DEVICES_NAME, "");
                }
                if (!values.containsKey(DevicesSetColumns.DEVICES_VERSION)) {
                    values.put(DevicesSetColumns.DEVICES_VERSION, "");
                }
                if (!values.containsKey(DevicesSetColumns.DEVICES_SYSTEMID)) {
                    values.put(DevicesSetColumns.DEVICES_SYSTEMID, "");
                }
                if (!values.containsKey(DevicesSetColumns.DEVICES_NICK)) {
                    values.put(DevicesSetColumns.DEVICES_NICK, "");
                }
                if (!values.containsKey(DevicesSetColumns.DEVICES_IMG)) {
                    values.put(DevicesSetColumns.DEVICES_IMG, "");
                }
                if (!values.containsKey(DevicesSetColumns.DEVICES_ONLINE)) {
                    values.put(DevicesSetColumns.DEVICES_ONLINE, Integer.valueOf(DEVICES));
                }
                if (!values.containsKey(DevicesSetColumns.DEVICES_LASTTIME)) {
                    values.put(DevicesSetColumns.DEVICES_LASTTIME, "");
                }
                if (!values.containsKey(DevicesSetColumns.DEVICES_TYPE)) {
                    values.put(DevicesSetColumns.DEVICES_TYPE, Integer.valueOf(0));
                }
                if (!values.containsKey(DevicesSetColumns.DEVICES_ID1)) {
                    values.put(DevicesSetColumns.DEVICES_ID1, "");
                }
                if (!values.containsKey(DevicesSetColumns.DEVICES_IR1)) {
                    values.put(DevicesSetColumns.DEVICES_IR1, Integer.valueOf(0));
                }
                if (!values.containsKey(DevicesSetColumns.DEVICES_TY1)) {
                    values.put(DevicesSetColumns.DEVICES_TY1, Integer.valueOf(0));
                }
                if (!values.containsKey(DevicesSetColumns.DEVICES_TW1)) {
                    values.put(DevicesSetColumns.DEVICES_TW1, Integer.valueOf(0));
                }
                if (!values.containsKey(DevicesSetColumns.DEVICES_DL1)) {
                    values.put(DevicesSetColumns.DEVICES_DL1, Integer.valueOf(0));
                }
                if (!values.containsKey(DevicesSetColumns.DEVICES_DISTYPE1)) {
                    values.put(DevicesSetColumns.DEVICES_DISTYPE1, Integer.valueOf(0));
                }
                if (!values.containsKey(DevicesSetColumns.DEVICES_ID2)) {
                    values.put(DevicesSetColumns.DEVICES_ID2, "");
                }
                if (!values.containsKey(DevicesSetColumns.DEVICES_IR2)) {
                    values.put(DevicesSetColumns.DEVICES_IR2, Integer.valueOf(0));
                }
                if (!values.containsKey(DevicesSetColumns.DEVICES_TY2)) {
                    values.put(DevicesSetColumns.DEVICES_TY2, Integer.valueOf(0));
                }
                if (!values.containsKey(DevicesSetColumns.DEVICES_TW2)) {
                    values.put(DevicesSetColumns.DEVICES_TW2, Integer.valueOf(0));
                }
                if (!values.containsKey(DevicesSetColumns.DEVICES_DL2)) {
                    values.put(DevicesSetColumns.DEVICES_DL2, Integer.valueOf(0));
                }
                if (!values.containsKey(DevicesSetColumns.DEVICES_DISTYPE2)) {
                    values.put(DevicesSetColumns.DEVICES_DISTYPE2, Integer.valueOf(0));
                }
                if (!values.containsKey(DevicesSetColumns.DEVICES_ID3)) {
                    values.put(DevicesSetColumns.DEVICES_ID3, "");
                }
                if (!values.containsKey(DevicesSetColumns.DEVICES_IR3)) {
                    values.put(DevicesSetColumns.DEVICES_IR3, Integer.valueOf(0));
                }
                if (!values.containsKey(DevicesSetColumns.DEVICES_TY3)) {
                    values.put(DevicesSetColumns.DEVICES_TY3, Integer.valueOf(0));
                }
                if (!values.containsKey(DevicesSetColumns.DEVICES_TW3)) {
                    values.put(DevicesSetColumns.DEVICES_TW3, Integer.valueOf(0));
                }
                if (!values.containsKey(DevicesSetColumns.DEVICES_DL3)) {
                    values.put(DevicesSetColumns.DEVICES_DL3, Integer.valueOf(0));
                }
                if (!values.containsKey(DevicesSetColumns.DEVICES_DISTYPE3)) {
                    values.put(DevicesSetColumns.DEVICES_DISTYPE3, Integer.valueOf(0));
                }
                if (!values.containsKey(DevicesSetColumns.DEVICES_ID4)) {
                    values.put(DevicesSetColumns.DEVICES_ID4, "");
                }
                if (!values.containsKey(DevicesSetColumns.DEVICES_IR4)) {
                    values.put(DevicesSetColumns.DEVICES_IR4, Integer.valueOf(0));
                }
                if (!values.containsKey(DevicesSetColumns.DEVICES_TY4)) {
                    values.put(DevicesSetColumns.DEVICES_TY4, Integer.valueOf(0));
                }
                if (!values.containsKey(DevicesSetColumns.DEVICES_TW4)) {
                    values.put(DevicesSetColumns.DEVICES_TW4, Integer.valueOf(0));
                }
                if (!values.containsKey(DevicesSetColumns.DEVICES_DL4)) {
                    values.put(DevicesSetColumns.DEVICES_DL4, Integer.valueOf(0));
                }
                if (!values.containsKey(DevicesSetColumns.DEVICES_DISTYPE4)) {
                    values.put(DevicesSetColumns.DEVICES_DISTYPE4, Integer.valueOf(0));
                }
                long rowId = this.mOpenHelper.getWritableDatabase().insert(tableName, null, values);
                if (rowId > 0) {
                    Uri noteUri = ContentUris.withAppendedId(uri, rowId);
                    getContext().getContentResolver().notifyChange(noteUri, null);
                    return noteUri;
                }
                throw new SQLException("Failed to insert row into " + uri);
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    public int delete(Uri uri, String where, String[] whereArgs) {
        SQLiteDatabase db = this.mOpenHelper.getWritableDatabase();
        switch (sUriMatcher.match(uri)) {
            case DEVICES /*1*/:
                int count = db.delete(Constants.DEVICES, where, whereArgs);
                getContext().getContentResolver().notifyChange(uri, null);
                return count;
            case DEVICESID /*2*/:
                db.delete(Constants.DEVICES, "_id=" + ((String) uri.getPathSegments().get(DEVICES)) + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""), whereArgs);
                break;
        }
        throw new IllegalArgumentException("Unknown URI " + uri);
    }

    public int update(Uri uri, ContentValues values, String where, String[] whereArgs) {
        int count;
        SQLiteDatabase db = this.mOpenHelper.getWritableDatabase();
        switch (sUriMatcher.match(uri)) {
            case DEVICES /*1*/:
                count = db.update(Constants.DEVICES, values, where, whereArgs);
                break;
            case DEVICESID /*2*/:
                count = db.update(Constants.DEVICES, values, "_id=" + ((String) uri.getPathSegments().get(DEVICES)) + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""), whereArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }
}

