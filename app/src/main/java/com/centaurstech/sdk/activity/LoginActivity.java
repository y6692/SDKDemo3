package com.centaurstech.sdk.activity;

import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;

import com.centaurstech.qiwu.QiWuAPI;
import com.centaurstech.qiwu.callback.APICallback;
import com.centaurstech.qiwu.common.DataStore;
import com.centaurstech.qiwu.entity.APIEntity;
import com.centaurstech.qiwu.entity.TokenEntity;
import com.centaurstech.qiwu.entity.UserInfoEntity;
import com.centaurstech.qiwu.utils.LogUtils;
import com.centaurstech.qiwu.utils.UIUtils;
import com.centaurstech.qiwu.utils.VerifyUtils;
import com.centaurstech.sdk.R;
import com.centaurstech.sdk.WXOpenUtils;
import com.centaurstech.sdk.common.CaptchaCountDownTimer;
import com.centaurstech.sdk.common.Const2;
import com.centaurstech.sdk.databinding.ActivityLoginBinding;
import com.centaurstech.sdk.databinding.LayoutImageCaptchaBinding;
import com.centaurstech.qiwu.entity.EventBusEntity;
import com.centaurstech.sdk.utils.ToastUtils;
import com.centaurstech.sdk.view.PopDialog;

import org.greenrobot.eventbus.EventBus;

/**
 * @author Leon(黄长亮)
 * @date 2019/6/22
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener {

    ActivityLoginBinding mLoginBinding;

    CaptchaCountDownTimer mCaptchaCountDownTimer;

    LayoutImageCaptchaBinding mImageCaptchaBinding;

    private PopDialog mPopDialog;

    private long currTime = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLoginBinding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        long timestamp = DataStore.getSmsTimestamp() * 1000;
        if (System.currentTimeMillis() - timestamp < 60000) {
            startTimer(60000 - (System.currentTimeMillis() - timestamp));
        }
        currTime = System.currentTimeMillis();
        mLoginBinding.tvGetCaptcha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = mLoginBinding.etPhone.getText().toString().trim();
                if (TextUtils.isEmpty(phone)) {
                    ToastUtils.show("手机号不能为空");
                    return;
                }
                if (phone.length() < 11) {
                    return;
                }
                getSMSCaptcha("");
            }
        });

        mLoginBinding.iHeadLayout.tvTitle.setText("登录");

        mLoginBinding.iHeadLayout.btnNavBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mLoginBinding.tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = mLoginBinding.etPhone.getText().toString();
                String captcha = mLoginBinding.etCaptcha.getText().toString();

                if (!VerifyUtils.phoneNumber(phone)) {
                    ToastUtils.show("请输入正确的手机号");
                    return;
                }

                if (TextUtils.isEmpty(phone)) {
                    ToastUtils.show("手机号不能为空");
                    return;
                }

                if (TextUtils.isEmpty(captcha)) {
                    ToastUtils.show("验证码不能为空");
                    return;
                }

                if (phone.length() < 11) {
                    return;
                }
                QiWuAPI.account.login(phone, captcha, new APICallback<TokenEntity>() {
                    @Override
                    public void onSuccess(TokenEntity response) {
                        if (response.isSuccess()) {
                            QiWuAPI.account.getUserInfo(new APICallback<APIEntity<UserInfoEntity>>() {
                                @Override
                                public void onSuccess(APIEntity<UserInfoEntity> response) {
                                    UIUtils.showToast("登录成功");
                                    EventBus.getDefault().post(new EventBusEntity(Const2.login_success));
                                    resultActivity();
                                }
                            });
                        } else {
                            UIUtils.showToast(response.getMsg());
                        }
                    }
                });
            }
        });

        mLoginBinding.tvWechatLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WXOpenUtils.accredit();
            }
        });
    }

    private void startTimer(long timestamp) {
        mCaptchaCountDownTimer = new CaptchaCountDownTimer(mLoginBinding.tvGetCaptcha, timestamp, 1000);
        mCaptchaCountDownTimer.start();
    }

    private void getImageCaptcha() {
        View view = View.inflate(this, R.layout.layout_image_captcha, null);
        mImageCaptchaBinding = DataBindingUtil.bind(view);
        mImageCaptchaBinding.tvClickRefresh.setOnClickListener(this);
        mPopDialog = PopDialog.create(this).setNeedDismiss(false).setHideAllButton();
        mPopDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                mImageCaptchaBinding = null;
                mPopDialog = null;
            }
        });
        mImageCaptchaBinding.tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPopDialog.dismiss();
                mImageCaptchaBinding = null;
                mPopDialog = null;
            }
        });

        mImageCaptchaBinding.tvConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mImageCaptchaBinding == null) return;
                if (TextUtils.isEmpty(mImageCaptchaBinding.etCaptcha.getText())) {
                    ToastUtils.show(R.string.please_input_captcha);
                    return;
                }
                if (!VerifyUtils.numberAndLetter(mImageCaptchaBinding.etCaptcha.getText())) {
                    mImageCaptchaBinding.tvErrorHint.setVisibility(View.VISIBLE);
                    return;
                }
                getSMSCaptcha(mImageCaptchaBinding.etCaptcha.getText().toString().trim());
            }
        });
        mPopDialog.setContent(view)
                .show();
        QiWuAPI.sms.getImageCaptcha(mLoginBinding.etPhone.getText().toString().trim(), new APICallback<APIEntity<Bitmap>>() {
            @Override
            public void onSuccess(APIEntity<Bitmap> response) {
                if (response == null) {
                    LogUtils.sf("showImageCaptchaData dismiss");
                    mPopDialog.dismiss();
                    mImageCaptchaBinding = null;
                    mPopDialog = null;
                    return;
                }
                if (mImageCaptchaBinding != null) {
                    mImageCaptchaBinding.ivCaptcha.setImageBitmap(response.getData());
                }
            }
        });
    }

    private void getSMSCaptcha(String imageCaptcha) {
        mLoginBinding.etCaptcha.requestFocus();
        startTimer(60000);
        QiWuAPI.sms.getCaptcha(mLoginBinding.etPhone.getText().toString().trim(), imageCaptcha, new APICallback<APIEntity<String>>() {
            @Override
            public void onSuccess(APIEntity<String> response) {
                if (response.isSuccess()) {
                    DataStore.addSmsCount(String.valueOf(System.currentTimeMillis()));
                    DataStore.setSmsTimestamp(System.currentTimeMillis() / 1000);
                    ToastUtils.show(R.string.sms_send_succeed);
                    if (mPopDialog != null) {
                        mPopDialog.dismiss();
                        mImageCaptchaBinding = null;
                        mPopDialog = null;
                    }
                } else if (response.getRetcode() == 20002) {
                    if (mCaptchaCountDownTimer != null) {
                        mCaptchaCountDownTimer.onFinish();
                    }
                    if (mImageCaptchaBinding != null) {
                        mImageCaptchaBinding.tvErrorHint.setVisibility(View.VISIBLE);
                    } else {
                        getImageCaptcha();
                    }
                } else {
                    if (mCaptchaCountDownTimer != null) {
                        mCaptchaCountDownTimer.onFinish();
                    }
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tvClickRefresh:
                QiWuAPI.sms.getImageCaptcha(mLoginBinding.etPhone.getText().toString().trim(), new APICallback<APIEntity<Bitmap>>() {
                    @Override
                    public void onSuccess(APIEntity<Bitmap> response) {
                        if (response == null) {
                            LogUtils.sf("showImageCaptchaData dismiss");
                            mPopDialog.dismiss();
                            mImageCaptchaBinding = null;
                            mPopDialog = null;
                            return;
                        }
                        if (mImageCaptchaBinding != null) {
                            mImageCaptchaBinding.ivCaptcha.setImageBitmap(response.getData());
                        }
                    }
                });
                break;
        }
    }
}
