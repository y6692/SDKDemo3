package com.centaurstech.sdk.view;

import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.centaurstech.sdk.R;

/**
 * @author Leon(黄长亮)
 * @describe 可拖拽的按钮
 * @date 2018/7/26
 */

public class DragButton extends FrameLayout {

    public DragButton(@NonNull Context context) {
        super(context,null);
    }

    public DragButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public DragButton(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        View view = View.inflate(getContext(), R.layout.layout_drag_view,null);
        addView(view, ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
    }
}
