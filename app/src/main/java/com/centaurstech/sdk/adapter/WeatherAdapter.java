package com.centaurstech.sdk.adapter;


import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.centaurstech.qiwu.entity.WeatherEntity;
import com.centaurstech.sdk.R;
import com.centaurstech.sdk.databinding.ItemWeatherTwoBinding;
import com.centaurstech.sdk.utils.ConversionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Leon(黄长亮)
 * @describe 地图兴趣点Item
 * @date 2018/7/20
 */

public class WeatherAdapter extends RecyclerView.Adapter<WeatherAdapter.WeatherViewHolder> {

    List<WeatherEntity> mWeatherEntitys = new ArrayList<>();

    public List<WeatherEntity> getmWeatherEntitys() {
        return mWeatherEntitys;
    }

    public void setWeatherEntitys(List<WeatherEntity> mWeatherEntitys) {
        this.mWeatherEntitys = mWeatherEntitys;
    }

    @NonNull
    @Override
    public WeatherViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new WeatherAdapter.WeatherViewHolder(View.inflate(viewGroup.getContext(), R.layout.item_weather_two, null));
    }

    @Override
    public void onBindViewHolder(@NonNull WeatherViewHolder weatherViewHolder, int i) {
        WeatherEntity mWeatherEntity = mWeatherEntitys.get(i);
        weatherViewHolder.mItemWeatherTwoBinding.ivICon.setImageResource(ConversionUtils.weather(mWeatherEntity.getIconSeq()));
        weatherViewHolder.mItemWeatherTwoBinding.tvDate.setText(mWeatherEntity.getDate().substring(mWeatherEntity.getDate().indexOf("-") + 1) + " " + mWeatherEntity.getDayOfWeek());
        weatherViewHolder.mItemWeatherTwoBinding.tvWeather.setText(mWeatherEntity.getWeather());
        weatherViewHolder.mItemWeatherTwoBinding.tvCentigrade.setText(mWeatherEntity.getLow() + "℃ - " + mWeatherEntity.getHigh() + "℃");
    }

    @Override
    public int getItemCount() {
        return mWeatherEntitys.size();
    }


    public static class WeatherViewHolder extends RecyclerView.ViewHolder {

        public ItemWeatherTwoBinding mItemWeatherTwoBinding;

        public WeatherViewHolder(@NonNull View itemView) {
            super(itemView);
            mItemWeatherTwoBinding = DataBindingUtil.bind(itemView);
        }
    }
}