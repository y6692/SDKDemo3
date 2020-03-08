package com.centaurstech.sdk.voice;

import com.centaurstech.qiwu.common.Const;
import com.centaurstech.qiwu.entity.EventBusEntity;
import com.centaurstech.voice.QiWuVoice;
import com.centaurstech.qiwu.QiWuPlayer;
import com.centaurstech.qiwu.common.PlayState;
import com.centaurstech.qiwu.common.PlayType;
import com.centaurstech.qiwu.listener.PlayStateListener;
import com.centaurstech.qiwu.manager.PhoneManager;
import com.centaurstech.qiwu.utils.LogUtils;

import org.greenrobot.eventbus.EventBus;

import static com.centaurstech.qiwu.common.PlayType.TONE;

/**
 * @author Leon(黄长亮)
 * @describe todo
 * @date 2019/6/25
 */
public class MyPlayStateListener implements PlayStateListener {


    @Override
    public void onState(PlayState state, int playType) {
        LogUtils.sf("PlayState:" + state + "--------- audioType:" + playType);
        switch (state) {
            case COMPLETED:
                switch (playType) {
                    case PlayType.DEFAULT:
                        EventBus.getDefault().post(new EventBusEntity(Const.Instruct.START_RECORD, false));
                        break;
                    case PlayType.COHERENT:
                        QiWuVoice.getTTS().speechMultiple();
                        break;
                    case PlayType.NONE:
                        if (QiWuPlayer.getInstance().getMusic() != null && QiWuPlayer.getInstance().getState() == PlayState.PAUSED) {
                            switch (QiWuPlayer.getInstance().getPauseType()) {
                                case NORMAL:
                                    QiWuPlayer.getInstance().play();
                                    break;
                                case MANUAL:
                                    break;
                            }
                        }
                        break;
                    case PlayType.CALL:
                        PhoneManager.getInstance().call();
                        break;
                    case PlayType.MUSIC_PLAY:
                        QiWuPlayer.getInstance().play();
                        break;
                    case TONE:
                        QiWuVoice.getASR().start();
                        break;
                }
                break;
            case STARTED:
                QiWuPlayer.getInstance().pause();
                break;
            case STOPPED:
//                switch (playType){
//                    case 0:
//                        QiWuVoice.getASR().start();
//                        break;
//                }
                break;
            case RELEASED:
                break;
        }
    }

}
