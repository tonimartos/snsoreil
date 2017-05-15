package com.example.user.bluetooth_howtopair;

import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;

public class ByteUtils {
    public static int[] getByteHL(byte src) {
        return new int[]{(byte) (src & 15), (byte) ((src & 240) >> 4)};
    }

    public static String byteToString(byte b) {
        String str = Integer.toBinaryString(b | AccessibilityNodeInfoCompat.ACTION_NEXT_AT_MOVEMENT_GRANULARITY);
        int len = str.length();
        return str.substring(len - 8, len);
    }

    public static int byteToInt(byte b, int id) {
        String str = Integer.toBinaryString(b | AccessibilityNodeInfoCompat.ACTION_NEXT_AT_MOVEMENT_GRANULARITY);
        int len = str.length();
        return Integer.valueOf(new StringBuffer(str.substring(len - 8, len)).reverse().toString().substring(id - 1, id)).intValue();
    }

    public static byte stringToByte(String string) {
        byte result = (byte) 0;
        int i = string.length() - 1;
        int j = 0;
        while (i >= 0) {
            result = (byte) ((int) (((double) result) + (((double) Byte.parseByte(new StringBuilder(String.valueOf(string.charAt(i))).toString())) * Math.pow(2.0d, (double) j))));
            i--;
            j++;
        }
        return result;
    }
}

