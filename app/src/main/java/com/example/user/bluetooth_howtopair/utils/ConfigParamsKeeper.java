package com.example.user.bluetooth_howtopair.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class ConfigParamsKeeper {
    public static final String KEEPERNAME = "configparams";
    private Context context;

    public ConfigParamsKeeper(Context context) {
        this.context = context;
    }

    public int getIntValue(String key) {
        return this.context.getSharedPreferences(KEEPERNAME, 0).getInt(key, 0);
    }

    public void setValue(String key, int value) {
        SharedPreferences.Editor editor = this.context.getSharedPreferences(KEEPERNAME, 0).edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public float getFloatValue(String key) {
        return this.context.getSharedPreferences(KEEPERNAME, 0).getFloat(key, 0.0f);
    }

    public long getLongValue(String key) {
        return this.context.getSharedPreferences(KEEPERNAME, 0).getLong(key, 0);
    }

    public void setValue(String key, float value) {
        SharedPreferences.Editor editor = this.context.getSharedPreferences(KEEPERNAME, 0).edit();
        editor.putFloat(key, value);
        editor.commit();
    }

    public void setValue(String key, long value) {
        SharedPreferences.Editor editor = this.context.getSharedPreferences(KEEPERNAME, 0).edit();
        editor.putLong(key, value);
        editor.commit();
    }

    public boolean getBooleanValue(String key) {
        return this.context.getSharedPreferences(KEEPERNAME, 0).getBoolean(key, false);
    }

    public void setValue(String key, boolean flag) {
        SharedPreferences.Editor editor = this.context.getSharedPreferences(KEEPERNAME, 0).edit();
        editor.putBoolean(key, flag);
        editor.commit();
    }

    public void setValue(String key, String value) {
        SharedPreferences.Editor editor = this.context.getSharedPreferences(KEEPERNAME, 0).edit();
        editor.putString(key, value);
        editor.commit();
    }

    public String getStringValue(String key) {
        return this.context.getSharedPreferences(KEEPERNAME, 0).getString(key, null);
    }

    public boolean hasKey(String key) {
        return this.context.getSharedPreferences(KEEPERNAME, 0).contains(key);
    }
}

