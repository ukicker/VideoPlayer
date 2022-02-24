package com.connectsdk.external;

import android.util.Log;

/**
 * @PackageName : com.connectsdk.as
 * @File : CastLogUtils.java
 * @Date : 2021/12/30 2021/12/30
 * @Author : K
 * @E-mail : vip@devkit.vip
 * @Version : V 1.0
 * @Describe ：
 */
public class CastLogUtils {

    private static final String TAG = "ukicker_cast";
    private static boolean isLog = true;

    /**
     * 设置是否开启日志
     * @param isLog                 是否开启日志
     */
    public static void setIsLog(boolean isLog) {
        CastLogUtils.isLog = isLog;
    }

    public static boolean isIsLog() {
        return isLog;
    }

    public static void d(String message) {
        if(isLog){
            Log.d(TAG, message);
        }
    }

    public static void i(String message) {
        if(isLog){
            Log.i(TAG, message);
        }

    }

    public static void e(String msg) {
        if (isLog) {
            Log.e(TAG, msg);
        }
    }

    public static void e(String message, Throwable throwable) {
        if(isLog){
            Log.e(TAG, message, throwable);
        }
    }

}
