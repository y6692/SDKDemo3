package com.centaurstech.sdk.view.seat;

import android.content.Context;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import com.centaurstech.sdk.R;
import com.qiwu.ui.util.UIUtils;

/**
 * @Author: 樊德鹏
 * 时   间:2019/5/29
 * 简   述:<功能描述>
 */
public class SeatBestViewPainter {
    private Paint mBestViewPaint = new Paint();

    SeatBestViewPainter(Context context, @Nullable AttributeSet attrs,
                        int defStyleAttr, int defStyleRes) {
        mBestViewPaint.setAntiAlias(true);
        mBestViewPaint.setStyle(Paint.Style.STROKE);
        mBestViewPaint.setColor(UIUtils.getColor(R.color.colorF68709));
        mBestViewPaint.setStrokeWidth(1);
    }
}
