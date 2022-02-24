package com.yc.ycvideoplayer;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;

/**
 * @PackageName : com.yc.ycvideoplayer
 * @File : TextViewLoadSuffix.java
 * @Date : 2021/12/27 2021/12/27
 * @Author : K
 * @E-mail : vip@devkit.vip
 * @Version : V 1.0
 * @Describe ：
 */
public class TextViewLoadSuffix extends androidx.appcompat.widget.AppCompatTextView {
    private String suffix = ".";
    private String text;

    public TextViewLoadSuffix(Context context) {
        super(context);
        text = getText().toString();
    }

    public TextViewLoadSuffix(Context context, AttributeSet attrs) {
        super(context, attrs);
        text = getText().toString();
    }

    public TextViewLoadSuffix(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        text = getText().toString();
    }


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            invalidate();
            if (suffix.length() >= 3) {
                suffix = ".";
            } else {
                suffix = suffix + ".";
            }
            setText(text + suffix);
            sendEmptyMessageDelayed(0, 400);
        }
    };

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        handler.sendEmptyMessage(0);
    }

    @Override
    protected void onDetachedFromWindow() {
        handler.removeMessages(0);
        super.onDetachedFromWindow();
    }
}
