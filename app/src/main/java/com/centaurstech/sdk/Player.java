package com.centaurstech.sdk;

import android.annotation.SuppressLint;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.AnimationDrawable;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.SeekBar;

import com.centaurstech.qiwu.QiWuPlayer;
import com.centaurstech.qiwu.common.PauseType;
import com.centaurstech.qiwu.common.PlayState;
import com.centaurstech.qiwu.common.ThreadPoolFactory;
import com.centaurstech.qiwu.db.MusicDBManager;
import com.centaurstech.qiwu.entity.MediaEntity;
import com.centaurstech.qiwu.entity.MsgEntity;
import com.centaurstech.qiwu.entity.MusicCardEntity;
import com.centaurstech.qiwu.entity.MusicEntity;
import com.centaurstech.qiwu.entity.MusicListDataEntity;
import com.centaurstech.qiwu.listener.OnMusicPlayListener;
import com.centaurstech.qiwu.manager.MusicPlayManager;
import com.centaurstech.qiwu.utils.DateUtils;
import com.centaurstech.qiwu.utils.GsonUtils;
import com.centaurstech.qiwu.utils.LogUtils;
import com.centaurstech.qiwu.utils.UIUtils;
import com.centaurstech.sdk.activity.MainActivity;
import com.centaurstech.sdk.adapter.MusicListAdapter;
import com.centaurstech.sdk.databinding.ItemMusicBinding;
import com.centaurstech.sdk.databinding.LayoutMusicListBinding;
import com.centaurstech.sdk.utils.ToastUtils;
import com.centaurstech.sdk.utils.ViewUtils;
import com.centaurstech.voice.QiWuVoice;
import com.qiwu.ui.dialog.AroundDialog;

import java.util.ArrayList;
import java.util.List;

import static android.view.View.GONE;
import static com.centaurstech.qiwu.QiWu.getContext;
import static com.qiwu.ui.util.UIUtils.getResources;

/**
 * @author Leon(黄长亮)
 * @describe TODO
 * @date 2019/6/15
 */
public class Player implements OnMusicPlayListener, View.OnClickListener, SeekBar.OnSeekBarChangeListener {

    public ItemMusicBinding mMusicBinding;
    public MsgEntity mMsgEntity;
    private long mPrevClickTime = 0;

    public Player(ItemMusicBinding leftBinding, MsgEntity msgEntity) {
        mMusicBinding = leftBinding;
        mMsgEntity = msgEntity;
        mMusicBinding.iMusic.tvMode.setOnClickListener(this);
        mMusicBinding.iMusic.ivNext.setOnClickListener(this);
        mMusicBinding.iMusic.ivPrevious.setOnClickListener(this);
        mMusicBinding.iMusic.ivPlay.setOnClickListener(this);
        mMusicBinding.iMusic.seek.setOnSeekBarChangeListener(this);
        mMusicBinding.iMusic.ivMusicList.setOnClickListener(this);
        if (msgEntity.getData().getMusicData() != null && msgEntity.getData().getMusicData().size() > 0) {
            MusicEntity music = msgEntity.getData().getMusicData().get(0);
            mMusicBinding.iMusic.tvMusicName.setText(music.getName());
            mMusicBinding.iMusic.tvSingerName.setText(music.getArtist());
        }
        if (QiWuPlayer.getInstance().getData() == mMsgEntity) {
            QiWuPlayer.getInstance().setOnMusicPlayListener(this);
            switch (QiWuPlayer.getInstance().getState()) {
                case PAUSED:
                case STARTED:
                    mMusicBinding.iMusic.seek.setMax(QiWuPlayer.getInstance().getDuration());
                    mMusicBinding.iMusic.seek.setProgress(QiWuPlayer.getInstance().getCurrentPosition());
                    mMusicBinding.iMusic.tvProgress.setText(DateUtils.timestamp2String(QiWuPlayer.getInstance().getCurrentPosition(), "m:ss"));
                    mMusicBinding.iMusic.tvDuration.setText(DateUtils.timestamp2String(QiWuPlayer.getInstance().getDuration(), "m:ss"));
                    break;
                default:
                    resetUI();
                    break;
            }
        } else {
            resetUI();
        }
    }


    private void resetUI() {
        mMusicBinding.iMusic.seek.setMax(100);
        mMusicBinding.iMusic.seek.setProgress(0);
        mMusicBinding.iMusic.tvProgress.setText(DateUtils.timestamp2String(0, "m:ss"));
        mMusicBinding.iMusic.tvDuration.setText(DateUtils.timestamp2String(0, "m:ss"));
    }

    @Override
    public void onNext(MusicEntity musicEntity) {
        LogUtils.sf("MusicView onNext" + GsonUtils.toJson(musicEntity));
        setMusicUI(true);
        UIUtils.postTaskSafely(new Runnable() {
            @Override
            public void run() {
                mMusicBinding.iMusic.seek.setProgress(0);
                mMusicBinding.iMusic.tvProgress.setText("0:00");
                mMusicBinding.iMusic.tvDuration.setText("0:00");
            }
        });
    }

    @Override
    public void onPrevious(MusicEntity musicEntity) {
        setMusicUI(true);
        UIUtils.postTaskSafely(new Runnable() {
            @Override
            public void run() {
                mMusicBinding.iMusic.seek.setProgress(0);
                mMusicBinding.iMusic.tvProgress.setText("0:00");
                mMusicBinding.iMusic.tvDuration.setText("0:00");
            }
        });
    }

    @Override
    public void onPause(MusicEntity musicEntity) {
        UIUtils.postTaskSafely(new Runnable() {
            @Override
            public void run() {
                mMusicBinding.iMusic.ivPlay.setImageResource(R.mipmap.music_btn_play);
                MusicPlayManager.getInstance().pause();
            }
        });
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onStart(MusicEntity music) {
        if (music == null) return;
        if (QiWuPlayer.getInstance().getData() == mMsgEntity) {
            LogUtils.sf("Player onStart:" + GsonUtils.toJson(music));
            mMusicBinding.iMusic.tvMusicName.setText(music.getName());
            mMusicBinding.iMusic.tvSingerName.setText(music.getArtist());
            mMusicBinding.iMusic.ivPlay.setImageResource(R.mipmap.music_btn_pause);
        }
    }

    @Override
    public void onChange(MusicEntity musicEntity) {
        LogUtils.sf("MusicView onChange");
        if (mMsgEntity.getExpire() == 0) {
            setMusicUI(true);
            UIUtils.postTaskSafely(new Runnable() {
                @Override
                public void run() {
                    mMusicBinding.iMusic.seek.setProgress(0);
                    mMusicBinding.iMusic.tvProgress.setText("0:00");
                    mMusicBinding.iMusic.tvDuration.setText("0:00");
                }
            });
        }
    }

    @Override
    public void onStop(MusicEntity musicEntity) {
        UIUtils.postTaskSafely(new Runnable() {
            @Override
            public void run() {
                resetUI();
                mMusicBinding.iMusic.ivPlay.setImageResource(R.mipmap.music_btn_play);
                QiWuPlayer.getInstance().stop();
                LogUtils.sf("Player onStop");
            }
        });
    }

    @Override
    public void onRelease(MusicEntity musicEntity, boolean change) {
        LogUtils.sf("Player onRelease");
    }

    @Override
    public void onCompletion(MusicEntity musicEntity) {
        LogUtils.sf("Player onCompletion");
        LogUtils.sf("MusicView onCompletion");
        mMusicBinding.iMusic.seek.setProgress(0);
        mMusicBinding.iMusic.tvProgress.setText("0:00");
    }

    @Override
    public void onReset(MusicEntity musicEntity) {
        LogUtils.sf("Player onCompletion");
    }

    @Override
    public void onModeChange() {

    }

    @Override
    public void onProgress(int progress, int duration) {
        LogUtils.sf("onProgress:" + progress + " duration:" + duration);
        if (!mSeekBarTouch) {
            mMusicBinding.iMusic.seek.setMax(duration);
            mMusicBinding.iMusic.seek.setProgress(progress);
            mMusicBinding.iMusic.tvProgress.setText(DateUtils.timestamp2String(progress, "m:ss"));
            mMusicBinding.iMusic.tvDuration.setText(DateUtils.timestamp2String(duration, "m:ss"));
        }
    }

    @Override
    public void onBufferProgress(int percent, int duration) {
        LogUtils.sf("onBufferProgress:" + percent + " duration:" + duration);
    }


    @Override
    public void onClick(View v) {
        if (mMsgEntity.getExpire() == 1) {
            ToastUtils.show("信息已超时，请重新查询");
            return;
        }
        if (MainActivity.getThis().mMainBinding.mainBottomBar.getBtnListening().isRunning()) return;

        if (mMsgEntity.getData().getMusicData() == null || mMsgEntity.getData().getMusicData().size() == 0) {
            ToastUtils.show("资源不存在");
            mMusicBinding.iMusic.ivPlay.setImageResource(R.mipmap.music_btn_play);
            return;
        }
        LogUtils.sf("MusicView " + MusicPlayManager.getInstance().getState());
        QiWuPlayer.getInstance().setOnMusicPlayListener(this);
        switch (v.getId()) {
            case R.id.tvMode:
                QiWuPlayer.getInstance().changePlayMode();
                break;
            case R.id.ivNext:
                QiWuPlayer.getInstance().next();
                break;
            case R.id.ivPrevious:
                QiWuPlayer.getInstance().previous();
                break;
            case R.id.ivPlay:
                if (System.currentTimeMillis() - mPrevClickTime < 500) return;
                mPrevClickTime = System.currentTimeMillis();
                if (MusicPlayManager.getInstance().getMusicEntities() == null || MusicPlayManager.getInstance().getMusicEntities().size() == 0) {
                    MusicPlayManager.getInstance().setMusicEntities(mMsgEntity.getData().getMusicData());
                }
                switch (MusicPlayManager.getInstance().getState()) {
                    case STARTED:
                    case PREPARED:
                    case PREPARING:
                        MusicPlayManager.getInstance().pause();
                        mMusicBinding.iMusic.ivPlay.setImageResource(R.mipmap.music_btn_play);
                        break;
                    default:
                        mMusicBinding.iMusic.ivPlay.setImageResource(R.mipmap.music_btn_pause);
                        QiWuPlayer.getInstance().play();
                        break;
                }
                break;
            case R.id.ivMusicList:
                showMusicList();
                break;
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        mMusicBinding.iMusic.seek.setProgress(progress);
        mMusicBinding.iMusic.tvProgress.setText(DateUtils.timestamp2String(progress, "m:ss"));
        mMusicBinding.iMusic.tvDuration.setText(DateUtils.timestamp2String(MusicPlayManager.getInstance().getDuration(), "m:ss"));
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        mSeekBarTouch = true;
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        mSeekBarTouch = false;
        QiWuPlayer.getInstance().seekTo(seekBar.getProgress());
    }

    private boolean mSeekBarTouch;

    private void showMusicList() {
        View view = View.inflate(getContext(), R.layout.layout_music_list, null);
        final LayoutMusicListBinding musicListBinding = DataBindingUtil.bind(view);
        final MusicListAdapter musicListAdapter = new MusicListAdapter(getContext(), musicListBinding.rvList);

        musicListBinding.rvList.setLayoutManager(new LinearLayoutManager(getContext()));
        musicListBinding.rvList.setAdapter(musicListAdapter);
        musicListAdapter.setMsgEntity(mMsgEntity);
        musicListAdapter.setMusicEntities((ArrayList<MusicEntity>) MusicPlayManager.getInstance().getMusicEntities());
        final AroundDialog aroundDialog = AroundDialog.create(getContext(), R.style.DialogInOutBottom);
        aroundDialog.setContent(view)
                .setBackgroundTransparency()
                .setGravity(Gravity.BOTTOM)
                .setHideAllButton()
                .show();
        musicListBinding.tvClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                aroundDialog.dismiss();
            }
        });
    }

    public void setThumbState(int state) {
        LogUtils.sf("MusicView setThumbState");
        switch (state) {
            case 1://默认状态
                mMusicBinding.iMusic.seek.setThumb(getResources().getDrawable(R.drawable.music_seekbar_thumb_default));
                break;
            case 2:// 加载中
                mMusicBinding.iMusic.seek.setThumb(getResources().getDrawable(R.drawable.music_seekbar_thumb_loading));
                AnimationDrawable drawable = (AnimationDrawable) mMusicBinding.iMusic.seek.getThumb();
                if (drawable != null) {
                    drawable.start();
                }
                break;
        }
    }

    private void setMusicUI(final boolean change) {
        LogUtils.sf("MusicView setMusicUI");
        UIUtils.postTaskSafely(new Runnable() {
            @Override
            public void run() {
                MusicEntity currentMusic = MusicPlayManager.getInstance().getCurrentMusic();
                LogUtils.sf("MusicView setMusicUI" + GsonUtils.toJson(currentMusic));
                if (currentMusic != null) {
                    mMsgEntity.getData().setMusic(new ArrayList<MusicCardEntity>());
                    MusicEntity musicCardEntity = new MusicEntity();
                    musicCardEntity.setSource(currentMusic.getSource());
                    musicCardEntity.setName(currentMusic.getName());
                    musicCardEntity.setAlbumName(currentMusic.getAlbumName());
                    musicCardEntity.setCategory_type(currentMusic.getCategory_type());
                    MusicCardEntity.SingerInfosBean singerInfosBean = new MusicCardEntity.SingerInfosBean();
                    singerInfosBean.setName(currentMusic.getArtist());
                    mMsgEntity.getData().getMusicData().add(musicCardEntity);
                    ThreadPoolFactory.getNormalProxy().execute(new Runnable() {
                        @Override
                        public void run() {
                            List<MusicEntity> musicListDataEntity = MusicDBManager.getInstance().get(mMsgEntity.get_ID());
                            MusicDBManager.getInstance().save(musicListDataEntity, mMsgEntity.get_ID());
                        }
                    });

                    mMusicBinding.iMusic.tvMusicName.setText(currentMusic.getName());
                    mMusicBinding.iMusic.tvSingerName.setText(currentMusic.getArtist());
                    setPlayMode();
                    switch (currentMusic.getSource()) {
                        case WEB:
                        case TAI_HE:
                            mMusicBinding.iMusic.ivMusicPay.setVisibility(GONE);
                            break;
                        case QQ:
                            mMusicBinding.iMusic.ivMusicPay.setVisibility(GONE);
                            break;
                        case HIMALAYA:
                        case XIAO_YA:
                            mMusicBinding.iMusic.ivMusicPay.setVisibility(GONE);
                            break;
                        case LOCATION:
                            mMusicBinding.iMusic.ivMusicPay.setVisibility(GONE);
                            break;
                        case KOU_DAI:
                            break;
                        case LAZY_BOOK:
                            break;
                    }
                    mMusicBinding.iMusic.seek.setMax(MusicPlayManager.getInstance().getDuration());
                    if (!change) {
                        if (MusicPlayManager.getInstance().isPlaying()) {
                            mMusicBinding.iMusic.ivPlay.setImageResource(R.mipmap.music_btn_pause);
                        } else {
                            mMusicBinding.iMusic.ivPlay.setImageResource(R.mipmap.music_btn_play);
                        }
                    }

                    if (MusicPlayManager.getInstance().isPlaying()
                            || MusicPlayManager.getInstance().getState() == PlayState.PAUSED) {
                        mMusicBinding.iMusic.seek.setMax(MusicPlayManager.getInstance().getDuration());
                        mMusicBinding.iMusic.seek.setProgress(MusicPlayManager.getInstance().getCurrentPosition());
                        mMusicBinding.iMusic.tvProgress.setText(DateUtils.timestamp2String(MusicPlayManager.getInstance().getCurrentPosition(), "m:ss"));
                        mMusicBinding.iMusic.tvDuration.setText(DateUtils.timestamp2String(MusicPlayManager.getInstance().getDuration(), "m:ss"));
                    } else {
                        mMusicBinding.iMusic.seek.setMax(1000);
                        mMusicBinding.iMusic.seek.setProgress(0);
                        mMusicBinding.iMusic.tvProgress.setText(DateUtils.timestamp2String(0, "m:ss"));
                        mMusicBinding.iMusic.tvDuration.setText(DateUtils.timestamp2String(0, "m:ss"));
                    }
                }
            }
        });
    }

    private void setPlayMode() {
        switch (MusicPlayManager.getInstance().getPlayMode()) {
            case LOOP:
                mMusicBinding.iMusic.tvMode.setImageResource(R.mipmap.music_btn_listloop);
                break;
            case SINGLE:
                mMusicBinding.iMusic.tvMode.setImageResource(R.mipmap.music_icon_singlecycle);
                break;
            case RANDOM:
                mMusicBinding.iMusic.tvMode.setImageResource(R.mipmap.music_icon_random);
                break;
        }
    }

}
