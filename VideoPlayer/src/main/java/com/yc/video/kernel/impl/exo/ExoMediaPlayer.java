package com.yc.video.kernel.impl.exo;

import android.app.Application;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.os.Handler;
import android.view.Surface;
import android.view.SurfaceHolder;

import androidx.annotation.Nullable;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackException;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.RenderersFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.analytics.AnalyticsCollector;
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory;
import com.google.android.exoplayer2.source.LoadEventInfo;
import com.google.android.exoplayer2.source.MediaLoadData;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.MediaSourceEventListener;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.MappingTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.util.Clock;
import com.google.android.exoplayer2.util.EventLogger;
import com.google.android.exoplayer2.video.VideoSize;
import com.yc.video.kernel.inter.AbstractVideoPlayer;
import com.yc.video.kernel.inter.VideoPlayerListener;
import com.yc.video.kernel.utils.PlayerConstant;
import com.yc.video.kernel.utils.VideoLogUtils;
import com.yc.video.tool.PlayerUtils;

import java.util.Map;

import static com.google.android.exoplayer2.ExoPlaybackException.TYPE_SOURCE;


/**
 * <pre>
 * exo视频播放器实现类
 * </pre>
 */
public class ExoMediaPlayer extends AbstractVideoPlayer implements Player.Listener, MediaSourceEventListener {

    protected Context mAppContext;
    protected SimpleExoPlayer mInternalPlayer;
    protected MediaItem mMediaSource;
    private PlaybackParameters mSpeedPlaybackParameters;
    private int mLastReportedPlaybackState = Player.STATE_IDLE;
    private boolean mIsPreparing;
    private boolean mIsBuffering;

    private LoadControl mLoadControl;
    private RenderersFactory mRenderersFactory;
    private TrackSelector mTrackSelector;


    public ExoMediaPlayer(Context context) {
        if (context instanceof Application) {
            mAppContext = context;
        } else {
            mAppContext = context.getApplicationContext();
        }
    }

    @Override
    public void initPlayer() {
        DefaultTrackSelector.ParametersBuilder builder = new DefaultTrackSelector.ParametersBuilder(mAppContext);
        DefaultTrackSelector.Parameters trackSelectorParameters = builder.build();
        DefaultTrackSelector trackSelector = new DefaultTrackSelector(mAppContext);
        trackSelector.setParameters(trackSelectorParameters);
        //创建exo播放器
        mInternalPlayer = new SimpleExoPlayer.Builder(
                mAppContext
                , new DefaultRenderersFactory(mAppContext)
                , trackSelector
                , new DefaultMediaSourceFactory(mAppContext)
                , new DefaultLoadControl()
                , DefaultBandwidthMeter.getSingletonInstance(mAppContext)
                , new AnalyticsCollector(Clock.DEFAULT)
        )
                .build();
        setOptions();

        //播放器日志
        if (VideoLogUtils.isIsLog() && mTrackSelector instanceof MappingTrackSelector) {
            mInternalPlayer.addAnalyticsListener(new EventLogger((MappingTrackSelector) mTrackSelector, "ExoPlayer"));
        }
        initListener();
    }

    /**
     * exo视频播放器监听listener
     */
    private void initListener() {
        mInternalPlayer.addListener(this);
    }

    public void setTrackSelector(TrackSelector trackSelector) {
        mTrackSelector = trackSelector;
    }

    public void setRenderersFactory(RenderersFactory renderersFactory) {
        mRenderersFactory = renderersFactory;
    }

    public void setLoadControl(LoadControl loadControl) {
        mLoadControl = loadControl;
    }

    /**
     * 设置播放地址
     *
     * @param path    播放地址
     * @param headers 播放地址请求头
     */
    @Override
    public void setDataSource(String path, Map<String, String> headers) {
        // 设置dataSource
        if (path == null || path.length() == 0) {
            if (mPlayerEventListener != null) {
                mPlayerEventListener.onInfo(PlayerConstant.MEDIA_INFO_URL_NULL, 0);
            }
            return;
        }
        mMediaSource = new MediaItem
                .Builder()
                .setUri(path)
                .build();
    }

    @Override
    public void setDataSource(AssetFileDescriptor fd) {
        //no support
    }

    /**
     * 准备开始播放（异步）
     */
    @Override
    public void prepareAsync() {
        if (mInternalPlayer == null) {
            return;
        }
        if (mMediaSource == null) {
            return;
        }
        if (mSpeedPlaybackParameters != null) {
            mInternalPlayer.setPlaybackParameters(mSpeedPlaybackParameters);
        }
        mIsPreparing = true;
        mInternalPlayer.setMediaItem(mMediaSource);
        mInternalPlayer.prepare();
    }

    /**
     * 播放
     */
    @Override
    public void start() {
        if (mInternalPlayer == null) {
            return;
        }
        mInternalPlayer.setPlayWhenReady(true);
    }

    /**
     * 暂停
     */
    @Override
    public void pause() {
        if (mInternalPlayer == null) {
            return;
        }
        mInternalPlayer.setPlayWhenReady(false);
    }

    /**
     * 停止
     */
    @Override
    public void stop() {
        if (mInternalPlayer == null) {
            return;
        }
        mInternalPlayer.stop();
    }

    private MediaSourceEventListener mMediaSourceEventListener = new MediaSourceEventListener() {
        @Override
        public void onLoadCompleted(int windowIndex, @Nullable MediaSource.MediaPeriodId mediaPeriodId, LoadEventInfo loadEventInfo, MediaLoadData mediaLoadData) {
            if (mPlayerEventListener != null && mIsPreparing) {
                mPlayerEventListener.onPrepared();
            }
        }
    };


    /**
     * 重置播放器
     */
    @Override
    public void reset() {
        if (mInternalPlayer != null) {
            mInternalPlayer.stop(true);
            mInternalPlayer.setVideoSurface(null);
            mIsPreparing = false;
            mIsBuffering = false;
            mLastReportedPlaybackState = Player.STATE_IDLE;
        }
    }

    /**
     * 是否正在播放
     */
    @Override
    public boolean isPlaying() {
        if (mInternalPlayer == null) {
            return false;
        }
        int state = mInternalPlayer.getPlaybackState();
        switch (state) {
            case Player.STATE_BUFFERING:
            case Player.STATE_READY:
                return mInternalPlayer.getPlayWhenReady();
            case Player.STATE_IDLE:
            case Player.STATE_ENDED:
            default:
                return false;
        }
    }

    /**
     * 调整进度
     */
    @Override
    public void seekTo(long time) {
        if (mInternalPlayer == null) {
            return;
        }
        mInternalPlayer.seekTo(time);
    }

    /**
     * 释放播放器
     */
    @Override
    public void release() {
        if (mInternalPlayer != null) {
            mInternalPlayer.removeListener(this);
            mInternalPlayer.release();
            mInternalPlayer = null;
        }
        mIsPreparing = false;
        mIsBuffering = false;
        mLastReportedPlaybackState = Player.STATE_IDLE;
        mSpeedPlaybackParameters = null;
    }

    /**
     * 获取当前播放的位置
     */
    @Override
    public long getCurrentPosition() {
        if (mInternalPlayer == null) {
            return 0;
        }
        return mInternalPlayer.getCurrentPosition();
    }

    /**
     * 获取视频总时长
     */
    @Override
    public long getDuration() {
        if (mInternalPlayer == null) {
            return 0;
        }
        return mInternalPlayer.getDuration();
    }

    /**
     * 获取缓冲百分比
     */
    @Override
    public int getBufferedPercentage() {
        return mInternalPlayer == null ? 0 : mInternalPlayer.getBufferedPercentage();
    }

    /**
     * 设置渲染视频的View,主要用于SurfaceView
     */
    @Override
    public void setSurface(Surface surface) {
        if (surface != null) {
            try {
                if (mInternalPlayer != null) {
                    mInternalPlayer.setVideoSurface(surface);
                }
            } catch (Exception e) {
                mPlayerEventListener.onError(PlayerConstant.ErrorType.TYPE_UNEXPECTED, e.getMessage());
            }
        }
    }

    @Override
    public void setDisplay(SurfaceHolder holder) {
        if (holder == null) {
            setSurface(null);
        } else {
            setSurface(holder.getSurface());
        }
    }

    /**
     * 设置音量
     */
    @Override
    public void setVolume(float leftVolume, float rightVolume) {
        if (mInternalPlayer != null) {
            mInternalPlayer.setVolume((leftVolume + rightVolume) / 2);
        }
    }

    /**
     * 设置是否循环播放
     */
    @Override
    public void setLooping(boolean isLooping) {
        if (mInternalPlayer != null) {
            mInternalPlayer.setRepeatMode(isLooping ? Player.REPEAT_MODE_ALL : Player.REPEAT_MODE_OFF);
        }
    }

    @Override
    public void setOptions() {
        //准备好就开始播放
        mInternalPlayer.setPlayWhenReady(true);
    }

    /**
     * 设置播放速度
     */
    @Override
    public void setSpeed(float speed) {
        VideoLogUtils.e("setSpeed:" + speed);
        PlaybackParameters playbackParameters = new PlaybackParameters(speed);
        mSpeedPlaybackParameters = playbackParameters;
        if (mInternalPlayer != null) {
            mInternalPlayer.setPlaybackParameters(playbackParameters);
        }
    }

    /**
     * 获取播放速度
     */
    @Override
    public float getSpeed() {
        if (mSpeedPlaybackParameters != null) {
            return mSpeedPlaybackParameters.speed;
        }
        return 1f;
    }

    /**
     * 获取当前缓冲的网速
     */
    @Override
    public long getTcpSpeed() {
        // no support
        return 0;
    }

    @Override
    public void onPlaybackStateChanged(int playbackState) {
        if (mPlayerEventListener == null) {
            return;
        }
        if (mIsPreparing) {
            return;
        }
        if (mLastReportedPlaybackState != playbackState) {
            switch (playbackState) {
                //最开始调用的状态
                case Player.STATE_IDLE:
                    break;
                //开始缓充
                case Player.STATE_BUFFERING:
                    mPlayerEventListener.onInfo(PlayerConstant.MEDIA_INFO_BUFFERING_START, getBufferedPercentage());
                    mIsBuffering = true;
                    break;
                //开始播放
                case Player.STATE_READY:
                    if (mIsBuffering) {
                        mPlayerEventListener.onInfo(PlayerConstant.MEDIA_INFO_BUFFERING_END, getBufferedPercentage());
                        mIsBuffering = false;
                    }
                    break;
                //播放器已经播放完了媒体
                case Player.STATE_ENDED:
                    mPlayerEventListener.onCompletion();
                    break;
                default:
                    break;
            }
            mLastReportedPlaybackState = playbackState;
        }
    }

    @Override
    public void onPlayerError(PlaybackException error) {
        VideoLogUtils.e("PlaybackException:"+error.errorCode + "/" + error.timestampMs + "/" + error.getErrorCodeName()+"/"+error.getMessage());
        if (mPlayerEventListener != null) {
            int type = error.errorCode;
            if (type==2001) {
                mPlayerEventListener.onError(PlayerConstant.ErrorType.ERROR_CODE_IO_NETWORK_CONNECTION_FAILED, error.getMessage());
            } else if (type == TYPE_SOURCE) {
                //错误的链接
                mPlayerEventListener.onError(PlayerConstant.ErrorType.TYPE_SOURCE, error.getMessage());
            } else if (type == ExoPlaybackException.TYPE_RENDERER
                    || type == ExoPlaybackException.TYPE_UNEXPECTED
                    || type == ExoPlaybackException.TYPE_REMOTE
            ) {
                mPlayerEventListener.onError(PlayerConstant.ErrorType.TYPE_UNEXPECTED, error.getMessage());
            }else {
                mPlayerEventListener.onError(PlayerConstant.ErrorType.TYPE_UNEXPECTED, error.getMessage());

            }
        }
    }

    @Override
    public void onVideoSizeChanged(VideoSize videoSize) {
        if (mPlayerEventListener != null) {
            mPlayerEventListener.onVideoSizeChanged(videoSize.width, videoSize.height);
            if (videoSize.unappliedRotationDegrees > 0) {
                mPlayerEventListener.onInfo(PlayerConstant.MEDIA_INFO_VIDEO_ROTATION_CHANGED, videoSize.unappliedRotationDegrees);
            }
        }
    }

    @Override
    public void onRenderedFirstFrame() {
        if (mPlayerEventListener != null && mIsPreparing) {
            mPlayerEventListener.onInfo(PlayerConstant.MEDIA_INFO_VIDEO_RENDERING_START, 0);
            mIsPreparing = false;
        }
    }

    public void onLoadCompleted(int windowIndex, @Nullable MediaSource.MediaPeriodId mediaPeriodId, LoadEventInfo loadEventInfo, MediaLoadData mediaLoadData) {
        if (mPlayerEventListener != null && mIsPreparing) {
            mPlayerEventListener.onPrepared();
        }
    }

    @Override
    public void setPlayerEventListener(VideoPlayerListener playerEventListener) {
        super.setPlayerEventListener(playerEventListener);
    }

}
