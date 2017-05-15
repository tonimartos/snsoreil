package com.example.user.bluetooth_howtopair;

import android.database.Cursor;
import android.support.v4.app.NotificationCompat;
import android.support.v4.media.TransportMediator;
import android.support.v4.widget.CursorAdapter;
import com.example.user.bluetooth_howtopair.DevicesSetProvider.DevicesSetColumns;

public class IOUtils {
    public static Tyre getTyre(Cursor cursor, int id1) {
        Tyre tyre = new Tyre();
        tyre.setId(id1);
        switch (id1) {
            case CursorAdapter.FLAG_AUTO_REQUERY /*1*/:
                tyre.setIr(cursor.getInt(cursor.getColumnIndex(DevicesSetColumns.DEVICES_IR1)));
                tyre.setDl(cursor.getInt(cursor.getColumnIndex(DevicesSetColumns.DEVICES_DL1)));
                tyre.setTw(cursor.getInt(cursor.getColumnIndex(DevicesSetColumns.DEVICES_TW1)));
                tyre.setTy(cursor.getInt(cursor.getColumnIndex(DevicesSetColumns.DEVICES_TY1)));
                tyre.setDis(cursor.getInt(cursor.getColumnIndex(DevicesSetColumns.DEVICES_DISTYPE1)));
                break;
            case CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER /*2*/:
                tyre.setIr(cursor.getInt(cursor.getColumnIndex(DevicesSetColumns.DEVICES_IR2)));
                tyre.setDl(cursor.getInt(cursor.getColumnIndex(DevicesSetColumns.DEVICES_DL2)));
                tyre.setTw(cursor.getInt(cursor.getColumnIndex(DevicesSetColumns.DEVICES_TW2)));
                tyre.setTy(cursor.getInt(cursor.getColumnIndex(DevicesSetColumns.DEVICES_TY2)));
                tyre.setDis(cursor.getInt(cursor.getColumnIndex(DevicesSetColumns.DEVICES_DISTYPE2)));
                break;
            case NotificationCompat.WearableExtender.SIZE_MEDIUM /*3*/:
                tyre.setIr(cursor.getInt(cursor.getColumnIndex(DevicesSetColumns.DEVICES_IR3)));
                tyre.setDl(cursor.getInt(cursor.getColumnIndex(DevicesSetColumns.DEVICES_DL3)));
                tyre.setTw(cursor.getInt(cursor.getColumnIndex(DevicesSetColumns.DEVICES_TW3)));
                tyre.setTy(cursor.getInt(cursor.getColumnIndex(DevicesSetColumns.DEVICES_TY3)));
                tyre.setDis(cursor.getInt(cursor.getColumnIndex(DevicesSetColumns.DEVICES_DISTYPE3)));
                break;
            case TransportMediator.FLAG_KEY_MEDIA_PLAY /*4*/:
                tyre.setIr(cursor.getInt(cursor.getColumnIndex(DevicesSetColumns.DEVICES_IR4)));
                tyre.setDl(cursor.getInt(cursor.getColumnIndex(DevicesSetColumns.DEVICES_DL4)));
                tyre.setTw(cursor.getInt(cursor.getColumnIndex(DevicesSetColumns.DEVICES_TW4)));
                tyre.setTy(cursor.getInt(cursor.getColumnIndex(DevicesSetColumns.DEVICES_TY4)));
                tyre.setDis(cursor.getInt(cursor.getColumnIndex(DevicesSetColumns.DEVICES_DISTYPE4)));
                break;
        }
        return tyre;
    }
}
