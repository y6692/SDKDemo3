package com.centaurstech.sdk.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.centaurstech.qiwu.QiWuConfig;
import com.centaurstech.qiwu.QiWuPlayerConfig;
import com.centaurstech.qiwu.common.Const;
import com.centaurstech.qiwu.entity.FlightTicketInfoEntity;
import com.centaurstech.qiwu.manager.AudioPlayManager;
import com.centaurstech.qiwu.manager.MusicPlayManager;
import com.centaurstech.qiwu.utils.UIUtils;
import com.centaurstech.sdk.SDKApplication;
import com.centaurstech.sdk.activity.movies.SelectedSeatActivity;
import com.centaurstech.sdk.activity.order.ConfirmFlightOrderActivity;
import com.centaurstech.sdk.activity.order.ConfirmTrainOrderActivity;
import com.centaurstech.sdk.common.Const2;
import com.centaurstech.qiwu.entity.EventBusEntity;
import com.centaurstech.sdk.listener.OnMusicDeleteListener;
import com.centaurstech.sdk.utils.NetWorkUtils;
import com.centaurstech.sdk.utils.ToastUtils;
import com.centaurstech.sdk.view.ListeningButton;
import com.centaurstech.sdk.view.MainBottomBar;
import com.centaurstech.sdk.view.PopDialog;
import com.centaurstech.voice.QiWuVoice;
import com.centaurstech.qiwu.QiWu;
import com.centaurstech.qiwu.QiWuAPI;
import com.centaurstech.qiwu.QiWuBot;
import com.centaurstech.qiwu.QiWuPlayer;
import com.centaurstech.qiwu.callback.APICallback;
import com.centaurstech.qiwu.common.MsgType;
import com.centaurstech.qiwu.common.User;
import com.centaurstech.qiwu.db.MsgDBManager;
import com.centaurstech.qiwu.entity.APIEntity;
import com.centaurstech.qiwu.entity.MsgEntity;
import com.centaurstech.qiwu.entity.UserInfoEntity;
import com.centaurstech.qiwu.listener.BotMessageListener;
import com.centaurstech.qiwu.manager.WebMusicManager;
import com.centaurstech.qiwu.utils.GsonUtils;
import com.centaurstech.qiwu.utils.LogUtils;
import com.centaurstech.sdk.adapter.ChatAdapter;
import com.centaurstech.sdk.R;
import com.centaurstech.sdk.voice.MyAsrListener;
import com.centaurstech.sdk.voice.MyPlayStateListener;
import com.centaurstech.sdk.voice.MyWakeupListener;
import com.centaurstech.sdk.databinding.ActivityMainBinding;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static com.centaurstech.qiwu.common.MsgType.HOROSCOPE_LEAVE_ALONE_CONFIRM;

public class MainActivity extends BaseActivity implements MyAsrListener.VoiceListener {

    private static String TAG = "MainActivity";
    public ActivityMainBinding mMainBinding;
    ArrayList<MsgEntity> mData = new ArrayList<>();

    private MyAsrListener mMyAsrListener = new MyAsrListener();

    private MyWakeupListener mMyWakeupListener = new MyWakeupListener();

    private List<OnMusicDeleteListener> mOnMusicDeleteListeners = new ArrayList<>();

    private MyPlayStateListener playStateListener = new MyPlayStateListener();

    private ChatAdapter chatAdapter;

    private static MainActivity mMainActivity;

    public static MainActivity getThis() {
        return mMainActivity;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.CALL_PHONE,
                    Manifest.permission.READ_CALL_LOG,
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.READ_CONTACTS,
                    Manifest.permission.WRITE_CONTACTS,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.CALL_PHONE,
                    Manifest.permission.READ_CALL_LOG,
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.READ_CONTACTS,
                    Manifest.permission.WRITE_CONTACTS,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
        mMainActivity = this;
        initView();
        initListener();
    }


    @Override
    public boolean useEventBus() {
        return true;
    }

    private void initView() {
        mMainBinding.iHeadLayout.tvTitle.setText(R.string.app_name);
        mMainBinding.rvList.setLayoutManager(new LinearLayoutManager(this));
        chatAdapter = new ChatAdapter();
        mMainBinding.rvList.setAdapter(chatAdapter);
        chatAdapter.setData(mData);

        mMainBinding.iHeadLayout.btnNavBack.setImageResource(R.mipmap.toolbar_icon_peo);

        mMainBinding.iHeadLayout.btnNavBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void initListener() {
        mMyAsrListener.setVoiceListener(this);
        mMainBinding.mainBottomBar.setOnViewClickListener(new MainBottomBar.OnViewClickListener() {
            @Override
            public void OnClick(View view) {
                try {
                    switch (view.getId()) {
                        case R.id.itemUser:
                            QiWuVoice.getTTS().stop();
                            AudioPlayManager.getInstance().stop();
                            QiWuVoice.getWakeup().start();
                            Intent intent = new Intent(MainActivity.this, PageIndexActivity.class);
                            startActivity(intent);
                            break;
                        case R.id.itemKeyboard:
                            mMainBinding.mainBottomBar.showEditText(true);
                            break;
                        case R.id.btnListening:
                            if (NetWorkUtils.getCurrentNetworkState(MainActivity.this) == null) {
                                // 没有网络
                                UIUtils.showToast(UIUtils.getString(R.string.net_error));
                                return;
                            }
                            AudioPlayManager.getInstance().stop();
                            if (mMainBinding.mainBottomBar.getBtnListening().isRunning())
                                return;
                            if (mMainBinding.mainBottomBar.getBtnListening().getState() == ListeningButton.State.IDLE) {
                                mMainBinding.mainBottomBar.getBtnListening().setState(ListeningButton.State.LISTENING);
                            } else if (mMainBinding.mainBottomBar.getBtnListening().getState() == ListeningButton.State.THINK) {
                                mMainBinding.mainBottomBar.getBtnListening().setState(ListeningButton.State.LISTENING);
                            } else if (mMainBinding.mainBottomBar.getBtnListening().getState() == ListeningButton.State.LISTENING) {
                                AudioPlayManager.getInstance().release();
                                mMainBinding.mainBottomBar.getBtnListening().setState(ListeningButton.State.THINK, true, false, false);
                            }
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        QiWuBot.setBotMessageListener(new BotMessageListener() {

            @Override
            public void onBefore(MsgEntity entity) {
                QiWuVoice.getTTS().stop();
                QiWuPlayer.getInstance().stop();
                MusicPlayManager.getInstance().pause();

                Log.e("ma===onBefore", entity.isSuccess()+"====" +entity.getMsg()+"====" + entity.getMsgType()+"====" + entity.getPlayType());

                mData.add(entity);
                mMainBinding.rvList.getAdapter().notifyDataSetChanged();
                mMainBinding.mainBottomBar.getBtnListening().setState(ListeningButton.State.THINK);
                toListViewBottom();
            }

            @Override
            public void onError(int code, String sn) {

            }

            @Override
            public void onSuccess(MsgEntity entity, String data) {
                mMainBinding.mainBottomBar.getBtnListening().setState(ListeningButton.State.IDLE);
                LogUtils.sf("onSuccess : " + GsonUtils.toJson(entity));
                QiWuVoice.getTTS().speech(entity.getMsg(), entity.getPlayType());

                Log.e("ma===BotMessage0", GsonUtils.toJson(entity)+"====");

                Log.e("ma===BotMessage", entity.isSuccess()+"====" +entity.getMsg()+"====" + entity.getMsgType()+"====" + entity.getPlayType());

                MsgEntity msgEntity;
                Map<String, Serializable> serializableMap;
                if (entity.isSuccess()) {
                    switch (entity.getMsgType()) {
                        case MsgType.MUSIC:
                            UIUtils.postTaskSafely(new Runnable() {
                                @Override
                                public void run() {
                                    for (int i = mData.size() - 1; i >= 0; i--) {
                                        MsgEntity msgEntity = mData.get(i);
                                        if (msgEntity.getMsgType() == 80100) {
                                            msgEntity.setExpire(1);
                                            MsgDBManager.getInstance().update(msgEntity);
                                        }
                                    }
                                }
                            });
                            QiWuPlayer.getInstance().setData(entity);
                            break;
                        case MsgType.CALL:
                            entity.getData().getContactInfo();
                            break;
                        case MsgType.HOTEL:
                            break;
                        case MsgType.TAXI_CONFIRM:
                            //TODO 如要开放订单请修改SDKApplication appOrder 为 0
                            if (SDKApplication.getInstance().getAppOrder() <= 1) {
                                ToastUtils.show("暂未开放订单功能如需接入，请联系齐悟");
                                return;
                            } else {
                                ToastUtils.show("暂未开放订单功能如需接入，请联系齐悟");
                            }
                            break;
                        case MsgType.CONFIRM_HOTEL_ORDER:
//                            TestAPI.Order.bookingHotel();
                            if (SDKApplication.getInstance().getAppOrder() <= 1) {
                                ToastUtils.show("暂未开放订单功能如需接入，请联系齐悟");
                                return;
                            } else {
                                ToastUtils.show("暂未开放订单功能如需接入，请联系齐悟");
                            }
                            break;
                        case MsgType.PHONE_RECHARGE_CONFIRM:
//                            TestAPI.Order.phoneRecharge(MainActivity.this);
                            if (SDKApplication.getInstance().getAppOrder() <= 1) {
                                ToastUtils.show("暂未开放订单功能如需接入，请联系齐悟");
                                return;
                            } else {
                                ToastUtils.show("暂未开放订单功能如需接入，请联系齐悟");
                            }
                            break;
                        case MsgType.CONFIRM_FLIGHT_ORDER:
                            if (SDKApplication.getInstance().getAppOrder() <= 1) {
                                ToastUtils.show("暂未开放订单功能如需接入，请联系齐悟");
                                return;
                            }
                            msgEntity = MsgDBManager.getInstance().getLastFromType(MsgType.FLIGHT);
                            serializableMap = new HashMap<>();
                            FlightTicketInfoEntity flightTicketInfo = msgEntity.getData().getFlightTicketInfo();
                            if (flightTicketInfo.getStage().equals("FIRST")) { // 是去程
                                serializableMap.put("data", msgEntity.getData().getFlightTicketInfo().getFlightTickets().get(0));
                                if (!TextUtils.isEmpty(flightTicketInfo.getRelevanceId())) { // 如果有关联id，说明是往返票
                                    MsgEntity m2 = MsgDBManager.getInstance().get(flightTicketInfo.getRelevanceId());
                                    if (m2 != null) {
                                        serializableMap.put("data2", m2.getData().getFlightTicketInfo().getFlightTickets().get(0));
                                    }
                                }
                            } else { // 是返程
                                if (!TextUtils.isEmpty(flightTicketInfo.getRelevanceId())) { // 如果有关联id，说明是往返票
                                    MsgEntity m1 = MsgDBManager.getInstance().get(flightTicketInfo.getRelevanceId());
                                    if (m1 != null) {
                                        serializableMap.put("data", m1.getData().getFlightTicketInfo().getFlightTickets().get(0));
                                    }
                                }
                                serializableMap.put("data2", msgEntity.getData().getFlightTicketInfo().getFlightTickets().get(0));
                            }
                            launchActivity(ConfirmFlightOrderActivity.class, serializableMap);
                            break;
                        case MsgType.CONFIRM_TRAIN_ORDER:
                            if (SDKApplication.getInstance().getAppOrder() <= 1) {
                                ToastUtils.show("暂未开放订单功能如需接入，请联系齐悟");
                                return;
                            }
                            msgEntity = MsgDBManager.getInstance().getLastFromType(MsgType.TRAIN);
                            serializableMap = new HashMap<>();
                            serializableMap.put("train", msgEntity.getData().getTrainTicketInfo().getCurrentTicket());
                            launchActivity(ConfirmTrainOrderActivity.class, serializableMap);
                            break;
                        case MsgType.EXPRESS_CONFIRM:
//                            TestAPI.Order.bookingExpress(MainActivity.this);
                            if (SDKApplication.getInstance().getAppOrder() <= 1) {
                                ToastUtils.show("暂未开放订单功能如需接入，请联系齐悟");
                                return;
                            } else {
                                ToastUtils.show("暂未开放订单功能如需接入，请联系齐悟");
                            }
                            break;
                        case MsgType.HOROSCOPE_FORTUNE_CONFIRM:
//                            msgEntity = MsgDBManager.getInstance().getLastFromType(MsgType.HOROSCOPE_FORTUNE);
//                            QiWuAPI.horoscope.bookingFortune(msgEntity.getData().getHoroscope(), 1, new APICallback<APIEntity<String>>() {
//                                @Override
//                                public void onSuccess(APIEntity<String> response) {
//                                    if (response.isSuccess())
//                                        launchActivity(HoroscopeFortuneOrderDetailActivity.class, "id", response.getData());
//                                }
//                            });
                            if (SDKApplication.getInstance().getAppOrder() <= 1) {
                                ToastUtils.show("暂未开放订单功能如需接入，请联系齐悟");
                                return;
                            } else {
                                ToastUtils.show("暂未开放订单功能如需接入，请联系齐悟");
                            }
                            break;
                        case HOROSCOPE_LEAVE_ALONE_CONFIRM:
//                            msgEntity = MsgDBManager.getInstance().getLastFromType(MsgType.HOROSCOPE_LEAVE_ALONE);
//                            QiWuAPI.horoscope.bookingLeaveAlone(msgEntity.getData().getHoroscope(), new APICallback<APIEntity<String>>() {
//                                @Override
//                                public void onSuccess(APIEntity<String> response) {
//                                    if (response.isSuccess())
//                                        launchActivity(HoroscopeLeaveAloneOrderDetailActivity.class, "id", response.getData());
//                                }
//                            });
                            if (SDKApplication.getInstance().getAppOrder() <= 1) {
                                ToastUtils.show("暂未开放订单功能如需接入，请联系齐悟");
                                return;
                            } else {
                                ToastUtils.show("暂未开放订单功能如需接入，请联系齐悟");
                            }
                            break;
                        case MsgType.HOROSCOPE_LOVERS_DISC_CONFIRM:
//                            msgEntity = MsgDBManager.getInstance().getLastFromType(MsgType.HOROSCOPE_LOVERS_DISC);
//                            QiWuAPI.horoscope.bookingLoversDisc(msgEntity.getData().getHoroscope(), new APICallback<APIEntity<String>>() {
//                                @Override
//                                public void onSuccess(APIEntity<String> response) {
//                                    if (response.isSuccess())
//                                        launchActivity(HoroscopeLoversDiscOrderDetailActivity.class, "id", response.getData());
//                                }
//                            });
                            if (SDKApplication.getInstance().getAppOrder() <= 1) {
                                ToastUtils.show("暂未开放订单功能如需接入，请联系齐悟");
                                return;
                            } else {
                                ToastUtils.show("暂未开放订单功能如需接入，请联系齐悟");
                            }
                            break;
                        case MsgType.MOVIE_SEAT:
                            if (SDKApplication.getInstance().getAppOrder() <= 1) {
                                ToastUtils.show("暂未开放订单功能如需接入，请联系齐悟");
                                return;
                            }
                            if (!User.isLogin()) {
                                launchActivity(LoginActivity.class);
                                return;
                            }
                            launchActivity(SelectedSeatActivity.class, new BaseActivity.IntentExpand() {
                                @Override
                                public void extraValue(Intent intent) {
                                    intent.putExtra(Const.Intent.DATA, entity.getData().getMovieSeatInfo());
                                }
                            });
                            break;
                    }
                } else {
                    switch (entity.getRetcode()) {
                        case 103333:
                            PopDialog.create(MainActivity.this)
                                    .setContent(R.string.jihuo_again)
                                    .setOnViewClickListener(new PopDialog.OnViewClickListener() {
                                        @Override
                                        public void onClick(View view, boolean isConfirm) {
                                            if (isConfirm) {
                                                QiWu.activate();
                                            }
                                        }
                                    });
                            break;
                        case 103334:
                            launchActivity(LoginActivity.class);
                            break;
                    }
                }
                mData.add(entity);
                mMainBinding.rvList.getAdapter().notifyDataSetChanged();
                toListViewBottom();
            }
        });

        mMainBinding.iHeadLayout.btnNavBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchActivity(UserInfoActivity.class);
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length == 0) return;
        LinkedList<String> deniedPermissions = new LinkedList<>();
        for (int i = 0; i < permissions.length; i++) {
            String permission = permissions[i];
            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                deniedPermissions.add(permission);
            }
        }
        if (deniedPermissions.isEmpty()) {
//            if (DataStore.isFirstLaunch()) {
//                LogUtils.sf("第一次启动");
//                ActivatePresenter.deleteHistory();
//                User.logout(true);
//                DataStore.firstLaunch(false);
//            }
            initSDK();
            testBot();
            testAPI();
        } else {
            Log.v(TAG, GsonUtils.toJson(deniedPermissions));
        }
    }

    private void initSDK() {
        QiWu.init(this, "sdkdemo-sdk-demo-test",
                "3f1308d02fe8df6754f020f2299c3711");
        WebMusicManager.getInstance().init(this);
        QiWuPlayerConfig.getInstance().setSDKHandleAudioFocus(false);
        QiWuVoice.init(this,
                "16630414",
                "GYG0WAUCgmlHE1sVOuupvehr",
                "K8hhWeXkV6gDiEhMHIO0CI3jDaojn6OA",
                mMyAsrListener,
                playStateListener, mMyWakeupListener);
        AudioPlayManager.getInstance().addPlayStateListener(playStateListener);
        mData.addAll(MsgDBManager.getInstance().getAll());
        if (mMainBinding.rvList.getAdapter() != null) {
            mMainBinding.rvList.getAdapter().notifyDataSetChanged();
        }
        toListViewBottom();
        if (User.isLogin()) {
            QiWuAPI.account.getUserInfo(new APICallback<APIEntity<UserInfoEntity>>() {
                @Override
                public void onSuccess(APIEntity<UserInfoEntity> response) {
                    if (response.isSuccess()) {

                    }
                }
            });
            QiWuAPI.map.sendGPS(new APICallback<APIEntity<String>>() {
                @Override
                public void onSuccess(APIEntity<String> response) {
                    LogUtils.sf("response:" + response);
                }
            });
        }
        LogUtils.sf("Login:" + User.isLogin());
    }


    private void testBot() {
    }

    private void testAPI() {
//        TestAPI.Order.bookingExpress(this);
//        TestAPI.Order.getExpressOrder("");
    }

    public void toListViewBottom() {
        toListViewPosition(mData.size() - 1);
    }

    public void toListViewPosition(int position) {
        mMainBinding.rvList.scrollToPosition(position);
    }

    @Override
    public void onEvent(EventBusEntity event) {
        super.onEvent(event);
        switch (event.getMessage()) {
            case Const2.into_background:
                break;
            case Const2.close_audio:
                mMainBinding.mainBottomBar.getBtnListening().setState(ListeningButton.State.IDLE);
                break;
            case Const2.audio_wake:
                mMainBinding.mainBottomBar.getBtnListening().setState(ListeningButton.State.LISTENING, true, true);
                break;
            case Const.Instruct.START_RECORD:
                boolean isDelayed = (boolean) event.getData();
                mMainBinding.mainBottomBar.getBtnListening().setState(ListeningButton.State.LISTENING, true, isDelayed);
                break;
            case Const2.add_new_message:
                MsgEntity entity = (MsgEntity) event.getData();
                mData.add(entity);
                break;
            case Const.Instruct.HIDE_NAVIGATION:
                break;
        }
    }

    @Override
    public void volume(int volume) {
        mMainBinding.mainBottomBar.getBtnListening().setVolume(volume);
    }

    @Override
    public void startListen(boolean isDelayed) {

    }

    @Override
    public void idle() {
        mMainBinding.mainBottomBar.getBtnListening().setState(ListeningButton.State.IDLE);
    }

    @Override
    public void thick() {
        mMainBinding.mainBottomBar.getBtnListening().setState(ListeningButton.State.THINK);
    }

    public void addOnMusicDeleteListeners(OnMusicDeleteListener musicDeleteListener) {
        if (!mOnMusicDeleteListeners.contains(musicDeleteListener)) {
            mOnMusicDeleteListeners.add(musicDeleteListener);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMainActivity = null;
    }
}
