package com.example.user.bluetooth_howtopair;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import com.example.user.bluetooth_howtopair.MessageManager;
import com.example.user.bluetooth_howtopair.DevicesProvider.DevicesColumns;
import com.example.user.bluetooth_howtopair.DevicesSetProvider.DevicesSetColumns;
import com.example.user.bluetooth_howtopair.ProviderUtils;
import com.example.user.bluetooth_howtopair.BluetoothMultiService;
import com.example.user.bluetooth_howtopair.DeviceAdapter;
import com.example.user.bluetooth_howtopair.BleDevice;

public class DeviceListActivity extends OtherBaseActivity implements AdapterView.OnItemClickListener, View.OnClickListener {
    protected static final int END = 0;
    public static final int ONNotifyStatus = 0;
    public static final int ORDER_QUERY = 11;
    public static final int READRSSI = 107;
    private static final long SCAN_PERIOD = 10000;
    protected static final int TIMEOUT = 1;
    private BluetoothAdapter adapter;
    private List<BleDevice> devices1;
    private List<BleDevice> devices2;
    private Handler handler;
    private DeviceAdapter homeDevice1;
    private DeviceAdapter homeDevice2;
    private ListView homelist;
    private ListView listView1;
    BluetoothAdapter.LeScanCallback mLeScanCallback;
    BroadcastReceiver receiver;

    class C00601 extends BroadcastReceiver {
        C00601() {
        }

        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("android.bluetooth.adapter.action.STATE_CHANGED")) {
                int state = intent.getIntExtra("android.bluetooth.adapter.extra.STATE", 10);
                int prestate = intent.getIntExtra("android.bluetooth.adapter.extra.PREVIOUS_STATE", 10);
                if (state == 12 && !BluetoothMultiService.mScanning) {
                    DeviceListActivity.this.findDevices(true);
                }
            }
        }
    }

    class C00622 implements BluetoothAdapter.LeScanCallback {

        /* renamed from: com.changhewulian.ble.taiya.activity.DeviceListActivity.2.1 */
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

        C00622() {
        }

        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            DeviceListActivity.this.handler.post(new C00611(device, rssi, scanRecord));
        }
    }

    /* renamed from: com.changhewulian.ble.taiya.activity.DeviceListActivity.3 */
    class C00633 implements Runnable {
        C00633() {
        }

        public void run() {
            DeviceListActivity.this.stopFindDevices();
        }
    }

    public DeviceListActivity() {
        this.adapter = null;
        this.handler = new Handler();
        this.receiver = new C00601();
        this.mLeScanCallback = new C00622();
    }

    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_devicelist);
        this.adapter = BluetoothAdapter.getDefaultAdapter();
        this.listView1 = (ListView) findViewById(R.id.listView1);
        this.homelist = (ListView) findViewById(R.id.listView2);
        this.listView1.setOnItemClickListener(this);
        this.homelist.setOnItemClickListener(this);
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.bluetooth.adapter.action.STATE_CHANGED");
        registerReceiver(this.receiver, filter);
        this.devices2 = new ArrayList();
        this.homeDevice2 = new DeviceAdapter(this, this.devices2);
        this.homelist.setAdapter(this.homeDevice2);
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
            for (BleDevice dev : this.devices2) {
                if (dev.getMac().equals(device.getAddress())) {
                    this.devices2.remove(dev);
                    this.devices2.add(index, new BleDevice(device));
                    return;
                }
                index += TIMEOUT;
            }
            BleDevice bleDevice = new BleDevice(device);
            bleDevice.setConnect(false);
            bleDevice.setUuid(device.getAddress());
            this.devices2.add(bleDevice);
            this.homeDevice2.notifyDataSetChanged();
        } else if (!TextUtils.isEmpty(device.getName())) {
            String[] strArr2 = new String[TIMEOUT];
            strArr2[ONNotifyStatus] = device.getAddress();
            getContentResolver().update(DevicesColumns.CONTENT_URI, values, "mac=?", strArr2);
        }
    }

    public void findDevices(boolean isStick) {
        if (!this.adapter.isEnabled()) {
            displayToast((int) R.string.dialog6);
        } else if (!BluetoothMultiService.mScanning) {
            BluetoothMultiService.mScanning = true;
            if (isStick) {
                this.adapter.startLeScan(this.mLeScanCallback);
                return;
            }
            this.handler.postDelayed(new C00633(), SCAN_PERIOD);
            this.adapter.startLeScan(this.mLeScanCallback);
        }
    }

    public void stopFindDevices() {
        BluetoothMultiService.mScanning = false;
        if (this.adapter.isEnabled()) {
            this.adapter.stopLeScan(this.mLeScanCallback);
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
        this.devices1 = new ArrayList();
        Cursor cursor = getContentResolver().query(DevicesColumns.CONTENT_URI, null, null, null, null);
        while (cursor != null) {
            BleDevice bleDevice = new BleDevice();
            bleDevice.setName(cursor.getString(cursor.getColumnIndex(DevicesSetColumns.DEVICES_NAME)));
            bleDevice.setMac(cursor.getString(cursor.getColumnIndex(DevicesSetColumns.DEVICES_SYSTEMID)));
            bleDevice.setUuid(cursor.getString(cursor.getColumnIndex(DevicesSetColumns.DEVICES_SYSTEMID)));
            bleDevice.setConnect(true);
            for (BleDevice dev : this.devices2) {
                if (dev.getMac().equals(bleDevice.getMac())) {
                    this.devices2.remove(dev);
                    this.homeDevice2.notifyDataSetChanged();
                    return;
                }
            }
            this.devices1.add(bleDevice);
            cursor.close();
        }
        this.homeDevice1 = new DeviceAdapter(this, this.devices1);
        this.listView1.setAdapter(this.homeDevice1);
    }
}

