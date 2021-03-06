package com.yc.ycvideoplayer.newPlayer.activity;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.connectsdk.device.ConnectableDevice;
import com.connectsdk.discovery.DiscoveryManager;
import com.connectsdk.service.command.ServiceCommandError;
import com.yc.video.kernel.factory.PlayerFactory;
import com.yc.video.kernel.utils.PlayerConstant;
import com.yc.video.kernel.utils.PlayerFactoryUtils;
import com.yc.video.kernel.utils.VideoLogUtils;
import com.yc.ycvideoplayer.BuriedPointEventImpl;
import com.yc.ycvideoplayer.CastDeviceListDialog;
import com.yc.ycvideoplayer.ConstantVideo;

import org.yc.ycvideoplayer.R;

import com.yc.video.config.ConstantKeys;
import com.yc.video.config.VideoPlayerConfig;
import com.yc.video.player.OnVideoStateListener;
import com.yc.video.player.VideoPlayer;
import com.yc.video.player.VideoPlayerBuilder;
import com.yc.video.player.VideoViewManager;
import com.yc.video.ui.view.BasisVideoController;
import com.yc.video.ui.view.CustomErrorView;
import com.yc.ycvideoplayer.cast.OnDiscoveryManagerListener;
import com.yc.ycvideoplayer.cast.PlayerCastComponent;

import java.io.File;

public class NormalActivity extends AppCompatActivity implements View.OnClickListener {
    private  String TAG ="ukicker";

    private VideoPlayer mVideoPlayer;
    private Button mBtnScaleNormal;
    private Button mBtnScale169;
    private Button mBtnScale43;
    private Button mBtnScaleFull;
    private Button mBtnScaleOriginal;
    private Button mBtnScaleCrop;
    private Button mBtnCrop;
    private Button mBtnGif;
    private BasisVideoController controller;
    private CastDeviceListDialog mCastDeviceListDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        initFindViewById();
        initVideoPlayer();
        initListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mVideoPlayer != null) {
            //????????????????????????????????????????????????????????????????????????????????????????????????????????????
            mVideoPlayer.resume();
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (mVideoPlayer != null) {
            //????????????????????????????????????????????????????????????????????????????????????????????????
            mVideoPlayer.pause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mVideoPlayer != null) {
            //???????????????????????????????????????????????????????????????????????????????????????????????????????????????
            mVideoPlayer.release();
        }
    }

    @Override
    public void onBackPressed() {
        //???????????????????????????????????????????????????????????????????????????????????????????????????
        if (mVideoPlayer == null || !mVideoPlayer.onBackPressed()) {
            super.onBackPressed();
        }
    }

    private void initFindViewById() {
        mVideoPlayer = findViewById(R.id.video_player);
        mBtnScaleNormal = findViewById(R.id.btn_scale_normal);
        mBtnScale169 = findViewById(R.id.btn_scale_169);
        mBtnScale43 = findViewById(R.id.btn_scale_43);
        mBtnScaleFull = findViewById(R.id.btn_scale_full);
        mBtnScaleOriginal = findViewById(R.id.btn_scale_original);
        mBtnScaleCrop = findViewById(R.id.btn_scale_crop);
        mBtnCrop = findViewById(R.id.btn_crop);
        mBtnGif = findViewById(R.id.btn_gif);
    }

    private void initVideoPlayer() {
        String url = getIntent().getStringExtra(IntentKeys.URL);
        if (url == null || url.length() == 0) {
            url = ConstantVideo.VideoPlayerList[0];
        }
//        url = "rtmp://pili-live-rtmp.ukicker.cn/ukicker/5062456095a6e09d88197a5e73a33388?sign=ab74f4ae63817eb48643e8ca8a4cc9c8&t=61f127e5";

//        url = "rtmp://pili-live-rtmp.ukicker.cn/ukicker/268ba1eb0cf9b06241037d8686d80944?sign=ebbbe342a77cce62f35d7a64168bb34b&t=620e05b6";
        String filePath = Environment.getExternalStorageDirectory() + File.separator +  "Download"+ File.separator+"2.mp4";
        File file = new File(filePath);

        VideoLogUtils.d(file.exists()+"|"+file.getAbsolutePath()+"|"+file.getPath());
        //??????????????????????????????????????????????????????
        controller = new BasisVideoController(this);
        controller.setLive(true);
        PlayerCastComponent castComponent = new PlayerCastComponent(this,R.layout.layout_cast,R.id.player_iv_cast);
        if (mCastDeviceListDialog==null){
            mCastDeviceListDialog = new CastDeviceListDialog(NormalActivity.this);
            mCastDeviceListDialog.setConnectableDeviceListener(new CastDeviceListDialog.OnSelectConnectableDeviceListener() {
                @Override
                public void onSelect(ConnectableDevice device) {
                    castComponent.onSelectConnectableDevice(device);
                }

                @Override
                public void startDiscoveryDevice() {
                    castComponent.startDiscoveryDevice();
                }
            });
        }
        castComponent.setOnErrorListener(msg -> Toast.makeText(NormalActivity.this, msg+"", Toast.LENGTH_SHORT).show());
        castComponent.setDiscoveryManagerListener(new OnDiscoveryManagerListener() {
            @Override
            public void onSearchStart() {
                Log.d(TAG, "onSearchStart: 1");
                if (mCastDeviceListDialog!=null){
                    mCastDeviceListDialog.show();
                }
            }

            @Override
            public void onSearchEnd() {
                Log.d(TAG, "onSearchEnd: 0");
                if (mCastDeviceListDialog!=null){
                    mCastDeviceListDialog.onSearchEnd();
                }

            }

            @Override
            public void onSelectSuccess() {
                if (mCastDeviceListDialog!=null){
                    mCastDeviceListDialog.dismiss();
                }
            }

            @Override
            public void onDeviceAdded(DiscoveryManager manager, ConnectableDevice device) {
                Log.d(TAG, "onDeviceAdded: "+device.toString());
                if (mCastDeviceListDialog!=null){
                    mCastDeviceListDialog.onDeviceAdded(device);
                }
            }


            @Override
            public void onDeviceRemoved(DiscoveryManager manager, ConnectableDevice device) {
                Log.d(TAG, "onDeviceRemoved: "+device.toString());
                if (mCastDeviceListDialog!=null){
                    mCastDeviceListDialog.onDeviceRemoved(device);
                }

            }
        });
        controller.addControlComponent(castComponent);
        mVideoPlayer.setController(controller);
        mVideoPlayer.setUrl(url);
        mVideoPlayer.postDelayed(() -> mVideoPlayer.start(), 300);
        //?????????????????????
        Glide.with(this).load(R.drawable.image_default).into(controller.getThumb());
    }

    private void initListener() {
        mBtnScaleNormal.setOnClickListener(this);
        mBtnScale169.setOnClickListener(this);
        mBtnScale43.setOnClickListener(this);
        mBtnScaleFull.setOnClickListener(this);
        mBtnScaleOriginal.setOnClickListener(this);
        mBtnScaleCrop.setOnClickListener(this);
        mBtnCrop.setOnClickListener(this);
        mBtnGif.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        if (v == mBtnScale169) {
            mVideoPlayer.setUrl(ConstantVideo.VideoPlayerList[1]);
            mVideoPlayer.start();
        } else if (v == mBtnScaleNormal) {
            mVideoPlayer.setScreenScaleType(ConstantKeys.PlayerScreenScaleType.SCREEN_SCALE_DEFAULT);
        } else if (v == mBtnScale43) {
            mVideoPlayer.setScreenScaleType(ConstantKeys.PlayerScreenScaleType.SCREEN_SCALE_4_3);
        } else if (v == mBtnScaleFull) {
            mVideoPlayer.setScreenScaleType(ConstantKeys.PlayerScreenScaleType.SCREEN_SCALE_MATCH_PARENT);
        } else if (v == mBtnScaleOriginal) {
            mVideoPlayer.setScreenScaleType(ConstantKeys.PlayerScreenScaleType.SCREEN_SCALE_ORIGINAL);
        } else if (v == mBtnScaleCrop) {
            controller.addDefaultTitle("??????");
        } else if (v == mBtnCrop) {
            controller.showError("????????????");
        } else if (v == mBtnGif) {
            if (!mBtnGif.getText().equals("??????")) {
                controller.setLive(true);
                mBtnGif.setText("??????");
            } else {
                controller.setLive(false);
                mBtnGif.setText("??????");
            }
        }
    }

    private void test() {
        //VideoPlayer??????
        VideoPlayerBuilder.Builder builder = VideoPlayerBuilder.newBuilder();
        VideoPlayerBuilder videoPlayerBuilder = new VideoPlayerBuilder(builder);
        //?????????????????????????????????
        builder.setPlayerBackgroundColor(Color.BLACK);
        //?????????????????????
        int[] mTinyScreenSize = {0, 0};
        builder.setTinyScreenSize(mTinyScreenSize);
        //????????????AudioFocus????????? ????????????
        builder.setEnableAudioFocus(false);
        mVideoPlayer.setVideoBuilder(videoPlayerBuilder);
        //??????
        Bitmap bitmap = mVideoPlayer.doScreenShot();
        //??????????????????????????????
        mVideoPlayer.clearOnStateChangeListeners();
        //???????????????????????????
        int bufferedPercentage = mVideoPlayer.getBufferedPercentage();
        //??????????????????????????????
        int currentPlayerState = mVideoPlayer.getCurrentPlayerState();
        //???????????????????????????
        int currentPlayState = mVideoPlayer.getCurrentPlayState();
        //???????????????????????????
        long currentPosition = mVideoPlayer.getCurrentPosition();
        //?????????????????????
        long duration = mVideoPlayer.getDuration();
        //??????????????????
        float speed = mVideoPlayer.getSpeed();
        //??????????????????
        long tcpSpeed = mVideoPlayer.getTcpSpeed();
        //??????????????????
        int[] videoSize = mVideoPlayer.getVideoSize();
        //????????????????????????
        boolean mute = mVideoPlayer.isMute();
        //??????????????????????????????
        boolean fullScreen = mVideoPlayer.isFullScreen();
        //????????????????????????
        boolean tinyScreen = mVideoPlayer.isTinyScreen();

        //????????????????????????
        boolean playing = mVideoPlayer.isPlaying();
        //????????????
        mVideoPlayer.pause();
        //????????????????????????????????????????????????
        mVideoPlayer.onPrepared();
        //????????????
        mVideoPlayer.replay(true);
        //????????????
        mVideoPlayer.resume();
        //??????????????????
        mVideoPlayer.seekTo(100);
        //??????????????? ?????????????????????
        mVideoPlayer.setLooping(true);
        //??????????????????
        mVideoPlayer.setSpeed(1.1f);
        //???????????? 0.0f-1.0f ??????
        mVideoPlayer.setVolume(1, 1);
        //????????????
        mVideoPlayer.start();


        //????????????
        mVideoPlayer.startFullScreen();
        //????????????
        mVideoPlayer.stopFullScreen();
        //????????????
        mVideoPlayer.startTinyScreen();
        //????????????
        mVideoPlayer.stopTinyScreen();

        mVideoPlayer.setOnStateChangeListener(new OnVideoStateListener() {
            /**
             * ????????????
             * ???????????????????????????????????????????????????????????????
             * MODE_NORMAL              ????????????
             * MODE_FULL_SCREEN         ????????????
             * MODE_TINY_WINDOW         ????????????
             * @param playerState                       ????????????
             */
            @Override
            public void onPlayerStateChanged(int playerState) {
                switch (playerState) {
                    case ConstantKeys.PlayMode.MODE_NORMAL:
                        //????????????
                        break;
                    case ConstantKeys.PlayMode.MODE_FULL_SCREEN:
                        //????????????
                        break;
                    case ConstantKeys.PlayMode.MODE_TINY_WINDOW:
                        //????????????
                        break;
                }
            }

            /**
             * ????????????
             * -1               ????????????
             * 0                ???????????????
             * 1                ???????????????
             * 2                ??????????????????
             * 3                ????????????
             * 4                ????????????
             * 5                ????????????(??????????????????????????????????????????????????????????????????????????????????????????????????????)
             * 6                ????????????(?????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
             * 7                ????????????
             * 8                ??????????????????
             * @param playState                         ???????????????????????????????????????????????????
             */
            @Override
            public void onPlayStateChanged(int playState) {
                switch (playState) {
                    case ConstantKeys.CurrentState.STATE_IDLE:
                        //???????????????????????????
                        break;
                    case ConstantKeys.CurrentState.STATE_START_ABORT:
                        //??????????????????
                        break;
                    case ConstantKeys.CurrentState.STATE_PREPARING:
                        //???????????????
                        break;
                    case ConstantKeys.CurrentState.STATE_PREPARED:
                        //??????????????????
                        break;
                    case ConstantKeys.CurrentState.STATE_ERROR:
                        //????????????
                        break;
                    case ConstantKeys.CurrentState.STATE_BUFFERING_PLAYING:
                        //????????????
                        break;
                    case ConstantKeys.CurrentState.STATE_PLAYING:
                        //????????????
                        break;
                    case ConstantKeys.CurrentState.STATE_PAUSED:
                        //????????????
                        break;
                    case ConstantKeys.CurrentState.STATE_BUFFERING_PAUSED:
                        //????????????
                        break;
                    case ConstantKeys.CurrentState.STATE_COMPLETED:
                        //????????????
                        break;
                }
            }
        });

        //?????????????????????
        ImageView thumb = controller.getThumb();
        Glide.with(this).load(R.drawable.image_default).into(controller.getThumb());
        //??????????????????
        controller.setTitle("????????????");
        //?????????????????????????????????????????????????????????????????????????????????
        CustomErrorView customErrorView = new CustomErrorView(this);
        controller.addControlComponent(customErrorView);
        //??????????????????
        controller.removeControlComponent(customErrorView);
        //?????????????????????
        controller.removeAllControlComponent();
        //??????????????????
        controller.hide();
        //??????????????????
        controller.show();
        //????????????????????????????????????/????????????
        controller.setEnableOrientation(true);
        //??????????????????????????????
        controller.showNetWarning();
        //???????????????
        int cutoutHeight = controller.getCutoutHeight();
        //??????????????????
        boolean b = controller.hasCutout();
        //???????????????????????????
        controller.setAdaptCutout(true);
        //??????????????????
        controller.stopProgress();
        //????????????????????????????????????STATE_PLAYING?????????????????????????????????
        controller.startProgress();
        //??????????????????
        boolean locked = controller.isLocked();
        //??????????????????
        controller.setLocked(true);
        //????????????
        controller.stopFadeOut();
        //????????????
        controller.startFadeOut();
        //????????????????????????????????????
        controller.setDismissTimeout(8);
        //??????
        controller.destroy();


        //????????????????????????????????????????????????????????????
        PlayerFactory player = PlayerFactoryUtils.getPlayer(PlayerConstant.PlayerType.TYPE_IJK);
        VideoViewManager.instance().setConfig(VideoPlayerConfig.newBuilder()
                //???????????????
                .setContext(this)
                //??????????????????????????????
                .setBuriedPointEvent(new BuriedPointEventImpl())
                //?????????????????????????????????????????????
                .setLogEnabled(true)
                //??????ijk
                .setPlayerFactory(player)
                //????????????????????????start()?????????????????????????????????????????????
                .setPlayOnMobileNetwork(false)
                //????????????AudioFocus????????? ????????????
                .setEnableAudioFocus(true)
                //????????????????????????????????????
                .setAdaptCutout(true)
                //?????????????????????????????????/????????? ???????????????
                .setEnableOrientation(false)
                //?????????????????????view????????????RenderView
                //.setRenderViewFactory(null)
                .build());

    }

}
