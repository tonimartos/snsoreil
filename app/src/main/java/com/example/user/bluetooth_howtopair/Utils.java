package com.example.user.bluetooth_howtopair;

import android.content.Context;
import android.support.v4.app.NotificationCompat;
import android.support.v4.media.TransportMediator;
import android.support.v4.widget.CursorAdapter;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

public class Utils {
    public static final int BASEHIGH = 28;
    public static final int BASELOW = 15;
    public static final int BASETEMPERATURE = 70;
    public static final byte HEAD1 = (byte) -4;
    public static final String UNINT1 = "PSI";
    public static final String UNINT2 = "Kpa";

    public static String getLT(Context context, int id) {
        switch (id) {
            case CursorAdapter.FLAG_AUTO_REQUERY /*1*/:
                return context.getString(R.string.carlunzihigh1);
            case CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER /*2*/:
                return context.getString(R.string.carlunzihigh2);
            case NotificationCompat.WearableExtender.SIZE_MEDIUM /*3*/:
                return context.getString(R.string.carlunzihigh3);
            case TransportMediator.FLAG_KEY_MEDIA_PLAY /*4*/:
                return context.getString(R.string.carlunzihigh4);
            default:
                return context.getString(R.string.carlunzihigh1);
        }
    }

    public static String getHashMap(HashMap<String, String> titlecon) {
        String title = new String();
        int id = 0;
        for (Map.Entry entry : titlecon.entrySet()) {
            String key = (String) entry.getKey();
            String val = (String) entry.getValue();
            if (id != 0) {
                title = new StringBuilder(String.valueOf(title)).append(",").append(val).append(key).toString();
            } else {
                title = new StringBuilder(String.valueOf(val)).append(key).toString();
            }
            id++;
        }
        return title;
    }

    public static String get2HashMap(HashMap<String, String> titlecon, boolean b) {
        String title = new String();
        int id = 0;
        for (Map.Entry entry : titlecon.entrySet()) {
            String key = (String) entry.getKey();
            String val = (String) entry.getValue();
            if (id != 0) {
                title = new StringBuilder(String.valueOf(title)).append(",").append(key).toString();
            } else {
                title = key;
            }
            id++;
        }
        return title;
    }

    public static String getTem(int progress) {
        int type = ExampleApplication.getInstance().getIntValue(ConfigParams.TEMPRAUNIT);
        int value = ExampleApplication.getInstance().getIntValue(ConfigParams.HIGHTW);
        String str = type == 0 ? "\u00b0C" : "\u00b0F";
        if (type == 0) {
            return new StringBuilder(String.valueOf(Integer.toString(value + BASETEMPERATURE))).append(str).toString();
        }
        return new StringBuilder(String.valueOf(Integer.toString((int) (32.0d + (((double) (value + BASETEMPERATURE)) * 1.8d))))).append(str).toString();
    }

    public static String getLowpa(int progress) {
        int type = ExampleApplication.getInstance().getIntValue(ConfigParams.YALIUNIT);
        int value = ExampleApplication.getInstance().getIntValue(ConfigParams.LOWPA);
        String str = "Bar";
        switch (type) {
            case MainActivity.OPENSET /*0*/:
                return new StringBuilder(String.valueOf(new DecimalFormat("0.0").format((((double) (value + BASELOW)) * 1.0d) / 10.0d))).append("Bar").toString();
            case CursorAdapter.FLAG_AUTO_REQUERY /*1*/:
                return new StringBuilder(String.valueOf(new DecimalFormat("0.0").format((((double) (value + BASELOW)) * 14.5d) / 10.0d))).append(UNINT1).toString();
            case CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER /*2*/:
                return new StringBuilder(String.valueOf(new DecimalFormat("0.0").format((((double) (value + BASELOW)) * 100.0d) / 10.0d))).append(UNINT2).toString();
            case NotificationCompat.WearableExtender.SIZE_MEDIUM /*3*/:
                return new StringBuilder(String.valueOf(new DecimalFormat("0.0").format((((double) (value + BASELOW)) * 1.02d) / 10.0d))).append("Kg").toString();
            default:
                return new StringBuilder(String.valueOf(new DecimalFormat("0.0").format((((double) (value + BASELOW)) * 1.0d) / 10.0d))).append("Bar").toString();
        }
    }

    public static String getHighpa(int progress) {
        int type = ExampleApplication.getInstance().getIntValue(ConfigParams.YALIUNIT);
        int value = ExampleApplication.getInstance().getIntValue(ConfigParams.HIGHPA);
        String str = "Bar";
        switch (type) {
            case MainActivity.OPENSET /*0*/:
                return new StringBuilder(String.valueOf(new DecimalFormat("0.0").format((((double) (value + BASEHIGH)) * 1.0d) / 10.0d))).append("Bar").toString();
            case CursorAdapter.FLAG_AUTO_REQUERY /*1*/:
                return new StringBuilder(String.valueOf(new DecimalFormat("0.0").format((((double) (value + BASEHIGH)) * 14.5d) / 10.0d))).append(UNINT1).toString();
            case CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER /*2*/:
                return new StringBuilder(String.valueOf(new DecimalFormat("0.0").format((((double) (value + BASEHIGH)) * 100.0d) / 10.0d))).append(UNINT2).toString();
            case NotificationCompat.WearableExtender.SIZE_MEDIUM /*3*/:
                return new StringBuilder(String.valueOf(new DecimalFormat("0.0").format((((double) (value + BASEHIGH)) * 1.02d) / 10.0d))).append("Kg").toString();
            default:
                return new StringBuilder(String.valueOf(new DecimalFormat("0.0").format((((double) (value + BASEHIGH)) * 1.0d) / 10.0d))).append("Bar").toString();
        }
    }

    public static String getTemValue(int value) {
        if (ExampleApplication.getInstance().getIntValue(ConfigParams.TEMPRAUNIT) == 0) {
            return Integer.toString(value);
        }
        return Integer.toString((int) (32.0d + (((double) value) * 1.8d)));
    }

    public static String getUnitTaiya() {
        String str = "Bar";
        switch (ExampleApplication.getInstance().getIntValue(ConfigParams.YALIUNIT)) {
            case MainActivity.OPENSET /*0*/:
                return "Bar";
            case CursorAdapter.FLAG_AUTO_REQUERY /*1*/:
                return UNINT1;
            case CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER /*2*/:
                return UNINT2;
            case NotificationCompat.WearableExtender.SIZE_MEDIUM /*3*/:
                return "Kg";
            default:
                return str;
        }
    }

    public static String getUnitTemp() {
        return ExampleApplication.getInstance().getIntValue(ConfigParams.TEMPRAUNIT) == 0 ? "\u00b0C" : "\u00b0F";
    }

    public static String getTaiyaValue(int value) {
        switch (ExampleApplication.getInstance().getIntValue(ConfigParams.YALIUNIT)) {
            case MainActivity.OPENSET /*0*/:
                return new DecimalFormat("0.0").format(((((double) value) * 1.0d) / 10.0d) * 1.0d);
            case CursorAdapter.FLAG_AUTO_REQUERY /*1*/:
                return new DecimalFormat("0.0").format(((((double) value) * 1.0d) / 10.0d) * 14.5d);
            case CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER /*2*/:
                return new DecimalFormat("0.0").format(((((double) value) * 1.0d) / 10.0d) * 100.0d);
            case NotificationCompat.WearableExtender.SIZE_MEDIUM /*3*/:
                return new DecimalFormat("0.0").format(((((double) value) * 1.0d) / 10.0d) * 1.02d);
            default:
                return new DecimalFormat("0.0").format((((double) value) * 1.0d) / 10.0d);
        }
    }
}

