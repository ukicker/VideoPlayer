package com.yc.ycvideoplayer;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.connectsdk.device.ConnectableDevice;

import org.yc.ycvideoplayer.R;

import java.util.List;

/**
 * @PackageName : com.yc.ycvideoplayer
 * @File : CastDeviceAdapter.java
 * @Date : 2021/12/27 2021/12/27
 * @Author : K
 * @E-mail : vip@devkit.vip
 * @Version : V 1.0
 * @Describe ：
 */
public class CastDeviceAdapter  extends BaseQuickAdapter<ConnectableDevice, BaseViewHolder> {

    private String TAG = "ukicker";


    public CastDeviceAdapter(int layoutResId, @Nullable List<ConnectableDevice> data) {
        super(layoutResId, data);
    }


    @Override
    protected void convert(BaseViewHolder helper, ConnectableDevice item) {

        Log.d(TAG, "convert: "+item.isConnecting+"/"+item.isConnectable()+"/"+item.isConnected()+"/"+item.getLastSeenOnWifi());
        Log.d(TAG, "convert: "+item.getLastKnownIPAddress());
        Log.d(TAG, "convert: "+item.getCapabilities().toString());

        ImageView imageView = helper.getView(R.id.iv_img);
        if (item.isConnected()) {
            imageView.setImageResource(R.mipmap.icon_cast_select);
            imageView.setVisibility(View.VISIBLE);
        } else {
            imageView.setVisibility(View.GONE);
        }
        String text;
        if (item.getFriendlyName() != null) {
            text = item.getFriendlyName()+"("+item.getServiceId()+")";
        } else {
            text = item.getModelName()+"("+item.getServiceId()+")";
        }
        helper.setText(R.id.tv_device_name, text);

    }

}
