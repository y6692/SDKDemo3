package com.centaurstech.sdk.adapter;

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import com.centaurstech.qiwu.common.CommentMethod;
import com.centaurstech.qiwu.entity.FlightTicketEntity;
import com.centaurstech.qiwu.utils.UIUtils;
import com.centaurstech.sdk.R;
import com.centaurstech.sdk.activity.BaseActivity;
import com.centaurstech.sdk.databinding.ItemOrderFlightBinding;
import com.centaurstech.sdk.databinding.ItemOrderTrainBinding;

import java.util.ArrayList;
import java.util.List;

public class OrderMoreFlightAdapter extends RecyclerView.Adapter<OrderMoreFlightAdapter.OrderMoreViewHolder> {

    private List<FlightTicketEntity> mFlightTicketEntitys = new ArrayList<>();

    BaseActivity mActivity;

    public void setOnItemListener(OnItemListener mOnItemListener) {
        this.mOnItemListener = mOnItemListener;
    }

    private OnItemListener mOnItemListener;

    public OrderMoreFlightAdapter(BaseActivity activity) {
        this.mActivity = activity;
    }

    @NonNull
    @Override
    public OrderMoreViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new OrderMoreFlightAdapter.OrderMoreViewHolder(View.inflate(viewGroup.getContext(), R.layout.item_order_flight, null));
    }

    @Override
    public void onBindViewHolder(@NonNull OrderMoreViewHolder orderMoreViewHolder, int i) {
        FlightTicketEntity airTicketEntity = mFlightTicketEntitys.get(i);
        orderMoreViewHolder.mItemOrderFlightBinding.tvOriginAisle.setText(airTicketEntity.getDeparture_airport().get(0));
        orderMoreViewHolder.mItemOrderFlightBinding.tvOriginTime.setText(CommentMethod.getTime(airTicketEntity.getDeparture_time().get(0), "HH:mm"));
        orderMoreViewHolder.mItemOrderFlightBinding.tvPassAddress.setText((TextUtils.isEmpty(airTicketEntity.getStop_city()) ? "" : UIUtils.getString(R.string.transit) + " " + airTicketEntity.getStop_city()));
        orderMoreViewHolder.mItemOrderFlightBinding.ivTrafficIcon.setImageResource(R.mipmap.airp_icon_way);
        orderMoreViewHolder.mItemOrderFlightBinding.tvDestinationAisle.setText(airTicketEntity.getArrival_airport().get(0));
        orderMoreViewHolder.mItemOrderFlightBinding.tvDestinationTime.setText(CommentMethod.getTime(airTicketEntity.getArrival_time().get(0), "HH:mm"));
        orderMoreViewHolder.mItemOrderFlightBinding.tvVehicleType.setText(airTicketEntity.getCarrier().get(0) + airTicketEntity.getNumber().get(0));
//        binding.iTicketInfo.tvMiddleText.setText(UIUtils.getString(R.string.transit)+ticketEntity.getTrainTicket().getMiddleStation()+"站");
        orderMoreViewHolder.mItemOrderFlightBinding.tvMiddleText.setText(airTicketEntity.getFlightTypeFullName().get(0));
        orderMoreViewHolder.mItemOrderFlightBinding.tvDuration.setText(UIUtils.getString(R.string.voyage) + CommentMethod.getDuration(Integer.valueOf(airTicketEntity.getDuration().get(0))));
        orderMoreViewHolder.mItemOrderFlightBinding.tvPrice.setText(airTicketEntity.getPrice());
        String cabin = airTicketEntity.getCabin().get(0).length() >= 3 ? airTicketEntity.getCabin().get(0) : "经济舱";
        orderMoreViewHolder.mItemOrderFlightBinding.tvPrice.setText(airTicketEntity.getPrice());
        orderMoreViewHolder.mItemOrderFlightBinding.tvCabin.setText(cabin);

        orderMoreViewHolder.mItemOrderFlightBinding.rlRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnItemListener != null) {
                    mOnItemListener.onItemCLick(airTicketEntity,i);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mFlightTicketEntitys.size();
    }

    class OrderMoreViewHolder extends RecyclerView.ViewHolder {
        public ItemOrderFlightBinding mItemOrderFlightBinding;

        public OrderMoreViewHolder(@NonNull View itemView) {
            super(itemView);
            mItemOrderFlightBinding = DataBindingUtil.bind(itemView);
        }
    }

    public void setFlightTicketEntitys(List<FlightTicketEntity> mFlightTicketEntitys) {
        this.mFlightTicketEntitys = mFlightTicketEntitys;
    }

    public interface OnItemListener {
        void onItemCLick(FlightTicketEntity entity,int position);
    }
}
