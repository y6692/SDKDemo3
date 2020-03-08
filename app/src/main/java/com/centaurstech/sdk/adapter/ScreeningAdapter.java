package com.centaurstech.sdk.adapter;

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.centaurstech.qiwu.QiWuAPI;
import com.centaurstech.qiwu.entity.CinemaEntity;
import com.centaurstech.qiwu.entity.MovieEntity;
import com.centaurstech.qiwu.entity.MovieTimeTableEntity;
import com.centaurstech.qiwu.entity.ScreeningTimesEntity;
import com.centaurstech.sdk.R;
import com.centaurstech.sdk.databinding.ItemScreeningBinding;
import com.centaurstech.sdk.databinding.ItemTextBinding;
import com.centaurstech.sdk.utils.ConversionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Leon(黄长亮)
 * @describe TODO
 * @date 2019/7/26
 */
public class ScreeningAdapter extends RecyclerView.Adapter<ScreeningAdapter.MovieViewHolder> {

    private onItemClick mOnItemClick;
    List<MovieTimeTableEntity.PlanInfosBean> mPlanInfosBean = new ArrayList<>();

    /***
     * 轮询所有的列表
     */
    public List<MovieTimeTableEntity.PlanInfosBean> getPlanInfos(List<MovieTimeTableEntity> timeTableBeans) {
        if (null == timeTableBeans && timeTableBeans.size() <= 0) {
            return null;
        }
        List<MovieTimeTableEntity.PlanInfosBean> mPlanInfosBeans = new ArrayList<>();
        for (int i = 0; i < timeTableBeans.size(); i++) {
            MovieTimeTableEntity mTimeTableBean = timeTableBeans.get(i);
            for (int j = 0; j < timeTableBeans.get(i).getPlanInfos().size(); j++) {
                MovieTimeTableEntity.PlanInfosBean bean = mTimeTableBean.getPlanInfos().get(j);
                bean.setStartTime(mTimeTableBean.getStartTime());
                bean.setEndTime(mTimeTableBean.getEndTime());
                mPlanInfosBeans.add(bean);
            }
        }
        return mPlanInfosBeans;
    }


    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new MovieViewHolder(View.inflate(viewGroup.getContext(), R.layout.item_screening, null));
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder movieViewHolder, int i) {
        MovieTimeTableEntity.PlanInfosBean timeTableEntity = mPlanInfosBean.get(i);
        movieViewHolder.mItemScreeningBinding.tvStartTimer.setText(timeTableEntity.getStartTime());
        movieViewHolder.mItemScreeningBinding.tvEndTimer.setText(timeTableEntity.getEndTime() + "散场");
        movieViewHolder.mItemScreeningBinding.tvPrice.setText(ConversionUtils.getStringNumber2(timeTableEntity.getPrice()));
        movieViewHolder.mItemScreeningBinding.tvPlayType.setText(timeTableEntity.getLan() + "  " + timeTableEntity.getPlayerType());
        movieViewHolder.mItemScreeningBinding.tvPlayPlace.setText(timeTableEntity.getTheater());

        movieViewHolder.mItemScreeningBinding.rlClickItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClick != null) {
                    mOnItemClick.onItemClick(timeTableEntity);
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return mPlanInfosBean.size();
    }


    public static class MovieViewHolder extends RecyclerView.ViewHolder {

        public ItemScreeningBinding mItemScreeningBinding;

        public MovieViewHolder(@NonNull View itemView) {
            super(itemView);
            mItemScreeningBinding = DataBindingUtil.bind(itemView);
        }
    }


    public List<MovieTimeTableEntity.PlanInfosBean> getmPlanInfosBean() {
        return mPlanInfosBean;
    }

    public void setPlanInfosBean(List<MovieTimeTableEntity.PlanInfosBean> mPlanInfosBean) {
        this.mPlanInfosBean = mPlanInfosBean;
    }

    public void setOnItemClick(onItemClick mOnItemClick) {
        this.mOnItemClick = mOnItemClick;
    }

    public interface onItemClick {
        void onItemClick(MovieTimeTableEntity.PlanInfosBean mPlanInfosBean);
    }

}
