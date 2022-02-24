package com.yc.video.ui.cast;

import com.connectsdk.device.ConnectableDevice;
import com.connectsdk.discovery.DiscoveryManager;
import com.connectsdk.service.command.ServiceCommandError;

/**
 * @PackageName : com.yc.video.ui.cast
 * @File : OnCastSearchListener.java
 * @Date : 2021/12/27 2021/12/27
 * @Author : K
 * @E-mail : vip@devkit.vip
 * @Version : V 1.0
 * @Describe ：
 */
public interface OnDiscoveryManagerListener {


    /**
     * 开始搜索设备
     */
    void onSearchStart();

    /**
     * 搜索结束
     */
    void onSearchEnd();

    /**
     * 选择设备成功
     */
    void onSelectSuccess();

    /**
     * 设备添加
     * @param manager
     * @param device
     */
    void onDeviceAdded(DiscoveryManager manager, ConnectableDevice device);


    /**
     * 设备移除
     * @param manager
     * @param device
     */
    void onDeviceRemoved(DiscoveryManager manager, ConnectableDevice device);

}
