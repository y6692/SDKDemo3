package com.centaurstech.sdk.listener;

import com.centaurstech.qiwu.entity.MsgEntity;

/**
 * @author Leon(黄长亮)
 * @describe 音乐删除
 * @date 2019/2/15
 */

public interface OnMusicDeleteListener {
    void onDelete(MsgEntity msgEntity);
}
