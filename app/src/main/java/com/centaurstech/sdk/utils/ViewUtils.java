package com.centaurstech.sdk.utils;

import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.Transformation;
import com.centaurstech.sdk.R;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

import static com.qiwu.ui.util.UIUtils.getContext;
import static com.qiwu.ui.util.UIUtils.getDrawable;

public class ViewUtils {
    /**
     * 加载图片
     *
     * @param imgUrl
     * @param imageView
     */
    public static void loadImage(@NonNull final ImageView imageView, @Nullable String imgUrl) {
        try {
            Glide.with(getContext())
                    .load(imgUrl)
                    .placeholder(getDrawable(R.mipmap.iv_default_photo))
                    .error(getDrawable(R.mipmap.iv_default_photo)).dontAnimate()
                    .into(imageView);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 加载本地资源头像图片
     *
     * @param id
     * @param imageView
     */
    public static void loadHeadImage(@NonNull ImageView imageView, @Nullable int id) {
        loadImage(imageView, id, new CropCircleTransformation(getContext()));
    }

    /**
     * 加载本地资源图片
     *
     * @param id
     * @param imageView
     */
    public static void loadImage(@NonNull ImageView imageView, @Nullable int id) {
        try {
            Glide.with(getContext())
                    .load(id)
                    .placeholder(R.mipmap.iv_default_photo)
                    .error(R.mipmap.iv_default_photo)
                    .into(imageView);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 加载本地资源图片
     *
     * @param id
     * @param imageView
     */
    public static void loadImage(@NonNull ImageView imageView, @Nullable int id, @Nullable Transformation transformation) {
        try {
            Glide.with(getContext())
                    .load(id)
                    .placeholder(R.mipmap.iv_default_photo)
                    .error(R.mipmap.iv_default_photo)
                    .bitmapTransform(transformation)
                    .into(imageView);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 加载头像
     *
     * @param imgUrl
     * @param imageView
     */
    public static void loadHeadImage(@NonNull ImageView imageView, @Nullable String imgUrl) {
        loadImage(imageView, imgUrl, R.mipmap.iv_default_avatar, new CropCircleTransformation(getContext()));
    }

    /**
     * 加载图片
     *
     * @param imgUrl
     * @param errorImgRes
     * @param transformation
     * @param imageView
     */
    public static void loadImage(@NonNull ImageView imageView, @Nullable String imgUrl, @DrawableRes int errorImgRes, @Nullable Transformation transformation) {
        try {
            if (null != transformation) {
                Glide.with(getContext())
                        .load(imgUrl)
                        .placeholder(errorImgRes)
                        .error(errorImgRes)
                        .bitmapTransform(transformation)
                        .into(imageView);
            } else {
                Glide.with(getContext())
                        .load(imgUrl)
                        .placeholder(errorImgRes)
                        .error(errorImgRes)
                        .into(imageView);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
