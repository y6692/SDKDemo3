package com.centaurstech.sdk.activity.order;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.centaurstech.sdk.activity.order.OrderDetailActivity;
import com.centaurstech.voice.QiWuVoice;
import com.centaurstech.qiwu.QiWuAPI;
import com.centaurstech.qiwu.callback.APICallback;
import com.centaurstech.qiwu.entity.APIEntity;
import com.centaurstech.qiwu.entity.FortuneEntity;
import com.centaurstech.qiwu.entity.FortuneReportEntity;
import com.centaurstech.qiwu.utils.UIUtils;

import java.util.ArrayList;

/**
 * @author Leon(黄长亮)
 * @describe 月运运势
 * @date 2019/6/26
 */
public class HoroscopeFortuneOrderDetailActivity extends OrderDetailActivity {

    FortuneEntity mOrder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
    }

    private void initData() {
        QiWuAPI.horoscope.getFortuneOrder(mOrderId, new APICallback<APIEntity<FortuneEntity>>() {
            @Override
            public void onSuccess(APIEntity<FortuneEntity> response) {
                if (response.isSuccess()){
                    mOrder = response.getData();
                }
            }
        });
    }


    @Override
    public void deleteOrder() {
        QiWuAPI.horoscope.deleteFortune(mOrderId, new APICallback<APIEntity<String>>() {
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
        QiWuAPI.horoscope.cancelFortune(mOrderId, new APICallback<APIEntity<String>>() {
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
        QiWuAPI.horoscope.getFortuneReport(mOrderId, mOrder.getChildOrderIds().split(",")[0], new APICallback<APIEntity<FortuneReportEntity>>() {
            @Override
            public void onSuccess(APIEntity<FortuneReportEntity> response) {
                ArrayList<String> ts = new ArrayList<>() ;
                int index = 0;
                for (FortuneReportEntity.LiveFortuneLeaveAloneBean.LeaveAloneRateBean fortune : response.getData().getLiveFortuneLeaveAlone().getLeaveAloneRate()){
                    String str;
                    if (index == 0){
                        str = fortune.getAmbiguousRate() +"，"
                                + fortune.getLeavealoneRate();
                    }else {
                        str = fortune.getTitle() +"，"
                                + fortune.getAmbiguousRate() +"，"
                                + fortune.getLeavealoneRate();
                    }
                    index++;
                    ts.add(str);
                    ts.add(fortune.getMessage());
                    if (!TextUtils.isEmpty(fortune.getHelpness())){
                        ts.add(fortune.getHelpness());
                    }
                    if (!TextUtils.isEmpty(fortune.getHelpless())){
                        ts.add(fortune.getHelpless());
                    }
                }
                QiWuVoice.getTTS().speechMultiple(ts);
            }
        });
    }



}
