package com.centaurstech.sdk.activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.centaurstech.qiwu.QiWu;
import com.centaurstech.qiwu.QiWuAPI;
import com.centaurstech.qiwu.callback.APICallback;
import com.centaurstech.qiwu.entity.APIEntity;
import com.centaurstech.qiwu.entity.FlightInfoEntity;
import com.centaurstech.qiwu.entity.FlightTicketEntity;
import com.centaurstech.qiwu.entity.HotelEntity;
import com.centaurstech.qiwu.entity.InsureEntity;
import com.centaurstech.qiwu.entity.PassengerEntity;
import com.centaurstech.qiwu.entity.TrainTicketEntity;
import com.centaurstech.qiwu.entity.TrainTimeTableEntity;
import com.centaurstech.qiwu.utils.DateUtils;
import com.centaurstech.qiwu.utils.FileUtils;
import com.centaurstech.qiwu.utils.GsonUtils;
import com.centaurstech.sdk.R;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Leon(黄长亮)
 * @describe 下单预定
 * @date 2019/6/24
 */
public class BookingHotelActivity extends BaseActivity {

    HotelEntity mHotelEntity;
    String orderType = "";
    ArrayList<ArrayList<FlightInfoEntity>> mFlightInfoEntities;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHotelEntity = (HotelEntity) getIntent().getSerializableExtra("hotel");

        String orderData = "";

        if (mHotelEntity != null){
            orderType = "酒店";
            orderData = GsonUtils.toJson(mHotelEntity);
        }
    }
}
