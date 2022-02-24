package com.yc.video.kernel.impl.exo;

import android.content.Context;

import com.yc.video.kernel.factory.PlayerFactory;

/**
 * <pre>
 *  抽象工厂具体实现类
 * </pre>
 */
public class ExoPlayerFactory extends PlayerFactory<ExoMediaPlayer> {

    public static ExoPlayerFactory create() {
        return new ExoPlayerFactory();
    }

    @Override
    public ExoMediaPlayer createPlayer(Context context) {
        return new ExoMediaPlayer(context);
    }
}
