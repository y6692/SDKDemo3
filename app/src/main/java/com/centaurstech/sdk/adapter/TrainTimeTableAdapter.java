package com.centaurstech.sdk.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.widget.TextView;

import com.centaurstech.qiwu.entity.TrainTimeTableEntity;
import com.centaurstech.qiwu.utils.UIUtils;
import com.centaurstech.sdk.R;
import com.centaurstech.sdk.databinding.ItemTrainTimeBinding;
import com.qiwu.ui.view.adapter.DataBindRecyclerViewAdapter;

/**
 * @author Leon(黄长亮)
 * @describe 火车时刻表
 * @date 2018/9/28
 */

public class TrainTimeTableAdapter extends DataBindRecyclerViewAdapter<TrainTimeTableEntity, ItemTrainTimeBinding> {


    public TrainTimeTableAdapter(Context context) {
        super(context);
    }

    @Override
    public int getItemViewType(int position) {
        return R.layout.item_train_time;
    }

    @Override
    public void onBindHolder(ItemTrainTimeBinding binding, TrainTimeTableEntity itemData, int position) {
        switch (position){
            case 0:
                binding.tvText1.setText("车站");
                binding.tvText2.setText("到达");
                binding.tvText3.setText("发车");
                binding.tvText4.setText("停留");
                break;
            default:
                binding.tvText1.setText(itemData.getStation());
                setText(binding.tvText2,itemData.getArrivalTime());
                setText(binding.tvText3,itemData.getDepartureTime());
                setText(binding.tvText4,itemData.getStayTimeSpan()+ UIUtils.getString(R.string.minute));

                if (itemData.getValid() == 0) {
                    binding.tvText1.setTextColor(UIUtils.getColor(R.color.colorB3B3B3));
                    binding.tvText2.setTextColor(UIUtils.getColor(R.color.colorB3B3B3));
                    binding.tvText3.setTextColor(UIUtils.getColor(R.color.colorB3B3B3));
                    binding.tvText4.setTextColor(UIUtils.getColor(R.color.colorB3B3B3));
                } else {
                    binding.tvText1.setTextColor(UIUtils.getColor(R.color.color292929));
                    binding.tvText2.setTextColor(UIUtils.getColor(R.color.color292929));
                    binding.tvText3.setTextColor(UIUtils.getColor(R.color.color292929));
                    binding.tvText4.setTextColor(UIUtils.getColor(R.color.color292929));
                }
                break;
        }
    }


    private void setText(TextView tv, String text){
        if (!TextUtils.isEmpty(text)){
            tv.setText(text.contains("----")?"----":text);
        }else {
            tv.setText("");
        }
    }
}
