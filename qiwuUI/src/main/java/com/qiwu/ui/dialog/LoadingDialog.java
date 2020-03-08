package com.qiwu.ui.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import com.qiwu.ui.R;


/**
 * Created by cy on 2016/7/27.
 * 加载框
 */
public class LoadingDialog extends Dialog {


    public LoadingDialog(Context context) {
        this(context, R.style.Hint_Dialog);
    }


    @SuppressLint("InflateParams")
    private LoadingDialog(Context context, int defStyle) {
        super(context, defStyle);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_loading, null);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setCanceledOnTouchOutside(false);
        super.setContentView(dialogView);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setGravity(Gravity.CENTER);
        WindowManager wm = getWindow().getWindowManager();
        Display defaultDisplay = wm.getDefaultDisplay();
        WindowManager.LayoutParams attributes = getWindow().getAttributes();
        attributes.width = defaultDisplay.getWidth();
        attributes.alpha = 0.95f;
        getWindow().setAttributes(attributes);
    }


}

