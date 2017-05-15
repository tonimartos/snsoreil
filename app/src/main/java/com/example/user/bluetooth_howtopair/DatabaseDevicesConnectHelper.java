package com.example.user.bluetooth_howtopair;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/* compiled from: DeviceContentProvider */
class DatabaseDevicesConnectHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "devices.db";
    private static final int DATABASE_VERSION = 1;

    public DatabaseDevicesConnectHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE devices (_id INTEGER PRIMARY KEY,name TEXT,nick TEXT,mac TEXT,version TEXT,img BLOB,online INTEGER,type INTEGER,id1 TEXT,ir1 INTEGER,ty1 INTEGER,tw1 INTEGER,dl1 INTEGER,distype1 INTEGER,id2 TEXT,ir2 INTEGER,ty2 INTEGER,tw2 INTEGER,dl2 INTEGER,distype2 INTEGER,id3 TEXT,ir3 INTEGER,ty3 INTEGER,tw3 INTEGER,dl3 INTEGER,distype3 INTEGER,id4 TEXT,ir4 INTEGER,ty4 INTEGER,tw4 INTEGER,dl4 INTEGER,distype4 INTEGER,lasttime TEXT);");
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS devices");
        onCreate(db);
    }
}

