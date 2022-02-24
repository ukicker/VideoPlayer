package com.yc.video.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.yc.video.bridge.ControlWrapper;

/**
 * @PackageName : com.yc.video.ui.view
 * @File : CustomLoadingView.java
 * @Date : 2021/10/27 2021/10/27
 * @Author : K
 * @E-mail : vip@devkit.vip
 * @Version : V 1.0
 * @Describe ： 加载中
 */
public class CustomLoadingView extends FrameLayout implements InterControlView, View.OnClickListener {


    public CustomLoadingView(@NonNull Context context) {
        super(context);
    }

    public CustomLoadingView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomLoadingView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }




    @Override
    public void onClick(View v) {

    }

    @Override
    public void attach(@NonNull ControlWrapper controlWrapper) {

    }

    @Override
    public View getView() {
        return null;
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
}
