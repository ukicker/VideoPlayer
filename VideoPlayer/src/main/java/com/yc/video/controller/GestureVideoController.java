package com.yc.video.controller;

import android.app.Activity;
import android.content.Context;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.yc.video.config.ConstantKeys;
import com.yc.video.kernel.utils.VideoLogUtils;
import com.yc.video.receiver.VolumeReceiver;
import com.yc.video.tool.PlayerUtils;
import com.yc.video.ui.view.InterControlView;

import java.util.Map;


/**
 * <pre>
 * - onDown(MotionEvent e):用户按下屏幕的时候的回调。
 * - onShowPress(MotionEvent e)：用户按下按键后100ms（根据Android7.0源码）还没有松开或者移动就会回调，官方在源码的解释是说一般用于告诉用户已经识别按下事件的回调（我暂时想不出有什么用途，因为这个回调触发之后还会触发其他的，不像长按）。
 * - onLongPress(MotionEvent e)：用户长按后（好像不同手机的时间不同，源码里默认是100ms+500ms）触发，触发之后不会触发其他回调，直至松开（UP事件）。
 * - onScroll(MotionEvent e1, MotionEvent e2,float distanceX, float distanceY)：手指滑动的时候执行的回调（接收到MOVE事件，且位移大于一定距离），e1,e2分别是之前DOWN事件和当前的MOVE事件，distanceX和distanceY就是当前MOVE事件和上一个MOVE事件的位移量。
 * - onFling(MotionEvent e1, MotionEvent e2, float velocityX,float velocityY)：用户执行抛操作之后的回调，MOVE事件之后手松开（UP事件）那一瞬间的x或者y方向速度，如果达到一定数值（源码默认是每秒50px），就是抛操作（也就是快速滑动的时候松手会有这个回调，因此基本上有onFling必然有onScroll）。
 * - onSingleTapUp(MotionEvent e)：用户手指松开（UP事件）的时候如果没有执行onScroll()和onLongPress()这两个回调的话，就会回调这个，说明这是一个点击抬起事件，但是不能区分是否双击事件的抬起。
 * ———————————————— 包含手势操作的VideoController
 * </pre>
 */
public abstract class GestureVideoController extends BaseVideoController implements
        GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener, View.OnTouchListener {

    private GestureDetector mGestureDetector;
    private AudioManager mAudioManager;
    private boolean mIsGestureEnabled = true;
    private int mStreamVolume;
    private float mBrightness;
    private int mSeekPosition;
    /**
     * 是否是第一次触摸
     */
    private boolean mFirstTouch;
    /**
     * 是否改变位置
     */
    private boolean mChangePosition;
    /**
     * 是否改变亮度
     */
    private boolean mChangeBrightness;
    /**
     * 是否改变音量
     */
    private boolean mChangeVolume;
    /**
     * 是否可以改变位置
     */
    private boolean mCanChangePosition = true;
    /**
     * 是否在竖屏模式下开始手势控制
     */
    private boolean mEnableInNormal;
    /**
     * 是否关闭了滑动手势
     */
    private boolean mCanSlide;
    /**
     * 播放状态
     */
    private int mCurPlayState;
    /**
     * 屏幕一半的距离
     */
    private int mHalfScreen;

    public GestureVideoController(@NonNull Context context) {
        super(context);
    }

    public GestureVideoController(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public GestureVideoController(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void initView(Context context) {
        super.initView(context);
        mHalfScreen = PlayerUtils.getScreenWidth(getContext(), true) / 2;
        mAudioManager = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
        mGestureDetector = new GestureDetector(getContext(), this);
        setOnTouchListener(this);
    }

    public void setCanSlide(boolean canSlide) {
        mCanSlide = canSlide;
    }

    /**
     * 设置是否可以滑动调节进度，默认可以
     */
    public void setCanChangePosition(boolean canChangePosition) {
        mCanChangePosition = canChangePosition;
    }

    /**
     * 是否在竖屏模式下开始手势控制，默认关闭
     */
    public void setEnableInNormal(boolean enableInNormal) {
        mEnableInNormal = enableInNormal;
    }

    /**
     * 是否开启手势空控制，默认开启，关闭之后，双击播放暂停以及手势调节进度，音量，亮度功能将关闭
     */
    public void setGestureEnabled(boolean gestureEnabled) {
        mIsGestureEnabled = gestureEnabled;
    }

    /**
     * 调用此方法向控制器设置播放器模式
     *
     * @param playerState 播放模式
     */
    @Override
    public void setPlayerState(int playerState) {
        super.setPlayerState(playerState);
        if (playerState == ConstantKeys.PlayMode.MODE_NORMAL) {
            mCanSlide = mEnableInNormal;
        } else if (playerState == ConstantKeys.PlayMode.MODE_FULL_SCREEN) {
            mCanSlide = true;
        }
    }

    /**
     * 调用此方法向控制器设置播放状态
     *
     * @param playState 播放状态
     */
    @Override
    public void setPlayState(int playState) {
        super.setPlayState(playState);
        mCurPlayState = playState;
    }

    private boolean isInPlaybackState() {
        return mControlWrapper != null
                && mCurPlayState != ConstantKeys.CurrentState.STATE_ERROR
                && mCurPlayState != ConstantKeys.CurrentState.STATE_IDLE
                && mCurPlayState != ConstantKeys.CurrentState.STATE_PREPARING
                && mCurPlayState != ConstantKeys.CurrentState.STATE_PREPARED
                && mCurPlayState != ConstantKeys.CurrentState.STATE_START_ABORT
                && mCurPlayState != ConstantKeys.CurrentState.STATE_BUFFERING_PLAYING;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return mGestureDetector.onTouchEvent(event);
    }

    /**
     * 手指按下的瞬间
     */
    @Override
    public boolean onDown(MotionEvent e) {
        if (!isInPlaybackState() //不处于播放状态
                || !mIsGestureEnabled //关闭了手势
                || PlayerUtils.isEdge(getContext(), e)) {
            //处于屏幕边沿
            return true;
        }
        mStreamVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        Activity activity = PlayerUtils.scanForActivity(getContext());
        if (activity == null) {
            mBrightness = 0;
        } else {
            mBrightness = activity.getWindow().getAttributes().screenBrightness;
        }
        mFirstTouch = true;
        mChangePosition = false;
        mChangeBrightness = false;
        mChangeVolume = false;
        return true;
    }

    /**
     * 单击
     */
    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        if (isInPlaybackState()) {
            //切换显示/隐藏状态
            mControlWrapper.toggleShowState();
        }
        return true;
    }

    /**
     * 双击
     */
    @Override
    public boolean onDoubleTap(MotionEvent e) {
        //如果没有锁屏，
        if (!isLocked() && isInPlaybackState()) {
            //播放和暂停
            togglePlay();
        }
        return true;
    }

    /**
     * 在屏幕上滑动
     * 左右滑动，则是改变播放进度
     * 上下滑动，滑动左边改变音量；滑动右边改变亮度
     */
    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        VideoLogUtils.e("事件----------事件onScroll----------"+mIsGestureEnabled+"/"+mCanSlide);

        if (!isInPlaybackState() //不处于播放状态
                || !mIsGestureEnabled //关闭了手势
                || !mCanSlide //关闭了滑动手势
                || isLocked() //锁住了屏幕
                //处于屏幕边沿
                || PlayerUtils.isEdge(getContext(), e1)) {
            return true;
        }
        float deltaX = e1.getX() - e2.getX();
        float deltaY = e1.getY() - e2.getY();
        //如果是第一次触摸
        if (mFirstTouch) {
            //判断是左右滑动，还是上下滑动
            mChangePosition = Math.abs(distanceX) >= Math.abs(distanceY);
            if (!mChangePosition) {
                //上下滑动，滑动左边改变音量；滑动右边改变亮度
                //半屏宽度
                if (mHalfScreen == 0) {
                    mHalfScreen = PlayerUtils.getScreenWidth(getContext(), true) / 2;
                }
                if (e2.getX() > mHalfScreen) {
                    mChangeVolume = true;
                } else {
                    mChangeBrightness = true;
                }
            }

            //左右滑动，则是改变播放进度
            if (mChangePosition) {
                //根据用户设置是否可以滑动调节进度来决定最终是否可以滑动调节进度
                mChangePosition = mCanChangePosition;
            }

            if (mChangePosition || mChangeBrightness || mChangeVolume) {
                for (Map.Entry<InterControlView, Boolean> next : mControlComponents.entrySet()) {
                    InterControlView component = next.getKey();
                    if (component instanceof IGestureComponent) {
                        ((IGestureComponent) component).onStartSlide();
                    }
                }
            }
            mFirstTouch = false;
        }
        if (mChangePosition) {
            slideToChangePosition(deltaX);
        } else if (mChangeBrightness) {
            slideToChangeBrightness(deltaY);
        } else if (mChangeVolume) {
            slideToChangeVolume(deltaY);
        }
        return true;
    }

    protected void slideToChangePosition(float deltaX) {
        deltaX = -deltaX;
        int width = getMeasuredWidth();
        int duration = (int) mControlWrapper.getDuration();
        int currentPosition = (int) mControlWrapper.getCurrentPosition();
        int position = (int) (deltaX / width * 120000 + currentPosition);
        if (position > duration) position = duration;
        if (position < 0) position = 0;
        for (Map.Entry<InterControlView, Boolean> next : mControlComponents.entrySet()) {
            InterControlView component = next.getKey();
            if (component instanceof IGestureComponent) {
                ((IGestureComponent) component).onPositionChange(position, currentPosition, duration);
            }
        }
        mSeekPosition = position;
    }

    protected void slideToChangeBrightness(float deltaY) {
        Activity activity = PlayerUtils.scanForActivity(getContext());
        if (!PlayerUtils.isActivityLiving(activity)) {
            return;
        }
        Window window = activity.getWindow();
        WindowManager.LayoutParams attributes = window.getAttributes();
        int height = getMeasuredHeight();
        if (mBrightness == -1.0f) mBrightness = 0.5f;
        float brightness = deltaY * 2 / height * 1.0f + mBrightness;
        if (brightness < 0) {
            brightness = 0f;
        }
        if (brightness > 1.0f) brightness = 1.0f;
        int percent = (int) (brightness * 100);
        attributes.screenBrightness = brightness;
        window.setAttributes(attributes);
        for (Map.Entry<InterControlView, Boolean> next : mControlComponents.entrySet()) {
            InterControlView component = next.getKey();
            if (component instanceof IGestureComponent) {
                ((IGestureComponent) component).onBrightnessChange(percent);
            }
        }
    }

    protected void slideToChangeVolume(float deltaY) {
        int streamMaxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int height = getMeasuredHeight();
        float deltaV = deltaY * 2 / height * streamMaxVolume;
        float index = mStreamVolume + deltaV;
        if (index > streamMaxVolume) index = streamMaxVolume;
        if (index < 0) index = 0;
        int percent = (int) (index / streamMaxVolume * 100);
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, (int) index, 0);
        for (Map.Entry<InterControlView, Boolean> next : mControlComponents.entrySet()) {
            InterControlView component = next.getKey();
            if (component instanceof IGestureComponent) {
                ((IGestureComponent) component).onVolumeChange(percent);
            }
        }
    }


    /**
     * 触摸事件
     *
     * @param event event事件，主要处理up，down，cancel
     * @return 返回值
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        VideoLogUtils.e("事件----------事件触摸----------");
        //滑动结束时事件处理
        if (!mGestureDetector.onTouchEvent(event)) {
            int action = event.getAction();
            switch (action) {
                case MotionEvent.ACTION_UP:
                    stopSlide();
                    if (mSeekPosition > 0) {
                        mControlWrapper.seekTo(mSeekPosition);
                        mSeekPosition = 0;
                    }
                    break;
                case MotionEvent.ACTION_CANCEL:
                    stopSlide();
                    mSeekPosition = 0;
                    break;
                case MotionEvent.ACTION_HOVER_MOVE:
                    break;
                case MotionEvent.ACTION_DOWN:
                    break;
            }
        }
        return super.onTouchEvent(event);
    }

    private void stopSlide() {
        for (Map.Entry<InterControlView, Boolean> next : mControlComponents.entrySet()) {
            InterControlView component = next.getKey();
            if (component instanceof IGestureComponent) {
                ((IGestureComponent) component).onStopSlide();
            }
        }
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        VideoLogUtils.e("onLongPress/" + isLocked() + "/" + isInPlaybackState() + "/" + mIsGestureEnabled);
        if (!isLocked()// 不锁住了屏幕
                && mControlWrapper.isPlaying()//处于播放状态
                && !PlayerUtils.isEdge(getContext(), e)// 安全区域
                && mIsGestureEnabled //开启手势
        ) {
            for (Map.Entry<InterControlView, Boolean> next : mControlComponents.entrySet()) {
                InterControlView component = next.getKey();
                if (component instanceof IGestureComponent) {
                    ((IGestureComponent) component).onLongPress();
                }
            }
        }
    }

    @Override
    public void onShowPress(MotionEvent e) {
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent e) {
        return false;
    }


    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }
}
