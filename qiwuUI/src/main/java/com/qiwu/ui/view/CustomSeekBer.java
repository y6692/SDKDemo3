package com.qiwu.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * @author Leon(黄长亮)
 * @describe TODO
 * @date 2019/1/3
 */

public class CustomSeekBer extends android.support.v7.widget.AppCompatSeekBar {


    public CustomSeekBer(Context context) {
        super(context);
    }

    public CustomSeekBer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomSeekBer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        getParent().requestDisallowInterceptTouchEvent(true);
        return super.dispatchTouchEvent(ev);
    }
}
