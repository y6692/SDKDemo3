package com.centaurstech.sdk.adapter;

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.centaurstech.qiwu.QiWuAPI;
import com.centaurstech.qiwu.entity.CinemaEntity;
import com.centaurstech.qiwu.entity.MovieEntity;
import com.centaurstech.sdk.R;
import com.centaurstech.sdk.databinding.ItemTextBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Leon(黄长亮)
 * @describe TODO
 * @date 2019/7/26
 */
public class CinemaAdapter extends RecyclerView.Adapter<CinemaAdapter.MovieViewHolder> {

    List<CinemaEntity> mEntities = new ArrayList<>();

    private onItemClick mOnItemClick;

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new MovieViewHolder(View.inflate(viewGroup.getContext(), R.layout.item_text, null));
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder movieViewHolder, int i) {
        CinemaEntity cinemaEntity = mEntities.get(i);
        movieViewHolder.mTextBinding.tvTitle.setText(cinemaEntity.getName());
        movieViewHolder.mTextBinding.tvText.setText("点击选择");
        movieViewHolder.mTextBinding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClick != null) {
                    mOnItemClick.onItemClick(cinemaEntity);
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return mEntities.size();
    }


    public static class MovieViewHolder extends RecyclerView.ViewHolder {

        public ItemTextBinding mTextBinding;

        public MovieViewHolder(@NonNull View itemView) {
            super(itemView);
            mTextBinding = DataBindingUtil.bind(itemView);
        }
    }


    public List<CinemaEntity> getEntities() {
        return mEntities;
    }

    public void setEntities(List<CinemaEntity> entities) {
        mEntities = entities;
    }

    public void setOnItemClick(onItemClick mOnItemClick) {
        this.mOnItemClick = mOnItemClick;
    }

    public interface onItemClick {
        void onItemClick(CinemaEntity cinemaEntity);
    }
}
