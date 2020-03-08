package com.centaurstech.sdk.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Leon(黄长亮)
 * @describe TODO
 * @date 2018/10/16
 */

public class IRecyclerView extends RecyclerView {

    private List<OnScrollListener> mOnScrollListeners = new ArrayList<>();

    public IRecyclerView(Context context) {
        this(context,null);
    }

    public IRecyclerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public IRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        getParent().requestDisallowInterceptTouchEvent(true);
        return super.dispatchTouchEvent(ev);
    }


    @Override
    public void addOnScrollListener(@NonNull OnScrollListener listener) {
        super.addOnScrollListener(listener);
        mOnScrollListeners.add(listener);
    }


    public List<OnScrollListener> getOnScrollListeners() {
        return mOnScrollListeners;
    }
}
