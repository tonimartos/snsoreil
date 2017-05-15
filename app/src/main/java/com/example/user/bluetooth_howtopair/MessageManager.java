package com.example.user.bluetooth_howtopair;

import android.content.Context;
import android.content.Intent;

public class MessageManager {
    public static final String LOGIN = "com.example.user.bluetooth_howtopair.login";
    public static final String SENDDATA = "com.example.user.bluetooth_howtopair.sendmessage";
    private static MessageManager manager;
    private Context context;

    public static final MessageManager getIntance() {
        if (manager == null) {
            manager = new MessageManager();
        }
        return manager;
    }

    public void initContext(Context context) {
        this.context = context;
    }

    public void Login() {
        if (this.context != null) {
            this.context.sendBroadcast(new Intent(LOGIN));
        }
    }

    public void writeMessage(byte[] message) {
        if (this.context != null) {
            Intent i = new Intent(SENDDATA);
            i.putExtra("data", message);
            this.context.sendBroadcast(i);
        }
    }

    public void connectDevice(BleDevice device) {
        if (this.context != null) {
            Intent i = new Intent(BluetoothMultiService.ACTIONCONNECT);
            i.putExtra("device", device);
            this.context.sendBroadcast(i);
        }
    }

    public void disconnectDevice(BleDevice device) {
        if (this.context != null) {
            Intent i = new Intent(BluetoothMultiService.ACTIONDISCONNECT);
            i.putExtra("device", device);
            this.context.sendBroadcast(i);
        }
    }
}

