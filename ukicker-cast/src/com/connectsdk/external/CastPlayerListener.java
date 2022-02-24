package com.connectsdk.external;

import com.connectsdk.device.ConnectableDevice;
import com.connectsdk.service.capability.MediaControl;
import com.connectsdk.service.command.ServiceCommandError;

/**
 * @PackageName : com.connectsdk.as
 * @File : CastListener.java
 * @Date : 2021/12/30 2021/12/30
 * @Author : K
 * @E-mail : vip@devkit.vip
 * @Version : V 1.0
 * @Describe ：
 */
public interface CastPlayerListener {

   /**
    * 投屏播放状态
    */
   void onCastPlayerState(ConnectableDevice device, CustomCastBean castBean, MediaControl.PlayStateStatus status);

   /**
    *进度更新
    */
   void onCastPositionUpdate(ConnectableDevice device, CustomCastBean castBean, Long duration, Long position);

    /**
     * 错误
     */
    void onError(ConnectableDevice device, CustomCastBean castBean, ServiceCommandError e);

}
