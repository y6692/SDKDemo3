package com.centaurstech.sdk.activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import com.centaurstech.qiwu.QiWuAPI;
import com.centaurstech.qiwu.callback.APICallback;
import com.centaurstech.qiwu.entity.APIEntity;
import com.centaurstech.qiwu.entity.MovieEntity;
import com.centaurstech.qiwu.entity.MovieSeatInfoEntity;
import com.centaurstech.qiwu.entity.MovieTimeTableEntity;
import com.centaurstech.qiwu.entity.OrderEntity;
import com.centaurstech.qiwu.utils.GsonUtils;
import com.centaurstech.qiwu.utils.LogUtils;
import com.centaurstech.qiwu.utils.UIUtils;
import com.centaurstech.sdk.R;
import com.centaurstech.sdk.databinding.ActivityListBinding;
import com.centaurstech.sdk.databinding.ActivityMoviesBinding;
import com.centaurstech.sdk.databinding.ItemMoviesBinding;
import com.centaurstech.sdk.databinding.ItemOrderBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Leon(黄长亮)
 * @describe 所有影片
 * @date 2019/6/22
 */
public class MoviesActivity extends BaseActivity {

    ActivityMoviesBinding mListBinding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mListBinding = DataBindingUtil.setContentView(this, R.layout.activity_movies);
        mListBinding.rvList.setLayoutManager(new LinearLayoutManager(this));
        mListBinding.rvList.setAdapter(mAdapter);
        QiWuAPI.movie.getAllMovie(getIntent().getStringExtra("key"), new APICallback<APIEntity<ArrayList<MovieEntity>>>() {
            @Override
            public void onSuccess(APIEntity<ArrayList<MovieEntity>> response) {
                if (response.isSuccess()) {
                    data.addAll(response.getData());
                    mAdapter.notifyDataSetChanged();
                }
            }
        });
        mListBinding.iHeader.tvTitle.setText(UIUtils.getString(R.string.hot_movie));
        mListBinding.iHeader.btnNavBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private List<MovieEntity> data = new ArrayList<>();

    private RecyclerView.Adapter mAdapter = new RecyclerView.Adapter() {
        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            return new CustomHolder(View.inflate(viewGroup.getContext(), R.layout.item_movies, null));
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
            CustomHolder indexViewHolder = (CustomHolder) viewHolder;
            MovieEntity movieEntity = data.get(i);
            indexViewHolder.mBinding.tvMovieName.setText(movieEntity.getName());
            switch (movieEntity.getStatus()) {
                case "ON_SALE":
                    indexViewHolder.mBinding.tvMovieTime.setText("在映");
                    break;
                case "PRE_SELL":
                    indexViewHolder.mBinding.tvMovieTime.setText("预售");
                    break;
                case "UNSOLD":
                    if (TextUtils.isEmpty(movieEntity.getReleaseDate())) {
                        indexViewHolder.mBinding.tvMovieTime.setText("即将上映");
                    } else {
                        indexViewHolder.mBinding.tvMovieTime.setText(movieEntity.getReleaseDate() + " 上映");
                    }
                    break;
            }
        }

        @Override
        public int getItemCount() {
            return data.size();
        }
    };


    private class CustomHolder extends RecyclerView.ViewHolder {

        public ItemMoviesBinding mBinding;

        public CustomHolder(@NonNull View itemView) {
            super(itemView);
            mBinding = DataBindingUtil.bind(itemView);
        }
    }

}
