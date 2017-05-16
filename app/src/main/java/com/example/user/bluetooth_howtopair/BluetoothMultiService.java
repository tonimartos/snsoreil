package com.example.user.bluetooth_howtopair;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.util.UUID;

import com.example.user.bluetooth_howtopair.handlers.CacheMessageManager;
import com.example.user.bluetooth_howtopair.handlers.CacheMessageManager.CacheMessageListener;
import com.example.user.bluetooth_howtopair.handlers.BluetoothDeviceVo;
import com.example.user.bluetooth_howtopair.handlers.DataUnit;
import com.example.user.bluetooth_howtopair.handlers.IOHandler;
import com.example.user.bluetooth_howtopair.handlers.IOHandler.IOHandlerListener;
import com.example.user.bluetooth_howtopair.handlers.MessageManager;
import com.example.user.bluetooth_howtopair.utils.Constants;
import com.example.user.bluetooth_howtopair.utils.UtilsConfig;

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
