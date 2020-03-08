package com.centaurstech.sdk.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.text.TextUtils;

import com.centaurstech.qiwu.entity.OrderMovieEntity;
import com.centaurstech.qiwu.utils.UIUtils;
import com.centaurstech.sdk.R;
import com.centaurstech.sdk.databinding.ItemCinemaTicketBinding;
import com.qiwu.ui.view.adapter.DataBindRecyclerViewAdapter;

/**
 * Time:2019/12/25
 * Author: 樊德鹏
 * Description:
 */
public class CinemaTicketAdapter extends DataBindRecyclerViewAdapter<OrderMovieEntity.CodeBean, ItemCinemaTicketBinding> {

    public CinemaTicketAdapter(Context context) {
        super(context);
    }

    public void setOrderState(int orderState) {
        this.orderState = orderState;
    }

    private int orderState;

    @Override
    public int getItemViewType(int position) {
        return R.layout.item_cinema_ticket;
    }

    @Override
    public void onBindHolder(ItemCinemaTicketBinding binding, OrderMovieEntity.CodeBean itemData, int position) {
        if (!TextUtils.isEmpty(itemData.getText())) {
            binding.tvTicketText.setText(itemData.getText() + ": ");
        }
        binding.tvTicketNo.setText(itemData.getValue());
        if (orderState == 3) {
            binding.tvTicketText.setTextColor(UIUtils.getColor(R.color.color9B9B9B));
            binding.tvTicketNo.setTextColor(UIUtils.getColor(R.color.color9B9B9B));
            binding.tvTicketNo.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            binding.tvTicketText.setTextColor(UIUtils.getColor(R.color.color2A2A2A));
            binding.tvTicketNo.setTextColor(UIUtils.getColor(R.color.color2A2A2A));
        }
    }
}
