package com.centaurstech.sdk.activity.movies;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;

import com.centaurstech.qiwu.QiWuAPI;
import com.centaurstech.qiwu.callback.APICallback;
import com.centaurstech.qiwu.common.Const;
import com.centaurstech.qiwu.entity.APIEntity;
import com.centaurstech.qiwu.entity.EventBusEntity;
import com.centaurstech.qiwu.entity.OrderMovieEntity;
import com.centaurstech.qiwu.utils.DateUtils;
import com.centaurstech.qiwu.utils.GsonUtils;
import com.centaurstech.qiwu.utils.LogUtils;
import com.centaurstech.qiwu.utils.UIUtils;
import com.centaurstech.sdk.R;
import com.centaurstech.sdk.SDKApplication;
import com.centaurstech.sdk.activity.BaseActivity;
import com.centaurstech.sdk.activity.PaymentActivity;
import com.centaurstech.sdk.adapter.CinemaTicketAdapter;
import com.centaurstech.sdk.common.Const2;
import com.centaurstech.sdk.databinding.ActivityCinemaDetailBinding;
import com.centaurstech.sdk.utils.ImageUtils;
import com.centaurstech.sdk.utils.ViewUtils;
import com.centaurstech.sdk.view.PopDialog;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

public class OrderDetailCinemaActivity extends BaseActivity implements View.OnClickListener {

    private ActivityCinemaDetailBinding mBinding;

    private String mOrderId = "";

    private OrderMovieEntity mOrderMovieEntity;

    public CountDownTimer mCountDownTimer;

    private List<OrderMovieEntity.CodeBean> mCodeBeans = new ArrayList<>();

    private CinemaTicketAdapter mCinemaTicketAdapter;

    private boolean isTimeOut;

    @Override
    public boolean useEventBus() {
        return true;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_cinema_detail);
        mOrderId = getIntent().getStringExtra(Const.Intent.DATA);
        QiWuAPI.movie.getOrder(mOrderId, new APICallback<APIEntity<OrderMovieEntity>>() {
            @Override
            public void onSuccess(APIEntity<OrderMovieEntity> response) {
                LogUtils.sf(GsonUtils.toJson(response.getData()));
                mOrderMovieEntity = response.getData();
                initData();
            }
        });
    }

    private void initData() {
        mBinding.tvToPay.setOnClickListener(this);
        mBinding.tvCancelOrder.setOnClickListener(this);
        mBinding.tvContactService.setOnClickListener(this);
        mBinding.iHeadLayout.btnNavBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mBinding.iHeadLayout.rlRoot.setBackgroundColor(UIUtils.getColor(R.color.colorWhite));
        mBinding.iHeadLayout.tvTitle.setText(UIUtils.getString(R.string.my_order));
        mBinding.iHeadLayout.tvTitle.setTextColor(UIUtils.getColor(R.color.colorFF000000));
        mBinding.iHeadLayout.btnNavBack.setImageResource(R.mipmap.nav_btn_back);
        if (mOrderMovieEntity != null) {
            ViewUtils.loadImage(mBinding.svMovieImg, mOrderMovieEntity.getMovieImg());
            mBinding.tvMovieName.setText(mOrderMovieEntity.getMovieName());
            mBinding.tvTicketNumber.setText(mOrderMovieEntity.getTicketNumber() + "张");
            String startTimer = DateUtils.date2Date(mOrderMovieEntity.getStartTime(), "yyyy-MM-dd HH:mm", "M月d日 HH:mm");
            String enTime = DateUtils.date2Date(mOrderMovieEntity.getEndTime(), "yyyy-MM-dd HH:mm", "HH:mm");
            try {
                String timer;
                if (DateUtils.IsToday(mOrderMovieEntity.getStartTime())) {
                    timer = "今天" + startTimer + "-" + enTime;
                } else {
                    timer = DateUtils.getWeek(mOrderMovieEntity.getStartTime()) + startTimer + "-" + enTime;
                }
                mBinding.tvTimerType.setText(timer + "(" + mOrderMovieEntity.getMovieLanguage() + mOrderMovieEntity.getMovieScreen() + ")");
            } catch (Exception e) {

            }
            if (!TextUtils.isEmpty(mOrderMovieEntity.getTwoDimenCode())) {
                Bitmap bitmap = ImageUtils.createQRCode(mOrderMovieEntity.getTwoDimenCode());
                mBinding.ivZing.setImageBitmap(bitmap);
                mBinding.flZingLayout.setVisibility(View.VISIBLE);
            } else {
                mBinding.flZingLayout.setVisibility(View.GONE);
            }
            mBinding.tvAddress.setText(mOrderMovieEntity.getCinemaName());
            mBinding.tvPlace.setText(mOrderMovieEntity.getTheaterInfo() + "(" + getSeatPlace() + ")");
            mBinding.tvMovieAddress.setText(mOrderMovieEntity.getCinemaAddress());
            if (!TextUtils.isEmpty(mOrderMovieEntity.getCinemaPhone())) {
                String phone = mOrderMovieEntity.getCinemaPhone().split(" ")[0];
                mBinding.tvPhone.setText(UIUtils.getString(R.string.contact_phone) + "  " + phone);
            }
            mBinding.tvMobile.setText(mOrderMovieEntity.getContactPhone());
            mBinding.tvCreateTimer.setText(UIUtils.getString(R.string.buy_order_time) + "  " + mOrderMovieEntity.getCreateTime());
            mBinding.tvOrderPrice.setText(UIUtils.getString(R.string.money_sign) + mOrderMovieEntity.getAmount());
            mBinding.tvOrderNumber.setText(UIUtils.getString(R.string.order_id) + "  " + mOrderMovieEntity.getOrderId());
            mBinding.tvCinemaName.setText(mOrderMovieEntity.getCinemaName());
            setCinemaState(mOrderMovieEntity.getState());
            mCinemaTicketAdapter = new CinemaTicketAdapter(this);
            mBinding.rvTickets.setLayoutManager(new LinearLayoutManager(this));
            mBinding.rvTickets.setAdapter(mCinemaTicketAdapter);
            mCinemaTicketAdapter.setOrderState(mOrderMovieEntity.getState());
            mCinemaTicketAdapter.setData(mCodeBeans);
        }
    }

    private void setCinemaState(int state) {
        mBinding.tvToPay.setVisibility(View.GONE);
        mBinding.tvCancelOrder.setVisibility(View.GONE);
        mBinding.tvContactService.setVisibility(View.GONE);
        mBinding.llHasTicketLayout.setVisibility(View.GONE);
        mBinding.tvStateDetail.setVisibility(View.GONE);
        mBinding.ivComplete.setVisibility(View.GONE);
        mBinding.tvOrderTimer.setVisibility(View.GONE);
        if (mOrderMovieEntity != null) {
            getCinemaTicket();
            mBinding.tvTicketNumber2.setText(mOrderMovieEntity.getTicketNumber() + "张电影票");
        }
        switch (state) {
            case 0:
                if (mCountDownTimer != null) {
                    mCountDownTimer.cancel();
                }
                payDownTimer();
                mBinding.tvOrderTimer.setVisibility(View.VISIBLE);
                mBinding.tvToPay.setVisibility(View.VISIBLE);
                mBinding.tvCancelOrder.setVisibility(View.VISIBLE);
                break;
            case 13:
                mBinding.tvOrderState.setText(UIUtils.getString(R.string.make_ticket_failed_refunding));
//                mBinding.tvContactService.setVisibility(View.VISIBLE);
                mBinding.tvStateDetail.setVisibility(View.VISIBLE);
                mBinding.tvStateDetail.setText("数据异常出票失败，票款将原路退回");
                break;
            case 1:
                mBinding.tvOrderState.setText(UIUtils.getString(R.string.exit_ticket));
//                mBinding.tvContactService.setVisibility(View.VISIBLE);
                mBinding.tvStateDetail.setVisibility(View.VISIBLE);
                mBinding.tvStateDetail.setText("正在为您出票，请耐心等候");
                break;
            case 6:
                mBinding.tvOrderState.setText(UIUtils.getString(R.string.refund_moneyed));
//                mBinding.tvContactService.setVisibility(View.VISIBLE);
                mBinding.tvStateDetail.setVisibility(View.VISIBLE);
                mBinding.tvStateDetail.setText("数据异常出票失败，票款将原路退回");
                break;
            case 7:
                mBinding.tvOrderState.setText(UIUtils.getString(R.string.canceled));
//                mBinding.tvContactService.setVisibility(View.VISIBLE);
                mBinding.tvStateDetail.setVisibility(View.VISIBLE);
                mBinding.tvStateDetail.setText("订单已取消");
                break;
            case 2:
                mBinding.tvOrderState.setText(UIUtils.getString(R.string.wait_screened));
//                mBinding.tvContactService.setVisibility(View.VISIBLE);
                mBinding.llHasTicketLayout.setVisibility(View.VISIBLE);
                break;
            case 3:
                mBinding.ivComplete.setVisibility(View.VISIBLE);
                mBinding.tvTicketNumber2.setTextColor(UIUtils.getColor(R.color.color9B9B9B));
                mBinding.tvOrderState.setText(UIUtils.getString(R.string.screened));
//                mBinding.tvContactService.setVisibility(View.VISIBLE);
                mBinding.llHasTicketLayout.setVisibility(View.VISIBLE);
                mBinding.ivZing.setAlpha(0.3f);
                break;
            case 11:
                mBinding.tvOrderState.setText(UIUtils.getString(R.string.canceling));
//                mBinding.tvContactService.setVisibility(View.VISIBLE);
                mBinding.tvStateDetail.setVisibility(View.VISIBLE);
                mBinding.tvStateDetail.setText("正在为你取消订单");
                break;
        }
    }

    /**
     * 获取座位
     */
    private String getSeatPlace() {
        if (null == mOrderMovieEntity) {
            return "";
        }
        String seatInfo = "";
        for (int i = 0; i < mOrderMovieEntity.getSeat().size(); i++) {
            if (i == mOrderMovieEntity.getSeat().size() - 1) {
                seatInfo += mOrderMovieEntity.getSeat().get(i).getRow() + "排" + mOrderMovieEntity.getSeat().get(i).getCol() + "座";
            } else {
                seatInfo += mOrderMovieEntity.getSeat().get(i).getRow() + "排" + mOrderMovieEntity.getSeat().get(i).getCol() + "座  ";
            }
        }
        return seatInfo;
    }

    private void getCinemaTicket() {
        mCodeBeans.clear();
        if (null != mOrderMovieEntity.getFront() && mOrderMovieEntity.getFront().size() > 0) {
            for (int i = 0; i < mOrderMovieEntity.getFront().size(); i++) {
                OrderMovieEntity.CodeBean mCodeFont = new OrderMovieEntity.CodeBean();
                mCodeFont.setText(mOrderMovieEntity.getFront().get(i).getText());
                mCodeFont.setValue(mOrderMovieEntity.getFront().get(i).getValue());
                mCodeFont.setState(mOrderMovieEntity.getState());
                mCodeBeans.add(mCodeFont);
            }
        } else {
            if (null != mOrderMovieEntity.getThird() && mOrderMovieEntity.getThird().size() > 0) {
                for (int i = 0; i < mOrderMovieEntity.getThird().size(); i++) {
                    OrderMovieEntity.CodeBean mCodeThird = new OrderMovieEntity.CodeBean();
                    mCodeThird.setText("第三方" + mOrderMovieEntity.getThird().get(i).getText());
                    mCodeThird.setValue(mOrderMovieEntity.getThird().get(i).getValue());
                    mCodeThird.setState(mOrderMovieEntity.getState());
                    mCodeBeans.add(mCodeThird);
                }
            }

            if (null != mOrderMovieEntity.getNuoMi() && mOrderMovieEntity.getNuoMi().size() > 0) {
                for (int i = 0; i < mOrderMovieEntity.getNuoMi().size(); i++) {
                    OrderMovieEntity.CodeBean mCodeNuo = new OrderMovieEntity.CodeBean();
                    mCodeNuo.setText("糯米" + mOrderMovieEntity.getNuoMi().get(i).getText());
                    mCodeNuo.setValue(mOrderMovieEntity.getNuoMi().get(i).getValue());
                    mCodeNuo.setState(mOrderMovieEntity.getState());
                    mCodeBeans.add(mCodeNuo);
                }
            }

            if (null != mOrderMovieEntity.getCommon() && mOrderMovieEntity.getCommon().size() > 0) {
                for (int i = 0; i < mOrderMovieEntity.getCommon().size(); i++) {
                    OrderMovieEntity.CodeBean mCodeCommon = new OrderMovieEntity.CodeBean();
                    mCodeCommon.setText("糯米通取" + mOrderMovieEntity.getCommon().get(i).getText());
                    mCodeCommon.setValue(mOrderMovieEntity.getCommon().get(i).getValue());
                    mCodeCommon.setState(mOrderMovieEntity.getState());
                    mCodeBeans.add(mCodeCommon);
                }
            }
        }
        if (mCodeBeans.size() <= 0) {
            OrderMovieEntity.CodeBean mCodeCommon = new OrderMovieEntity.CodeBean();
            mCodeCommon.setText("");
            mCodeCommon.setValue("无数字取票码，请联系客服");
            mCodeCommon.setState(mOrderMovieEntity.getState());
            mCodeBeans.add(mCodeCommon);
        }
    }

    private void payDownTimer() {
        mCountDownTimer = new CountDownTimer(mOrderMovieEntity.getCountdown() * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mBinding.tvOrderTimer.setText(DateUtils.timestamp2String(millisUntilFinished, "mm:ss"));
            }

            @Override
            public void onFinish() {
                try {
                    Message msg = new Message();
                    msg.what = 1;
                    mHandler.sendMessage(msg);
                } catch (Exception e) {
                    finish();
                }
            }
        };
        mCountDownTimer.start();
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            try {
                if (msg.what == 1) {
                    isTimeOut = true;
                    if (mCountDownTimer != null) {
                        mCountDownTimer.cancel();
                    }
                    PopDialog.create(OrderDetailCinemaActivity.this)
                            .setContent("付款超时，请返回首页重新购买?")
                            .setHideRightButton()
                            .setHideTitle()
                            .setCanceledTouchOutside(false)
                            .setOnViewClickListener(new PopDialog.OnViewClickListener() {
                                @Override
                                public void onClick(View view, boolean isConfirm) {
                                    if (isConfirm) {
                                        SDKApplication.returnMainActivity();
                                        EventBus.getDefault().post(new EventBusEntity(Const.Instruct.RETURN_MAIN));
                                    }
                                }
                            });
                }
            } catch (Exception e) {
                finish();
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tvToPay:
                //TODO 跳转二维码页面
                launchActivity(PaymentActivity.class, new IntentExpand() {
                    @Override
                    public void extraValue(Intent intent) {
                        intent.putExtra(Const.Intent.DATA, mOrderId);
                        intent.putExtra(Const.Intent.TYPE, 3);
                    }
                });
                break;
            case R.id.tvContactService:

                break;
            case R.id.tvCancelOrder:
                PopDialog.create(OrderDetailCinemaActivity.this)
                        .setContent("确定要取消订单吗?")
                        .setOnViewClickListener(new PopDialog.OnViewClickListener() {
                            @Override
                            public void onClick(View view, boolean isConfirm) {
                                if (isConfirm) {
                                    showLoadingDialog();
                                    QiWuAPI.movie.cancel(mOrderId, new APICallback<APIEntity<String>>() {
                                        @Override
                                        public void onSuccess(APIEntity<String> response) {
                                            hideLoadingDialog();
                                            finish();
                                        }
                                    });
                                }
                            }
                        });
                break;
        }
    }

    @Override
    public void onEvent(EventBusEntity event) {
        super.onEvent(event);
        switch (event.getMessage()) {
            case Const2.update_order:
                initData();
                break;
        }
    }
}
