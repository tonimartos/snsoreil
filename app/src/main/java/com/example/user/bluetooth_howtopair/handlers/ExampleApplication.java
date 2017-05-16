package com.example.user.bluetooth_howtopair.handlers;


import com.example.user.bluetooth_howtopair.activities.MainActivity;
import com.example.user.bluetooth_howtopair.utils.ConfigApplication;
import com.example.user.bluetooth_howtopair.utils.ConfigParams;

public class ExampleApplication extends ConfigApplication {
    private static ExampleApplication instance;

    public static ExampleApplication getInstance() {
        return instance;
    }

    public void onCreate() {
        super.onCreate();
        instance = this;
        if (!hasKey(ConfigParams.INITVALUE)) {
            setValue(ConfigParams.HIGHPA, 4);
            setValue(ConfigParams.LOWPA, 3);
            setValue(ConfigParams.HIGHTW, 20);
            setValue(ConfigParams.IRPOWERVOICE, true);
            setValue(ConfigParams.IRPOWERRING, true);
            setValue(ConfigParams.INITVALUE, "init");
            setValue(ConfigParams.LANGUAGE, 3);
        }
        MessageManager.getIntance().initContext(this);
        enableActivityLifeCallback(MainActivity.class.getCanonicalName());
    }

    public void writeMessage(byte[] data) {
        MessageManager.getIntance().writeMessage(data);
    }
}

