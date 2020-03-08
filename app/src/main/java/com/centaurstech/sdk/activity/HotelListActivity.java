package com.centaurstech.sdk.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;

import com.centaurstech.qiwu.entity.HotelEntity;
import com.centaurstech.qiwu.utils.DateUtils;
import com.centaurstech.sdk.adapter.HotelListAdapter;
import com.centaurstech.sdk.databinding.ActivityHotelListBinding;


public class HotelListActivity extends BaseActivity {

    private ActivityHotelListBinding mBinding;

    private HotelListAdapter mAdapter;

    private HotelEntity mHotelEntity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (null != mHotelEntity) {
            mBinding.iHeadLayout.tvTitle.setText(DateUtils.date2Date(mHotelEntity.getArrival_date(), "yyyy-MM-dd", "M月d日")
                    + " - " + DateUtils.date2Date(mHotelEntity.getDeparture_date(), "yyyy-MM-dd", "M月d日") + "    " + mHotelEntity.getCity());
        }
        if (null == mAdapter) {
            mAdapter = new HotelListAdapter(mHotelEntity);
        }
        mBinding.hotelListView.setLayoutManager(new LinearLayoutManager(this));
        mBinding.hotelListView.setAdapter(mAdapter);
    }
}
