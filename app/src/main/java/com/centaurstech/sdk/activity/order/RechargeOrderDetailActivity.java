package com.centaurstech.sdk.activity.order;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.centaurstech.qiwu.QiWuAPI;
import com.centaurstech.qiwu.callback.APICallback;
import com.centaurstech.qiwu.entity.APIEntity;
import com.centaurstech.qiwu.entity.OrderPhoneRechargeEntity;
import com.centaurstech.qiwu.utils.UIUtils;
import com.centaurstech.sdk.activity.order.OrderDetailActivity;

/**
 * @author Leon(黄长亮)
 * @describe 酒店
 * @date 2019/6/26
 */
public class RechargeOrderDetailActivity extends OrderDetailActivity {

    OrderPhoneRechargeEntity mOrder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
    }

    private void initData() {
        QiWuAPI.recharge.getOrder(getIntent().getStringExtra("id"), new APICallback<APIEntity<OrderPhoneRechargeEntity>>() {
            @Override
            public void onSuccess(APIEntity<OrderPhoneRechargeEntity> response) {
                if (response.isSuccess()){
                    mOrder = response.getData();
                }
            }
        });
    }


    @Override
    public void deleteOrder() {
        QiWuAPI.recharge.delete(getIntent().getStringExtra("id"), new APICallback<APIEntity<String>>() {
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
        QiWuAPI.recharge.cancel(getIntent().getStringExtra("id"), new APICallback<APIEntity<String>>() {
            @Override
            public void onSuccess(APIEntity<String> stringAPIEntity) {
                if (stringAPIEntity.isSuccess()){
                    UIUtils.showToast("取消成功");
                }
            }
        });
    }

}
