package com.centaurstech.sdk.adapter;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.centaurstech.qiwu.QiWu;
import com.centaurstech.qiwu.QiWuAPI;
import com.centaurstech.qiwu.QiWuBot;
import com.centaurstech.qiwu.QiWuConfig;
import com.centaurstech.qiwu.callback.RechargeInfoCallback;
import com.centaurstech.qiwu.common.CommentMethod;
import com.centaurstech.qiwu.common.Const;
import com.centaurstech.qiwu.common.MsgType;
import com.centaurstech.qiwu.common.User;
import com.centaurstech.qiwu.db.MsgDBManager;
import com.centaurstech.qiwu.entity.CinemaEntity;
import com.centaurstech.qiwu.entity.FlightTicketEntity;
import com.centaurstech.qiwu.entity.HoroscopeEntity;
import com.centaurstech.qiwu.entity.HotelEntity;
import com.centaurstech.qiwu.entity.MovieTimeTableEntity;
import com.centaurstech.qiwu.entity.MsgEntity;
import com.centaurstech.qiwu.entity.MsgPhoneRechargeExpand;
import com.centaurstech.qiwu.entity.PhoneContactEntity;
import com.centaurstech.qiwu.entity.RechargeFeeEntity;
import com.centaurstech.qiwu.entity.RechargeInfoEntity;
import com.centaurstech.qiwu.entity.TrainTicketEntity;
import com.centaurstech.qiwu.entity.WeatherEntity;
import com.centaurstech.qiwu.entity.Weathers;
import com.centaurstech.qiwu.manager.AudioPlayManager;
import com.centaurstech.qiwu.manager.PhoneManager;
import com.centaurstech.qiwu.utils.DateUtils;
import com.centaurstech.qiwu.utils.GsonUtils;
import com.centaurstech.qiwu.utils.LogUtils;
import com.centaurstech.qiwu.utils.PhoneUtils;
import com.centaurstech.qiwu.utils.UIUtils;
import com.centaurstech.qiwu.utils.VerifyUtils;
import com.centaurstech.sdk.Player;
import com.centaurstech.sdk.R;
import com.centaurstech.sdk.SDKApplication;
import com.centaurstech.sdk.activity.BaseActivity;
import com.centaurstech.sdk.activity.order.ConfirmFlightOrderActivity;
import com.centaurstech.sdk.activity.order.ConfirmTrainOrderActivity;
import com.centaurstech.sdk.activity.LoginActivity;
import com.centaurstech.sdk.activity.MoviesActivity;
import com.centaurstech.sdk.activity.order.OrderMoreFlightActivity;
import com.centaurstech.sdk.activity.order.OrderMoreTrainActivity;
import com.centaurstech.sdk.databinding.ItemAiChatCallPhoneBinding;
import com.centaurstech.sdk.databinding.ItemAirBinding;
import com.centaurstech.sdk.databinding.ItemCallPhoneBinding;
import com.centaurstech.sdk.databinding.ItemConstellationBinding;
import com.centaurstech.sdk.databinding.ItemConstellationFortuneBinding;
import com.centaurstech.sdk.databinding.ItemConstellationLoversDiscBinding;
import com.centaurstech.sdk.databinding.ItemConstellationPairBinding;
import com.centaurstech.sdk.databinding.ItemExpressBinding;
import com.centaurstech.sdk.databinding.ItemHotelBinding;
import com.centaurstech.sdk.databinding.ItemLeftBinding;
import com.centaurstech.sdk.databinding.ItemListBinding;
import com.centaurstech.sdk.databinding.ItemMovieBinding;
import com.centaurstech.sdk.databinding.ItemMusicBinding;
import com.centaurstech.sdk.databinding.ItemPhoneRechargeBinding;
import com.centaurstech.sdk.databinding.ItemRightBinding;
import com.centaurstech.sdk.databinding.ItemTelephoneChargePriceBinding;
import com.centaurstech.sdk.databinding.ItemTrainBinding;
import com.centaurstech.sdk.databinding.ItemWeatherBinding;
import com.centaurstech.sdk.helper.ConstellationFortuneHelper;
import com.centaurstech.sdk.helper.ConstellationLoversDiscHelper;
import com.centaurstech.sdk.helper.ConstellationPairHelper;
import com.centaurstech.sdk.utils.ConversionUtils;
import com.centaurstech.sdk.utils.ToastUtils;
import com.centaurstech.voice.QiWuVoice;
import com.qiwu.ui.view.adapter.DataBindRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Leon(黄长亮)
 * @describe TODO
 * @date 2019/7/26
 */
public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {


    ArrayList<MsgEntity> mData = new ArrayList<>();

    @Override
    public int getItemViewType(int position) {
        MsgEntity msg = mData.get(position);
        return msg.getMsgType();
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        switch (i) {
            case MsgType.TEXT_USER:
                return new ChatViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_right, viewGroup, false));
            case MsgType.MUSIC:
                return new ChatViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_music, viewGroup, false));
            case MsgType.MOVIES:
                return new ChatViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_movie, viewGroup, false));
            case MsgType.CINEMAS:
            case MsgType.SCREENING_TIMES:
                return new ChatViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_list, viewGroup, false));
            case MsgType.WEATHER:
                return new ChatViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_weather, viewGroup, false));
            case MsgType.TRAIN:
                return new ChatViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_train, viewGroup, false));
            case MsgType.FLIGHT:
                return new ChatViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_air, viewGroup, false));
            case MsgType.HOTEL:
                return new ChatViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_hotel, viewGroup, false));
            case MsgType.EXPRESS:
                return new ChatViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_express, viewGroup, false));
            case MsgType.PHONE_RECHARGE:
                return new ChatViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_phone_recharge, viewGroup, false));
            case MsgType.HOROSCOPE_LEAVE_ALONE:
                return new ChatViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_constellation_pair, viewGroup, false));
            case MsgType.HOROSCOPE_LOVERS_DISC:
                return new ChatViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_constellation_lovers_disc, viewGroup, false));
            case MsgType.HOROSCOPE_FORTUNE:
                return new ChatViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_constellation_fortune, viewGroup, false));
            case MsgType.CALL:
            case MsgType.PHONE_RECHARGE_CONTACT:
                return new ChatViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_ai_chat_call_phone, viewGroup, false));
            case MsgType.TEXT_DEFAULT:
            default:
                return new ChatViewHolder(View.inflate(viewGroup.getContext(), R.layout.item_left, null));
        }
    }


    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder viewHolder, int i) {
        MsgEntity msg = mData.get(i);
        switch (msg.getMsgType()) {
            case MsgType.TEXT_USER:
                ItemRightBinding itemRightBinding = (ItemRightBinding) viewHolder.mViewBinding;
                itemRightBinding.tvText.setText(msg.getMsg());
                setLongClickListener(itemRightBinding.tvText);
                break;
            case MsgType.MUSIC:
                ItemMusicBinding itemMusicBinding = (ItemMusicBinding) viewHolder.mViewBinding;
                setMsg(itemMusicBinding.getRoot(), msg);
                new Player(itemMusicBinding, msg);
                break;
            case MsgType.MOVIES:
                ItemMovieBinding itemMovieBinding = (ItemMovieBinding) viewHolder.mViewBinding;
                MovieAdapter movieAdapter = new MovieAdapter();
                itemMovieBinding.tvMoviesAll.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (isExpire(msg)) {
                            return;
                        }
                        QiWuVoice.getTTS().stop();
                        BaseActivity activity = (BaseActivity) v.getContext();
                        activity.launchActivity(MoviesActivity.class, "key", msg.getData().getMovieInfo().getQueryToken());
                    }
                });
                itemMovieBinding.tvMoviesCount.setText(UIUtils.getString(R.string.hot_movie) + "(" + msg.getData().getMovieInfo().getSize() + ")");
                itemMovieBinding.crv.setLayoutManager(new LinearLayoutManager(itemMovieBinding.crv.getContext()));
                itemMovieBinding.crv.setAdapter(movieAdapter);
                movieAdapter.setEntities(msg.getData().getMovieInfo().getMovies());
                setMsg(itemMovieBinding.getRoot(), msg);
                break;
            case MsgType.CINEMAS:
                ItemListBinding cinemasBinding = (ItemListBinding) viewHolder.mViewBinding;
                CinemaAdapter cinemaAdapter = new CinemaAdapter();
                cinemasBinding.crv.setLayoutManager(new LinearLayoutManager(cinemasBinding.crv.getContext()));
                cinemasBinding.crv.setAdapter(cinemaAdapter);
                cinemaAdapter.setEntities(msg.getData().getCinemas());

                cinemaAdapter.setOnItemClick(new CinemaAdapter.onItemClick() {
                    @Override
                    public void onItemClick(CinemaEntity cinemaEntity) {
                        if (isExpire(msg)) {
                            return;
                        }
                        QiWuAPI.movie.selectCinema(cinemaEntity.getName());
                    }
                });
                setMsg(cinemasBinding.getRoot(), msg);
                break;
            case MsgType.SCREENING_TIMES:
                ItemListBinding screeningBinding = (ItemListBinding) viewHolder.mViewBinding;
                ScreeningAdapter screeningAdapter = new ScreeningAdapter();
                screeningBinding.crv.setLayoutManager(new LinearLayoutManager(screeningBinding.crv.getContext()));
                screeningBinding.crv.setAdapter(screeningAdapter);

                final List<MovieTimeTableEntity.PlanInfosBean> mPlanInfosBean = screeningAdapter.getPlanInfos(msg.getData().getScreeningTimes().getTimeTable());
                screeningAdapter.setPlanInfosBean(mPlanInfosBean);

                screeningAdapter.setOnItemClick(new ScreeningAdapter.onItemClick() {
                    @Override
                    public void onItemClick(MovieTimeTableEntity.PlanInfosBean mPlanInfosBean) {
                        if (isExpire(msg)) {
                            return;
                        }
                        String date = DateUtils.date2Date(msg.getData().getScreeningTimes().getDates().get(0), "yyyy-MM-dd", "yyyy年M月d日");
                        QiWuAPI.movie.selectScreening(msg.getData().getScreeningTimes().getMovieName(),
                                msg.getData().getScreeningTimes().getCinemaName(), date + " " + mPlanInfosBean.getStartTime());
                    }
                });
                setMsg(screeningBinding.getRoot(), msg);
                break;
            case MsgType.WEATHER:
                ItemWeatherBinding itemWeatherBinding = (ItemWeatherBinding) viewHolder.mViewBinding;
                itemWeatherBinding.tvText.setText(msg.getMsg());
                WeatherListAdapter weatherListAdapter;
                if (itemWeatherBinding.rvWeathers.getAdapter() == null) {
                    itemWeatherBinding.rvWeathers.setLayoutManager(new LinearLayoutManager(itemWeatherBinding.rvWeathers.getContext(), LinearLayoutManager.HORIZONTAL, false));
                    final float scale = 0.9f;
                    final float scaleDiff = 0.1f;
                    itemWeatherBinding.rvWeathers.addOnScrollListener(new RecyclerView.OnScrollListener() {
                        @Override
                        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                            super.onScrolled(recyclerView, dx, dy);
                            int childCount = recyclerView.getChildCount();
                            int width = recyclerView.getChildAt(0).getWidth();
                            int padding = (recyclerView.getWidth() - width) / 2;
                            for (int j = 0; j < childCount; j++) {
                                View v = recyclerView.getChildAt(j);
                                float rate = 0;
                                if (v.getLeft() <= padding) {
                                    if (v.getLeft() >= padding - v.getWidth()) {
                                        rate = (padding - v.getLeft()) * 1f / v.getWidth();
                                    } else {
                                        rate = 1;
                                    }
                                    v.setScaleY(1 - rate * scaleDiff);
                                    v.setY(rate * scaleDiff / 2 * v.getHeight());
                                } else {
                                    if (v.getLeft() <= recyclerView.getWidth() - padding) {
                                        rate = (recyclerView.getWidth() - padding - v.getLeft()) * 1f / v.getWidth();
                                    }
                                    v.setScaleY(scale + rate * scaleDiff);
                                    v.setY((1 - (scale + rate * scaleDiff)) / 2 * v.getHeight());
                                }
                            }
                        }
                    });

                    itemWeatherBinding.rvWeathers.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                        @Override
                        public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                            if (itemWeatherBinding.rvWeathers.getChildCount() < 3) {
                                if (itemWeatherBinding.rvWeathers.getChildAt(1) != null) {
                                    View v1 = itemWeatherBinding.rvWeathers.getChildAt(1);
                                    v1.setScaleY(scale);
                                    v1.setY((int) ((1 - scale) / 2 * v1.getHeight()));
                                }
                            } else {
                                if (itemWeatherBinding.rvWeathers.getChildAt(0) != null) {
                                    View v0 = itemWeatherBinding.rvWeathers.getChildAt(0);
                                    v0.setScaleY(scale);
                                    v0.setY((int) ((1 - scale) / 2 * v0.getHeight()));
                                }
                                if (itemWeatherBinding.rvWeathers.getChildAt(2) != null) {
                                    View v2 = itemWeatherBinding.rvWeathers.getChildAt(2);
                                    v2.setScaleY(scale);
                                    v2.setY((int) ((1 - scale) / 2 * v2.getHeight()));
                                }
                            }
                        }
                    });

                    weatherListAdapter = new WeatherListAdapter();
                    itemWeatherBinding.rvWeathers.setAdapter(weatherListAdapter);
                } else {
                    weatherListAdapter = (WeatherListAdapter) itemWeatherBinding.rvWeathers.getAdapter();
                }
                itemWeatherBinding.rvWeathers.setOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                        super.onScrollStateChanged(recyclerView, newState);
                        if (newState == 0) {
                            LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
                            msg.getData().setNumber(String.valueOf(manager.findLastCompletelyVisibleItemPosition()));
                        }
                    }
                });

                weatherListAdapter.clearList();
                for (List<WeatherEntity> ws : msg.getData().getWeathers()) {
                    weatherListAdapter.addData(new Weathers(ws));
                }
                if (msg.getData().getWeathers() != null && msg.getData().getWeathers().size() > 0) {
                    if (msg.getData().getWeathers().get(0).size() > 1) {
                        itemWeatherBinding.tvAnother.setVisibility(View.GONE);
                    } else {
                        itemWeatherBinding.tvAnother.setVisibility(View.VISIBLE);
                    }
                } else {
                    itemWeatherBinding.tvAnother.setVisibility(View.VISIBLE);
                }
                if (TextUtils.isEmpty(msg.getData().getNumber())) {
                    msg.getData().setNumber("0");
                }
                itemWeatherBinding.rvWeathers.scrollToPosition(Integer.parseInt(msg.getData().getNumber()));

                itemWeatherBinding.tvAnother.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (isExpire(msg)) {
                            return;
                        }
                        if (System.currentTimeMillis() - msg.getTimestamp() > 24 * 60 * 60 * 1000) {
                            UIUtils.showToast("天气展示已超时，请重新查询");
                        } else {
                            QiWuVoice.getTTS().stop();
                            AudioPlayManager.getInstance().stop();
                            String sendMsg = "查看" + msg.getData().getWeathers().get(Integer.parseInt(msg.getData().getNumber())).get(0).getLocation() + "未来7天天气";
                            QiWuBot.sendMessage(sendMsg);
                        }
                    }
                });
                break;
            case MsgType.TRAIN:
                ItemTrainBinding itemTrainBinding = (ItemTrainBinding) viewHolder.mViewBinding;
                if (msg.getData().getTrainTicketInfo().getTrainTickets() == null || msg.getData().getTrainTicketInfo().getTrainTickets().size() <= 0)
                    return;
                TrainTicketEntity ticketEntity = null;
                for (TrainTicketEntity train : msg.getData().getTrainTicketInfo().getTrainTickets()) {
                    if (train.getTrainIndex() == msg.getData().getTrainTicketInfo().getTrainIndex()) {
                        ticketEntity = train;
                    }
                }
                if (ticketEntity == null) {
                    ticketEntity = msg.getData().getTrainTicketInfo().getTrainTickets().get(0);
                }
                itemTrainBinding.iTicketInfo.ivTrafficIcon.setImageResource(R.mipmap.train_icon_way);
                itemTrainBinding.iTicketInfo.tvDate.setText(CommentMethod.getTime(ticketEntity.getDeparturetime(), "M月d日"));
                itemTrainBinding.iTicketInfo.tvRoute.setText(ticketEntity.getStart_city() + "-" + ticketEntity.getEnd_city());
                itemTrainBinding.iTicketInfo.tvCabinInfo.setText(ticketEntity.getTrainTicket().getTicketType());
                itemTrainBinding.iTicketInfo.tvOriginAisle.setText(ticketEntity.getStation());
                itemTrainBinding.iTicketInfo.tvOriginTime.setText(CommentMethod.getTime(ticketEntity.getDeparturetime(), "HH:mm"));
                itemTrainBinding.iTicketInfo.tvPassAddress.setText("");
                itemTrainBinding.iTicketInfo.tvDestinationAisle.setText(ticketEntity.getEndstation());
                itemTrainBinding.iTicketInfo.tvDestinationTime.setText(CommentMethod.getTime(ticketEntity.getArrivaltime(), "HH:mm"));
                itemTrainBinding.iTicketInfo.tvVehicleType.setText(ticketEntity.getType() + ticketEntity.getTrainno());
                itemTrainBinding.iTicketInfo.tvMiddleText.setText("");
                itemTrainBinding.iTicketInfo.vLine.setVisibility(View.GONE);
                itemTrainBinding.iTicketInfo.tvDuration.setText(UIUtils.getString(R.string.drive) + ConversionUtils.getDuration(ticketEntity.getCosttime()));
                itemTrainBinding.tvPrice.setText(ticketEntity.getTrainTicket().getTicketPrice());
                long departureTime = CommentMethod.getTimestamp(ticketEntity.getDeparturetime());
                long arrivalTime = CommentMethod.getTimestamp(ticketEntity.getArrivaltime());
                int diffDays = DateUtils.differentDays(departureTime, arrivalTime);
                if (diffDays > 0) {
                    itemTrainBinding.iTicketInfo.tvDiffDays.setText("+" + diffDays);
                    itemTrainBinding.iTicketInfo.tvDiffDays.setVisibility(View.VISIBLE);
                } else {
                    itemTrainBinding.iTicketInfo.tvDiffDays.setVisibility(View.GONE);
                }

                itemTrainBinding.iFooter.tvAnother.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (isExpire(msg)) {
                            return;
                        }
                        if (msg.getData().getTrainTicketInfo().getTrainTickets().size() <= 1) {
                            ToastUtils.show("抱歉，没有更多票了");
                            return;
                        }
                        QiWuVoice.getTTS().stop();
                        BaseActivity activity = (BaseActivity) view.getContext();
                        activity.launchActivity(OrderMoreTrainActivity.class, new BaseActivity.IntentExpand() {
                            @Override
                            public void extraValue(Intent intent) {
                                intent.putExtra(Const.Intent.DATA, (ArrayList) msg.getData().getTrainTicketInfo().getTrainTickets());
                            }
                        });
                    }
                });

                itemTrainBinding.cvTicket.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        BaseActivity activity = (BaseActivity) view.getContext();
                        if (!User.isLogin()) {
                            QiWuVoice.getTTS().stop();
                            activity.launchActivity(LoginActivity.class);
                            return;
                        }
                        if (isExpire(msg)) {
                            return;
                        }
                        if (SDKApplication.getInstance().getAppOrder() <= 1) {
                            ToastUtils.show("暂未开放订单功能如需接入，请联系齐悟");
                            return;
                        }
                        QiWuVoice.getTTS().stop();
                        activity.launchActivity(ConfirmTrainOrderActivity.class, new BaseActivity.IntentExpand() {
                            @Override
                            public void extraValue(Intent intent) {
                                intent.putExtra("train", msg.getData().getTrainTicketInfo().getCurrentTicket());
                            }
                        });
                    }
                });

                setMsg(itemTrainBinding.getRoot(), msg);
                break;
            case MsgType.FLIGHT:
                ItemAirBinding itemAirBinding = (ItemAirBinding) viewHolder.mViewBinding;
                if (msg.getData().getFlightTicketInfo() == null) return;

                FlightTicketEntity flightTicketEntity = msg.getData().getFlightTicketInfo().getFlightTickets().get(0);

                long timestamp1 = CommentMethod.getTimestamp(flightTicketEntity.getDeparture_time().get(0));
                long timestamp2 = CommentMethod.getTimestamp(flightTicketEntity.getArrival_time().get(0));
                itemAirBinding.iTicketInfo.tvDate.setText(DateUtils.timestamp2String(timestamp1, "M" + UIUtils.getString(R.string.month) + "d" + UIUtils.getString(R.string.day2)));
                itemAirBinding.iTicketInfo.tvRoute.setText(flightTicketEntity.getDeparture_city() + "-" + flightTicketEntity.getArrival_city());
                String cabin = flightTicketEntity.getCabin().get(0).length() >= 3 ? flightTicketEntity.getCabin().get(0) : "经济舱";
                itemAirBinding.iTicketInfo.tvCabinInfo.setText(cabin);
                itemAirBinding.iTicketInfo.tvOriginAisle.setText(flightTicketEntity.getDeparture_airport().get(0));
                itemAirBinding.iTicketInfo.tvOriginTime.setText(DateUtils.timestamp2String(timestamp1, "HH:mm"));
                itemAirBinding.iTicketInfo.tvPassAddress.setText((TextUtils.isEmpty(flightTicketEntity.getStop_city()) ? "" : UIUtils.getString(R.string.transit) + " " + flightTicketEntity.getStop_city()));
                itemAirBinding.iTicketInfo.tvDestinationAisle.setText(flightTicketEntity.getArrival_airport().get(0));
                itemAirBinding.iTicketInfo.tvDestinationTime.setText(DateUtils.timestamp2String(timestamp2, "HH:mm"));
                itemAirBinding.iTicketInfo.tvVehicleType.setText(flightTicketEntity.getCarrier().get(0) + flightTicketEntity.getNumber().get(0));
                itemAirBinding.iTicketInfo.tvMiddleText.setText(flightTicketEntity.getFlightTypeFullName().get(0));
                itemAirBinding.iTicketInfo.vLine.setVisibility(View.VISIBLE);
                itemAirBinding.iTicketInfo.tvDuration.setText(UIUtils.getString(R.string.voyage) + ConversionUtils.getDuration(Integer.valueOf(flightTicketEntity.getDuration().get(0))));
                itemAirBinding.tvFuelOil.setText(R.string.not_fuel_oil);
                itemAirBinding.tvPrice.setText(flightTicketEntity.getPrice());

                diffDays = DateUtils.differentDays(timestamp1, timestamp2);
                if (diffDays > 0) {
                    itemAirBinding.iTicketInfo.tvDiffDays.setText("+" + diffDays);
                    itemAirBinding.iTicketInfo.tvDiffDays.setVisibility(View.VISIBLE);
                } else {
                    itemAirBinding.iTicketInfo.tvDiffDays.setVisibility(View.GONE);
                }
                itemAirBinding.iFooter.tvAnother.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        QiWuVoice.getTTS().stop();
                        BaseActivity activity = (BaseActivity) view.getContext();
                        if (msg.getData().getFlightTicketInfo().isReturn_trip()) {
                            if (!TextUtils.isEmpty(msg.getData().getFlightTicketInfo().getRelevanceId())) {
                                if (msg.getData().getFlightTicketInfo().getStage().equals("FIRST")) {
                                    ToastUtils.show(R.string.card_not_support_select);
                                } else {
                                    activity.launchActivity(OrderMoreFlightActivity.class, new BaseActivity.IntentExpand() {
                                        @Override
                                        public void extraValue(Intent intent) {
                                            intent.putExtra(Const.Intent.DATA, msg.getData().getFlightTicketInfo());
                                        }
                                    });
                                }
                            } else {
                                activity.launchActivity(OrderMoreFlightActivity.class, new BaseActivity.IntentExpand() {
                                    @Override
                                    public void extraValue(Intent intent) {
                                        intent.putExtra(Const.Intent.DATA, msg.getData().getFlightTicketInfo());
                                    }
                                });
                            }
                        } else {
                            activity.launchActivity(OrderMoreFlightActivity.class, new BaseActivity.IntentExpand() {
                                @Override
                                public void extraValue(Intent intent) {
                                    intent.putExtra(Const.Intent.DATA, msg.getData().getFlightTicketInfo());
                                }
                            });
                        }
                    }
                });

                itemAirBinding.cvTicket.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        BaseActivity activity = (BaseActivity) view.getContext();
                        if (!User.isLogin()) {
                            QiWuVoice.getTTS().stop();
                            activity.launchActivity(LoginActivity.class);
                            return;
                        }

                        if (isExpire(msg)) {
                            return;
                        }

                        if (SDKApplication.getInstance().getAppOrder() <= 1) {
                            ToastUtils.show("暂未开放订单功能如需接入，请联系齐悟");
                            return;
                        }
                        if (msg.getData().getFlightTicketInfo().isReturn_trip()) {
                            if (!TextUtils.isEmpty(msg.getData().getFlightTicketInfo().getRelevanceId())) {
                                if (msg.getData().getFlightTicketInfo().getStage().equals("FIRST")) {
                                    ToastUtils.show("该卡片暂不支持重新选择");
                                } else {
                                    QiWuVoice.getTTS().stop();
                                    msg.getData().getFlightTicketInfo().setSelectPosition(0);
                                    activity.launchActivity(ConfirmFlightOrderActivity.class, new BaseActivity.IntentExpand() {
                                        @Override
                                        public void extraValue(Intent intent) {
                                            if (msg.getData().getFlightTicketInfo().getStage().equals("FIRST")) {
                                                intent.putExtra(Const.Intent.DATA, msg.getData().getFlightTicketInfo().getFlightTickets().get(0));
                                                if (!TextUtils.isEmpty(msg.getData().getFlightTicketInfo().getRelevanceId())) {
                                                    MsgEntity m1 = MsgDBManager.getInstance().get(msg.getData().getFlightTicketInfo().getRelevanceId());
                                                    if (m1 != null) {
                                                        intent.putExtra(Const.Intent.DATA2, m1.getData().getFlightTicketInfo().getFlightTickets().get(0));
                                                    }
                                                }
                                            } else {
                                                intent.putExtra(Const.Intent.DATA2, msg.getData().getFlightTicketInfo().getFlightTickets().get(0));
                                                if (!TextUtils.isEmpty(msg.getData().getFlightTicketInfo().getRelevanceId())) {
                                                    MsgEntity m2 = MsgDBManager.getInstance().get(msg.getData().getFlightTicketInfo().getRelevanceId());
                                                    if (m2 != null) {
                                                        intent.putExtra(Const.Intent.DATA, m2.getData().getFlightTicketInfo().getFlightTickets().get(0));
                                                    }
                                                }
                                            }
                                        }
                                    });
                                }
                            }
                        } else {
                            QiWuVoice.getTTS().stop();
                            activity.launchActivity(ConfirmFlightOrderActivity.class, new BaseActivity.IntentExpand() {
                                @Override
                                public void extraValue(Intent intent) {
                                    intent.putExtra(Const.Intent.DATA, msg.getData().getFlightTicketInfo().getFlightTickets().get(0));
                                }
                            });
                        }

                    }
                });
                setMsg(itemAirBinding.getRoot(), msg);
                break;
            case MsgType.HOTEL:
                ItemHotelBinding itemHotelBinding = (ItemHotelBinding) viewHolder.mViewBinding;
                if (msg.getData() == null || msg.getData().getHotel() == null) return;
                HotelEntity hotelEntity = msg.getData().getHotel();
                HotelEntity.ProductInfosBean.MostGeneralScoreProductBoBean roomDetailEntity = msg.getData().getHotel().getProduct_infos().getMostGeneralScoreProductBo();
                HotelEntity.ProductInfosBean.MostGeneralScoreProductBoBean.HotelBean typesBean = roomDetailEntity.getHotel();
//                ViewUtils.loadImage(binding.sdvImage, typesBean.getImage_url());
                String startStr = "";
                switch (typesBean.getStar()) {
                    case 1:
                        startStr = "一星级";
                        break;
                    case 2:
                        startStr = "二星级";
                        break;
                    case 3:
                        startStr = "三星级";
                        break;
                    case 4:
                        startStr = "四星级";
                        break;
                    case 5:
                        startStr = "五星级";
                        break;
                    default:
                        startStr = "";
                        break;
                }
                String city = TextUtils.isEmpty(typesBean.getCity()) ? "" : typesBean.getCity();
                itemHotelBinding.tvTimeAndCity.setText(DateUtils.date2Date(hotelEntity.getArrival_date(), "yyyy-MM-dd", "M月d日") + "-" +
                        DateUtils.date2Date(hotelEntity.getDeparture_date(), "yyyy-MM-dd", "M月d日") + " " + city);
                itemHotelBinding.tvHotelName.setText(typesBean.getName());
                String score = typesBean.getScore() == 0 ? "暂无评分  " : typesBean.getScore() + "分";
                itemHotelBinding.tvStarBar.setText(score + "  " + startStr);
                itemHotelBinding.tvPrice.setText(roomDetailEntity.getAvg_price());
                itemHotelBinding.tvRoomType.setText("" + roomDetailEntity.getRoom_type_name());
                String tvDistrict = "";
                if (TextUtils.isEmpty(msg.getData().getHotel().getDestination()) || TextUtils.isEmpty(typesBean.getDistance())) {
                    tvDistrict = "<font color=\"#000000\">" + (TextUtils.isEmpty(typesBean.getDistrict()) ? "" : typesBean.getDistrict()) + "</font>";
                    itemHotelBinding.tvDistrict.setText(Html.fromHtml(tvDistrict));
                } else {
                    tvDistrict = "<font color=\"#007EFF\">" + UIUtils.getString(R.string.distance)
                            + msg.getData().getHotel().getDestination()
                            + ConversionUtils.distance2(Float.valueOf(typesBean.getDistance()) * 1000)
                            + "</font> "
                            + "<font color=\"#000000\">" + (TextUtils.isEmpty(typesBean.getDistrict()) ? "" : typesBean.getDistrict()) + "</font>";
                    itemHotelBinding.tvDistrict.setText(Html.fromHtml(tvDistrict));
                }

                if (ConversionUtils.getScreenWidthLevel() == ConversionUtils.ScreenWidthLevel.MIX) {
                    itemHotelBinding.tvRoomType.setVisibility(View.GONE);
                }

                itemHotelBinding.cvTicket.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!User.isLogin()) {
                            QiWuVoice.getTTS().stop();
                            BaseActivity activity = (BaseActivity) view.getContext();
                            activity.launchActivity(LoginActivity.class);
                            return;
                        }
                        if (isExpire(msg)) {
                            return;
                        }
                        ToastUtils.show("暂未开放订单功能如需接入，请联系齐悟");
                    }
                });
//                itemHotelBinding.iFooter.tvAnother.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        BaseActivity activity = (BaseActivity) view.getContext();
//                        activity.launchActivity(HotelListActivity.class, msg.getData().getHotel(), position);
//                    }
//                });
                setMsg(itemHotelBinding.getRoot(), msg);
                break;
            case MsgType.EXPRESS:
                ItemExpressBinding itemExpressBinding = (ItemExpressBinding) viewHolder.mViewBinding;
                itemExpressBinding.tvSendNameAndPhone.setText(msg.getData().getExpress().getSender().getName() + "     " + msg.getData().getExpress().getSender().getMobile());
                itemExpressBinding.tvSendAddress.setText(msg.getData().getExpress().getSender().getAddress());
                itemExpressBinding.tvCollectNameAndPhone.setText(msg.getData().getExpress().getReceiver().getName() + "     " + msg.getData().getExpress().getReceiver().getMobile());
                itemExpressBinding.tvCollectAddress.setText(msg.getData().getExpress().getReceiver().getAddress());
                if (TextUtils.isEmpty(msg.getData().getExpress().getWeight())) {
                    itemExpressBinding.tvNumber.setText("5KG");
                    msg.getData().getExpress().setWeight("5");
                } else {
                    int weight = Integer.valueOf(msg.getData().getExpress().getWeight());
                    if (weight >= 5) {
                        itemExpressBinding.tvNumber.setText(msg.getData().getExpress().getWeight() + "KG");
                    } else {
                        itemExpressBinding.tvNumber.setText("5KG");
                        msg.getData().getExpress().setWeight("5");
                    }
                    weight = Integer.valueOf(msg.getData().getExpress().getWeight());
                    if (weight > 5) {
                        itemExpressBinding.ivReduce.setImageResource(R.mipmap.shansong_card_icon_cutbacke_s);
                    } else {
                        itemExpressBinding.ivReduce.setImageResource(R.mipmap.shansong_card_icon_cutbacke_d);
                    }
                    if (weight < 99) {
                        itemExpressBinding.ivAdd.setImageResource(R.mipmap.shansong_card_icon_increase_s);
                    } else {
                        itemExpressBinding.ivAdd.setImageResource(R.mipmap.shansong_card_icon_increase);
                    }
                }

                if (TextUtils.isEmpty(msg.getData().getExpress().getPrice())) {
                    itemExpressBinding.tvSubmitOrder.setText(UIUtils.getString(R.string.get_price));
                    itemExpressBinding.tvPrice.setText("￥--");
                } else {
                    if (!TextUtils.isEmpty(msg.getData().getExpress().getChangeSubmit())) {
                        itemExpressBinding.tvSubmitOrder.setText(msg.getData().getExpress().getChangeSubmit());
                        itemExpressBinding.tvPrice.setText(TextUtils.isEmpty(msg.getData().getExpress().getPrice()) ? "￥--" : "￥" + msg.getData().getExpress().getPrice());
                    } else {
                        itemExpressBinding.tvSubmitOrder.setText(UIUtils.getString(R.string.submit_order));
                        itemExpressBinding.tvPrice.setText("￥" + msg.getData().getExpress().getPrice());
                    }
                }

                itemExpressBinding.llSubmitOrder.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (isExpire(msg)) {
                            return;
                        }
                        ToastUtils.show("暂未开放订单功能如需接入，请联系齐悟");
                    }
                });
                setMsg(itemExpressBinding.getRoot(), msg);
                break;
            case MsgType.PHONE_RECHARGE:
                ItemPhoneRechargeBinding binding = (ItemPhoneRechargeBinding) viewHolder.mViewBinding;
                setTelephoneCharge(binding, msg, i, -1);
                break;
            case MsgType.HOROSCOPE_LEAVE_ALONE:
                ItemConstellationPairBinding pairBinding = (ItemConstellationPairBinding) viewHolder.mViewBinding;
                ConstellationPairHelper.getInstance().setData(pairBinding, msg, i, this);
                setMsg(pairBinding.getRoot(), msg);
                break;
            case MsgType.HOROSCOPE_LOVERS_DISC:
                ItemConstellationLoversDiscBinding loversDiscBinding = (ItemConstellationLoversDiscBinding) viewHolder.mViewBinding;
                ConstellationLoversDiscHelper.getInstance().setData(loversDiscBinding, msg, i, this);
                setMsg(loversDiscBinding.getRoot(), msg);
                break;
            case MsgType.HOROSCOPE_FORTUNE:
                ItemConstellationFortuneBinding fortuneBinding = (ItemConstellationFortuneBinding) viewHolder.mViewBinding;
                ConstellationFortuneHelper.getInstance().setData(fortuneBinding, msg, i, this);
                setMsg(fortuneBinding.getRoot(), msg);
                break;
            case MsgType.HOROSCOPE:
                ItemConstellationBinding constellationBinding = (ItemConstellationBinding) viewHolder.mViewBinding;
                final HoroscopeEntity constellation = msg.getData().getHoroscope();
                constellationBinding.tvTitle.setText(constellation.getOwnBirthCity());
                if (!TextUtils.isEmpty(constellation.getOwnLivingCity())) {
                    constellationBinding.tvDate.setText(constellation.getOwnLivingCity());
                } else {
                    constellationBinding.tvDate.setText("");
                }
                if (!TextUtils.isEmpty(constellation.getBusinessType())) {
                    constellationBinding.tvTestReport.setText(constellation.getBusinessType());
                    constellationBinding.tvTestReport.setVisibility(View.VISIBLE);
                } else {
                    constellationBinding.tvTestReport.setText("");
                    constellationBinding.tvTestReport.setVisibility(View.GONE);
                }

                constellationBinding.cvListenAllReport.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (isExpire(msg)) {
                            return;
                        }
//                        new ConstellationHelper().toReadReport((BaseActivity) mContext, constellation.getOwnBirthProvince(), true);
                    }
                });
                constellationBinding.cvReadAllReport.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (isExpire(msg)) {
                            return;
                        }
//                        new ConstellationHelper().toReadReport((BaseActivity) mContext, constellation.getOwnBirthProvince(), false);
                    }
                });
                setMsg(constellationBinding.getRoot(), msg);
                break;
            case MsgType.CALL:
                ItemAiChatCallPhoneBinding itemAiChatCallPhoneBinding = (ItemAiChatCallPhoneBinding) viewHolder.mViewBinding;
                CallPhoneAdapter callPhoneAdapter;
                if (itemAiChatCallPhoneBinding.rvPhones.getAdapter() == null) {
                    itemAiChatCallPhoneBinding.rvPhones.setLayoutManager(new LinearLayoutManager(itemAiChatCallPhoneBinding.getRoot().getContext()));
                    callPhoneAdapter = new CallPhoneAdapter(itemAiChatCallPhoneBinding.getRoot().getContext(), msg);
                    itemAiChatCallPhoneBinding.rvPhones.setAdapter(callPhoneAdapter);
                    callPhoneAdapter.setOnItemClickListener(new DataBindRecyclerViewAdapter.OnItemClickListener<PhoneContactEntity, ItemCallPhoneBinding>() {
                        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
                        @Override
                        public void onItemClick(ItemCallPhoneBinding binding, PhoneContactEntity itemData, int position, View v) {
                            PhoneUtils.callPhone2(itemData.getNumber(), PhoneManager.getInstance().getSubscriberId());
                        }
                    });
                } else {
                    callPhoneAdapter = (CallPhoneAdapter) itemAiChatCallPhoneBinding.rvPhones.getAdapter();
                }
                FrameLayout.LayoutParams rlp = (FrameLayout.LayoutParams) itemAiChatCallPhoneBinding.rvPhones.getLayoutParams();
                rlp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                if (msg.getData().getContactInfo().getContacts() != null && msg.getData().getContactInfo().getContacts().size() > 0) {
                    itemAiChatCallPhoneBinding.cvPhones.setVisibility(View.VISIBLE);
                    if (msg.getData().getContactInfo().getContacts().size() > 3) {
                        rlp.height = (int) (UIUtils.dip2Px(82) * 3.5) + UIUtils.dip2Px(12);
                    } else {
                        rlp.height = UIUtils.dip2Px(82) * msg.getData().getContactInfo().getContacts().size() + UIUtils.dip2Px(12);
                    }
                } else {
                    itemAiChatCallPhoneBinding.cvPhones.setVisibility(View.GONE);
                }
                itemAiChatCallPhoneBinding.rvPhones.requestLayout();
                callPhoneAdapter.setData(msg.getData().getContactInfo().getContacts());
                setMsg(itemAiChatCallPhoneBinding.getRoot(), msg);
                break;
            case MsgType.PHONE_RECHARGE_CONTACT:
                ItemAiChatCallPhoneBinding chatCallPhoneBinding = (ItemAiChatCallPhoneBinding) viewHolder.mViewBinding;
                setTelephoneChargePhone(chatCallPhoneBinding, msg, i);
                break;
            case MsgType.TEXT_DEFAULT:
            default:
                ItemLeftBinding leftBinding = (ItemLeftBinding) viewHolder.mViewBinding;
                setMsg(leftBinding.getRoot(), msg);
                break;
        }
    }

    private void setTelephoneCharge(final ItemPhoneRechargeBinding itemPhoneRechargeBinding,
                                    final MsgEntity itemData, final int p, int index) {
        itemPhoneRechargeBinding.etPhoneNumber.clearTextChangedListener();
        itemPhoneRechargeBinding.etPhoneNumber.setText(itemData.getData().getRechargeInfo().getContact().getNumber());
        itemPhoneRechargeBinding.etPhoneNumber.setText(itemData.getData().getRechargeInfo().getContact().getNumber());
        if (index != -1) {
            itemPhoneRechargeBinding.etPhoneNumber.setSelection(index);
        } else {
            itemPhoneRechargeBinding.etPhoneNumber.setSelection(itemData.getData().getRechargeInfo().getContact().getNumber().length());
        }
        String name = TextUtils.isEmpty(itemData.getData().getRechargeInfo().getContact().getName()) ? "" : itemData.getData().getRechargeInfo().getContact().getName();

        itemPhoneRechargeBinding.rvChargeFee.setLayoutManager(new GridLayoutManager(itemPhoneRechargeBinding.rvChargeFee.getContext(), 4));
        final TelephoneChargePriceAdapter adapter;
        if (itemPhoneRechargeBinding.rvChargeFee.getAdapter() == null) {
            adapter = new TelephoneChargePriceAdapter();
            itemPhoneRechargeBinding.rvChargeFee.setAdapter(adapter);
        } else {
            adapter = (TelephoneChargePriceAdapter) itemPhoneRechargeBinding.rvChargeFee.getAdapter();
        }
        adapter.setEnable(VerifyUtils.phoneNumber(itemData.getData().getRechargeInfo().getContact().getNumber()));
        adapter.setMoney(itemData.getData().getRechargeInfo().getMoney());
        adapter.setPosition(itemData.getData().getRechargeInfo().getMoneyIndex());
        adapter.setRechargeFeeEntitys(itemData.getData().getRechargeInfo().getFeeList());
        itemData.getData().getRechargeInfo().setMoney(adapter.getMoney());
        itemData.getData().getRechargeInfo().setMoneyIndex(adapter.getPosition());

        if (VerifyUtils.phoneNumber(itemData.getData().getRechargeInfo().getContact().getNumber())) {
            itemPhoneRechargeBinding.tvPHoneNumberInfo.setText(name + "（" + itemData.getData().getRechargeInfo().getContact().getDisplayName() + "）");
            itemPhoneRechargeBinding.tvPHoneNumberInfo.setTextColor(UIUtils.getColor(R.color.colorB3B3B3));
        } else {
            itemPhoneRechargeBinding.tvPHoneNumberInfo.setText("请输入正确的手机号");
            itemPhoneRechargeBinding.tvPHoneNumberInfo.setTextColor(UIUtils.getColor(R.color.colorE7143A));
            itemData.getData().getRechargeInfo().setMoneyIndex(-1);
            itemData.getData().getRechargeInfo().setMoney("");
        }

        adapter.setOnItemCLick(new TelephoneChargePriceAdapter.onItemCLickListener() {
            @Override
            public void onItemCLick(ItemTelephoneChargePriceBinding binding, RechargeFeeEntity mRechargeFeeEntity, int position) {
                if (isExpire(itemData)) {
                    return;
                }
                if (!VerifyUtils.phoneNumber(itemData.getData().getRechargeInfo().getContact().getNumber())) {
                    return;
                }
                if (Double.valueOf(mRechargeFeeEntity.getUser_price()) == 0) return;
                if (adapter.getPosition() == position) return;
                if (adapter.getIvBackground() != null) {
                    adapter.getIvBackground().setImageResource(R.mipmap.charge_price_bg_white);
                }
                if (adapter.getTvSellingPrice() != null) {
                    adapter.getTvSellingPrice().setTextColor(UIUtils.getColor(R.color.colorFF9100));
                }
                itemPhoneRechargeBinding.ivToCharge.setImageResource(R.mipmap.charge_btn);
                adapter.setMoney(mRechargeFeeEntity.getAmount());
                adapter.setPosition(position);
                adapter.setIvBackground(binding.ivBackground);
                adapter.setTvSellingPrice(binding.tvSellingPrice);
                adapter.getTvSellingPrice().setTextColor(UIUtils.getColor(R.color.colorFF2A2A2A));
                adapter.getIvBackground().setImageResource(R.mipmap.charge_price_bg_yellow);
                itemData.getData().getRechargeInfo().setMoney(mRechargeFeeEntity.getAmount());
                itemData.getData().getRechargeInfo().setMoneyIndex(position);
            }
        });

        itemPhoneRechargeBinding.ivToCharge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isExpire(itemData)) {
                    return;
                }
                ToastUtils.show("暂未开放订单功能如需接入，请联系齐悟");
            }
        });
        if (!isExpire(itemData, "")) {
            itemPhoneRechargeBinding.etPhoneNumber.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (s.toString().equals(itemData.getData().getRechargeInfo().getContact().getNumber()))
                        return;
                    itemData.getData().getRechargeInfo().getContact().setNumber(s.toString());
                    itemData.getData().getRechargeInfo().getContact().setName(s.toString().equals(User.getMobilePhone()) ? UIUtils.getString(R.string.default_number) : UIUtils.getString(R.string.unknown_number));
                    for (PhoneContactEntity contact : PhoneManager.getInstance().getContacts()) {
                        if (contact.getNumber().equals(s.toString())) {
                            itemData.getData().getRechargeInfo().getContact().setName(contact.getName());
                        }
                    }
                    if (VerifyUtils.phoneNumber(s.toString())) {
                        new MsgPhoneRechargeExpand().query("", true, null);
                        new MsgPhoneRechargeExpand().checkPhoneNumber(itemData.getData().getRechargeInfo(), new RechargeInfoCallback() {

                            @Override
                            public void callback(RechargeInfoEntity chargeInfo) {
                                MsgDBManager.getInstance().save(itemData);
                                setTelephoneCharge(itemPhoneRechargeBinding, itemData, p, itemPhoneRechargeBinding.etPhoneNumber.getSelectionStart());
                            }
                        });
                    } else {
                        itemData.getData().getRechargeInfo().setMoney("");
                        itemData.getData().getRechargeInfo().setMoneyIndex(-1);
                        new MsgPhoneRechargeExpand().setInvalidChargeInfo(itemData.getData().getRechargeInfo());
                        MsgDBManager.getInstance().save(itemData);
                        setTelephoneCharge(itemPhoneRechargeBinding, itemData, p, itemPhoneRechargeBinding.etPhoneNumber.getSelectionStart());
                    }
                }
            });
        }
        setMsg(itemPhoneRechargeBinding.getRoot(), itemData);
    }

    private void setTelephoneChargePhone(final ItemAiChatCallPhoneBinding binding,
                                         final MsgEntity itemData, final int p) {
        LogUtils.sf("setTelephoneChargePhone" + GsonUtils.toJson(itemData));
        CallPhoneAdapter callPhoneAdapter;
        if (binding.rvPhones.getAdapter() == null) {
            binding.rvPhones.setLayoutManager(new LinearLayoutManager(binding.getRoot().getContext()));
            callPhoneAdapter = new CallPhoneAdapter(binding.getRoot().getContext(), itemData);
            binding.rvPhones.setAdapter(callPhoneAdapter);
            callPhoneAdapter.setOnItemClickListener(new DataBindRecyclerViewAdapter.OnItemClickListener<PhoneContactEntity, ItemCallPhoneBinding>() {
                @Override
                public void onItemClick(ItemCallPhoneBinding binding, PhoneContactEntity item, int position, View v) {
                    String money = "0";
                    if (itemData.getData().getRechargeInfo().getContact() != null) {
                        money = itemData.getData().getRechargeInfo().getMoney();
                    }
                    QiWuVoice.getTTS().stop();
                    new MsgPhoneRechargeExpand().createRechargeInfo(item, money);
                }
            });
        } else {
            callPhoneAdapter = (CallPhoneAdapter) binding.rvPhones.getAdapter();
        }
        callPhoneAdapter.setHideIcon(true);
        FrameLayout.LayoutParams rlp = (FrameLayout.LayoutParams) binding.rvPhones.getLayoutParams();
        rlp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        if (null != itemData.getData().getContactInfo() && itemData.getData().getContactInfo().getContacts() != null && itemData.getData().getContactInfo().getContacts().size() > 0) {
            binding.cvPhones.setVisibility(View.VISIBLE);
            if (itemData.getData().getContactInfo().getContacts().size() > 3) {
                rlp.height = (int) (UIUtils.dip2Px(82) * 3.5) + UIUtils.dip2Px(12);
            } else {
                rlp.height = UIUtils.dip2Px(82) * itemData.getData().getContactInfo().getContacts().size() + UIUtils.dip2Px(12);
            }
        } else {
            binding.cvPhones.setVisibility(View.GONE);
        }
        binding.rvPhones.requestLayout();
        if (null != itemData.getData().getContactInfo()) {
            callPhoneAdapter.setData(itemData.getData().getContactInfo().getContacts());
        }
        setMsg(binding.getRoot(), itemData);
    }

    private void setMsg(View view, MsgEntity msg) {
        TextView tvText = view.findViewById(R.id.tvText);
        tvText.setText(msg.getMsg());
        setLongClickListener(tvText);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public ArrayList<MsgEntity> getData() {
        return mData;
    }

    public void setData(ArrayList<MsgEntity> data) {
        mData = data;
    }


    static class ChatViewHolder extends RecyclerView.ViewHolder {
        ViewDataBinding mViewBinding;

        ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            mViewBinding = DataBindingUtil.bind(itemView);
        }
    }

    private boolean isExpire(MsgEntity itemData) {
        return isExpire(itemData, UIUtils.getString(R.string.taxi_invalid));
    }

    private boolean isExpire(MsgEntity itemData, String text) {
        if (System.currentTimeMillis() - itemData.getTimestamp() > 5 * 60 * 1000) {
            if (!TextUtils.isEmpty(text))
                ToastUtils.show(text);
        }
        return System.currentTimeMillis() - itemData.getTimestamp() > 5 * 60 * 1000;
    }

    private void setLongClickListener(TextView tv) {
        tv.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ClipboardManager cm = (ClipboardManager) QiWu.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                cm.setText(tv.getText());
                UIUtils.showToast("已复制");
                return true;
            }
        });
    }
}
