package com.centaurstech.sdk.activity.order;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.centaurstech.sdk.activity.order.OrderDetailActivity;
import com.centaurstech.voice.QiWuVoice;
import com.centaurstech.qiwu.QiWuAPI;
import com.centaurstech.qiwu.callback.APICallback;
import com.centaurstech.qiwu.entity.APIEntity;
import com.centaurstech.qiwu.entity.LoversDiscEntity;
import com.centaurstech.qiwu.entity.LoversDiscReportEntity;
import com.centaurstech.qiwu.utils.UIUtils;

import java.util.ArrayList;

/**
 * @author Leon(黄长亮)
 * @describe 情侣合盘
 * @date 2019/6/26
 */
public class HoroscopeLoversDiscOrderDetailActivity extends OrderDetailActivity {

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
        QiWuAPI.horoscope.cancelLoversDisc(mOrderId, new APICallback<APIEntity<String>>() {
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
        QiWuAPI.horoscope.getLoversDiscReport(mOrderId, new APICallback<APIEntity<LoversDiscReportEntity>>() {
            @Override
            public void onSuccess(APIEntity<LoversDiscReportEntity> response) {
                if (response.isSuccess()){
                    ArrayList<String> ts = new ArrayList<>() ;
                    LoversDiscReportEntity loversReport = response.getData();
                    ts.add("情侣合盘运势报告");
                    ts.add(loversReport.getLoveDepth().getModuleTitle().toLowerCase().replace("ta","他"));
                    ts.add(loversReport.getLoveDepth().getMsg());
                    ts.add(loversReport.getBroken().getModuleTitle());
                    StringBuilder broken = new StringBuilder();
                    for (String b : loversReport.getBroken().getBrokens()){
                        broken.append(b).append("\n");
                    }
                    ts.add(broken.toString());
                    ts.add(loversReport.getFriendRelation().getModuleTitle());
                    ts.add(loversReport.getFriendRelation().getWorthMessageOne().getMessage().toLowerCase().replace("ta","他")
                            +"。"+loversReport.getFriendRelation().getWorthMessageTwo().getMessage().toLowerCase().replace("ta","他"));
                    ts.add(loversReport.getEmotionalFortune().getModuleTitle());
                    for (LoversDiscReportEntity.EmotionalFortuneBean.EmotionalFortuneChildBean fortuneBean : response.getData()
                            .getEmotionalFortune().getEmotionalFortune()){
                        ts.add(fortuneBean.getMonth() + "月情感指数" + fortuneBean.getNumber());
                        ts.add(fortuneBean.getMessage());
                    }
                    QiWuVoice.getTTS().speechMultiple(ts);
                }
            }
        });
    }

}
