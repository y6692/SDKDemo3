package com.centaurstech.sdk.activity.movies;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;

import com.centaurstech.qiwu.QiWuAPI;
import com.centaurstech.qiwu.QiWuBot;
import com.centaurstech.qiwu.callback.APICallback;
import com.centaurstech.qiwu.common.Const;
import com.centaurstech.qiwu.common.DataStore;
import com.centaurstech.qiwu.common.User;
import com.centaurstech.qiwu.entity.APIEntity;
import com.centaurstech.qiwu.entity.EventBusEntity;
import com.centaurstech.qiwu.entity.MovieSeatInfoEntity;
import com.centaurstech.qiwu.utils.DateUtils;
import com.centaurstech.qiwu.utils.GsonUtils;
import com.centaurstech.qiwu.utils.LogUtils;
import com.centaurstech.qiwu.utils.UIUtils;
import com.centaurstech.sdk.R;
import com.centaurstech.sdk.activity.BaseActivity;
import com.centaurstech.sdk.adapter.SelectedSeatAdapter;
import com.centaurstech.sdk.common.Const2;
import com.centaurstech.sdk.databinding.ActivitySelectedSeatBinding;
import com.centaurstech.sdk.utils.ConversionUtils;
import com.centaurstech.sdk.utils.ToastUtils;
import com.centaurstech.sdk.view.PopDialog;
import com.centaurstech.sdk.view.seat.OnChooseSeatListener;
import com.centaurstech.sdk.view.seat.SeatData;
import com.centaurstech.sdk.view.seat.SeatView;

import java.util.ArrayList;
import java.util.List;

import static com.qiwu.ui.common.AppConfig.getContext;

/**
 * @Author: 樊德鹏
 * 时   间:2019/5/21
 * 简   述:<功能描述>
 */
public class SelectedSeatActivity extends BaseActivity implements OnChooseSeatListener {

    private MovieSeatInfoEntity mSeatInfoEntity;

    private SelectedSeatAdapter mAdapter;

    private List<String> mSeatNo = new ArrayList<>();

    private List<MovieSeatInfoEntity.SeatEntity> mSeatEntits = new ArrayList<>();

    private List<SeatData> mSelectedSeat = new ArrayList<>();

    private List<SeatData> mDefaultSelectedSeat = new ArrayList<>();

    public static final int RESULT_SEAT_CODE = 1001;

    private List<SeatData> isSoldSeatList = new ArrayList<>();

    private int number;

    private ActivitySelectedSeatBinding mBinding;

    private static final int REQUEST_CODE_LOGIN = 12;

    @Override
    public boolean useEventBus() {
        return true;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_selected_seat);
        mSeatInfoEntity = (MovieSeatInfoEntity) getIntent().getSerializableExtra(Const.Intent.DATA);
        setTitle("电影选座");
        initView();
        initListener();
        if (!ConversionUtils.checkLogin(this, REQUEST_CODE_LOGIN)) {
            return;
        }
    }

    public void initView() {
        mBinding.iHeadLayout.tvTitle.setText(mSeatInfoEntity.getCinemaName());
        mBinding.svTableView.setSeatState(SeatView.STATE_LOADING);
        mBinding.svTableView.setMaxCount(mSeatInfoEntity.getSeatTable().getMaxSelectNum());

        mBinding.tvMovieName.setText(mSeatInfoEntity.getMovieName());
        mBinding.tvMoviesPlace.setText(mSeatInfoEntity.getTheater());
        String dateStr = "";
        if (DateUtils.IsToday(mSeatInfoEntity.getDate())) {
            dateStr = "今天" + DateUtils.date2Date(mSeatInfoEntity.getDate(), "yyyy-MM-dd", "M月d日");
        } else {
            dateStr = DateUtils.getWeek(mSeatInfoEntity.getDate()) + "  " + DateUtils.date2Date(mSeatInfoEntity.getDate(), "yyyy-MM-dd", "M月d日");
        }
        mBinding.tvMovieDetails.setText(dateStr + "    " + mSeatInfoEntity.getStartTime() + " - " + mSeatInfoEntity.getEndTime() + "( " + mSeatInfoEntity.getLan() + mSeatInfoEntity.getPlayerType() + " )");

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mAdapter = new SelectedSeatAdapter();
        mBinding.rvSeatView.setLayoutManager(linearLayoutManager);
        mBinding.rvSeatView.setAdapter(mAdapter);
        mSeatEntits = mSeatInfoEntity.getAutoSelected();
        number = mSeatEntits.size();
        mAdapter.setSeatEntitys(mSeatEntits);
        getData(mSeatInfoEntity.getSeatTable().getRows(), mSeatEntits);
        mBinding.tvTotalPrice.setText(UIUtils.getString(R.string.money_sign) + getPrice());
        mBinding.tvTicketNumber.setText(mSeatEntits.size() + "张");
        mBinding.llBottom.setVisibility(mSeatEntits.size() > 0 ? View.VISIBLE : View.GONE);
    }

    public void initListener() {
        mBinding.svTableView.setOnChooseSeatListener(this);
        mBinding.tvConfirmSeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean legal = mBinding.svTableView.isSelectedSeatLegal();
                if (!legal) {
                    PopDialog.create(SelectedSeatActivity.this)
                            .setContent("锁座时需要连续选座，座位中间不要有空格")
                            .setHideRightButton()
                            .setOnViewClickListener(new PopDialog.OnViewClickListener() {
                                @Override
                                public void onClick(View view, boolean isConfirm) {
                                    if (isConfirm) {

                                    }
                                }
                            });
                    return;
                }
                long timer = DateUtils.date2Timestamp(mSeatInfoEntity.getOutBuyTime());
                if (timer < System.currentTimeMillis()) {
                    PopDialog.create(getContext())
                            .setTitle("场次已停售")
                            .setContent("抱歉，该场次已停售，您可以选择其他场次观看电影？")
                            .setLeftButtonText("我不看了")
                            .setRightButtonText("重选场次")
                            .setNeedDismiss(true)
                            .setCanceledTouchOutside(false)
                            .setOnViewClickListener(new PopDialog.OnViewClickListener() {
                                @Override
                                public void onClick(View view, boolean isConfirm) {
                                    if (!isConfirm) {
                                        QiWuBot.sendMessage("我想换个场次");
                                        finish();
                                    } else {
                                        QiWuBot.sendMessage("我不想看电影了");
                                        finish();
                                    }
                                }
                            });
                    return;
                }
                confirmOrder();
            }
        });

        mAdapter.setOnItemClickListener(new SelectedSeatAdapter.OnItemClickListener() {
            @Override
            public void onItemCLick(MovieSeatInfoEntity.SeatEntity mSelectSeatEntity, int position) {
                for (int i = 0; i < mSelectedSeat.size(); i++) {
                    if (mSelectSeatEntity.getNo().equals(mSelectedSeat.get(i).seatNo)) {
                        mSelectedSeat.remove(i);
                    }
                }

                for (int i = 0; i < mSeatEntits.size(); i++) {
                    if (mSelectSeatEntity.getNo().equals(mSeatEntits.get(i).getNo())) {
                        mSeatEntits.remove(i);
                    }
                }
                mAdapter.notifyDataSetChanged();
                runOnUiThread(
                        new Runnable() {
                            @Override
                            public void run() {
                                mBinding.svTableView.setSelectedData(mSelectedSeat);
                            }
                        });
                mBinding.llBottom.setVisibility(mSeatEntits.size() > 0 ? View.VISIBLE : View.GONE);
                mBinding.tvTotalPrice.setText(UIUtils.getString(R.string.money_sign) + getPrice());
                mBinding.tvTicketNumber.setText(mSeatEntits.size() + "张");
            }
        });

        mBinding.iHeadLayout.btnNavBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                QiWuBot.sendMessage("我想换个场次");
                finish();
            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private void getData(final List<MovieSeatInfoEntity.SeatTableBean.SeatBean> seats, final List<MovieSeatInfoEntity.SeatEntity> selectSeatEntities) {
        mSeatNo.clear();
        isSoldSeatList.clear();
        mSelectedSeat.clear();
        showLoadingDialog();
        new Thread(new Runnable() {
            @Override
            public void run() {
                final List<SeatData> seatList = new ArrayList<>();
                for (int i = 0; i < seats.size(); i++) {
                    mSeatNo.add(seats.get(i).getRid());
                    for (int col = 0; col < seats.get(i).getCols().size(); col++) {
                        SeatData seatData = new SeatData();
                        if (TextUtils.isEmpty(seats.get(i).getCols().get(col).getSno())) {
                            continue;
                        }
                        seatData.seatNo = seats.get(i).getCols().get(col).getSno();
                        seatData.seatRow = seats.get(i).getRid();
                        seatData.seatCol = seats.get(i).getCols().get(col).getCid();
                        seatData.aId = seats.get(i).getCols().get(col).getAid();
                        seatData.point =
                                new Point(i + 1, col + 1);
                        switch (seats.get(i).getCols().get(col).getSt()) {
                            case "1":
                                seatData.state = SeatData.STATE_NORMAL;
                                break;
                            case "2":
                                seatData.state = SeatData.STATE_SOLD;
                                isSoldSeatList.add(seatData);
                                break;
                        }
                        seatList.add(seatData);
                    }
                }

                if (isBuy()) {
                    for (int i = 0; i < selectSeatEntities.size(); i++) {
                        for (int s = 0; s < seatList.size(); s++) {
                            if (selectSeatEntities.get(i).getNo().equals(seatList.get(s).seatNo)) {
                                mSelectedSeat.add(seatList.get(s));
                                mDefaultSelectedSeat.add(seatList.get(s));
                            }
                        }
                    }
                    mSeatEntits = selectSeatEntities;
                } else {
                    for (int i = 0; i < mSeatEntits.size(); i++) {
                        for (int s = 0; s < seatList.size(); s++) {
                            if (mSeatEntits.get(i).getNo().equals(seatList.get(s).seatNo)) {
                                mSelectedSeat.add(seatList.get(s));
                                mDefaultSelectedSeat.add(seatList.get(s));
                            }
                        }
                    }
                }
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                runOnUiThread(
                        new Runnable() {
                            @Override
                            public void run() {
                                hideLoadingDialog();
                                mBinding.svTableView.setSeatData(seatList, mSeatNo);
                                mBinding.svTableView.setSelectedData(mSelectedSeat);
                                mBinding.tvTotalPrice.setText(UIUtils.getString(R.string.money_sign) + getPrice());
                                mBinding.tvTicketNumber.setText(mSeatEntits.size() + "张");
                                mAdapter.setSeatEntitys(mSeatEntits);
                            }
                        });
            }
        }).start();
    }

    /***
     * 判断用户座位是否已售出
     */
    private boolean isBuy() {
        LogUtils.sf(GsonUtils.toJson(mSeatEntits));
        if (mSeatEntits.size() <= 0) {
            return true;
        }
        for (int i = 0; i < mSeatEntits.size(); i++) {
            for (int j = 0; j < isSoldSeatList.size(); j++) {
                if (mSeatEntits.get(i).getNo().equals(isSoldSeatList.get(j).seatNo)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void onPickLoverSeatOverMaxCount(int maxSelectCount) {

    }

    @Override
    public void onSelectedSeatOverMaxCount(int maxSelectCount) {
        ToastUtils.show("最多选择" + maxSelectCount + "个座位");
    }

    @Override
    public void onSelectSeatNotMatchRegular() {
//        ToastUtils.show("中间不能留空座");
    }

    @Override
    public void onSelectedSeatChanged(List<SeatData> selectedSeats) {
        mSelectedSeat.clear();
        mSeatEntits.clear();
        List<SeatData> mSeats = mBinding.svTableView.getSelectedSeat();
        for (SeatData seat : mSeats) {
            MovieSeatInfoEntity.SeatEntity mSelectSeatEntity = new MovieSeatInfoEntity.SeatEntity();
            mSelectSeatEntity.setAid(seat.aId);
            mSelectSeatEntity.setCol(seat.seatCol);
            mSelectSeatEntity.setRow(seat.seatRow);
            mSelectSeatEntity.setNo(seat.seatNo);
            mSelectSeatEntity.setPrice(mSeatInfoEntity.getPrice());
            mSeatEntits.add(mSelectSeatEntity);
            mSelectedSeat.add(seat);
        }
        if (null != mSeatInfoEntity.getAreas() && mSeatInfoEntity.getAreas().size() > 0) {
            List<MovieSeatInfoEntity.AreaBean> mAreas = mSeatInfoEntity.getAreas();
            for (int i = 0; i < mSeatEntits.size(); i++) {
                for (int j = 0; j < mAreas.size(); j++) {
                    if (mSeatEntits.get(i).getAid().equals(mAreas.get(j).getId())) {
                        mSeatEntits.get(i).setPrice(String.valueOf(mAreas.get(j).getPrice()));
                    }
                }
            }
        }
        mAdapter.setSeatEntitys(mSeatEntits);
        mBinding.llBottom.setVisibility(mSeatEntits.size() > 0 ? View.VISIBLE : View.GONE);
        mBinding.tvTotalPrice.setText(UIUtils.getString(R.string.money_sign) + getPrice());
        mBinding.tvTicketNumber.setText(mSeatEntits.size() + "张");
    }

    @Override
    public void onSelectedSeatSold() {
        ToastUtils.show("选择的座位已被售出");
    }

    private String getPrice() {
        String price = "";
        for (int i = 0; i < mSeatEntits.size(); i++) {
            price = ConversionUtils.addition2Price(price, mSeatEntits.get(i).getPrice());
        }
        return price;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case RESULT_SEAT_CODE:
                    showLoadingDialog();
                    mSeatEntits = DataStore.getSeatList();
                    number = mSeatEntits.size();
                    QiWuAPI.movie.getSeats(mSeatInfoEntity.getQueryToken(), number + "", new APICallback<APIEntity<MovieSeatInfoEntity>>() {
                        @Override
                        public void onSuccess(APIEntity<MovieSeatInfoEntity> response) {
                            hideLoadingDialog();
                            updateSeatInfo(response.getRetcode(), response.getData());
                        }
                    });
                    break;
                case REQUEST_CODE_LOGIN:
                    if (User.isLogin()) {

                    } else {
                        finish();
                    }
                    break;
            }
        }
    }

    /***
     *  更新座位信息
     * @param recode
     * @param entity
     */
    private void updateSeatInfo(int recode, MovieSeatInfoEntity entity) {
        switch (recode) {
            case 150002:
                PopDialog.create(getContext())
                        .setTitle("场次已停售")
                        .setContent("抱歉，该场次已停售，您可以选择其他场次观看电影？")
                        .setLeftButtonText("我不看了")
                        .setRightButtonText("重选场次")
                        .setNeedDismiss(true)
                        .setCanceledTouchOutside(false)
                        .setOnViewClickListener(new PopDialog.OnViewClickListener() {
                            @Override
                            public void onClick(View view, boolean isConfirm) {
                                if (!isConfirm) {
                                    QiWuBot.sendMessage("我想换个场次");
                                    finish();
                                } else {
                                    QiWuBot.sendMessage("我不想看电影了");
                                    finish();
                                }
                            }
                        });
                break;
            case 0:
                if (entity == null) {
                    return;
                }
                getData(entity.getSeatTable().getRows(), entity.getAutoSelected());
                break;
        }
    }

    private void confirmOrder() {
        showLoadingDialog();
        QiWuAPI.movie.booking(mSeatInfoEntity, mSeatEntits, new APICallback<APIEntity<String>>() {
            @Override
            public void onSuccess(APIEntity<String> response) {
                hideLoadingDialog();
                switch (response.getRetcode()) {
                    case 0:
                        launchActivity(ConfirmCinemaOrderActivity.class, new IntentExpand() {
                            @Override
                            public void extraValue(Intent intent) {
                                intent.putExtra(Const.Intent.DATA, response.getData());
                            }
                        }, RESULT_SEAT_CODE);
                        break;
                    case 40039:
                        PopDialog.create(SelectedSeatActivity.this)
                                .setContent("您之前购买的电影票还未付款？")
                                .setLeftButtonText(UIUtils.getString(R.string.cancel_order))
                                .setRightButtonText("立即付款")
                                .setNeedDismiss(true)
                                .setHideTitle()
                                .setCanceledTouchOutside(false)
                                .setOnViewClickListener(new PopDialog.OnViewClickListener() {
                                    @Override
                                    public void onClick(View view, boolean isConfirm) {
                                        if (!isConfirm) {
                                            launchActivity(OrderDetailCinemaActivity.class, new IntentExpand() {
                                                @Override
                                                public void extraValue(Intent intent) {
                                                    intent.putExtra(Const.Intent.DATA, response.getData());
                                                }
                                            });
                                        } else {
                                            showLoadingDialog();
                                            QiWuAPI.movie.cancel(response.getMsg(), new APICallback<APIEntity<String>>() {
                                                @Override
                                                public void onSuccess(APIEntity<String> response) {
                                                    hideLoadingDialog();
                                                    ToastUtils.show("操作成功");
                                                }
                                            });
                                        }
                                    }
                                });
                        break;
                    default:
                        ToastUtils.show("锁座失败，晓悟将重新为您智能选座");
                        number = mSeatEntits.size();
                        QiWuAPI.movie.getSeats(mSeatInfoEntity.getQueryToken(), number + "", new APICallback<APIEntity<MovieSeatInfoEntity>>() {
                            @Override
                            public void onSuccess(APIEntity<MovieSeatInfoEntity> response) {
                                hideLoadingDialog();
                                updateSeatInfo(response.getRetcode(), response.getData());
                            }
                        });
                        break;
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        QiWuBot.sendMessage("我想换个场次");
        finish();
    }

    /***
     * 是否变动了默认座位
     * @return
     */
    private boolean isChangeSeat() {
        if (mSelectedSeat.size() != mDefaultSelectedSeat.size()) {
            return false;
        }
        return mSelectedSeat.containsAll(mDefaultSelectedSeat);
    }

    @Override
    public void onEvent(EventBusEntity event) {
        super.onEvent(event);
        switch (event.getMessage()) {
            case Const2.clear_activity:
                finish();
                break;
        }
    }
}
