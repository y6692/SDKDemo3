package com.centaurstech.sdk.activity.order;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;

import com.centaurstech.qiwu.QiWuAPI;
import com.centaurstech.qiwu.callback.APICallback;
import com.centaurstech.qiwu.common.CommentMethod;
import com.centaurstech.qiwu.common.Const;
import com.centaurstech.qiwu.entity.APIEntity;
import com.centaurstech.qiwu.entity.InsureEntity;
import com.centaurstech.qiwu.entity.PassengerEntity;
import com.centaurstech.qiwu.entity.TrainTicketEntity;
import com.centaurstech.qiwu.entity.TrainTimeTableEntity;
import com.centaurstech.qiwu.utils.DateUtils;
import com.centaurstech.qiwu.utils.UIUtils;
import com.centaurstech.qiwu.utils.VerifyUtils;
import com.centaurstech.sdk.R;
import com.centaurstech.sdk.activity.AddPassengerActivity;
import com.centaurstech.sdk.activity.BaseActivity;
import com.centaurstech.sdk.activity.PaymentActivity;
import com.centaurstech.sdk.activity.order.OrderListActivity;
import com.centaurstech.sdk.adapter.PassengerSlidingAdapter;
import com.centaurstech.sdk.adapter.TrainSeatTypeAdapter;
import com.centaurstech.sdk.adapter.TrainTimeTableAdapter;
import com.centaurstech.sdk.databinding.ActivityConfirmTrainOrderBinding;
import com.centaurstech.sdk.databinding.ItemTrainTimeBinding;
import com.centaurstech.sdk.databinding.LayoutDialogListBinding;
import com.centaurstech.sdk.listener.OnPassengerSlidingListener;
import com.centaurstech.sdk.utils.AnimatorUtils;
import com.centaurstech.sdk.utils.ConversionUtils;
import com.centaurstech.sdk.utils.ToastUtils;
import com.centaurstech.sdk.view.PopDialog;
import com.qiwu.ui.view.adapter.DataBindRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * @data 2019/12/26
 * @author: 樊德鹏
 * @description:
 */
public class ConfirmTrainOrderActivity extends BaseActivity implements View.OnClickListener {

    private TrainTicketEntity mTrainTicketEntity;
    private List<TrainTimeTableEntity> mTrainTimeTableEntity;
    private ActivityConfirmTrainOrderBinding mBinding;
    private String orderType = "";
    private PassengerSlidingAdapter mPassengerSlidingAdapter;

    private TrainSeatTypeAdapter mTrainSeatTypeAdapter;

    private int mPriceViewHeight = 0;

    private int mCount = 0;

    private String amount = "";

    private String mInsurancePrice;

    private boolean selectInsurance = false;

    private boolean mShowTicketDetail = false;

    private String mPubPid;

    private String mInsureClause;

    private static final int REQUEST_CODE_TRAVELER = 35;

    private ArrayList<PassengerEntity> mPassengerEntitys = new ArrayList<>();

    private DataBindRecyclerViewAdapter<TrainTimeTableEntity, ItemTrainTimeBinding> mTrainTimeTableAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_confirm_train_order);
        mTrainTicketEntity = (TrainTicketEntity) getIntent().getSerializableExtra("train");
        initView();
    }

    private void initView() {
        mBinding.iHeadLayout.rlRoot.setBackgroundColor(UIUtils.getColor(R.color.colorWhite));
        mBinding.iHeadLayout.tvTitle.setText(UIUtils.getString(R.string.confirm_order));
        mBinding.iHeadLayout.tvTitle.setTextColor(UIUtils.getColor(R.color.colorFF000000));
        mBinding.iHeadLayout.btnNavBack.setImageResource(R.mipmap.nav_btn_back);
        mBinding.ivAdd.setOnClickListener(this);
        mBinding.iTicketInfo.tvTrainTimeTable.setOnClickListener(this);
        mBinding.iOrderFooter.llShowTicketDetail.setOnClickListener(this);
        mBinding.iOrderFooter.tvSubmitOrder.setOnClickListener(this);
        mBinding.flPriceDetailBG.setOnClickListener(this);
        mBinding.iInsurance.ivRadio.setOnClickListener(this);
        getInsurance();
        mBinding.rvPassengers.setLayoutManager(new LinearLayoutManager(this) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        mPassengerSlidingAdapter = new PassengerSlidingAdapter(this);
        mBinding.rvPassengers.setAdapter(mPassengerSlidingAdapter);
        mBinding.iOrderPriceDetail.iAviationAccidentInsurance.tvTitle.setText(R.string.traffic_accident_insurance);
        mBinding.iContactName.tvTitle.setText(R.string.contact);
        mBinding.iContactName.etText.setHint(R.string.please_input_contact_name);
        mBinding.iContactPhone.tvTitle.setText(R.string.phone_number);
        mBinding.iContactPhone.ivRightImage.setVisibility(View.INVISIBLE);
        mBinding.iContactPhone.etText.setHint(R.string.receive_order_feedback);
        mBinding.iContactPhone.etText.setPadding(5, 0, 8, 0);
        mBinding.iInsurance.tvTitle.setText(R.string.traffic_accident_insurance);


        mTrainSeatTypeAdapter = new TrainSeatTypeAdapter(this);
        mBinding.rvSeatType.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mTrainSeatTypeAdapter.setSeatClass(mTrainTicketEntity.getTrainTicket().getSeatClass());
        mTrainSeatTypeAdapter.setTicketPrice(mTrainTicketEntity.getTrainTicket().getTicketPrice());
        mTrainSeatTypeAdapter.setSeatName(mTrainTicketEntity.getTrainTicket().getTicketType());
        mBinding.rvSeatType.setAdapter(mTrainSeatTypeAdapter);
//        Collections.reverse(mTrainTicketEntity.getAllSeats());
        mTrainSeatTypeAdapter.setData(mTrainTicketEntity.getAllSeats());

        mBinding.flPriceDetail.measure(0, 0);
        mPriceViewHeight = mBinding.flPriceDetail.getMeasuredHeight();
        AnimatorUtils.setMargin(mBinding.flPriceDetail, 0, -mPriceViewHeight, 4);
        mBinding.flPriceDetailBG.setVisibility(View.GONE);

        mPassengerSlidingAdapter.setOnPassengerSlidingListener(new OnPassengerSlidingListener() {
            @Override
            public void onOperation(int type, int position) {
                mPassengerEntitys.remove(position);
                setPersonCount(mPassengerSlidingAdapter.getData().size());
            }
        });

        mBinding.iHeadLayout.btnNavBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        initData();
    }

    private void getInsurance() {
        QiWuAPI.train.getInsurance(new APICallback<APIEntity<ArrayList<InsureEntity>>>() {
            @Override
            public void onSuccess(APIEntity<ArrayList<InsureEntity>> response) {
                mInsurancePrice = response.getData().get(0).getInsureUnitPrice();
                mPubPid = response.getData().get(0).getPubPid();
                mInsureClause = response.getData().get(0).getInsureClause();
                mBinding.iInsurance.tvText.setText(mInsurancePrice + "x0" + getString(R.string.person));
            }
        });
    }

    public void setPersonCount() {
        setPersonCount(mPassengerSlidingAdapter.getData().size());
    }

    /***
     * 设置乘车人数
     * @param count
     */
    public void setPersonCount(int count) {
        try {
            mCount = count;
            amount = ConversionUtils.getMoney((Double.valueOf(mTrainSeatTypeAdapter.getTicketPrice()) * count
                    + Double.valueOf(selectInsurance ? mInsurancePrice : "0") * count));
            mBinding.iInsurance.tvText.setText(mInsurancePrice + "x" + count + getString(R.string.person));
            mBinding.iOrderFooter.tvOrderAmount.setText(getString(R.string.money_sign) + amount);
            mBinding.iOrderFooter.tvPersonCount.setText("（" + count + getString(R.string.person) + "）");
            mBinding.iOrderPriceDetail.iTicketPrice.tvTitle.setText(mTrainSeatTypeAdapter.getSeatName() + " x" + count);
            mBinding.iOrderPriceDetail.iTicketPrice.tvPrice.setText(getString(R.string.money_sign) + ConversionUtils.getMoney(Double.valueOf(mTrainSeatTypeAdapter.getTicketPrice()) * count));
            mBinding.iOrderPriceDetail.llAviationAccidentInsurance.setVisibility(selectInsurance ? View.VISIBLE : View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /***
     * 显示底部详细信息
     */
    private void showOrHideTicketDetailView() {
        if (mShowTicketDetail) {
            mBinding.iOrderFooter.ivShowTicketPop.setImageResource(R.mipmap.btn_pop2);
            AnimatorUtils.hide(mBinding.flPriceDetailBG, 200);
            AnimatorUtils.setMargin(mBinding.flPriceDetail, 200, -mPriceViewHeight, 4);
        } else {
            mBinding.iOrderFooter.ivShowTicketPop.setImageResource(R.mipmap.btn_pop_pre);
            AnimatorUtils.show(mBinding.flPriceDetailBG, 200);
            AnimatorUtils.setMargin(mBinding.flPriceDetail, 200, 0, 4);
        }
        mShowTicketDetail = !mShowTicketDetail;
    }

    private void showTrainTimeTableDetail() {
        try {
            if (mTrainTimeTableEntity == null) {
                QiWuAPI.train.getTimeTable(mTrainTicketEntity.getStation(), mTrainTicketEntity.getEndstation(),
                        DateUtils.timestamp2String(CommentMethod.getTimestamp(mTrainTicketEntity.getDeparturetime()), "yyyy-MM-dd"),
                        mTrainTicketEntity.getTrainno(), new APICallback<APIEntity<ArrayList<TrainTimeTableEntity>>>() {
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
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ivAdd:
                launchActivity(AddPassengerActivity.class, REQUEST_CODE_TRAVELER);
                break;
            case R.id.tvTrainTimeTable:
                showTrainTimeTableDetail();
                break;
            case R.id.llShowTicketDetail:
            case R.id.flPriceDetailBG:
                showOrHideTicketDetailView();
                break;
            case R.id.tvSubmitOrder:
                showCreatingOrderDialog();
                break;
            case R.id.ivRadio:
                selectInsurance = !selectInsurance;
                mBinding.iInsurance.ivRadio.setImageResource(selectInsurance ? R.mipmap.btn_radio : R.mipmap.btn_radio_pre);
                setPersonCount(mPassengerSlidingAdapter.getData().size());
                break;
        }
    }

    private void showCreatingOrderDialog() {
        if (mPassengerSlidingAdapter.getData().size() <= 0) {
            ToastUtils.show(R.string.please_choose_train_passenger);
            return;
        }

        final String name = mBinding.iContactName.etText.getText().toString().trim();
        final String phone = mBinding.iContactPhone.etText.getText().toString().trim();
        if (TextUtils.isEmpty(name)) {
            ToastUtils.show(R.string.please_input_contact_name);
            return;
        }
        if (TextUtils.isEmpty(phone)) {
            ToastUtils.show(R.string.please_input_right_phone);
            return;
        }

        if (!VerifyUtils.phoneNumber(phone)) {
            ToastUtils.show(R.string.please_input_right_phone);
            return;
        }
        submitOrder(name, phone);
    }

    /***
     * 确定下单
     * @param name
     * @param phone
     */
    private void submitOrder(String name, String phone) {
        showLoadingDialog();
        mTrainTicketEntity.setSeatClass(mTrainSeatTypeAdapter.getSeatClass());
        mTrainTicketEntity.setSeatPrice(mTrainSeatTypeAdapter.getTicketPrice());
        QiWuAPI.train.booking(mTrainTicketEntity, name, phone, mPassengerEntitys, new APICallback<APIEntity<String>>() {
            @Override
            public void onSuccess(APIEntity<String> response) {
                hideLoadingDialog();
                if (response.isSuccess()) {
                    launchActivity(OrderListActivity.class);
                    launchActivity(TrainOrderDetailActivity.class, new IntentExpand() {
                        @Override
                        public void extraValue(Intent intent) {
                            intent.putExtra(Const.Intent.DATA, response.getData());
//                        intent.putExtra(Const.Intent.TYPE, 1);
                        }
                    });
                    finish();
                } else {
                    ToastUtils.show(response.getMsg());
                }
            }
        });
    }

    private void initData() {
        if (mTrainTicketEntity != null) {
            TrainTicketEntity ticketEntity = mTrainTicketEntity;
            if (ConversionUtils.getScreenWidthLevel() == ConversionUtils.ScreenWidthLevel.MIX) {
                mBinding.iTicketInfo.tvOriginTime.setTextSize(16);
                mBinding.iTicketInfo.tvDestinationTime.setTextSize(16);
            }
            mBinding.iTicketInfo.tvDate.setText(CommentMethod.getTime(ticketEntity.getDeparturetime(), "M月d日"));
            mBinding.iTicketInfo.tvRoute.setText(ticketEntity.getStart_city() + "-" + ticketEntity.getEnd_city());
            mBinding.iTicketInfo.tvOriginAisle.setText(ticketEntity.getStation());
            mBinding.iTicketInfo.tvOriginTime.setText(CommentMethod.getTime(ticketEntity.getDeparturetime(), "H:mm"));
            mBinding.iTicketInfo.tvDestinationAisle.setText(ticketEntity.getEndstation());
            mBinding.iTicketInfo.tvDestinationTime.setText(CommentMethod.getTime(ticketEntity.getArrivaltime(), "H:mm"));
            mBinding.iTicketInfo.tvTrainNumber.setText(ticketEntity.getType() + ticketEntity.getTrainno());
            mBinding.iInsurance.ivDetail.setVisibility(View.VISIBLE);
            mBinding.iTicketInfo.tvTrainDuration.setText(CommentMethod.getDuration(ticketEntity.getCosttime()));
            mBinding.tvSeatType.setText(ticketEntity.getTrainTicket().getTicketType());
            mBinding.tvTicketPrice.setText(getString(R.string.money_sign) + ticketEntity.getTrainTicket().getTicketPrice());
            long departureTime = CommentMethod.getTimestamp(ticketEntity.getDeparturetime());
            long arrivalTime = CommentMethod.getTimestamp(ticketEntity.getArrivaltime());
            int diffDays = DateUtils.differentDays(departureTime, arrivalTime);
            if (diffDays > 0) {
                mBinding.iTicketInfo.tvDiffDays.setText("+" + diffDays);
                mBinding.iTicketInfo.tvDiffDays.setVisibility(View.VISIBLE);
            } else {
                mBinding.iTicketInfo.tvDiffDays.setVisibility(View.GONE);
            }
            mBinding.iOrderFooter.tvOrderAmount.setText(getString(R.string.money_sign) + " ----");
            mBinding.iOrderFooter.tvPersonCount.setText("（0" + getString(R.string.person) + "）");
        }
        setPersonCount(0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_TRAVELER:
                    PassengerEntity entity = (PassengerEntity) data.getSerializableExtra(Const.Intent.DATA);
                    if (entity == null) return;
                    mPassengerEntitys.add(entity);
                    if (mPassengerEntitys != null) {
                        mPassengerSlidingAdapter.setData(mPassengerEntitys);
                        setPersonCount(mPassengerEntitys.size());
                    }
                    break;
            }
        }
    }
}