package com.centaurstech.sdk.helper;

import android.content.Context;
import android.view.View;

import com.centaurstech.qiwu.entity.HoroscopeEntity;
import com.centaurstech.qiwu.entity.MsgEntity;
import com.centaurstech.qiwu.entity.OrderHoroscopePairEntity;
import com.centaurstech.qiwu.utils.DateUtils;
import com.centaurstech.sdk.R;
import com.centaurstech.sdk.activity.BaseActivity;
import com.centaurstech.sdk.adapter.ChatAdapter;
import com.centaurstech.sdk.databinding.ItemConstellationPairBinding;
import com.qiwu.ui.dialog.AroundDialog;

import java.util.ArrayList;

/**
 * @author Leon(黄长亮)
 * @describe 星座脱单助手
 * @date 2019/4/25
 */
public class ConstellationPairHelper extends ConstellationHelper {


    private static ConstellationPairHelper instance;

    public synchronized static ConstellationPairHelper getInstance() {
        if (instance == null) {
            instance = new ConstellationPairHelper();
        }
        return instance;
    }

    public void setData(final ItemConstellationPairBinding binding, final MsgEntity itemData, final int position, ChatAdapter adapter) {
        final HoroscopeEntity constellation = itemData.getData().getHoroscope();
        binding.tvBirthday.setText(DateUtils.timestamp2String(constellation.getOwnBirthday(), "yyyy.MM.dd"));
        setGender(binding, itemData);
        binding.ivGenderMan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isExpire(itemData)) return;
                itemData.getData().getHoroscope().setOwnGender("男");
                setGender(binding, itemData);
            }
        });
        binding.ivGenderWoman.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isExpire(itemData)) return;
                itemData.getData().getHoroscope().setOwnGender("女");
                setGender(binding, itemData);
            }
        });
        binding.cvGetPrice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isExpire(itemData)) return;
                OrderHoroscopePairEntity pairRequest = new OrderHoroscopePairEntity();
                pairRequest.setCreatePairOrders(new ArrayList<>());

                OrderHoroscopePairEntity.CreatePairOrdersBean pair = new
                        OrderHoroscopePairEntity.CreatePairOrdersBean();
                pair.setBirth(DateUtils.timestamp2String(constellation.getOwnBirthday(), "yyyy-MM-dd"));
                pair.setGender(constellation.getOwnGender().equals("男") ? 1 : 0);

                pairRequest.getCreatePairOrders().add(pair);
                showPairSellDialog((BaseActivity) v.getContext(), constellation, pairRequest);
            }
        });
        binding.llBirthDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isExpire(itemData)) return;
                showBirthdayDialog((BaseActivity) v.getContext(), binding.tvBirthday, itemData, true);
            }
        });
    }

    /***
     * 下单
     * @param context
     * @param horoscopeEntity
     */
    public void startBuy(Context context, HoroscopeEntity horoscopeEntity) {
        OrderHoroscopePairEntity pairRequest = new OrderHoroscopePairEntity();
        pairRequest.setCreatePairOrders(new ArrayList<>());

        OrderHoroscopePairEntity.CreatePairOrdersBean pair = new
                OrderHoroscopePairEntity.CreatePairOrdersBean();
        pair.setBirth(DateUtils.timestamp2String(horoscopeEntity.getOwnBirthday(), "yyyy-MM-dd"));
        pair.setGender(horoscopeEntity.getOwnGender().equals("男") ? 1 : 0);
        pairRequest.getCreatePairOrders().add(pair);
        showPairSellDialog((BaseActivity) context, horoscopeEntity, pairRequest);
    }

    private void setGender(final ItemConstellationPairBinding binding, MsgEntity itemData) {
        switch (itemData.getData().getHoroscope().getOwnGender()) {
            case "男":
                binding.ivGenderMan.setImageResource(R.mipmap.btn_profile_selected);
                binding.ivGenderWoman.setImageResource(R.mipmap.btn_profile);
                break;
            case "女":
                binding.ivGenderWoman.setImageResource(R.mipmap.btn_profile_selected);
                binding.ivGenderMan.setImageResource(R.mipmap.btn_profile);
                break;
        }
    }


    public void dismissDialog() {
        if (mPopDialog != null) {
            mPopDialog.dismiss();
        }
        AroundDialog.destroy();
    }
}
