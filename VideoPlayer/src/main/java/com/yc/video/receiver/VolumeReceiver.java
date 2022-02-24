package com.yc.video.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * @PackageName : com.yc.video.receiver
 * @File : VReceiver.java
 * @Date : 2021/10/28 2021/10/28
 * @Author : K
 * @E-mail : vip@devkit.vip
 * @Version : V 1.0
 * @Describe ：
 */
public class VolumeReceiver extends BroadcastReceiver {


    private OnVolumeListener mVolumeListener;


    public void setVolumeListener(OnVolumeListener volumeListener) {
        mVolumeListener = volumeListener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (mVolumeListener != null) {
            mVolumeListener.onVolumeChange(1);
        }
    }

   public interface OnVolumeListener {
        void onVolumeChange(int p);
    }
}
