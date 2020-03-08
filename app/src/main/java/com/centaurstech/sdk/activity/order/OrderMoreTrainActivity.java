package com.centaurstech.sdk.activity.order;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.centaurstech.qiwu.common.CommentMethod;
import com.centaurstech.qiwu.common.Const;
import com.centaurstech.qiwu.entity.TrainTicketEntity;
import com.centaurstech.qiwu.utils.DateUtils;
import com.centaurstech.sdk.R;
import com.centaurstech.sdk.activity.BaseActivity;
import com.centaurstech.sdk.adapter.OrderMoreTrainAdapter;
import com.centaurstech.sdk.databinding.ActivityOrderMoreTrainBinding;

import java.util.ArrayList;

public class OrderMoreTrainActivity extends BaseActivity {

    private ActivityOrderMoreTrainBinding mBinding;

    private OrderMoreTrainAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_order_more_train);
        ArrayList<TrainTicketEntity> data = (ArrayList<TrainTicketEntity>) getIntent().getSerializableExtra(Const.Intent.DATA);
        mAdapter = new OrderMoreTrainAdapter(this);
        mBinding.rvList.setLayoutManager(new LinearLayoutManager(this));
        mBinding.rvList.setAdapter(mAdapter);

        if (data != null && data.size() > 0) {
            TrainTicketEntity ticketEntity = data.get(0);
            mBinding.tvTitle.setText(CommentMethod.getTime(ticketEntity.getDeparturetime(), "M月d日")
                    + " " + DateUtils.getWeekOfDate(CommentMethod.getTimestamp(ticketEntity.getDeparturetime()))
                    + "   " + ticketEntity.getStart_city() + "-" + ticketEntity.getEnd_city());
            mAdapter.setrainTicketEntitys(data);
        }

        mBinding.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
