package com.centaurstech.sdk.activity.order;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.centaurstech.sdk.activity.order.OrderDetailActivity;
import com.centaurstech.voice.QiWuVoice;
import com.centaurstech.qiwu.QiWuAPI;
import com.centaurstech.qiwu.callback.APICallback;
import com.centaurstech.qiwu.entity.APIEntity;
import com.centaurstech.qiwu.entity.LeaveAloneReportEntity;
import com.centaurstech.qiwu.entity.LoversDiscEntity;
import com.centaurstech.qiwu.utils.UIUtils;

import java.util.ArrayList;

/**
 * @author Leon(黄长亮)
 * @describe 快速脱单
 * @date 2019/6/26
 */
public class HoroscopeLeaveAloneOrderDetailActivity extends OrderDetailActivity {

    LoversDiscEntity mOrder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
    }

    private void initData() {
        QiWuAPI.horoscope.getLoversDiscOrder(mOrderId, new APICallback<APIEntity<LoversDiscEntity>>() {
            @Override
            public void onSuccess(APIEntity<LoversDiscEntity> response) {
                if (response.isSuccess()){
                    mOrder = response.getData();
                }
            }
        });
    }


    @Override
    public void deleteOrder() {
        QiWuAPI.horoscope.deleteLoversDisc(mOrderId, new APICallback<APIEntity<String>>() {
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
        QiWuAPI.horoscope.cancelLeaveAlone(mOrderId, new APICallback<APIEntity<String>>() {
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
        ArrayList<String> ts = new ArrayList<>() ;
        QiWuAPI.horoscope.getLeaveAloneReport(mOrderId, new APICallback<APIEntity<LeaveAloneReportEntity>>() {
            @Override
            public void onSuccess(APIEntity<LeaveAloneReportEntity> response) {
                if (response.isSuccess()){
                    for (LeaveAloneReportEntity.ItemBean itemBean : response.getData().getMarriagePartner()){
                        ts.add(itemBean.getTitle());
                        ts.add(itemBean.getContent().toLowerCase().replace("ta","他"));
                    }
                    QiWuVoice.getTTS().speechMultiple(ts);
                }
            }
        });
    }



}
