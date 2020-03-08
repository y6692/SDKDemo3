package com.centaurstech.sdk.listener;

/**
 * @author Leon(黄长亮)
 * @describe OnAudioStatusUpdateListener
 * @date 2019/11/21
 */
public interface OnAudioStatusUpdateListener {

    /**
     * 录音中...
     *
     * @param time 录音时长
     */
    void onUpdate(byte[] bytes, long time);

    /**
     * 停止录音
     *
     * @param time     录音时长
     * @param filePath 保存路径
     */
    void onFinish(long time, String filePath);

    /**
     * 录音失败
     */
    void onError();

}
