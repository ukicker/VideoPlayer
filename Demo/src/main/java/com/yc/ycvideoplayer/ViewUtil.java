package com.yc.ycvideoplayer;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

/**
 * @PackageName : com.yc.ycvideoplayer
 * @File : ViewUtil.java
 * @Date : 2021/12/27 2021/12/27
 * @Author : K
 * @E-mail : vip@devkit.vip
 * @Version : V 1.0
 * @Describe ：
 */
public class ViewUtil {

    /**
     * 获取屏幕尺寸 - 宽
     */
    public static int getScreenWidth(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int widthPixels = displayMetrics.widthPixels;
        int heightPixels = displayMetrics.heightPixels;
        return widthPixels;
    }

    /**
     * 获取屏幕尺寸 - GAO
     */
    public static int getScreenHeight(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int widthPixels = displayMetrics.widthPixels;
        int heightPixels = displayMetrics.heightPixels;
        return heightPixels;
    }

    /**
     * 测量view
     */
    public static void measureView(View view) {
        ViewGroup.LayoutParams p = view.getLayoutParams();
        if (p == null) {
            p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0, p.width);
        int lpHeight = p.height;
        int childHeightSpec;
        if (lpHeight > 0) {
            childHeightSpec = View.MeasureSpec.makeMeasureSpec(lpHeight, View.MeasureSpec.EXACTLY);
        } else {
            childHeightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        }
        view.measure(childWidthSpec, childHeightSpec);
    }

    /**
     * 获取view 的宽度
     */
    public static int getViewWidth(View view) {
        measureView(view);
        return view.getMeasuredWidth();
    }

    /**
     * 获取View 的高度
     */
    public static int getViewHeight(View view) {
        measureView(view);
        return view.getMeasuredHeight();
    }

    /**
     * dp转px
     */
    public static int dp2px(Context context, float dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpVal, context.getResources().getDisplayMetrics());
    }

    /**
     * sp转px
     */
    public static int sp2px(Context context, float spVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                spVal, context.getResources().getDisplayMetrics());
    }

    /**
     * px转dp
     */
    public static float px2dp(Context context, float pxVal) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (pxVal / scale);
    }

    /**
     * px转sp
     */
    public static float px2sp(Context context, float pxVal) {
        return (pxVal / context.getResources().getDisplayMetrics().scaledDensity);
    }

    /**
     * 是否在屏幕右侧
     *
     * @param mContext 上下文
     * @param xPos 位置的x坐标值
     * @return true：是。
     */
    public static boolean isInRight(Context mContext, int xPos) {
        return (xPos > getScreenWidth(mContext) / 2);
    }

    /**
     * 是否在屏幕左侧
     *
     * @param mContext 上下文
     * @param xPos 位置的x坐标值
     * @return true：是。
     */
    public static boolean isInLeft(Context mContext, int xPos) {
        return (xPos < getScreenWidth(mContext) / 2);
    }

    public static int getStatusBarHeight(Context context) {
        int result = 0;
        try {
            int resourceId = Resources.getSystem().getIdentifier("status_bar_height", "dimen", "android");
            if (resourceId > 0) {
                int sizeOne = context.getResources().getDimensionPixelSize(resourceId);
                int sizeTwo = Resources.getSystem().getDimensionPixelSize(resourceId);

                float densityOne = context.getResources().getDisplayMetrics().density;
                float densityTwo = Resources.getSystem().getDisplayMetrics().density;
                if (sizeTwo >= sizeOne) {
                    return sizeTwo;
                } else {
                    return Math.round(sizeOne * densityTwo / densityOne);
                }
            }
        } catch (Resources.NotFoundException ignored) {
            return 0;
        }
        return result;
    }


    public static int getRvHeight(Context context, RecyclerView view){


        if (view.getLayoutManager() instanceof LinearLayoutManager){

        }

        if (view.getLayoutManager() instanceof GridLayoutManager){

        }

        if (view.getLayoutManager() instanceof StaggeredGridLayoutManager){

        }



        LinearLayoutManager layoutManager = (LinearLayoutManager) view.getLayoutManager();
        for (int i = 0; i <view.getChildCount() ; i++) {


        }

        return 0;

    }

}
