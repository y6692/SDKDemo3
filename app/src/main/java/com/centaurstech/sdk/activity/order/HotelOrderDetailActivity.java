package com.centaurstech.sdk.activity.order;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.centaurstech.qiwu.QiWuAPI;
import com.centaurstech.qiwu.callback.APICallback;
import com.centaurstech.qiwu.entity.APIEntity;
import com.centaurstech.qiwu.entity.OrderHotelEntity;
import com.centaurstech.qiwu.utils.UIUtils;
import com.centaurstech.sdk.activity.order.OrderDetailActivity;

/**
 * @author Leon(黄长亮)
 * @describe 酒店
 * @date 2019/6/26
 */
public class HotelOrderDetailActivity extends OrderDetailActivity {

    OrderHotelEntity mOrder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
    }

    private void initData() {
        QiWuAPI.hotel.getOrder(getIntent().getStringExtra("id"), new APICallback<APIEntity<OrderHotelEntity>>() {
            @Override
            public void onSuccess(APIEntity<OrderHotelEntity> response) {
                if (response.isSuccess()){
                    mOrder = response.getData();
                }
            }
        });
    }


    @Override
    public void deleteOrder() {
        QiWuAPI.hotel.delete(getIntent().getStringExtra("id"), new APICallback<APIEntity<String>>() {
            @Override
            public void onSuccess(APIEntity<String> stringAPIEntity) {
                if (stringAPIEntity.isSuccess()){
                    UIUtils.showToast("删除成功");
                }
            }
        });
    }

    @Override
    public void cancelOrder() {
        QiWuAPI.hotel.cancel(getIntent().getStringExtra("id"), new APICallback<APIEntity<String>>() {
            @Override
            public void onSuccess(APIEntity<String> stringAPIEntity) {
                if (stringAPIEntity.isSuccess()){
                    UIUtils.showToast("取消成功");
                }
            }
        });
    }

    @Override
    public void refundOrder() {
        QiWuAPI.hotel.refund(getIntent().getStringExtra("id"), new APICallback<APIEntity<String>>() {
            @Override
            public void onSuccess(APIEntity<String> response) {
                UIUtils.showToast("退订成功");
            }
        });
    }



}
