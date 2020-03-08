package com.centaurstech.sdk.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.View;

import com.centaurstech.qiwu.QiWuAPI;
import com.centaurstech.qiwu.callback.APICallback;
import com.centaurstech.qiwu.common.Const;
import com.centaurstech.qiwu.entity.APIEntity;
import com.centaurstech.qiwu.entity.EventBusEntity;
import com.centaurstech.qiwu.entity.OrderEntity;
import com.centaurstech.qiwu.utils.GsonUtils;
import com.centaurstech.qiwu.utils.LogUtils;
import com.centaurstech.qiwu.utils.UIUtils;
import com.centaurstech.sdk.R;
import com.centaurstech.sdk.activity.BaseFragment;
import com.centaurstech.sdk.activity.order.FlightOrderDetailActivity;
import com.centaurstech.sdk.activity.order.TrainOrderDetailActivity;
import com.centaurstech.sdk.activity.movies.OrderDetailCinemaActivity;
import com.centaurstech.sdk.common.Const2;
import com.centaurstech.sdk.databinding.ItemOrderBinding;
import com.centaurstech.sdk.fragment.OrderListFragment;
import com.centaurstech.sdk.utils.ToastUtils;
import com.centaurstech.sdk.view.PopDialog;
import com.qiwu.ui.view.adapter.DataBindRecyclerViewAdapter;

import org.greenrobot.eventbus.EventBus;

/**
 * Time:2019/12/25
 * Author: 樊德鹏
 * Description:
 */
public class OrderAdapter extends DataBindRecyclerViewAdapter<OrderEntity, ItemOrderBinding> {


    private OrderListFragment mOrderListFragment;

    private int mPosition = -1;

    public OrderAdapter(Context context, OrderListFragment orderListFragment) {
        super(context);
        mOrderListFragment = orderListFragment;
    }


    @Override
    public int getItemViewType(int position) {
        return R.layout.item_order;
    }


    @Override
    public void onBindHolder(ItemOrderBinding binding, OrderEntity itemData, int position) {
        RecyclerView.LayoutParams rlp = (RecyclerView.LayoutParams) binding.cvContainer.getLayoutParams();
        if (position == mData.size() - 1) {
            rlp.setMargins(UIUtils.dip2Px(28), UIUtils.dip2Px(12), UIUtils.dip2Px(28), UIUtils.dip2Px(28));
        } else {
            rlp.setMargins(UIUtils.dip2Px(28), UIUtils.dip2Px(12), UIUtils.dip2Px(28), UIUtils.dip2Px(0));
        }
        binding.cvContainer.requestLayout();

        LogUtils.sf(itemData.getType() + "");
        binding.tvOrderId.setText(getContext().getString(R.string.order_id) + ": " + itemData.getOrderId());
        LogUtils.sf(GsonUtils.toJson(itemData));
        String major = "", child = "", baby = "";
        binding.cdEnd.setVisibility(View.GONE);
        binding.cdStart.setVisibility(View.GONE);
        switch (itemData.getState()) {
            case 3:
            case 4:
            case 6:
            case 7:
            case 16:
            case 8:
            case 14:
                binding.line.setVisibility(View.VISIBLE);
                binding.tvDeleteOrder.setVisibility(View.VISIBLE);
                break;
            default:
                binding.line.setVisibility(View.GONE);
                binding.tvDeleteOrder.setVisibility(View.GONE);
                break;
        }
        switch (itemData.getOrderType()) {
            case OrderEntity.Type.flight:
            case OrderEntity.Type.flight_return:
                binding.tvOrderType.setText(UIUtils.getString(R.string.air_ticket));
                binding.ivOrderIcon.setImageResource(R.mipmap.myorder_icon_ticket);
                binding.cvPoint.setCardBackgroundColor(UIUtils.getColor(getColor(itemData.getState())));
                binding.tvOrderState.setTextColor(UIUtils.getColor(getColor(itemData.getState())));
                binding.tvOrderState.setText(getState(itemData.getState(), itemData.getOrderType()));
                binding.tvOrderTitle.setText(itemData.getStartAdd() + " - " + itemData.getEndAdd());
                binding.tvOrderText1.setText(itemData.getAriwayOrTrainType() + itemData.getNumber() + " " + itemData.getSeatType());
                binding.tvOrderText2.setText(UIUtils.getString(R.string.origin_time) + ":" + itemData.getStartTime().replace("-", "."));
                if (itemData.getType().getMajor() != 0) {
                    major = "成人票" + itemData.getType().getMajor() + "张";
                }
                if (itemData.getType().getChild() != 0) {
                    child = "，儿童票" + itemData.getType().getChild() + "张";
                }
                if (itemData.getType().getBaby() != 0) {
                    baby = "，婴儿票" + itemData.getType().getBaby() + "张";
                }
                binding.tvPrice.setVisibility(View.VISIBLE);
                binding.tvPrice.setText(Html.fromHtml("<font color = \"#2A2A2A\">" + UIUtils.getString(R.string.total) + "（" + major + child + baby + "）:</font>   " + UIUtils.getString(R.string.money_sign) + itemData.getTotal()));
                binding.tvPrice.setVisibility(View.VISIBLE);
                binding.tvDeleteOrder.setVisibility(View.VISIBLE);
                break;
            case OrderEntity.Type.train:
                binding.tvOrderType.setText(UIUtils.getString(R.string.train_ticket));
                binding.ivOrderIcon.setImageResource(R.mipmap.myorder_icon_train);
                binding.cvPoint.setCardBackgroundColor(UIUtils.getColor(getColor(itemData.getState())));
                binding.tvOrderState.setTextColor(UIUtils.getColor(getColor(itemData.getState())));
                binding.tvOrderState.setText(itemData.getState() == OrderEntity.State.occupy_seat ? "占座中" : getState(itemData.getState(), itemData.getOrderType()));
                binding.tvOrderTitle.setText(itemData.getStartAdd() + " - " + itemData.getEndAdd());
                binding.tvOrderText1.setText(itemData.getAriwayOrTrainType() + itemData.getNumber() + " " + itemData.getSeatType());
                binding.tvOrderText2.setText(UIUtils.getString(R.string.origin_time) + ":" + itemData.getStartTime().replace("-", "."));
                if (itemData.getType().getMajor() != 0) {
                    major = "成人票" + itemData.getType().getMajor() + "张";
                }
                if (itemData.getType().getChild() != 0) {
                    child = "，儿童票" + itemData.getType().getChild() + "张";
                }
                if (itemData.getType().getBaby() != 0) {
                    baby = "，婴儿票" + itemData.getType().getBaby() + "张";
                }
                binding.tvPrice.setText(Html.fromHtml("<font color = \"#2A2A2A\">" + UIUtils.getString(R.string.total) + "（" + major + child + baby + "）:</font>   " + UIUtils.getString(R.string.money_sign) + itemData.getTotal()));
                binding.tvPrice.setVisibility(View.VISIBLE);
                binding.tvDeleteOrder.setVisibility(View.VISIBLE);
                break;
//            case OrderEntity.Type.hotel:
//                binding.tvOrderType.setText(UIUtils.getString(R.string.hotel_subscribe));
//                binding.ivOrderIcon.setImageResource(R.mipmap.myorder_icon_hotel);
//                binding.cvPoint.setCardBackgroundColor(UIUtils.getColor(getColor(itemData.getOrder().getState())));
//                binding.tvOrderState.setTextColor(UIUtils.getColor(getColor(itemData.getOrder().getState())));
//                binding.tvOrderState.setText(getState(itemData.getOrder().getState(), itemData.getType()));
//                binding.tvOrderTitle.setText(order.getNumber());
//                binding.tvOrderText1.setText(order.getSeatType() + " " + order.getAmount() + "间" + order.getEndAdd() + "晚");
//                binding.tvOrderText2.setText(UIUtils.getString(R.string.check_in_time) + ":" + order.getStartTime().replace("-", ".") + "至" + order.getEndTime().replace("-", "."));
//                binding.tvPrice.setText(Html.fromHtml("<font color = \"#2A2A2A\">" + UIUtils.getString(R.string.total) + ": </font>   " + UIUtils.getString(R.string.money_sign) + order.getTotal()));
//                binding.tvPrice.setVisibility(View.VISIBLE);
//                break;
//            case OrderEntity.Type.taxi:
//                binding.tvOrderType.setText(UIUtils.getString(R.string.taxi));
//                binding.ivOrderIcon.setImageResource(R.mipmap.myorder_icon_car);
//                binding.cvPoint.setCardBackgroundColor(UIUtils.getColor(getColor(itemData.getOrder().getState())));
//                binding.tvOrderState.setTextColor(UIUtils.getColor(getColor(itemData.getOrder().getState())));
//                if (1 == itemData.getOrder().getState()) {
//                    binding.tvOrderState.setText(UIUtils.getString(R.string.get_order_now));
//                } else if (2 == itemData.getOrder().getState()) {
//                    binding.tvOrderState.setText(UIUtils.getString(R.string.connation));
//                } else if (3 == itemData.getOrder().getState()) {
//                    binding.tvOrderState.setText(UIUtils.getString(R.string.now_complite));
//                } else if (15 == itemData.getOrder().getState()) {
//                    binding.tvOrderState.setText("司机抵达");
//                } else {
//                    binding.tvOrderState.setText(getState(itemData.getOrder().getState(), itemData.getType()));
//                }
//                try {
//                    String timer = DateUtils.date2Date(order.getCreateTime(), "yyyy-MM-dd HH:mm", "yyyy-MM-dd HH:mm");
//                    binding.tvOrderTitle.setText(order.getNumber() + " " + timer.replace("-", "."));
//                } catch (Exception e) {
//                    binding.tvOrderTitle.setText(order.getNumber() + " " + order.getCreateTime());
//                }
//                binding.tvOrderText1.setText(order.getStartAdd());
//                binding.tvOrderText2.setText(order.getEndAdd());
//
//                if (order.getTotal()>0) {
//                    binding.tvPrice.setText(UIUtils.getString(R.string.money_sign) + order.getTotal());
//                }else {
//                    binding.tvPrice.setText(UIUtils.getString(R.string.money_sign) + "----");
//                }
//
//                binding.tvPrice.setVisibility(View.VISIBLE);
//                binding.cdEnd.setVisibility(View.VISIBLE);
//                binding.cdStart.setVisibility(View.VISIBLE);
//                break;
//            case OrderEntity.Type.mobile_charge:
//                binding.tvOrderType.setText(UIUtils.getString(R.string.huafei_charge));
//                binding.ivOrderIcon.setImageResource(R.mipmap.myorder_icon_callcharge);
//                binding.cvPoint.setCardBackgroundColor(UIUtils.getColor(getColor(itemData.getOrder().getState())));
//                binding.tvOrderState.setTextColor(UIUtils.getColor(getColor(itemData.getOrder().getState())));
//                if (1 == itemData.getOrder().getState() || 2 == itemData.getOrder().getState()) {
//                    binding.tvOrderState.setText(UIUtils.getString(R.string.connation));
//                } else if (3 == itemData.getOrder().getState()) {
//                    binding.tvOrderState.setText(UIUtils.getString(R.string.now_complite));
//                } else {
//                    binding.tvOrderState.setText(getState(itemData.getOrder().getState(), itemData.getType()));
//                }
//                try {
//                    binding.tvOrderTitle.setText(UIUtils.getString(R.string.charge) + itemData.getOrder().getAriwayOrTrainType() + "元");
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                binding.tvOrderText1.setText(UIUtils.getString(R.string.phone_number2) + "：" + itemData.getOrder().getNumber());
//                binding.tvOrderText2.setText(UIUtils.getString(R.string.charge_date) + "：" + DateUtils.date2Date(order.getCreateTime(), "yyyy-MM-dd HH:mm:ss", "yyyy.MM.dd HH:mm"));
//                binding.tvPrice.setText(UIUtils.getString(R.string.money_sign) + order.getTotal());
//                binding.tvPrice.setVisibility(View.VISIBLE);
//                break;
//            case OrderEntity.Type.delivery:
//                binding.tvOrderType.setText(UIUtils.getString(R.string.delivery));
//                binding.ivOrderIcon.setImageResource(R.mipmap.myorder_icon_shansong);
//                binding.cvPoint.setCardBackgroundColor(UIUtils.getColor(getColor(itemData.getOrder().getState())));
//                binding.tvOrderState.setTextColor(UIUtils.getColor(getColor(itemData.getOrder().getState())));
//                if (1 == itemData.getOrder().getState()) {
//                    binding.tvOrderState.setText(UIUtils.getString(R.string.get_order_now));
//                } else if (2 == itemData.getOrder().getState()) {
//                    binding.tvOrderState.setText(UIUtils.getString(R.string.is_send));
//                } else if (3 == itemData.getOrder().getState()) {
//                    binding.tvOrderState.setText(UIUtils.getString(R.string.now_complite));
//                } else if (15 == itemData.getOrder().getState()) {
//                    binding.tvOrderState.setText(UIUtils.getString(R.string.arrived_address2));
//                } else if (16 == itemData.getOrder().getState()) {
//                    binding.tvOrderState.setText(UIUtils.getString(R.string.canceled));
//                } else if (10 == itemData.getOrder().getState()) {
//                    binding.tvOrderState.setText(UIUtils.getString(R.string.wait_grab_sheet));
//                } else if (13 == itemData.getOrder().getState()) {
//                    binding.tvOrderState.setText(UIUtils.getString(R.string.trace_shibai_tuikuan2));
//                } else if (6 == itemData.getOrder().getState()) {
//                    binding.tvOrderState.setText(UIUtils.getString(R.string.trace_shibai));
//                } else {
//                    binding.tvOrderState.setText(getState(itemData.getOrder().getState(), itemData.getType()));
//                }
//                if (!TextUtils.isEmpty(order.getSeatType())) {
//                    binding.tvOrderTitle.setText(order.getSeatType());
//                }
//                binding.tvOrderText1.setText(order.getStartAdd());
//                binding.tvOrderText2.setText(order.getEndAdd());
//                binding.tvPrice.setText(UIUtils.getString(R.string.money_sign) + order.getTotal());
//                binding.tvPrice.setVisibility(View.VISIBLE);
//                binding.cdEnd.setVisibility(View.VISIBLE);
//                binding.cdStart.setVisibility(View.VISIBLE);
//                break;
//            case OrderEntity.Type.constellation:
//            case OrderEntity.Type.constellation_pair:
//                if (itemData.getOrder().getState() == 3) {
//                    binding.line.setVisibility(View.GONE);
//                    binding.tvDeleteOrder.setVisibility(View.GONE);
//                }
//                binding.tvOrderType.setText(UIUtils.getString(R.string.constellation));
//                binding.ivOrderIcon.setImageResource(R.mipmap.myorder_icon_star);
//                binding.tvOrderState.setText(getState(itemData.getOrder().getState(), itemData.getType()));
//                switch (itemData.getOrder().getState()) {
//                    case OrderEntity.State.travel:
//                        binding.tvOrderState.setText(R.string.now_complite);
//                        break;
//                }
//                binding.cvPoint.setCardBackgroundColor(UIUtils.getColor(getColor(itemData.getOrder().getState())));
//                binding.tvOrderState.setTextColor(UIUtils.getColor(getColor(itemData.getOrder().getState())));
//                binding.tvOrderTitle.setText(itemData.getOrder().getAriwayOrTrainType());
//                switch (itemData.getOrder().getOrderType()) {
//                    case OrderEntity.Type.constellation_pair:
//                        binding.tvOrderText1.setText("单次");
//                        break;
//                    default:
//                        binding.tvOrderText1.setText((int) order.getTotal() / 30 + "个月");
//                        switch (itemData.getOrder().getFlightType()) {
//                            case "APP_SUBSCRIPTION30":
//                                binding.tvOrderText1.setText("1个月");
//                                break;
//                            case "APP_SUBSCRIPTION60":
//                                binding.tvOrderText1.setText("2个月");
//                                break;
//                            case "APP_SUBSCRIPTION90":
//                                binding.tvOrderText1.setText("3个月");
//                                break;
//                        }
//                        break;
//                }
//                binding.tvOrderText2.setText(UIUtils.getString(R.string.buy_order_time) + "：" + DateUtils.date2Date(order.getStartTime(), "yyyy-MM-dd HH:mm:ss", "yyyy.MM.dd HH:mm"));
//                binding.tvPrice.setText(UIUtils.getString(R.string.money_sign) + order.getTotal());
//                binding.tvPrice.setVisibility(View.VISIBLE);
//                break;
            case OrderEntity.Type.movie:
                binding.tvOrderType.setText(UIUtils.getString(R.string.movie));
                binding.ivOrderIcon.setImageResource(R.mipmap.myorder_icon_movie);
                binding.cvPoint.setCardBackgroundColor(UIUtils.getColor(getColor(itemData.getState())));
                binding.tvOrderState.setTextColor(UIUtils.getColor(getColor(itemData.getState())));
                binding.tvOrderTitle.setText(itemData.getAriwayOrTrainType());
                binding.tvPrice.setText(Html.fromHtml("<font color = \"#2A2A2A\">" + UIUtils.getString(R.string.total) + "(" + itemData.getAmount() + "张）: </font>" + UIUtils.getString(R.string.money_sign) + itemData.getTotal()));
                binding.tvPrice.setVisibility(View.VISIBLE);
                if (2 == itemData.getState()) {
                    binding.tvOrderState.setText(UIUtils.getString(R.string.wait_screened));
                } else if (3 == itemData.getState()) {
                    binding.tvOrderState.setText(UIUtils.getString(R.string.screened));
                } else if (13 == itemData.getState()) {
                    binding.tvOrderState.setText(UIUtils.getString(R.string.trace_shibai_tuikuan2));
                } else {
                    binding.tvOrderState.setText(getState(itemData.getState(), itemData.getOrderType()));
                }
                binding.tvOrderText1.setText(itemData.getStartAdd());
                binding.tvOrderText2.setText("观影时间:  " + itemData.getStartTime().replace("-", "."));
                binding.tvDeleteOrder.setVisibility(View.GONE);
                break;
        }

        binding.tvDeleteOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPosition = position;
                PopDialog.create(getContext())
                        .setTitle("删除订单")
                        .setContent("订单删除后不可恢复，您确定要删除订单吗？")
                        .setRightButtonColor(R.color.colorText)
                        .setLeftButtonColor(R.color.colorText)
                        .setOnViewClickListener(new PopDialog.OnViewClickListener() {
                            @Override
                            public void onClick(View view, boolean isConfirm) {
                                if (isConfirm) {
                                    switch (itemData.getOrderType()) {
                                        case OrderEntity.Type.flight:
                                        case OrderEntity.Type.flight_return:
                                            QiWuAPI.flight.delete(itemData.getOrderId(), new APICallback<APIEntity<String>>() {
                                                @Override
                                                public void onSuccess(APIEntity<String> response) {
                                                    if (response.isSuccess()) {
                                                        ToastUtils.show("删除成功");
                                                        EventBus.getDefault().post(new EventBusEntity(Const2.update_order));
                                                    } else {
                                                        ToastUtils.show(response.getMsg());
                                                    }
                                                }
                                            });
                                            break;
                                        case OrderEntity.Type.train:
                                            QiWuAPI.train.delete(itemData.getOrderId(), new APICallback<APIEntity<String>>() {
                                                @Override
                                                public void onSuccess(APIEntity<String> response) {
                                                    if (response.isSuccess()) {
                                                        ToastUtils.show("删除成功");
                                                        EventBus.getDefault().post(new EventBusEntity(Const2.update_order));
                                                    } else {
                                                        ToastUtils.show(response.getMsg());
                                                    }
                                                }
                                            });
                                            break;
                                    }
                                }
                            }
                        });

            }
        });
    }

    private String getState(int state, int type) {
        switch (type) {
            case OrderEntity.Type.flight:
            case OrderEntity.Type.train:
                switch (state) {
                    case 13:
                        return UIUtils.getString(R.string.make_ticket_failed_refunding2);
                    default:
                        return OrderState[state];
                }
//            case OrderEntity.Type.hotel:
//                switch (state) {
//                    case 1:
//                        return UIUtils.getString(R.string.wait_hotel_confirm2);
//                    case 12:
//                        return UIUtils.getString(R.string.unsubscribe);
//                    case 4:
//                    case 13:
//                        return UIUtils.getString(R.string.subscribe_failed);
//                    default:
//                        return OrderState[state];
//                }
//            case OrderEntity.Type.taxi:
//                switch (state) {
//                    case 15:
//                        return UIUtils.getString(R.string.driver_arrived);
//                }
            default:
                return OrderState[state];
        }
    }

    @Override
    public void itemClick(ItemOrderBinding binding, OrderEntity itemData, int position, View v) {
        super.itemClick(binding, itemData, position, v);
        switch (itemData.getOrderType()) {
            case OrderEntity.Type.flight:
            case OrderEntity.Type.flight_return:
                launchActivity(FlightOrderDetailActivity.class, itemData.getOrderId(), position);
                break;
            case OrderEntity.Type.train:
                launchActivity(TrainOrderDetailActivity.class, itemData.getOrderId(), position);
                break;
//            case OrderEntity.Type.hotel:
//                launchActivity(OrderDetailHotelActivity.class, itemData.getOrder().getOrderId(), position);
//                break;
//            case OrderEntity.Type.taxi:
//                launchActivity(OrderDetailTaxiTwoActivity.class, itemData.getOrder().getOrderId(), itemData.getOrder().getState(), position);
//                break;
//            case OrderEntity.Type.mobile_charge:
//                launchActivity(OrderDetailMobileChargeActivity.class, itemData.getOrder().getOrderId(), itemData.getOrder().getState(), position);
//                break;
//            case OrderEntity.Type.delivery:
//                launchActivity(OrderDetailDeliveryActivity.class, itemData.getOrder().getOrderId(), itemData.getOrder().getState(), position);
//                break;
//            case OrderEntity.Type.constellation:
//            case OrderEntity.Type.constellation_pair:
//                launchActivity(OrderDetailConstellationActivity.class, itemData.getOrder().getOrderId(), itemData.getOrder().getState(), position);
//                break;
            case OrderEntity.Type.movie:
                launchActivity(OrderDetailCinemaActivity.class, itemData.getOrderId(), itemData.getState(), position);
                break;
        }
    }

    private void launchActivity(Class clazz, final String id, int position) {
        mOrderListFragment.launchActivity(clazz, new BaseFragment.IntentExpand() {
            @Override
            public void extraValue(Intent intent) {
                intent.putExtra(Const.Intent.DATA, id);
                intent.putExtra(Const.Intent.TYPE, id);
            }
        }, 1111);
    }

    private void launchActivity(Class clazz, final String id, final int state, int position) {
        mOrderListFragment.launchActivity(clazz, new BaseFragment.IntentExpand() {
            @Override
            public void extraValue(Intent intent) {
                intent.putExtra(Const.Intent.DATA, id);
                intent.putExtra(Const.Intent.TYPE, state);
            }
        }, 1111);
    }

    String[] OrderState = new String[]{
            "待支付" //0
            , "出票中"//1
            , "待出行"//2
            , "已出行"//3
            , "出票失败"//4
            , "退款中"//5
            , "已退款"//6
            , "已取消"//7
            , "交易关闭"//8
            , "改签中"//9
            , "占座中"//10
            , "取消中"//11
            , "退票中"//12
            , "出票失败 退款中"//13
    };

    private int getColor(int state) {
        switch (state) {
            case OrderEntity.State.unpaid:
                return R.color.colorFF9100;
            case OrderEntity.State.make_ticket:
            case OrderEntity.State.refunding:
            case OrderEntity.State.refunding_ticket:
            case OrderEntity.State.make_ticket_failed:
                return R.color.color007EFF;
            case OrderEntity.State.to_travel:
                return R.color.color7ADF0E;
            case OrderEntity.State.traveled:
            case OrderEntity.State.refunded:
            case OrderEntity.State.canceled:
            case OrderEntity.State.deal_close:
                return R.color.colorB3B3B3;
        }
        return R.color.color007EFF;
    }
}
