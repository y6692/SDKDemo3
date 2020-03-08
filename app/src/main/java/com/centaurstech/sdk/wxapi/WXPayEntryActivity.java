package com.centaurstech.sdk.wxapi;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup;

import com.centaurstech.qiwu.utils.LogUtils;
import com.centaurstech.sdk.PayManager;
import com.centaurstech.sdk.R;
import com.centaurstech.sdk.WXOpenUtils;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;


public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {

    private static final String TAG = "MicroMsg.SDKSample.WXPayEntryActivity";

    private IWXAPI api;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewGroup view = (ViewGroup) getWindow().getDecorView();
        view.setBackgroundResource(R.color.colorWhite);
        api = WXAPIFactory.createWXAPI(this, WXOpenUtils.APP_ID);
        api.handleIntent(getIntent(), this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq req) {

    }

    @Override
    public void onResp(BaseResp resp) {
        LogUtils.sf(TAG + ":" + "onResp, errCode = " + resp.errCode);
        if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
            switch (resp.errCode) {
                case 0: // 支付成功
                    LogUtils.sf(resp.transaction);
                    PayManager.getInstance().payFinish();
                    break;
                case -1: // 支付失败
                    PayManager.getInstance().payFailure();
                    break;
                case -2: // 支付取消
                    PayManager.getInstance().payCancel();
                    break;
            }

        }
        finish();
    }
}