package com.centaurstech.sdk.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.centaurstech.qiwu.QiWuBot;
import com.centaurstech.qiwu.utils.LogUtils;
import com.centaurstech.qiwu.utils.UIUtils;
import com.centaurstech.sdk.R;
import com.centaurstech.sdk.activity.BaseActivity;
import com.centaurstech.sdk.activity.MainActivity;
import com.centaurstech.sdk.databinding.BarMainBottomBinding;
import com.centaurstech.sdk.utils.KeyboardUtils;
import com.centaurstech.voice.QiWuVoice;


import static com.centaurstech.sdk.view.ListeningButton.State.IDLE;
import static com.centaurstech.sdk.view.ListeningButton.State.LISTENING;
import static com.centaurstech.sdk.view.ListeningButton.State.THINK;


/**
 * @describe Ai聊天按钮
 * @date 2018/7/19
 */

public class MainBottomBar extends FrameLayout implements View.OnClickListener, ListeningButton.AnimatorCallback {

    private OnViewClickListener mOnViewClickListener;
    ListeningButton mBtnListening;
    LinearLayout mItemHelp, mItemUser;

    public MainBottomBar(@NonNull Context context) {
        this(context, null);
    }

    public MainBottomBar(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MainBottomBar(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public BarMainBottomBinding mBottomBinding;

    @SuppressLint("ClickableViewAccessibility")
    private void init() {
        View view = View.inflate(getContext(), R.layout.bar_main_bottom, null);
        mBottomBinding = DataBindingUtil.bind(view);
        view.findViewById(R.id.itemKeyboard).setOnClickListener(this);
//        view.findViewById(R.id.itemCommunication).setOnClickListener(this);
        mItemHelp = view.findViewById(R.id.itemKeyboard);
        mItemUser = view.findViewById(R.id.itemUser);
        mItemHelp.setOnClickListener(this);
        mItemUser.setOnClickListener(this);
        mBottomBinding.ivVoice.setOnClickListener(this);
        mBottomBinding.ivSend.setOnClickListener(this);
        view.findViewById(R.id.btnListening).setOnClickListener(this);
        mBtnListening = view.findViewById(R.id.btnListening);
        mBtnListening.setAnimatorCallback(this);
        addView(view);
        mBottomBinding.etMsg.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == 4) {
                    sendMessage();
                }
                return true;
            }
        });

        mBtnListening.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        break;
                    case MotionEvent.ACTION_MOVE:
                        break;
                    case MotionEvent.ACTION_UP:
                        if (mBtnListening.getState() != IDLE && isLongClicking) {
                            isLongClicking = false;
                            mBtnListening.setState(THINK);
                        }
                        break;
                }
                return false;
            }
        });

        mBottomBinding.etMsg.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    ((BaseActivity) getContext()).getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                }
            }
        });

        mBtnListening.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                isLongClicking = true;
                mBtnListening.setState(LISTENING, false);
                return true;
            }
        });
    }

    private boolean isLongClicking;

    @Override
    public void onClick(View v) {
        if (mOnViewClickListener != null)
            mOnViewClickListener.OnClick(v);
        switch (v.getId()) {
            case R.id.ivVoice:
                mBtnListening.setState(IDLE);
                hideEditText();
                break;
            case R.id.ivSend:
                sendMessage();
                break;
        }
    }

    private void sendMessage() {
        String text = mBottomBinding.etMsg.getText().toString().trim();
        if (!TextUtils.isEmpty(text)) {
            QiWuVoice.getTTS().stop();
            QiWuVoice.getASR().cancel();
            mBottomBinding.etMsg.setText("");
            QiWuBot.sendMessage(text);
        } else {
            UIUtils.showToast(UIUtils.getString(R.string.send_content_not_null));
        }
    }

    private void hideBtn() {
        mItemHelp.setVisibility(GONE);
        mItemUser.setVisibility(GONE);
    }

    private void showBtn() {
        mItemHelp.setVisibility(INVISIBLE);
        mItemUser.setVisibility(INVISIBLE);
    }


    public ListeningButton getBtnListening() {
        return mBtnListening;
    }

    public void setVolume(int volume) {
        mBtnListening.setVolume(volume);
    }


    public void setOnViewClickListener(OnViewClickListener onViewClickListener) {
        mOnViewClickListener = onViewClickListener;
    }

    @Override
    public void show() {
        switch (mBottomBinding.btnListening.getState()) {
            case LISTENING:
                break;
            case THINK:
                break;
            case IDLE:
                showBtn();
                break;
        }
    }

    @Override
    public void hide() {
        hideBtn();
    }


    public void showEditText(boolean isOpenKeyboard) {
        LogUtils.sf("showEditText : " + isOpenKeyboard);
        mBottomBinding.llEditContainer.setVisibility(VISIBLE);
        mBottomBinding.btnListening.setVisibility(GONE);
        mBottomBinding.etMsg.setFocusable(true);
        mBottomBinding.etMsg.setFocusableInTouchMode(true);
        if (isOpenKeyboard) {
            KeyboardUtils.openKeyboard(mBottomBinding.etMsg);
            mBottomBinding.etMsg.requestFocus();
        }
    }


    public void hideEditText() {
        KeyboardUtils.closeKeyboard(mBottomBinding.etMsg);
        MainActivity mainActivity = (MainActivity) getContext();
        View currentFocus = mainActivity.getCurrentFocus();
        if (currentFocus != null) {
            currentFocus.clearFocus();
        }
        mBottomBinding.llEditContainer.setVisibility(GONE);
        mBottomBinding.btnListening.setVisibility(VISIBLE);
    }


    public interface OnViewClickListener {
        void OnClick(View view);
    }

    public enum State {
        KEYBOARD_HIDE_VOICE, KEYBOARD_HIDE_EDIT, KEYBOARD_SHOWING,
    }
}
