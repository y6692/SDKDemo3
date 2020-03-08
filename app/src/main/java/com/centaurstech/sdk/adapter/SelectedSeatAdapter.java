package com.centaurstech.sdk.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.centaurstech.qiwu.entity.MovieSeatInfoEntity;
import com.centaurstech.qiwu.utils.UIUtils;
import com.centaurstech.sdk.R;
import com.centaurstech.sdk.databinding.ItemSelectedSeatBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: 樊德鹏
 * 时   间:2019/5/28
 * 简   述:<功能描述>
 */
public class SelectedSeatAdapter extends RecyclerView.Adapter<SelectedSeatAdapter.SelectedSeatViewHolder> {

    private List<MovieSeatInfoEntity.SeatEntity> mSeatEntitys = new ArrayList<>();

    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setSeatEntitys(List<MovieSeatInfoEntity.SeatEntity> mSeatEntitys) {
        this.mSeatEntitys = mSeatEntitys;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SelectedSeatViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new SelectedSeatViewHolder(View.inflate(viewGroup.getContext(), R.layout.item_selected_seat, null));
    }

    @Override
    public void onBindViewHolder(@NonNull SelectedSeatViewHolder selectedSeatViewHolder, int i) {
        MovieSeatInfoEntity.SeatEntity mEntity = mSeatEntitys.get(i);
        selectedSeatViewHolder.mItemSelectedSeatBinding.tvPrice.setText(UIUtils.getString(R.string.money_sign) + mEntity.getPrice());
        selectedSeatViewHolder.mItemSelectedSeatBinding.tvSeatNumber.setText(mEntity.getRow() + "排" + mEntity.getCol() + "座");
        selectedSeatViewHolder.mItemSelectedSeatBinding.llRootLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemCLick(mEntity, i);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mSeatEntitys.size();
    }


    public static class SelectedSeatViewHolder extends RecyclerView.ViewHolder {

        public ItemSelectedSeatBinding mItemSelectedSeatBinding;

        public SelectedSeatViewHolder(@NonNull View itemView) {
            super(itemView);
            mItemSelectedSeatBinding = DataBindingUtil.bind(itemView);
        }
    }

    public void remove(int position){
        mSeatEntitys.remove(position);
        notifyItemRemoved(position);
    }

   public interface OnItemClickListener {
        void onItemCLick(MovieSeatInfoEntity.SeatEntity seatEntity, int position);
    }
}
