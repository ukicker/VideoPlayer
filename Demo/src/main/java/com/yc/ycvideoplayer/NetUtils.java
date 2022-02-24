package com.yc.ycvideoplayer;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import androidx.annotation.RequiresPermission;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.List;

import static android.Manifest.permission.ACCESS_NETWORK_STATE;

/**
 * @PackageName : com.yc.ycvideoplayer
 * @File : NetUtils.java
 * @Date : 2021/12/27 2021/12/27
 * @Author : K
 * @E-mail : vip@devkit.vip
 * @Version : V 1.0
 * @Describe ：
 */
public class NetUtils {
    public static final String NETWORK_TYPE_WIFI = "WIFI";
    public static final String NETWORK_TYPE_5G = "5G";
    public static final String NETWORK_TYPE_4G = "4G";
    public static final String NETWORK_TYPE_3G = "3G";
    public static final String NETWORK_TYPE_2G = "2G";
    public static final String NETWORK_TYPE_WAP = "WAP";
    public static final String NETWORK_TYPE_UNKNOWN = "UNKNOWN";
    public static final String NETWORK_TYPE_DISCONNECT = "disconnect";
    public static final String NETWORK_TYPE_NO = "无网络";



    /**
     * 检查网络状态
     *
     * @param context
     * @return
     */
    public static boolean isConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        if (info != null && info.isConnected()) {
            if (info.getState() == NetworkInfo.State.CONNECTED) {
                return true;
            }
        }
        return false;
    }

    /**
     * 网络是否可用
     *
     * @param context
     * @return
     */
    public static boolean isNetworkAvailable(Context context) {
        if (context == null) {
            return false;
        }
        try {
            ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivity != null) {
                NetworkInfo info = connectivity.getActiveNetworkInfo();
                return info.isAvailable();
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    /**
     * 是否是WIFI网络
     *
     * @param cxt
     * @return
     */
    public static boolean isWiFi(Context cxt) {
        return getNetworkType(cxt).equals("WIFI");
    }

    /**
     * 打开网络设置界面
     *
     * @param act
     */
    //unchecked
    public static void openNetSetting(Activity act) {
        Intent intent = new Intent();
        ComponentName cm = new ComponentName("com.android.settings", "com.android.settings.WirelessSettings");
        intent.setComponent(cm);
        intent.setAction("android.intent.action.VIEW");
        act.startActivityForResult(intent, 0);
    }



    /**
     * 设置WiFi状态
     *
     * @param context
     * @param enabled
     */
    public static void setWifiEnabled(Context context, boolean enabled) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        wifiManager.setWifiEnabled(enabled);
    }

    /**
     * 获取WIFI列表
     *
     * @param context
     * @return
     */
    public static List<ScanResult> getWifiScanResults(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        return wifiManager.startScan() ? wifiManager.getScanResults() : null;
    }

    /**
     * 过滤扫描结果
     *
     * @param context
     * @param bssid
     * @return
     */
    public static ScanResult getScanResultsByBSSID(Context context, String bssid) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        ScanResult scanResult = null;
        boolean f = wifiManager.startScan();
        if (!f) {
            getScanResultsByBSSID(context, bssid);
        }
        List<ScanResult> list = wifiManager.getScanResults();
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                scanResult = list.get(i);
                if (scanResult.BSSID.equals(bssid)) {
                    break;
                }
            }
        }
        return scanResult;
    }

    /**
     * 获取WiFi链接信息
     *
     * @param context
     * @return
     */
    public static WifiInfo getWifiConnectionInfo(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        return wifiManager.getConnectionInfo();
    }

    public static String getHostIp() {
        String hostIp = "";

        try {
            Enumeration nis = NetworkInterface.getNetworkInterfaces();
            InetAddress ia = null;

            while(true) {
                while(nis.hasMoreElements()) {
                    NetworkInterface ni = (NetworkInterface)nis.nextElement();
                    Enumeration ias = ni.getInetAddresses();

                    while(ias.hasMoreElements()) {
                        ia = (InetAddress)ias.nextElement();
                        if (!(ia instanceof Inet6Address)) {
                            String ip = ia.getHostAddress();
                            if (!"127.0.0.1".equals(ip)) {
                                hostIp = ia.getHostAddress();
                                break;
                            }
                        }
                    }
                }

                return hostIp;
            }
        } catch (SocketException var6) {
            return "0.0.0.0";
        }
    }

    public static boolean isIp(String str) {
        if (TextUtils.isEmpty(str)) {
            return false;
        } else {
            String[] addr = str.split("\\.");
            return addr.length == 4;
        }
    }



    /**
     * Return the name of network operate.
     *
     * @return the name of network operate
     */
    public static String getNetworkOperatorName(Context context) {
        TelephonyManager tm =
                (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (tm == null) return "";
        return tm.getNetworkOperatorName();
    }

    /**
     * Return type of network.
     * <p>Must hold {@code <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />}</p>
     *
     * @return type of network
     * <ul>
     * </ul>
     */
    @RequiresPermission(ACCESS_NETWORK_STATE)
    public static String getNetworkType(Context context ){
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        if (info != null && info.isAvailable()) {
            if (info.getType() == ConnectivityManager.TYPE_WIFI) {
                return NETWORK_TYPE_WIFI;
            } else if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
                switch (info.getSubtype()) {
                    case TelephonyManager.NETWORK_TYPE_GSM:
                    case TelephonyManager.NETWORK_TYPE_GPRS:
                    case TelephonyManager.NETWORK_TYPE_CDMA:
                    case TelephonyManager.NETWORK_TYPE_EDGE:
                    case TelephonyManager.NETWORK_TYPE_1xRTT:
                    case TelephonyManager.NETWORK_TYPE_IDEN:
                        return NETWORK_TYPE_2G;
                    case TelephonyManager.NETWORK_TYPE_TD_SCDMA:
                    case TelephonyManager.NETWORK_TYPE_EVDO_A:
                    case TelephonyManager.NETWORK_TYPE_UMTS:
                    case TelephonyManager.NETWORK_TYPE_EVDO_0:
                    case TelephonyManager.NETWORK_TYPE_HSDPA:
                    case TelephonyManager.NETWORK_TYPE_HSUPA:
                    case TelephonyManager.NETWORK_TYPE_HSPA:
                    case TelephonyManager.NETWORK_TYPE_EVDO_B:
                    case TelephonyManager.NETWORK_TYPE_EHRPD:
                    case TelephonyManager.NETWORK_TYPE_HSPAP:
                        return NETWORK_TYPE_3G;

                    case TelephonyManager.NETWORK_TYPE_IWLAN:
                    case TelephonyManager.NETWORK_TYPE_LTE:
                        return NETWORK_TYPE_4G;
                    // TODO: 2020/8/17 升级
                    case 20://TelephonyManager.NETWORK_TYPE_NR
                        return NETWORK_TYPE_5G;
                    default:
                        String subtypeName = info.getSubtypeName();
                        if (subtypeName.equalsIgnoreCase("TD-SCDMA")
                                || subtypeName.equalsIgnoreCase("WCDMA")
                                || subtypeName.equalsIgnoreCase("CDMA2000")) {
                            return NETWORK_TYPE_3G;
                        } else {
                            return NETWORK_TYPE_UNKNOWN;
                        }
                }
            } else {
                return NETWORK_TYPE_UNKNOWN;
            }
        }
        return NETWORK_TYPE_DISCONNECT;
    }




}
