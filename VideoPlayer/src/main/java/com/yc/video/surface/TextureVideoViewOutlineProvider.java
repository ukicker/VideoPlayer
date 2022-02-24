package com.yc.video.surface;

import android.graphics.Outline;
import android.graphics.Rect;
import android.os.Build;
import android.view.View;
import android.view.ViewOutlineProvider;

import androidx.annotation.RequiresApi;

/**
 * @PackageName : com.yc.video.surface
 * @File : TextureVideoViewOutlineProvider.java
 * @Date : 2021/10/28 2021/10/28
 * @Author : K
 * @E-mail : vip@devkit.vip
 * @Version : V 1.0
 * @Describe ：
 */
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class TextureVideoViewOutlineProvider extends ViewOutlineProvider {

    private float mRadius;
    public TextureVideoViewOutlineProvider(int radius) {
        this.mRadius = radius;

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void getOutline(View view, Outline outline) {
        Rect rect = new Rect();
        view.getGlobalVisibleRect(rect);
        int leftMargin = 0;
        int topMargin = 0;
        Rect selfRect = new Rect(leftMargin, topMargin,
                rect.right - rect.left - leftMargin, rect.bottom - rect.top - topMargin);
        outline.setRoundRect(selfRect, mRadius);
    }
}
