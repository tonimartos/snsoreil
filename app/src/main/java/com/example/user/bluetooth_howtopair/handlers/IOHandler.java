package com.example.user.bluetooth_howtopair.handlers;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.support.v4.media.TransportMediator;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.support.v4.widget.CursorAdapter;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.example.user.bluetooth_howtopair.DevicesSetProvider.DevicesSetColumns;
import com.example.user.bluetooth_howtopair.DevicesProvider.DevicesColumns;
import com.example.user.bluetooth_howtopair.R;
import com.example.user.bluetooth_howtopair.utils.ConfigParams;
import com.example.user.bluetooth_howtopair.utils.ProviderUtils;
import com.example.user.bluetooth_howtopair.utils.ServiceConstants;
import com.example.user.bluetooth_howtopair.utils.UtilsConfig;

public class IOHandler {
    byte[] cachebyte;
    int cachebytelength;
    private Context context;
    byte[] databyte;
    int databytelength;
    private int length;
    private IOHandlerListener listener;
    private String mac;
    private int order;
    private ContentResolver resolver;

    public interface IOHandlerListener {
        Context getContext();

        void onSetPaLarge();

        void onToast(int i);

        void onToast(String str);

        void queryId();
    }

    public IOHandler(IOHandlerListener service) {
        this.length = 0;
        this.databyte = new byte[0];
        this.databytelength = 0;
        this.cachebyte = new byte[0];
        this.cachebytelength = 0;
        this.listener = service;
        this.context = service.getContext();
        //this.resolver = this.context.getContentResolver();
    }

    public void handler(DataUnit data) {
        byte[] msg = data.getData();
        int lastindex = -1;
        for (int index = 0; index < msg.length; index++) {
            if (msg[index] == -4) {
                lastindex = index;
                byte[] items;
                DataUnit unit;
                if (index + 1 < msg.length) {
                    int value1 = getAbsValue(msg[index + 1]);
                    if ((index + value1) + 1 >= msg.length) {
                        items = new byte[(msg.length - index)];
                        System.arraycopy(msg, index, items, 0, items.length);
                        unit = new DataUnit();
                        unit.setData(items);
                        unit.setMac(data.getMac());
                        cashBytes(unit);
                        break;
                    }
                    items = new byte[(value1 + 2)];
                    System.arraycopy(msg, index, items, 0, items.length);
                    unit = new DataUnit();
                    unit.setData(items);
                    unit.setMac(data.getMac());
                    cashBytes(unit);
                } else {
                    items = new byte[1];
                    System.arraycopy(msg, index, items, 0, 1);
                    unit = new DataUnit();
                    unit.setData(items);
                    unit.setMac(data.getMac());
                    cashBytes(unit);
                    break;
                }
            }
        }
        if (lastindex == -1) {
            cashBytes(data);
        }
    }

    private void resetData() {
        this.cachebyte = new byte[0];
        this.cachebytelength = this.cachebyte.length;
    }

    private void cashBytes(DataUnit itemdata) {
        byte[] msg = itemdata.getData();
        this.mac = itemdata.getMac();
        if (msg[0] == -4) {
            reset();
        }
        this.databyte = UtilsConfig.uniteBytes(this.databyte, msg);
        if (this.databyte[0] != -4) {
            reset();
        } else if (this.databyte.length != 1) {
            getLength();
            if (this.databyte.length == this.length + 2) {
                byte[] item = new byte[(this.databyte.length - 1)];
                System.arraycopy(this.databyte, 0, item, 0, item.length);
                int value = item[0];
                for (int i = 1; i < item.length; i++) {
                    value ^= item[i];
                }
                if (((byte) value) != this.databyte[this.databyte.length - 1]) {
                    Toast.makeText(this.context, "\u6570\u636e\u6821\u9a8c\u9519\u8bef,\u975e\u6cd5\u5b57\u7b26", 0).show();
                    return;
                }
                this.order = this.databyte[2];
                //this.order = -59;
                switch (this.order) {
                    case -91:
                        if (ExampleApplication.getInstance().getBooleanValue(ConfigParams.OBDPOWERVOICE)) {
                            ExampleApplication.getInstance().setValue(ConfigParams.OBDPOWERVOICE, false);
                        }
                        sendBroadcastNotify(ServiceConstants.SETTING_CHANGE);
                    case -90:
                        sendBroadcastNotify(ServiceConstants.SETTING_CHANGE);
                    case -75:
                        if (!ExampleApplication.getInstance().getBooleanValue(ConfigParams.OBDPOWERVOICE)) {
                            ExampleApplication.getInstance().setValue(ConfigParams.OBDPOWERVOICE, true);
                        }
                        sendBroadcastNotify(ServiceConstants.SETTING_CHANGE);
                    case -74:
                        sendBroadcastNotify(ServiceConstants.SETTING_CHANGE);
                    case -59:
                        ExampleApplication.getInstance().setValue(ConfigParams.HIGHPA, getAbsValue(this.databyte[3]) - 28);
                        ExampleApplication.getInstance().setValue(ConfigParams.LOWPA, getAbsValue(this.databyte[4]) - 15);
                        ExampleApplication.getInstance().setValue(ConfigParams.LOWDL, getAbsValue(this.databyte[5]));
                        ExampleApplication.getInstance().setValue(ConfigParams.HIGHTW, getAbsValue(this.databyte[6]) - 70);
                        ExampleApplication.getInstance().setValue(ConfigParams.OBDPOWERVOICE, getAbsValue(this.databyte[7]) == 0);
                    case -43:
                        String serial = getSerial();
                        getDeviceIds();
                    case -27:
                        this.listener.onToast((int) R.string.taiyachangesuccess);
                        getChangeResult();
                    case -26:
                        this.listener.onToast((int) R.string.taiyachangefail);
                        sendBroadcastNotify(ServiceConstants.SETTING_CHANGE);
                    case CursorAdapter.FLAG_AUTO_REQUERY /*1*/:
                        getDeviceStatus();
                        Log.d("CASE 1", "Asking for device status to DB");
                    case CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER /*2*/:
                        getHignLowPa(ConfigParams.LOWPA);
                    case NotificationCompat.WearableExtender.SIZE_MEDIUM /*3*/:
                        getHignLowPa(ConfigParams.HIGHPA);
                    case TransportMediator.FLAG_KEY_MEDIA_PLAY /*4*/:
                        this.listener.onToast((int) R.string.taiyasetsuccess);
                        getDeviceId();
                        getHignLowPa(ConfigParams.LOWPA);
                        getHignLowPa(ConfigParams.HIGHPA);
                    case NotificationCompat.WearableExtender.SIZE_FULL_SCREEN /*5*/:
                        if (msg.length == 9) {
                            this.listener.onToast((int) R.string.toast6);
                        }
                        this.listener.onSetPaLarge();
                        sendBroadcastNotify(ServiceConstants.SETTING_CHANGE);
                        //case FragmentManagerImpl.ANIM_STYLE_FADE_EXIT /*6*/:
                    /*    String str = getId();
                        Intent intent = new Intent(ServiceConstants.STUDYSUCCESS);
                        intent.putExtra("id", str);
                        this.context.sendBroadcast(intent);*/
                    default:
                }
            }
        }
    }

    public String getId() {
        byte[] items = new byte[4];
        System.arraycopy(this.databyte, 4, items, 0, items.length);
        return UtilsConfig.decodeBytesToHexString(items);
    }

    private String getSerial() {
        try {
            return ((TelephonyManager) this.context.getSystemService("phone")).getDeviceId();
        } catch (Exception e) {
            return null;
        }
    }

    private void getDeviceIds() {
        byte[] items = new byte[4];
        System.arraycopy(this.databyte, 3, items, 0, items.length);
        Log.d("ID1", UtilsConfig.bytesToHex(items));
        System.arraycopy(this.databyte, 7, items, 0, items.length);
        Log.d("ID2", UtilsConfig.bytesToHex(items));
        System.arraycopy(this.databyte, 11, items, 0, items.length);
        Log.d("ID3", UtilsConfig.bytesToHex(items));
        System.arraycopy(this.databyte, 15, items, 0, items.length);
        Log.d("ID4", UtilsConfig.bytesToHex(items));
    }

    /*private void getDeviceIds() {
        ContentValues values = new ContentValues();
        byte[] items = new byte[4];
        System.arraycopy(this.databyte, 3, items, 0, items.length);
        values.put(DevicesSetColumns.DEVICES_ID1, UtilsConfig.bytesToHex(items));
        System.arraycopy(this.databyte, 7, items, 0, items.length);
        values.put(DevicesSetColumns.DEVICES_ID2, UtilsConfig.bytesToHex(items));
        System.arraycopy(this.databyte, 11, items, 0, items.length);
        values.put(DevicesSetColumns.DEVICES_ID3, UtilsConfig.bytesToHex(items));
        System.arraycopy(this.databyte, 15, items, 0, items.length);
        values.put(DevicesSetColumns.DEVICES_ID4, UtilsConfig.bytesToHex(items));
        values.put(DevicesSetColumns.DEVICES_SYSTEMID, this.mac);
        values.put(DevicesSetColumns.DEVICES_LASTTIME, Long.valueOf(System.currentTimeMillis()));
        ProviderUtils.insertData(this.context, DevicesColumns.CONTENT_URI, values, DevicesSetColumns.DEVICES_SYSTEMID);
    }*/

    private void getChangeResult() {
        this.listener.queryId();
    }

    private void getDeviceId() {
        byte[] items = new byte[4];
        System.arraycopy(this.databyte, 4, items, 0, items.length);
        switch (this.databyte[3]) {
            case 1 : //1
                Log.d("ID1", UtilsConfig.bytesToHex(items));
                break;
            case 2 : //2
                Log.d("ID2", UtilsConfig.bytesToHex(items));
                break;
            case 3 : //3
                Log.d("ID3", UtilsConfig.bytesToHex(items));
                break;
            case 4 : //4
                Log.d("ID4", UtilsConfig.bytesToHex(items));
                break;
        }
    }

    private void getHignLowPa(String key) {
        int i = 15;
        ExampleApplication instance;
        int absValue;
        switch (this.databyte[3]) {
            case 1:
                instance = ExampleApplication.getInstance();
                absValue = getAbsValue(this.databyte[4]);
                if (!key.equals(ConfigParams.LOWPA)) {
                    i = 28;
                }
                instance.setValue(key, absValue - i);
                break;
            case 2:
                instance = ExampleApplication.getInstance();
                absValue = getAbsValue(this.databyte[4]);
                if (!key.equals(ConfigParams.LOWPA)) {
                    i = 28;
                }
                instance.setValue(key, absValue - i);
                break;
            case 3:
                instance = ExampleApplication.getInstance();
                absValue = getAbsValue(this.databyte[4]);
                if (!key.equals(ConfigParams.LOWPA)) {
                    i = 28;
                }
                instance.setValue(key, absValue - i);
                break;
            case 4:
                instance = ExampleApplication.getInstance();
                absValue = getAbsValue(this.databyte[4]);
                if (!key.equals(ConfigParams.LOWPA)) {
                    i = 28;
                }
                instance.setValue(key, absValue - i);
                break;
        }
        sendBroadcastNotify(ServiceConstants.SETTING_CHANGE);
    }

    private void sendBroadcastNotify(String action) {
        this.context.sendBroadcast(new Intent(action));
    }

    private void getDeviceStatus() {
        switch (getAbsValue(this.databyte[3])) {
            case 1:
                Log.d("DEVICES_IR1", Integer.valueOf(getAbsValue(this.databyte[4])).toString());
                Log.d("DEVICES_TY1", Integer.valueOf(getAbsValue(this.databyte[5])).toString());
                Log.d("DEVICES_TW1", Integer.valueOf(getAbsValue(this.databyte[6])).toString());
                Log.d("DEVICES_DL1", Integer.valueOf(getAbsValue(this.databyte[7])).toString());
                Log.d("DEVICES_DISTYPE1", Integer.valueOf(getAbsValue(this.databyte[8])).toString());
                break;
            case 2:
                Log.d("DEVICES_IR1", Integer.valueOf(getAbsValue(this.databyte[4])).toString());
                Log.d("DEVICES_TY1", Integer.valueOf(getAbsValue(this.databyte[5])).toString());
                Log.d("DEVICES_TW1", Integer.valueOf(getAbsValue(this.databyte[6])).toString());
                Log.d("DEVICES_DL1", Integer.valueOf(getAbsValue(this.databyte[7])).toString());
                Log.d("DEVICES_DISTYPE1", Integer.valueOf(getAbsValue(this.databyte[8])).toString());
                break;
            case 3:
                Log.d("DEVICES_IR1", Integer.valueOf(getAbsValue(this.databyte[4])).toString());
                Log.d("DEVICES_TY1", Integer.valueOf(getAbsValue(this.databyte[5])).toString());
                Log.d("DEVICES_TW1", Integer.valueOf(getAbsValue(this.databyte[6])).toString());
                Log.d("DEVICES_DL1", Integer.valueOf(getAbsValue(this.databyte[7])).toString());
                Log.d("DEVICES_DISTYPE1", Integer.valueOf(getAbsValue(this.databyte[8])).toString());
                break;
            case 4:
                Log.d("DEVICES_IR1", Integer.valueOf(getAbsValue(this.databyte[4])).toString());
                Log.d("DEVICES_TY1", Integer.valueOf(getAbsValue(this.databyte[5])).toString());
                Log.d("DEVICES_TW1", Integer.valueOf(getAbsValue(this.databyte[6])).toString());
                Log.d("DEVICES_DL1", Integer.valueOf(getAbsValue(this.databyte[7])).toString());
                Log.d("DEVICES_DISTYPE1", Integer.valueOf(getAbsValue(this.databyte[8])).toString());
                break;
        }
    }

    private void getLength() {
        this.length = getAbsValue(this.databyte[1]);
    }

    private int getAbsValue(byte item) {
        byte value1 = item;
        if (value1 < 0) {
            return value1 + AccessibilityNodeInfoCompat.ACTION_NEXT_AT_MOVEMENT_GRANULARITY;
        }
        return value1;
    }

    private void reset() {
        this.databyte = new byte[0];
        this.databytelength = 0;
        this.cachebyte = new byte[0];
        this.cachebytelength = 0;
        this.length = 0;
    }

    public void setHandler(Handler dataCrackHandler) {
    }
}

