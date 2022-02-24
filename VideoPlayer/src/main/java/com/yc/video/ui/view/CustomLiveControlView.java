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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.yc.video.R;
import com.yc.video.bridge.ControlWrapper;
import com.yc.video.config.ConstantKeys;
import com.yc.video.kernel.utils.VideoLogUtils;
import com.yc.video.tool.CustomQualityBean;
import com.yc.video.tool.PlayerUtils;

import java.util.ArrayList;
import java.util.List;


/**
 * <pre>
 *     desc  : 底部控制栏视图
 *     revise: 用于直播
 * </pre>
 */
public class CustomLiveControlView extends FrameLayout implements InterControlView, View.OnClickListener {

    private Context mContext;
    private ControlWrapper mControlWrapper;
    private LinearLayout mLlBottomContainer;
    private LinearLayout mLlBottomMenuSafe;
    private LinearLayout mLlPortraitContainer;
    private ImageView mIvPortraitPlay;
    private ImageView mIvPortraitRefresh;
    private ImageView mIvPortraitFull;
    private LinearLayout mRlLandscapeContainer;
    private ImageView mIvLandscapePlay;
    private ImageView mIvLandscapeRefresh;
    private ImageView mIvLandscapeTiny;
    private TextView mTvLandscapeQuality;

    private CustomQualityView<CustomQualityBean> mQualityView ;
    private  List<CustomQualityBean> mQualityBeans = new ArrayList<>();

    public CustomLiveControlView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public CustomLiveControlView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CustomLiveControlView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        this.mContext = context;
        setVisibility(GONE);
        View view = LayoutInflater.from(getContext()).inflate(
                R.layout.player_custom_video_live_bottom, this, true);
        initFindViewById(view);
        initListener();
        mQualityBeans.add(new CustomQualityBean("超清","超清1080P",true));
    }

    private void initFindViewById(View view) {
        mLlBottomContainer = view.findViewById(R.id.ll_bottom_container);
        mLlBottomMenuSafe = view.findViewById(R.id.ll_bottom_menu_safe);
        mLlPortraitContainer = view.findViewById(R.id.ll_portrait_container);
        mIvPortraitPlay = view.findViewById(R.id.iv_portrait_play);
        mIvPortraitRefresh = view.findViewById(R.id.iv_portrait_refresh);
        mIvPortraitFull = view.findViewById(R.id.iv_portrait_full);
        mRlLandscapeContainer = view.findViewById(R.id.rl_landscape_container);
        mIvLandscapePlay = view.findViewById(R.id.iv_landscape_play);
        mIvLandscapeRefresh = view.findViewById(R.id.iv_landscape_refresh);
        mIvLandscapeTiny = view.findViewById(R.id.iv_landscape_tiny);
        mTvLandscapeQuality = view.findViewById(R.id.tv_landscape_quality);
    }

    private void initListener() {

        mIvPortraitFull.setOnClickListener(this);
        mIvPortraitPlay.setOnClickListener(this);
        mIvLandscapePlay.setOnClickListener(this);

        mIvPortraitRefresh.setOnClickListener(this);
        mIvLandscapeRefresh.setOnClickListener(this);

        mIvLandscapeTiny.setOnClickListener(this);
        mTvLandscapeQuality.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (v == mIvPortraitFull) {
            toggleFullScreen();
        } else if (v == mIvLandscapePlay || v == mIvPortraitPlay) {
            mControlWrapper.togglePlay();
        } else if (v == mIvLandscapeRefresh || v == mIvPortraitRefresh) {
            mControlWrapper.replay(true);
        } else if (v == mTvLandscapeQuality) {
            if (mQualityView==null){
               mQualityView = new CustomQualityView<CustomQualityBean>(mContext);
            }
            mQualityView.setQualityList(mQualityBeans);
            mQualityView.setQualityListener((CustomQualityView.OnQualityListener<CustomQualityBean>) mT -> {
                mTvLandscapeQuality.setText(mT.getName());
                for (int i = 0; i <mQualityBeans.size() ; i++) {
                    if (mQualityBeans.get(i).getName().equals(mT.getName())){
                        mQualityBeans.get(i).setSelect(true);
                    }else {
                        mQualityBeans.get(i).setSelect(false);
                    }
                }
                VideoLogUtils.e(":"+mT.getQualityName()+"/"+mT.isSelect()+"/"+mQualityBeans.toArray().toString());
            });
            ViewGroup viewGroup = (ViewGroup) getParent();
            if (viewGroup instanceof BasisVideoController) {
                BasisVideoController basisVideoController = (BasisVideoController) viewGroup;
                basisVideoController.removeControlComponent(mQualityView);
                basisVideoController.addControlComponent(mQualityView);
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
            if (getVisibility() == GONE) {
                setVisibility(VISIBLE);
                if (anim != null) {
                    startAnimation(anim);
                }
            }
        } else {
            if (getVisibility() == VISIBLE) {
                setVisibility(GONE);
                if (anim != null) {
                    startAnimation(anim);
                }
            }
        }
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
                setVisibility(GONE);
                break;
            case ConstantKeys.CurrentState.STATE_PLAYING:
                mIvLandscapePlay.setSelected(true);
                mIvPortraitPlay.setSelected(true);
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
                mLlPortraitContainer.setVisibility(VISIBLE);
                mRlLandscapeContainer.setVisibility(GONE);
                break;
            case ConstantKeys.PlayMode.MODE_FULL_SCREEN:
                mLlPortraitContainer.setVisibility(GONE);
                mRlLandscapeContainer.setVisibility(VISIBLE);
                break;
        }

        Activity activity = PlayerUtils.scanForActivity(mContext);
        if (activity != null && mControlWrapper.hasCutout()) {
            int orientation = activity.getRequestedOrientation();
            int cutoutHeight = mControlWrapper.getCutoutHeight();
            VideoLogUtils.e("cutoutHeight:" + cutoutHeight);
            if (orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                mLlBottomContainer.setPadding(0, 0, 0, 0);
            } else if (orientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                mLlBottomContainer.setPadding(cutoutHeight, 0, 0, 0);
            } else if (orientation == ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE) {
                mLlBottomContainer.setPadding(0, 0, cutoutHeight, 0);
            }
        }
    }

    @Override
    public void setProgress(int duration, int position) {

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

}
