package com.centaurstech.sdk.activity.order;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import com.centaurstech.qiwu.QiWuAPI;
import com.centaurstech.qiwu.callback.APICallback;
import com.centaurstech.qiwu.common.CommentMethod;
import com.centaurstech.qiwu.common.Const;
import com.centaurstech.qiwu.entity.APIEntity;
import com.centaurstech.qiwu.entity.EventBusEntity;
import com.centaurstech.qiwu.entity.FlightRefundReasonEntity;
import com.centaurstech.qiwu.entity.OrderEntity;
import com.centaurstech.qiwu.entity.OrderFlightEntity;
import com.centaurstech.qiwu.entity.TrainPassengerEntity;
import com.centaurstech.qiwu.utils.DateUtils;
import com.centaurstech.qiwu.utils.UIUtils;
import com.centaurstech.sdk.R;
import com.centaurstech.sdk.activity.PaymentActivity;
import com.centaurstech.sdk.activity.order.OrderDetailActivity;
import com.centaurstech.sdk.common.Const2;
import com.centaurstech.sdk.databinding.LayoutTicketInfoBinding;
import com.centaurstech.sdk.utils.ToastUtils;
import com.centaurstech.sdk.view.PopDialog;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Leon(黄长亮)
 * @describe TODO
 * @date 2019/6/26
 */
public class FlightOrderDetailActivity extends OrderDetailActivity {


    private static final int PAYMENT_RESULT_CODE = 30;

    @Override
    public boolean useEventBus() {
        return true;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
    }

    private void initData() {
        showLoadingDialog();
        QiWuAPI.flight.getOrder(getIntent().getStringExtra(Const.Intent.DATA), new APICallback<APIEntity<ArrayList<OrderFlightEntity>>>() {
            @Override
            public void onSuccess(APIEntity<ArrayList<OrderFlightEntity>> arrayListAPIEntity) {
                hideLoadingDialog();
                mOrderFlightEntities = arrayListAPIEntity.getData();
                mOrderFlightEntity = mOrderFlightEntities.get(0);
                mState = Integer.parseInt(mOrderFlightEntity.getState());
                mAmount = mOrderFlightEntity.getAmount();
                mCountdown = mOrderFlightEntity.getCountdown();
                initView();
            }
        });
    }

    @Override
    public void deleteOrder() {
        PopDialog.create(this)
                .setTitle(getString(R.string.delete_order))
                .setContent(R.string.confirm_delete_order)
                .setOnViewClickListener(new PopDialog.OnViewClickListener() {
                    @Override
                    public void onClick(View view, boolean isConfirm) {
                        if (isConfirm) {
                            QiWuAPI.flight.delete(getIntent().getStringExtra(Const.Intent.DATA), new APICallback<APIEntity<String>>() {
                                @Override
                                public void onSuccess(APIEntity<String> stringAPIEntity) {
                                    if (stringAPIEntity.isSuccess()) {
                                        UIUtils.showToast("删除成功");
                                    }
                                }
                            });
                        }
                    }
                });
    }

    @Override
    public void cancelOrder() {
        PopDialog.create(this)
                .setTitle(getString(R.string.cancel_order))
                .setContent(R.string.confirm_cancel_order)
                .setOnViewClickListener(new PopDialog.OnViewClickListener() {
                    @Override
                    public void onClick(View view, boolean isConfirm) {
                        if (isConfirm) {
                            QiWuAPI.flight.cancel(getIntent().getStringExtra(Const.Intent.DATA), new APICallback<APIEntity<String>>() {
                                @Override
                                public void onSuccess(APIEntity<String> stringAPIEntity) {
                                    if (stringAPIEntity.isSuccess()) {
                                        setOrderState(OrderEntity.State.canceled);
                                        mOrderFlightEntity.setState(String.valueOf(OrderEntity.State.canceled));
                                        UIUtils.showToast("取消成功");
                                        EventBus.getDefault().post(new EventBusEntity(Const2.update_order));
                                        setState();
                                    }
                                }
                            });
                        }
                    }
                });
    }

    @Override
    public void refundOrder() {
        showLoadingDialog();
        QiWuAPI.flight.getRefundReason(getIntent().getStringExtra(Const.Intent.DATA), new APICallback<APIEntity<List<FlightRefundReasonEntity>>>() {
            @Override
            public void onSuccess(APIEntity<List<FlightRefundReasonEntity>> response) {
                if (response.isSuccess()) {
                    FlightRefundReasonEntity flightRefund = response.getData().get(0);
                    QiWuAPI.flight.refund(getIntent().getStringExtra(Const.Intent.DATA),
                            flightRefund.getCode(),
                            flightRefund.getMsg(),
                            flightRefund.getRefundPassengerPriceInfoList().get(0), new APICallback<APIEntity<String>>() {
                                @Override
                                public void onSuccess(APIEntity<String> response) {
                                    hideLoadingDialog();
                                    if (response.isSuccess()) {
                                        setOrderState(OrderEntity.State.canceled);
                                        mOrderFlightEntity.setState(String.valueOf(OrderEntity.State.canceled));
                                        setState();
                                        UIUtils.showToast("退订成功");
                                        EventBus.getDefault().post(new EventBusEntity(Const2.update_order));
                                    } else {
                                        ToastUtils.show(response.getMsg());
                                    }
                                }
                            });
                } else {
                    ToastUtils.show(response.getMsg());
                }
            }
        });
    }

    @Override
    public void createPayData() {
        launchActivity(PaymentActivity.class, new IntentExpand() {
            @Override
            public void extraValue(Intent intent) {
                intent.putExtra(Const.Intent.DATA, getIntent().getStringExtra(Const.Intent.DATA));
                intent.putExtra(Const.Intent.TYPE, 2);
            }
        }, PAYMENT_RESULT_CODE);
    }

    private void initView() {
        mBinding.tvMakeInvoice.setVisibility(View.GONE);
        switch (mState) {
            case OrderEntity.State.to_travel:
            case OrderEntity.State.traveled:
            case OrderEntity.State.refunded:
            case OrderEntity.State.refunding:
                mBinding.tvMakeInvoice.setVisibility(View.VISIBLE);
                break;
        }

        mBinding.tvOrderId.setText(getString(R.string.order_id) + ": " + mOrderFlightEntity.getOrderId());
        mBinding.tvPlaceOrderTime.setText(getString(R.string.buy_order_time) + ": " + mOrderFlightEntity.getCreateTime().replace("-", "."));

        mBinding.llTicketInfo.removeAllViews();
        View view = View.inflate(this, R.layout.layout_ticket_info, null);
        LayoutTicketInfoBinding infoBinding = DataBindingUtil.bind(view);
        mBinding.llTicketInfo.addView(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        infoBinding.tvDate.setText(DateUtils.date2Date(mOrderFlightEntity.getDeptDate(), "yyyy-MM-dd", "M月d日"));
        infoBinding.tvRoute.setText(mOrderFlightEntity.getDeptCity() +
                "-" + mOrderFlightEntity.getArrCity());
        infoBinding.tvCabinInfo.setText(mOrderFlightEntity.getCabinType());
        infoBinding.tvOriginAisle.setText(mOrderFlightEntity.getDeptAirport() + mOrderFlightEntity.getDeptTerminal());
        infoBinding.tvOriginTime.setText(mOrderFlightEntity.getDeptTime());
        infoBinding.tvPassAddress.setText((TextUtils.isEmpty(mOrderFlightEntity.getStopCity()) ? "" : UIUtils.getString(R.string.transit) + " " + mOrderFlightEntity.getStopCity()));
        infoBinding.tvDestinationAisle.setText(mOrderFlightEntity.getArrAirport() + mOrderFlightEntity.getArrTerminal());
        infoBinding.tvDestinationTime.setText(mOrderFlightEntity.getArrTime());
        infoBinding.tvVehicleType.setText((TextUtils.isEmpty(mOrderFlightEntity.getFlightRealCom())
                || mOrderFlightEntity.getFlightRealCom().equals("null") ? "" : mOrderFlightEntity.getFlightRealCom()) + mOrderFlightEntity.getFlightNum());
        infoBinding.tvMiddleText.setText("");
        mBinding.iOrderHeader.ivPop.setVisibility(View.VISIBLE);
        long duration = getDiffTimestamp(mOrderFlightEntity.getDeptDate() + " " + mOrderFlightEntity.getDeptTime(), mOrderFlightEntity.getArrDate() + " " + mOrderFlightEntity.getArrTime());
        infoBinding.tvDuration.setText(getString(R.string.voyage) + CommentMethod.getDuration(duration));

        mBinding.tvContactNameAndPhone.setText(mOrderFlightEntity.getContactName() + " " + getString(R.string.phone_number) + "：" + mOrderFlightEntity.getContactPhone());

        setState();
        List<TrainPassengerEntity> passengers = new ArrayList<>();
        for (OrderFlightEntity.FlightOrderPassengersBean passengersBean : mOrderFlightEntity.getFlightOrderPassengers()) {
            TrainPassengerEntity passengerEntity = new TrainPassengerEntity();
            passengerEntity.setName(passengersBean.getName());
            passengerEntity.setId_no(passengersBean.getCard_no());
            passengerEntity.setId_type(passengersBean.getCard_type());
            passengerEntity.setId_type_name(Const2.DataArray.Certificates[passengersBean.getCard_type() - 1]);
            passengerEntity.setOpen(false);
            passengers.add(passengerEntity);
        }
        mPassengerAdapter.setData(passengers);
        infoBinding.vLine.setVisibility(View.GONE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PAYMENT_RESULT_CODE:
                    initData();
                    break;
            }
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

    @Override
    public void showTicketChangingDialog() {
        PopDialog.create(this)
                .setTitle(getString(R.string.changing_remind))
                .setContent(R.string.please_call_customer_service)
                .setHideRightButton()
                .setRightImage(R.mipmap.endorse_icon_phone, UIUtils.dip2Px(15), UIUtils.dip2Px(21))
                .setOnViewClickListener(new PopDialog.OnViewClickListener() {
                    @Override
                    public void onClick(View view, boolean isConfirm) {

                    }
                });
    }
}
