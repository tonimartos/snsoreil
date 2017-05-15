package com.example.user.bluetooth_howtopair;

import android.app.Activity;
import android.widget.Toast;

import com.example.user.bluetooth_howtopair.ExampleApplication;
import com.example.user.bluetooth_howtopair.UtilsConfig;
import com.example.user.bluetooth_howtopair.ObserverHandler;

public class BaseActivity extends Activity {
    protected ObserverHandler observerHandler;
    private Toast toast;

    protected void displayToast(int resourceId) {
        displayToast(getResources().getString(resourceId));
    }

    protected void displayToast(String msg) {
        if (this.toast != null) {
            this.toast.cancel();
            this.toast = null;
        }
        this.toast = Toast.makeText(this, msg, 0);
        this.toast.show();
    }

    protected void writeOrder(byte[] data) {
        byte[][] r4 = new byte[2][];
        r4[0] = new byte[]{(byte) -4, (byte) (data.length + 1)};
        r4[1] = data;
        byte[] orders = UtilsConfig.uniteBytes(r4);
        int value = orders[0];
        for (int i = 1; i < orders.length; i++) {
            value ^= orders[i];
        }
        byte crc = (byte) value;
        r4 = new byte[2][];
        r4[0] = orders;
        r4[1] = new byte[]{crc};
        writeByte(UtilsConfig.uniteBytes(r4));
    }

    protected void writeByte(byte[] data) {
        ExampleApplication.getInstance().writeMessage(data);
    }
}

