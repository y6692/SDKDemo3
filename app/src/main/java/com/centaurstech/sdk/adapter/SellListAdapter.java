package com.centaurstech.sdk.adapter;

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.centaurstech.qiwu.utils.UIUtils;
import com.centaurstech.sdk.R;
import com.centaurstech.sdk.databinding.ItemSellListBinding;

import java.util.ArrayList;

public class SellListAdapter extends RecyclerView.Adapter<SellListAdapter.SellListViewHolder> {

    ArrayList<String> data = new ArrayList<>();

    public void setData(ArrayList<String> data) {
        this.data = data;
    }


    private String mPrice;
    private int mPosition = 1;
    private CardView mCvItem;
    private TextView mTvPrice;

    private onItemClickListener onItemClick;

    public void setOnItemClick(onItemClickListener onItemClick) {
        this.onItemClick = onItemClick;
    }

    @NonNull
    @Override
    public SellListViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new SellListAdapter.SellListViewHolder(View.inflate(viewGroup.getContext(), R.layout.item_sell_list, null));
    }

    @Override
    public void onBindViewHolder(@NonNull SellListViewHolder sellListViewHolder, int position) {
        String itemData = data.get(position);
        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) sellListViewHolder.mItemSellListBinding.cvItem.getLayoutParams();
        if (position == 0) {
            lp.leftMargin = 0;
        } else {
            lp.leftMargin = UIUtils.dip2Px(12);
        }

        if (position == mPosition) {
            mCvItem = sellListViewHolder.mItemSellListBinding.cvItem;
            mTvPrice = sellListViewHolder.mItemSellListBinding.tvPrice;
            mPrice = itemData;
            mPosition = position;
            mCvItem.setCardBackgroundColor(UIUtils.getColor(R.color.colorFFD400));
            mTvPrice.setTextColor(UIUtils.getColor(R.color.color2A2A2A));
        } else {
            sellListViewHolder.mItemSellListBinding.cvItem.setCardBackgroundColor(UIUtils.getColor(R.color.colorWhite));
            sellListViewHolder.mItemSellListBinding.tvPrice.setTextColor(UIUtils.getColor(R.color.colorFF9100));
        }

        sellListViewHolder.mItemSellListBinding.cvItem.requestLayout();
        sellListViewHolder.mItemSellListBinding.tvMonth.setText((position + 1) + "个月");
        sellListViewHolder.mItemSellListBinding.tvPrice.setText(UIUtils.getString(R.string.money_sign) + " " + itemData);
        sellListViewHolder.mItemSellListBinding.flClickLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPosition = position;
                mPrice = itemData;
                if (mCvItem != null) {
                    mCvItem.setCardBackgroundColor(UIUtils.getColor(R.color.colorWhite));
                }
                if (mTvPrice != null) {
                    mTvPrice.setTextColor(UIUtils.getColor(R.color.colorFF9100));
                }
                mCvItem = sellListViewHolder.mItemSellListBinding.cvItem;
                mTvPrice = sellListViewHolder.mItemSellListBinding.tvPrice;
                mCvItem.setCardBackgroundColor(UIUtils.getColor(R.color.colorFFD400));
                mTvPrice.setTextColor(UIUtils.getColor(R.color.color2A2A2A));

                if (onItemClick != null) {
                    onItemClick.onItemCLick(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class SellListViewHolder extends RecyclerView.ViewHolder {

        public ItemSellListBinding mItemSellListBinding;

        public SellListViewHolder(@NonNull View itemView) {
            super(itemView);
            mItemSellListBinding = DataBindingUtil.bind(itemView);
        }
    }


    public String getPrice() {
        return mPrice;
    }

    public void setPrice(String price) {
        mPrice = price;
    }

    public int getPosition() {
        return mPosition;
    }

    public void setPosition(int position) {
        mPosition = position;
    }

    public interface onItemClickListener {
        void onItemCLick(int position);
    }
}
