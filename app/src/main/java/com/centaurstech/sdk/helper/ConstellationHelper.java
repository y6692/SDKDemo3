package com.centaurstech.sdk.helper;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.centaurstech.qiwu.QiWuAPI;
import com.centaurstech.qiwu.callback.APICallback;
import com.centaurstech.qiwu.callback.AddressSelectCallback;
import com.centaurstech.qiwu.db.HoroscopeDBManager;
import com.centaurstech.qiwu.entity.APIEntity;
import com.centaurstech.qiwu.entity.Address2Entity;
import com.centaurstech.qiwu.entity.HoroscopeEntity;
import com.centaurstech.qiwu.entity.MsgEntity;
import com.centaurstech.qiwu.entity.OrderHoroscopePairEntity;
import com.centaurstech.qiwu.manager.ParamsManager;
import com.centaurstech.qiwu.utils.DateUtils;
import com.centaurstech.qiwu.utils.GsonUtils;
import com.centaurstech.qiwu.utils.SPUtils;
import com.centaurstech.qiwu.utils.UIUtils;
import com.centaurstech.sdk.R;
import com.centaurstech.sdk.activity.BaseActivity;
import com.centaurstech.sdk.databinding.LayoutBirthdayBinding;
import com.centaurstech.sdk.databinding.LayoutCityChooseBinding;
import com.centaurstech.sdk.databinding.LayoutCommoditySellPictureBinding;
import com.centaurstech.sdk.utils.ToastUtils;
import com.centaurstech.sdk.utils.ViewUtils;
import com.centaurstech.sdk.view.GregorianLunarCalendarView;
import com.centaurstech.sdk.view.PopDialog;
import com.google.gson.reflect.TypeToken;
import com.qiwu.ui.dialog.AroundDialog;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.UUID;

import cn.carbswang.android.numberpickerview.library.NumberPickerView;

/**
 * @author Leon(黄长亮)
 * @describe 星座助手
 * @date 2019/4/26
 */
public class ConstellationHelper {

    public static final String UID = UUID.randomUUID().toString();

    public PopDialog mPopDialog;

    public void showBirthdayDialog(BaseActivity activity, final TextView textView, final MsgEntity msgEntity, final boolean own) {
        try {
            View view = View.inflate(activity, R.layout.layout_birthday, null);
            final LayoutBirthdayBinding birthBinding = DataBindingUtil.bind(view);
            assert birthBinding != null;
            birthBinding.calendarView.init();
            int year, month, day;
            if ((own ? msgEntity.getData().getHoroscope().getOwnBirthday() : msgEntity.getData().getHoroscope().getPartnerBirthday()) == 0) {
                year = 1999;
                month = 12;
                day = 31;
            } else {
                Calendar instance = Calendar.getInstance();
                instance.setTimeInMillis(own ? msgEntity.getData().getHoroscope().getOwnBirthday() : msgEntity.getData().getHoroscope().getPartnerBirthday());
                year = instance.get(Calendar.YEAR);
                month = instance.get(Calendar.MONTH) + 1;
                day = instance.get(Calendar.DAY_OF_MONTH);
            }

            if (year > 5204) {
                year = 1999;
            }

            if (month > 12) {
                month = 12;
            }

            if (day > 31) {
                day = 28;
            }

            birthBinding.calendarView.getNumberPickerYear().setMaxValue(Integer.valueOf(DateUtils.timestamp2String(System.currentTimeMillis(), "yyyy")));
            birthBinding.calendarView.getNumberPickerYear().setValue(year);
            birthBinding.calendarView.getNumberPickerMonth().setValue(month);
            birthBinding.calendarView.getNumberPickerDay().setMaxValue(31);
            birthBinding.calendarView.getNumberPickerDay().setValue(day);
            final AroundDialog aroundDialog = AroundDialog.create(activity, R.style.DialogInOutBottom);
            aroundDialog
                    .setContent(view)
                    .setHideAllButton()
                    .setGravity(Gravity.BOTTOM)
                    .setCanceledTouchOutside(false)
                    .setOnViewClickListener(new AroundDialog.OnViewClickListener() {
                        @Override
                        public void onClick(View view, boolean isConfirm) {

                        }
                    });
            birthBinding.tvCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    aroundDialog.dismiss();
                }
            });
            birthBinding.calendarView.setOnDateChangedListener(new GregorianLunarCalendarView.OnDateChangedListener() {
                @Override
                public void onDateChanged(GregorianLunarCalendarView.CalendarData calendarData) {
                    if (calendarData.getCalendar().getTimeInMillis() > System.currentTimeMillis()) {
                        birthBinding.calendarView.getNumberPickerMonth().setValue(Integer.valueOf(DateUtils.timestamp2String(System.currentTimeMillis(), "M")));
                        birthBinding.calendarView.getNumberPickerDay().setValue(Integer.valueOf(DateUtils.timestamp2String(System.currentTimeMillis(), "d")));
                    }
                }
            });
            birthBinding.tvConfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    long timeMillis = birthBinding.calendarView.getCalendarData().getCalendar().getTimeInMillis();
                    if (timeMillis > System.currentTimeMillis()) {
                        timeMillis = System.currentTimeMillis();
                    }
                    String newBirthDay = DateUtils.timestamp2String(timeMillis, "yyyy.MM.dd");
                    textView.setText(newBirthDay);
                    HoroscopeEntity constellation = new HoroscopeEntity();
                    if (own) {
                        msgEntity.getData().getHoroscope().setOwnBirthday(timeMillis);
                        constellation.setOwnBirthday(timeMillis);
                    } else {
                        msgEntity.getData().getHoroscope().setPartnerBirthday(timeMillis);
                        constellation.setPartnerBirthday(timeMillis);
                    }
                    HoroscopeDBManager.getInstance().save(msgEntity.getData().getHoroscope(), msgEntity.get_ID());
                    aroundDialog.dismiss();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showAddressDialog(final BaseActivity activity, final Address2Entity ad, final AddressSelectCallback callback) {
        String addressList = SPUtils.getString("AddressList", "");
        if (TextUtils.isEmpty(addressList)) {
            QiWuAPI.horoscope.getAddress(new APICallback<APIEntity<ArrayList<Address2Entity>>>() {
                @Override
                public void onSuccess(APIEntity<ArrayList<Address2Entity>> response) {
                    showAddressSecondDialog(activity, response.getData(), ad, callback);
                    SPUtils.put("AddressList", GsonUtils.toJson(response));
                }
            });
        } else {
            ArrayList<Address2Entity> address = GsonUtils.fromJson(addressList, new TypeToken<ArrayList<Address2Entity>>() {
            }.getType());
            showAddressSecondDialog(activity, address, ad, callback);
        }
    }

    public void showAddressSecondDialog(final BaseActivity activity, final ArrayList<Address2Entity> address, final Address2Entity ad, final AddressSelectCallback addressCallback) {
        try {
            View view = View.inflate(activity, R.layout.layout_city_choose, null);
            final LayoutCityChooseBinding binding = DataBindingUtil.bind(view);
            int Pindex = 0;
            int Cindex = 0;
            final String[] provinces = new String[address.size()];
            for (int i = 0; i < address.size(); i++) {
                provinces[i] = address.get(i).getProvince();
                if (ad.getProvince().equals(address.get(i).getProvince())) {
                    Pindex = i;
                }
            }
            mProvince = provinces[Pindex];
            Address2Entity address2 = address.get(Pindex);
            String[] citys = new String[address2.getCitys().size()];
            for (int x = 0; x < address2.getCitys().size(); x++) {
                citys[x] = address2.getCitys().get(x);
                if (ad.getCity().equals(address2.getCitys().get(x))) {
                    Cindex = x;
                }
            }
            mCity = citys[Cindex];
            assert binding != null;
            binding.province.refreshByNewDisplayedValues(provinces);
            binding.city.refreshByNewDisplayedValues(citys);
            binding.province.setValue(Pindex);
            binding.city.setValue(Cindex);
            final AroundDialog aroundDialog = AroundDialog.create(activity, R.style.DialogInOutBottom);
            binding.province.setOnValueChangedListener(new NumberPickerView.OnValueChangeListener() {
                @Override
                public void onValueChange(NumberPickerView picker, int oldVal, int newVal) {
                    Address2Entity address2 = address.get(picker.getValue());
                    String[] citys = new String[address2.getCitys().size()];
                    for (int x = 0; x < address2.getCitys().size(); x++) {
                        citys[x] = address2.getCitys().get(x);
                    }
                    binding.city.refreshByNewDisplayedValues(citys);
                    mProvince = picker.getContentByCurrValue();
                    if (citys.length > 0) {
                        mCity = citys[0];
                    }
                }
            });
            binding.city.setOnValueChangedListener(new NumberPickerView.OnValueChangeListener() {
                @Override
                public void onValueChange(NumberPickerView picker, int oldVal, int newVal) {
                    mCity = picker.getContentByCurrValue();
                }
            });
            binding.tvCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    aroundDialog.dismiss();
                }
            });
            binding.tvConfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    aroundDialog.dismiss();
                    ad.setProvince(mProvince);
                    ad.setCity(mCity);
                    if (addressCallback != null) {
                        addressCallback.callback(ad);
                    }
                }
            });
            aroundDialog
                    .setContent(view)
                    .setHideAllButton()
                    .setGravity(Gravity.BOTTOM)
                    .setCanceledTouchOutside(false)
                    .setOnViewClickListener(new AroundDialog.OnViewClickListener() {
                        @Override
                        public void onClick(View view, boolean isConfirm) {

                        }
                    });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void startBuy(Context context, HoroscopeEntity horoscopeEntity) {
        OrderHoroscopePairEntity pairRequest = new OrderHoroscopePairEntity();
        pairRequest.setCreatePairOrders(new ArrayList<>());

        OrderHoroscopePairEntity.CreatePairOrdersBean pair1 = new
                OrderHoroscopePairEntity.CreatePairOrdersBean();
        pair1.setBirth(DateUtils.timestamp2String(horoscopeEntity.getOwnBirthday(), "yyyy-MM-dd"));
        pair1.setGender(horoscopeEntity.getOwnGender().equals("男") ? 1 : 0);
        OrderHoroscopePairEntity.CreatePairOrdersBean pair2 = new
                OrderHoroscopePairEntity.CreatePairOrdersBean();
        pair2.setBirth(DateUtils.timestamp2String(horoscopeEntity.getPartnerBirthday(), "yyyy-MM-dd"));
        pair2.setGender(horoscopeEntity.getPartnerGender().equals("男") ? 1 : 0);
        pairRequest.getCreatePairOrders().add(pair1);
        pairRequest.getCreatePairOrders().add(pair2);
        showPairSellDialog((BaseActivity) context, horoscopeEntity, pairRequest);
    }

    public void showPairSellDialog(final BaseActivity activity, HoroscopeEntity horoscopeEntity, final OrderHoroscopePairEntity orderHoroscopePairEntity) {
        if (System.currentTimeMillis() - sellTimestamp < 1000) return;
        sellTimestamp = System.currentTimeMillis();

        QiWuAPI.horoscope.getLoversDiscHistory(horoscopeEntity, new APICallback<APIEntity<String>>() {
            @Override
            public void onSuccess(APIEntity<String> response) {
                try {
                    switch (response.getRetcode()) {
                        case 0:
                            if (TextUtils.isEmpty(response.getPayload())) {
                                showPairSellDialogSecond(activity, orderHoroscopePairEntity);
                            } else {
                                ToastUtils.show("暂未开放订单功能如需接入，请联系齐悟");
                            }
                            break;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void showPairSellDialogSecond(final BaseActivity activity, final OrderHoroscopePairEntity horoscopeEntity) {
        View contentView = View.inflate(activity, R.layout.layout_commodity_sell_picture, null);
        LayoutCommoditySellPictureBinding binding = DataBindingUtil.bind(contentView);
        mPopDialog = PopDialog.create(activity)
                .setContent(contentView)
                .setBackgroundTransparency()
                .setCanceledTouchOutside(false)
                .setMargin(24)
                .setHideAllButton();
        mPopDialog.show();
        assert binding != null;

        if (horoscopeEntity.getCreatePairOrders().size() == 1) {
            // TODO 用户可自定义图片
            ViewUtils.loadImage(binding.sdvImage, R.mipmap.iv_default_photo);
            ViewUtils.loadImage(binding.sdvPriceBg, R.mipmap.iv_default_photo);
            binding.tvPrice.setTextColor(UIUtils.getColor(R.color.color915013));
        } else {
            // TODO 用户可自定义图片
            ViewUtils.loadImage(binding.sdvImage, R.mipmap.iv_default_photo);
            ViewUtils.loadImage(binding.sdvPriceBg, R.mipmap.iv_default_photo);
            binding.tvPrice.setTextColor(UIUtils.getColor(R.color.colorB33B49));
        }
        binding.tvPrice.setText(UIUtils.getString(R.string.money_sign) + " " + 38.8);
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
    }

    public long sellTimestamp = 0;


    private String mProvince, mCity;

    public boolean isExpire(MsgEntity itemData) {
        return isExpire(itemData, UIUtils.getString(R.string.taxi_invalid));
    }

    private boolean isExpire(MsgEntity itemData, String text) {
        if (itemData.getExpire() != 0) {
            if (!TextUtils.isEmpty(text))
                ToastUtils.show(text);
        }
        return itemData.getExpire() != 0;
    }

}
