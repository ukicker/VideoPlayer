package com.yc.ycvideoplayer.cast;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import com.connectsdk.device.ConnectableDevice;
import com.connectsdk.discovery.DiscoveryManager;
import com.connectsdk.external.CastLogUtils;
import com.connectsdk.external.CastManager;
import com.connectsdk.external.CastPlayerListener;
import com.connectsdk.external.ConnectListener;
import com.connectsdk.external.CustomCastBean;
import com.connectsdk.external.DiscoveryResultListener;
import com.connectsdk.external.MediaType;
import com.connectsdk.service.DeviceService;
import com.connectsdk.service.capability.MediaControl;
import com.connectsdk.service.command.ServiceCommandError;
import com.yc.video.bridge.ControlWrapper;
import com.yc.video.config.ConstantKeys;
import com.yc.video.config.VideoPlayerConfig;
import com.yc.video.kernel.utils.VideoLogUtils;
import com.yc.video.player.VideoViewManager;
import com.yc.video.tool.NetworkUtils;
import com.yc.video.tool.PlayerUtils;
import com.yc.video.ui.view.InterControlView;

import java.util.List;

import static com.yc.video.config.ConstantKeys.PlayMode.MODE_TINY_WINDOW;

/**
 * @PackageName : com.yc.video.ui.cast
 * @File : PlayerCastC.java
 * @Date : 2021/12/18 2021/12/18
 * @Author : K
 * @E-mail : vip@devkit.vip
 * @Version : V 1.0
 * @Describe ：
 */
public class PlayerCastComponent extends FrameLayout implements InterControlView
        , View.OnClickListener
        , DiscoveryResultListener
        , ConnectListener
        , CastPlayerListener {


    private int clickId = -1;
    private OnErrorListener mOnErrorListener;
    private OnDiscoveryManagerListener mDiscoveryManagerListener;
    protected ControlWrapper mControlWrapper;
    private Handler mHandler = new Handler();
    private PlayerCastControlView castControlView;

    private CastManager mCastManager;
    private View mClickView;


    public void setOnErrorListener(OnErrorListener onErrorListener) {
        mOnErrorListener = onErrorListener;
    }

    public void setDiscoveryManagerListener(OnDiscoveryManagerListener discoveryManagerListener) {
        mDiscoveryManagerListener = discoveryManagerListener;
    }

    public PlayerCastComponent(@NonNull Context context) {
        super(context);
        throw new RuntimeException("请先调用重载方法进行初始化");
    }

    public PlayerCastComponent(@NonNull Context context, int layoutId, int clickId) {
        super(context);
        this.clickId = clickId;
        init(context, layoutId, clickId);
    }


    private void init(Context context, int layoutId, int clickId) {
        View view = LayoutInflater.from(getContext()).inflate(layoutId, this, true);
        if (view != null) {
            mClickView = findViewById(clickId);
            mClickView.setOnClickListener(this);
        }
        CastManager.init(context);
        mCastManager = CastManager.getInstance();
        mCastManager.setDiscoveryResultListener(this);
        mCastManager.setConnectListener(this);
        mCastManager.setCastPlayerListener(this);

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == clickId) {
            Activity activity = PlayerUtils.scanForActivity(getContext());
            if (activity != null && mControlWrapper.isFullScreen()) {
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                if (mControlWrapper != null) {
                    mControlWrapper.stopFullScreen();
                }
            }
            mHandler.postDelayed(() -> {
                if (NetworkUtils.isWifiConnected(getContext())) {
                    startDiscoveryDevice();
                } else {
                    if (mOnErrorListener != null) {
                        mOnErrorListener.onError("WIFI未连接，请链接WIFI后使用");
                    }
                }
            }, 300);
            VideoPlayerConfig config = VideoViewManager.instance().getConfig();
            if (config != null && config.mBuriedPointEvent != null) {
//                config.mBuriedPointEvent.onCast(mControlWrapper.getUrl());
            }
        }
    }

    @Override
    public void attach(@NonNull ControlWrapper controlWrapper) {
        this.mControlWrapper = controlWrapper;
    }

    @Override
    public View getView() {
        return this;
    }

    @Override
    public void onVisibilityChanged(boolean isVisible, Animation anim) {
        if (isVisible) {
            if (!mControlWrapper.isTinyScreen()) {
                setVisibility(VISIBLE);
                if (anim != null) {
                    startAnimation(anim);
                }
            }
        } else {
            setVisibility(GONE);
            if (anim != null) {
                startAnimation(anim);
            }
        }
    }

    @Override
    public void onPlayStateChanged(int playState) {
        switch (playState) {
            case ConstantKeys.CurrentState.STATE_IDLE:
            case ConstantKeys.CurrentState.STATE_PLAYING:
                break;
            case ConstantKeys.CurrentState.STATE_BUFFERING_PLAYING:
            case ConstantKeys.CurrentState.STATE_COMPLETED:
            case ConstantKeys.CurrentState.STATE_ERROR:
            case ConstantKeys.CurrentState.STATE_NETWORK_ERROR:
            case ConstantKeys.CurrentState.STATE_PARSE_ERROR:
            case ConstantKeys.CurrentState.STATE_URL_NULL:
                setVisibility(GONE);
                break;
            default: {
            }
        }
    }

    @Override
    public void onPlayerStateChanged(int playerState) {
        if (playerState == MODE_TINY_WINDOW) {
            mClickView.setVisibility(GONE);
        } else {
            mClickView.setVisibility(VISIBLE);
        }
    }

    @Override
    public void setProgress(int duration, int position) {

    }

    @Override
    public void onLockStateChanged(boolean isLocked) {
        if (isLocked) {
           setVisibility(GONE);
        } else {
            setVisibility(VISIBLE);
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }


    @Override
    protected void onDetachedFromWindow() {
        if (mCastManager != null) {
            mCastManager.release();
        }
        super.onDetachedFromWindow();

    }

    public void startDiscoveryDevice() {
        VideoLogUtils.d("reSearch:" + 0.0);
        if (mDiscoveryManagerListener != null) {
            mDiscoveryManagerListener.onSearchStart();
        }
        if (mCastManager != null) {
            mCastManager.startDiscovery();
        }
    }

    public void stopDiscovery() {
        VideoLogUtils.d("reSearch:" + 0.0);
        if (mDiscoveryManagerListener != null) {
            mDiscoveryManagerListener.onSearchEnd();
        }
        if (mCastManager != null) {
            mCastManager.stopDiscovery();
        }
    }


    @Override
    public void onDeviceAdded(DiscoveryManager manager, ConnectableDevice device) {
        CastLogUtils.d("onDeviceAdded: " + device.toString());
        if (mDiscoveryManagerListener != null) {
            mDiscoveryManagerListener.onDeviceAdded(manager, device);
        }

    }

    @Override
    public void onDeviceUpdated(DiscoveryManager manager, ConnectableDevice device) {
    }

    @Override
    public void onDiscoveryComplete(DiscoveryManager manager) {
        if (null != castControlView && mDiscoveryManagerListener != null) {
            mDiscoveryManagerListener.onSearchEnd();
        }
    }

    @Override
    public void onDeviceRemoved(DiscoveryManager manager, ConnectableDevice device) {
        if (mDiscoveryManagerListener != null) {
            mDiscoveryManagerListener.onDeviceRemoved(manager, device);
        }
    }

    @Override
    public void onDiscoveryFailed(DiscoveryManager manager, ServiceCommandError error) {
    }

    public void onSelectConnectableDevice(ConnectableDevice device) {
        if (mCastManager != null) {
            mCastManager.connect(device);
        }
    }

    @Override
    public void onDeviceReady(ConnectableDevice device) {
        VideoLogUtils.d("onDeviceReady:" + device.getFriendlyName());
        mDiscoveryManagerListener.onSelectSuccess();
        castControlView = new PlayerCastControlView(getContext());
        castControlView.setOnCompleteListener(() -> {
            mCastManager.disconnect();
            removeView(castControlView);
            mControlWrapper.start();
            mControlWrapper.show();
            castControlView = null;
        });
        addView(castControlView);
        mControlWrapper.pause();
        CustomCastBean customCastBean = new CustomCastBean.Builder()
                .setMediaType(MediaType.MEDIA_TYPE_VIDEO)
                .setPath(mControlWrapper.getUrl())
                .setTitle("")
                .setDescription("我是球星App投屏观看高清视频")
                .build();
        mCastManager.setPlayerMedia(customCastBean);
        castControlView.setCastManager(mCastManager);
        mCastManager.stopDiscovery();
    }

    @Override
    public void onDeviceDisconnected(ConnectableDevice device) {
        VideoLogUtils.d("onDeviceDisconnected:" + device.getFriendlyName());
        if (castControlView != null) {
            castControlView.onDeviceDisconnected();
        }
    }

    @Override
    public void onPairingRequired(ConnectableDevice device, DeviceService service, DeviceService.PairingType pairingType) {
        VideoLogUtils.d("onPairingRequired:" + device.getFriendlyName());
        if (castControlView != null) {
            castControlView.onPairingRequired();
        }
    }

    @Override
    public void onCapabilityUpdated(ConnectableDevice device, List<String> added, List<String> removed) {
        VideoLogUtils.d("onCapabilityUpdated:" + device.getFriendlyName());

    }

    @Override
    public void onConnectionFailed(ConnectableDevice device, ServiceCommandError error) {
        VideoLogUtils.d("onConnectionFailed:" + device.getFriendlyName());
        if (castControlView != null) {
            castControlView.onConnectionFailed();
        }
    }


    @Override
    public void onCastPlayerState(ConnectableDevice device, CustomCastBean castBean, MediaControl.PlayStateStatus status) {
        if (castControlView != null) {
            castControlView.onPlayStateStatus(status);
        }
    }

    @Override
    public void onCastPositionUpdate(ConnectableDevice device, CustomCastBean castBean, Long duration, Long position) {
        if (castControlView != null) {
            castControlView.onPositionUpdate(duration, position);
        }
    }


    @Override
    public void onError(ConnectableDevice device, CustomCastBean castBean, ServiceCommandError e) {
        if (castControlView != null) {
            castControlView.onError(-1, e);
        }
    }


    public interface OnErrorListener {
        void onError(String msg);
    }


}
