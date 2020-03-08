package com.centaurstech.sdk;

import android.app.Activity;
import android.text.TextUtils;

import com.alipay.sdk.app.PayTask;
import com.centaurstech.qiwu.callback.PayCallback;
import com.centaurstech.qiwu.common.ThreadPoolFactory;
import com.centaurstech.qiwu.utils.LogUtils;
import com.centaurstech.qiwu.utils.UIUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Leon(黄长亮)
 * @describe 支付管理
 * @date 2019/6/27
 */
public class PayManager {

    private static PayManager instance;

    public static PayManager getInstance(){
        if (instance == null){
            instance = new PayManager();
        }
        return instance;
    }

    private List<String> mOid = null;
    private PayCallback mPayCallback;
    private PayCallback.Type mType;

    public void aliPay(final String response, final Activity activity, final PayCallback payCallback) {
        mOid = new ArrayList<>();
        aliPay(response,activity,payCallback,mOid);
    }

    public void aliPay(final String response, final Activity activity, final PayCallback payCallback, final List<String> ids) {
        if (TextUtils.isEmpty(response))return;
        ThreadPoolFactory.getNormalProxy().execute(new Runnable() {
            @Override
            public void run() {
                mType = PayCallback.Type.ALIPAY;
                mOid = ids;
                mPayCallback = payCallback;
                PayTask alipay = new PayTask(activity);
                final Map<String, String> result = alipay.payV2(response, true);
                final AliPayResultEntity payResult = new AliPayResultEntity(result);
                UIUtils.postTaskSafely(new Runnable() {
                    @Override
                    public void run() {
                        //  String resultInfo = payResult.getResult();// 同步返回需要验证的信息
                        String resultStatus = payResult.getResultStatus();
                        LogUtils.sf(resultStatus);
                        // 判断resultStatus 为9000则代表支付成功
                        if (TextUtils.equals(resultStatus, "9000")) {
                            // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
                            if (mPayCallback != null) {
                                mPayCallback.onPayResult(PayCallback.State.FINISH, PayCallback.Type.ALIPAY, getOid());
                            }
                        } else {
                            // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
                            if (mPayCallback != null) {
                                mPayCallback.onPayResult(PayCallback.State.CANCEL, PayCallback.Type.WALLET, getOid());
                            }
                        }
                    }
                });
            }
        });
    }


    public void wechatPay(final String response,PayCallback payCallback) {
        wechatPay(response,mOid,payCallback);
    }


    public void wechatPay(final String response,List<String> ids,PayCallback payCallback) {
        if (TextUtils.isEmpty(response))return;
        try {
            mType = PayCallback.Type.WECHAT_PAY;
            mOid = ids;
            mPayCallback = payCallback;
            JSONObject j = new JSONObject(response);
            JSONObject param = j.optJSONObject("xml");
            LogUtils.sf(param.optString("appid"));
            LogUtils.sf(param.optString("partnerid"));
            LogUtils.sf(param.optString("prepayid"));
            LogUtils.sf(param.optString("noncestr"));
            LogUtils.sf(param.optString("timestamp"));
            LogUtils.sf(param.optString("sign"));
            WXOpenUtils.pay(
                    param.optString("appid"),
                    param.optString("partnerid"),
                    param.optString("prepayid"),
                    param.optString("noncestr"),
                    param.optString("timestamp"),
                    param.optString("sign"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void payFinish() {
        if (mPayCallback != null) {
            mPayCallback.onPayResult(PayCallback.State.FINISH, mType, mOid);
            mPayCallback = null;
            mOid = null;
        }
    }

    public void payFailure() {
        if (mPayCallback != null) {
            mPayCallback.onPayResult(PayCallback.State.FAILURE, mType, mOid);
        }
    }

    public void payCancel() {
        if (mPayCallback != null) {
            mPayCallback.onPayResult(PayCallback.State.CANCEL, mType, mOid);
            mPayCallback = null;
            mOid = null;
        }
    }

    public List<String> getOid() {
        return mOid;
    }
}
