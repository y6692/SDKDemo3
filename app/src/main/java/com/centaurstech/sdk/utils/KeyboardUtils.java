package com.centaurstech.sdk.utils;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

/**
 * 键盘工具类
 *
 * @author Leon(黄长亮)
 * @date 17/5/10
 */
public class KeyboardUtils {




    /**
     * 关闭软键盘
     */
    public static void closeKeyboard(Activity activity) {
        View view = activity.getWindow().peekDecorView();
        if (view != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }


    /**
     * 打卡软键盘
     *
     * @param mEditText
     *            输入框
     */
    public static void openKeyboard(EditText mEditText)
    {
        InputMethodManager imm = (InputMethodManager) mEditText.getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(mEditText, InputMethodManager.RESULT_SHOWN);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,
                InputMethodManager.HIDE_IMPLICIT_ONLY);
    }


    /**
     * 关闭软键盘
     *
     * @param mEditText
     *            输入框
     */
    public static void closeKeyboard(EditText mEditText)
    {
        InputMethodManager imm = (InputMethodManager) mEditText.getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);

        imm.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
    }


}
