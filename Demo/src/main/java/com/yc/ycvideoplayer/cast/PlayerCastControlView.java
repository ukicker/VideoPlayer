package com.yc.ycvideoplayer.cast;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.connectsdk.external.CastManager;
import com.connectsdk.service.capability.MediaControl;
import com.yc.video.R;
import com.yc.video.bridge.ControlWrapper;
import com.yc.video.kernel.utils.VideoLogUtils;
import com.yc.video.ui.view.InterControlView;

import java.util.Formatter;
import java.util.Locale;

/**
 * @PackageName : com.yc.video.ui.cast
 * @File : PlayerCastC.java
 * @Date : 2021/12/18 2021/12/18
 * @Author : K
 * @E-mail : vip@devkit.vip
 * @Version : V 1.0
 * @Describe ：
 */
public class PlayerCastControlView extends FrameLayout implements InterControlView, View.OnClickListener {

    private RelativeLayout mRlCastConsole;
    private TextView mTvCastStatus;
    private TextView mTvCastDescribe;
    private LinearLayout mLlCastFail;
    private TextView mTvCastReset;
    private TextView mTvCastExit;
    private ImageView mIvCastPlay;
    private LinearLayout mLlCastProgress;
    private TextView mTvCastCurrentTime;
    private SeekBar mCastSeekBar;
    private TextView mTvCastDurationTime;


    private OnCompleteListener mOnCompleteListener;
    private CastManager mCastManager;

    public void setCastManager(CastManager castManager) {
        this.mCastManager = castManager;
    }

    public interface OnCompleteListener {
        void onComplete();
    }

    public void setOnCompleteListener(OnCompleteListener onCompleteListener) {
        mOnCompleteListener = onCompleteListener;
    }

    public PlayerCastControlView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public PlayerCastControlView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public PlayerCastControlView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }


    private void init(Context context) {
        View itemView = LayoutInflater.from(getContext()).inflate(R.layout.player_video_cast, this, true);
        mRlCastConsole = itemView.findViewById(R.id.rl_cast_console);
        mTvCastStatus = itemView.findViewById(R.id.tv_cast_status);
        mTvCastDescribe = itemView.findViewById(R.id.tv_cast_describe);
        mLlCastFail = itemView.findViewById(R.id.ll_cast_fail);
        mTvCastReset = itemView.findViewById(R.id.tv_cast_reset);
        mTvCastExit = itemView.findViewById(R.id.tv_cast_exit);
        mIvCastPlay = itemView.findViewById(R.id.iv_cast_play);
        mLlCastProgress = itemView.findViewById(R.id.ll_cast_progress);
        mTvCastCurrentTime = itemView.findViewById(R.id.tv_cast_current_time);
        mCastSeekBar = itemView.findViewById(R.id.cast_seek_bar);
        mTvCastDurationTime = itemView.findViewById(R.id.tv_cast_duration_time);
        mIvCastPlay.setOnClickListener(this);
        itemView.findViewById(R.id.tv_cast_exit).setOnClickListener(this);

        mCastSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    VideoLogUtils.d("onProgressChanged:" + seekBar.getProgress());
                    mCastManager.seekTo(seekBar.getProgress());
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mCastManager.seekTo(seekBar.getProgress());
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tv_cast_exit) {
            if (mOnCompleteListener != null) {
                mOnCompleteListener.onComplete();
            }
        } else if (v.getId() == R.id.iv_cast_play) {
            if (mCastManager != null) {
                MediaControl.PlayStateStatus stateStatus = mCastManager.getPlayStateStatus();
                if (stateStatus == MediaControl.PlayStateStatus.Playing) {
                    mCastManager.pause();
                } else {
                    mCastManager.play();
                }
            }
        }

    }

    @Override
    public void attach(@NonNull ControlWrapper controlWrapper) {

    }

    @Override
    public View getView() {
        return this;
    }

    @Override
    public void onVisibilityChanged(boolean isVisible, Animation anim) {

    }

    @Override
    public void onPlayStateChanged(int playState) {

    }

    @Override
    public void onPlayerStateChanged(int playerState) {

    }

    @Override
    public void setProgress(int duration, int position) {

    }

    @Override
    public void onLockStateChanged(boolean isLocked) {

    }

    public void onConnectionFailed() {
    }

    public void onDeviceDisconnected() {
    }

    public void onPairingRequired() {
    }

    public void onPositionUpdate(Long duration, Long position) {
        mTvCastCurrentTime.setText(getStringTime(position));
        mTvCastDurationTime.setText(getStringTime(duration));
        mCastSeekBar.setMax(duration.intValue());
        mCastSeekBar.setProgress(position.intValue());
    }

    public void onPlayStateStatus(MediaControl.PlayStateStatus playState) {
        switch (playState) {
            case Playing:
                mIvCastPlay.setImageResource(R.mipmap.player_ic_pause);
                break;
            case Paused:
                mIvCastPlay.setImageResource(R.mipmap.player_ic_play);
                break;
            case Unknown:
                break;
            case Finished:
                mIvCastPlay.setImageResource(R.mipmap.player_ic_play);
                VideoLogUtils.d("Play Finished");
                mTvCastExit.performClick();
                break;
            default:
                break;
        }
    }

    public void onError(int code, Throwable throwable) {
    }

    public String getStringTime(long timeMs) {
        StringBuilder formatBuilder = new StringBuilder();
        Formatter formatter = new Formatter(formatBuilder, Locale.CHINA);
        long totalSeconds = timeMs / 1000;
        long seconds = totalSeconds % 60;
        long minutes = (totalSeconds / 60) % 60;
        long hours = totalSeconds / 3600;
        return formatter.format("%02d:%02d:%02d", hours, minutes, seconds).toString();
    }
}
