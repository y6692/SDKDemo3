package com.centaurstech.sdk.common;

import com.centaurstech.sdk.R;

import static com.centaurstech.qiwu.utils.UIUtils.getString;

/**
 * @author Leon(黄长亮)
 * @describe 闲聊
 * @date 2019/8/14
 */
public class Const2 {

    public static final String small_talk = "闲聊";
    public static final String weather = "天气";
    public static final String direction = "路径规划";
    public static final String navigation = "导航";
    public static final String poi = "兴趣点";
    public static final String music = "音乐";
    public static final String call = "打电话";
    public static final String close_navigation = "关闭导航";
    public static final String asr_end = "识别结束";
    public static final String close_audio = "关闭录音";
    public static final String into_background = "进入后台";
    public static final String over_navigation = "结束导航";
    public static final String login_success = "登陆成功";
    public static final String audio_wake = "语音唤醒";
    public static final String clear_activity = "清除界面";
    public static final String update_order = "刷新订单";
    public static final String add_new_message = "添加消息";

    public static final String show_guide = "显示按钮";

    /**
     * 获取对应天气
     *
     * @param weather
     * @return
     */
    public static int weather(int weather) {
        switch (weather) {
//            case 0:
//            case 2:
//            case 38:
//                return R.mipmap.weather_0;
//            case 1:
//            case 3:
//                return R.mipmap.weather_1;
//            case 4:
//                return R.mipmap.weather_4;
//            case 5:
//                return R.mipmap.weather_5;
//            case 6:
//                return R.mipmap.weather_6;
//            case 7:
//                return R.mipmap.weather_7;
//            case 8:
//                return R.mipmap.weather_8;
//            case 9:
//                return R.mipmap.weather_9;
//            case 10:
//            case 11:
//            case 13:
//            case 19:
//                return R.mipmap.weather_10;
//            case 12:
//                return R.mipmap.weather_12;
//            case 14:
//                return R.mipmap.weather_14;
//            case 15:
//                return R.mipmap.weather_15;
//            case 16:
//            case 17:
//            case 18:
//                return R.mipmap.weather_16;
//            case 20:
//                return R.mipmap.weather_20;
//            case 21:
//            case 22:
//                return R.mipmap.weather_21;
//            case 23:
//            case 24:
//            case 25:
//            case 37:
//                return R.mipmap.weather_23;
//            case 26:
//            case 27:
//            case 28:
//            case 29:
//                return R.mipmap.weather_26;
//            case 30:
//                return R.mipmap.weather_30;
//            case 31:
//                return R.mipmap.weather_31;
//            case 32:
//            case 33:
//                return R.mipmap.weather_32;
//            case 34:
//            case 35:
//            case 36:
//                return R.mipmap.weather_34;
//            case 99:
//                return R.mipmap.weather_99;
            default:
                return 0;
        }
    }

    public interface DataArray {
        String[] Certificates = new String[]{
                getString(R.string.identity_card)
                , getString(R.string.protection)
                , getString(R.string.MTPs)
                , getString(R.string.hk_and_macau_pass)
                , getString(R.string.hk_and_macau_from_pass)
                , getString(R.string.tw_from_pass)
                , getString(R.string.officer_card)
                , getString(R.string.soldier_card)};

        String[] Gender = new String[]{
                getString(R.string.woman),
                getString(R.string.man)};

        String[] Rooms = new String[]{
                getString(R.string.room_one)
                , getString(R.string.room_two)
                , getString(R.string.room_three)
                , getString(R.string.room_four)
                , getString(R.string.room_five)};

        String[] OrderState = new String[]{
                getString(R.string.unpaid) //0
                , getString(R.string.exit_ticket)//1
                , getString(R.string.no_travel)//2
                , getString(R.string.travel)//3
                , getString(R.string.exitTicket_failed)//4
                , getString(R.string.refunding)//5
                , getString(R.string.refund_moneyed)//6
                , getString(R.string.canceled)//7
                , getString(R.string.deal_close)//8
                , getString(R.string.change_ticket)//9
                , getString(R.string.occupy_seat)//10
                , getString(R.string.canceling)//11
                , getString(R.string.the_refund)//12
                , getString(R.string.make_ticket_failed_refunding)//13
        };
    }


}
