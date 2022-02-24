package com.connectsdk.external;

import com.connectsdk.device.ConnectableDevice;
import com.connectsdk.discovery.DiscoveryManager;
import com.connectsdk.discovery.DiscoveryManagerListener;

/**
 * @PackageName : com.connectsdk.as
 * @File : IDiscoveryListener.java
 * @Date : 2021/12/30 2021/12/30
 * @Author : K
 * @E-mail : vip@devkit.vip
 * @Version : V 1.0
 * @Describe ：
 */
public interface DiscoveryResultListener extends DiscoveryManagerListener {


    @Override
    void onDeviceUpdated(DiscoveryManager manager, ConnectableDevice device);


    /**
     * 发现完成
     * @param manager
     */
    void onDiscoveryComplete(DiscoveryManager manager);
}
