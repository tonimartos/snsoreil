package com.example.user.bluetooth_howtopair.activities;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.user.bluetooth_howtopair.BleDevice;
import com.example.user.bluetooth_howtopair.BluetoothMultiService;
import com.example.user.bluetooth_howtopair.DeviceAdapter;
import com.example.user.bluetooth_howtopair.DevicesProvider.DevicesColumns;
import com.example.user.bluetooth_howtopair.DevicesSetProvider.DevicesSetColumns;
import com.example.user.bluetooth_howtopair.R;
import com.example.user.bluetooth_howtopair.handlers.MessageManager;
import com.example.user.bluetooth_howtopair.utils.ProviderUtils;

import java.util.ArrayList;
import java.util.List;

public class DeviceListActivity extends OtherBaseActivity implements AdapterView.OnItemClickListener, View.OnClickListener {
    protected static final int END = 0;
    public static final int ONNotifyStatus = 0;
    public static final int ORDER_QUERY = 11;
    public static final int READRSSI = 107;
    private static final long SCAN_PERIOD = 10000;
    protected static final int TIMEOUT = 1;
    private BluetoothAdapter bluetoothAdapter;
    private List<BleDevice> pairedDevices;
    private List<BleDevice> unPairedDevices;
    private Handler handler;
    private DeviceAdapter pairedAdapter;
    private DeviceAdapter unPairedAdapter;
    private ListView unPairedDevicesList;
    private ListView pairedDevicesList;
    BluetoothAdapter.LeScanCallback mLeScanCallback;
    BroadcastReceiver receiver;
    private ShitConnection shitConnection;
    BluetoothMultiService homeBinder;

    class Receiver extends BroadcastReceiver {

        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("android.bluetooth.adapter.action.STATE_CHANGED")) {
                int state = intent.getIntExtra("android.bluetooth.adapter.extra.STATE", 10);
                int prestate = intent.getIntExtra("android.bluetooth.adapter.extra.PREVIOUS_STATE", 10);
                if (state == 12 && !BluetoothMultiService.mScanning) {
                    findDevices(true);
                }
            }
        }
    }

    class BAScanCallback implements BluetoothAdapter.LeScanCallback {

        class C00611 implements Runnable {
            private final /* synthetic */ BluetoothDevice val$device;
            private final /* synthetic */ int val$rssi;
            private final /* synthetic */ byte[] val$scanRecord;

            C00611(BluetoothDevice bluetoothDevice, int i, byte[] bArr) {
                this.val$device = bluetoothDevice;
                this.val$rssi = i;
                this.val$scanRecord = bArr;
            }

            public void run() {
                DeviceListActivity.this.addDevice(this.val$device, this.val$rssi, this.val$scanRecord);
            }
        }

        BAScanCallback() {
        }

        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            DeviceListActivity.this.handler.post(new C00611(device, rssi, scanRecord));
        }
    }

    class StopFindingDevices implements Runnable {
        StopFindingDevices() {
        }

        public void run() {
            DeviceListActivity.this.stopFindDevices();
        }
    }

    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_devicelist);

        this.bluetoothAdapter = null;
        this.handler = new Handler();
        this.receiver = new Receiver();
        this.mLeScanCallback = new BAScanCallback();
        this.shitConnection = new ShitConnection();
        this.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        this.pairedDevicesList = (ListView) findViewById(R.id.listView1);
        this.pairedDevicesList.setOnItemClickListener(this);
        this.unPairedDevicesList = (ListView) findViewById(R.id.listView2);
        this.unPairedDevicesList.setOnItemClickListener(this);
        this.unPairedDevices = new ArrayList();
        this.unPairedAdapter = new DeviceAdapter(this, this.unPairedDevices);
        this.unPairedDevicesList.setAdapter(this.unPairedAdapter);
        bindService(new Intent(this, BluetoothMultiService.class), shitConnection, BIND_AUTO_CREATE);
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.bluetooth.adapter.action.STATE_CHANGED");
        registerReceiver(this.receiver, filter);
    }

    class ShitConnection implements ServiceConnection {

        public void onServiceDisconnected(ComponentName name) {
            homeBinder = null;
        }

        public void onServiceConnected(ComponentName name, IBinder service) {
            homeBinder = ((BluetoothMultiService.HomeBinder) service).getService();
            //homeBinder.connectDevices(false);
        }
    }

    protected void onResume() {
        super.onResume();
        freshView();
    }

    protected void onPause() {
        super.onPause();
    }

    protected void onStart() {
        super.onStart();
        BluetoothMultiService.mScanning = false;
        findDevices(true);
    }

    protected void onStop() {
        super.onStop();
        stopFindDevices();
    }

    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(this.receiver);
    }

    protected void addDevice(BluetoothDevice device, int rssi, byte[] scanRecord) {
        ContentValues values = new ContentValues();
        values.put(DevicesSetColumns.DEVICES_SYSTEMID, device.getAddress());
        values.put(DevicesSetColumns.DEVICES_NICK, device.getName());
        values.put(DevicesSetColumns.DEVICES_NAME, device.getName());
        Uri uri = DevicesColumns.CONTENT_URI;
        String[] strArr = new String[TIMEOUT];
        strArr[ONNotifyStatus] = DevicesSetColumns.DEVICES_SYSTEMID;
        if (!ProviderUtils.existData(this, uri, values, strArr)) {
            int index = ONNotifyStatus;
            for (BleDevice dev : this.unPairedDevices) {
                if (dev.getMac().equals(device.getAddress())) {
                    this.unPairedDevices.remove(dev);
                    this.unPairedDevices.add(index, new BleDevice(device));
                    return;
                }
                index += TIMEOUT;
            }
            BleDevice bleDevice = new BleDevice(device);
            bleDevice.setConnect(false);
            bleDevice.setUuid(device.getAddress());
            this.unPairedDevices.add(bleDevice);
            this.unPairedAdapter.notifyDataSetChanged();
        } else if (!TextUtils.isEmpty(device.getName())) {
            String[] strArr2 = new String[TIMEOUT];
            strArr2[ONNotifyStatus] = device.getAddress();
            getContentResolver().update(DevicesColumns.CONTENT_URI, values, "mac=?", strArr2);
        }
    }

    public void findDevices(boolean isStick) {
        if (!this.bluetoothAdapter.isEnabled()) {
            displayToast((int) R.string.dialog6);
        } else if (!BluetoothMultiService.mScanning) {
            BluetoothMultiService.mScanning = true;
            if (isStick) {
                this.bluetoothAdapter.startLeScan(this.mLeScanCallback);
                return;
            }
            this.handler.postDelayed(new StopFindingDevices(), SCAN_PERIOD);
            this.bluetoothAdapter.startLeScan(this.mLeScanCallback);
        }
    }

    public void stopFindDevices() {
        BluetoothMultiService.mScanning = false;
        if (this.bluetoothAdapter.isEnabled()) {
            this.bluetoothAdapter.stopLeScan(this.mLeScanCallback);
        }
    }

    private boolean checkUUID(byte[] data) {
        return false;
    }

    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        BleDevice device = (BleDevice) view.getTag();
        if (parent.getId() == R.id.listView1) {
            MessageManager.getIntance().connectDevice(device);
        } else {
            MessageManager.getIntance().connectDevice(device);
        }
    }

    public void onClick(View v) {
    }

    public Context getContext() {
        return this;
    }

    public void onDataChange(Uri uri) {
        freshView();
    }

    protected void onSettingChange() {
        freshView();
    }

    private void freshView() {
        this.pairedDevices = new ArrayList();
        Cursor cursor = getContentResolver().query(DevicesColumns.CONTENT_URI, null, null, null, null);
        while (cursor != null) {
            BleDevice pairedDevice = new BleDevice();
            pairedDevice.setName(cursor.getString(cursor.getColumnIndex(DevicesSetColumns.DEVICES_NAME)));
            pairedDevice.setMac(cursor.getString(cursor.getColumnIndex(DevicesSetColumns.DEVICES_SYSTEMID)));
            pairedDevice.setUuid(cursor.getString(cursor.getColumnIndex(DevicesSetColumns.DEVICES_SYSTEMID)));
            pairedDevice.setConnect(true);
            for (BleDevice dev : this.unPairedDevices) {
                if (dev.getMac().equals(pairedDevice.getMac())) {
                    this.unPairedDevices.remove(dev);
                    this.unPairedAdapter.notifyDataSetChanged();
                    return;
                }
            }
            this.pairedDevices.add(pairedDevice);
            cursor.close();
        }
        this.pairedAdapter = new DeviceAdapter(this, this.pairedDevices);
        this.pairedDevicesList.setAdapter(this.pairedAdapter);
    }
}

