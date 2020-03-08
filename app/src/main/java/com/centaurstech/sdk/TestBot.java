package com.centaurstech.sdk;

import android.Manifest;

import com.centaurstech.qiwu.QiWuAPI;
import com.centaurstech.qiwu.QiWuBot;
import com.centaurstech.qiwu.callback.APICallback;
import com.centaurstech.qiwu.common.MsgType;
import com.centaurstech.qiwu.entity.FlightInfoEntity;
import com.centaurstech.qiwu.entity.MsgEntity;
import com.centaurstech.qiwu.entity.APIEntity;
import com.centaurstech.qiwu.listener.BotMessageListener;
import com.centaurstech.qiwu.utils.GsonUtils;
import com.centaurstech.qiwu.utils.LogUtils;
import com.qw.soul.permission.SoulPermission;
import com.qw.soul.permission.bean.Permission;
import com.qw.soul.permission.bean.Permissions;
import com.qw.soul.permission.callbcak.CheckRequestPermissionsListener;

import java.util.ArrayList;

import okhttp3.Response;

/**
 * @author Leon(黄长亮)
 * @describe bot测试
 * @date 2019/6/6
 */
public enum  TestBot {

    INSTANCE;

    public void initListener(){
        QiWuBot.setBotMessageListener(new BotMessageListener() {
            @Override
            public void onBefore(MsgEntity entity) {
                LogUtils.sf("onBefore:"+ GsonUtils.toJson(entity));
            }

            @Override
            public void onError(int code, String sn) {
                LogUtils.sf("onError:"+code);
            }

            @Override
            public void onSuccess(MsgEntity entity, String data) {
                if (entity.isOmit())return;
                LogUtils.sf("onSuccess:"+GsonUtils.toJson(entity));
                if (entity.isSuccess()){
                    switch (entity.getMsgType()){
                        case MsgType.CALL:
                            break;
                        case MsgType.PHONE_RECHARGE:
                            break;
                        case MsgType.FLIGHT:
                            QiWuAPI.flight.getInfo(entity.getData().getFlightTicketInfo().getFlightTickets().get(0), new APICallback<APIEntity<ArrayList<ArrayList<FlightInfoEntity>>>>() {
                                @Override
                                public void onSuccess(APIEntity<ArrayList<ArrayList<FlightInfoEntity>>> response) {
                                    LogUtils.sf(GsonUtils.toJson(response));
                                }
                            });
                            break;
                        default:
                            break;
                    }

                }


//                QiWuRobot.sendMessage("好的");
            }
        });
    }

    private void applyCallPermission(MsgEntity entity){
        SoulPermission.getInstance().checkAndRequestPermissions(
                Permissions.build(Manifest.permission.READ_CONTACTS,Manifest.permission.READ_CALL_LOG,Manifest.permission.CALL_PHONE),
                new CheckRequestPermissionsListener() {
                    @Override
                    public void onAllPermissionOk(Permission[] allPermissions) {
                        QiWuAPI.call.dial(entity,true);
                    }

                    @Override
                    public void onPermissionDenied(Permission[] refusedPermissions) {

                    }
                });
    }


}
