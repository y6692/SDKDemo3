package com.centaurstech.sdk;

/**
 * @author Leon(黄长亮)
 * @describe BaseSpeechVoice
 * @date 2019/5/25
 */
public abstract class BaseSpeechVoice {

    public abstract void setMode();
    public abstract void speech(String msg,int priority);
    public abstract void stop();
    public abstract void stop(boolean result);
    public abstract void resume();
    public abstract void pause();
    public abstract void release();

}
