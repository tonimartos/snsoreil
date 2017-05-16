package com.example.user.bluetooth_howtopair;

import android.bluetooth.BluetoothDevice;

import com.example.user.bluetooth_howtopair.handlers.BluetoothDeviceVo;

import java.io.Serializable;

public class BleDevice implements Serializable {
    private static final long serialVersionUID = 1;
    private boolean connect;
    private String mac;
    private String name;
    private String uuid;

    public BleDevice() {}

    public BleDevice(BluetoothDevice device) {
        this.mac = device.getAddress();
        this.name = device.getName();
    }

    public BleDevice(BluetoothDeviceVo device) {
        this.mac = device.getAddress();
        this.name = device.getName();
    }

    public String getMac() {
        return this.mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUuid() {
        return this.uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public boolean isConnect() {
        return this.connect;
    }

    public void setConnect(boolean connect) {
        this.connect = connect;
    }
}
