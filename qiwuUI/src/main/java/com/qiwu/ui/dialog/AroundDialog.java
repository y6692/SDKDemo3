package com.qiwu.ui.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import com.qiwu.ui.R;

import static com.qiwu.ui.util.UIUtils.dip2Px;

/**
 * @author Leon(黄长亮)
 * @describe 从周围出现的Dialog
 * @date 2018/8/13
 */

public class AroundDialog extends Dialog implements View.OnClickListener {


    private Button mBtnCancel;
    private Button mBtnDetermine;
    private LinearLayout mContentContainer;
    private FrameLayout mLeftBtnLayout;
    private FrameLayout mRightBtnLayout;
    private View mHorizontalHalvingLine;
    private View mVerticalHalvingLine;
    private LinearLayout mSuperContainer;
    private CardView mcvContainer;
    private static AroundDialog aroundDialog;

    private OnViewClickListener mOnViewClickListener;


    public static AroundDialog create(Context context) {
        aroundDialog = new AroundDialog(context);
        return aroundDialog;
    }


    public static void destroy() {
        if (aroundDialog != null) {
            aroundDialog.dismiss();
            aroundDialog = null;
        }
    }


    public static AroundDialog create(Context context, int style) {
        aroundDialog = new AroundDialog(context, style);
        return aroundDialog;
    }


    public AroundDialog setContent(View contentView) {
        mContentContainer.addView(contentView);
        mContentContainer.setMinimumHeight(dip2Px(100));
        mContentContainer.setVisibility(View.VISIBLE);
        return this;
    }

    public AroundDialog setHideAllButton() {
        mLeftBtnLayout.setVisibility(View.GONE);
        mRightBtnLayout.setVisibility(View.GONE);
        mHorizontalHalvingLine.setVisibility(View.GONE);
        mVerticalHalvingLine.setVisibility(View.GONE);
        setCanceledOnTouchOutside(true);
        return this;
    }

    public AroundDialog setHideLeftButton() {
        mLeftBtnLayout.setVisibility(View.GONE);
        mVerticalHalvingLine.setVisibility(View.GONE);
        return this;
    }

    public AroundDialog setHideRightButton() {
        mRightBtnLayout.setVisibility(View.GONE);
        mVerticalHalvingLine.setVisibility(View.GONE);
        return this;
    }

    public AroundDialog setBackgroundTransparency() {
        mContentContainer.setBackgroundResource(R.color.colorTransparent);
        mSuperContainer.setBackgroundResource(R.color.colorTransparent);
        return this;
    }

    public AroundDialog setLeftButtonText(int text) {
        return setLeftButtonText(getContext().getString(text));
    }

    public AroundDialog setLeftButtonText(String text) {
        mBtnCancel.setText(text);
        return this;
    }

    public AroundDialog setCanceledTouchOutside(boolean cancel) {
        setCancelable(cancel);
        setCanceledOnTouchOutside(cancel);
        return this;
    }

    public AroundDialog setRightButtonText(int text) {
        return setRightButtonText(getContext().getString(text));
    }

    public AroundDialog setRightButtonText(String text) {
        mBtnDetermine.setText(text);
        return this;
    }

    public AroundDialog setBackgroundColor(int color) {
        mcvContainer.setBackgroundColor(color);
        return this;
    }

    public AroundDialog setGravity(int gravity) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.gravity = gravity;
        getWindow().setAttributes(lp);
        return this;
    }

    public AroundDialog setHeight(int height) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.height = height;
        getWindow().setAttributes(lp);
        return this;
    }

    public AroundDialog setOnViewClickListener(OnViewClickListener onViewClickListener) {
        mOnViewClickListener = onViewClickListener;
        show();
        return this;
    }

    public AroundDialog(@NonNull Context context) {
        this(context, R.style.Hint_Dialog);
    }

    @SuppressLint("InflateParams")
    private AroundDialog(Context context, int defStyle) {
        super(context, defStyle);
        init();
    }

    private void init() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_around, null);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        mBtnCancel = dialogView.findViewById(R.id.btnCancel);
        mBtnDetermine = dialogView.findViewById(R.id.btnDetermine);
        mContentContainer = dialogView.findViewById(R.id.contentContainer);
        mLeftBtnLayout = dialogView.findViewById(R.id.leftBtnLayout);
        mRightBtnLayout = dialogView.findViewById(R.id.rightBtnLayout);
        mHorizontalHalvingLine = dialogView.findViewById(R.id.horizontalHalvingLine);
        mVerticalHalvingLine = dialogView.findViewById(R.id.verticalHalvingLine);
        mSuperContainer = dialogView.findViewById(R.id.superContainer);
        mcvContainer = dialogView.findViewById(R.id.cvContainer);
        setCanceledOnTouchOutside(false);
        super.setContentView(dialogView);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowManager.LayoutParams attributes = getWindow().getAttributes();
        attributes.alpha = 1f;
        attributes.width = WindowManager.LayoutParams.MATCH_PARENT;
        attributes.height = WindowManager.LayoutParams.WRAP_CONTENT;
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        getWindow().setAttributes(attributes);
    }

    @Override
    public void dismiss() {
        if (mOnViewClickListener != null) {
            mOnViewClickListener.onClick(mBtnCancel, false);
            clear();
        }
        aroundDialog = null;
        super.dismiss();
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btnCancel) {
            if (mOnViewClickListener != null) {
                mOnViewClickListener.onClick(v, false);
                clear();
            }
            this.dismiss();
        } else if (i == R.id.btnDetermine) {
            if (mOnViewClickListener != null) {
                mOnViewClickListener.onClick(v, true);
                clear();
            }
            this.dismiss();
        }
    }

    @Override
    public void show() {
        try {
            super.show();
        } catch (Exception e) {
            System.out.print("Dialog---show" + e.getMessage());
        }
    }

    public interface OnViewClickListener {
        void onClick(View view, boolean isConfirm);
    }

    private void clear() {
        mOnViewClickListener = null;
    }


}



