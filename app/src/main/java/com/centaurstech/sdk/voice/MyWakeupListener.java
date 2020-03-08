package com.centaurstech.sdk.voice;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.PowerManager;

import com.centaurstech.qiwu.common.Const;
import com.centaurstech.sdk.SDKApplication;
import com.centaurstech.qiwu.entity.EventBusEntity;
import com.centaurstech.voice.QiWuVoice;
import com.centaurstech.voice.WakeupListener;
import com.centaurstech.qiwu.QiWuPlayer;
import com.centaurstech.qiwu.utils.LogUtils;

import org.greenrobot.eventbus.EventBus;

/**
 * @author Leon(黄长亮)
 * @describe TODO
 * @date 2019/6/25
 */
public class MyWakeupListener extends WakeupListener {

    @Override
    public void onWakeUp(String word) {
        LogUtils.sf(word);
        QiWuVoice.getTTS().stop();
        QiWuPlayer.getInstance().pause();
        PowerManager pm = (PowerManager) SDKApplication.getInstance().getSystemService(Context.POWER_SERVICE);
        if (pm != null && !pm.isScreenOn()) {
            @SuppressLint("InvalidWakeLockTag") PowerManager.WakeLock wakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "tag");
            if (wakeLock != null) {
                wakeLock.acquire(10 * 60 * 1000L /*10 minutes*/);
                wakeLock.release();
            }
        }
        SDKApplication.returnMainActivity();
        EventBus.getDefault().post(new EventBusEntity(Const.Instruct.START_RECORD,true));
    }

    @Override
    public void onDormant() {

    }

}
