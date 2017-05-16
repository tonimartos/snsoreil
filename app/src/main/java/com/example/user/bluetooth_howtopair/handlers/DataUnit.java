package com.example.user.bluetooth_howtopair.handlers;

import android.bluetooth.BluetoothDevice;

import java.io.Serializable;

public class DataUnit implements Serializable {
    private static final long serialVersionUID = 1;
    private byte[] data;
    private String mac;
    private String name;

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DataUnit() {}

    public DataUnit(String address, byte[] data) {
        this.mac = address;
        this.data = data;
    }

    public DataUnit(BluetoothDevice device, byte[] arrayOfByte) {
        this.name = device.getName();
        this.mac = device.getAddress();
        this.data = arrayOfByte;
    }

    public byte[] getData() {
        return this.data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public String getMac() {
        return this.mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }
}

