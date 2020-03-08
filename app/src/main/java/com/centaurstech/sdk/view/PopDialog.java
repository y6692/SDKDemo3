package com.centaurstech.sdk.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.text.Html;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.centaurstech.sdk.R;
import com.centaurstech.sdk.activity.BaseActivity;
import com.qiwu.ui.common.AppConfig;
import com.centaurstech.sdk.listener.OnDestroyListener;
import com.qiwu.ui.util.UIUtils;

import java.util.ArrayList;
import java.util.List;
import static com.qiwu.ui.util.UIUtils.dip2Px;
import static com.qiwu.ui.util.UIUtils.getScreensHeight;
import static com.qiwu.ui.util.UIUtils.getScreensWidth;


/**
 * 从中间弹出的Dialog
 *
 * @author Leon(黄长亮)
 * @date 2017/5/23.
 */
public class PopDialog extends Dialog implements View.OnClickListener, OnDestroyListener {

    private static List<PopDialog> mDialogs = new ArrayList<>();

    private LinearLayout mSuperContainer;
    private TextView mTvTitle;
    private TextView mTvLeftText;
    private TextView mTvRightText;
    private TextView mTvContent;
    private TextView mTvTitleSub;
    private LinearLayout mLeftBtnLayout;
    private LinearLayout mRightBtnLayout;
    private LinearLayout mContentContainer;
    private LinearLayout mTextContainer;
    private View mHorizontalHalvingLine;
    private View mVerticalHalvingLine;
    private ImageView mIvLeft, mIvRight;
    private boolean mIsSetWidth;
    private boolean mNeedDismiss = true;
    private int mMargin = UIUtils.getScreenWidthLevel() == UIUtils.ScreenWidthLevel.MIX ? 12 : 53;

    private OnViewClickListener mOnViewClickListener;

    private BaseActivity mActivity;


    public static PopDialog create(Activity context) {
        return new PopDialog(context);
    }

    public static PopDialog create(Context context) {
        return new PopDialog(context);
    }


    public PopDialog setTitle(String title) {
        mTvTitle.setText(title);
        mTvTitle.setVisibility(View.VISIBLE);
        mContentContainer.setVisibility(View.GONE);
        return this;
    }

    public PopDialog setTitleSub(String title) {
        mTvTitleSub.setText(title);
        mTvTitleSub.setVisibility(View.VISIBLE);
        mContentContainer.setVisibility(View.GONE);
        return this;
    }

    public PopDialog setContent(int content) {
        return setContent(getContext().getString(content));
    }

    public PopDialog setContent(String content) {
        if (content.contains("<") && content.contains(">") && content.contains("</")) {
            mTvContent.setText(Html.fromHtml(content));
        } else {
            mTvContent.setText(content);
        }
        try {
            int width = getScreensWidth() - dip2Px(40 * 2) - mTvContent.getPaddingRight() - mTvContent.getPaddingLeft();
            int index = measureTextViewHeight(mTvContent, content, width, 1)[0];
            if (index == -1) {
                mTvContent.setGravity(Gravity.LEFT);
                mTvTitle.setGravity(Gravity.CENTER);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        mTvContent.setVisibility(View.VISIBLE);
        mContentContainer.setVisibility(View.GONE);
        return this;
    }


    public PopDialog setContent(View contentView) {
        mContentContainer.addView(contentView);
        mContentContainer.setMinimumHeight(dip2Px(100));
        mContentContainer.setVisibility(View.VISIBLE);
        mTextContainer.setVisibility(View.GONE);
        return this;
    }

    public PopDialog setHideAllButton() {
        mLeftBtnLayout.setVisibility(View.GONE);
        mRightBtnLayout.setVisibility(View.GONE);
        mHorizontalHalvingLine.setVisibility(View.GONE);
        mVerticalHalvingLine.setVisibility(View.GONE);
        return this;
    }

    public PopDialog setHideLeftButton() {
        mLeftBtnLayout.setVisibility(View.GONE);
        mVerticalHalvingLine.setVisibility(View.GONE);
        return this;
    }

    public PopDialog setHideRightButton() {
        mRightBtnLayout.setVisibility(View.GONE);
        mVerticalHalvingLine.setVisibility(View.GONE);
        return this;
    }

    public PopDialog setHideTitle() {
        mTvTitle.setVisibility(View.GONE);
        return this;
    }

    public PopDialog setVisibleSubTitle() {
        mTvTitleSub.setVisibility(View.VISIBLE);
        return this;
    }

    public PopDialog setBackgroundTransparency() {
        mContentContainer.setBackgroundResource(R.color.colorTransparent);
        mSuperContainer.setBackgroundResource(R.color.colorTransparent);
        return this;
    }


    public PopDialog setGrayLevel(float value) {
        WindowManager.LayoutParams attributes = getWindow().getAttributes();
        attributes.dimAmount = value;
        getWindow().setAttributes(attributes);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND | WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        return this;
    }

    public PopDialog setLeftButtonText(int text) {
        return setLeftButtonText(getContext().getString(text));
    }

    public PopDialog setNeedDismiss(boolean dismiss) {
        mNeedDismiss = dismiss;
        return this;
    }

    public PopDialog setLeftButtonText(String text) {
        mTvLeftText.setText(text);
        return this;
    }

    public PopDialog setCanceledTouchOutside(boolean cancel) {
        setCancelable(cancel);
        setCanceledOnTouchOutside(cancel);
        return this;
    }

    public PopDialog setRightButtonText(int text) {
        return setRightButtonText(getContext().getString(text));
    }

    public PopDialog setRightButtonText(String text) {
        mTvRightText.setText(text);
        return this;
    }

    public PopDialog setLeftButtonColor(int color) {
        mTvLeftText.setTextColor(AppConfig.getContext().getResources().getColor(color));
        return this;
    }

    public PopDialog setRightButtonColor(int color) {
        mTvRightText.setTextColor(AppConfig.getContext().getResources().getColor(color));
        return this;
    }


    public PopDialog setRightButtonBlackColor() {
        mTvRightText.setTextColor(AppConfig.getContext().getResources().getColor(R.color.colorText));
        return this;
    }

    public PopDialog setOnViewClickListener(OnViewClickListener onViewClickListener) {
        mOnViewClickListener = onViewClickListener;
        show();
        return this;
    }

    public PopDialog setRightImage(int resId, int width, int height) {
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mIvRight.getLayoutParams();
        lp.width = width;
        lp.height = height;
        mIvRight.setImageResource(resId);
        mIvRight.setVisibility(View.VISIBLE);
        return this;
    }

    public PopDialog setMargin(int margin) {
        mMargin = margin;
        return this;
    }

    public PopDialog setLeftImage(int resId, int width, int height) {
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mIvLeft.getLayoutParams();
        lp.width = width;
        lp.height = height;
        mIvLeft.setImageResource(resId);
        mIvLeft.setVisibility(View.VISIBLE);
        return this;
    }


    public PopDialog setGravity(int gravity) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        switch (gravity) {
            case Gravity.TOP:
                lp.y = -(getScreensHeight() - lp.height) / 2; // 新位置Y坐标
                break;
            case Gravity.BOTTOM:
                lp.y = (getScreensHeight() - lp.height) / 2; // 新位置Y坐标
                break;
        }
        getWindow().setAttributes(lp);
        return this;
    }

    public PopDialog setWidth(int width) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.width = width + dip2Px(16);
        getWindow().setAttributes(lp);
        mIsSetWidth = true;
        return this;
    }

    @Override
    public void show() {
        if (!mIsSetWidth) {
            WindowManager.LayoutParams attributes = getWindow().getAttributes();
//            attributes.width = (int) (getContext().getResources().getDisplayMetrics().widthPixels * 0.8);
            attributes.width = getScreensWidth() -dip2Px(mMargin) * 2;
            getWindow().setAttributes(attributes);
        }
        super.show();
    }

    /**
     * 窗口变暗的程度。1.0表示完全不透明，0.0表示没有变暗
     *
     * @param amount
     * @return
     */
    public PopDialog setDimAmount(float amount) {
        getWindow().setDimAmount(amount);
        return this;
    }

    public PopDialog addItem(String text, int tag) {
        return addItem(text, 0, tag);
    }

    public PopDialog addItem(String text, @DrawableRes int icon, int tag) {
        return addItem(text, icon, 0, tag);
    }

    public PopDialog addItem(String text, @DrawableRes int icon, int icPadding, int tag) {
        View view = View.inflate(getContext(), R.layout.dialog_item, null);
        View line = view.findViewById(R.id.line);
        RelativeLayout relativeLayout = (RelativeLayout) view.findViewById(R.id.itemLayout);
        relativeLayout.setOnClickListener(this);
        relativeLayout.setTag(tag);
        TextView textView = (TextView) view.findViewById(R.id.tvText);
        ImageView imageView = (ImageView) view.findViewById(R.id.ivIcon);
        if (icon != 0) {
            imageView.setImageResource(icon);
            imageView.setPadding(icPadding, icPadding, icPadding, icPadding);
        } else {
            imageView.setVisibility(View.GONE);
        }
        textView.setText(Html.fromHtml(text));
        mContentContainer.setBackgroundResource(R.color.colorTheme);
        if (mContentContainer != null) {
            if (mContentContainer.getChildCount() == 0) {
                line.setVisibility(View.GONE);
            }
            mContentContainer.addView(view);
            mContentContainer.setVisibility(View.VISIBLE);
        }
        mTextContainer.setVisibility(View.GONE);
        return this;
    }

    public PopDialog(Context context) {
        this(context, R.style.Hint_Dialog);
        mActivity = (BaseActivity) context;
    }

    @SuppressLint("InflateParams")
    private PopDialog(Context context, int defStyle) {
        super(context, defStyle);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_pop, null);
        mTvTitle = dialogView.findViewById(R.id.tvTitle);
        mTvContent = dialogView.findViewById(R.id.tvContent);
        mTvLeftText = dialogView.findViewById(R.id.tvLeftText);
        mTvRightText = dialogView.findViewById(R.id.tvRightText);
        mLeftBtnLayout = dialogView.findViewById(R.id.leftBtnLayout);
        mRightBtnLayout = dialogView.findViewById(R.id.rightBtnLayout);
        mContentContainer = dialogView.findViewById(R.id.contentContainer);
        mSuperContainer = dialogView.findViewById(R.id.superContainer);
        mTvTitleSub = dialogView.findViewById(R.id.tvTitleSub);
        mTextContainer = dialogView.findViewById(R.id.textContainer);
        mHorizontalHalvingLine = dialogView.findViewById(R.id.horizontalHalvingLine);
        mVerticalHalvingLine = dialogView.findViewById(R.id.verticalHalvingLine);
        mIvLeft = dialogView.findViewById(R.id.ivLeft);
        mIvRight = dialogView.findViewById(R.id.ivRight);
        mLeftBtnLayout.setOnClickListener(this);
        mRightBtnLayout.setOnClickListener(this);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setCanceledOnTouchOutside(true);
        super.setContentView(dialogView);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setGravity(Gravity.CENTER);
        WindowManager.LayoutParams attributes = getWindow().getAttributes();
        attributes.alpha = 1f;
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        getWindow().setAttributes(attributes);
        mActivity.addDestroyListener(this);
        mDialogs.add(this);
    }


    @Override
    public void onClick(View v) {
        if (mOnPopDialogClick != null) {
            mOnPopDialogClick.onItemClick(v);
            if (mNeedDismiss) {
                this.dismiss();
            }
        }
        int i = v.getId();
        if (i == R.id.leftBtnLayout) {
            if (mOnViewClickListener != null) {
                mOnViewClickListener.onClick(v, true);
            }
            if (mNeedDismiss) {
                this.dismiss();
            }
        } else if (i == R.id.rightBtnLayout) {
            if (mOnViewClickListener != null) {
                mOnViewClickListener.onClick(v, false);
            }
            if (mNeedDismiss) {
                this.dismiss();
            }
        }
    }

    @Override
    public void dismiss() {
        clear();
        try {
            //判断是所在Activity是否已经销毁
            if (getContext() instanceof Activity) {
                if (!((Activity) getContext()).isFinishing() && !((Activity) getContext()).isDestroyed()){
                    super.dismiss();
                }
            } else{
                super.dismiss();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 在不绘制textView的情况下算出textView的高度，
     * 并且根据最大行数得到应该显示最后一个字符的下标，
     * 请在主线程顺序执行，第一个返回值是最后一个字符的下标，第二个返回值是高度
     *
     * @param textView
     * @param content
     * @param width
     * @param maxLine
     * @return
     */
    public int[] measureTextViewHeight(TextView textView, String content, int width, int maxLine) {
        TextPaint textPaint = textView.getPaint();
        StaticLayout staticLayout = new StaticLayout(content, textPaint, width, Layout.Alignment.ALIGN_NORMAL, 1, 0, false);
        int[] result = new int[2];
        if (staticLayout.getLineCount() > maxLine) {//如果行数超出限制
            int lastIndex = staticLayout.getLineStart(maxLine) - 1;
            result[0] = lastIndex;
            result[1] = new StaticLayout(content.substring(0, lastIndex), textPaint, width, Layout.Alignment.ALIGN_NORMAL, 1, 0, false).getHeight();
            return result;
        } else {//如果行数没有超出限制
            result[0] = -1;
            result[1] = staticLayout.getHeight();
            return result;
        }
    }


    private void clear() {
        mOnPopDialogClick = null;
        mOnViewClickListener = null;
    }

    public OnPopDialogClick mOnPopDialogClick;

    public PopDialog setOnPopDialogClick(OnPopDialogClick onPopDialogClick) {
        mOnPopDialogClick = onPopDialogClick;
        return this;
    }


    @Override
    public void onDestroy() {
        mDialogs.remove(this);
        if (mActivity != null){
            mActivity.removeDestroyListener(this);
        }
        mActivity = null;
        dismiss();
    }


    public static void dismissAll() {
        for (int i = mDialogs.size() - 1; i >= 0; i--) {
            mDialogs.get(i).dismiss();
        }
    }

    public interface OnPopDialogClick {
        void onItemClick(View v);
    }

    public interface OnViewClickListener {
        void onClick(View view, boolean isConfirm);
    }


}
