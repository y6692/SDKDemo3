package com.centaurstech.sdk.voice;

import com.centaurstech.sdk.common.Const2;
import com.centaurstech.qiwu.entity.EventBusEntity;
import com.centaurstech.voice.AsrListener;
import com.centaurstech.qiwu.QiWuBot;
import com.centaurstech.qiwu.QiWuPlayer;
import com.centaurstech.qiwu.common.PlayState;
import com.centaurstech.qiwu.utils.LogUtils;
import com.centaurstech.voice.QiWuVoice;

import org.greenrobot.eventbus.EventBus;

/**
 * @author Leon(黄长亮)
 * @describe TODO
 * @date 2019/6/25
 */
public class MyAsrListener extends AsrListener {

    public void setVoiceListener(VoiceListener mVoiceListener) {
        this.mVoiceListener = mVoiceListener;
    }

    public VoiceListener mVoiceListener;

    @Override
    public void onReady() {
//        UIUtils.showToast("开始识别");
    }

    @Override
    public void onVolume(int volume) {
        mVoiceListener.volume(volume);
    }

    @Override
    public void onFinish(int errorCode, String result) {
        switch (errorCode) {
            case 0:
                QiWuBot.sendMessage(result);
//                UIUtils.showToast("结束识别");
                mVoiceListener.thick();
                break;
            default:
                LogUtils.sf(errorCode);
//                if (QiWuPlayer.getInstance().getMusic() != null && QiWuPlayer.getInstance().getState() == PlayState.PAUSED) {
//                    switch (QiWuPlayer.getInstance().getPauseType()) {
//                        case NORMAL:
//                            QiWuPlayer.getInstance().play();
//                            break;
//                        case MANUAL:
//                            break;
//                    }
//                }
                QiWuVoice.getWakeup().start();
                mVoiceListener.idle();
                EventBus.getDefault().post(new EventBusEntity(Const2.close_audio));
                break;
        }
    }

    @Override
    public void onPartial(String message) {
        LogUtils.sf("onPartial :" + message);
    }

    public interface VoiceListener {
        void volume(int volume);

        void startListen(boolean isDelayed);

        void idle();

        void thick();
    }

}
