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

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.yc.video.config.ConstantKeys;
import com.yc.video.bridge.ControlWrapper;
import com.yc.video.config.VideoPlayerConfig;
import com.yc.video.kernel.utils.VideoLogUtils;
import com.yc.video.tool.PlayerUtils;

import com.yc.video.R;

/**
 * <pre>
 *     @author yangchong
 *     blog  : https://github.com/yangchong211
 *     time  : 2017/11/9
 *     desc  : 底部控制栏视图
 *     revise: 用于普通播放器
 * </pre>
 */
public class CustomBottomView extends FrameLayout implements InterControlView,
        View.OnClickListener, SeekBar.OnSeekBarChangeListener {

    private Context mContext;
    protected ControlWrapper mControlWrapper;
    private LinearLayout mLlBottomContainer;
    private LinearLayout mLlPortraitContainer;
    private ImageView mIvPortraitPlay;
    private TextView mTvPortraitPosition;
    private PointSeekBar mPortraitProgress;
    private TextView mTvPortraitDuration;
    private ImageView mIvPortraitFull;
    private LinearLayout mLlLandscapeContainer;
    private TextView mTvLandscapePosition;
    private PointSeekBar mLandscapeProgress;
    private TextView mTvLandscapeDuration;
    private RelativeLayout mRlTvLandscapeBottom;
    private ImageView mIvLandscapePlay;
    private ImageView mIvLandscapeTiny;
    private TextView mTvLandscapeSpeed;
    private SeekBar mPbBottomProgress;

    private CustomSpeedView speedView;


    private boolean mIsDragging;
    private boolean mIsShowBottomProgress = true;

    public CustomBottomView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public CustomBottomView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CustomBottomView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        this.mContext = context;
        setVisibility(GONE);
        View view = LayoutInflater.from(getContext()).inflate(getLayoutId(), this, true);
        initFindViewById(view);
        initListener();
        //5.1以下系统SeekBar高度需要设置成WRAP_CONTENT
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP_MR1) {
            mPbBottomProgress.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
        }
    }


    private void initFindViewById(View view) {

        mLlBottomContainer = view.findViewById(R.id.ll_bottom_container);
        mLlPortraitContainer = view.findViewById(R.id.ll_portrait_container);
        mIvPortraitPlay = view.findViewById(R.id.iv_portrait_play);
        mTvPortraitPosition = view.findViewById(R.id.tv_portrait_position);
        mPortraitProgress = view.findViewById(R.id.portrait_progress);
        mTvPortraitDuration = view.findViewById(R.id.tv_portrait_duration);
        mIvPortraitFull = view.findViewById(R.id.iv_portrait_full);
        mLlLandscapeContainer = view.findViewById(R.id.ll_landscape_container);
        mTvLandscapePosition = view.findViewById(R.id.tv_landscape_position);
        mLandscapeProgress = view.findViewById(R.id.landscape_progress);
        mTvLandscapeDuration = view.findViewById(R.id.tv_landscape_duration);
        mRlTvLandscapeBottom = view.findViewById(R.id.rl_tv_landscape_bottom);
        mIvLandscapePlay = view.findViewById(R.id.iv_landscape_play);
        mIvLandscapeTiny = view.findViewById(R.id.iv_landscape_tiny);
        mTvLandscapeSpeed = view.findViewById(R.id.tv_landscape_speed);
        mPbBottomProgress = view.findViewById(R.id.pb_bottom_progress);

        mLlBottomContainer = view.findViewById(R.id.ll_bottom_container);
        mLlPortraitContainer = view.findViewById(R.id.ll_portrait_container);
        mLlLandscapeContainer = view.findViewById(R.id.ll_landscape_container);


        mPbBottomProgress = view.findViewById(R.id.pb_bottom_progress);
    }

    private void initListener() {
        mPbBottomProgress.setOnSeekBarChangeListener(this);
        mPortraitProgress.setOnSeekBarChangeListener(this);
        mLandscapeProgress.setOnSeekBarChangeListener(this);
        mIvPortraitFull.setOnClickListener(this);
        mIvLandscapePlay.setOnClickListener(this);
        mIvPortraitPlay.setOnClickListener(this);
        mIvLandscapeTiny.setOnClickListener(this);
        mTvLandscapeSpeed.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        if (v == mIvPortraitFull) {
            toggleFullScreen();
        } else if (v == mIvLandscapePlay || v == mIvPortraitPlay) {
            mControlWrapper.togglePlay();
        } else if (v == mTvLandscapeSpeed) {
            ViewGroup viewGroup = (ViewGroup) this.getParent();
            if (viewGroup instanceof BasisVideoController) {
                BasisVideoController videoController = (BasisVideoController) viewGroup;
                if (speedView==null){
                    speedView = new CustomSpeedView(mContext);
                }
                videoController.removeControlComponent(speedView);
                videoController.addControlComponent(speedView);
            }
        } else if (v == mIvLandscapeTiny) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!Settings.canDrawOverlays(getContext())) {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getContext().getPackageName()));
                    getContext().startActivity(intent);
                    return;
                }
            }
            mControlWrapper.startTinyScreen();
        }
    }

    protected int getLayoutId() {
        return R.layout.player_custom_video_bottom;
    }

    /**
     * 是否显示底部进度条，默认显示
     */
    public void showBottomProgress(boolean isShow) {
        mIsShowBottomProgress = isShow;
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
        if (isVisible) {
            mLlBottomContainer.setVisibility(VISIBLE);
            if (anim != null) {
                mLlBottomContainer.startAnimation(anim);
            }
            if (mIsShowBottomProgress) {
                mPbBottomProgress.setVisibility(GONE);
            }
        } else {
            mLlBottomContainer.setVisibility(GONE);
            if (anim != null) {
                mLlBottomContainer.startAnimation(anim);
            }
            if (mIsShowBottomProgress && !mControlWrapper.isTinyScreen()) {
                mPbBottomProgress.setVisibility(VISIBLE);
                AlphaAnimation animation = new AlphaAnimation(0f, 1f);
                animation.setDuration(300);
                mPbBottomProgress.startAnimation(animation);
            }
        }
    }

    @Override
    public void onPlayStateChanged(int playState) {
        switch (playState) {
            case ConstantKeys.CurrentState.STATE_IDLE:
            case ConstantKeys.CurrentState.STATE_BUFFERING_PLAYING:
                setVisibility(GONE);
                mPbBottomProgress.setProgress(0);
                mPbBottomProgress.setSecondaryProgress(0);
                mPortraitProgress.setProgress(0);
                mPortraitProgress.setSecondaryProgress(0);
                mLandscapeProgress.setProgress(0);
                mLandscapeProgress.setSecondaryProgress(0);

                break;
            case ConstantKeys.CurrentState.STATE_START_ABORT:
            case ConstantKeys.CurrentState.STATE_PREPARING:
            case ConstantKeys.CurrentState.STATE_PREPARED:
            case ConstantKeys.CurrentState.STATE_ERROR:
            case ConstantKeys.CurrentState.STATE_ONCE_LIVE:
                setVisibility(GONE);
                break;
            case ConstantKeys.CurrentState.STATE_PLAYING:
                mIvLandscapePlay.setSelected(true);
                mIvPortraitPlay.setSelected(true);
                if (mIsShowBottomProgress) {
                    if (mControlWrapper.isShowing()) {
                        mPbBottomProgress.setVisibility(GONE);
                        mLlBottomContainer.setVisibility(VISIBLE);
                    } else {
                        mLlBottomContainer.setVisibility(GONE);
                        if (!mControlWrapper.isTinyScreen()) {
                            mPbBottomProgress.setVisibility(VISIBLE);
                        }
                    }
                } else {
                    mLlBottomContainer.setVisibility(GONE);
                }
                setVisibility(VISIBLE);
                //开始刷新进度
                mControlWrapper.startProgress();
                break;
            case ConstantKeys.CurrentState.STATE_PAUSED:
                mIvLandscapePlay.setSelected(false);
                mIvPortraitPlay.setSelected(false);
                break;
            case ConstantKeys.CurrentState.STATE_BUFFERING_PAUSED:
            case ConstantKeys.CurrentState.STATE_COMPLETED:
                mIvLandscapePlay.setSelected(mControlWrapper.isPlaying());
                mIvPortraitPlay.setSelected(mControlWrapper.isPlaying());
                break;
        }
    }

    @Override
    public void onPlayerStateChanged(int playerState) {
        switch (playerState) {
            case ConstantKeys.PlayMode.MODE_NORMAL:
                if (mLlPortraitContainer != null) {
                    mLlPortraitContainer.setVisibility(VISIBLE);
                    mLlLandscapeContainer.setVisibility(GONE);
                }

                break;
            case ConstantKeys.PlayMode.MODE_FULL_SCREEN:
                if (mLlPortraitContainer != null) {
                    mLlPortraitContainer.setVisibility(GONE);
                    mLlLandscapeContainer.setVisibility(VISIBLE);
                }
                break;
            case ConstantKeys.PlayMode.MODE_TINY_WINDOW:
                mLlPortraitContainer.setVisibility(GONE);
                mLlLandscapeContainer.setVisibility(GONE);
                mPbBottomProgress.setVisibility(GONE);
                break;
        }

        Activity activity = PlayerUtils.scanForActivity(mContext);
        if (activity != null && mControlWrapper.hasCutout()) {
            int orientation = activity.getRequestedOrientation();
            int cutoutHeight = mControlWrapper.getCutoutHeight();
            VideoLogUtils.d("orientation:"+orientation+" cutoutHeight:"+cutoutHeight);
            if (orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                mLlBottomContainer.setPadding(0, 0, 0, 0);
                mPbBottomProgress.setPadding(0, 0, 0, 0);
            } else if (orientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                mLlBottomContainer.setPadding(cutoutHeight, 0, 0, 0);
                mPbBottomProgress.setPadding(cutoutHeight, 0, 0, 0);
            } else if (orientation == ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE) {
                mLlBottomContainer.setPadding(0, 0, cutoutHeight, 0);
                mPbBottomProgress.setPadding(0, 0, cutoutHeight, 0);
            }
        }
    }

    /**
     * 刷新进度回调，子类可在此方法监听进度刷新，然后更新ui
     *
     * @param duration 视频总时长
     * @param position 视频当前时长
     */
    @Override
    public void setProgress(int duration, int position) {
        if (mIsDragging) {
            return;
        }

        if (mPbBottomProgress != null) {
            if (duration > 0) {
                mPortraitProgress.setEnabled(true);
                mLandscapeProgress.setEnabled(true);
                int pos = (int) (position * 1.0 / duration * mPortraitProgress.getMax());
                mPortraitProgress.setProgress(pos);
                mLandscapeProgress.setProgress(pos);
                mPbBottomProgress.setProgress(pos);
            } else {
                mPortraitProgress.setEnabled(false);
                mLandscapeProgress.setEnabled(false);
            }
            int percent = mControlWrapper.getBufferedPercentage();
            if (percent >= 95) {
                //解决缓冲进度不能100%问题
                mPortraitProgress.setSecondaryProgress(mPortraitProgress.getMax());
                mLandscapeProgress.setSecondaryProgress(mLandscapeProgress.getMax());
                mPbBottomProgress.setSecondaryProgress(mPbBottomProgress.getMax());
            } else {
                mPortraitProgress.setSecondaryProgress(percent * 10);
                mLandscapeProgress.setSecondaryProgress(percent * 10);
                mPbBottomProgress.setSecondaryProgress(percent * 10);
            }
        }

        if (mTvLandscapePosition != null) {
            mTvLandscapePosition.setText(PlayerUtils.formatTime(position));
            mTvPortraitPosition.setText(PlayerUtils.formatTime(position));
        }
        if (mTvLandscapeDuration != null) {
            mTvLandscapeDuration.setText(PlayerUtils.formatTime(duration));
            mTvPortraitDuration.setText(PlayerUtils.formatTime(duration));
        }


        if (VideoPlayerConfig.newBuilder().build().mIsShowToast) {
            long time = VideoPlayerConfig.newBuilder().build().mShowToastTime;
            if (time <= 0) {
                time = 5;
            }
            long currentPosition = mControlWrapper.getCurrentPosition();
            Log.d("progress---", "duration---" + duration + "--currentPosition--" + currentPosition);
            if (duration - currentPosition < 2 * time * 1000) {
                //当前视频播放到最后3s时，弹出toast提示：即将自动为您播放下一个视频。
                if ((duration - currentPosition) / 1000 % 60 == time) {
                    Log.d("progress---", "即将自动为您播放下一个视频");
                    if (listener != null) {
                        listener.showToastOrDialog();
                    }
                }
            }
        }
    }

    @Override
    public void onLockStateChanged(boolean isLocked) {
        onVisibilityChanged(!isLocked, null);
    }


    /**
     * 横竖屏切换
     */
    private void toggleFullScreen() {
        Activity activity = PlayerUtils.scanForActivity(getContext());
        mControlWrapper.toggleFullScreen(activity);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        mIsDragging = true;
        mControlWrapper.stopProgress();
        mControlWrapper.stopFadeOut();
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        long duration = mControlWrapper.getDuration();
        long newPosition = (duration * seekBar.getProgress()) / mPbBottomProgress.getMax();
        mControlWrapper.seekTo((int) newPosition);
        mIsDragging = false;
        mControlWrapper.startProgress();
        mControlWrapper.startFadeOut();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (!fromUser) {
            return;
        }
        long duration = mControlWrapper.getDuration();
        long newPosition = (duration * progress) / mPbBottomProgress.getMax();
        if (mTvPortraitPosition != null) {
            mTvPortraitPosition.setText(PlayerUtils.formatTime(newPosition));
            mTvLandscapePosition.setText(PlayerUtils.formatTime(newPosition));

        }
    }

    private OnToastListener listener;

    public void setListener(OnToastListener listener) {
        this.listener = listener;
    }

    public interface OnToastListener {
        void showToastOrDialog();
    }

}
