package com.example.user.bluetooth_howtopair.utils;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import com.example.user.bluetooth_howtopair.DevicesSetProvider.DevicesSetColumns;

public class ProviderUtils {
    public static boolean existData(Context context, Uri uri, ContentValues values, String... keys) {
        ContentResolver resolver = context.getContentResolver();
        String str = null;
        String[] selections = null;
        if (keys.length > 0) {
            str = new String();
            selections = new String[keys.length];
        }
        for (int i = 0; i < keys.length; i++) {
            if (keys.length - i > 1) {
                str = new StringBuilder(String.valueOf(str)).append(keys[i]).append("=? and ").toString();
            } else {
                str = new StringBuilder(String.valueOf(str)).append(keys[i]).append("=?").toString();
            }
            selections[i] = values.getAsString(keys[i]);
        }
        Cursor cursor = resolver.query(uri, null, str, selections, null);
        if (cursor != null) {
            cursor.moveToFirst();
            cursor.close();
            return true;
        }
        return false;
    }

    public static Uri insertData(Context context, Uri uri, ContentValues values) {
        return context.getContentResolver().insert(uri, values);
    }

    public static void deleteData(Context context, Uri uri, String keys, String... values) {
        context.getContentResolver().delete(uri, keys, values);
    }

    public static boolean insertData(Context context, Uri uri, ContentValues values, String... keys) {
        ContentResolver resolver = context.getContentResolver();
        String str = null;
        String[] selections = null;
        if (keys.length > 0) {
            str = new String();
            selections = new String[keys.length];
        }
        for (int i = 0; i < keys.length; i++) {
            if (keys.length - i > 1) {
                str = new StringBuilder(String.valueOf(str)).append(keys[i]).append("=? and ").toString();
            } else {
                str = new StringBuilder(String.valueOf(str)).append(keys[i]).append("=?").toString();
            }
            selections[i] = values.getAsString(keys[i]);
        }
        Cursor cursor = resolver.query(uri, null, str, selections, null);
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            if (values.containsKey(DevicesSetColumns.DEVICES_NICK)) {
                values.remove(DevicesSetColumns.DEVICES_NICK);
            }
            resolver.update(uri, values, str, selections);
        } else {
            resolver.insert(uri, values);
        }
        cursor.close();
        return true;
    }

    public static boolean undateData(Context context, Uri uri, ContentValues values, String... keys) {
        ContentResolver resolver = context.getContentResolver();
        String str = null;
        String[] selections = null;
        if (keys.length > 0) {
            str = new String();
            selections = new String[keys.length];
        }
        for (int i = 0; i < keys.length; i++) {
            if (keys.length - i > 1) {
                str = new StringBuilder(String.valueOf(str)).append(keys[i]).append("=? and ").toString();
            } else {
                str = new StringBuilder(String.valueOf(str)).append(keys[i]).append("=?").toString();
            }
            selections[i] = values.getAsString(keys[i]);
        }
        resolver.update(uri, values, str, selections);
        return true;
    }
}

