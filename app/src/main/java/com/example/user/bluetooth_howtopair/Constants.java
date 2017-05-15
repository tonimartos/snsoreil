package com.example.user.bluetooth_howtopair;

import java.util.UUID;

public class Constants {
    public static final byte BARUNIT = (byte) 0;
    public static final UUID BLUETOOTH_READUUID;
    public static final UUID BLUETOOTH_UUID;
    public static final UUID BLUETOOTH_WRITEUUID;
    public static String CLIENT_CHARACTERISTIC_CONFIG = null;
    public static final String CONNECTSTATE = "state";
    public static final int CONNECTSTATE_CONNECTED = 1;
    public static final int CONNECTSTATE_CONNECTING = 2;
    public static final int CONNECTSTATE_DISCONNECTED = 4;
    public static final int CONNECTSTATE_DISCONNECTING = 3;
    public static final String DATAUNIT = "dataunit";
    public static final String DEVICENAME = "TPMS";
    public static final String DEVICES = "devices";
    public static String HEART_RATE_MEASUREMENT = null;
    public static final String PHONENUBER = "phonenumber";
    public static final String QUERYID = "FC02D52B";
    public static final String QUERYSET = "FC02C53B";

    public interface DEVICEORDER {
        public static final byte HEAD1 = (byte) -4;
        public static final byte ORDER1 = (byte) 1;
        public static final byte ORDER2 = (byte) 2;
        public static final byte ORDER3 = (byte) 3;
        public static final byte ORDER4 = (byte) 4;
        public static final byte ORDER5 = (byte) 5;
        public static final byte ORDER6 = (byte) 6;
        public static final byte ORDER7 = (byte) 7;
        public static final byte ORDERA5 = (byte) -91;
        public static final byte ORDERA6 = (byte) -90;
        public static final byte ORDERB5 = (byte) -75;
        public static final byte ORDERB6 = (byte) -74;
        public static final byte ORDERC5 = (byte) -59;
        public static final byte ORDERD5 = (byte) -43;
        public static final byte ORDERE5 = (byte) -27;
        public static final byte ORDERE6 = (byte) -26;
    }

    public interface UNIT {
        public static final int BAR = 0;
        public static final int PS1 = 1;
    }

    static {
        BLUETOOTH_UUID = UUID.fromString("0000ffe0-0000-1000-8000-00805f9b34fb");
        BLUETOOTH_READUUID = UUID.fromString("0000ffe4-0000-1000-8000-00805f9b34fb");
        BLUETOOTH_WRITEUUID = UUID.fromString("0000ffe9-0000-1000-8000-00805f9b34fb");
        HEART_RATE_MEASUREMENT = "6e400003-b5a3-f393-e0a9-e50e24dcca9e";
        CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";
    }
}

