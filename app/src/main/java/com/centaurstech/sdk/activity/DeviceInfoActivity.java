package com.centaurstech.sdk.activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.centaurstech.qiwu.net.HttpParams;
import com.centaurstech.qiwu.utils.QiWuUtils;
import com.centaurstech.sdk.R;
import com.centaurstech.sdk.databinding.ActivityDeviceInfoBinding;
import com.centaurstech.sdk.utils.ConversionUtils;

/**
 * @author Leon(黄长亮)
 * @describe TODO
 * @date 2019/7/4
 */
public class DeviceInfoActivity extends BaseActivity {

    ActivityDeviceInfoBinding mBinding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_device_info);

        mBinding.iHeadLayout.tvTitle.setText("设备信息");
        mBinding.iHeadLayout.btnNavBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        HttpParams httpParams = new HttpParams()
                .put("imei", QiWuUtils.getIMEI())
                .put("sn", QiWuUtils.getSerialNumber())
                .put("mac", QiWuUtils.getMac())
                .put("androidId", QiWuUtils.getAndroidId())
                .put("hardware", QiWuUtils.getAndroidInfo());
        mBinding.tvSN.setText("sn:" + QiWuUtils.getSerialNumber());

        mBinding.tvCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ConversionUtils.copyText(QiWuUtils.getSerialNumber());
            }
        });
    }
}
