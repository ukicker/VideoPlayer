package com.yc.ycvideoplayer.demo;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.analytics.AnalyticsCollector;
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Clock;
import com.yc.video.kernel.impl.exo.ExoPlayerFactory;
import com.yc.ycvideoplayer.ConstantVideo;

import org.yc.ycvideoplayer.R;

public class ExoActivity extends AppCompatActivity {

    private PlayerView mVideoView;
    private SimpleExoPlayer player;

    @Override
    protected void onPause() {
        super.onPause();
        mVideoView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mVideoView.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (player!=null){
            player.release();
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exo_player);
        mVideoView = findViewById(R.id.video_view);

        SimpleExoPlayer player = new SimpleExoPlayer.Builder(
                this
                , new DefaultRenderersFactory(this)
                , null
                , new DefaultMediaSourceFactory(this)
                , new DefaultLoadControl()
                , DefaultBandwidthMeter.getSingletonInstance(this)
                , new AnalyticsCollector(Clock.DEFAULT)
        )
                .build();
        player.setPlayWhenReady(true);
        mVideoView.setPlayer(player);
        Uri uri = Uri.parse(ConstantVideo.VideoPlayerList[0]);
        DefaultHttpDataSourceFactory dataSourceFactory = new DefaultHttpDataSourceFactory("user-agent");
        ProgressiveMediaSource mediaSource = new ProgressiveMediaSource.Factory(
                dataSourceFactory).createMediaSource(uri);
        // 播放
        player.prepare(mediaSource);
    }

}
