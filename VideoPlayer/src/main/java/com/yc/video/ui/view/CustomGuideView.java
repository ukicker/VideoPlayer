package com.yc.video.ui.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.yc.video.R;
import com.yc.video.bridge.ControlWrapper;
import com.yc.video.tool.PlayerSpUtil;

/**
 * @PackageName : com.yc.video.ui.view
 * @File : CustomGuideView.java
 * @Date : 2021/10/27 2021/10/27
 * @Author : K
 * @E-mail : vip@devkit.vip
 * @Version : V 1.0
 * @Describe ： 引导
 */
public class CustomGuideView extends FrameLayout implements InterControlView, View.OnClickListener {
    private boolean isLive;
    private ImageView mIvLeftUp;
    private ImageView mIvLeftGesture;
    private LinearLayout mLlGuideHorizontal;
    private ImageView mIvGestureHorizontal;
    private ImageView mIvRightDown;
    private ImageView mIvRightGesture;
    private TextView mTvClose;

    public void setLive(boolean isLive) {
        this.isLive = isLive;
        if (mLlGuideHorizontal != null) {
            mLlGuideHorizontal.setVisibility(isLive ? GONE : VISIBLE);
        }
    }


    public CustomGuideView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public CustomGuideView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CustomGuideView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(context);

    }

    @SuppressLint("ClickableViewAccessibility")
    private void init(Context context) {
        setVisibility(GONE);
        View itemView = LayoutInflater.from(getContext()).inflate(R.layout.player_custom_video_guide_view, this, true);

        mIvLeftUp = (ImageView) itemView.findViewById(R.id.iv_left_up);
        mIvLeftGesture = (ImageView) itemView.findViewById(R.id.iv_left_gesture);
        mLlGuideHorizontal = (LinearLayout) itemView.findViewById(R.id.ll_guide_horizontal);
        mIvGestureHorizontal = (ImageView) itemView.findViewById(R.id.iv_gesture_horizontal);
        mIvRightDown = (ImageView) itemView.findViewById(R.id.iv_right_down);
        mIvRightGesture = (ImageView) itemView.findViewById(R.id.iv_right_gesture);
        mTvClose = (TextView) itemView.findViewById(R.id.tv_close);
        mLlGuideHorizontal.setVisibility(isLive ? GONE : VISIBLE);

        startAnimation();
        mTvClose.setOnClickListener(v -> {
            if (isLive) {
                PlayerSpUtil.put(getContext(), "player_live_guide", false);
            } else {
                PlayerSpUtil.put(getContext(), "player_video_guide", false);
            }
            ViewGroup viewGroup = (ViewGroup) getParent();
            viewGroup.removeView(CustomGuideView.this);
        });
        itemView.setOnTouchListener((v, event) -> true);

        boolean show;
        if (isLive) {
            show = (boolean) PlayerSpUtil.get(getContext(), "player_live_guide", true);
        } else {
            show = (boolean) PlayerSpUtil.get(getContext(), "player_video_guide", true);
        }
        setVisibility(show?VISIBLE:GONE);
    }


    @Override
    public void onClick(View v) {

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

    public void startAnimation() {

        mIvLeftUp.startAnimation(leftToAnimation());
        mIvLeftGesture.startAnimation(left2Animation());

        mIvGestureHorizontal.startAnimation(vAnimation());


        mIvRightDown.startAnimation(rightAnimation());
        mIvRightGesture.startAnimation(right2Animation());

    }

    public Animation leftToAnimation() {
        AnimationSet animation = new AnimationSet(false);
        TranslateAnimation translateAnimation = new TranslateAnimation(0, 0, 0, -10);
        AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.1f);
        alphaAnimation.setRepeatCount(1);
        translateAnimation.setRepeatCount(1);
        animation.addAnimation(translateAnimation);
        animation.addAnimation(alphaAnimation);
        animation.setDuration(500);
        animation.setRepeatMode(Animation.REVERSE);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mIvLeftUp.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startAnimation();
                    }
                }, 1000);

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        return animation;
    }

    public Animation left2Animation() {
        AnimationSet animation = new AnimationSet(false);
        TranslateAnimation translateAnimation = new TranslateAnimation(0, 0, 0, -50);
        AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.1f);
        alphaAnimation.setRepeatCount(1);
        translateAnimation.setRepeatCount(1);

        animation.addAnimation(translateAnimation);
        animation.addAnimation(alphaAnimation);
        animation.setDuration(500);
        animation.setRepeatMode(Animation.REVERSE);
        return animation;
    }


    public Animation rightAnimation() {
        AnimationSet animation = new AnimationSet(false);
        TranslateAnimation translateAnimation = new TranslateAnimation(0, 0, 0, 15);
        AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.1f);
        alphaAnimation.setRepeatCount(1);
        translateAnimation.setRepeatCount(1);
        animation.addAnimation(translateAnimation);
        animation.addAnimation(alphaAnimation);
        animation.setDuration(500);
        animation.setRepeatMode(Animation.REVERSE);
        return animation;
    }

    public Animation right2Animation() {
        AnimationSet animation = new AnimationSet(false);
        TranslateAnimation translateAnimation = new TranslateAnimation(0, 0, 0, 50);
        AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.1f);
        alphaAnimation.setRepeatCount(1);
        translateAnimation.setRepeatCount(1);
        animation.addAnimation(translateAnimation);
        animation.addAnimation(alphaAnimation);
        animation.setRepeatCount(-1);
        animation.setDuration(500);
        animation.setRepeatMode(Animation.REVERSE);
        return animation;
    }

    public Animation vAnimation() {
        AlphaAnimation animation = new AlphaAnimation(1.0f, 0.3f);
        animation.setRepeatCount(-1);
        animation.setDuration(300);
        animation.setRepeatMode(Animation.REVERSE);
        return animation;
    }
}
