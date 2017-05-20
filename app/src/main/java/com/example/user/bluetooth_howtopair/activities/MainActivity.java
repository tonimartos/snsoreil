package com.example.user.bluetooth_howtopair.activities;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.media.TransportMediator;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.user.bluetooth_howtopair.BluetoothMultiService;
import com.example.user.bluetooth_howtopair.handlers.ByteUtils;
import com.example.user.bluetooth_howtopair.utils.ConfigParams;
import com.example.user.bluetooth_howtopair.DeviceListAdapter;
import com.example.user.bluetooth_howtopair.DevicesProvider.DevicesColumns;
import com.example.user.bluetooth_howtopair.handlers.ExampleApplication;
import com.example.user.bluetooth_howtopair.utils.IOUtils;
import com.example.user.bluetooth_howtopair.R;
import com.example.user.bluetooth_howtopair.handlers.Tyre;
import com.example.user.bluetooth_howtopair.utils.Utils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;


public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{
    private static final String TAG = "MainActivity";
    public static final int OPENSET = 0;
    private static final int REQUEST_ENABLE_BT = 3;
    private EditText title;
    HashMap<String, String> titlecon;

    BluetoothAdapter mBluetoothAdapter;
    Button btnEnableDisable_Discoverable;

    public ArrayList<BluetoothDevice> mBTDevices = new ArrayList<>();

    public DeviceListAdapter mDeviceListAdapter;

    ListView lvNewDevices;
    private TextView unittaiya1;
    private TextView unittaiya2;
    private TextView unittaiya3;
    private TextView unittaiya4;
    private TextView unittemp1;
    private TextView unittemp2;
    private TextView unittemp3;
    private TextView unittemp4;
    private ImageView imageView1;
    private ImageView imageView2;
    private ImageView imageView3;
    private ImageView imageView4;
    private RelativeLayout rel1;
    private RelativeLayout rel2;
    private RelativeLayout rel3;
    private RelativeLayout rel4;
    private TextView tempra1;
    private TextView tempra2;
    private TextView tempra3;
    private TextView tempra4;
    private TextView barvalue1;
    private TextView barvalue2;
    private TextView barvalue3;
    private TextView barvalue4;


    // Create a BroadcastReceiver for ACTION_FOUND
    private final BroadcastReceiver mBroadcastReceiver1 = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // When discovery finds a device
            if (action.equals(mBluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, mBluetoothAdapter.ERROR);

                switch(state){
                    case BluetoothAdapter.STATE_OFF:
                        Log.d(TAG, "onReceive: STATE OFF");
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        Log.d(TAG, "mBroadcastReceiver1: STATE TURNING OFF");
                        break;
                    case BluetoothAdapter.STATE_ON:
                        Log.d(TAG, "mBroadcastReceiver1: STATE ON");
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        Log.d(TAG, "mBroadcastReceiver1: STATE TURNING ON");
                        break;
                }
            }
        }
    };

    /**
     * Broadcast Receiver for changes made to bluetooth states such as:
     * 1) Discoverability mode on/off or expire.
     */
    private final BroadcastReceiver mBroadcastReceiver2 = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED)) {

                int mode = intent.getIntExtra(BluetoothAdapter.EXTRA_SCAN_MODE, BluetoothAdapter.ERROR);

                switch (mode) {
                    //Device is in Discoverable Mode
                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE:
                        Log.d(TAG, "mBroadcastReceiver2: Discoverability Enabled.");
                        break;
                    //Device not in discoverable mode
                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE:
                        Log.d(TAG, "mBroadcastReceiver2: Discoverability Disabled. Able to receive connections.");
                        break;
                    case BluetoothAdapter.SCAN_MODE_NONE:
                        Log.d(TAG, "mBroadcastReceiver2: Discoverability Disabled. Not able to receive connections.");
                        break;
                    case BluetoothAdapter.STATE_CONNECTING:
                        Log.d(TAG, "mBroadcastReceiver2: Connecting....");
                        break;
                    case BluetoothAdapter.STATE_CONNECTED:
                        Log.d(TAG, "mBroadcastReceiver2: Connected.");
                        break;
                }

            }
        }
    };




    /**
     * Broadcast Receiver for listing devices that are not yet paired
     * -Executed by btnDiscover() method.
     */
    private BroadcastReceiver mBroadcastReceiver3 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            Log.d(TAG, "onReceive: ACTION FOUND.");

            if (action.equals(BluetoothDevice.ACTION_FOUND)){
                BluetoothDevice device = intent.getParcelableExtra (BluetoothDevice.EXTRA_DEVICE);
                mBTDevices.add(device);
                Log.d(TAG, "onReceive: " + device.getName() + ": " + device.getAddress());
                mDeviceListAdapter = new DeviceListAdapter(context, R.layout.device_adapter_view, mBTDevices);
                lvNewDevices.setAdapter(mDeviceListAdapter);
            }
        }
    };

    /**
     * Broadcast Receiver that detects bond state changes (Pairing status changes)
     */
    private final BroadcastReceiver mBroadcastReceiver4 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if(action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)){
                BluetoothDevice mDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                //3 cases:
                //case1: bonded already
                if (mDevice.getBondState() == BluetoothDevice.BOND_BONDED){
                    Log.d(TAG, "BroadcastReceiver: BOND_BONDED.");
                }
                //case2: creating a bone
                if (mDevice.getBondState() == BluetoothDevice.BOND_BONDING) {
                    Log.d(TAG, "BroadcastReceiver: BOND_BONDING.");
                }
                //case3: breaking a bond
                if (mDevice.getBondState() == BluetoothDevice.BOND_NONE) {
                    Log.d(TAG, "BroadcastReceiver: BOND_NONE.");
                }
            }
        }
    };


    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy: called.");
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver1);
        unregisterReceiver(mBroadcastReceiver2);
        unregisterReceiver(mBroadcastReceiver3);
        unregisterReceiver(mBroadcastReceiver4);
        //mBluetoothAdapter.cancelDiscovery();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button btnONOFF = (Button) findViewById(R.id.btnONOFF);
        Button btnRunFreshView = (Button) findViewById(R.id.launchInitial);
        //btnEnableDisable_Discoverable = (Button) findViewById(R.id.btnDiscoverable_on_off);
        lvNewDevices = (ListView) findViewById(R.id.lvNewDevices);
        mBTDevices = new ArrayList<>();

        //Broadcasts when bond state changes (ie:pairing)
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        registerReceiver(mBroadcastReceiver4, filter);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        lvNewDevices.setOnItemClickListener(MainActivity.this);


        btnONOFF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: enabling/disabling bluetooth.");
                enableDisableBT();
            }
        });


    }



    public void enableDisableBT(){
        if(mBluetoothAdapter == null){
            Log.d(TAG, "enableDisableBT: Does not have BT capabilities.");
        }
        if(!mBluetoothAdapter.isEnabled()){
            Log.d(TAG, "enableDisableBT: enabling BT.");
            Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(enableBTIntent);

            IntentFilter BTIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(mBroadcastReceiver1, BTIntent);
        }
        if(mBluetoothAdapter.isEnabled()){
            Log.d(TAG, "enableDisableBT: disabling BT.");
            mBluetoothAdapter.disable();

            IntentFilter BTIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(mBroadcastReceiver1, BTIntent);
        }

    }


    public void btnEnableDisable_Discoverable(View view) {
        Log.d(TAG, "btnEnableDisable_Discoverable: Making device discoverable for 300 seconds.");

        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
        startActivity(discoverableIntent);

        IntentFilter intentFilter = new IntentFilter(mBluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
        registerReceiver(mBroadcastReceiver2,intentFilter);

    }

    public void btnLaunch(View view){
        Log.d(TAG, "Clicked on Launching button.");
        onDataChange(null);
    }

    public void btnDiscover(View view) {
        Log.d(TAG, "btnDiscover: Looking for unpaired devices.");

        if(mBluetoothAdapter.isDiscovering()){
            mBluetoothAdapter.cancelDiscovery();
            Log.d(TAG, "btnDiscover: Canceling discovery.");

            //check BT permissions in manifest
            checkBTPermissions();

            mBluetoothAdapter.startDiscovery();
            IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(mBroadcastReceiver3, discoverDevicesIntent);
        }
        if(!mBluetoothAdapter.isDiscovering()){

            //check BT permissions in manifest
            checkBTPermissions();

            mBluetoothAdapter.startDiscovery();
            IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(mBroadcastReceiver3, discoverDevicesIntent);
        }
    }

    /**
     * This method is required for all devices running API23+
     * Android must programmatically check the permissions for bluetooth. Putting the proper permissions
     * in the manifest is not enough.
     *
     * NOTE: This will only execute on versions > LOLLIPOP because it is not needed otherwise.
     */
    private void checkBTPermissions() {
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP){
            int permissionCheck = this.checkSelfPermission("Manifest.permission.ACCESS_FINE_LOCATION");
            permissionCheck += this.checkSelfPermission("Manifest.permission.ACCESS_COARSE_LOCATION");
            if (permissionCheck != 0) {

                this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1001); //Any number
            }
        }else{
            Log.d(TAG, "checkBTPermissions: No need to check permissions. SDK version < LOLLIPOP.");
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        //first cancel discovery because its very memory intensive.
        mBluetoothAdapter.cancelDiscovery();

        Log.d(TAG, "onItemClick: You Clicked on a device.");
        String deviceName = mBTDevices.get(i).getName();
        String deviceAddress = mBTDevices.get(i).getAddress();

        Log.d(TAG, "onItemClick: deviceName = " + deviceName);
        Log.d(TAG, "onItemClick: deviceAddress = " + deviceAddress);

        //create the bond.
        //NOTE: Requires API 17+? I think this is JellyBean
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2){
            Log.d(TAG, "Trying to pair with " + deviceName);
            onDataChange(null);
            mBTDevices.get(i).createBond();
        }
    }

    protected void onResume() {
        super.onResume();
        onDataChange(null);
        /*if (this.homeBinder != null) {
            this.homeBinder.connectDevices(true);
        }*/
    }

    public void onDataChange(Uri uri) {

        EditText editText;
        int i;
        editText = this.title;
        i =  R.string.menu2en;
        //editText.setText(i);

        Cursor cursor = getContentResolver().query(DevicesColumns.CONTENT_URI, null, null, null, null);

        if (cursor != null) {
            Tyre tyre1 = IOUtils.getTyre(cursor, 1);
            Tyre tyre2 = IOUtils.getTyre(cursor, 2);
            Tyre tyre3 = IOUtils.getTyre(cursor, REQUEST_ENABLE_BT);
            Tyre tyre4 = IOUtils.getTyre(cursor, 4);
            freshView(tyre1, 1);
            freshView(tyre2, 2);
            freshView(tyre3, REQUEST_ENABLE_BT);
            freshView(tyre4, 4);
        }

        if (cursor != null) {
            cursor.close();
            this.unittaiya1.setText(Utils.getUnitTaiya());
            this.unittemp1.setText(Utils.getUnitTemp());
            this.unittaiya2.setText(Utils.getUnitTaiya());
            this.unittemp2.setText(Utils.getUnitTemp());
            this.unittaiya3.setText(Utils.getUnitTaiya());
            this.unittemp3.setText(Utils.getUnitTemp());
            this.unittaiya4.setText(Utils.getUnitTaiya());
            this.unittemp4.setText(Utils.getUnitTemp());
        }
    }

    private void freshView(Tyre tyre, int id) {
        String content;
        int res;
        if (id == 1) {
            if (this.titlecon.size() != 0) {
                this.titlecon.clear();
            }
        }
        int irh = ExampleApplication.getInstance().getIntValue(ConfigParams.HIGHPA);
        Log.i("HIGHPA", " --" + irh);
        int irl = ExampleApplication.getInstance().getIntValue(ConfigParams.LOWPA);
        int itw = ExampleApplication.getInstance().getIntValue(ConfigParams.HIGHTW);
        int idl = ExampleApplication.getInstance().getIntValue(ConfigParams.LOWDL);
        if (irl == 0) {
            irh = 15;
        }
        if (irh == 0) {
        }
        if (itw == 0) {
        }
        if (idl == 0) {
        }
        DecimalFormat df = new DecimalFormat("0.0");
        byte dd = (byte) tyre.getIr();
        int ir1 = ByteUtils.byteToInt(dd, 1);
        int ir2 = ByteUtils.byteToInt(dd, 2);
        int ir3 = ByteUtils.byteToInt(dd, REQUEST_ENABLE_BT);
        int ir4 = ByteUtils.byteToInt(dd, 4);
        if (ir1 == 1 && ir2 == 0) {
            String irt = getResources().getString(R.string.ir1);
            content = (String) this.titlecon.get(irt);
            if (TextUtils.isEmpty(content)) {
                content = Utils.getLT(this, id);
            } else {
                content = content.concat("\u3001" + Utils.getLT(this, id));
            }
            this.titlecon.put(irt, content);
        }
        if (ir1 == 0 && ir2 == 1) {
            String irt = getResources().getString(R.string.ir2);
            content = (String) this.titlecon.get(irt);
            if (TextUtils.isEmpty(content)) {
                content = Utils.getLT(this, id);
            } else {
                content = content.concat("\u3001" + Utils.getLT(this, id));
            }
            this.titlecon.put(irt, content);
        }
        if (ir1 == 1 && ir2 == 1) {
            String irt = getResources().getString(R.string.ir3);
            content = (String) this.titlecon.get(irt);
            if (TextUtils.isEmpty(content)) {
                content = Utils.getLT(this, id);
            } else {
                content = content.concat("\u3001" + Utils.getLT(this, id));
            }
            this.titlecon.put(irt, content);
        }
        if (ir3 == 1) {
            String irt = getResources().getString(R.string.ir4);
            content = (String) this.titlecon.get(irt);
            if (TextUtils.isEmpty(content)) {
                content = Utils.getLT(this, id);
            } else {
                content = content.concat("\u3001" + Utils.getLT(this, id));
            }
            this.titlecon.put(irt, content);
        }
        if (ir4 == 1) {
            String irt = getResources().getString(R.string.ir5);
            content = (String) this.titlecon.get(irt);
            if (TextUtils.isEmpty(content)) {
                content = Utils.getLT(this, id);
            } else {
                content = content.concat("\u3001" + Utils.getLT(this, id));
            }
            this.titlecon.put(irt, content);
        }
        if (id == 4) {
            EditText editText;
            int i;
            if (this.titlecon.size() == 0) {
                this.title.setTextColor(-1);
                if (BluetoothMultiService.isConnect) {
                    editText = this.title;
                    i = R.string.menu2en;
                    editText.setText(i);
                } else {
                    editText = this.title;
                    i = R.string.menu1en;
                    editText.setText(i);
                }
            } else {
                this.title.setTextColor(getResources().getColor(R.color.common_select_back7));
                if (BluetoothMultiService.isConnect) {
                    editText = this.title;
                    i = R.string.menu2en;
                    editText.setText(i);
                } else {
                    editText = this.title;
                    i = R.string.menu1en;
                    editText.setText(i);
                }
            }
        }
        int color = getResources().getColor(R.color.common_select_back);
        if (ir4 == 1 || ir3 == 1 || ((ir1 == 1 && ir2 == 0) || ((ir1 == 0 && ir2 == 1) || (ir1 == 1 && ir2 == 1)))) {
            color = getResources().getColor(R.color.common_select_back7);
        }/*
        if (tyre.getDl() >= 26) {
            res = R.drawable.battery_full3x;
        } else if (tyre.getDl() >= 18) {
            res = R.drawable.battery_high3x;
        } else if (tyre.getDl() >= 9) {
            res = R.drawable.battery_mid3x;
        } else {
            res = R.drawable.battery_small3x;
        }*/
        switch (id) {
            case CursorAdapter.FLAG_AUTO_REQUERY /*1*/:
                //this.imageView1.setImageResource(res);
                this.barvalue1.setText(Utils.getTaiyaValue(tyre.getTy()));
                this.tempra1.setText(Utils.getTemValue(tyre.getTw()));
                this.rel1.setBackgroundColor(color);
            case CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER /*2*/:
                //this.imageView2.setImageResource(res);
                this.barvalue2.setText(Utils.getTaiyaValue(tyre.getTy()));
                this.tempra2.setText(Utils.getTemValue(tyre.getTw()));
                this.rel2.setBackgroundColor(color);
            case REQUEST_ENABLE_BT /*3*/:
                //this.imageView3.setImageResource(res);
                this.barvalue3.setText(Utils.getTaiyaValue(tyre.getTy()));
                this.tempra3.setText(Utils.getTemValue(tyre.getTw()));
                this.rel3.setBackgroundColor(color);
            case TransportMediator.FLAG_KEY_MEDIA_PLAY /*4*/:
                //this.imageView4.setImageResource(res);
                this.barvalue4.setText(Utils.getTaiyaValue(tyre.getTy()));
                this.tempra4.setText(Utils.getTemValue(tyre.getTw()));
                this.rel4.setBackgroundColor(color);
            default:
        }
    }
}
