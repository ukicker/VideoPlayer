package com.yc.video.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.util.AttributeSet;
import android.util.Log;

import androidx.appcompat.widget.AppCompatSeekBar;

import com.yc.video.R;
import com.yc.video.tool.PlayerUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @PackageName : com.yc.video.ui.view
 * @File : PointSeekBar.java
 * @Date : 2021/10/27 2021/10/27
 * @Author : K
 * @E-mail : vip@devkit.vip
 * @Version : V 1.0
 * @Describe ：
 */
public class PointSeekBar extends AppCompatSeekBar {
    private float mPointRadius;
    private long duration = 0;
    private boolean mShowPoint = false;
    private List<IPointInfo> mPoints;

    public PointSeekBar(Context context) {
        this(context, null);
    }

    public PointSeekBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PointSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mPointRadius = PlayerUtils.dp2px(context,2);
    }

    public void setProgressDrawable(Drawable d) {
        if (d != null) {
            LayerDrawable drawable = (LayerDrawable) d;
            boolean bool = drawable.setDrawableByLayerId(R.id.player_point_seek_bar_anchor_background, new PointDrawable());
            Log.i("PointSeekBar", "bool:" + bool);
            super.setProgressDrawable(d);
        } else {
            super.setProgressDrawable(null);
        }
    }

    public void setPointRadius(float pointRadius) {
        mPointRadius = pointRadius;
        invalidate();
    }

    public void setShowPoint(boolean isShowPoint) {
        mShowPoint = isShowPoint;
        invalidate();
    }

    public void updatePoints(List<? extends IPointInfo> list) {
        if (mPoints == null) {
            mPoints = new ArrayList<>();
        } else {
            mPoints.clear();
        }
        mPoints.addAll(list);
        invalidate();
    }

    public void setDuration(long duration) {
        this.duration = duration;
        invalidate();
    }

    public void addPoint(IPointInfo info) {
        if (info != null) {
            if (mPoints == null) {
                mPoints = new ArrayList<>();
            }
            mPoints.add(info);
        }
        invalidate();
    }

    private class PointDrawable extends ColorDrawable {
        private final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        PointDrawable() {
            mPaint.setDither(true);
            mPaint.setColor(Color.WHITE);
        }

        @Override
        public void draw(Canvas canvas) {
            super.draw(canvas);
            if (mShowPoint && mPoints != null && duration > 0) {
                final float y = getMeasuredHeight() / 2f;
                Rect rect = getBounds();
                final int width = Math.abs(rect.left - rect.right);
                for (IPointInfo point : mPoints) {
                    float pointTime = stringToLong(point.getPlayTime());
                    float offset = pointTime / duration * width;
                    canvas.drawCircle(rect.left + offset, y, mPointRadius, mPaint);
                }
            }
        }
    }

    private long stringToLong(String arg) {
        long i = 0;
        if (arg != null && arg.matches("[0-9]+|-[0-9]+")) {
            i = Long.parseLong(arg);
        }
        return i;
    }

    public interface IPointInfo {
        String getHint();

        String getPlayTime();

        String getPointTime();
    }

}