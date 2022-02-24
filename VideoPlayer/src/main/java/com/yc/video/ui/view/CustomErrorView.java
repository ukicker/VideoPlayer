package com.yc.video.ui.view;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yc.video.config.ConstantKeys;
import com.yc.video.bridge.ControlWrapper;

import com.yc.video.R;

/**
 * <pre>
 * 出错提示界面
 * </pre>
 */
public class CustomErrorView extends LinearLayout implements InterControlView, View.OnClickListener {

    private Context mContext;
    private float mDownX;
    private float mDownY;
    private TextView mTvMessage;
    private ImageView mIvRetry;

    private ControlWrapper mControlWrapper;

    public CustomErrorView(Context context) {
        super(context);
        init(context);
    }

    public CustomErrorView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CustomErrorView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }


    private void init(Context context) {
        this.mContext = context;
        setVisibility(GONE);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.player_custom_video_error, this, true);
        initFindViewById(view);
        initListener();
        setClickable(true);
    }

    private void initFindViewById(View view) {
        mTvMessage = view.findViewById(R.id.tv_message);
        mIvRetry = view.findViewById(R.id.iv_retry);
    }

    private void initListener() {
        mIvRetry.setOnClickListener(this);
    }


    public void showError(String error){
        mControlWrapper.pause();
        bringToFront();
        setVisibility(VISIBLE);
        mTvMessage.setText(error);
    }

    @Override
    public void onClick(View v) {
        if (v == mIvRetry) {
            setVisibility(GONE);
            mControlWrapper.replay(true);
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

    }

    @Override
    public void onPlayStateChanged(int playState) {
        if (playState == ConstantKeys.CurrentState.STATE_ERROR) {
            bringToFront();
            setVisibility(VISIBLE);
            mTvMessage.setText("视频播放异常");
        }
        if (playState == ConstantKeys.CurrentState.STATE_NETWORK_ERROR) {
            bringToFront();
            setVisibility(VISIBLE);
            mTvMessage.setText("无网络，请检查网络设置");
        }
        if (playState == ConstantKeys.CurrentState.STATE_PARSE_ERROR) {
            bringToFront();
            setVisibility(VISIBLE);
            mTvMessage.setText("视频加载错误");
        } else if (playState == ConstantKeys.CurrentState.STATE_IDLE) {
            setVisibility(GONE);
        } else if (playState == ConstantKeys.CurrentState.STATE_ONCE_LIVE) {
            setVisibility(GONE);
        }
    }

    @Override
    public void onPlayerStateChanged(int playerState) {

    }

    @Override
    public void setProgress(int duration, int position) {

    }

    @Override
    public void onLockStateChanged(boolean isLock) {

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDownX = ev.getX();
                mDownY = ev.getY();
                getParent().requestDisallowInterceptTouchEvent(true);
                break;
            case MotionEvent.ACTION_MOVE:
                float absDeltaX = Math.abs(ev.getX() - mDownX);
                float absDeltaY = Math.abs(ev.getY() - mDownY);
                if (absDeltaX > ViewConfiguration.get(getContext()).getScaledTouchSlop() ||
                        absDeltaY > ViewConfiguration.get(getContext()).getScaledTouchSlop()) {
                    getParent().requestDisallowInterceptTouchEvent(false);
                }
            case MotionEvent.ACTION_UP:
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

}
