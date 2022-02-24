package com.yc.video.tool;

import com.yc.video.kernel.utils.VideoLogUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * @PackageName : com.smilodontech.player
 * @File : TimeFormater.java
 * @Date : 2021/4/27 2021/4/27
 * @Author : K
 * @E-mail : vip@devkit.vip
 * @Version : V 1.0
 * @Describe ：
 */
public class TimeFormater {

    /**
     * 格式化毫秒数为 xx:xx:xx这样的时间格式。
     *
     * @param ms 毫秒数
     * @return 格式化后的字符串
     */
    public static String formatMs(long ms) {
        int seconds = (int) (ms / 1000);
        int finalSec = seconds % 60;
        int finalMin = seconds / 60 % 60;
        int finalHour = seconds / 3600;

        StringBuilder msBuilder = new StringBuilder("");
        if (finalHour > 9) {
            msBuilder.append(finalHour).append(":");
        } else if (finalHour > 0) {
            msBuilder.append("0").append(finalHour).append(":");
        }

        if (finalMin > 9) {
            msBuilder.append(finalMin).append(":");
        } else if (finalMin > 0) {
            msBuilder.append("0").append(finalMin).append(":");
        } else {
            msBuilder.append("00").append(":");
        }

        if (finalSec > 9) {
            msBuilder.append(finalSec);
        } else if (finalSec > 0) {
            msBuilder.append("0").append(finalSec);
        } else {
            msBuilder.append("00");
        }

        return msBuilder.toString();
    }

    public static long getExpirationInGMTFormat(String expirationInGMTFormat) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.CHINA);
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date date = sdf.parse(expirationInGMTFormat);
            return date.getTime() / 1000;
        } catch (ParseException e) {
            if (VideoLogUtils.isIsLog()) {
                e.printStackTrace();
            }
            return 0;
        }
    }
}
