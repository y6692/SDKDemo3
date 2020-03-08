package com.centaurstech.sdk.activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.view.View;

import com.centaurstech.qiwu.QiWuAPI;
import com.centaurstech.qiwu.callback.APICallback;
import com.centaurstech.qiwu.common.Const;
import com.centaurstech.qiwu.entity.APIEntity;
import com.centaurstech.qiwu.entity.EventBusEntity;
import com.centaurstech.qiwu.entity.PayConfirmEntity;
import com.centaurstech.qiwu.utils.UIUtils;
import com.centaurstech.sdk.QRCodeUtils;
import com.centaurstech.sdk.R;
import com.centaurstech.sdk.common.Const2;
import com.centaurstech.sdk.databinding.ActivityPaymentBinding;
import com.centaurstech.sdk.utils.ToastUtils;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Time:2020/1/3
 * Author: 樊德鹏
 * Description:
 */
public class PaymentActivity extends BaseActivity {

    private ActivityPaymentBinding mBinding;

    private String mOrderId;

    private int mType;

    private int number;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_payment);
        mType = getIntent().getIntExtra(Const.Intent.TYPE, 0);
        mOrderId = getIntent().getStringExtra(Const.Intent.DATA);
        initView();
        initListener();
    }

    private void initView() {
        mBinding.iHeadLayout.rlRoot.setBackgroundColor(UIUtils.getColor(R.color.colorWhite));
        mBinding.iHeadLayout.tvTitle.setTextColor(UIUtils.getColor(R.color.colorFF000000));
        mBinding.iHeadLayout.btnNavBack.setImageResource(R.mipmap.nav_btn_back);
        switch (mType) {
            case 1:
                mBinding.iHeadLayout.tvTitle.setText("火车票支付");
                break;
            case 2:
                mBinding.iHeadLayout.tvTitle.setText("机票支付");
                break;
            case 3:
                mBinding.iHeadLayout.tvTitle.setText("电影票支付");
                break;
        }
        aliPayQRCode();
    }

    private void initListener() {
        mBinding.tvAliPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBinding.tvAliPay.setTextColor(UIUtils.getColor(R.color.colorFFD400));
                mBinding.tvWeChat.setTextColor(UIUtils.getColor(R.color.color2A2A2A));
                aliPayQRCode();
            }
        });

        mBinding.tvWeChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBinding.tvWeChat.setTextColor(UIUtils.getColor(R.color.colorFFD400));
                mBinding.tvAliPay.setTextColor(UIUtils.getColor(R.color.color2A2A2A));
                wechatPayQRCode();
            }
        });

        mBinding.iHeadLayout.btnNavBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mBinding.tvPayComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getPayState();
            }
        });
    }

    /***
     * 支付宝支付
     */
    public void aliPayQRCode() {
        QiWuAPI.pay.aliPay(mOrderId, 2, new APICallback<APIEntity<String>>() {
            @Override
            public void onSuccess(APIEntity<String> response) {
                switch (response.getRetcode()) {
                    case 0:
                        mBinding.ivCode.setImageBitmap(QRCodeUtils.createQRCodeBitmap(response.getData(), UIUtils.dip2Px(200), UIUtils.dip2Px(200)));
                        break;
                }
            }
        });
    }

    private void getPayState() {
        QiWuAPI.order.getPayState(mOrderId, new APICallback<APIEntity<PayConfirmEntity>>() {
            @Override
            public void onSuccess(APIEntity<PayConfirmEntity> response) {
                switch (response.getRetcode()) {
                    case 0:
                        switch (response.getData().getMark()) {
                            case 0://支付失败
                                break;
                            case 1://支付成功
                                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        ToastUtils.show("支付成功");
                                        EventBus.getDefault().post(new EventBusEntity(Const2.update_order));
                                        finish();
                                    }
                                }, 2000);
                                break;
                            case 2://未支付
                                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        ToastUtils.show("订单未支付");
                                    }
                                }, 2000);
                                break;
                        }
                        break;
                }
            }
        });
    }

    /***
     * 微信支付
     */
    public void wechatPayQRCode() {
        QiWuAPI.pay.wechatPay(mOrderId, 2, new APICallback<APIEntity<String>>() {
            @Override
            public void onSuccess(APIEntity<String> response) {
                switch (response.getRetcode()) {
                    case 0:
                        try {
                            JSONObject json = new JSONObject(response.getData());
                            mBinding.ivCode.setImageBitmap(QRCodeUtils.createQRCodeBitmap(json.optJSONObject("xml").optString("codeUrl"), UIUtils.dip2Px(200), UIUtils.dip2Px(200)));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        break;
                }
            }
        });
    }
}
