package com.centaurstech.sdk.adapter;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.centaurstech.qiwu.entity.RechargeFeeEntity;
import com.centaurstech.qiwu.utils.UIUtils;
import com.centaurstech.sdk.R;
import com.centaurstech.sdk.databinding.ItemTelephoneChargePriceBinding;
import com.centaurstech.sdk.utils.ConversionUtils;

import java.util.ArrayList;

public class TelephoneChargePriceAdapter extends RecyclerView.Adapter<TelephoneChargePriceAdapter.TelephoneChargeViewHolder> {

    private ArrayList<RechargeFeeEntity> mRechargeFeeEntitys = new ArrayList<>();
    private int mPosition = -1;
    private String mMoney = "";
    private ImageView mIvBackground;
    private TextView mTvSellingPrice;
    private boolean mEnable = true;

    private onItemCLickListener onItemCLick;


    @NonNull
    @Override
    public TelephoneChargeViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new TelephoneChargePriceAdapter.TelephoneChargeViewHolder(View.inflate(viewGroup.getContext(), R.layout.item_telephone_charge_price, null));
    }

    @Override
    public void onBindViewHolder(@NonNull TelephoneChargeViewHolder telephoneChargeViewHolder, int position) {
        RechargeFeeEntity mRechargeFeeEntity = mRechargeFeeEntitys.get(position);
        telephoneChargeViewHolder.mPriceBinding.tvPrice.setText(UIUtils.getString(R.string.money_sign) + mRechargeFeeEntity.getAmount());
        telephoneChargeViewHolder.mPriceBinding.tvSellingPrice.setText(UIUtils.getString(R.string.selling_price) + ":" + UIUtils.getString(R.string.money_sign) + ConversionUtils.stringToNumber2(mRechargeFeeEntity.getUser_price()));
        telephoneChargeViewHolder.mPriceBinding.tvPrice.setTextColor(UIUtils.getColor(R.color.colorFF2A2A2A));
        if (mPosition == position || mRechargeFeeEntity.getAmount().equals(mMoney)) {
            mPosition = position;
            mMoney = mRechargeFeeEntity.getAmount();
            telephoneChargeViewHolder.mPriceBinding.ivBackground.setImageResource(R.mipmap.charge_price_bg_yellow);
            mIvBackground = telephoneChargeViewHolder.mPriceBinding.ivBackground;
            telephoneChargeViewHolder.mPriceBinding.tvSellingPrice.setTextColor(UIUtils.getColor(R.color.colorFF2A2A2A));
            mTvSellingPrice = telephoneChargeViewHolder.mPriceBinding.tvSellingPrice;
        } else {
            telephoneChargeViewHolder.mPriceBinding.ivBackground.setImageResource(R.mipmap.charge_price_bg_white);
            telephoneChargeViewHolder.mPriceBinding.tvSellingPrice.setTextColor(UIUtils.getColor(R.color.colorFF9100));
        }

        if (Double.valueOf(mRechargeFeeEntity.getUser_price()) == 0) {
            telephoneChargeViewHolder.mPriceBinding.tvPrice.setTextColor(UIUtils.getColor(R.color.colorFFB1B1B1));
            telephoneChargeViewHolder.mPriceBinding.tvSellingPrice.setTextColor(UIUtils.getColor(R.color.colorFFB1B1B1));
        }

        if (!mEnable) {
            telephoneChargeViewHolder.mPriceBinding.tvPrice.setTextColor(UIUtils.getColor(R.color.colorFFB1B1B1));
            telephoneChargeViewHolder.mPriceBinding.tvSellingPrice.setTextColor(UIUtils.getColor(R.color.colorFFB1B1B1));
        }

        telephoneChargeViewHolder.mPriceBinding.flClickLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onItemCLick != null) {
                    onItemCLick.onItemCLick(telephoneChargeViewHolder.mPriceBinding, mRechargeFeeEntity, position);
                }
            }
        });

    }

    interface onItemCLickListener {
        void onItemCLick(ItemTelephoneChargePriceBinding binding, RechargeFeeEntity mRechargeFeeEntity, int position);
    }

    @Override
    public int getItemCount() {
        return mRechargeFeeEntitys.size();
    }

    public static class TelephoneChargeViewHolder extends RecyclerView.ViewHolder {

        public ItemTelephoneChargePriceBinding mPriceBinding;

        public TelephoneChargeViewHolder(@NonNull View itemView) {
            super(itemView);
            mPriceBinding = DataBindingUtil.bind(itemView);
        }
    }

    public void setEnable(boolean enable) {
        mEnable = enable;
        notifyDataSetChanged();
    }

    public String getMoney() {
        return mMoney;
    }

    public void setMoney(String money) {
        mMoney = money;
    }

    public int getPosition() {
        return mPosition;
    }

    public void setPosition(int position) {
        mPosition = position;
    }

    public ImageView getIvBackground() {
        return mIvBackground;
    }

    public void setIvBackground(ImageView ivBackground) {
        mIvBackground = ivBackground;
    }

    public TextView getTvSellingPrice() {
        return mTvSellingPrice;
    }

    public void setTvSellingPrice(TextView tvSellingPrice) {
        mTvSellingPrice = tvSellingPrice;
    }

    public void setRechargeFeeEntitys(ArrayList<RechargeFeeEntity> mRechargeFeeEntitys) {
        this.mRechargeFeeEntitys = mRechargeFeeEntitys;
    }

    public void setOnItemCLick(onItemCLickListener onItemCLick) {
        this.onItemCLick = onItemCLick;
    }
}
