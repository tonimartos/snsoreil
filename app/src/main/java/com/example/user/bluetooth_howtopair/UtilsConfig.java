package com.example.user.bluetooth_howtopair;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;

import org.apache.http.conn.util.InetAddressUtils;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class UtilsConfig {
    public static final int NETWORK_PHONE = 1;
    public static final int NETWORK_WIFI = 2;
    public static final int NO_NETWORK = 0;

    public static boolean isNetWorkConnected(Context context) {
        if (context != null) {
            NetworkInfo mNetworkInfo = ((ConnectivityManager) context.getSystemService("connectivity")).getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    public static boolean isWiFiActive(Context inContext) {
        ConnectivityManager connectivity = (ConnectivityManager) inContext.getApplicationContext().getSystemService("connectivity");
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                int i = 0;
                while (i < info.length) {
                    if (info[i].getTypeName().equals("WIFI") && info[i].isConnected()) {
                        return true;
                    }
                    i += NETWORK_PHONE;
                }
            }
        }
        return false;
    }

    public static int isNetworkActive(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService("connectivity");
        NetworkInfo.State wifiState = cm.getNetworkInfo(NETWORK_PHONE).getState();
        NetworkInfo.State mobileState = cm.getNetworkInfo(0).getState();
        if (wifiState != null && mobileState != null && NetworkInfo.State.CONNECTED != wifiState && NetworkInfo.State.CONNECTED == mobileState) {
            return NETWORK_PHONE;
        }
        if (wifiState == null || mobileState == null || NetworkInfo.State.CONNECTED == wifiState || NetworkInfo.State.CONNECTED == mobileState) {
            return NETWORK_WIFI;
        }
        return 0;
    }

    public static boolean isExitsSdcard() {
        if (Environment.getExternalStorageState().equals("mounted")) {
            return true;
        }
        return false;
    }

    static String getString(Context context, int resId) {
        return context.getResources().getString(resId);
    }

    public static String getTopActivity(Context context) {
        List<ActivityManager.RunningTaskInfo> runningTaskInfos = ((ActivityManager) context.getSystemService("activity")).getRunningTasks(NETWORK_PHONE);
        if (runningTaskInfos != null) {
            return ((ActivityManager.RunningTaskInfo) runningTaskInfos.get(0)).topActivity.getClassName();
        }
        return "";
    }

    public static byte uniteByte(byte src0, byte src1) {
        StringBuilder stringBuilder = new StringBuilder("0x");
        byte[] bArr = new byte[NETWORK_PHONE];
        bArr[0] = src0;
        byte _b0 = (byte) (Byte.decode(stringBuilder.append(new String(bArr)).toString()).byteValue() << 4);
        stringBuilder = new StringBuilder("0x");
        bArr = new byte[NETWORK_PHONE];
        bArr[0] = src1;
        return (byte) (_b0 ^ Byte.decode(stringBuilder.append(new String(bArr)).toString()).byteValue());
    }

    public static byte[] intToBytes(int n) {
        return new byte[]{(byte) (n & MotionEventCompat.ACTION_MASK), (byte) ((MotionEventCompat.ACTION_POINTER_INDEX_MASK & n) >> 8), (byte) ((16711680 & n) >> 16), (byte) ((ViewCompat.MEASURED_STATE_MASK & n) >> 32)};
    }

    public static byte[] uniteBytes(byte[]... data) {
        int i;
        int length = 0;
        for (i = 0; i < data.length; i += NETWORK_PHONE) {
            length += data[i].length;
        }
        byte[] newData = new byte[length];
        int i2 = 0;
        int length2 = data.length;
        for (i = 0; i < length2; i += NETWORK_PHONE) {
            byte[] msg = data[i];
            System.arraycopy(msg, 0, newData, i2, msg.length);
            i2 += msg.length;
        }
        return newData;
    }

    public static byte[] chatOrders(String c) {
        return chatOrder(c.getBytes());
    }

    public static byte[] chatOrder(byte[] m) {
        if (m.length % NETWORK_WIFI != 0) {
            return null;
        }
        byte[] bytes = new byte[(m.length / NETWORK_WIFI)];
        int i = 0;
        int j = 0;
        while (i < m.length) {
            bytes[j] = uniteByte(m[i], m[i + NETWORK_PHONE]);
            i += NETWORK_WIFI;
            j += NETWORK_PHONE;
        }
        return bytes;
    }

    public static byte[] decodeByte(byte src) {
        byte[] des = new byte[NETWORK_WIFI];
        des[0] = (byte) (src & 15);
        des[NETWORK_PHONE] = (byte) ((src & 240) >> 4);
        return des;
    }

    public static String decodeByteToHexString(byte src) {
        byte[] des = new byte[NETWORK_WIFI];
        des[NETWORK_PHONE] = (byte) (src & 15);
        des[0] = (byte) ((src & 240) >> 4);
        return new StringBuilder(String.valueOf(Integer.toHexString(des[0]))).append(Integer.toHexString(des[NETWORK_PHONE])).toString();
    }

    public static String decodeBytesToHexString(byte[] data) {
        String result = new String();
        int length = data.length;
        for (int i = 0; i < length; i += NETWORK_PHONE) {
            result = result.concat(decodeByteToHexString(data[i]));
        }
        return result;
    }

    public static byte[] longToBytes(long num) {
        byte[] targets = new byte[4];
        for (int i = 0; i < 4; i += NETWORK_PHONE) {
            targets[i] = (byte) ((int) ((num >>> (((targets.length - 1) - i) * 8)) & 255));
        }
        return targets;
    }

    public static String bytesToHex(byte[] bytes) {
        StringBuilder sbuf = new StringBuilder();
        for (int idx = 0; idx < bytes.length; idx += NETWORK_PHONE) {
            int intVal = bytes[idx] & MotionEventCompat.ACTION_MASK;
            if (intVal < 16) {
                sbuf.append("0");
            }
            sbuf.append(Integer.toHexString(intVal).toUpperCase());
        }
        return sbuf.toString();
    }

    public static byte[] getUTF8Bytes(String str) {
        try {
            return str.getBytes("UTF-8");
        } catch (Exception e) {
            return null;
        }
    }

    public static String loadFileAsString(String filename) throws IOException {
        BufferedInputStream is = new BufferedInputStream(new FileInputStream(filename), AccessibilityNodeInfoCompat.ACTION_NEXT_HTML_ELEMENT);
        try {
            String str;
            ByteArrayOutputStream baos = new ByteArrayOutputStream(AccessibilityNodeInfoCompat.ACTION_NEXT_HTML_ELEMENT);
            byte[] bytes = new byte[AccessibilityNodeInfoCompat.ACTION_NEXT_HTML_ELEMENT];
            boolean isUTF8 = false;
            int count = 0;
            while (true) {
                int read = is.read(bytes);
                if (read == -1) {
                    break;
                }
                if (count == 0) {
                    if (bytes[0] == -17 && bytes[NETWORK_PHONE] == -69 && bytes[NETWORK_WIFI] == -65) {
                        isUTF8 = true;
                        baos.write(bytes, 3, read - 3);
                        count += read;
                    }
                }
                baos.write(bytes, 0, read);
                count += read;
            }
            if (isUTF8) {
                str = new String(baos.toByteArray(), "UTF-8");
            } else {
                str = new String(baos.toByteArray());
            }
            return str;
        } finally {
            try {
                is.close();
            } catch (Exception e) {
            }
        }
    }

    @SuppressLint({"NewApi"})
    public static String getMACAddress(String interfaceName) {
        try {
            NetworkInterface intf;
            Iterator it = Collections.list(NetworkInterface.getNetworkInterfaces()).iterator();
            do {
                if (it.hasNext()) {
                    intf = (NetworkInterface) it.next();
                    if (interfaceName == null) {
                        break;
                    }
                } else {
                    return "";
                }
            } while (!intf.getName().equalsIgnoreCase(interfaceName));
            byte[] mac = intf.getHardwareAddress();
            if (mac == null) {
                return "";
            }
            StringBuilder buf = new StringBuilder();
            for (int idx = 0; idx < mac.length; idx += NETWORK_PHONE) {
                Object[] objArr = new Object[NETWORK_PHONE];
                objArr[0] = Byte.valueOf(mac[idx]);
                buf.append(String.format("%02X:", objArr));
            }
            if (buf.length() > 0) {
                buf.deleteCharAt(buf.length() - 1);
            }
            return buf.toString();
        } catch (Exception e) {
            return e.toString();
        }
    }

    public static String getIPAddress(boolean useIPv4) {
        try {
            for (NetworkInterface intf : Collections.list(NetworkInterface.getNetworkInterfaces())) {
                for (InetAddress addr : Collections.list(intf.getInetAddresses())) {
                    if (!addr.isLoopbackAddress()) {
                        String sAddr = addr.getHostAddress().toUpperCase();
                        boolean isIPv4 = InetAddressUtils.isIPv4Address(sAddr);
                        if (useIPv4) {
                            if (isIPv4) {
                                return sAddr;
                            }
                        } else if (!isIPv4) {
                            int delim = sAddr.indexOf(37);
                            if (delim >= 0) {
                                return sAddr.substring(0, delim);
                            }
                            return sAddr;
                        }
                    }
                }
            }
        } catch (Exception e) {
        }
        return "";
    }

    public static boolean isNumeric(String str) {
        int i = str.length();
        do {
            i--;
            if (i < 0) {
                return true;
            }
        } while (Character.isDigit(str.charAt(i)));
        return false;
    }

    public static Bitmap Bytes2Bimap(byte[] b) {
        if (b.length != 0) {
            return BitmapFactory.decodeByteArray(b, 0, b.length);
        }
        return null;
    }

    public static byte[] Bitmap2Bytes(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

    public static byte[] getTempBitmapByte(String path) {
        byte[] bArr = null;
        File file = new File(path);
        if (!file.exists()) {
            return null;
        }
        try {
            FileInputStream icoinput = new FileInputStream(file);
            bArr = new byte[icoinput.available()];
            icoinput.read(bArr, 0, bArr.length);
            icoinput.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e2) {
            e2.printStackTrace();
        }
        return bArr;
    }
}

