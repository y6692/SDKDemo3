package com.centaurstech.sdk.entity;


import java.io.Serializable;


/**
 * @author Leon(黄长亮)
 * @describe 飞机票价格明细
 * @date 2018/7/19
 */

public class AirPriceDetailsEntity implements Serializable {

    private static final long serialVersionUID = -6511591821842749262L;

    private String text;
    private String tripType;
    private int count;
    private int type;
    private String price;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getTripType() {
        return tripType;
    }

    public void setTripType(String tripType) {
        this.tripType = tripType;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }



    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
