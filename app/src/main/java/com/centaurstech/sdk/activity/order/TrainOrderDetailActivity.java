package com.centaurstech.sdk.activity.order;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.centaurstech.qiwu.QiWuAPI;
import com.centaurstech.qiwu.callback.APICallback;
import com.centaurstech.qiwu.common.CommentMethod;
import com.centaurstech.qiwu.common.Const;
import com.centaurstech.qiwu.entity.APIEntity;
import com.centaurstech.qiwu.entity.EventBusEntity;
import com.centaurstech.qiwu.entity.OrderEntity;
import com.centaurstech.qiwu.entity.OrderTrainEntity;
import com.centaurstech.qiwu.entity.TrainPassengerEntity;
import com.centaurstech.qiwu.entity.TrainTimeTableEntity;
import com.centaurstech.qiwu.utils.DateUtils;
import com.centaurstech.qiwu.utils.UIUtils;
import com.centaurstech.sdk.R;
import com.centaurstech.sdk.activity.PaymentActivity;
import com.centaurstech.sdk.activity.order.OrderDetailActivity;
import com.centaurstech.sdk.adapter.TrainTimeTableAdapter;
import com.centaurstech.sdk.common.Const2;
import com.centaurstech.sdk.databinding.ItemTrainTimeBinding;
import com.centaurstech.sdk.databinding.LayoutDialogListBinding;
import com.centaurstech.sdk.databinding.LayoutTrainTicketInfoBinding;
import com.centaurstech.sdk.view.PopDialog;
import com.qiwu.ui.view.adapter.DataBindRecyclerViewAdapter;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * @data 2019/12/31
 * @author: 樊德鹏
 * @description:
 */
public class TrainOrderDetailActivity extends OrderDetailActivity {

    private List<TrainTimeTableEntity> mTrainTimeTableEntity;

    private static final int PAYMENT_RESULT_CODE = 30;

    private DataBindRecyclerViewAdapter<TrainTimeTableEntity, ItemTrainTimeBinding> mTrainTimeTableAdapter;

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
        QiWuAPI.train.getOrder(getIntent().getStringExtra(Const.Intent.DATA), new APICallback<APIEntity<OrderTrainEntity>>() {
            @Override
            public void onSuccess(APIEntity<OrderTrainEntity> orderTrainEntity) {
                mOrderTrainEntity = orderTrainEntity.getData();
                initView();
            }
        });
    }

    private void initView() {
        if (mOrderTrainEntity == null) return;
        mAmount = mOrderTrainEntity.getAmount();
        mState = Integer.parseInt(mOrderTrainEntity.getState());
        mCountdown = mOrderTrainEntity.getCountdown();

        setState();

        switch (mState) {
            case OrderEntity.State.deal_close:
            case OrderEntity.State.make_ticket_failed:
            case OrderEntity.State.make_ticket_failed_refunding:
                if (!TextUtils.isEmpty(mOrderTrainEntity.getFailureReason())) {
                    mBinding.iOrderHeader.tvOrderUseHint.setText(mOrderTrainEntity.getFailureReason());
                }
                break;
            case OrderEntity.State.occupy_seat:
                mBinding.iOrderHeader.tvOrderPrice.setVisibility(View.GONE);
                break;
        }

        List<TrainPassengerEntity> passengers = new ArrayList<>();
        for (OrderTrainEntity.TicketBean ticketsBean : mOrderTrainEntity.getTickets()) {
            TrainPassengerEntity passengerEntity = new TrainPassengerEntity();
            passengerEntity.setId_no(ticketsBean.getPassenger().getId_no());
            passengerEntity.setId_type_name(ticketsBean.getPassenger().getId_type_name());
            passengerEntity.setId_type(Integer.parseInt(ticketsBean.getPassenger().getId_type()));
            passengerEntity.setName(ticketsBean.getPassenger().getName());
            passengerEntity.setSeat_no(ticketsBean.getSeat_no());
            passengerEntity.setType(1);
            passengerEntity.setState(mState);
            passengerEntity.setOpen(false);
            passengers.add(passengerEntity);
        }

        mPassengerAdapter.setData(passengers);

        mBinding.tvContactNameAndPhone.setText(mOrderTrainEntity.getContactName() + " " + getString(R.string.phone_number) + "：" + mOrderTrainEntity.getContactPhone());

        mBinding.llTicketInfo.removeAllViews();
        View view = View.inflate(this, R.layout.layout_train_ticket_info, null);
        LayoutTrainTicketInfoBinding infoBinding = DataBindingUtil.bind(view);
        mBinding.llTicketInfo.addView(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        mBinding.tvOrderId.setText(getString(R.string.order_id) + ": " + mOrderTrainEntity.getOrderId());
        mBinding.tvPlaceOrderTime.setText(getString(R.string.buy_order_time) + ": " + mOrderTrainEntity.getCreateAt().replace("-", "."));

        infoBinding.tvDate.setText(DateUtils.date2Date(mOrderTrainEntity.getDepartureAt(), "yyyy-MM-dd HH:mm", "M月d日"));
        infoBinding.tvRoute.setText(mOrderTrainEntity.getDepartureCityName() + "-" + mOrderTrainEntity.getArrivalCityName());
        infoBinding.tvOriginAisle.setText(mOrderTrainEntity.getFromStation());
        infoBinding.tvOriginTime.setText(DateUtils.date2Date(mOrderTrainEntity.getDepartureAt(), "yyyy-MM-dd HH:mm", "HH:mm"));
        infoBinding.tvDestinationAisle.setText(mOrderTrainEntity.getToStation());
        infoBinding.tvDestinationTime.setText(DateUtils.date2Date(mOrderTrainEntity.getArrivalAt(), "yyyy-MM-dd HH:mm", "HH:mm"));
        infoBinding.tvTrainNumber.setText(mOrderTrainEntity.getTrainType() + mOrderTrainEntity.getTrainNo());
        infoBinding.tvTrainTimeTable.setOnClickListener(this);
        mBinding.iOrderHeader.ivPop.setVisibility(View.GONE);
        mBinding.iOrderHeader.tvPriceDetail.setVisibility(View.GONE);

        infoBinding.tvTrainDuration.setText(CommentMethod.getDuration(Long.parseLong(mOrderTrainEntity.getRunTimeSpan())));
        long departureTime = DateUtils.date2Timestamp(mOrderTrainEntity.getDepartureAt(), "yyyy-MM-dd HH:mm");
        long arrivalTime = DateUtils.date2Timestamp(mOrderTrainEntity.getArrivalAt(), "yyyy-MM-dd HH:mm");
        int diffDays = DateUtils.differentDays(departureTime, arrivalTime);
        if (diffDays > 0) {
            infoBinding.tvDiffDays.setText("+" + diffDays);
            infoBinding.tvDiffDays.setVisibility(View.VISIBLE);
        } else {
            infoBinding.tvDiffDays.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.tvTrainTimeTable:
                showTrainTimeTableDetail();
                break;
        }
    }

    private void showTrainTimeTableDetail() {
        if (mTrainTimeTableEntity == null) {
            QiWuAPI.train.getTimeTable(mOrderTrainEntity.getFromStation(), mOrderTrainEntity.getToStation(),
                    DateUtils.timestamp2String(CommentMethod.getTimestamp(mOrderTrainEntity.getDepartureAt()), "yyyy-MM-dd"),
                    mOrderTrainEntity.getTrainNo(), new APICallback<APIEntity<ArrayList<TrainTimeTableEntity>>>() {
                        @Override
                        public void onSuccess(APIEntity<ArrayList<TrainTimeTableEntity>> response) {
                            mTrainTimeTableEntity = response.getData();
                            mTrainTimeTableEntity.add(0, new TrainTimeTableEntity());
                            mTrainTimeTableAdapter.setData(mTrainTimeTableEntity);
                        }
                    });
        }
        View view = View.inflate(this, R.layout.layout_dialog_list, null);
        LayoutDialogListBinding listBinding = DataBindingUtil.bind(view);
        listBinding.tvTitle.setText(R.string.train_time_table);
        listBinding.tvTitle.setTextSize(18);
        LinearLayout.LayoutParams tlp = (LinearLayout.LayoutParams) listBinding.tvTitle.getLayoutParams();
        tlp.gravity = Gravity.CENTER;
        listBinding.tvTitle.requestLayout();
        LinearLayout.LayoutParams rlp = (LinearLayout.LayoutParams) listBinding.rvList.getLayoutParams();
        rlp.height = UIUtils.dip2Px(330);
        listBinding.rvList.requestLayout();
        LinearLayout.LayoutParams llp = (LinearLayout.LayoutParams) listBinding.tvTitle.getLayoutParams();
        llp.gravity = Gravity.CENTER;
        listBinding.tvTitle.requestLayout();
        listBinding.rvList.setLayoutManager(new LinearLayoutManager(this));
        mTrainTimeTableAdapter = new TrainTimeTableAdapter(this);
        listBinding.rvList.setAdapter(mTrainTimeTableAdapter);
        if (mTrainTimeTableEntity != null) {
            mTrainTimeTableAdapter.setData(mTrainTimeTableEntity);
        }
        PopDialog.create(this)
                .setContent(view)
                .setHideAllButton().show();
    }

    @Override
    public void deleteOrder() {
        QiWuAPI.train.delete(getIntent().getStringExtra(Const.Intent.DATA), new APICallback<APIEntity<String>>() {
            @Override
            public void onSuccess(APIEntity<String> stringAPIEntity) {
                if (stringAPIEntity.isSuccess()) {
                    UIUtils.showToast("删除成功");
                    finish();
                }
            }
        });
    }

    @Override
    public void cancelOrder() {
        QiWuAPI.train.cancel(getIntent().getStringExtra(Const.Intent.DATA), new APICallback<APIEntity<String>>() {
            @Override
            public void onSuccess(APIEntity<String> stringAPIEntity) {
                if (stringAPIEntity.isSuccess()) {
                    setOrderState(OrderEntity.State.canceled);
                    mOrderTrainEntity.setState(String.valueOf(OrderEntity.State.canceled));
                    UIUtils.showToast("取消成功");
                    setState();
                    EventBus.getDefault().post(new EventBusEntity(Const2.update_order));
                }
            }
        });
    }

    @Override
    public void refundOrder() {
        QiWuAPI.train.refund(getIntent().getStringExtra(Const.Intent.DATA), String.valueOf(mOrderTrainEntity.getTickets().get(0).getPassenger().getId()), new APICallback<APIEntity<String>>() {
            @Override
            public void onSuccess(APIEntity<String> stringAPIEntity) {
                if (stringAPIEntity.isSuccess()) {
                    setOrderState(OrderEntity.State.canceled);
                    mOrderTrainEntity.setState(String.valueOf(OrderEntity.State.canceled));
                    UIUtils.showToast("退订成功");
                    setState();
                    EventBus.getDefault().post(new EventBusEntity(Const2.update_order));
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
                intent.putExtra(Const.Intent.TYPE, 1);
            }
        });
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
}
