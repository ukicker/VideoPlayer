package com.yc.video.ui.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.yc.video.R;
import com.yc.video.bridge.ControlWrapper;
import com.yc.video.config.ConstantKeys;
import com.yc.video.kernel.utils.VideoLogUtils;
import com.yc.video.player.VideoPlayerHelper;
import com.yc.video.player.VideoViewManager;
import com.yc.video.tool.PlayerUtils;
import com.yc.video.ui.pip.FloatVideoManager;
import com.yc.video.ui.pip.FloatVideoView;

/**
 * @PackageName : com.yc.video.ui.view
 * @File : CustomControlView.java
 * @Date : 2021/10/28 2021/10/28
 * @Author : K
 * @E-mail : vip@devkit.vip
 * @Version : V 1.0
 * @Describe ：
 */
public class CustomPipControlView extends FrameLayout implements InterControlView, View.OnClickListener {


    private FrameLayout mPlayerControlContainer;
    private ImageView mIvClose;
    private ImageView mIvBack;
    private ImageView mIvPlay;

    private ControlWrapper mControlWrapper;

    public CustomPipControlView(@NonNull Context context) {
        super(context);
        initView();
    }

    public CustomPipControlView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public CustomPipControlView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        setVisibility(GONE);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.player_custom_pip_contral, this, true);
        mPlayerControlContainer = view.findViewById(R.id.player_control_container);
        mIvClose = view.findViewById(R.id.iv_close);
        mIvBack = view.findViewById(R.id.iv_back);
        mIvPlay = view.findViewById(R.id.iv_play);
        mIvClose.setOnClickListener(this);
        mIvPlay.setOnClickListener(this);
        mIvBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == mIvClose) {
            Activity activity = PlayerUtils.scanForActivity(getContext());
            if (null != activity && !PlayerUtils.isTopApp(activity)) {
                mControlWrapper.release();
            }else {
                mControlWrapper.pause();
            }
            mControlWrapper.stopTinyScreen();

        } else if (v == mIvPlay) {
            mControlWrapper.togglePlay();
        } else if (v == mIvBack) {
            mControlWrapper.stopTinyScreen();
            Activity activity = PlayerUtils.scanForActivity(getContext());
            if (null != activity && !PlayerUtils.isTopApp(activity)) {
                Intent intent = new Intent(activity, activity.getClass());
                if (activity.getIntent() != null) {
                    intent.putExtras(activity.getIntent());
                }
                activity.startActivity(intent);
            }
        } else {

        }
    }

    @Override
    public void attach(@NonNull ControlWrapper controlWrapper) {
        mControlWrapper = controlWrapper;
    }

    @Override
    public View getView() {
        return this;
    }

    @Override
    public void onVisibilityChanged(boolean isVisible, Animation anim) {
        VideoLogUtils.e("onVisibilityChanged:" + this.getParent().getClass().getSimpleName());
        mPlayerControlContainer.setVisibility(isVisible ? VISIBLE : GONE);
    }

    @Override
    public void onPlayStateChanged(int playState) {
        switch (playState) {
            case ConstantKeys.CurrentState.STATE_IDLE:
            case ConstantKeys.CurrentState.STATE_START_ABORT:
            case ConstantKeys.CurrentState.STATE_PREPARING:
            case ConstantKeys.CurrentState.STATE_PREPARED:
            case ConstantKeys.CurrentState.STATE_ERROR:
            case ConstantKeys.CurrentState.STATE_BUFFERING_PLAYING:
            case ConstantKeys.CurrentState.STATE_ONCE_LIVE:
                break;
            case ConstantKeys.CurrentState.STATE_PLAYING:
                mIvPlay.setSelected(true);
                break;
            case ConstantKeys.CurrentState.STATE_PAUSED:
                mIvPlay.setSelected(false);
                break;
            case ConstantKeys.CurrentState.STATE_BUFFERING_PAUSED:
            case ConstantKeys.CurrentState.STATE_COMPLETED:
                mIvPlay.setSelected(mControlWrapper.isPlaying());
                break;
        }
    }

    @Override
    public void onPlayerStateChanged(int playerState) {
        switch (playerState) {
            case ConstantKeys.PlayMode.MODE_NORMAL:
            case ConstantKeys.PlayMode.MODE_FULL_SCREEN:
                setVisibility(GONE);
                break;
            case ConstantKeys.PlayMode.MODE_TINY_WINDOW:
                setVisibility(VISIBLE);
                break;
        }
    }

    @Override
    public void setProgress(int duration, int position) {

    }

    @Override
    public void onLockStateChanged(boolean isLocked) {

    }
}
