package com.example.user.bluetooth_howtopair;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.media.TransportMediator;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.example.user.bluetooth_howtopair.handlers.BluetoothDeviceModel;
import com.example.user.bluetooth_howtopair.handlers.CacheMessageManager;
import com.example.user.bluetooth_howtopair.handlers.CacheMessageManager.CacheMessageListener;
import com.example.user.bluetooth_howtopair.handlers.DataUnit;
import com.example.user.bluetooth_howtopair.handlers.ExampleApplication;
import com.example.user.bluetooth_howtopair.handlers.IOHandler;
import com.example.user.bluetooth_howtopair.handlers.IOHandler.IOHandlerListener;
import com.example.user.bluetooth_howtopair.handlers.MessageManager;
import com.example.user.bluetooth_howtopair.handlers.Tyre;
import com.example.user.bluetooth_howtopair.utils.ConfigParams;
import com.example.user.bluetooth_howtopair.utils.Constants;
import com.example.user.bluetooth_howtopair.utils.ServiceConstants;
import com.example.user.bluetooth_howtopair.utils.UtilsConfig;

import java.util.List;
import java.util.UUID;

import static android.bluetooth.BluetoothGatt.GATT_SUCCESS;
import static android.bluetooth.BluetoothProfile.STATE_CONNECTED;
import static android.bluetooth.BluetoothProfile.STATE_DISCONNECTED;

public class BluetoothMultiService extends Service implements CacheMessageListener, IOHandlerListener {

    public static final String ACTIONCONNECT = "com.example.user.bluetooth_howtopair.actionconnect";
    public static final String ACTIONDISCONNECT = "com.example.user.bluetooth_howtopair.actiondisconnect";
    public static final String ACTIONSTARTSEARCH = "com.example.user.bluetooth_howtopair.actionstartsearch";
    public static final String ACTIONSTOPSEARCH = "com.example.user.bluetooth_howtopair.actionstopsearch";
    public static final String ACTIONWRITE = "com.example.user.bluetooth_howtopair.actionwrite";
    private static final int ORDER_QUERYSET = 1;
    public static boolean isConnect;
    public static boolean mScanning;
    public static final String TAG;
    public static final UUID UUID_HEART_RATE_MEASUREMENT;
    private final Messenger messenger;
    private BluetoothGattCharacteristic notifyCharacteristic;
    private BluetoothGattCharacteristic writeCharacteristic;
    private CacheMessageManager manager;
    private IOHandler ioHandler;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothDevice bluetoothDevice;
    private BluetoothGattCharacteristic mWriteCharacteristic;
    private IBinder mBinder;
    private BluetoothGatt mBluetoothGatt;
    private BluetoothGattCallback connectionCallback;
    private static BluetoothMultiService instance;
    BroadcastReceiver receiver;

    public BluetoothMultiService() {
        /*this.mBinder = new HomeBinder();
        this.hasinit = false;
        this.mLeScanCallback = new C00973();*/
        this.messenger = new Messenger();
        this.notifyCharacteristic = null;
        this.connectionCallback = new BluetoothConnectionCallback();
        this.bluetoothAdapter = null;
        this.receiver = new Receiver();
    }



    public class HomeBinder extends Binder {
        public BluetoothMultiService getService() {
            return BluetoothMultiService.this;
        }
    }

    public void onCreate() {
        super.onCreate();
        instance = this;
        this.manager = new CacheMessageManager(this);
        this.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
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
        isConnect = false;
        UUID_HEART_RATE_MEASUREMENT = UUID.fromString(Constants.HEART_RATE_MEASUREMENT);
    }

    protected synchronized void receiveData(DataUnit dataUnit) {
        isConnect = true;
        this.ioHandler.handler(dataUnit);
    }

    public void writeBytes(byte[] data) {
        this.manager.readyMessage(data);
    }

    protected void disconnectDevices(BluetoothDeviceModel bluetoothDeviceModel) {
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
        return this;
    }

    @Override
    public void onSetPaLarge() {

    }

    @Override
    public void onToast(int i) {
        Toast.makeText(this, i, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onToast(String str) {

    }

    @Override
    public void queryId() {
        writeBytes(UtilsConfig.chatOrders(Constants.QUERYID));
    }

    protected void onMessage(String message) {
        if (!TextUtils.isEmpty(message)) {
        }
    }

    public void connectDevices(BluetoothDeviceModel modelDevice, boolean sure) {
        BluetoothDevice device = this.bluetoothAdapter.getRemoteDevice(modelDevice.getAddress());
        if (this.mBluetoothGatt != null) {
            if (!this.mBluetoothGatt.connect()) {
                this.mBluetoothGatt.disconnect();
                this.mBluetoothGatt.close();
                this.mBluetoothGatt = null;
            } else if (modelDevice.getAddress().equals(this.mBluetoothGatt.getDevice().getAddress())) {
                if (!sure) {
                    onToast((int) R.string.dialog7);
                    return;
                }
                return;
            }
        }
        this.mBluetoothGatt = device.connectGatt(this, false, this.connectionCallback);
    }

    protected void setConnectState(BluetoothGatt gatt, int state) {
        BluetoothDeviceModel deviceModel = new BluetoothDeviceModel(gatt.getDevice());

        Intent intent = new Intent(ServiceConstants.CONNECTSTATE_CHANGE);
        intent.putExtra(Constants.DEVICES, deviceModel);
        intent.putExtra(Constants.CONNECTSTATE, state);
        sendBroadcast(intent);

        switch (state) {
            case 1 /*1*/:
                isConnect = true;
                break;
            case 2 /*2*/:
                isConnect = true;
                break;
            case 3 /*3*/:
                isConnect = false;
                break;
                //isConnect = true;
            case 4 /*4*/:
                isConnect = false;
                //this.writeCharacteristic = null;
                //this.mBluetoothGatt = null;
                //TODO deleteDeviceFromDB();
                if (!mScanning && this.mBluetoothGatt != null) {
                    //findDevices(true);
                }
                break;
            default:
                break;
        }
    }

    class Receiver extends BroadcastReceiver {

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(MessageManager.SENDDATA)) {
                BluetoothMultiService.this.writeBytes(intent.getByteArrayExtra("data"));
            } else if (action.equals(BluetoothMultiService.ACTIONCONNECT)) {
                BleDevice bleDevice = (BleDevice) intent.getSerializableExtra("device");
                if (bleDevice == null) {
                    //BluetoothMultiService.this.connectDevices(false);
                } else {
                    BluetoothMultiService.this.connectDevices(new BluetoothDeviceModel(bleDevice), false);
                }
            } else if (action.equals(BluetoothMultiService.ACTIONDISCONNECT)) {
                BluetoothMultiService.this.disconnectDevices(new BluetoothDeviceModel((BleDevice) intent.getSerializableExtra("device")));
            } else if (action.equals(BluetoothMultiService.ACTIONSTOPSEARCH)) {
                //BluetoothMultiService.this.stopFindDevices();
            } else if (action.equals(BluetoothMultiService.ACTIONSTARTSEARCH)) {
                //BluetoothMultiService.this.findDevices(true);
            }
        }
    }

    class BluetoothConnectionCallback extends BluetoothGattCallback {

        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (newState == STATE_CONNECTED) {
                setConnectState(gatt, BluetoothMultiService.ORDER_QUERYSET);
                if (mBluetoothGatt != null) {
                    mBluetoothGatt.discoverServices();
                }
                Log.i(BluetoothMultiService.TAG, "Connected to GATT server.");
            } else if (newState == STATE_DISCONNECTED) {
                BluetoothMultiService.this.setConnectState(gatt, TransportMediator.FLAG_KEY_MEDIA_PLAY);
                Log.i(BluetoothMultiService.TAG, "Disconnected from GATT server.");
            }
        }

        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == GATT_SUCCESS) {
                Log.w(BluetoothMultiService.TAG, "onServicesDiscovered success: " + status);
                onBluetoothServiceFound(gatt.getServices(), gatt);
                return;
            }
            Log.w(BluetoothMultiService.TAG, "onServicesDiscovered received: " + status);
        }

        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            if (status == GATT_SUCCESS) {
                byte[] arrayOfByte = characteristic.getValue();
                if (arrayOfByte == null || arrayOfByte.length <= 0) {
                    Log.i(BluetoothMultiService.TAG, "message is null");
                    return;
                }
                int length = arrayOfByte.length;
                Log.i("length", Integer.toString(length));
                messenger.obtainMessage(BluetoothMultiService.RECEIVEDATA, new DataUnit(gatt.getDevice().getAddress(), arrayOfByte)).sendToTarget();

                Log.i("RECEIVE DATA", String.valueOf(messenger));
            }
        }

        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            byte[] arrayOfByte = characteristic.getValue();
            if (arrayOfByte == null || arrayOfByte.length <= 0) {
                Log.i(BluetoothMultiService.TAG, "message is null");
                return;
            }
            int length = arrayOfByte.length;
            Log.i("length", Integer.toString(length));

            DataUnit dataUnit = new DataUnit(gatt.getDevice(), arrayOfByte);

            messenger.obtainMessage(BluetoothMultiService.RECEIVEDATA, dataUnit).sendToTarget();
            //receiveData(dataUnit);

            Log.i("RECEIVE DATA", String.valueOf(messenger.obtainMessage(103, dataUnit)));
        }

        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            Log.i(BluetoothMultiService.TAG, "onCharacteristicWrite");
            super.onCharacteristicWrite(gatt, characteristic, status);
        }

        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            Log.i(BluetoothMultiService.TAG, "onDescriptorRead");
            super.onDescriptorRead(gatt, descriptor, status);
        }

        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
             Log.i(BluetoothMultiService.TAG, "onDescriptorWrite");
            /*super.onDescriptorWrite(gatt, descriptor, status);
            if (!BluetoothMultiService.this.hasinit) {
                BluetoothMultiService.this.messenger.sendEmptyMessage(BluetoothMultiService.INITORDER);
            }*/
        }
    }

    public void onBluetoothServiceFound(List<BluetoothGattService> bluetoothServices, BluetoothGatt gatt) {
        if (bluetoothServices != null) {
            for (BluetoothGattService gattService : bluetoothServices) {
                for (BluetoothGattCharacteristic gattCharacteristic : gattService.getCharacteristics()) {

                    if (gattCharacteristic.getUuid().toString().equalsIgnoreCase(Constants.BLUETOOTH_WRITEUUID.toString())) {

                        this.mWriteCharacteristic = gattCharacteristic;
                        this.mWriteCharacteristic.setWriteType(2);

                        //characteristicWrite(gatt, gattCharacteristic);
                        //characteristicRead(gatt, gattCharacteristic);
                        saveConnectedDevice(this.mWriteCharacteristic, gatt);

                    } else if (gattCharacteristic.getUuid().toString().equalsIgnoreCase(Constants.BLUETOOTH_UUID.toString())) {

                        this.messenger.obtainMessage(ONMESSAGE, new BluetoothDeviceModel(gatt.getDevice())).sendToTarget();

                    } else if (gattCharacteristic.getUuid().toString().equalsIgnoreCase(Constants.BLUETOOTH_READUUID.toString())) {
                        characteristicRead(gatt, gattCharacteristic);
                        this.messenger.obtainMessage(ONMESSAGE, new BluetoothDeviceModel(gatt.getDevice())).sendToTarget();
                    }
                }
            }
        }
    }

    private void characteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic gattCharacteristic) {
        this.writeCharacteristic = gattCharacteristic;
        this.writeCharacteristic.setWriteType(2);
        Log.d("peneloco", "onCharacteristicWrite");
        //saveConnectedDevice(this.writeCharacteristic, gatt);
    }

    private void characteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic gattCharacteristic) {
        this.notifyCharacteristic = gattCharacteristic;

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if ((this.notifyCharacteristic.getProperties() | 16) > 0) {
            setCharacteristicNotification(this.notifyCharacteristic, true);
        }
        try {
            Thread.sleep(100);
        } catch (InterruptedException e2) {
            e2.printStackTrace();
        }
        //this.messenger.obtainMessage(ONMESSAGE, new StringBuilder(String.valueOf(gatt.getDevice().getAddress())).append("  READ UUID Service").toString()).sendToTarget();
        this.messenger.obtainMessage(ONMESSAGE, new BluetoothDeviceModel(gatt.getDevice())).sendToTarget();
    }

    public void setCharacteristicNotification(BluetoothGattCharacteristic characteristic, boolean enabled) {
        if (this.bluetoothAdapter == null || this.mBluetoothGatt == null) {
            Log.v(TAG, "BluetoothAdapter not initialized");
            return;
        }
        this.mBluetoothGatt.setCharacteristicNotification(characteristic, enabled);
        if (enabled) {
            Log.v("ble", "UUID" + characteristic.getUuid().toString() + "True");
        }
        if (UUID_HEART_RATE_MEASUREMENT.equals(characteristic.getUuid())) {
            BluetoothGattDescriptor descriptor = characteristic.getDescriptor(UUID.fromString(Constants.CLIENT_CHARACTERISTIC_CONFIG));
            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            this.mBluetoothGatt.writeDescriptor(descriptor);
        }
    }

    private void saveConnectedDevice(BluetoothGattCharacteristic writeCharacteristic, BluetoothGatt gatt) {
        this.mBluetoothGatt = gatt;
        this.bluetoothDevice = gatt.getDevice();
        this.writeCharacteristic = writeCharacteristic;

        messenger.obtainMessage(CONNECTTED, new BluetoothDeviceModel(gatt.getDevice())).sendToTarget();
        //messenger.obtainMessage(RECEIVEDATA, new BluetoothDeviceModel(gatt.getDevice())).sendToTarget();
        /*
        ContentValues values = new ContentValues();
        values.put(DevicesSetColumns.DEVICES_SYSTEMID, gatt.getDevice().getAddress());

        Uri uri = DevicesColumns.CONTENT_URI;
        String[] strArr = new String[ORDER_QUERYSET];
        strArr[INITORDER] = DevicesSetColumns.DEVICES_SYSTEMID;
        if (!ProviderUtils.existData(this, uri, values, strArr)) {
            ProviderUtils.deleteData(this, DevicesColumns.CONTENT_URI, null, new String[INITORDER]);
        }
        values.put(DevicesSetColumns.DEVICES_NAME, gatt.getDevice().getName());
        values.put(DevicesSetColumns.DEVICES_NICK, gatt.getDevice().getName());
        values.put(DevicesSetColumns.DEVICES_ONLINE, Boolean.valueOf(true));
        values.put(DevicesSetColumns.DEVICES_LASTTIME, Long.valueOf(System.currentTimeMillis()));
        uri = DevicesColumns.CONTENT_URI;
        strArr = new String[ORDER_QUERYSET];
        strArr[INITORDER] = DevicesSetColumns.DEVICES_SYSTEMID;

        ProviderUtils.insertData(this, uri, values, strArr);
        ExampleApplication.getInstance().setValue(ConfigParams.LASTBINDNOTIFYMAC, gatt.getDevice().getAddress());

        this.messenger.obtainMessage(CONNECTTED, new BluetoothDeviceVo(gatt.getDevice())).sendToTarget();*/
    }

    public static final int CLOSESEARCH = 101;
    public static final int CONNECTDEVICE = 102;
    public static final int CONNECTTED = 106;
    public static final int RECEIVEDATA = 103;
    public static final int ONMESSAGE = 105;
    protected static final int DELAYTASKMESSAGE = 107;

    class Messenger extends Handler {

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case BluetoothMultiService.CLOSESEARCH /*101*/:
                    //BluetoothMultiService.this.stopFindDevices();
                    int i = 0;
                case BluetoothMultiService.CONNECTDEVICE /*102*/:
                    BluetoothMultiService.this.connectDevices(new BluetoothDeviceModel(null, msg.obj.toString()), false);
                case BluetoothMultiService.RECEIVEDATA /*103*/:
                    BluetoothMultiService.this.receiveData((DataUnit)msg.obj);
                case BluetoothMultiService.ONMESSAGE /*105*/:
                    BluetoothMultiService.this.onMessage(msg.obj.toString());
                case BluetoothMultiService.CONNECTTED /*106*/:
                    BluetoothMultiService.this.onConnectDevice((BluetoothDeviceModel)msg.obj);
                case BluetoothMultiService.DELAYTASKMESSAGE /*107*/:
                    int j =0;
                    //BluetoothMultiService.this.DelayMessage(msg.arg1);
                default:
            }
        }
    }

    protected void onConnectDevice(BluetoothDeviceModel device) {
        Log.d("peneloco", "onConnectDevice");
        onToast((int) R.string.dialog7);
        queryId();
        messenger.sendMessageDelayed(messenger.obtainMessage(DELAYTASKMESSAGE, ORDER_QUERYSET, -1), DELAYTASKMESSAGE);
    }

    public void NextMessage(Object data) {
        if (this.mWriteCharacteristic == null || this.mBluetoothGatt == null) {
            sendBroadcast(new Intent(ServiceConstants.NOCONNECTION));
            return;
        }
        this.mWriteCharacteristic.setValue((byte[]) data);
        this.mBluetoothGatt.writeCharacteristic(this.mWriteCharacteristic);
    }


    public void getTyreData (Tyre tyre, int id){
        Integer irh = ExampleApplication.getInstance().getIntValue(ConfigParams.HIGHPA);
        Integer irl = ExampleApplication.getInstance().getIntValue(ConfigParams.LOWPA);
        Integer itw = ExampleApplication.getInstance().getIntValue(ConfigParams.HIGHTW);
        Integer idl = ExampleApplication.getInstance().getIntValue(ConfigParams.LOWDL);

        Log.d("Tyre data", irh.toString() + irl.toString() + itw.toString() + idl.toString());
    }
}
