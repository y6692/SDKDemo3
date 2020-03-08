package com.qiwu.ui.util;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.qiwu.ui.common.AppConfig;

public class UIUtils {

    /**
     * 得到上下文
     */
    public static Context getContext() {
        return AppConfig.getContext();
    }

    /**
     * 得到Resources对象
     */
    public static Resources getResources() {
        return getContext().getResources();
    }

    /**
     * Dip换Px
     */
    public static int dip2Px(double dip) {
        float density = getResources().getDisplayMetrics().density;
        int px = (int) (dip * density + .5f);
        return px;
    }

    /**
     * 得到Drawable对象
     */
    public static Drawable getDrawable(@DrawableRes int id) {
        return ContextCompat.getDrawable(UIUtils.getContext(), id);
    }

    /**
     * 得到Colors.xml中的的颜色
     */
    public static int getColor(@ColorRes int colorId) {
        return ContextCompat.getColor(getContext(), colorId);
    }

    /**
     * 获取屏幕宽度（像素）
     *
     * @return
     */
    public static int getScreensWidth() {
        return getResources().getDisplayMetrics().widthPixels; // 屏幕宽度（像素）;
    }

    /**
     * 屏幕高度（像素）
     *
     * @return
     */
    public static int getScreensHeight() {
        return getResources().getDisplayMetrics().heightPixels;// 屏幕高度（像素）
    }


    /**
     * Get center child in X Axes
     */
    public static View getCenterXChild(RecyclerView recyclerView) {
        int childCount = recyclerView.getChildCount();
        if (childCount > 0) {
            for (int i = 0; i < childCount; i++) {
                View child = recyclerView.getChildAt(i);
                if (isChildInCenterX(recyclerView, child)) {
                    return child;
                }
            }
        }
        return null;
    }

    /**
     * Get position of center child in X Axes
     */
    public static int getCenterXChildPosition(RecyclerView recyclerView) {
        int childCount = recyclerView.getChildCount();
        if (childCount > 0) {
            for (int i = 0; i < childCount; i++) {
                View child = recyclerView.getChildAt(i);
                if (isChildInCenterX(recyclerView, child)) {
                    return recyclerView.getChildAdapterPosition(child);
                }
            }
        }
        return childCount;
    }

    /**
     * Get center child in Y Axes
     */
    public static View getCenterYChild(RecyclerView recyclerView) {
        int childCount = recyclerView.getChildCount();
        if (childCount > 0) {
            for (int i = 0; i < childCount; i++) {
                View child = recyclerView.getChildAt(i);
                if (isChildInCenterY(recyclerView, child)) {
                    return child;
                }
            }
        }
        return null;
    }

    /**
     * Get position of center child in Y Axes
     */
    public static int getCenterYChildPosition(RecyclerView recyclerView) {
        int childCount = recyclerView.getChildCount();
        if (childCount > 0) {
            for (int i = 0; i < childCount; i++) {
                View child = recyclerView.getChildAt(i);
                if (isChildInCenterY(recyclerView, child)) {
                    return recyclerView.getChildAdapterPosition(child);
                }
            }
        }
        return childCount;
    }

    public static boolean isChildInCenterX(RecyclerView recyclerView, View view) {
        int childCount = recyclerView.getChildCount();
        int[] lvLocationOnScreen = new int[2];
        int[] vLocationOnScreen = new int[2];
        recyclerView.getLocationOnScreen(lvLocationOnScreen);
        int middleX = lvLocationOnScreen[0] + recyclerView.getWidth() / 2;
        if (childCount > 0) {
            view.getLocationOnScreen(vLocationOnScreen);
            if (vLocationOnScreen[0] <= middleX && vLocationOnScreen[0] + view.getWidth() >= middleX) {
                return true;
            }
        }
        return false;
    }

    public static boolean isChildInCenterY(RecyclerView recyclerView, View view) {
        int childCount = recyclerView.getChildCount();
        int[] lvLocationOnScreen = new int[2];
        int[] vLocationOnScreen = new int[2];
        recyclerView.getLocationOnScreen(lvLocationOnScreen);
        int middleY = lvLocationOnScreen[1] + recyclerView.getHeight() / 2;
        if (childCount > 0) {
            view.getLocationOnScreen(vLocationOnScreen);
            if (vLocationOnScreen[1] <= middleY && vLocationOnScreen[1] + view.getHeight() >= middleY) {
                return true;
            }
        }
        return false;
    }

    public static ScreenWidthLevel getScreenWidthLevel(){
        if (getScreensWidth() >= 1080){
            return ScreenWidthLevel.MAX;
        }else if (getScreensWidth() >= 720){
            return ScreenWidthLevel.MIDDLE;
        }else {
            return ScreenWidthLevel.MIX;
        }
    }


    public enum ScreenWidthLevel{
        MAX,MIDDLE,MIX
    }


}
