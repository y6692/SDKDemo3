package com.centaurstech.sdk.activity.order;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;

import com.centaurstech.qiwu.common.CommentMethod;
import com.centaurstech.qiwu.common.Const;
import com.centaurstech.qiwu.db.MsgDBManager;
import com.centaurstech.qiwu.entity.FlightTicketEntity;
import com.centaurstech.qiwu.entity.FlightTicketInfoEntity;
import com.centaurstech.qiwu.utils.DateUtils;
import com.centaurstech.sdk.R;
import com.centaurstech.sdk.activity.BaseActivity;
import com.centaurstech.sdk.adapter.OrderMoreFlightAdapter;
import com.centaurstech.sdk.databinding.ActivityOrderMoreFlightBinding;
import com.centaurstech.sdk.view.PopDialog;

public class OrderMoreFlightActivity extends BaseActivity {

    private ActivityOrderMoreFlightBinding mBinding;

    private OrderMoreFlightAdapter mAdapter;

    private FlightTicketInfoEntity mEntity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_order_more_flight);
        mEntity = (FlightTicketInfoEntity) getIntent().getSerializableExtra(Const.Intent.DATA);
        mAdapter = new OrderMoreFlightAdapter(this);
        mBinding.rvList.setLayoutManager(new LinearLayoutManager(this));
        mBinding.rvList.setAdapter(mAdapter);

        if (mEntity != null && mEntity.getFlightTickets() != null && mEntity.getFlightTickets().size() > 0) {
            FlightTicketEntity ticketEntity = mEntity.getFlightTickets().get(0);
            mBinding.tvTitle.setText(CommentMethod.getTime(ticketEntity.getDeparture_time().get(0), "M月d日")
                    + " " + DateUtils.getWeekOfDate(CommentMethod.getTimestamp(ticketEntity.getDeparture_time().get(0)))
                    + "   " + ticketEntity.getDeparture_city() + "-" + ticketEntity.getArrival_city());
            mAdapter.setFlightTicketEntitys(mEntity.getFlightTickets());
        }

        mBinding.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mAdapter.setOnItemListener(new OrderMoreFlightAdapter.OnItemListener() {
            @Override
            public void onItemCLick(FlightTicketEntity itemData, int position) {
                mEntity.setSelectPosition(position);
                if (itemData.isReturn_trip()) { //是往返
                    if (!TextUtils.isEmpty(mEntity.getRelevanceId())) {
                        launchActivity(ConfirmFlightOrderActivity.class, new BaseActivity.IntentExpand() {
                            @Override
                            public void extraValue(Intent intent) {
                                if (mEntity.getStage().equals("FIRST")) {
                                    intent.putExtra(Const.Intent.DATA, mEntity.getFlightTickets().get(position));
                                    intent.putExtra(Const.Intent.DATA2, MsgDBManager.getInstance().get(mEntity.getRelevanceId()));
                                } else {
                                    intent.putExtra(Const.Intent.DATA2, mEntity.getFlightTickets().get(position));
                                    if (!TextUtils.isEmpty(mEntity.getRelevanceId())) {
                                        intent.putExtra(Const.Intent.DATA, MsgDBManager.getInstance().get(mEntity.getRelevanceId()));
                                    }
                                }
                            }
                        });
                    }
                } else { //是单程
                    launchActivity(ConfirmFlightOrderActivity.class, new BaseActivity.IntentExpand() {
                        @Override
                        public void extraValue(Intent intent) {
                            intent.putExtra(Const.Intent.DATA, mEntity.getFlightTickets().get(position));
                        }
                    });
                }
            }
        });
    }
}
