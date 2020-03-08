package com.centaurstech.sdk;

import android.text.TextUtils;

import com.centaurstech.qiwu.QiWuAPI;
import com.centaurstech.qiwu.callback.APICallback;
import com.centaurstech.qiwu.callback.PayCallback;
import com.centaurstech.qiwu.common.MsgType;
import com.centaurstech.qiwu.db.MsgDBManager;
import com.centaurstech.qiwu.entity.ExpressPriceEntity;
import com.centaurstech.qiwu.entity.FlightInfoEntity;
import com.centaurstech.qiwu.entity.FlightRefundReasonEntity;
import com.centaurstech.qiwu.entity.FortuneEntity;
import com.centaurstech.qiwu.entity.FortuneReportEntity;
import com.centaurstech.qiwu.entity.LeaveAloneEntity;
import com.centaurstech.qiwu.entity.LeaveAloneReportEntity;
import com.centaurstech.qiwu.entity.InsureEntity;
import com.centaurstech.qiwu.entity.InvoiceTitleEntity;
import com.centaurstech.qiwu.entity.LinkmanEntity;
import com.centaurstech.qiwu.entity.LoversDiscEntity;
import com.centaurstech.qiwu.entity.LoversDiscReportEntity;
import com.centaurstech.qiwu.entity.MsgEntity;
import com.centaurstech.qiwu.entity.OrderExpressEntity;
import com.centaurstech.qiwu.entity.OrderFlightEntity;
import com.centaurstech.qiwu.entity.OrderPhoneRechargeEntity;
import com.centaurstech.qiwu.entity.OrderTaxiEntity;
import com.centaurstech.qiwu.entity.OrderTrainEntity;
import com.centaurstech.qiwu.entity.PassengerEntity;
import com.centaurstech.qiwu.entity.APIEntity;
import com.centaurstech.qiwu.entity.RechargePayEntity;
import com.centaurstech.qiwu.entity.TokenEntity;
import com.centaurstech.qiwu.entity.TrainRefundEntity;
import com.centaurstech.qiwu.entity.TrainTicketEntity;
import com.centaurstech.qiwu.entity.UserInfoEntity;
import com.centaurstech.qiwu.utils.GsonUtils;
import com.centaurstech.qiwu.utils.LogUtils;
import com.centaurstech.sdk.activity.BaseActivity;
import com.centaurstech.sdk.activity.order.ExpressOrderDetailActivity;
import com.centaurstech.sdk.activity.order.TaxiOrderDetailActivity;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Leon(黄长亮)
 * @describe 测试API
 * @date 2019/6/6
 */
public class TestAPI {

    public static class Order {

        /**
         * 获取打车订单
         */
        public static void getTaxiOrder() {
            QiWuAPI.taxi.getOrder("c20190124001535065690001", new APICallback<APIEntity<OrderTaxiEntity>>() {
                @Override
                public void onSuccess(APIEntity<OrderTaxiEntity> response) {
                    LogUtils.sf("onSuccess:" + GsonUtils.toJson(response));
                }
            });
        }


        /**
         * 获取话费充值订单
         */
        public static void getPhoneRechargeOrder() {
            QiWuAPI.recharge.getOrder("p20190511161805216800001", new APICallback<APIEntity<OrderPhoneRechargeEntity>>() {
                @Override
                public void onSuccess(APIEntity<OrderPhoneRechargeEntity> response) {
                    LogUtils.sf("onSuccess:" + GsonUtils.toJson(response));
                }
            });
        }


        /**
         * 话费充值
         */
        public static void phoneRecharge(BaseActivity baseActivity) {
            MsgEntity msgEntity = MsgDBManager.getInstance().getLastFromType(MsgType.PHONE_RECHARGE);
            if (msgEntity.getData().getRechargeInfo() == null) return;
            QiWuAPI.recharge.booking(msgEntity.getData().getRechargeInfo(), 1, new APICallback<APIEntity<RechargePayEntity>>() {
                @Override
                public void onSuccess(APIEntity<RechargePayEntity> response) {
                    LogUtils.sf("onSuccess:" + GsonUtils.toJson(response.getData()));
                    if (response.isSuccess()) {
                        PayManager.getInstance().aliPay(response.getData().getPaymentSign(), baseActivity, new PayCallback() {
                            @Override
                            public void onPayResult(State state, Type type, List<String> oid) {

                            }
                        });
                    }
                }
            });

        }


        /**
         * 获取话费价格
         */
        public static void getPhoneRechargePrice() {
            MsgEntity msgEntity = MsgDBManager.getInstance().get("", 1).get(0);
            if (msgEntity.getData().getRechargeInfo() == null) return;

        }

        /**
         * 获取星座运势报告
         */
        public static void getLoversDiscReport() {
            QiWuAPI.horoscope.getLoversDiscReport("l20190509175444756200001", new APICallback<APIEntity<LoversDiscReportEntity>>() {
                @Override
                public void onSuccess(APIEntity<LoversDiscReportEntity> response) {
                    LogUtils.sf("onSuccess:" + GsonUtils.toJson(response));
                }
            });
        }

        /**
         * 获取情侣合盘订单
         */
        public static void getLoversDiscOrder() {
            QiWuAPI.horoscope.getLoversDiscOrder("l20190509175444756200001", new APICallback<APIEntity<LoversDiscEntity>>() {
                @Override
                public void onSuccess(APIEntity<LoversDiscEntity> response) {
                    LogUtils.sf("onSuccess:" + GsonUtils.toJson(response));
                }
            });
        }

        /**
         * 取消星座脱单订单
         */
        public static void cancelLoversDisc() {
            QiWuAPI.horoscope.cancelLeaveAlone("l20190618151958591710001", new APICallback<APIEntity<String>>() {
                @Override
                public void onSuccess(APIEntity<String> response) {
                    LogUtils.sf("onSuccess:" + GsonUtils.toJson(response));
                }
            });
        }

        /**
         * 获取星座情侣合盘订单
         */
        public static void bookingLoversDisc() {
            MsgEntity msgEntity = MsgDBManager.getInstance().get("", 1).get(0);
            if (msgEntity.getData().getHoroscope() == null) return;
            QiWuAPI.horoscope.bookingLeaveAlone(msgEntity.getData().getHoroscope(), new APICallback<APIEntity<String>>() {
                @Override
                public void onSuccess(APIEntity<String> response) {
                    LogUtils.sf("onSuccess:" + GsonUtils.toJson(response));
                }
            });
        }


        /**
         * 获取情侣合盘脱单记录
         */
        public static void getLoversDiscHistory() {
            MsgEntity msgEntity = MsgDBManager.getInstance().get("", 1).get(0);
            if (msgEntity.getData().getHoroscope() == null) return;
            QiWuAPI.horoscope.getLeaveAloneHistory(msgEntity.getData().getHoroscope(), new APICallback<APIEntity<String>>() {
                @Override
                public void onSuccess(APIEntity<String> response) {
                    LogUtils.sf("onSuccess:" + GsonUtils.toJson(response));
                }
            });
        }


        /**
         * 删除星座脱单订单
         */
        public static void deleteLeaveAlone() {
            QiWuAPI.horoscope.deleteLeaveAlone("l20190618143243902260001", new APICallback<APIEntity<String>>() {
                @Override
                public void onSuccess(APIEntity<String> response) {
                    LogUtils.sf("onSuccess:" + GsonUtils.toJson(response));
                }
            });
        }

        /**
         * 取消星座脱单订单
         */
        public static void cancelLeaveAlone() {
            QiWuAPI.horoscope.cancelLeaveAlone("l20190618145114594600001", new APICallback<APIEntity<String>>() {
                @Override
                public void onSuccess(APIEntity<String> response) {
                    LogUtils.sf("onSuccess:" + GsonUtils.toJson(response));
                }
            });
        }


        /**
         * 获取星座脱单订单
         */
        public static void bookingLeaveAlone() {
            MsgEntity msgEntity = MsgDBManager.getInstance().get("", 1).get(0);
            if (msgEntity.getData().getHoroscope() == null) return;
            QiWuAPI.horoscope.bookingLeaveAlone(msgEntity.getData().getHoroscope(), new APICallback<APIEntity<String>>() {
                @Override
                public void onSuccess(APIEntity<String> response) {
                    LogUtils.sf("onSuccess:" + GsonUtils.toJson(response));
                }
            });
        }


        /**
         * 获取星座脱单记录
         */
        public static void getLeaveAloneHistory() {
            MsgEntity msgEntity = MsgDBManager.getInstance().get("", 1).get(0);
            if (msgEntity.getData().getHoroscope() == null) return;
            QiWuAPI.horoscope.getLeaveAloneHistory(msgEntity.getData().getHoroscope(), new APICallback<APIEntity<String>>() {
                @Override
                public void onSuccess(APIEntity<String> response) {
                    LogUtils.sf("onSuccess:" + GsonUtils.toJson(response));
                }
            });
        }


        /**
         * 获取星座运势报告
         */
        public static void getLeaveAloneReport() {
            QiWuAPI.horoscope.getLeaveAloneReport("l20190509182625199990001", new APICallback<APIEntity<LeaveAloneReportEntity>>() {
                @Override
                public void onSuccess(APIEntity<LeaveAloneReportEntity> response) {
                    LogUtils.sf("onSuccess:" + GsonUtils.toJson(response));
                }
            });
        }

        /**
         * 获取星座脱单订单
         */
        public static void getLeaveAloneOrder() {
            QiWuAPI.horoscope.getLeaveAloneOrder("l20190509182625199990001", new APICallback<APIEntity<LeaveAloneEntity>>() {
                @Override
                public void onSuccess(APIEntity<LeaveAloneEntity> response) {
                    response.getData().setOrderReturnMessage("");
                    LogUtils.sf("onSuccess:" + GsonUtils.toJson(response));
                }
            });
        }


        /**
         * 获取星座运势报告
         */
        public static void getFortuneReport() {
            QiWuAPI.horoscope.getFortuneReport("fo20190509194358446850001", "fo201905091943584468500011", new APICallback<APIEntity<FortuneReportEntity>>() {
                @Override
                public void onSuccess(APIEntity<FortuneReportEntity> response) {
                    LogUtils.sf("onSuccess:" + GsonUtils.toJson(response));
                }
            });
        }


        /**
         * 获取星座运势订单
         *
         * @param orderId
         */
        public static void getFortuneOrder(String orderId) {
            if (TextUtils.isEmpty(orderId)) {
                orderId = "fo20190509194358446850001";
            }
            QiWuAPI.horoscope.getFortuneOrder(orderId, new APICallback<APIEntity<FortuneEntity>>() {
                @Override
                public void onSuccess(APIEntity<FortuneEntity> response) {
                    LogUtils.sf("onSuccess:" + GsonUtils.toJson(response));
                }
            });
        }


        /**
         * 获取闪送订单
         *
         * @param orderId
         */
        public static void getExpressOrder(String orderId) {
            if (TextUtils.isEmpty(orderId)) {
                orderId = "e20190530171503504300001";
            }
            QiWuAPI.express.getOrder(orderId, new APICallback<APIEntity<OrderExpressEntity>>() {
                @Override
                public void onSuccess(APIEntity<OrderExpressEntity> response) {
                    LogUtils.sf("onSuccess:" + GsonUtils.toJson(response));
                }
            });
        }

        /**
         * 获取火车票订单
         *
         * @param orderId
         */
        public static void getTrainOrder(String orderId) {
            if (TextUtils.isEmpty(orderId)) {
                orderId = "t20190619160039031480001";
            }
            QiWuAPI.train.getOrder(orderId, new APICallback<APIEntity<OrderTrainEntity>>() {
                @Override
                public void onSuccess(APIEntity<OrderTrainEntity> response) {
                    LogUtils.sf("onSuccess:" + GsonUtils.toJson(response));
                }
            });
        }

        /**
         * 获取飞机票订单
         *
         * @param orderId
         */
        public static void getFlightOrder(String orderId) {
            if (TextUtils.isEmpty(orderId)) {
                orderId = "f20190610115318156440001";
            }
            QiWuAPI.flight.getOrder(orderId, new APICallback<APIEntity<ArrayList<OrderFlightEntity>>>() {
                @Override
                public void onSuccess(APIEntity<ArrayList<OrderFlightEntity>> response) {
                    LogUtils.sf("onSuccess:" + GsonUtils.toJson(response));
                }
            });
        }

        /**
         * 获取火车票保险
         */
        public static void getTrainInsurance() {
            QiWuAPI.train.getInsurance(new APICallback<APIEntity<ArrayList<InsureEntity>>>() {

                @Override
                public void onError(int code) {
                    super.onError(code);
                }

                @Override
                public void onSuccess(APIEntity<ArrayList<InsureEntity>> response) {

                }
            });
        }


        /**
         * 获取火车票退票费用
         */
        public static void getTrainRefundFee() {
            String orderId = "t20190619161437858750001";
            QiWuAPI.train.getOrder(orderId, new APICallback<APIEntity<OrderTrainEntity>>() {
                @Override
                public void onSuccess(APIEntity<OrderTrainEntity> response) {
                    if (response.isSuccess()) {
                        QiWuAPI.train.getRefundFee(orderId, response.getData().getTickets().get(0).getId(), new APICallback<APIEntity<TrainRefundEntity>>() {
                            @Override
                            public void onSuccess(APIEntity<TrainRefundEntity> response) {

                            }
                        });
                    }
                }
            });
        }

        /**
         * 预定火车票
         */
        public static void bookingTrainTicket() {
            MsgEntity msgEntity = MsgDBManager.getInstance().getLastFromType(MsgType.TRAIN);
            LogUtils.sf("bookingTrainTicket:" + GsonUtils.toJson(msgEntity));
            if (msgEntity.getData().getTrainTicketInfo() == null) return;
            ArrayList<PassengerEntity> passengers = GsonUtils.fromJson(TestData.passengers, new TypeToken<ArrayList<PassengerEntity>>() {
            }.getType());
            TrainTicketEntity ticket = msgEntity.getData().getTrainTicketInfo().getCurrentTicket();
            QiWuAPI.train.booking(ticket, "李三", "15107716544", passengers, new APICallback<APIEntity<String>>() {
                @Override
                public void onSuccess(APIEntity<String> response) {
                    LogUtils.sf("onSuccess:" + GsonUtils.toJson(response));
                }
            });
        }


        /**
         * 获取飞机票信息
         */
        public static void getFlightInfo() {
            MsgEntity msgEntity = GsonUtils.fromJson(TestData.flightTicketData, MsgEntity.class);
            QiWuAPI.flight.getInfo(msgEntity.getData().getFlightTicketInfo().getFlightTickets().get(0), new APICallback<APIEntity<ArrayList<ArrayList<FlightInfoEntity>>>>() {
                @Override
                public void onSuccess(APIEntity<ArrayList<ArrayList<FlightInfoEntity>>> response) {
                    LogUtils.sf(GsonUtils.toJson(response));
                }
            });
        }


        /**
         * 预定飞机票
         */
        public static void bookingFlightTicket() {
            MsgEntity msgEntity = MsgDBManager.getInstance().getLastFromType(MsgType.FLIGHT);
            if (msgEntity.getData().getFlightTicketInfo() == null) return;
            ArrayList<PassengerEntity> passengers = GsonUtils.fromJson(TestData.passengers, new TypeToken<ArrayList<PassengerEntity>>() {
            }.getType());
            QiWuAPI.flight.getInfo(msgEntity.getData().getFlightTicketInfo().getFlightTickets().get(0), new APICallback<APIEntity<ArrayList<ArrayList<FlightInfoEntity>>>>() {
                @Override
                public void onSuccess(APIEntity<ArrayList<ArrayList<FlightInfoEntity>>> response) {
                    LogUtils.sf(GsonUtils.toJson(response));
                    FlightInfoEntity flight1 = response.getData().get(0).get(0);
                    FlightInfoEntity flight2 = null;
                    QiWuAPI.flight.booking(flight1, passengers, "李雷", "15107716544", null,
                            new APICallback<APIEntity<String>>() {
                                @Override
                                public void onSuccess(APIEntity<String> response) {

                                }
                            });
                }
            });
        }


        /**
         * 预定飞机票
         */
        public static void bookingFlightTicket2() {
            MsgEntity msgEntity = MsgDBManager.getInstance().get("", 1).get(0);
            if (msgEntity.getData().getFlightTicketInfo() == null) return;
            ArrayList<PassengerEntity> passengers = GsonUtils.fromJson(TestData.passengers, new TypeToken<ArrayList<PassengerEntity>>() {
            }.getType());
            QiWuAPI.flight.getInfo(
                    msgEntity.getData().getFlightTicketInfo().getFlightTickets().get(0),
                    msgEntity.getData().getFlightTicketInfo().getFlightTickets().get(1),
                    new APICallback<APIEntity<ArrayList<ArrayList<FlightInfoEntity>>>>() {
                        @Override
                        public void onSuccess(APIEntity<ArrayList<ArrayList<FlightInfoEntity>>> response) {
                            if (!response.isSuccess()) return;
                            LogUtils.sf(GsonUtils.toJson(response));
                            FlightInfoEntity flight1 = response.getData().get(0).get(0);
                            FlightInfoEntity flight2 = response.getData().get(1).get(0);
                            QiWuAPI.flight.booking(flight1, flight2, passengers, "李雷", "15107716544", null,
                                    new APICallback<APIEntity<String>>() {
                                        @Override
                                        public void onSuccess(APIEntity<String> response) {

                                        }
                                    });
                        }
                    });
        }


        /**
         * 获取飞机票退票理由
         */
        public static void getFlightRefundReason() {
            QiWuAPI.flight.getRefundReason("f20190619175147879990001", new APICallback<APIEntity<List<FlightRefundReasonEntity>>>() {
                @Override
                public void onSuccess(APIEntity<List<FlightRefundReasonEntity>> response) {
                    LogUtils.sf("onSuccess:" + GsonUtils.toJson(response));
                }
            });
        }


        /**
         * 飞机票退票
         */
        public static void refundFlight() {
            String orderId = "f20190619175147879990001";
            QiWuAPI.flight.getRefundReason(orderId, new APICallback<APIEntity<List<FlightRefundReasonEntity>>>() {
                @Override
                public void onSuccess(APIEntity<List<FlightRefundReasonEntity>> response) {
                    if (response.isSuccess()) {
                        FlightRefundReasonEntity flightRefund = response.getData().get(0);
                        QiWuAPI.flight.refund(orderId,
                                flightRefund.getCode(),
                                flightRefund.getMsg(),
                                flightRefund.getRefundPassengerPriceInfoList().get(0), new APICallback<APIEntity<String>>() {
                                    @Override
                                    public void onSuccess(APIEntity<String> response) {
                                        LogUtils.sf("onSuccess:" + GsonUtils.toJson(response));
                                    }
                                });
                    }
                }
            });
        }


        /**
         * 取消飞机票
         */
        public static void cancelFlight() {
            QiWuAPI.flight.cancel("f20190619164417309080001", new APICallback<APIEntity<String>>() {
                @Override
                public void onSuccess(APIEntity<String> response) {
                    LogUtils.sf("onSuccess:" + GsonUtils.toJson(response));
                }
            });
        }


        /**
         * 预定酒店
         */
        public static void bookingHotel() {
            MsgEntity msgEntity = MsgDBManager.getInstance().getLastFromType(MsgType.HOTEL);
            if (msgEntity.getData().getHotel() == null) return;
            List<String> lodgers = new ArrayList<>();
            lodgers.add("李三");
            QiWuAPI.hotel.booking(
                    msgEntity.getData().getHotel(),
                    lodgers, "李四",
                    "15107765554",
                    "2019-06-15 14:00后办理入住",
                    new APICallback<APIEntity<String>>() {
                        @Override
                        public void onSuccess(APIEntity<String> response) {

                        }
                    });
        }

        /**
         * 获取闪送价格
         */
        public static void getExpressPrice() {
            MsgEntity msgEntity = MsgDBManager.getInstance().get("", 1).get(0);
            LogUtils.sf(GsonUtils.toJson(msgEntity));
            if (msgEntity.getData().getExpress() == null) return;
            QiWuAPI.express.getPrice(msgEntity.getData().getExpress(), new APICallback<APIEntity<ExpressPriceEntity>>() {
                @Override
                public void onSuccess(APIEntity<ExpressPriceEntity> response) {
                    LogUtils.sf("onSuccess:" + GsonUtils.toJson(response));
                }
            });
        }


        /**
         * 闪送下单
         */
        public static void bookingExpress(BaseActivity baseActivity) {
            MsgEntity msgEntity = MsgDBManager.getInstance().getLastFromType(MsgType.EXPRESS);
            if (msgEntity.getData().getExpress() == null) return;
            QiWuAPI.express.booking(msgEntity.getData().getExpress(), new APICallback<APIEntity<String>>() {
                @Override
                public void onSuccess(APIEntity<String> response) {
                    LogUtils.sf("onSuccess:" + GsonUtils.toJson(response));
                    baseActivity.launchActivity(ExpressOrderDetailActivity.class, "id", response.getData());
                }
            });
        }


        /**
         * 预定打车
         */
        public static void bookingTaxi(BaseActivity baseActivity) {
            MsgEntity msgEntity = MsgDBManager.getInstance().getLastFromType(MsgType.TAXI);
            LogUtils.sf(GsonUtils.toJson(msgEntity));
            if (msgEntity.getData().getTaxi() == null) return;
            QiWuAPI.taxi.booking(msgEntity.getData().getTaxi(), "陈亮", "15107716544", new APICallback<APIEntity<String>>() {
                @Override
                public void onSuccess(APIEntity<String> response) {
                    LogUtils.sf("onSuccess:" + GsonUtils.toJson(response));
                    if (response.isSuccess()) {
                        baseActivity.launchActivity(TaxiOrderDetailActivity.class, "id", response.getData());
                    }
                }
            });
        }


        /**
         * 取消打车
         */
        public static void cancelTaxi() {
            QiWuAPI.taxi.cancel("c20190619120249478650001", new APICallback<APIEntity<String>>() {
                @Override
                public void onSuccess(APIEntity<String> response) {
                    LogUtils.sf("onSuccess:" + GsonUtils.toJson(response));
                }
            });
        }


        /**
         * 请求支付
         */
        public static void requestPay() {
            QiWuAPI.pay.aliPay("t20190619161437858750001", new APICallback<APIEntity<String>>() {
                @Override
                public void onSuccess(APIEntity<String> response) {

                }
            });
        }
    }

    public static class CommonInfo {


        /**
         * 获取出行人
         */
        public static void getPassengers() {
            QiWuAPI.info.getPassengers("0", new APICallback<APIEntity<ArrayList<PassengerEntity>>>() {
                @Override
                public void onSuccess(APIEntity<ArrayList<PassengerEntity>> response) {
                    LogUtils.sf(GsonUtils.toJson(response));
                }
            });
        }

    }


    public static class Sms {

        /**
         * 获取短信验证码
         */
        public static void getSmsCaptcha() {
            QiWuAPI.sms.getCaptcha("15107716544", new APICallback<APIEntity<String>>() {
                @Override
                public void onSuccess(APIEntity<String> response) {
                    LogUtils.sf(GsonUtils.toJson(response));
                }
            });
        }

    }

    public static class User {

        /**
         * 退出登录
         */
        public static void logout() {
            QiWuAPI.account.logout(new APICallback<TokenEntity>() {
                @Override
                public void onSuccess(TokenEntity response) {
                    LogUtils.sf(GsonUtils.toJson(response));
                }
            });
        }


        /**
         * 手机号登录
         */
        public static void login() {
            QiWuAPI.account.login("15107716544", "9930", new APICallback<TokenEntity>() {
                @Override
                public void onSuccess(TokenEntity response) {
                    LogUtils.sf(GsonUtils.toJson(response));
                }
            });
        }

        /**
         * 获取用户信息
         */
        public static void getUserInfo() {
            QiWuAPI.account.getUserInfo(new APICallback<APIEntity<UserInfoEntity>>() {
                @Override
                public void onSuccess(APIEntity<UserInfoEntity> response) {

                }
            });
        }

        /**
         * 修改用户昵称
         */
        public static void updateNickname() {
            QiWuAPI.account.updateNickname("张三", new APICallback<APIEntity<UserInfoEntity>>() {
                @Override
                public void onSuccess(APIEntity<UserInfoEntity> response) {

                }
            });
        }


        /**
         * 获取用户信息
         */
        public static void refreshToken() {
            QiWuAPI.account.refreshToken(new APICallback<TokenEntity>() {
                @Override
                public void onSuccess(TokenEntity response) {
                    LogUtils.sf(GsonUtils.toJson(response));
                }
            });
        }

        /**
         * 保存企业发票抬头
         */
        public static void saveCompanyInvoice() {
            QiWuAPI.info.saveCompanyInvoice(
                    "深圳人牛互动有限公司",
                    "12131313@qq.com",
                    "中国银行",
                    "1315464646666",
                    "91440300MA5DM8RB1T",
                    "深圳湾",
                    "450844455",
                    "15101178987",
                    "",
                    true,
                    new APICallback<APIEntity<InvoiceTitleEntity>>() {
                        @Override
                        public void onSuccess(APIEntity<InvoiceTitleEntity> response) {

                        }
                    });
        }


        /**
         * 保存个人发票抬头
         */
        public static void savePersonalInvoice() {
            QiWuAPI.info.savePersonalInvoice(
                    "深圳马猪互动有限公司",
                    "1444445@qq.com",
                    "15101178911",
                    "",
                    true, new APICallback<APIEntity<InvoiceTitleEntity>>() {
                        @Override
                        public void onSuccess(APIEntity<InvoiceTitleEntity> response) {

                        }
                    });
        }


        /**
         * 获取发票抬头列表
         */
        public static void getInvoices() {
            QiWuAPI.info.getInvoices(new APICallback<APIEntity<ArrayList<InvoiceTitleEntity>>>() {
                @Override
                public void onSuccess(APIEntity<ArrayList<InvoiceTitleEntity>> response) {
                    LogUtils.sf("onSuccess:" + GsonUtils.toJson(response));
                }
            });
        }

        /**
         * 删除发票抬头
         */
        public static void deleteInvoice() {
            QiWuAPI.info.deleteInvoice(3, new APICallback<APIEntity<InvoiceTitleEntity>>() {
                @Override
                public void onSuccess(APIEntity<InvoiceTitleEntity> response) {

                }
            });
        }

        /**
         * 保存联系人
         */
        public static void saveLinkman() {
            QiWuAPI.info.saveLinkman("李白", "13199998888", "1", true, new APICallback<APIEntity<LinkmanEntity>>() {
                @Override
                public void onSuccess(APIEntity<LinkmanEntity> response) {

                }
            });
        }

        /**
         * 获取联系人
         */
        public static void getLinkman() {
            QiWuAPI.info.getLinkman(new APICallback<APIEntity<ArrayList<LinkmanEntity>>>() {
                @Override
                public void onSuccess(APIEntity<ArrayList<LinkmanEntity>> response) {
                    LogUtils.sf("onSuccess:" + GsonUtils.toJson(response));
                }
            });
        }

        /**
         * 删除联系人
         */
        public static void deleteLinkman() {
            QiWuAPI.info.deleteLinkman(1, new APICallback<APIEntity<LinkmanEntity>>() {
                @Override
                public void onSuccess(APIEntity<LinkmanEntity> response) {

                }
            });
        }


    }

}
