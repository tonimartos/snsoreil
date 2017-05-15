package com.example.user.bluetooth_howtopair;

import android.bluetooth.BluetoothDevice;
import com.example.user.bluetooth_howtopair.BleDevice;

import java.io.Serializable;

public class BluetoothDeviceVo implements Serializable {
    private static final long serialVersionUID = 1;
    private String address;
    private String name;

    public BluetoothDeviceVo(String name, String mac) {
        this.name = name;
        this.address = mac;
    }

    public BluetoothDeviceVo(BluetoothDevice device) {
        this.name = device.getName();
        this.address = device.getAddress();
    }

    public BluetoothDeviceVo(BleDevice bleDevice) {
        this.name = bleDevice.getName();
        this.address = bleDevice.getMac();
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return this.address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}

