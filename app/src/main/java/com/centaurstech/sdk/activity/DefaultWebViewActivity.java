package com.centaurstech.sdk.activity;

import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.centaurstech.sdk.R;
import com.centaurstech.sdk.databinding.ActivityMessageCenterBinding;
import com.centaurstech.sdk.utils.JavaScriptInterface;

/**
 * @Author: 樊德鹏
 * 时   间:2019/6/14
 * 简   述:<功能描述>
 */
public class DefaultWebViewActivity extends BaseActivity {

    private ActivityMessageCenterBinding mBinding;

    public static final String URL = "url";
    public static final String TITLE = "title";
    public static final String DATA = "data";

    private String mUrl = "";
    private String mTitle = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_message_center);
        mUrl = getIntent().getStringExtra(URL);
        mTitle = getIntent().getStringExtra(TITLE);
        initView();
    }

    private void initView() {
        mBinding.tvNavTitle.setText(mTitle);
        mBinding.messageWeb.addJavascriptInterface(new JavaScriptInterface(this), "android");//添加js监听 这样html就能调用客户端
        mBinding.messageWeb.setWebViewClient(new SafeWebViewClient());// 设置 WebViewClient
        WebSettings webSettings = mBinding.messageWeb.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        webSettings.setDomStorageEnabled(true);
        webSettings.setBuiltInZoomControls(false);//support zoom
        webSettings.setDisplayZoomControls(false);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setAllowContentAccess(true);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);

        mBinding.btnNavBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mBinding.messageWeb.canGoBack()) {
                    mBinding.messageWeb.goBack();
                    return;
                }
                finish();
            }
        });
        if (!TextUtils.isEmpty(mUrl)) {
            mBinding.messageWeb.loadUrl(mUrl);
        }
    }

    public class SafeWebViewClient extends WebViewClient {
        /**
         * 是否在 WebView 内加载页面
         *
         * @param view
         * @param url
         * @return
         */
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        /**
         * WebView 开始加载页面时回调，一次Frame加载对应一次回调
         *
         * @param view
         * @param url
         * @param favicon
         */
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }

        /**
         * WebView 完成加载页面时回调，一次Frame加载对应一次回调
         *
         * @param view
         * @param url
         */
        @Override
        public void onPageFinished(WebView view, String url) {
//            if (!TextUtils.isEmpty(view.getTitle())) {
//                mBinding.tvNavTitle.setText(view.getTitle());
//            }
        }

        /**
         * WebView 加载页面资源时会回调，每一个资源产生的一次网络加载，除非本地有当前 url 对应有缓存，否则就会加载。
         *
         * @param view WebView
         * @param url  url
         */
        @Override
        public void onLoadResource(WebView view, String url) {
            super.onLoadResource(view, url);
        }

        /**
         * WebView 可以拦截某一次的 request 来返回我们自己加载的数据，这个方法在后面缓存会有很大作用。
         *
         * @param view    WebView
         * @param request 当前产生 request 请求
         * @return WebResourceResponse
         */
        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
            return super.shouldInterceptRequest(view, request);
        }

        /**
         * WebView 访问 url 出错
         *
         * @param view
         * @param request
         * @param error
         */
        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            super.onReceivedError(view, request, error);
        }

        /**
         * WebView ssl 访问证书出错，handler.cancel()取消加载，handler.proceed()对然错误也继续加载
         *
         * @param view
         * @param handler
         * @param error
         */
        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            super.onReceivedSslError(view, handler, error);
        }
    }

    //使用Webview的时候，返回键没有重写的时候会直接关闭程序，这时候其实我们要其执行的知识回退到上一步的操作
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //这是一个监听用的按键的方法，keyCode 监听用户的动作，如果是按了返回键，同时Webview要返回的话，WebView执行回退操作，因为mWebView.canGoBack()返回的是一个Boolean类型，所以我们把它返回为true
        if (keyCode == KeyEvent.KEYCODE_BACK && mBinding.messageWeb.canGoBack()) {
            mBinding.messageWeb.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBinding.messageWeb.destroy();
    }
}
