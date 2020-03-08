package com.centaurstech.sdk.adapter;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.centaurstech.qiwu.common.CommentMethod;
import com.centaurstech.qiwu.common.Const;
import com.centaurstech.qiwu.entity.TrainTicketEntity;
import com.centaurstech.qiwu.utils.UIUtils;
import com.centaurstech.sdk.R;
import com.centaurstech.sdk.activity.BaseActivity;
import com.centaurstech.sdk.activity.order.ConfirmTrainOrderActivity;
import com.centaurstech.sdk.databinding.ItemOrderTrainBinding;

import java.util.ArrayList;

public class OrderMoreTrainAdapter extends RecyclerView.Adapter<OrderMoreTrainAdapter.OrderMoreViewHolder> {

    ArrayList<TrainTicketEntity> mTrainTicketEntitys = new ArrayList<>();

    BaseActivity mActivity;

    public OrderMoreTrainAdapter(BaseActivity activity) {
        this.mActivity = activity;
    }

    @NonNull
    @Override
    public OrderMoreViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new OrderMoreViewHolder(View.inflate(viewGroup.getContext(), R.layout.item_order_train, null));
    }

    @Override
    public void onBindViewHolder(@NonNull OrderMoreViewHolder orderMoreViewHolder, int i) {
        TrainTicketEntity ticketEntity = mTrainTicketEntitys.get(i);
        orderMoreViewHolder.mItemOrderTrainBinding.tvOriginAisle.setText(ticketEntity.getStation());
        orderMoreViewHolder.mItemOrderTrainBinding.tvOriginTime.setText(CommentMethod.getTime(ticketEntity.getDeparturetime(), "HH:mm"));
        orderMoreViewHolder.mItemOrderTrainBinding.tvPassAddress.setText("");
        orderMoreViewHolder.mItemOrderTrainBinding.ivTrafficIcon.setImageResource(R.mipmap.train_icon_way);
        orderMoreViewHolder.mItemOrderTrainBinding.tvDestinationAisle.setText(ticketEntity.getEndstation());
        orderMoreViewHolder.mItemOrderTrainBinding.tvDestinationTime.setText(CommentMethod.getTime(ticketEntity.getArrivaltime(), "HH:mm"));
        orderMoreViewHolder.mItemOrderTrainBinding.tvVehicleType.setText(ticketEntity.getType() + ticketEntity.getTrainno());
//        binding.iTicketInfo.tvMiddleText.setText(UIUtils.getString(R.string.transit)+ticketEntity.getTrainTicket().getMiddleStation()+"站");
        orderMoreViewHolder.mItemOrderTrainBinding.tvMiddleText.setText("");
        orderMoreViewHolder.mItemOrderTrainBinding.vLine.setVisibility(View.GONE);
        orderMoreViewHolder.mItemOrderTrainBinding.tvDuration.setText(UIUtils.getString(R.string.drive) + CommentMethod.getDuration(ticketEntity.getCosttime()));
        orderMoreViewHolder.mItemOrderTrainBinding.tvPrice.setText(ticketEntity.getTrainTicket().getTicketPrice());
        orderMoreViewHolder.mItemOrderTrainBinding.tvCabin.setText(ticketEntity.getTrainTicket().getTicketType());

        orderMoreViewHolder.mItemOrderTrainBinding.rlRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mActivity.launchActivity(ConfirmTrainOrderActivity.class, new BaseActivity.IntentExpand() {
                    @Override
                    public void extraValue(Intent intent) {
                        intent.putExtra("train", ticketEntity);
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return mTrainTicketEntitys.size();
    }

    class OrderMoreViewHolder extends RecyclerView.ViewHolder {
        public ItemOrderTrainBinding mItemOrderTrainBinding;

        public OrderMoreViewHolder(@NonNull View itemView) {
            super(itemView);
            mItemOrderTrainBinding = DataBindingUtil.bind(itemView);
        }
    }

    public void setrainTicketEntitys(ArrayList<TrainTicketEntity> mTrainTicketEntitys) {
        this.mTrainTicketEntitys = mTrainTicketEntitys;
    }
}
