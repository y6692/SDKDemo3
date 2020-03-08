package com.centaurstech.sdk.view;

import android.Manifest;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimationDrawable;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.centaurstech.qiwu.QiWu;
import com.centaurstech.qiwu.QiWuPlayer;
import com.centaurstech.qiwu.common.Const;
import com.centaurstech.qiwu.common.PlayType;
import com.centaurstech.qiwu.manager.AudioPlayManager;
import com.centaurstech.qiwu.manager.MusicPlayManager;
import com.centaurstech.qiwu.utils.LogUtils;
import com.centaurstech.sdk.R;
import com.centaurstech.qiwu.entity.EventBusEntity;
import com.centaurstech.voice.QiWuVoice;
import com.qiwu.ui.util.UIUtils;
import com.qiwu.ui.view.WaveLineView;

import org.greenrobot.eventbus.EventBus;

import java.util.Random;


/**
 * @describe 监听按钮
 * @date 2018/8/6
 */

public class ListeningButton extends FrameLayout {


    private ImageView mIvListening;
    private AnimationDrawable mAnimationThink;
    private WaveLineView mWaveLineView;
    private int mIvWidth = 0;
    private State mState = State.IDLE;
    private View mLineView;
    private LayoutParams mVlp;
    private LayoutParams mIlp;
    private LayoutParams mWlp;
    private LayoutParams mFlp;

    public ListeningButton(@NonNull Context context) {
        this(context, null);
    }

    public ListeningButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ListeningButton(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        initListeningView();
        mIvListening = new ImageView(getContext());
        mIvListening.setBackgroundResource(R.drawable.animation_think);
        mAnimationThink = (AnimationDrawable) mIvListening.getBackground();
        mAnimationThink.setOneShot(false);


        addView(mIvListening, UIUtils.dip2Px(38), UIUtils.dip2Px(38));
        mIlp = (LayoutParams) mIvListening.getLayoutParams();
        mIlp.gravity = Gravity.CENTER;
        mIvListening.requestLayout();

        mLineView = new View(getContext());
        mLineView.setBackground(UIUtils.getDrawable(R.drawable.shape_listening));
        addView(mLineView, 0, UIUtils.dip2Px(2));
        mVlp = (LayoutParams) mLineView.getLayoutParams();
        mVlp.gravity = Gravity.CENTER;
        mLineView.requestLayout();
    }

    private void initListeningView() {
        if (mWaveLineView == null) {
            mWaveLineView = new WaveLineView(getContext());
            mWaveLineView.setBackGroundColor(UIUtils.getColor(R.color.colorTheme));
            addView(mWaveLineView, 0, ViewGroup.LayoutParams.MATCH_PARENT);
            mWaveLineView.startAnim();
            mWlp = (LayoutParams) mWaveLineView.getLayoutParams();
            mWlp.gravity = Gravity.CENTER;
            mWaveLineView.setLayoutParams(mWlp);
        }
    }

    private int duration = 350;

    private void startListen(final boolean isDelayed) {
        if (mAnimator != null && mAnimator.isRunning()) return;
        if (mState == State.LISTENING) return;
        mState = State.LISTENING;
        mAnimationThink.stop();
        QiWuVoice.getTTS().stop();
        if (!isDelayed) {
            AudioPlayManager.getInstance().play(R.raw.begin, PlayType.TONE);
        } else {
            AudioPlayManager.getInstance().play(getVoice(), PlayType.TONE);
        }
        if (mAnimatorCallback != null) mAnimatorCallback.hide();
        if (QiWuPlayer.getInstance().isPlaying()) {
            QiWuPlayer.getInstance().pause();
        }
//        EventBus.getDefault().post(new EventBusEntity(Const.Instruct.START_RECORD));
        setListenAnimator(duration, new OnAnimatorListener() {
            @Override
            public void onState(int state) {
                switch (state) {
                    case 0:

                        break;
                    case 1:

                        break;
                    case 2:
                        if (!isDelayed) {
                            QiWuVoice.getASR().start();
                        }
                        break;
                }
            }
        });

    }

    private void think(final boolean isClick) {
        if (mAnimator != null && mAnimator.isRunning()) return;
        if (mState == State.THINK) return;
        mState = State.THINK;
        mAnimationThink.start();
        AudioPlayManager.getInstance().play(R.raw.end, 888);
        EventBus.getDefault().post(new EventBusEntity(Const.Instruct.CLOSE_RECORD));
        setThinkAnimator(duration, new OnAnimatorListener() {
            @Override
            public void onState(int state) {
                switch (state) {
                    case 0:
                        break;
                    case 1:
                        break;
                    case 2:
                        QiWuVoice.getASR().stop();
                        QiWuVoice.getWakeup().start();
                        if (mAnimatorCallback != null) mAnimatorCallback.hide();
                        if (!QiWuVoice.getASR().isRecording() && isClick) {
                            mAnimationThink.stop();
                            mAnimationThink.selectDrawable(0);
                            mState = State.IDLE;
                            if (mAnimatorCallback != null) mAnimatorCallback.show();
                        }
                        break;
                }
            }
        });

    }

    private void idle() {
        if (mAnimator != null && mAnimator.isRunning()) {
            return;
        }
        if (mState == State.IDLE) {
            QiWuVoice.getWakeup().start();
            mAnimationThink.stop();
            mAnimationThink.selectDrawable(0);
            return;
        }
        if (mState == State.LISTENING) {
            mState = State.IDLE;
            if (!MusicPlayManager.getInstance().isPlaying()) {
                AudioPlayManager.getInstance().play(R.raw.end, 888);
            }
            setThinkAnimator(duration, new OnAnimatorListener() {
                @Override
                public void onState(int state) {
                    switch (state) {
                        case 0:
                            break;
                        case 1:
                            break;
                        case 2:
                            mAnimationThink.stop();
                            mAnimationThink.selectDrawable(0);
                            QiWuVoice.getWakeup().start();
                            if (mAnimatorCallback != null) mAnimatorCallback.show();
                            break;
                    }
                }
            });
        } else {
            mState = State.IDLE;
            mAnimationThink.stop();
            mAnimationThink.selectDrawable(0);
            QiWuVoice.getWakeup().start();
            if (mAnimatorCallback != null) mAnimatorCallback.show();
        }

    }

    public void setState(State state) {
        setState(state, true);
    }

    public void setState(State state, final boolean isAutoStop) {
        setState(state, isAutoStop, false);
    }

    public void setState(final State state, final boolean isAutoStop, final boolean isDelayed) {
        setState(state, false, isAutoStop, isDelayed);
    }

    public void setState(final State state, final boolean isClick, final boolean isAutoStop, final boolean isDelayed) {
        LogUtils.sf("setState4 ：" + state + "-" + isClick + "-" + isAutoStop + "-" + isDelayed);
        switch (state) {
            case THINK:
                if (mState == State.IDLE) {
                    mIvListening.setVisibility(VISIBLE);
                    mAnimationThink.start();
                    if (mAnimatorCallback != null) mAnimatorCallback.hide();
                    mState = state;
                } else if (mState == State.LISTENING) {
                    think(isClick);
                }
                break;
            case LISTENING:
                if (ActivityCompat.checkSelfPermission(QiWu.getContext(), Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                    startListen(isDelayed);
                }
                break;
            case IDLE:
                idle();
                break;
        }

    }

    private ValueAnimator mAnimator;

    private void setListenAnimator(int duration, final OnAnimatorListener animatorListener) {
        if (mAnimator != null && mAnimator.isRunning()) return;
        if (mIvWidth == 0) mIvWidth = mIvListening.getWidth();
        mFlp = (LayoutParams) getLayoutParams();
        mAnimator = ValueAnimator.ofFloat(2, 0);
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                value = Float.valueOf(String.format("%.1f", value));
                if (value >= 1) { // 圆消失
                    mIlp.width = (int) ((value - 1) * mIvWidth);
                    mIlp.height = (int) ((value - 1) * mIvWidth);
                    mIvListening.requestLayout();
                } else { //线展开
                    mIlp.width = 0;
                    mIlp.height = 0;
                    mIvListening.requestLayout();
                    float v = (1 - value);
                    mFlp.width = (int) (v * UIUtils.getScreensWidth());
                    mVlp.width = (int) (v * UIUtils.getScreensWidth());
                    mWlp.width = (int) (v * UIUtils.getScreensWidth());
                    requestLayout();
                }

                if (value == 1.1 || value == 0.9) {
                    value = 1;
                }

                if (2 == value) { // 动画开始
                    mIvListening.setVisibility(VISIBLE);
                    if (animatorListener != null) animatorListener.onState(0);
                } else if (1 == value) { // 动画结束
                    mIvListening.setVisibility(INVISIBLE);
                    mLineView.setVisibility(VISIBLE);
                    if (animatorListener != null) animatorListener.onState(1);
                } else if (0 == value) { // 动画结束
                    if (animatorListener != null) animatorListener.onState(2);
                    mLineView.setVisibility(INVISIBLE);
                    mWaveLineView.setVisibility(VISIBLE);
                    mWaveLineView.startAnim();
                } else {
                    if (animatorListener != null) animatorListener.onState(1);
                }
            }
        });
        mAnimator.setDuration(duration);
        mAnimator.start();
    }


    private void setThinkAnimator(int duration, final OnAnimatorListener animatorListener) {
        if (mAnimator != null && mAnimator.isRunning()) return;
        if (mIvWidth == 0) mIvWidth = mIvListening.getWidth();
        mFlp = (LayoutParams) getLayoutParams();
        mAnimator = null;
        mAnimator = ValueAnimator.ofFloat(2, 0);
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                value = Float.valueOf(String.format("%.1f", value));
                if (value >= 1) { // 线消失
                    float v = (value - 1);
                    mFlp.width = (int) (v * UIUtils.getScreensWidth());
                    if (mFlp.width < UIUtils.dip2Px(140)) mFlp.width = UIUtils.dip2Px(140);
                    mVlp.width = (int) (v * UIUtils.getScreensWidth());
                    mWlp.width = (int) (v * UIUtils.getScreensWidth());
                    requestLayout();
                } else { //圆展开
                    mVlp.width = 0;
                    mLineView.requestLayout();
                    float v = (1 - value);
                    mIlp.width = (int) ((v) * mIvWidth);
                    mIlp.height = (int) ((v) * mIvWidth);
                    mIvListening.requestLayout();
                }

                if (2 == value) { // 动画开始
                    mWaveLineView.setVolume(0);
                    mWaveLineView.setVisibility(INVISIBLE);
                    mLineView.setVisibility(VISIBLE);
                    if (animatorListener != null) animatorListener.onState(0);
                } else if (1 == value) {
                    mIvListening.setVisibility(VISIBLE);
                    mLineView.setVisibility(INVISIBLE);
                    if (animatorListener != null) animatorListener.onState(1);
                } else if (0 == value) {
                    mIvListening.setVisibility(VISIBLE);
                    if (animatorListener != null) animatorListener.onState(2);
                } else {
                    if (animatorListener != null) animatorListener.onState(1);
                }

            }
        });
        mAnimator.setDuration(duration);
        mAnimator.start();
    }


    public boolean isRunning() {
        return mAnimator != null && mAnimator.isRunning();
    }


    public void setVolume(int volume) {
        mWaveLineView.setVolume(volume);
    }

    public State getState() {
        return mState;
    }

    public enum State {
        LISTENING, THINK, IDLE
    }

    private interface OnAnimatorListener {
        void onState(int state); // 0:开始 1:动画中 2:结束
    }


    public void setAnimatorCallback(AnimatorCallback animatorCallback) {
        mAnimatorCallback = animatorCallback;
    }

    public AnimatorCallback mAnimatorCallback;

    public interface AnimatorCallback {
        void show();

        void hide();
    }

    public int getVoice() {
        int[] voices = new int[]{R.raw.qingfenfu, R.raw.nihao, R.raw.wozai, R.raw.zaine};
        return voices[new Random().nextInt(3)];
    }


}
