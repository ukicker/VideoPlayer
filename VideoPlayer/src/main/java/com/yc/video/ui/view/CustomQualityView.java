package com.yc.video.ui.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
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
import androidx.annotation.RequiresApi;

import com.yc.video.R;
import com.yc.video.bridge.ControlWrapper;
import com.yc.video.config.ConstantKeys;
import com.yc.video.tool.IQualityName;
import com.yc.video.tool.PlayerUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @PackageName : com.smilodontech.player.view
 * @File : QualityView.java
 * @Date : 2021/4/26 2021/4/26
 * @Author : K
 * @E-mail : vip@devkit.vip
 * @Version : V 1.0
 * @Describe ：
 */
public class CustomQualityView<T> extends FrameLayout implements InterControlView, RadioGroup.OnCheckedChangeListener, View.OnClickListener {


    private RadioGroup mViewRg;
    private FrameLayout player_quality_layout;
    private FrameLayout mFLAnimLayout;
    private OnQualityListener mQualityListener;

    private ControlWrapper mControlWrapper;


    private List<? extends IQualityName> qualityList = new ArrayList<>();


    public void setQualityList(List<? extends IQualityName> qualityList) {
        this.qualityList = qualityList;

        if (mViewRg != null) {
            for (int i = 0; i < qualityList.size(); i++) {
                RadioButton radioButton = (RadioButton) View.inflate(getContext(), R.layout.player_select_radiobutton, null);
                radioButton.setText(qualityList.get(i).getQualityName());
                radioButton.setId(i);
                if (qualityList.get(i).isSelect()) {
                    radioButton.setChecked(true);
                }
                mViewRg.addView(radioButton);
            }
            mViewRg.setOnCheckedChangeListener(this);
            setVisibility(VISIBLE);
            Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.player_anim_in);
            animation.setFillBefore(true);
            animation.setDuration(200);
            mFLAnimLayout.startAnimation(animation);
        }
    }

    public void setQualityListener(OnQualityListener qualityListener) {
        mQualityListener = qualityListener;
    }

    public CustomQualityView(Context context) {
        super(context);
        initView();
    }

    public CustomQualityView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public CustomQualityView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    public void initView() {
        View itemView = LayoutInflater.from(getContext()).inflate(R.layout.player_quality_view, this);
        mViewRg = itemView.findViewById(R.id.rg_view);
        mFLAnimLayout = itemView.findViewById(R.id.fl_anim_layout);

        player_quality_layout = itemView.findViewById(R.id.player_quality_layout);
        player_quality_layout.setOnClickListener(this::onClick);

    }


    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        for (int i = 0; i < group.getChildCount(); i++) {
            RadioButton radioButton = (RadioButton) group.getChildAt(i);
            if (checkedId == i && radioButton.isChecked()) {
                if (mQualityListener != null) {
                    mQualityListener.setQuality(qualityList.get(i));
                }
                removeAnim();
            }
        }


    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.rg_view) {
        } else if (id == R.id.player_quality_layout) {
            removeAnim();
        }
    }

    void removeAnim() {
        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.player_anim_out);
        animation.setDuration(200);
        animation.setFillBefore(true);
        startAnimation(animation);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                ViewGroup viewGroup = (ViewGroup) getParent();
                if (viewGroup instanceof BasisVideoController) {
                    BasisVideoController basisVideoController = (BasisVideoController) viewGroup;
                    basisVideoController.post(() -> basisVideoController.removeControlComponent(CustomQualityView.this));
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


    public interface OnQualityListener<T> {
        void setQuality(T bean);
    }


}
