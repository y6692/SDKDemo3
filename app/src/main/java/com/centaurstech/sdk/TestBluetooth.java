package com.centaurstech.sdk;


import android.bluetooth.BluetoothDevice;
import android.os.Build;

import com.centaurstech.qiwu.QiWu;
import com.centaurstech.qiwu.utils.LogUtils;

import java.util.List;

/**
 * @author Leon(黄长亮)
 * @describe 测试蓝牙
 * @date 2019/6/6
 */
public class TestBluetooth {


    public static void connectBluetooth(){
//        BluetoothSDK.init(QiWu.getContext(),new int[]{0x11,0x16,0x20, 0x98,0x0A, 0xBF});
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
//            BleManager.getInstance().setBleConnectListener(new BleConnectListener() {
//                @Override
//                public void onLeScan(List<BluetoothDevice> devices, LeScanState state) {
//
//                }
//
//                @Override
//                public void onResult(BleCommunication bleCommunication, BleConnectState state) {
//                    LogUtils.d(bleCommunication.toString() + "----" + state);
//                }
//            });
//            BleManager.getInstance().startConnect();
//        }
    }

}
