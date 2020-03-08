package com.centaurstech.sdk.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.centaurstech.qiwu.entity.WeatherEntity;
import com.centaurstech.qiwu.entity.Weathers;
import com.centaurstech.sdk.R;
import com.centaurstech.sdk.databinding.ItemWeatherCardBinding;
import com.centaurstech.sdk.utils.ConversionUtils;
import com.qiwu.ui.util.UIUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Leon(黄长亮)
 * @describe 天气
 * @date 2018/8/22
 */

public class WeatherListAdapter extends RecyclerView.Adapter<WeatherListAdapter.WeatherListViewHolder> {

    List<Weathers> mWeathers = new ArrayList<>();

    @NonNull
    @Override
    public WeatherListAdapter.WeatherListViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new WeatherListAdapter.WeatherListViewHolder(View.inflate(viewGroup.getContext(), R.layout.item_weather_card, null));
    }

    @Override
    public void onBindViewHolder(@NonNull WeatherListAdapter.WeatherListViewHolder movieViewHolder, int i) {
        Weathers weathers = mWeathers.get(i);
        WeatherAdapter adapter;
        if (movieViewHolder.mItemWeatherCardBinding.rvWeathers.getAdapter() == null) {
            adapter = new WeatherAdapter();
            movieViewHolder.mItemWeatherCardBinding.rvWeathers.setAdapter(adapter);
        } else {
            adapter = (WeatherAdapter) movieViewHolder.mItemWeatherCardBinding.rvWeathers.getAdapter();
        }
        if (weathers == null || weathers.getWeathers() == null || weathers.getWeathers().size() < 1) {
            movieViewHolder.mItemWeatherCardBinding.tvCentigrade.setText("");
            movieViewHolder.mItemWeatherCardBinding.tvWeather.setText("");
            movieViewHolder.mItemWeatherCardBinding.tvDateAndLocation.setText("");
            return;
        }

        if (weathers.getWeathers().size() >= 2) {
            movieViewHolder.mItemWeatherCardBinding.rvWeathers.setVisibility(View.VISIBLE);
            movieViewHolder.mItemWeatherCardBinding.cvWeatherBG.setRadius(0);
            setGridLayoutManager(movieViewHolder.mItemWeatherCardBinding.rvWeathers, 3);
            adapter.setWeatherEntitys(weathers.getWeathers().subList(1, weathers.getWeathers().size()));
        } else {
            movieViewHolder.mItemWeatherCardBinding.rvWeathers.setVisibility(View.GONE);
            movieViewHolder.mItemWeatherCardBinding.cvWeatherBG.setRadius(UIUtils.dip2Px(5));
        }

        WeatherEntity weather = weathers.getWeathers().get(0);
        movieViewHolder.mItemWeatherCardBinding.ivWeather.setImageResource(ConversionUtils.weather(weather.getIconSeq()));


        if (weather.getTemp().equals("none")) {
            movieViewHolder.mItemWeatherCardBinding.tvCentigrade.setText(weather.getLow() + "°/" + weather.getHigh() + "°");
        } else {
            movieViewHolder.mItemWeatherCardBinding.tvCentigrade.setText(weather.getLow() + "°/" + weather.getHigh() + "°");
        }
        movieViewHolder.mItemWeatherCardBinding.tvWeather.setText(weather.getWeather());
        movieViewHolder.mItemWeatherCardBinding.tvDateAndLocation.setText(weather.getDate().replace("-", ".") + " | " + weather.getLocation());
    }

    @Override
    public int getItemCount() {
        return mWeathers.size();
    }

    public static class WeatherListViewHolder extends RecyclerView.ViewHolder {

        public ItemWeatherCardBinding mItemWeatherCardBinding;

        public WeatherListViewHolder(@NonNull View itemView) {
            super(itemView);
            mItemWeatherCardBinding = DataBindingUtil.bind(itemView);
        }
    }

    public void addData(Weathers data) {
        mWeathers.add(data);
        notifyDataSetChanged();
    }

    public void clearList() {
        if (mWeathers != null && mWeathers.size() > 0) {
            mWeathers.clear();
        }
        notifyDataSetChanged();
    }

    private void setGridLayoutManager(RecyclerView recyclerView, int size) {
        if (recyclerView.getLayoutManager() == null) {
            recyclerView.setLayoutManager(createGridLayoutManager(recyclerView.getContext(), size));
        } else {
            GridLayoutManager gridLayoutManager = (GridLayoutManager) recyclerView.getLayoutManager();
            gridLayoutManager.setSpanCount(size);
        }
    }


    private GridLayoutManager createGridLayoutManager(Context context, int size) {
        if (size > 3) size = 3;
        return new GridLayoutManager(context, size) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
    }
}
