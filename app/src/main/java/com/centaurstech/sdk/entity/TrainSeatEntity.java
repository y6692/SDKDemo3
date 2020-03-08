package com.centaurstech.sdk.entity;

import android.widget.ImageView;

import java.io.Serializable;

/**
 * @Author: 樊德鹏
 * 时   间:2019/7/25
 * 简   述:<功能描述>
 */
public class TrainSeatEntity implements Serializable {

    public TrainSeatEntity(ImageView checkBox, String seatNo, long timer, int position) {
        this.checkBox = checkBox;
        this.seatNo = seatNo;
        this.timer = timer;
        this.position = position;
    }

    private ImageView checkBox;
    private String seatNo;
    private long timer;
    private int position;

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public long getTimer() {
        return timer;
    }

    public void setTimer(long timer) {
        this.timer = timer;
    }

    public ImageView getCheckBox() {
        return checkBox;
    }

    public void setCheckBox(ImageView checkBox) {
        this.checkBox = checkBox;
    }

    public String getSeatNo() {
        return seatNo;
    }

    public void setSeatNo(String seatNo) {
        this.seatNo = seatNo;
    }
}
