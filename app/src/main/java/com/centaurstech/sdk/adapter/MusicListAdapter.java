package com.centaurstech.sdk.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import com.centaurstech.qiwu.db.MusicDBManager;
import com.centaurstech.qiwu.entity.MediaEntity;
import com.centaurstech.qiwu.entity.MsgEntity;
import com.centaurstech.qiwu.entity.MusicEntity;
import com.centaurstech.qiwu.entity.MusicListDataEntity;
import com.centaurstech.qiwu.listener.OnMusicPlayListener;
import com.centaurstech.qiwu.manager.MusicPlayManager;
import com.centaurstech.qiwu.utils.UIUtils;
import com.centaurstech.sdk.R;
import com.centaurstech.sdk.activity.MainActivity;
import com.centaurstech.sdk.databinding.ItemMusicPlayBinding;
import com.centaurstech.sdk.listener.OnMusicDeleteListener;
import com.centaurstech.sdk.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class MusicListAdapter extends RecyclerView.Adapter<MusicListAdapter.MusicListViewHolder> implements OnMusicPlayListener, OnMusicDeleteListener {

    MsgEntity mMsgEntity;
    private int mPosition = -1;
    private RecyclerView mRecyclerView;
    private long mLastClickTime;

    private ArrayList<MusicEntity> mMusicEntities = new ArrayList<>();

    public void setMusicEntities(ArrayList<MusicEntity> mMusicEntities) {
        this.mMusicEntities = mMusicEntities;
    }

    public MusicListAdapter(Context context, RecyclerView recyclerView) {
        mRecyclerView = recyclerView;
        ((MainActivity) context).addOnMusicDeleteListeners(this);
    }

    @NonNull
    @Override
    public MusicListViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new MusicListAdapter.MusicListViewHolder(View.inflate(viewGroup.getContext(), R.layout.item_music_play, null));
    }

    @Override
    public void onBindViewHolder(@NonNull MusicListViewHolder musicListViewHolder, int position) {
        MusicEntity itemData = mMusicEntities.get(position);
        musicListViewHolder.mItemMusicPlayBinding.tvMusicTitle.setText(itemData.getName());
        musicListViewHolder.mItemMusicPlayBinding.tvMusicSinger.setText(" - " + itemData.getArtist());
        if ((position == mPosition || itemData == MusicPlayManager.getInstance().getCurrentMusic()) && MusicPlayManager.getInstance().isPlaying()) {
            musicListViewHolder.mItemMusicPlayBinding.ivPlayState.setVisibility(VISIBLE);
            musicListViewHolder.mItemMusicPlayBinding.tvMusicTitle.setTextColor(UIUtils.getColor(R.color.colorFF9100));
            musicListViewHolder.mItemMusicPlayBinding.tvMusicSinger.setTextColor(UIUtils.getColor(R.color.colorFF9100));
        } else {
            musicListViewHolder.mItemMusicPlayBinding.ivPlayState.setVisibility(GONE);
            musicListViewHolder.mItemMusicPlayBinding.tvMusicTitle.setTextColor(UIUtils.getColor(R.color.color2A2A2A));
            musicListViewHolder.mItemMusicPlayBinding.tvMusicSinger.setTextColor(UIUtils.getColor(R.color.color999999));
        }
        musicListViewHolder.mItemMusicPlayBinding.ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMusicEntities.remove(position);
                MusicPlayManager.getInstance().deleteMusic(mMsgEntity.getData().getNot_title(), mMsgEntity.getData().getNot_singer());
                refreshList(MusicPlayManager.getInstance().getCurrentMusic());
                notifyDataSetChanged();
            }
        });

        musicListViewHolder.mItemMusicPlayBinding.flClickLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (MusicPlayManager.getInstance().getMusicEntities() == null || MusicPlayManager.getInstance().getMusicEntities().size() == 0) {
                    MusicPlayManager.getInstance().setMusicEntities(mMsgEntity.getData().getMusicData());
                }
                if (System.currentTimeMillis() - mPrevClickTime < 1000) return;
                mPrevClickTime = System.currentTimeMillis();

                if (MusicPlayManager.getInstance().getCurrentMusic() != null && MusicPlayManager.getInstance().getCurrentMusic().equals(itemData)) {

                } else {
                    if (MusicPlayManager.getInstance().getMusicEntities() != null
                            && MusicPlayManager.getInstance().getMusicEntities().size() > 0
                            && isContainsMusic(itemData)) {
                        MusicPlayManager.getInstance().setCurrentMusic(itemData);
                        MusicPlayManager.getInstance().start();
                    } else {
                        ToastUtils.show("资源不存在");
                    }
                }
            }
        });
    }

    private boolean isContainsMusic(MusicEntity itemData) {
        boolean c = false;
        for (MusicEntity music : MusicPlayManager.getInstance().getMusicEntities()) {
            if (music.getName().equals(itemData.getName()) && itemData.getArtist().equals(music.getArtist())) {
                c = true;
            }
        }
        return c;
    }


    private long mPrevClickTime = 0;

    @Override
    public int getItemCount() {
        return mMusicEntities.size();
    }

    @Override
    public void onNext(MusicEntity music) {
        refreshList(music);
    }

    @Override
    public void onPrevious(MusicEntity music) {
        refreshList(music);
    }

    @Override
    public void onPause(MusicEntity music) {
        refreshList(music);
    }

    @Override
    public void onStart(MusicEntity music) {
        refreshList(music);
    }

    @Override
    public void onChange(MusicEntity music) {
        refreshList(music);
    }

    @Override
    public void onStop(MusicEntity music) {

    }

    @Override
    public void onRelease(MusicEntity music, boolean change) {
        mPosition = -1;
        notifyDataSetChanged();
    }

    @Override
    public void onCompletion(MusicEntity music) {

    }

    @Override
    public void onReset(MusicEntity music) {

    }

    @Override
    public void onModeChange() {

    }

    @Override
    public void onProgress(int progress, int duration) {

    }

    @Override
    public void onBufferProgress(int percent, int duration) {

    }

    @Override
    public void onDelete(MsgEntity msgEntity) {
        deleteMusic(mMusicEntities, msgEntity);
        MusicDBManager.getInstance().remove(mMsgEntity.get_ID());
        notifyDataSetChanged();
    }

    private void deleteMusic(List<MusicEntity> musicEntities, MsgEntity msgEntity) {
        if (musicEntities == null || musicEntities.size() == 0) return;
        for (int i = musicEntities.size() - 1; i >= 0; i--) {
            MusicEntity musicEntity = musicEntities.get(i);
            if (!TextUtils.isEmpty(msgEntity.getData().getNot_singer()) && !TextUtils.isEmpty(msgEntity.getData().getNot_title())) {// 删除歌手+歌名
                if (musicEntity.getName().contains(msgEntity.getData().getNot_title()) && musicEntity.getArtist().equals(msgEntity.getData().getNot_singer())) {
                    musicEntities.remove(i);
                }
            } else if (!TextUtils.isEmpty(msgEntity.getData().getNot_singer()) && TextUtils.isEmpty(msgEntity.getData().getNot_title())) { // 删除歌手
                if (musicEntity.getArtist().contains(msgEntity.getData().getNot_singer())) {
                    musicEntities.remove(i);
                }
            } else if (TextUtils.isEmpty(msgEntity.getData().getNot_singer()) && !TextUtils.isEmpty(msgEntity.getData().getNot_title())) { // 删除歌名
                if (musicEntity.getName().contains(msgEntity.getData().getNot_title())) {
                    musicEntities.remove(i);
                }
            }
        }
    }

    public void refreshList(MusicEntity musicEntity) {
        if (mMsgEntity.getExpire() != 0) {
            return;
        }
        if (musicEntity == null) return;
//        if (musicEntity.getSource() == MediaEntity.Source.NEWS){
//            return;
//        }
        mPosition = MusicPlayManager.getInstance().getMusicEntities().indexOf(musicEntity);
        if (mPosition == -1) {
            for (int i = 0; i < mMusicEntities.size(); i++) {
                MusicEntity music = mMusicEntities.get(i);
                if (music != null) {
                    if (musicEntity.getArtist() != null && music.getArtist() != null && musicEntity.getName() != null && music.getName() != null) {
                        if (musicEntity.getArtist().equals(music.getArtist()) && musicEntity.getName().equals(music.getName())) {
                            mPosition = i;
                        }
                    }
                }
            }
        }
        setMusicEntities((ArrayList<MusicEntity>) mMsgEntity.getData().getMusicData());
        if (mPosition != -1 && mPosition < mMusicEntities.size()) {
            mRecyclerView.scrollToPosition(mPosition);
        }
    }

    public void setMsgEntity(MsgEntity msgEntity) {
        mMsgEntity = msgEntity;
        setMusicEntities((ArrayList<MusicEntity>) MusicPlayManager.getInstance().getMusicEntities());
        if (MusicPlayManager.getInstance().getCurrentMusic() != null) {
            if (MusicPlayManager.getInstance().isPlaying()) {
                mPosition = mMusicEntities.indexOf(MusicPlayManager.getInstance().getCurrentMusic());
                if (mPosition != -1 && mPosition < mMusicEntities.size()) {
                    mRecyclerView.scrollToPosition(mPosition);
                }
            }
        }
    }

    public static class MusicListViewHolder extends RecyclerView.ViewHolder {

        public ItemMusicPlayBinding mItemMusicPlayBinding;

        public MusicListViewHolder(@NonNull View itemView) {
            super(itemView);
            mItemMusicPlayBinding = DataBindingUtil.bind(itemView);
        }
    }
}
