package com.yc.video.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.yc.video.R;
import com.yc.video.bridge.ControlWrapper;
import com.yc.video.config.ConstantKeys;
import com.yc.video.kernel.utils.VideoLogUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @PackageName : com.smilodontech.player.view
 * @File : SpeedView.java
 * @Date : 2021/4/26 2021/4/26
 * @Author : K
 * @E-mail : vip@devkit.vip
 * @Version : V 1.0
 * @Describe ：
 */
public class CustomSpeedView extends FrameLayout implements InterControlView, RadioGroup.OnCheckedChangeListener, View.OnClickListener {


    private RadioGroup mViewRg;
    private FrameLayout mSpeedViewPlayer;
    private FrameLayout mSpeedAnimLayout;

    private ControlWrapper mControlWrapper;


    private List<Float> mList = new ArrayList();


    public CustomSpeedView(Context context) {
        super(context);
        initView();
    }

    public CustomSpeedView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public CustomSpeedView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    public void initView() {
        View itemView = LayoutInflater.from(getContext()).inflate(R.layout.player_speed_view, this);
        mViewRg = (RadioGroup) itemView.findViewById(R.id.rg_view);
        mSpeedAnimLayout = (FrameLayout) itemView.findViewById(R.id.fl_anim_layout);
        mSpeedViewPlayer = (FrameLayout) itemView.findViewById(R.id.speed_view);
        mSpeedViewPlayer.setOnClickListener(this);

        mList.add(2.0F);
        mList.add(1.5F);
        mList.add(1.25F);
        mList.add(1.0F);
        mList.add(0.5F);

        for (int i = 0; i < mList.size(); i++) {
            RadioButton radioButton = (RadioButton) LayoutInflater.from(getContext()).inflate(R.layout.player_select_radiobutton, null);
            radioButton.setText(String.format(getContext().getResources().getString(R.string.player_speed_current), String.valueOf(mList.get(i))));
            if (mControlWrapper != null && mControlWrapper.getSpeed() == mList.get(i)) {
                radioButton.setSelected(true);
            }

            mViewRg.addView(radioButton);
        }
        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.player_anim_in);
        animation.setFillBefore(true);
        animation.setDuration(200);
        mSpeedAnimLayout.startAnimation(animation);
        setVisibility(VISIBLE);
    }


    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        for (int i = 0; i < group.getChildCount(); i++) {
            RadioButton radioButton = (RadioButton) group.getChildAt(i);
            if (radioButton.isChecked()) {
                mControlWrapper.setSpeed(mList.get(i));
                gone();
            }
        }
    }

    public void show(float i) {
        for (int j = 0; j < mViewRg.getChildCount(); j++) {
            RadioButton radioButton = (RadioButton) mViewRg.getChildAt(j);
            if (radioButton.getTag().toString().equals("" + i)) {
                radioButton.setChecked(true);
            }
        }
        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.player_anim_in);
        animation.setFillBefore(true);
        animation.setDuration(200);
        mSpeedAnimLayout.startAnimation(animation);
        setVisibility(VISIBLE);

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.rg_view) {
        } else if (id == R.id.speed_view) {
            gone();
        }
    }

    void gone() {
        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.player_anim_out);
        animation.setDuration(200);
        animation.setFillBefore(true);
        mSpeedAnimLayout.startAnimation(animation);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                ViewGroup viewGroup = (ViewGroup) getParent();
                if (viewGroup instanceof BasisVideoController) {
                    BasisVideoController basisVideoController = (BasisVideoController) viewGroup;
                    basisVideoController.post(() -> basisVideoController.removeControlComponent(CustomSpeedView.this));
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    @Override
    public void attach(@NonNull ControlWrapper controlWrapper) {
        mControlWrapper = controlWrapper;
        for (int i = 0; i < mList.size(); i++) {
            if (mControlWrapper != null && mControlWrapper.getSpeed() == mList.get(i)) {
                ((RadioButton) mViewRg.getChildAt(i)).setChecked(true);
            }
        }
        mViewRg.setOnCheckedChangeListener(this);
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
        switch (playState) {
            case ConstantKeys.CurrentState.STATE_IDLE:
            case ConstantKeys.CurrentState.STATE_START_ABORT:
            case ConstantKeys.CurrentState.STATE_PREPARING:
            case ConstantKeys.CurrentState.STATE_PREPARED:
            case ConstantKeys.CurrentState.STATE_ERROR:
            case ConstantKeys.CurrentState.STATE_BUFFERING_PLAYING:
            case ConstantKeys.CurrentState.STATE_ONCE_LIVE:
            case ConstantKeys.CurrentState.STATE_COMPLETED:
                setVisibility(GONE);
                break;
        }

    }

    @Override
    public void onPlayerStateChanged(int playerState) {
        switch (playerState) {
            case ConstantKeys.PlayMode.MODE_NORMAL:
            case ConstantKeys.PlayMode.MODE_TINY_WINDOW:
                setVisibility(GONE);
                break;
        }
    }

    @Override
    public void setProgress(int duration, int position) {

    }

    @Override
    public void onLockStateChanged(boolean isLocked) {
        if (isLocked){
            setVisibility(GONE);
        }
    }

}
