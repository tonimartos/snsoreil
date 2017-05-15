package com.example.user.bluetooth_howtopair;

import android.bluetooth.BluetoothClass;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothAdapter.LeScanCallback;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat.WearableExtender;
import android.support.v4.media.TransportMediator;
import android.support.v4.widget.CursorAdapter;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import java.util.List;
import java.util.UUID;

import com.example.user.bluetooth_howtopair.CacheMessageManager.CacheMessageListener;
import com.example.user.bluetooth_howtopair.IOHandler.IOHandlerListener;
import com.example.user.bluetooth_howtopair.DevicesSetProvider.DevicesSetColumns;
import com.example.user.bluetooth_howtopair.DevicesProvider.DevicesColumns;

public class BluetoothMultiService extends Service implements CacheMessageListener, IOHandlerListener {

    public static final String ACTIONCONNECT = "com.example.user.bluetooth_howtopair.actionconnect";
    public static final String ACTIONDISCONNECT = "com.example.user.bluetooth_howtopair.actiondisconnect";
    public static final String ACTIONSTARTSEARCH = "com.example.user.bluetooth_howtopair.actionstartsearch";
    public static final String ACTIONSTOPSEARCH = "com.example.user.bluetooth_howtopair.actionstopsearch";
    public static final String ACTIONWRITE = "com.example.user.bluetooth_howtopair.actionwrite";
    public static boolean isconnect;
    public static boolean mScanning;
    public static final String TAG;
    public static final UUID UUID_HEART_RATE_MEASUREMENT;
    private CacheMessageManager manager;
    private IOHandler ioHandler;
    private BluetoothAdapter adapter;
    private IBinder mBinder;
    private BluetoothGatt mBluetoothGatt;
    private BluetoothGattCallback mGattCallback;
    private static BluetoothMultiService instance;
    BroadcastReceiver receiver;

    public void onCreate() {
        super.onCreate();
        instance = this;
        this.manager = new CacheMessageManager(this);
        this.adapter = BluetoothAdapter.getDefaultAdapter();
        this.ioHandler = new IOHandler(this);
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTIONCONNECT);
        filter.addAction(ACTIONDISCONNECT);
        filter.addAction(ACTIONWRITE);
        filter.addAction(ACTIONSTOPSEARCH);
        filter.addAction(ACTIONSTARTSEARCH);
        filter.addAction(MessageManager.SENDDATA);
        filter.addAction("android.bluetooth.adapter.action.SCAN_MODE_CHANGED");
        filter.addAction("android.bluetooth.adapter.action.DISCOVERY_FINISHED");
        filter.addAction("android.bluetooth.adapter.action.DISCOVERY_STARTED");
        registerReceiver(this.receiver, filter);
    }

    static {
        TAG = BluetoothMultiService.class.getSimpleName();
        mScanning = false;
        isconnect = false;
        UUID_HEART_RATE_MEASUREMENT = UUID.fromString(Constants.HEART_RATE_MEASUREMENT);
    }

    protected synchronized void receiveData(DataUnit msg) {
        isconnect = true;
        this.ioHandler.handler(msg);
    }

    public void writeBytes(byte[] data) {
        this.manager.readyMessage(data);
    }

    protected void disconnectDevices(BluetoothDeviceVo bluetoothDeviceVo) {
        if (this.mBluetoothGatt != null) {
            this.mBluetoothGatt.disconnect();
            if (this.mBluetoothGatt != null) {
                this.mBluetoothGatt.close();
            }
            this.mBluetoothGatt = null;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public Context getContext() {
        return null;
    }

    @Override
    public void onSetPaLarge() {

    }

    @Override
    public void onToast(int i) {

    }

    @Override
    public void onToast(String str) {

    }

    @Override
    public void queryId() {
        writeBytes(UtilsConfig.chatOrders(Constants.QUERYID));
    }

    @Override
    public void NextMessage(Object obj) {

    }
}
