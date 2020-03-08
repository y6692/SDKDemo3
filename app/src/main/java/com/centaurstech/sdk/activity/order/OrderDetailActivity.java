package com.centaurstech.sdk.activity.order;

import android.annotation.SuppressLint;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;

import com.centaurstech.qiwu.common.Const;
import com.centaurstech.qiwu.entity.EventBusEntity;
import com.centaurstech.qiwu.entity.FlightInfoEntity;
import com.centaurstech.qiwu.entity.OrderEntity;
import com.centaurstech.qiwu.entity.OrderFlightEntity;
import com.centaurstech.qiwu.entity.OrderTrainEntity;
import com.centaurstech.qiwu.entity.PayConfirmEntity;
import com.centaurstech.qiwu.utils.DateUtils;
import com.centaurstech.qiwu.utils.LogUtils;
import com.centaurstech.qiwu.utils.UIUtils;
import com.centaurstech.sdk.QRCodeUtils;
import com.centaurstech.sdk.activity.BaseActivity;
import com.centaurstech.sdk.adapter.PassengerAdapter;
import com.centaurstech.sdk.common.Const2;
import com.centaurstech.sdk.view.PopDialog;
import com.centaurstech.voice.QiWuVoice;
import com.centaurstech.qiwu.QiWuAPI;
import com.centaurstech.qiwu.callback.APICallback;
import com.centaurstech.qiwu.callback.PayCallback;
import com.centaurstech.qiwu.entity.APIEntity;
import com.centaurstech.sdk.PayManager;
import com.centaurstech.sdk.R;
import com.centaurstech.sdk.databinding.ActivityOrderDetailBinding;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * @data 2019/12/31
 * @author: 樊德鹏
 * @description:
 */
@SuppressLint("Registered")
public class OrderDetailActivity extends BaseActivity implements View.OnClickListener {

    public ActivityOrderDetailBinding mBinding;
    public String mOrderId;
    public int mState = 1;

    public int mOrderType;

    public long mCountdown;

    public CountDownTimer mCountDownTimer;

    public String mAmount;

    public ArrayList<OrderFlightEntity> mOrderFlightEntities;

    public OrderTrainEntity mOrderTrainEntity;

    public PassengerAdapter mPassengerAdapter;

    OrderFlightEntity mOrderFlightEntity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mOrderId = getIntent().getStringExtra(Const.Intent.DATA);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_order_detail);
        initView();
        initListener();
    }

    private void initView() {
        mBinding.iHeadLayout.rlRoot.setBackgroundColor(UIUtils.getColor(R.color.colorWhite));
        mBinding.iHeadLayout.tvTitle.setText(UIUtils.getString(R.string.order_details));
        mBinding.iHeadLayout.tvTitle.setTextColor(UIUtils.getColor(R.color.colorFF000000));
        mBinding.iHeadLayout.btnNavBack.setImageResource(R.mipmap.nav_btn_back);

        mBinding.rvPassengers.setLayoutManager(new LinearLayoutManager(this) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        mPassengerAdapter = new PassengerAdapter(this);
        mBinding.rvPassengers.setAdapter(mPassengerAdapter);

        mBinding.iHeadLayout.btnNavBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvTicketChanging:
                try {
                    showTicketChangingDialog();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.tvDelete:
                deleteOrder();
                break;
            case R.id.tvCancelOrder:
                cancelOrder();
                break;
            case R.id.tvToPay:
            case R.id.tvToPay1:
                createPayData();
                break;
            case R.id.tvRefund:
            case R.id.tvRefunding:
                refundOrder();
                break;
            case R.id.llDetail:
                showOrderPriceDetail();
                break;
            case R.id.tvRefundDetail:
                refundDetail();
                break;
        }
    }


    public void showTicketChangingDialog() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        QiWuVoice.getTTS().clearMultiple();
        QiWuVoice.getTTS().stop();
    }

    public void setState() {
        mBinding.iOrderHeader.llTravel.setVisibility(View.GONE);
        mBinding.iOrderHeader.llUnpaid.setVisibility(View.GONE);
        mBinding.iOrderHeader.llDelete.setVisibility(View.GONE);
        mBinding.iOrderHeader.llAgainBook.setVisibility(View.GONE);
        mBinding.iOrderHeader.llNoTravel.setVisibility(View.GONE);
        mBinding.iOrderHeader.llRefund.setVisibility(View.GONE);
        mBinding.iOrderHeader.llRefunding.setVisibility(View.GONE);
        mBinding.iOrderHeader.llStateContainer.setVisibility(View.VISIBLE);
        mBinding.iOrderHeader.tvOrderUseState.setTextColor(UIUtils.getColor(R.color.colorFF9100));

        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
        }

        mBinding.iOrderHeader.tvOrderPrice.setText(getString(R.string.money_sign) + " " + mAmount);

        switch (mState) {
            case OrderEntity.State.unpaid:
                mBinding.iOrderHeader.tvOrderUseState.setText(R.string.unpaid);
                mBinding.iOrderHeader.llUnpaid.setVisibility(View.VISIBLE);
                mCountDownTimer = new CountDownTimer(mCountdown * 1000, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        String html = "请<font color=\"#FF9100\">" + DateUtils.timestamp2String(millisUntilFinished, "mm分ss秒") + "</font>内完成支付";
                        mBinding.iOrderHeader.tvOrderUseHint.setText(Html.fromHtml(html));
                    }

                    @Override
                    public void onFinish() {
                        setOrderState(OrderEntity.State.deal_close);
                        setState();
                    }
                };
                mCountDownTimer.start();
                break;
            case OrderEntity.State.make_ticket:
                mBinding.iOrderHeader.tvOrderUseState.setText(R.string.exit_ticket);
                switch (mOrderType) {
                    case 0:
                    case 1:
                        mBinding.iOrderHeader.tvOrderUseHint.setText(R.string.make_ticket_for_company_about);
                        break;
                    case 2:
                        mBinding.iOrderHeader.tvOrderUseHint.setText(R.string.exit_ticket_info);
                        break;
                    case 3:
                        break;
                }
                mBinding.iOrderHeader.llRefunding.setVisibility(View.VISIBLE);
                break;
            case OrderEntity.State.refunding:
                mBinding.iOrderHeader.tvOrderUseState.setText(R.string.refunding);
                switch (mOrderType) {
                    case 0:
                    case 1:
                        mBinding.iOrderHeader.tvOrderUseHint.setText(R.string.refund_need_some_day);
                        break;
                    case 2:
                        mBinding.iOrderHeader.tvOrderUseHint.setText(R.string.applying_refund_for_you);
                        break;
                    case 3:
                        break;
                }
                mBinding.iOrderHeader.llRefund.setVisibility(View.VISIBLE);
                break;
            case OrderEntity.State.to_travel:
                mBinding.iOrderHeader.tvOrderUseState.setText(R.string.no_travel);
                mBinding.iOrderHeader.tvOrderUseHint.setText(R.string.enjoy_your_trip);
                mBinding.iOrderHeader.llNoTravel.setVisibility(View.VISIBLE);
                break;
            case OrderEntity.State.traveled:
                mBinding.iOrderHeader.tvOrderUseState.setText(R.string.travel);
                mBinding.iOrderHeader.tvOrderUseHint.setText(R.string.your_succeed_use_service);
                mBinding.iOrderHeader.llTravel.setVisibility(View.VISIBLE);
                break;
            case OrderEntity.State.make_ticket_failed:
                mBinding.iOrderHeader.tvOrderUseState.setText(R.string.exitTicket_failed);
                mBinding.iOrderHeader.tvOrderUseState.setTextColor(UIUtils.getColor(R.color.color292929));
                mBinding.iOrderHeader.tvOrderUseHint.setText(R.string.request_error_so_failed);
                mBinding.iOrderHeader.llAgainBook.setVisibility(View.VISIBLE);
                break;
            case OrderEntity.State.refunded:
                mBinding.iOrderHeader.tvOrderUseState.setText(R.string.refund_moneyed);
                mBinding.iOrderHeader.tvOrderUseState.setTextColor(UIUtils.getColor(R.color.color292929));
                mBinding.iOrderHeader.tvOrderUseHint.setText(R.string.refund_completed);
                mBinding.iOrderHeader.llDelete.setVisibility(View.VISIBLE);
                break;
            case OrderEntity.State.canceled:
                mBinding.iOrderHeader.tvOrderUseState.setText(R.string.canceled);
                mBinding.iOrderHeader.tvOrderUseState.setTextColor(UIUtils.getColor(R.color.color292929));
                mBinding.iOrderHeader.tvOrderPrice.setText(getString(R.string.money_sign) + " " + mAmount);
                mBinding.iOrderHeader.tvOrderUseHint.setText(R.string.order_canceled);
                mBinding.iOrderHeader.llDelete.setVisibility(View.VISIBLE);
                break;
            case OrderEntity.State.deal_close:
                mBinding.iOrderHeader.tvOrderUseState.setText(R.string.deal_close);
                mBinding.iOrderHeader.tvOrderUseState.setTextColor(UIUtils.getColor(R.color.color292929));
                mBinding.iOrderHeader.tvOrderPrice.setText(getString(R.string.money_sign) + " " + mAmount);
                mBinding.iOrderHeader.tvOrderUseHint.setText(R.string.timeout_auto_cancel);
                mBinding.iOrderHeader.llDelete.setVisibility(View.VISIBLE);
                break;
            case OrderEntity.State.refunding_ticket:
                mBinding.iOrderHeader.tvOrderUseState.setText(R.string.the_refund);
                mBinding.iOrderHeader.tvOrderPrice.setText(getString(R.string.money_sign) + " " + mAmount);
                mBinding.iOrderHeader.tvOrderUseHint.setText(R.string.predict_24_hour_result);
                mBinding.iOrderHeader.llRefund.setVisibility(View.VISIBLE);
                switch (mOrderType) {
                    case 0:
                    case 1:
                        mBinding.iOrderHeader.tvOrderUseHint.setText(R.string.predict_24_hour_result);
                        break;
                }
                break;
            case OrderEntity.State.occupy_seat:
                mBinding.iOrderHeader.tvOrderUseState.setText(R.string.occupy_seat);
                mBinding.iOrderHeader.tvOrderPrice.setText(getString(R.string.money_sign) + " " + mAmount);
                mBinding.iOrderHeader.tvOrderUseHint.setText(R.string.occupy_seat_ing);
                mBinding.iOrderHeader.llStateContainer.setVisibility(View.GONE);
                break;
            case OrderEntity.State.make_ticket_failed_refunding:
                mBinding.iOrderHeader.tvOrderUseState.setText(R.string.make_ticket_failed_refunding);
                mBinding.iOrderHeader.tvOrderPrice.setText(getString(R.string.money_sign) + " " + mAmount);
                mBinding.iOrderHeader.tvOrderUseHint.setText("");
                mBinding.iOrderHeader.llStateContainer.setVisibility(View.GONE);
                break;
        }
    }

    private void initListener() {
        mBinding.iOrderHeader.llDetail.setOnClickListener(this);
        mBinding.iOrderHeader.tvTicketChanging.setOnClickListener(this);
        mBinding.iOrderHeader.tvCancelOrder.setOnClickListener(this);
        mBinding.iOrderHeader.tvToPay.setOnClickListener(this);
        mBinding.iOrderHeader.tvToPay1.setOnClickListener(this);
        mBinding.iOrderHeader.tvDelete.setOnClickListener(this);
        mBinding.iOrderHeader.tvRefund.setOnClickListener(this);
        mBinding.iOrderHeader.tvRefunding.setOnClickListener(this);
        mBinding.iOrderHeader.tvRefundDetail.setOnClickListener(this);
        mBinding.tvLookRefundRule.setOnClickListener(this);
    }

    public void setOrderState(int state) {
        mState = state;
    }

    public void deleteOrder() {
    }

    public void cancelOrder() {

    }

    public void createPayData() {

    }

    public void refundDetail() {

    }

    public void showOrderPriceDetail() {

    }

    public void refundOrder() {
//        EventBus.getDefault().post(new EventBusEntity(Const2.update_order));
    }

    public long getDiffTimestamp(String time1, String time2) {
        long t1 = DateUtils.date2Timestamp(time1, "yyyy-MM-dd HH:mm");
        long t2 = DateUtils.date2Timestamp(time2, "yyyy-MM-dd HH:mm");
        return (t2 - t1) / 1000 / 60;
    }

}
