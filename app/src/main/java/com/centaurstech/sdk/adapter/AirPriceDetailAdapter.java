package com.centaurstech.sdk.adapter;


import android.content.Context;
import android.text.TextUtils;
import android.view.View;

import com.centaurstech.qiwu.utils.UIUtils;
import com.centaurstech.sdk.R;
import com.centaurstech.sdk.databinding.ItemPriceDetailBinding;
import com.centaurstech.sdk.entity.AirPriceDetailsEntity;
import com.qiwu.ui.view.adapter.DataBindRecyclerViewAdapter;


/**
 * @author Leon(黄长亮)
 * @describe 飞机票价格明细
 * @date 2018/7/20
 */

public class AirPriceDetailAdapter extends DataBindRecyclerViewAdapter<AirPriceDetailsEntity, ItemPriceDetailBinding> {

    public AirPriceDetailAdapter(Context context) {
        super(context);
    }

    @Override
    public int getItemViewType(int position) {
        return R.layout.item_price_detail;
    }

    @Override
    public boolean autoCheckData() {
        return true;
    }

    @Override
    public void onBindHolder(ItemPriceDetailBinding binding, AirPriceDetailsEntity itemData, int position) {
        binding.tvTicketType.setText(TextUtils.isEmpty(itemData.getTripType()) ? "" : itemData.getTripType() + ": ");
        binding.tvTicketType.setVisibility(itemData.getType() == 1 ? View.VISIBLE : View.INVISIBLE);
        binding.tvTicketTitle.setText(itemData.getText());
        binding.tvTicketPrice.setText(UIUtils.getString(R.string.money_sign) + " " + itemData.getPrice());
    }


}