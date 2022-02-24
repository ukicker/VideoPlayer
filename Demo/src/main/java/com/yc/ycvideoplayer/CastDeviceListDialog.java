package com.yc.ycvideoplayer;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.connectsdk.device.ConnectableDevice;

import org.yc.ycvideoplayer.R;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.WIFI_SERVICE;

/**
 * @PackageName : com.yc.ycvideoplayer
 * @File : CastDeviceListDialog.java
 * @Date : 2021/12/27 2021/12/27
 * @Author : K
 * @E-mail : vip@devkit.vip
 * @Version : V 1.0
 * @Describe ：
 */
public class CastDeviceListDialog extends Dialog implements View.OnClickListener {

    private   String TAG = this.getClass().getSimpleName();
    RecyclerView mRecyclerView;
    TextView mTvDescribe;
    TextView mTvRefresh;
    TextView mTvCancel;
    TextView mTvHelp;
    ImageView mIvRefresh;
    RelativeLayout mRlEmpty;
    RelativeLayout mRlSearch;
    LinearLayout mLlSearch;
    LottieAnimationView mLottieAnimationView;

    private CastDeviceAdapter mCastDeviceAdapter;
    private List<ConnectableDevice> mClingDeviceList = new ArrayList<>();

    private OnSelectConnectableDeviceListener mConnectableDeviceListener;
    private Activity mActivity;

    public void setConnectableDeviceListener(OnSelectConnectableDeviceListener connectableDeviceListener) {
        mConnectableDeviceListener = connectableDeviceListener;
    }

    public CastDeviceListDialog(@NonNull Activity context) {
        super(context, R.style.dialog_cast);
        mActivity = context;
    }


    @Override
    public void onContentChanged() {
        super.onContentChanged();
        WindowManager.LayoutParams params = this.getWindow().getAttributes();
        params.width = ViewUtil.getScreenWidth(mActivity);
        this.getWindow().setAttributes(params);
    }

    @Override
    public void onStart() {
        super.onStart();
        Window win = getWindow();
        WindowManager.LayoutParams params = win.getAttributes();
        params.gravity = Gravity.BOTTOM;
        params.width = ViewUtil.getScreenWidth(mActivity);
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        win.setAttributes(params);
        win.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_fragment_device_list);
        initView();
    }


    private void initView() {


        mRecyclerView = findViewById(R.id.rv_list);
        mRlEmpty = findViewById(R.id.rl_empty);
        mRlSearch = findViewById(R.id.rl_search);
        mTvCancel = findViewById(R.id.tv_cancel);
        mTvHelp = findViewById(R.id.tv_help);
        mTvRefresh = findViewById(R.id.tv_refresh);
        mTvDescribe = findViewById(R.id.tv_describe);
        mLlSearch = findViewById(R.id.ll_search);
        mIvRefresh = findViewById(R.id.iv_refresh);
        mLottieAnimationView = findViewById(R.id.lottie_search_view);
        mLottieAnimationView.setImageAssetsFolder("images/");


        mCastDeviceAdapter = new CastDeviceAdapter(R.layout.item_cling_device, mClingDeviceList);
        mLlSearch.setOnClickListener(this);
        mTvCancel.setOnClickListener(this);
        mTvHelp.setOnClickListener(this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(mCastDeviceAdapter);
        mCastDeviceAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (mConnectableDeviceListener != null) {
                    mConnectableDeviceListener.onSelect(mClingDeviceList.get(position));
                }
            }
        });

        if (null != "") {
            mTvDescribe.setText("当前Wi-Fi：" + getConnectWifiSsid());
        } else {
            mTvDescribe.setText("选择设备后，当前视频将投屏到设备播放");
        }

        if (!NetUtils.isWiFi(getContext())) {
        } else {
            mRlSearch.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
            mRlEmpty.setVisibility(View.GONE);
            mTvRefresh.setText("搜索中...");
            mIvRefresh.setVisibility(View.GONE);
        }


    }

    @Override
    public void show() {
        super.show();
        if (!NetUtils.isWiFi(getContext())) {
        } else {
            mRlSearch.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
            mRlEmpty.setVisibility(View.GONE);
            mTvRefresh.setText("搜索中...");
            mIvRefresh.setVisibility(View.GONE);
        }

    }

    @Override
    public void dismiss() {
        mClingDeviceList.clear();
        super.dismiss();
    }

    private String getConnectWifiSsid() {
        ConnectivityManager cm = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm.getActiveNetworkInfo().getType() != ConnectivityManager.TYPE_WIFI) {
            return "WIFI 未连接";
        }
        WifiManager wifiManager = (WifiManager) getContext().getApplicationContext().getSystemService(WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        return wifiInfo == null ? "未知" : wifiInfo.getSSID().contains("unknown ssid") ? "未知" : wifiInfo.getSSID();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_search:
                if (!NetUtils.isWiFi(getContext())) {
                } else {
                    mRlSearch.setVisibility(View.VISIBLE);
                    mRecyclerView.setVisibility(View.GONE);
                    mRlEmpty.setVisibility(View.GONE);
                    mTvRefresh.setText("搜索中...");
                    mIvRefresh.setVisibility(View.GONE);
                    mConnectableDeviceListener.startDiscoveryDevice();
                }
                break;
            case R.id.tv_cancel:
                dismiss();
                break;
            case R.id.tv_help:
                break;
        }
    }


    public void onDeviceRemoved(ConnectableDevice device) {
        mClingDeviceList.remove(device);
        if (mCastDeviceAdapter != null) {
            mCastDeviceAdapter.notifyDataSetChanged();
            if (mCastDeviceAdapter.getData().size() > 0) {
                mRecyclerView.setVisibility(View.VISIBLE);
                mRlEmpty.setVisibility(View.GONE);
                mRlSearch.setVisibility(View.GONE);
                mTvRefresh.setText("重新搜索");
                mIvRefresh.setVisibility(View.VISIBLE);
            }
        }

    }


    public void onDeviceAdded(ConnectableDevice device) {
        if (mClingDeviceList.size() > 0) {
            if (!mClingDeviceList.contains(device)){
                mClingDeviceList.add(device);
            }
        } else {
            mClingDeviceList.add(device);
        }
        if (mCastDeviceAdapter != null) {
            mCastDeviceAdapter.notifyDataSetChanged();
            if (mCastDeviceAdapter.getData().size() > 0) {
                mRecyclerView.setVisibility(View.VISIBLE);
                mRlEmpty.setVisibility(View.GONE);
                mRlSearch.setVisibility(View.GONE);
                mTvRefresh.setText("重新搜索");
                mIvRefresh.setVisibility(View.VISIBLE);
            }
        }

    }

    public void onSearchEnd() {
        Log.d(TAG, "onSearchEnd: 111");

        if (null != mCastDeviceAdapter) {
            if (mCastDeviceAdapter.getData().size() == 0){
                mRlEmpty.setVisibility(View.VISIBLE);
                mRecyclerView.setVisibility(View.GONE);
                mRlSearch.setVisibility(View.GONE);
                mTvRefresh.setText("重新搜索");
                mIvRefresh.setVisibility(View.VISIBLE);
            }
        } else {
            mRlEmpty.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
            mRlSearch.setVisibility(View.GONE);
            mTvRefresh.setText("重新搜索");
            mIvRefresh.setVisibility(View.VISIBLE);
        }
    }

    public void onDiscoveryFailed() {
        mClingDeviceList.clear();
        if (mCastDeviceAdapter != null) {
            mCastDeviceAdapter.notifyDataSetChanged();
        }
    }

    public interface OnSelectConnectableDeviceListener {

        void onSelect(ConnectableDevice device);

        void startDiscoveryDevice();
    }
}

