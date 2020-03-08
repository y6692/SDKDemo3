package com.centaurstech.sdk.helper;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.view.View;

import com.centaurstech.qiwu.QiWuAPI;
import com.centaurstech.qiwu.callback.APICallback;
import com.centaurstech.qiwu.callback.AddressSelectCallback;
import com.centaurstech.qiwu.db.HoroscopeDBManager;
import com.centaurstech.qiwu.entity.APIEntity;
import com.centaurstech.qiwu.entity.Address2Entity;
import com.centaurstech.qiwu.entity.FortuneEntity;
import com.centaurstech.qiwu.entity.FortuneHistoryEntity;
import com.centaurstech.qiwu.entity.HoroscopeEntity;
import com.centaurstech.qiwu.entity.MsgEntity;
import com.centaurstech.qiwu.utils.DateUtils;
import com.centaurstech.qiwu.utils.LogUtils;
import com.centaurstech.qiwu.utils.UIUtils;
import com.centaurstech.sdk.R;
import com.centaurstech.sdk.activity.BaseActivity;
import com.centaurstech.sdk.adapter.ChatAdapter;
import com.centaurstech.sdk.adapter.SellListAdapter;
import com.centaurstech.sdk.databinding.ItemConstellationFortuneBinding;
import com.centaurstech.sdk.databinding.LayoutCommoditySellListBinding;
import com.centaurstech.sdk.utils.ToastUtils;
import com.centaurstech.sdk.utils.ViewUtils;
import com.centaurstech.sdk.view.PopDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * @author Leon(黄长亮)
 * @describe 星座运势助手
 * @date 2019/4/25
 */
public class ConstellationFortuneHelper extends ConstellationHelper {


    private static ConstellationFortuneHelper instance;
    private PopDialog mPopDialog;

    public synchronized static ConstellationFortuneHelper getInstance() {
        if (instance == null) {
            instance = new ConstellationFortuneHelper();
        }
        return instance;
    }

    public void setData(final ItemConstellationFortuneBinding binding, final MsgEntity itemData, final int position, ChatAdapter adapter) {
        binding.tvBirthplace.setText(itemData.getData().getHoroscope().getOwnBirthCity());
        binding.tvPresentAddress.setText(itemData.getData().getHoroscope().getOwnLivingCity());
        binding.iBirthdayGender.tvBirthday.setText(DateUtils.timestamp2String(itemData.getData().getHoroscope().getOwnBirthday(), "yyyy.MM.dd"));
        setGender(binding, itemData);

        binding.cvGetPrice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isExpire(itemData)) return;
                showSellListDialog((BaseActivity) v.getContext(), itemData.getData().getHoroscope(), 1);
            }
        });
        binding.iBirthdayGender.llBirthDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isExpire(itemData)) return;
                showBirthdayDialog((BaseActivity) v.getContext(), binding.iBirthdayGender.tvBirthday, itemData, true);
            }
        });
        binding.iBirthdayGender.ivGenderMan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isExpire(itemData)) return;
                itemData.getData().getHoroscope().setOwnGender("男");
                setGender(binding, itemData);
                HoroscopeDBManager.getInstance().save(itemData.getData().getHoroscope(), itemData.get_ID());
                HoroscopeEntity constellation = new HoroscopeEntity();
                constellation.setOwnGender("男");
            }
        });
        binding.iBirthdayGender.ivGenderWoman.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isExpire(itemData)) return;
                itemData.getData().getHoroscope().setOwnGender("女");
                setGender(binding, itemData);
                HoroscopeDBManager.getInstance().save(itemData.getData().getHoroscope(), itemData.get_ID());
                HoroscopeEntity constellation = new HoroscopeEntity();
                constellation.setOwnGender("女");
            }
        });
        binding.llBirthAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isExpire(itemData)) return;
                Address2Entity ad = new Address2Entity(itemData.getData().getHoroscope().getOwnBirthProvince(), itemData.getData().getHoroscope().getOwnBirthCity());
                showAddressDialog((BaseActivity) v.getContext(), ad, new AddressSelectCallback() {
                    @Override
                    public void callback(Address2Entity address) {
                        binding.tvBirthplace.setText(address.getCity());
                        itemData.getData().getHoroscope().setOwnBirthCity(address.getCity());
                        itemData.getData().getHoroscope().setOwnBirthProvince(address.getProvince());
                        HoroscopeDBManager.getInstance().save(itemData.getData().getHoroscope(), itemData.get_ID());
                        HoroscopeEntity constellation = new HoroscopeEntity();
                        constellation.setOwnBirthProvince(address.getCity());
                        constellation.setOwnBirthCity(address.getProvince());
                    }
                });
            }
        });
        binding.llPresentAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isExpire(itemData)) return;
                Address2Entity ad = new Address2Entity(itemData.getData().getHoroscope().getOwnLivingProvince(), itemData.getData().getHoroscope().getOwnLivingCity());
                showAddressDialog((BaseActivity) v.getContext(), ad, new AddressSelectCallback() {
                    @Override
                    public void callback(Address2Entity address) {
                        binding.tvPresentAddress.setText(address.getCity());
                        itemData.getData().getHoroscope().setOwnLivingCity(address.getCity());
                        itemData.getData().getHoroscope().setOwnLivingProvince(address.getProvince());
                        HoroscopeDBManager.getInstance().save(itemData.getData().getHoroscope(), itemData.get_ID());
                        HoroscopeEntity constellation = new HoroscopeEntity();
                        constellation.setOwnLivingProvince(address.getCity());
                        constellation.setOwnLivingCity(address.getProvince());
                    }
                });
            }
        });
    }

    private void setGender(final ItemConstellationFortuneBinding binding, MsgEntity itemData) {
        switch (itemData.getData().getHoroscope().getOwnGender()) {
            case "男":
                binding.iBirthdayGender.ivGenderMan.setImageResource(R.mipmap.btn_profile_selected);
                binding.iBirthdayGender.ivGenderWoman.setImageResource(R.mipmap.btn_profile);
                break;
            case "女":
                binding.iBirthdayGender.ivGenderWoman.setImageResource(R.mipmap.btn_profile_selected);
                binding.iBirthdayGender.ivGenderMan.setImageResource(R.mipmap.btn_profile);
                break;
        }
    }

    private String fortuneStartDate;
    private String fortuneEndDate;

    public void showSellListDialog(final BaseActivity activity, final HoroscopeEntity constellation, int fortuneType) {
        if (System.currentTimeMillis() - sellTimestamp < 1000) return;
        sellTimestamp = System.currentTimeMillis();
        QiWuAPI.horoscope.getFortuneHistory(constellation, fortuneType, new APICallback<APIEntity<FortuneHistoryEntity>>() {
            @Override
            public void onSuccess(APIEntity<FortuneHistoryEntity> response) {
                try {
                    if (TextUtils.isEmpty(response.getPayload())) {
                        showSellListDialogSecond(activity, constellation, "", "");
                    } else {
                        FortuneHistoryEntity entity = response.getData();
                        String fStartDate = entity.getStartDate();
                        String fEndDate = entity.getEndDate();
                        final String orderId = entity.getOrderId();
                        if (TextUtils.isEmpty(orderId)) {
                            if (TextUtils.isEmpty(fEndDate)) {
                                showSellListDialogSecond(activity, constellation, DateUtils.date2Date(fStartDate, "yyyy-MM-dd", "yyyy.MM.dd"), "");
                            } else {
                                showSellListDialogSecond(activity, constellation, DateUtils.date2Date(fStartDate, "yyyy-MM-dd", "yyyy.MM.dd"), DateUtils.date2Date(fEndDate, "yyyy-MM-dd", "yyyy.MM.dd"));
                            }
                        } else {

                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void showSellListDialogSecond(final BaseActivity activity, final HoroscopeEntity constellation, final String sd, final String ed) {
        View contentView = View.inflate(activity, R.layout.layout_commodity_sell_list, null);
        final LayoutCommoditySellListBinding binding = DataBindingUtil.bind(contentView);
        mPopDialog = PopDialog.create(activity)
                .setContent(contentView)
                .setBackgroundTransparency()
                .setCanceledTouchOutside(false)
                .setMargin(24)
                .setHideAllButton();
        mPopDialog.show();
        assert binding != null;
        final SellListAdapter adapter = new SellListAdapter();
        ViewUtils.loadImage(binding.sdvImage, R.mipmap.iv_default_photo);
        StaggeredGridLayoutManager staggered = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        binding.rvSellList.setLayoutManager(staggered);
        binding.rvSellList.setAdapter(adapter);
        ArrayList<String> data = new ArrayList<>();
        data.add("30");
        data.add("60");
        data.add("90");
        adapter.setData(data);
        if (!TextUtils.isEmpty(sd) && !TextUtils.isEmpty(ed)) {
            binding.tvExplainHint.setText("您已订阅" + sd + " - " + ed + "运势");
            binding.tvExplainHint.setVisibility(View.VISIBLE);
            setSubscribeDate(binding, adapter.getPosition(), DateUtils.timestamp2String((DateUtils.date2Timestamp(ed, "yyyy.MM.dd") + (3600 * 24 * 1000)), "yyyy.MM.dd"));
        } else {
            setSubscribeDate(binding, adapter.getPosition(), sd);
        }
        adapter.setOnItemClick(new SellListAdapter.onItemClickListener() {
            @Override
            public void onItemCLick(int position) {
                if (!TextUtils.isEmpty(sd) && !TextUtils.isEmpty(ed)) {
                    setSubscribeDate(binding, position, DateUtils.timestamp2String((DateUtils.date2Timestamp(ed, "yyyy.MM.dd") + (3600 * 24 * 1000)), "yyyy.MM.dd"));
                } else {
                    setSubscribeDate(binding, position, sd);
                }
            }
        });
        binding.cvImmediatelyTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopDialog.dismiss();
                ToastUtils.show("暂未开放订单功能如需接入，请联系齐悟");
            }
        });
        binding.ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopDialog.dismiss();
            }
        });
        binding.cvImmediatelyTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopDialog.dismiss();
                ToastUtils.show("暂未开放订单功能如需接入，请联系齐悟");
            }
        });
    }

    private void setSubscribeDate(LayoutCommoditySellListBinding binding, int position, String startDate) {
        fortuneStartDate = startDate;
        long timestamp = 3600 * 24 * 30 * (position + 1);
        fortuneEndDate = DateUtils.timestamp2String((DateUtils.date2Timestamp(startDate, "yyyy.MM.dd") + (timestamp * 1000)), "yyyy.MM.dd");
        binding.tvExplain.setText(UIUtils.getString(R.string.subscribe_date) + "：" + fortuneStartDate + " - " + fortuneEndDate);
    }


    public void dismissDialog() {
        if (mPopDialog != null) {
            mPopDialog.dismiss();
        }
    }
}
