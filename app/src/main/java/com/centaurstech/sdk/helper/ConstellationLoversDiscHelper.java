package com.centaurstech.sdk.helper;

import android.content.Context;
import android.view.View;

import com.centaurstech.qiwu.db.HoroscopeDBManager;
import com.centaurstech.qiwu.entity.HoroscopeEntity;
import com.centaurstech.qiwu.entity.MsgEntity;
import com.centaurstech.qiwu.utils.DateUtils;
import com.centaurstech.sdk.R;
import com.centaurstech.sdk.activity.BaseActivity;
import com.centaurstech.sdk.adapter.ChatAdapter;
import com.centaurstech.sdk.databinding.ItemConstellationLoversDiscBinding;
import com.centaurstech.sdk.databinding.LayoutBirthdayGenderBinding;
import com.centaurstech.sdk.utils.ToastUtils;

/**
 * @author Leon(黄长亮)
 * @describe 星座脱单助手
 * @date 2019/4/25
 */
public class ConstellationLoversDiscHelper extends ConstellationHelper {

    private static ConstellationLoversDiscHelper instance;

    public synchronized static ConstellationLoversDiscHelper getInstance() {
        if (instance == null) {
            instance = new ConstellationLoversDiscHelper();
        }
        return instance;
    }


    public void setData(final ItemConstellationLoversDiscBinding binding, final MsgEntity itemData, final int position, ChatAdapter adapter) {
        final HoroscopeEntity constellation = itemData.getData().getHoroscope();
        binding.iBirthdayGender1.tvBirthday.setText(DateUtils.timestamp2String(constellation.getOwnBirthday(), "yyyy.MM.dd"));
        binding.iBirthdayGender2.tvBirthday.setText(DateUtils.timestamp2String(constellation.getPartnerBirthday(), "yyyy.MM.dd"));
        setGender(binding.iBirthdayGender1, constellation.getOwnGender());
        setGender(binding.iBirthdayGender2, constellation.getPartnerGender());
        binding.iBirthdayGender1.llBirthDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isExpire(itemData)) return;
                showBirthdayDialog((BaseActivity) v.getContext(), binding.iBirthdayGender1.tvBirthday, itemData, true);
            }
        });
        binding.iBirthdayGender2.llBirthDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isExpire(itemData)) return;
                showBirthdayDialog((BaseActivity) v.getContext(), binding.iBirthdayGender2.tvBirthday, itemData, false);
            }
        });
        setGenderClickListener(binding.iBirthdayGender1, itemData, true);
        setGenderClickListener(binding.iBirthdayGender2, itemData, false);
        binding.cvGetPrice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtils.show("暂未开放订单功能如需接入，请联系齐悟");
                if (isExpire(itemData))return;
                startBuy(v.getContext(),constellation);
            }
        });
    }


    private void setGenderClickListener(final LayoutBirthdayGenderBinding binding, final MsgEntity itemData, final boolean own) {
        binding.ivGenderMan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isExpire(itemData)) return;
                HoroscopeEntity constellation = new HoroscopeEntity();
                if (own) {
                    itemData.getData().getHoroscope().setOwnGender("男");
                    setGender(binding, itemData.getData().getHoroscope().getOwnGender());
                    constellation.setOwnGender("男");
                } else {
                    itemData.getData().getHoroscope().setPartnerGender("男");
                    setGender(binding, itemData.getData().getHoroscope().getPartnerGender());
                    constellation.setPartnerGender("男");
                }
                HoroscopeDBManager.getInstance().save(itemData.getData().getHoroscope(), itemData.get_ID());
            }
        });
        binding.ivGenderWoman.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isExpire(itemData)) return;
                HoroscopeEntity constellation = new HoroscopeEntity();
                if (own) {
                    itemData.getData().getHoroscope().setOwnGender("女");
                    setGender(binding, itemData.getData().getHoroscope().getOwnGender());
                    constellation.setOwnGender("女");
                } else {
                    itemData.getData().getHoroscope().setPartnerGender("女");
                    setGender(binding, itemData.getData().getHoroscope().getPartnerGender());
                    constellation.setPartnerGender("女");
                }
                HoroscopeDBManager.getInstance().save(itemData.getData().getHoroscope(), itemData.get_ID());
            }
        });
    }

    private void setGender(final LayoutBirthdayGenderBinding binding, String gender) {
        switch (gender) {
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
    }

}
