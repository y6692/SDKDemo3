package com.centaurstech.sdk.adapter;


import android.content.Context;
import android.text.TextUtils;
import android.view.View;

import com.centaurstech.qiwu.entity.TrainPassengerEntity;
import com.centaurstech.qiwu.utils.UIUtils;
import com.centaurstech.sdk.R;
import com.centaurstech.sdk.common.Const2;
import com.centaurstech.sdk.databinding.ItemPassengerBinding;
import com.centaurstech.sdk.utils.ConversionUtils;
import com.qiwu.ui.view.adapter.DataBindRecyclerViewAdapter;

/**
 * @author Leon(黄长亮)
 * @describe 乘机人
 * @date 2018/7/20
 */

public class PassengerAdapter extends DataBindRecyclerViewAdapter<TrainPassengerEntity, ItemPassengerBinding> {

    public PassengerAdapter(Context context) {
        super(context);
    }

    @Override
    public int getItemViewType(int position) {
        return R.layout.item_passenger;
    }

    @Override
    public void onBindHolder(ItemPassengerBinding binding, TrainPassengerEntity itemData, int position) {
        switch (itemData.getId_type()) {
            case 1:
                binding.tvPassenger.setText(itemData.getName() + " " + itemData.getId_type_name() + " : " + ConversionUtils.encryptIDCode(itemData.getId_no()));
                break;
            default:
                binding.tvPassenger.setText(itemData.getName() + " " + itemData.getId_type_name() + " : " + ConversionUtils.encryptIDCode4(itemData.getId_no()));
                break;
        }
        binding.tvRefund.setVisibility(View.GONE);
        binding.tvRefundState.setText(Const2.DataArray.OrderState[itemData.getState()]);
        switch (itemData.getType()) {
            case 1:
                binding.tvTravelerType.setText(R.string.train_passenger);
                if (!TextUtils.isEmpty(itemData.getSeat_no())) {
                    binding.tvSeatNumber.setText(UIUtils.getString(R.string.seat_no) + ": " + itemData.getSeat_no().replace(",", ""));
                } else {
                    binding.tvSeatNumber.setText(UIUtils.getString(R.string.seat_no) + ": " + UIUtils.getString(R.string.occupy_seat));
                }
                if (itemData.getState() == 4 || itemData.getState() == 8) {
                    binding.tvSeatNumber.setVisibility(View.INVISIBLE);
                } else {
                    binding.tvSeatNumber.setVisibility(View.VISIBLE);
                }
                if (TextUtils.isEmpty(itemData.getSeat_no())) {
                    binding.tvSeatNumber.setVisibility(View.GONE);
                }else {
                    binding.tvSeatNumber.setVisibility(View.VISIBLE);
                }
                break;
            case 2:
                binding.tvTravelerType.setText(R.string.train_passenger);
                if (!TextUtils.isEmpty(itemData.getSeat_no())) {
                    binding.tvSeatNumber.setText(UIUtils.getString(R.string.seat_no) + ": " + itemData.getSeat_no().replace(",", ""));
                }
                binding.tvSeatNumber.setVisibility(View.VISIBLE);
                binding.tvRefund.setVisibility(View.VISIBLE);


                break;
            case 3:
                binding.tvTravelerType.setText(R.string.air_passenger);
                binding.tvRefund.setVisibility(View.VISIBLE);
                if (itemData.isDisabled()) {
                    binding.tvRefundState.setVisibility(View.VISIBLE);
                    binding.tvRefund.setVisibility(View.INVISIBLE);
                    binding.tvRefundState.setText(R.string.not_refund);
                } else {
                    binding.tvRefundState.setVisibility(View.INVISIBLE);
                    binding.tvRefund.setVisibility(View.VISIBLE);
                }
                break;
        }


        if (itemData.getType() != 1 && (itemData.isRefunding()
                || itemData.getState() == 6
                || itemData.getState() == 5
                || itemData.getState() == 12)) {
            binding.tvRefundState.setVisibility(View.VISIBLE);
            binding.tvRefund.setVisibility(View.INVISIBLE);
        }

        addClickListener(binding.tvRefund);
    }
}
