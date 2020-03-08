package com.centaurstech.sdk.utils;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.centaurstech.sdk.listener.AnimatorListener;

/**
 * 动画工具类
 *
 * @author Leon(黄长亮)
 * @date 17/5/25
 */
public class AnimatorUtils {

    public static void rotation(final View view, int duration){
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(view,"rotation",360f);
        objectAnimator.setDuration(duration);
        objectAnimator.start();
    }

    public static void show(final View view, int duration){
        show(view,duration,null);
    }

    public static void show(final View view, int duration,final AnimatorListener animatorListener){
        view.setAlpha(0);
        if (view.getVisibility() == View.GONE){
            view.setVisibility(View.VISIBLE);
        }
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0f,1.0f);
        valueAnimator.setDuration(duration);
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                view.setAlpha(value);
                if (animatorListener != null){
                    if (value == 1)animatorListener.end();
                }
            }
        });
        if (animatorListener!= null)animatorListener.start();
        valueAnimator.start();
    }

    public static void hide(final View view, int duration){
        hide(view,duration,null);
    }

    public static void hide(final View view, int duration,final AnimatorListener animatorListener){
        if (view.getAlpha() == 0 || view.getVisibility() == View.GONE)return;
        view.setAlpha(1);
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(1.0f,0f);
        valueAnimator.setDuration(duration);
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                view.setAlpha(value);
                if (value == 0){
                    view.setVisibility(View.GONE);
                    if (animatorListener!= null)animatorListener.end();
                }
            }
        });
        if (animatorListener!= null)animatorListener.start();
        valueAnimator.start();
    }

    public static void addMargin(final View view, final int direction, int duration, final int value){
        final ValueAnimator valueAnimator = ValueAnimator.ofFloat(0f,1f);
        valueAnimator.setDuration(duration);
        valueAnimator.setInterpolator(new LinearInterpolator());
        final FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) view.getLayoutParams();
        final int marginLeft = layoutParams.leftMargin;
        final int marginTop = layoutParams.topMargin;
        final int marginRight = layoutParams.rightMargin;
        final int marginBottom = layoutParams.bottomMargin;
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float v = (float) animation.getAnimatedValue();
                switch (direction){
                    case 1: // left
                        layoutParams.setMargins(marginLeft + (int) (v * value),0,0,0 );
                        break;
                    case 2: // top
                        layoutParams.setMargins(0, marginTop + (int) (v * value),0,0);
                        break;
                    case 3: // right
                        layoutParams.setMargins(0,0,marginRight + (int) (v * value), 0);
                        break;
                    case 4: // bottom
                        layoutParams.setMargins(0,0,0, marginBottom + (int) (v * value));
                        break;
                }
                view.requestLayout();
            }
        });
        valueAnimator.start();
    }

    public static void minusMargin(final View view, final int direction, int duration, final int value){
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0f,1.0f);
        valueAnimator.setDuration(duration);
        valueAnimator.setInterpolator(new LinearInterpolator());
        final FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) view.getLayoutParams();
        final int marginLeft = layoutParams.leftMargin;
        final int marginTop = layoutParams.topMargin;
        final int marginRight = layoutParams.rightMargin;
        final int marginBottom = layoutParams.bottomMargin;
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float v = (float) animation.getAnimatedValue();
                FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) view.getLayoutParams();
                switch (direction){
                    case 1:// left
                        layoutParams.setMargins(marginLeft - (int) (v * value),0,0,0 );
                        break;
                    case 2: // top
                        layoutParams.setMargins(0, marginTop - (int) (v * value),0,0);
                        break;
                    case 3: // right
                        layoutParams.setMargins(0,0,marginRight - (int) (v * value), 0);
                        break;
                    case 4: // bottom
                        layoutParams.setMargins(0,0,0, marginBottom - (int) (v * value));
                        break;
                }
                view.requestLayout();
            }
        });
        valueAnimator.start();
    }

    /**
     * @param view
     * @param duration 时长
     * @param value 值
     * @param directions 1:left 2:top 3:right 4:bottom
     */
    public static void setMargin(final View view, int duration, final int value, final AnimatorListener animatorListener, final int... directions){
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0f,1.0f);
        valueAnimator.setDuration(duration);
        valueAnimator.setInterpolator(new LinearInterpolator());
        ViewGroup.LayoutParams tempLp = view.getLayoutParams();

        FrameLayout.LayoutParams flp = null;
        RelativeLayout.LayoutParams rlp = null;
        LinearLayout.LayoutParams llp = null;

        if (tempLp instanceof FrameLayout.LayoutParams){
            flp = (FrameLayout.LayoutParams) tempLp;
        }else if (tempLp instanceof RelativeLayout.LayoutParams){
            rlp = (RelativeLayout.LayoutParams) tempLp;
        }else if (tempLp instanceof LinearLayout.LayoutParams){
            llp = (LinearLayout.LayoutParams) tempLp;
        }

        final int ml;
        final int mt;
        final int mr;
        final int mb;

        if (flp != null){
            ml = flp.leftMargin;
            mt = flp.topMargin;
            mr = flp.rightMargin;
            mb = flp.bottomMargin;
        }else if (rlp != null){
            ml = rlp.leftMargin;
            mt = rlp.topMargin;
            mr = rlp.rightMargin;
            mb = rlp.bottomMargin;
        }else if (llp != null){
            ml = llp.leftMargin;
            mt = llp.topMargin;
            mr = llp.rightMargin;
            mb = llp.bottomMargin;
        }else {
            ml = 0;
            mt = 0;
            mr = 0;
            mb = 0;
        }

        int marginLeft = 0,marginTop = 0,marginRight = 0,marginBottom = 0;

        for (int i : directions){
            switch (i){
                case 1:// left
                    marginLeft = ml - value;
                    break;
                case 2: // top
                    marginTop = mt - value;
                    break;
                case 3: // right
                    marginRight = mr - value;
                    break;
                case 4: // bottom
                    marginBottom = mb - value;
                    break;
            }
        }

        final int finalMarginLeft = marginLeft;
        final int finalMarginTop = marginTop;
        final int finalMarginRight = marginRight;
        final int finalMarginBottom = marginBottom;

        final FrameLayout.LayoutParams finalFlp = flp;
        final RelativeLayout.LayoutParams finalRlp = rlp;
        final LinearLayout.LayoutParams finalLlp = llp;
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float v = (float) animation.getAnimatedValue();
                if (finalFlp != null){
                    finalFlp.setMargins(ml - (int) (v * finalMarginLeft),
                            mt - (int) (v * finalMarginTop),
                            mr - (int) (v * finalMarginRight),
                            mb - (int) (v * finalMarginBottom));
                }else if (finalRlp != null){
                    finalRlp.setMargins(ml - (int) (v * finalMarginLeft),
                            mt - (int) (v * finalMarginTop),
                            mr - (int) (v * finalMarginRight),
                            mb - (int) (v * finalMarginBottom));
                }else if (finalLlp != null){
                    finalLlp.setMargins(ml - (int) (v * finalMarginLeft),
                            mt - (int) (v * finalMarginTop),
                            mr - (int) (v * finalMarginRight),
                            mb - (int) (v * finalMarginBottom));
                }
                view.requestLayout();
                if (animatorListener != null){
                    if (v != 1){
                        animatorListener.running(v);
                    }else {
                        animatorListener.end();
                    }
                }
            }
        });
        valueAnimator.start();
        if (animatorListener != null){
            animatorListener.start();
        }
    }

    /**
     * @param view
     * @param duration 时长
     * @param value 值
     * @param directions 1:left 2:top 3:right 4:bottom
     */
    public static void setMargin(final View view, int duration, final int value, final int...directions){
        setMargin(view,duration,value,null,directions);
    }


    /**
     * @param view
     * @param repeatCount Animation.INFINITE 为无限循环
     * @param alpha 是添加透明度动画
     * @param duration 时长
     * @param size 大小
     * @param radius 圆角
     * @param animatorListener 动画监听
     */
    public static ValueAnimator setSize(final View view, final int repeatCount, final boolean alpha, int duration, int size, int radius, final AnimatorListener animatorListener){
        final ValueAnimator valueAnimator = ValueAnimator.ofFloat(0f,1f);
        valueAnimator.setDuration(duration);
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.setRepeatCount(repeatCount);

        if (alpha){
            view.setAlpha(1);
        }

        ViewGroup.LayoutParams tempLp = view.getLayoutParams();

        FrameLayout.LayoutParams flp = null;
        RelativeLayout.LayoutParams rlp = null;
        LinearLayout.LayoutParams llp = null;

        if (tempLp instanceof FrameLayout.LayoutParams){
            flp = (FrameLayout.LayoutParams) tempLp;
        }else if (tempLp instanceof RelativeLayout.LayoutParams){
            rlp = (RelativeLayout.LayoutParams) tempLp;
        }else if (tempLp instanceof LinearLayout.LayoutParams){
            llp = (LinearLayout.LayoutParams) tempLp;
        }

        final int width;
        final int height;

        if (flp != null){
            width = flp.width;
            height = flp.height;
        }else if (rlp != null){
            width = rlp.width;
            height = rlp.height;
        }else if (llp != null){
            width = llp.width;
            height = llp.height;
        }else {
            width = 0;
            height = 0;
        }

        final int newW = width - size;
        final int newH = height - size;
        float newR = 0;
        float r = 0;

        if (view instanceof CardView){
            r = ((CardView)view).getRadius();
            newR = r - radius;
        }



        final FrameLayout.LayoutParams finalFlp = flp;
        final RelativeLayout.LayoutParams finalRlp = rlp;
        final LinearLayout.LayoutParams finalLlp = llp;

        final float finalNewR = newR;
        final float finalR = r;

        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float v = (float) animation.getAnimatedValue();

                int w,h;
                float r;

                if (v == 1 && repeatCount == Animation.INFINITE){
                    w = width;
                    h = height;
                    r = finalR;
                }else {
                    w = width - (int) (v * newW);
                    h = height - (int) (v * newH);
                    r = finalR - (int) (v * finalNewR);
                }

                if (finalFlp != null){
                    finalFlp.width = w;
                    finalFlp.height = h;
                }else if (finalRlp != null){
                    finalRlp.width = w;
                    finalRlp.height = h;
                }else if (finalLlp != null){
                    finalLlp.width = w;
                    finalLlp.height = h;
                }
                if (view instanceof CardView){
                    ((CardView)view).setRadius(r);
                }
                if (alpha){
                    view.setAlpha(1 - v);
                }

                view.requestLayout();
                if (animatorListener != null){
                    if (v != 1){
                        animatorListener.running(v);
                    }else {
                        animatorListener.end();
                    }
                }
            }
        });
        valueAnimator.start();
        if (animatorListener != null){
            animatorListener.start();
        }
        return valueAnimator;
    }


}
