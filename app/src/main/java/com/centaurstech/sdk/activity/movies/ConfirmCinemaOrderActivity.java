package com.centaurstech.sdk.activity.movies;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.view.View;

import com.centaurstech.qiwu.QiWuAPI;
import com.centaurstech.qiwu.callback.APICallback;
import com.centaurstech.qiwu.common.Const;
import com.centaurstech.qiwu.entity.APIEntity;
import com.centaurstech.qiwu.entity.EventBusEntity;
import com.centaurstech.qiwu.entity.OrderMovieEntity;
import com.centaurstech.qiwu.utils.DateUtils;
import com.centaurstech.qiwu.utils.UIUtils;
import com.centaurstech.sdk.R;
import com.centaurstech.sdk.activity.BaseActivity;
import com.centaurstech.sdk.activity.PaymentActivity;
import com.centaurstech.sdk.activity.order.OrderListActivity;
import com.centaurstech.sdk.common.Const2;
import com.centaurstech.sdk.databinding.ActivityConfirmCinemaOrderBinding;
import com.centaurstech.sdk.utils.ToastUtils;
import com.centaurstech.sdk.view.PopDialog;

public class ConfirmCinemaOrderActivity extends BaseActivity {

    private ActivityConfirmCinemaOrderBinding mBinding;

    private String mOrderId = "";

    public CountDownTimer mCountDownTimer;

    private OrderMovieEntity mOrderMovieEntity;

    private boolean isTimeOut;

    @Override
    public boolean useEventBus() {
        return true;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_confirm_cinema_order);
        mOrderId = getIntent().getStringExtra(Const.Intent.DATA);
        initView();
        initListener();
    }

    private void initView() {
        QiWuAPI.movie.getOrder(mOrderId, new APICallback<APIEntity<OrderMovieEntity>>() {
            @Override
            public void onSuccess(APIEntity<OrderMovieEntity> response) {
                mOrderMovieEntity = response.getData();
                setData(response.getData());
            }
        });
    }

    private void initListener() {
        mBinding.btnNavBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOrderMovieEntity == null) {
                    return;
                }
                if (isTimeOut) {
                    PopDialog.create(ConfirmCinemaOrderActivity.this)
                            .setContent("付款超时，请返回首页重新购买?")
                            .setHideRightButton()
                            .setHideTitle()
                            .setOnViewClickListener(new PopDialog.OnViewClickListener() {
                                @Override
                                public void onClick(View view, boolean isConfirm) {
                                    if (isConfirm) {
                                        finish();
                                    }
                                }
                            });
                    return;
                }

                PopDialog.create(ConfirmCinemaOrderActivity.this)
                        .setContent("确定不要刚才选择的座位了吗?")
                        .setOnViewClickListener(new PopDialog.OnViewClickListener() {
                            @Override
                            public void onClick(View view, boolean isConfirm) {
                                if (isConfirm) {
                                    showLoadingDialog();
                                    QiWuAPI.movie.cancel(mOrderId, new APICallback<APIEntity<String>>() {
                                        @Override
                                        public void onSuccess(APIEntity<String> response) {
                                            hideLoadingDialog();
                                            ToastUtils.show("操作成功");
                                            finish();
                                        }
                                    });
                                }
                            }
                        });
            }
        });

        mBinding.tvMovieAgreement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ToastUtils.show("请自主开发");
            }
        });

        mBinding.btnNavBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOrderMovieEntity == null) {
                    return;
                }
                if (isTimeOut) {
                    PopDialog.create(ConfirmCinemaOrderActivity.this)
                            .setContent("付款超时，请返回首页重新购买?")
                            .setHideRightButton()
                            .setHideTitle()
                            .setOnViewClickListener(new PopDialog.OnViewClickListener() {
                                @Override
                                public void onClick(View view, boolean isConfirm) {
                                    if (isConfirm) {
                                        finish();
                                    }
                                }
                            });
                    return;
                }

                PopDialog.create(ConfirmCinemaOrderActivity.this)
                        .setContent("确定不要刚才选择的座位了吗?")
                        .setOnViewClickListener(new PopDialog.OnViewClickListener() {
                            @Override
                            public void onClick(View view, boolean isConfirm) {
                                if (isConfirm) {
                                    showLoadingDialog();
                                    QiWuAPI.movie.cancel(mOrderId, new APICallback<APIEntity<String>>() {
                                        @Override
                                        public void onSuccess(APIEntity<String> response) {
                                            hideLoadingDialog();
                                            ToastUtils.show("操作成功");
                                            finish();
                                        }
                                    });
                                }
                            }
                        });
            }
        });

        mBinding.tvConfirmSeat.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                launchActivity(PaymentActivity.class, new IntentExpand() {
                    @Override
                    public void extraValue(Intent intent) {
                        intent.putExtra(Const.Intent.DATA, mOrderId);
                        intent.putExtra(Const.Intent.TYPE, 3);
                    }
                });
            }
        });
    }

    private void setData(OrderMovieEntity mOrderMovieEntity) {
        if (null != mOrderMovieEntity) {
            if (mOrderMovieEntity.getMovieName().length() > 8) {
                String movieName = mOrderMovieEntity.getMovieName().substring(0, 8);
                mBinding.tvMovieName.setText(movieName + "...");
            } else {
                mBinding.tvMovieName.setText(mOrderMovieEntity.getMovieName());
            }
            mBinding.tvMovieTicket.setText(mOrderMovieEntity.getTicketNumber() + "张");
            String startTimer = DateUtils.date2Date(mOrderMovieEntity.getStartTime(), "yyyy-MM-dd HH:mm", "M月d日 HH:mm");
            String enTime = DateUtils.date2Date(mOrderMovieEntity.getEndTime(), "yyyy-MM-dd HH:mm", "HH:mm");
            try {
                String timer;
                if (DateUtils.IsToday(mOrderMovieEntity.getStartTime())) {
                    timer = "今天" + startTimer + "-" + enTime;
                } else {
                    timer = DateUtils.getWeek(mOrderMovieEntity.getStartTime()) + startTimer + "-" + enTime;
                }
                mBinding.tvMoviePlayType.setText(timer + "(" + mOrderMovieEntity.getMovieScreen() + ")");
            } catch (Exception e) {

            }
            mBinding.tvMovieAddress.setText(mOrderMovieEntity.getCinemaName());
            mBinding.tvMoviePlace.setText(mOrderMovieEntity.getTheaterInfo() + "(" + getSeatPlace() + ")");
            mBinding.tvPrice.setText(UIUtils.getString(R.string.money_sign) + mOrderMovieEntity.getAmount());
            mBinding.tvPayPrice.setText(UIUtils.getString(R.string.money_sign) + mOrderMovieEntity.getAmount());
            mBinding.tvPhoneNumber.setText(mOrderMovieEntity.getContactPhone());
            if (mCountDownTimer != null) {
                mCountDownTimer.cancel();
            }
            payDownTimer();
        }
    }

    private void payDownTimer() {
        mCountDownTimer = new CountDownTimer(mOrderMovieEntity.getCountdown() * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mBinding.tvMovieTimer.setText(DateUtils.timestamp2String(millisUntilFinished, "mm:ss"));
            }

            @Override
            public void onFinish() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        isTimeOut = true;
                        PopDialog.create(ConfirmCinemaOrderActivity.this)
                                .setContent("付款超时，请返回选座页重新购买?")
                                .setHideRightButton()
                                .setHideTitle()
                                .setCanceledTouchOutside(false)
                                .setOnViewClickListener(new PopDialog.OnViewClickListener() {
                                    @Override
                                    public void onClick(View view, boolean isConfirm) {
                                        if (isConfirm) {
                                            finish();
                                        }
                                    }
                                });
                    }
                });
            }
        };
        mCountDownTimer.start();
    }

    @Override
    public void onBackPressed() {
        if (isTimeOut) {
            PopDialog.create(ConfirmCinemaOrderActivity.this)
                    .setContent("付款超时，请返回选座页重新购买?")
                    .setHideRightButton()
                    .setHideTitle()
                    .setOnViewClickListener(new PopDialog.OnViewClickListener() {
                        @Override
                        public void onClick(View view, boolean isConfirm) {
                            if (isConfirm) {
                                finish();
                            }
                        }
                    });
            return;
        }
        PopDialog.create(ConfirmCinemaOrderActivity.this)
                .setContent("确定不要刚才选择的座位了吗?")
                .setOnViewClickListener(new PopDialog.OnViewClickListener() {
                    @Override
                    public void onClick(View view, boolean isConfirm) {
                        if (isConfirm) {
                            showLoadingDialog();
                            QiWuAPI.movie.cancel(mOrderId, new APICallback<APIEntity<String>>() {
                                @Override
                                public void onSuccess(APIEntity<String> response) {
                                    hideLoadingDialog();
                                    ToastUtils.show("操作成功");
                                    finish();
                                }
                            });
                        }
                    }
                });
    }

    /**
     * 获取座位
     */
    private String getSeatPlace() {
        if (null == mOrderMovieEntity) {
            return "";
        }
        String seatInfo = "";
        for (int i = 0; i < mOrderMovieEntity.getSeat().size(); i++) {
            if (i == mOrderMovieEntity.getSeat().size() - 1) {
                seatInfo += mOrderMovieEntity.getSeat().get(i).getRow() + "排" + mOrderMovieEntity.getSeat().get(i).getCol() + "座";
            } else {
                seatInfo += mOrderMovieEntity.getSeat().get(i).getRow() + "排" + mOrderMovieEntity.getSeat().get(i).getCol() + "座  ";
            }
        }
        return seatInfo;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
        }
    }

    @Override
    public void onEvent(EventBusEntity event) {
        super.onEvent(event);
        switch (event.getMessage()) {
            case Const2.update_order:
                finish();
                break;
        }
    }
}
