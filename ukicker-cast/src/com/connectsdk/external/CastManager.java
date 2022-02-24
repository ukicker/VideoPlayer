package com.connectsdk.external;

import android.content.Context;
import android.util.Log;

import com.connectsdk.core.AppInfo;
import com.connectsdk.core.MediaInfo;
import com.connectsdk.core.Util;
import com.connectsdk.device.ConnectableDevice;
import com.connectsdk.discovery.DiscoveryManager;
import com.connectsdk.service.capability.Launcher;
import com.connectsdk.service.capability.MediaControl;
import com.connectsdk.service.capability.MediaPlayer;
import com.connectsdk.service.capability.VolumeControl;
import com.connectsdk.service.capability.listeners.ResponseListener;
import com.connectsdk.service.command.ServiceCommandError;
import com.connectsdk.service.sessions.LaunchSession;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

/**
 * @PackageName : com.connectsdk
 * @File : CastSource.java
 * @Date : 2021/12/30 2021/12/30
 * @Author : K
 * @E-mail : vip@devkit.vip
 * @Version : V 1.0
 * @Describe ：
 */
public class CastManager {

    private static String TAG = "ukicker_cast";

    private static CastManager instance;
    private DiscoveryResultListener mDiscoveryResultListener;
    private CastPlayerListener mCastPlayerListener;
    private ConnectListener mConnectListener;


    private DiscoveryManager mDiscoveryManager;
    private ConnectableDevice mDevice;
    private Launcher mLauncher;
    private MediaPlayer mMediaPlayer;
    private LaunchSession launchSession;
    private MediaControl mMediaControl;
    private VolumeControl mVolumeControl;
    private MediaControl.PlayStateStatus mPlayStateStatus = MediaControl.PlayStateStatus.Idle;

    private CustomCastBean mCastBean;

    private Timer mDiscoveryTimer;
    private Timer mRefreshTimer;
    private int mDiscoveryTime = 0;
    private long totalTimeDuration = 0L;
    private final int REFRESH_INTERVAL_MS = (int) TimeUnit.SECONDS.toMillis(1);


    public static synchronized void init(Context context) {
        instance = new CastManager(context);
    }

    public static CastManager getInstance() {
        if (instance == null) {
            throw new Error("Call CastManager.init(Context) first");
        }
        return instance;
    }

    public CastManager(Context context) {
        DiscoveryManager.init(context);
        mDiscoveryManager = DiscoveryManager.getInstance();
        mDiscoveryManager.registerDefaultDeviceTypes();
        mDiscoveryManager.setPairingLevel(DiscoveryManager.PairingLevel.ON);
    }

    public void setCastPlayerListener(CastPlayerListener castPlayerListener) {
        mCastPlayerListener = castPlayerListener;
    }


    public void setDiscoveryResultListener(DiscoveryResultListener discoveryResultListener) {
        mDiscoveryResultListener = discoveryResultListener;
    }

    public void setConnectListener(ConnectListener connectListener) {
        mConnectListener = connectListener;
    }

    public void startDiscovery() {
        startDiscovery(20);
    }

    public void startDiscovery(int second) {
        CastLogUtils.d("startDiscovery: " + second + " | mDiscoveryResultListener is null:" + (mDiscoveryResultListener == null));
        if (mDiscoveryTimer != null) {
            mDiscoveryTimer.cancel();
            mDiscoveryTimer = null;
        }
        if (mDiscoveryResultListener != null) {
            mDiscoveryManager.addListener(mDiscoveryResultListener);
        }
        mDiscoveryManager.start();
        mDiscoveryTimer = new Timer();
        mDiscoveryTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                mDiscoveryTime++;
                CastLogUtils.d("startDiscovery Timer: " + mDiscoveryTime);
                if (mDiscoveryTime >= second) {
                    this.cancel();
                    stopDiscovery();
                    mDiscoveryTime = 0;
                }
            }
        }, 0, REFRESH_INTERVAL_MS);
    }

    public void stopDiscovery() {
        if (mDiscoveryManager!=null){
            mDiscoveryManager.stop();
        }
        if (mDiscoveryResultListener != null) {
            Util.runOnUI(() -> mDiscoveryResultListener.onDiscoveryComplete(mDiscoveryManager));
            mDiscoveryManager.removeListener(mDiscoveryResultListener);
        }
        if (mDiscoveryTimer != null) {
            mDiscoveryTimer.cancel();
            mDiscoveryTimer = null;
        }
    }

    public void connect(ConnectableDevice device) {
        if (device == null) {
            throw new Error("ConnectableDevice must no null");
        }
        this.mDevice = device;
        mDevice.connect();

        if (mDevice.hasCapability(MediaPlayer.Any)) {
            mMediaPlayer = mDevice.getCapability(MediaPlayer.class);
        }
        if (mDevice.hasCapability(MediaPlayer.Any)) {
            mMediaControl = mDevice.getCapability(MediaControl.class);
            mMediaControl.subscribePlayState(mPlayStateListener);
        }
        if (mDevice.hasCapability(VolumeControl.Any)) {
            mVolumeControl = mDevice.getCapability(VolumeControl.class);
        }
        if (mConnectListener != null) {
            mDevice.addListener(mConnectListener);
        }
    }

    public boolean disconnect() {
        if (mDevice != null) {
            stop();
            mMediaPlayer = null;
            mDevice.disconnect();
            mDevice = null;
            return true;
        }
        return false;
    }

    public ConnectableDevice getConnectableDevice() {
        return mDevice;
    }


    public void setPlayerMedia(CustomCastBean castBean) {
        this.mCastBean = castBean;
        if (mDevice == null) {
            throw new Error("ConnectableDevice must  no null");
        }
        if (mCastBean == null) {
            throw new Error("mCastBean must  no null");
        }
        if (null == mCastBean.getMimeType()) {
            String mimeType = "video/mp4";
            mCastBean.setMimeType(mimeType);
        }
        MediaInfo mediaInfo = new MediaInfo.Builder(mCastBean.getPath(), mCastBean.getMimeType())
                .setTitle(mCastBean.getTitle())
                .setDescription(mCastBean.getDescription())
                .setIcon("")
                .build();
        mMediaPlayer.playMedia(mediaInfo, mCastBean.isLoopMode(), new MediaPlayer.LaunchListener() {
            @Override
            public void onSuccess(MediaPlayer.MediaLaunchObject object) {
                mMediaControl = object.mediaControl;
            }

            @Override
            public void onError(ServiceCommandError error) {

            }
        });
    }


    public void pause() {
        if (mMediaControl != null) {
            mMediaControl.pause(new ActionResponseListener());
        }
    }

    public void play() {
        if (mMediaControl != null) {
            mMediaControl.play(new ActionResponseListener());
        }
    }

    public void seekTo(long position) {
        if (mMediaControl != null) {
            mMediaControl.seek(position, new ActionResponseListener());
        }
    }

    public void setVolume(long volume) {
        if (mVolumeControl != null) {
            mVolumeControl.setVolume(volume, new ActionResponseListener());
        }

    }


    public void setMute(boolean mute) {
        if (mVolumeControl != null) {
            mVolumeControl.setMute(mute, new ActionResponseListener());
        }
    }

    public void stop() {
        if (mMediaControl != null) {
            mMediaControl.stop(null);
        }
        stopUpdating();
    }


    public void displayImage(CustomCastBean bean) {
        if (mDevice != null && !mDevice.isConnected() && mMediaPlayer == null) {
            return;
        }
        MediaInfo mediaInfo = new MediaInfo.Builder(bean.getPath(), "image/jpeg")
                .setTitle(bean.getTitle())
                .setDescription(bean.getDescription())
                .setIcon("")
                .build();
        mMediaPlayer.displayImage(mediaInfo, new MediaPlayer.LaunchListener() {
            @Override
            public void onSuccess(MediaPlayer.MediaLaunchObject object) {

            }

            @Override
            public void onError(ServiceCommandError error) {
            }
        });
    }

    public void release() {
        stopDiscovery();
        stop();
        disconnect();
    }


    private void startUpdating() {
        if (mRefreshTimer != null) {
            mRefreshTimer.cancel();
            mRefreshTimer = null;
        }
        mRefreshTimer = new Timer();
        mRefreshTimer.schedule(new TimerTask() {

            @Override
            public void run() {
                CastLogUtils.d("Updating information");
                if (mMediaControl != null && mDevice != null && mDevice.hasCapability(MediaControl.Position)) {
                    mMediaControl.getPosition(mPositionListener);
                }
            }
        }, 0, REFRESH_INTERVAL_MS);
    }

    private void stopUpdating() {
        if (mRefreshTimer == null) {
            return;
        }
        mRefreshTimer.cancel();
        mRefreshTimer = null;
    }

    public MediaControl.PlayStateStatus getPlayStateStatus() {
        return mPlayStateStatus;
    }

    private final MediaControl.PlayStateListener mPlayStateListener = new MediaControl.PlayStateListener() {
        @Override
        public void onSuccess(MediaControl.PlayStateStatus status) {
            CastLogUtils.d("PlayStateStatus：" + status);
            mPlayStateStatus = status;
            switch (status) {
                case Idle:
                    break;
                case Playing:
                    startUpdating();
                    if (mMediaControl != null) {
                        mMediaControl.getDuration(mDurationListener);
                    }
                    break;
                case Buffering:
                    break;
                case Finished:
                    totalTimeDuration = -1;
                case Paused:
                case Unknown:
                default:
                    stopUpdating();
                    break;
            }

            if (mCastPlayerListener != null) {
                mCastPlayerListener.onCastPlayerState(mDevice, mCastBean, status);
            }
        }

        @Override
        public void onError(ServiceCommandError error) {
            if (mCastPlayerListener != null) {
                mCastPlayerListener.onError(mDevice, mCastBean, error);
            }
        }
    };

    private final MediaControl.DurationListener mDurationListener = new MediaControl.DurationListener() {
        @Override
        public void onSuccess(Long duration) {
            totalTimeDuration = duration;
        }

        @Override
        public void onError(ServiceCommandError error) {
            if (mCastPlayerListener != null) {
                mCastPlayerListener.onError(mDevice, mCastBean, error);
            }
        }
    };

    private final MediaControl.PositionListener mPositionListener = new MediaControl.PositionListener() {
        @Override
        public void onSuccess(Long position) {
            if (mCastPlayerListener != null) {
                mCastPlayerListener.onCastPositionUpdate(mDevice, mCastBean, totalTimeDuration, position);
            }
        }

        @Override
        public void onError(ServiceCommandError error) {
            if (mCastPlayerListener != null) {
                mCastPlayerListener.onError(mDevice, mCastBean, error);
            }
        }
    };

    public class ActionResponseListener<T> implements ResponseListener<T> {

        public ActionResponseListener() {
        }

        @Override
        public void onSuccess(T t) {

        }

        @Override
        public void onError(ServiceCommandError error) {
            if (mCastPlayerListener != null) {
                mCastPlayerListener.onError(mDevice, mCastBean, error);
            }
        }
    }
}