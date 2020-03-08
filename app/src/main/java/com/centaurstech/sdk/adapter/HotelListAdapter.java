package com.centaurstech.sdk.adapter;

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.centaurstech.qiwu.entity.HotelEntity;
import com.centaurstech.sdk.databinding.ItemHotelBinding;

/**
 * @Author: 樊德鹏
 * 时   间:2019/2/15
 * 简   述:<功能描述>
 */

public class HotelListAdapter extends RecyclerView.Adapter<HotelListAdapter.HotelListViewHolder>{
    private HotelEntity mHotelEntity;

    private int showAll;

    public HotelListAdapter(HotelEntity hotelEntity) {
        this.mHotelEntity = hotelEntity;
    }

    @NonNull
    @Override
    public HotelListViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull HotelListViewHolder hotelListViewHolder, int i) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public static class HotelListViewHolder extends RecyclerView.ViewHolder {

        public ItemHotelBinding mItemHotelBinding;

        public HotelListViewHolder(@NonNull View itemView) {
            super(itemView);
            mItemHotelBinding = DataBindingUtil.bind(itemView);
        }
    }
}
