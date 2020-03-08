package com.centaurstech.sdk.adapter;


import android.content.Context;
import android.view.View;

import com.centaurstech.qiwu.common.Const;
import com.centaurstech.qiwu.entity.PassengerEntity;
import com.centaurstech.sdk.R;
import com.centaurstech.sdk.common.Const2;
import com.centaurstech.sdk.databinding.ItemPassengerSlidingBinding;
import com.centaurstech.sdk.listener.OnPassengerSlidingListener;
import com.qiwu.ui.view.adapter.DataBindRecyclerViewAdapter;


/**
 * @author Leon(黄长亮)
 * @describe 乘机人
 * @date 2018/7/20
 */

public class PassengerSlidingAdapter extends DataBindRecyclerViewAdapter<PassengerEntity, ItemPassengerSlidingBinding> {

    private OnPassengerSlidingListener mOnPassengerSlidingListener;

    public PassengerSlidingAdapter(Context context) {
        super(context);
    }

    @Override
    public int getItemViewType(int position) {
        return R.layout.item_passenger_sliding;
    }

    @Override
    public void onBindHolder(ItemPassengerSlidingBinding binding, PassengerEntity itemData, int position) {
        addClickListener(binding.ivDelete);
        String certificateType = Const2.DataArray.Certificates[itemData.getiDType() - 1];
        binding.textAirPassenger.setText(itemData.getCname());
        binding.tvAirPassenger.setText(certificateType + "：" + itemData.getiDNumber());
    }


    @Override
    public void itemClick(ItemPassengerSlidingBinding binding, PassengerEntity itemData, int position, View v) {
        switch (v.getId()) {
            case R.id.ivDelete:
                getData().remove(position);
                notifyDataSetChanged();
                if (mOnPassengerSlidingListener != null) {
                    mOnPassengerSlidingListener.onOperation(1, position);
                }
                break;
        }
    }


    public void setOnPassengerSlidingListener(OnPassengerSlidingListener onPassengerSlidingListener) {
        mOnPassengerSlidingListener = onPassengerSlidingListener;
    }
}