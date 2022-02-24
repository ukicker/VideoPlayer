/*
Copyright 2017 yangchong211（github.com/yangchong211）

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package com.yc.video.ui.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.yc.video.R;
import com.yc.video.bridge.ControlWrapper;
import com.yc.video.config.ConstantKeys;
import com.yc.video.controller.IGestureComponent;
import com.yc.video.kernel.utils.VideoLogUtils;
import com.yc.video.tool.PlayerUtils;
import com.yc.video.tool.SpannableHelper;
import com.yc.video.tool.TimeFormater;

/**
 * <pre>
 *     desc  : 手势控制
 *     revise: 用于滑动改变亮度和音量的功能
 * </pre>
 */
public class CustomGestureView extends FrameLayout implements IGestureComponent {

    private Context mContext;
    private ControlWrapper mControlWrapper;

    private FrameLayout mFlCenterContainer;
    private TextView mTvSpeedPlay;
    private TextView mTvSeekTo;
    private LinearLayout mLlChangePercent;
    private ImageView mIvChangePercent;
    private SeekBar mSeekChangePercent;
    private float lastSpeed = 1f;

    public CustomGestureView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public CustomGestureView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CustomGestureView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        this.mContext = context;
        setVisibility(GONE);
        View view = LayoutInflater.from(mContext).inflate(R.layout.player_custom_video_gesture, this, true);
        initFindViewById(view);
        initListener();
    }

    private void initFindViewById(View view) {
        mFlCenterContainer = view.findViewById(R.id.fl_center_container);
        mTvSpeedPlay = view.findViewById(R.id.tv_speed_play);
        mTvSeekTo = view.findViewById(R.id.tv_seek_to);
        mLlChangePercent = view.findViewById(R.id.ll_change_percent);
        mIvChangePercent = view.findViewById(R.id.iv_change_percent);
        mSeekChangePercent = view.findViewById(R.id.seek_change_percent);
    }


    private void initListener() {
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

    }

    @Override
    public void onPlayerStateChanged(int playerState) {

    }


    /**
     * 开始滑动
     */
    @Override
    public void onStartSlide() {
//        mControlWrapper.hide();
        mFlCenterContainer.setVisibility(VISIBLE);
        mFlCenterContainer.setAlpha(1f);

    }

    /**
     * 结束滑动
     * 这个是指，手指抬起或者意外结束事件的时候，调用这个方法
     */
    @Override
    public void onStopSlide() {
        mFlCenterContainer.animate()
                .alpha(0f)
                .setDuration(300)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        if (mTvSpeedPlay.getVisibility()==VISIBLE){
                            mControlWrapper.setSpeed(lastSpeed);
                        }
                        mTvSeekTo.setVisibility(GONE);
                        mTvSpeedPlay.setVisibility(GONE);
                        mLlChangePercent.setVisibility(GONE);
                        mFlCenterContainer.setVisibility(GONE);
                    }
                })
                .start();
    }

    /**
     * 滑动调整进度
     *
     * @param slidePosition   滑动进度
     * @param currentPosition 当前播放进度
     * @param duration        视频总长度
     */
    @Override
    public void onPositionChange(int slidePosition, int currentPosition, int duration) {
        if (slidePosition > currentPosition) {
//            mIvIcon.setImageResource(R.drawable.ic_player_fast_forward);
        } else {
//            mIvIcon.setImageResource(R.drawable.ic_player_fast_rewind);
        }
        showSeekTo(slidePosition);
    }

    /**
     * 滑动调整亮度
     *
     * @param percent 亮度百分比
     */
    @Override
    public void onBrightnessChange(int percent) {
        setBrightness(percent);
    }

    /**
     * 滑动调整音量
     *
     * @param percent 音量百分比
     */
    @Override
    public void onVolumeChange(int percent) {
        setVolume(percent);
    }

    @Override
    public void onLongPress() {
        onStartSlide();
        showSpeed();
    }

    @Override
    public void onPlayStateChanged(int playState) {
        if (playState == ConstantKeys.CurrentState.STATE_IDLE
                || playState == ConstantKeys.CurrentState.STATE_START_ABORT
                || playState == ConstantKeys.CurrentState.STATE_PREPARING
                || playState == ConstantKeys.CurrentState.STATE_PREPARED
                || playState == ConstantKeys.CurrentState.STATE_ERROR
                || playState == ConstantKeys.CurrentState.STATE_BUFFERING_PLAYING
                || playState == ConstantKeys.CurrentState.STATE_ONCE_LIVE) {
            setVisibility(GONE);
        } else {
            setVisibility(VISIBLE);
        }
    }

    @Override
    public void setProgress(int duration, int position) {

    }

    @Override
    public void onLockStateChanged(boolean isLock) {

    }

    @Override
    public void onGestureChanged(boolean enable) {
        if (enable){

        }else {

        }

    }


    int mScreenMode = 1001;

    public void showSpeed() {
        lastSpeed = mControlWrapper.getSpeed() < 0.5 ? 1.0F : mControlWrapper.getSpeed();
        mControlWrapper.setSpeed(2F);
        if (mScreenMode == ConstantKeys.PlayMode.MODE_NORMAL) {
            SpannableHelper.Builder(getContext(), "")
                    .append("2.0X").setSize(15).setForegroundColor(Color.parseColor("#FF3044"))
                    .append("倍数播放中").setSize(12).setForegroundColor(Color.parseColor("#FFFFFF"))
                    .into(mTvSpeedPlay);
        } else {
            SpannableHelper.Builder(getContext(), "")
                    .append("2.0X").setSize(24).setForegroundColor(Color.parseColor("#FF3044"))
                    .append("倍数播放中").setSize(18).setForegroundColor(Color.parseColor("#FFFFFF"))
                    .into(mTvSpeedPlay);
        }
        mTvSpeedPlay.post(() -> mTvSpeedPlay.setVisibility(VISIBLE));
    }

    public void showSeekTo(long currentPosition) {
        if (mScreenMode == ConstantKeys.PlayMode.MODE_NORMAL) {
            SpannableHelper.Builder(getContext(), "")
                    .append(TimeFormater.formatMs(currentPosition)).setSize(12).setForegroundColor(Color.parseColor("#FF3044"))
                    .append("/" + TimeFormater.formatMs(mControlWrapper.getDuration())).setSize(12).setForegroundColor(Color.parseColor("#FFFFFF"))
                    .into(mTvSeekTo);
        } else {
            SpannableHelper.Builder(getContext(), "")
                    .append(TimeFormater.formatMs(currentPosition)).setSize(24).setForegroundColor(Color.parseColor("#FF3044"))
                    .append("/" + TimeFormater.formatMs(mControlWrapper.getDuration())).setSize(21).setForegroundColor(Color.parseColor("#FFFFFF"))
                    .into(mTvSeekTo);
        }
        mTvSeekTo.setVisibility(VISIBLE);
    }

    public void setBrightness(int percent) {
        mLlChangePercent.setVisibility(VISIBLE);
        if (percent <= 0) {
            mIvChangePercent.setImageResource(R.mipmap.player_brightness_0);
            mIvChangePercent.setAlpha(0.1f);
            mLlChangePercent.setAlpha(0.1f);
        } else if (percent > 1) {
            mLlChangePercent.setAlpha(1.0f);
            mIvChangePercent.setAlpha(1.0f);
            mIvChangePercent.setImageResource(R.mipmap.player_brightness_100);
        }
        mSeekChangePercent.setProgress(percent);
    }

    public void setVolume(int volume) {
        mLlChangePercent.setVisibility(VISIBLE);
        mIvChangePercent.setAlpha(1.0f);
        mLlChangePercent.setAlpha(1.0f);
        if (volume <= 0) {
            mIvChangePercent.setImageResource(R.mipmap.player_ic_mute);
        } else {
            mIvChangePercent.setImageResource(R.mipmap.player_ic_volume_change);
        }
        mSeekChangePercent.setProgress(volume);
    }



}
