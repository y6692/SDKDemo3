package com.centaurstech.sdk.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.centaurstech.qiwu.entity.TrainSeatTypeEntity;
import com.centaurstech.qiwu.utils.UIUtils;
import com.centaurstech.sdk.R;
import com.centaurstech.sdk.activity.order.ConfirmTrainOrderActivity;
import com.centaurstech.sdk.databinding.ItemTrainSeatTypeBinding;
import com.centaurstech.sdk.utils.ConversionUtils;
import com.qiwu.ui.view.adapter.DataBindRecyclerViewAdapter;

/**
 * Time:2019/12/27
 * Author: 樊德鹏
 * Description:
 */
public class TrainSeatTypeAdapter extends DataBindRecyclerViewAdapter<TrainSeatTypeEntity, ItemTrainSeatTypeBinding> {

    public TrainSeatTypeAdapter(Context context) {
        super(context);
    }

    @Override
    public int getItemViewType(int position) {
        return R.layout.item_train_seat_type;
    }

    private String mSeatClass;
    private String mSeatName;
    private String mTicketPrice;
    private int mTicketNumber;
    CardView mCardView;
    TextView mTvSeatName;
    TextView mTvCount;
    TextView mTvPrice;


    @Override
    public void onBindHolder(ItemTrainSeatTypeBinding binding, TrainSeatTypeEntity itemData, int position) {
        RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) binding.flContainer.getLayoutParams();
        if (mData.size() > 4) {
            layoutParams.width = (int) (UIUtils.getScreensWidth() / 4.5);
        } else {
            layoutParams.width = UIUtils.getScreensWidth() / mData.size();
        }
        if (itemData.getSeatClass().equals(mSeatClass)) {
            binding.cvContainer.setCardBackgroundColor(UIUtils.getColor(R.color.colorFF9600));
            binding.tvSeatName.setTextColor(UIUtils.getColor(R.color.colorWhite));
            binding.tvCount.setTextColor(UIUtils.getColor(R.color.colorWhite));
            binding.tvPrice.setTextColor(UIUtils.getColor(R.color.colorWhite));
            mCardView = binding.cvContainer;
            mTvSeatName = binding.tvSeatName;
            mTvCount = binding.tvCount;
            mTvPrice = binding.tvPrice;
            mTicketNumber = itemData.getNumber();
        } else {
            binding.cvContainer.setCardBackgroundColor(UIUtils.getColor(R.color.colorWhite));
            binding.tvSeatName.setTextColor(UIUtils.getColor(R.color.colorB3B3B3));
            binding.tvCount.setTextColor(UIUtils.getColor(R.color.colorB3B3B3));
            binding.tvPrice.setTextColor(UIUtils.getColor(R.color.colorB3B3B3));
        }

        switch (itemData.getNumber()) {
            case 0:
                binding.tvCount.setText(R.string.no_ticket);
                binding.tvSeatName.setTextColor(UIUtils.getColor(R.color.colorB3B3B3));
                binding.tvCount.setTextColor(UIUtils.getColor(R.color.colorB3B3B3));
                break;
            default:
                if (!itemData.getSeatClass().equals(mSeatClass)) {
                    binding.tvSeatName.setTextColor(UIUtils.getColor(R.color.color2A2A2A));
                    binding.tvCount.setTextColor(UIUtils.getColor(R.color.color2A2A2A));
                }
                if (itemData.getNumber() > 20) {
                    binding.tvCount.setText(R.string.have_ticket);
                } else {
                    binding.tvCount.setText(itemData.getNumber() + "张");
                }
                break;
        }


        binding.flContainer.requestLayout();
        binding.tvSeatName.setText(itemData.getName());
        binding.tvPrice.setText(ConversionUtils.stringToNumber2(itemData.getPrice()));
    }

    @Override
    public void itemClick(ItemTrainSeatTypeBinding binding, TrainSeatTypeEntity itemData, int position, View v) {
        super.itemClick(binding, itemData, position, v);

        if (itemData.getNumber() == 0) return;

        if (mCardView != null) {
            mCardView.setCardBackgroundColor(UIUtils.getColor(R.color.colorWhite));
            mTvPrice.setTextColor(UIUtils.getColor(R.color.colorB3B3B3));
            if (itemData.getNumber() == 0) {
                mTvSeatName.setTextColor(UIUtils.getColor(R.color.colorB3B3B3));
                mTvCount.setTextColor(UIUtils.getColor(R.color.colorB3B3B3));
            } else {
                mTvSeatName.setTextColor(UIUtils.getColor(R.color.color2A2A2A));
                mTvCount.setTextColor(UIUtils.getColor(R.color.color2A2A2A));
            }
        }


        mCardView = binding.cvContainer;
        mTvSeatName = binding.tvSeatName;
        mTvCount = binding.tvCount;
        mTvPrice = binding.tvPrice;

        mSeatClass = itemData.getSeatClass();
        mSeatName = itemData.getName();
        mTicketPrice = itemData.getPrice();
        mTicketNumber = itemData.getNumber();

        mCardView.setCardBackgroundColor(UIUtils.getColor(R.color.colorFF9600));
        mTvSeatName.setTextColor(UIUtils.getColor(R.color.colorWhite));
        mTvCount.setTextColor(UIUtils.getColor(R.color.colorWhite));
        mTvPrice.setTextColor(UIUtils.getColor(R.color.colorWhite));
        ((ConfirmTrainOrderActivity) getContext()).setPersonCount();

    }


    public String getSeatName() {
        return mSeatName;
    }

    public void setSeatName(String seatName) {
        mSeatName = seatName;
    }

    public String getSeatClass() {
        return mSeatClass;
    }

    public void setSeatClass(String seatClass) {
        mSeatClass = seatClass;
    }


    public String getTicketPrice() {
        return mTicketPrice;
    }

    public void setTicketPrice(String ticketPrice) {
        mTicketPrice = ticketPrice;
    }


    public int getmTicketNumber() {
        return mTicketNumber;
    }

    public void setmTicketNumber(int mTicketNumber) {
        this.mTicketNumber = mTicketNumber;
    }
}
