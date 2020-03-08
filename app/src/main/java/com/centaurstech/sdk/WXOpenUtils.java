package com.centaurstech.sdk;


import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;

import com.centaurstech.qiwu.QiWu;
import com.centaurstech.qiwu.annotation.ContentNonNull;
import com.centaurstech.qiwu.utils.LogUtils;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;


/**
 * 微信开放工具
 *
 * @author Leon(黄长亮)
 * @date 2017/8/10.
 */
public class WXOpenUtils {

    public final static String APP_ID = "wxbb77e671feeede78";

    private static IWXAPI mWXApi;

    public static IWXAPI getInstance() {
        return mWXApi;
    }


    /**
     * 获取微信授权登录
     */
    public static void accredit() {
        initAPI();
        SendAuth.Req req = new SendAuth.Req();
        req.scope = "snsapi_userinfo";
        req.state = "pepper_wx_login";
        mWXApi.sendReq(req);
    }


    /**
     * 调用微信支付
     */
    public static void pay(String appId, String partnerId, String prepayId, String nonceStr, String timeStamp, String sign) {
        initAPI(appId);
        PayReq req = new PayReq();
        req.appId = appId;
        req.partnerId = partnerId;
        req.prepayId = prepayId;
        req.nonceStr = nonceStr;
        req.timeStamp = timeStamp;
        req.packageValue = "Sign=WXPay";
        req.sign = sign;
        // 在支付之前，如果应用没有注册到微信，应该先调用IWXMsg.registerApp将应用注册到微信
        //3.调用微信支付sdk支付方法
        LogUtils.sf("支付返回:" + mWXApi.sendReq(req));
    }

    private static void initAPI() {
        mWXApi = WXAPIFactory.createWXAPI(QiWu.getContext(), APP_ID, false);
        mWXApi.registerApp(APP_ID);
    }

    private static void initAPI(String appId) {
        mWXApi = WXAPIFactory.createWXAPI(QiWu.getContext(), appId, false);
        mWXApi.registerApp(appId);
    }


}
