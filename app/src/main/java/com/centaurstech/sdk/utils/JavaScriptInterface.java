package com.centaurstech.sdk.utils;

import android.text.TextUtils;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import com.centaurstech.sdk.activity.BaseActivity;

/**
 * @Author: 樊德鹏
 * 时   间:2019/2/14
 * 简   述:<JS互调工具类>
 */

public class JavaScriptInterface {

    private BaseActivity mActivity;

    private WebView mWebView;

    public JavaScriptInterface(BaseActivity activity) {
        mActivity = activity;
    }

    public JavaScriptInterface(BaseActivity activity, WebView webView) {
        mActivity = activity;
        this.mWebView = webView;
    }

    /**
     * 与js交互时用到的方法，在js里直接调用的
     */
    @JavascriptInterface
    public void openAcivity(String type) {
        if (TextUtils.isEmpty(type)) {
            return;
        }
        int typeActivity = Integer.parseInt(type);
    }

    @JavascriptInterface
    public void alertMessage() {

    }
}
