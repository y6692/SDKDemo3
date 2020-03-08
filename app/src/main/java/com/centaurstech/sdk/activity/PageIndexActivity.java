package com.centaurstech.sdk.activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.centaurstech.qiwu.QiWu;
import com.centaurstech.sdk.R;
import com.centaurstech.sdk.activity.order.OrderListActivity;
import com.centaurstech.sdk.databinding.ActivityListBinding;
import com.centaurstech.sdk.databinding.ItemTitleBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Leon(黄长亮)
 * @describe 页面索引
 * @date 2019/6/22
 */
public class PageIndexActivity extends BaseActivity {

    ActivityListBinding mListBinding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mListBinding = DataBindingUtil.setContentView(this, R.layout.activity_list);
        title.add("登录");
        title.add("订单列表");
        title.add("设备信息");
        title.add("重新激活");
    }

    private List<String> title = new ArrayList<>();

    private RecyclerView.Adapter mAdapter = new RecyclerView.Adapter() {
        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            return new IndexViewHolder(View.inflate(viewGroup.getContext(), R.layout.item_title, null));
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
            IndexViewHolder indexViewHolder = (IndexViewHolder) viewHolder;
            indexViewHolder.mTitleBinding.tvTitle.setText(title.get(i));
            indexViewHolder.mTitleBinding.tvTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (i) {
                        case 0:
                            launchActivity(LoginActivity.class);
                            break;
                        case 1:
                            launchActivity(OrderListActivity.class);
                            break;
                        case 2:
                            launchActivity(DeviceInfoActivity.class);
                            break;
                        case 3:
                            QiWu.init(PageIndexActivity.this, "incarcloud-watch-sdk-test",
                                    "829f2cc64d4ecaf7724e8bb2dd107849",
                                    "72ede0fdbf8dbe85fe4d09dca64e7fd4",
                                    "59166e6874bf35e7e65dcfd23b68128f");
                            break;
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return title.size();
        }
    };


    private class IndexViewHolder extends RecyclerView.ViewHolder {

        public ItemTitleBinding mTitleBinding;

        public IndexViewHolder(@NonNull View itemView) {
            super(itemView);
            mTitleBinding = DataBindingUtil.bind(itemView);
        }
    }

}
