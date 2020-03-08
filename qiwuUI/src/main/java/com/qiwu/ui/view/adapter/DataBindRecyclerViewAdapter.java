package com.qiwu.ui.view.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.qiwu.ui.R;

import java.util.ArrayList;
import java.util.List;



/**
 * @data 2019/12/25
 * @author: 樊德鹏
 * @description:
 */
public abstract class DataBindRecyclerViewAdapter<M, B extends ViewDataBinding> extends BGABindingRecyclerViewAdapter<M, B> {


    private static String STATE_EMPTY = "state_empty";
    private static String STATE_ERROR = "state_error";
    private static String STATE_LOADING = "state_loading";


    private OnItemClickListener mOnItemClickListener;
    private OnItemLongClickListener mOnItemLongClickListener;

    private boolean mIsRespondItemLongClick = true;
    private boolean autoCheckData = false;

    private Context mContext;


    public DataBindRecyclerViewAdapter(Context context) {
        mContext = context;
    }

    public DataBindRecyclerViewAdapter(int defaultItemLayoutId) {
        super(defaultItemLayoutId);
    }


    public DataBindRecyclerViewAdapter(int defaultItemLayoutId, List<M> data) {
        super(defaultItemLayoutId);
        setData(data);
    }

    @Override
    public BGABindingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        DataBindViewHolder<B> dataBindViewHolder = new DataBindViewHolder<B>(this, DataBindingUtil.<B>inflate(getLayoutInflater(parent), viewType, parent, false));
        dispatchItemClickListener(dataBindViewHolder);
        return dataBindViewHolder;
    }

    @Override
    public final void onBindViewHolder(BGABindingViewHolder<B> viewHolder, int position) {
        super.onBindViewHolder(viewHolder, position);
        try {
            onBindHolder(viewHolder.getBinding(), mData.get(position), position);
            if (mClickViews.size() > 0) {
                for (View view : mClickViews) {
                    bindItemViewClickListener((DataBindViewHolder<B>) viewHolder, view);
                }
            }
            if (mLongClickViews.size() > 0) {
                for (View view : mLongClickViews) {
                    bindItemViewLongClickListener((DataBindViewHolder<B>) viewHolder, view);
                }
            }
            mClickViews.clear();
            mLongClickViews.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public abstract void onBindHolder(B binding, M itemData, int position);

//    @Override
//    public void addNewData(List<M> newData) {
//        this.mData.addAll(newData);
//        notifyItemRangeInserted(mData.size() - newData.size() + getHeaderAndFooterAdapter().getHeadersCount(), newData.size());
//        compatibilityDataSizeChanged(newData.size());
//    }

    public boolean setItemClick() {
        return true;
    }

    public boolean setItemLongClick() {
        return true;
    }

    public void checkAdapterData() {
        for (int i = getHeaderAndFooterAdapter().getFootersCount() - 1; i >= 0; i--) {
            View view = getHeaderAndFooterAdapter().getFootViews().valueAt(i);
            if (view.getTag().equals(STATE_EMPTY)
                    || view.getTag().equals(STATE_ERROR)
                    || view.getTag().equals(STATE_LOADING)) {
                getHeaderAndFooterAdapter().getFootViews().removeAt(i);
            }
        }
        if (!autoCheckData()) {
            notifyDataSetChanged();
            return;
        }
        if (mData.size() > 0) {
            getHeaderAndFooterAdapter().notifyDataSetChanged();
        } else {
            addFooterView(createEmptyView());
            getHeaderAndFooterAdapter().notifyDataSetChanged();
        }
    }

    private View mEmptyView;
    private int mLayout = 0;
    private int mImage = 0;
    private int mText = 0;

    public void setEmptyView(int layout) {
        mLayout = layout;
        autoCheckData = true;
    }
    public void setEmptyImage(int res) {
        mImage = res;
        autoCheckData = true;
    }
    public void setEmptyText(int res) {
        mText = res;
        autoCheckData = true;
    }

    private View createEmptyView() {
        if (mLayout != 0) {
            mEmptyView = View.inflate(mContext, mLayout, null);
        } else {
            mEmptyView = View.inflate(mContext, R.layout.layout_empty_view, null);
            ImageView imageView = mEmptyView.findViewById(R.id.ivEmpty);
            TextView textView = mEmptyView.findViewById(R.id.tvText);
            if (mImage != 0){
                imageView.setImageResource(mImage);
            }
            if (mText != 0){
                textView.setText(mText);
            }
        }
        mEmptyView.setTag(STATE_EMPTY);
        mEmptyView.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.MATCH_PARENT));
        return mEmptyView;
    }


    private View addErrorView() {
        return null;
    }

    public boolean autoCheckData() {
        return autoCheckData;
    }

    @Override
    public void addFirstItem(M model) {
        super.addFirstItem(model);
    }

    @Override
    public void addLastItem(M model) {
        super.addLastItem(model);
    }


    @Override
    public void addMoreData(List<M> data) {
        super.addMoreData(data);
    }

    @Override
    public void setData(List<M> data) {
        mData.clear();
        if (data != null && data.size() > 0) {
            mData.addAll(data);
        }
        checkAdapterData();
    }

    public void setData(@IntRange(from = 0) int index, @NonNull M data) {
        mData.set(index, data);
        notifyItemChanged(index + getHeaderAndFooterAdapter().getHeadersCount());
//        checkAdapterData();
    }

    public void setData(@NonNull M data) {
        mData.clear();
        mData.add(data);
        checkAdapterData();
    }


    public void addData(@IntRange(from = 0) int position, @NonNull List<M> data) {
        mData.addAll(position, data);
        notifyItemRangeInserted(position + getHeaderAndFooterAdapter().getHeadersCount(), data.size());
        compatibilityDataSizeChanged(data.size());
    }

    public void addData(M data) {
        mData.add(data);
        notifyItemChanged(mData.size() - 1 + getHeaderAndFooterAdapter().getHeadersCount());
//        checkAdapterData();
    }


    public void addNewData(M data) {
        mData.add(0, data);
        notifyItemRangeInserted(getHeaderAndFooterAdapter().getHeadersCount(), 1);
        compatibilityDataSizeChanged(1);
//        checkAdapterData();
    }


    @Override
    public List<M> getData() {
        return mData;
    }

    /**
     * remove the item associated with the specified position of adapter
     *
     * @param position
     */
    public void remove(@IntRange(from = 0) int position) {
        mData.remove(position);
        int internalPosition = position + getHeaderAndFooterAdapter().getHeadersCount();
        notifyItemRemoved(internalPosition);
        compatibilityDataSizeChanged(0);
        notifyItemRangeChanged(internalPosition, mData.size() - internalPosition);
    }

    public void removeAll() {
        mData.clear();
        notifyDataSetChanged();
    }

    private void compatibilityDataSizeChanged(int size) {
        final int dataSize = mData == null ? 0 : mData.size();
        if (dataSize == size) {
            notifyDataSetChanged();
        }
    }


    // 点击事件监听方法
    public void setOnItemClickListener(OnItemClickListener listener) {
        mOnItemClickListener = listener;
//        mOnItemClickListeners.add(listener);
    }

    // 长按事件监听方法
    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        mOnItemLongClickListener = listener;
//        mOnItemLongClickListeners.add(listener);
    }


    //    private List<Integer> mClickIds = new ArrayList<Integer>();
    private List<View> mClickViews = new ArrayList<View>();
    private List<View> mLongClickViews = new ArrayList<View>();

    public void addClickListener(View View) {
        mClickViews.add(View);
    }

    public void addClickLongListener(View View) {
        mLongClickViews.add(View);
    }

    public void addLongClickListener(View View) {
        mLongClickViews.add(View);
    }

    public void isRespondItemLongClick(boolean isRespondItemLongClick) {
        mIsRespondItemLongClick = isRespondItemLongClick;
    }

    public void bindItemViewClickListener(final B bind, final M data, View v, final int position) {
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnItemClickListener.onItemClick(bind, data, position, v);
                itemClick(bind, data, position, v);
            }
        });
    }

    public void itemClick(B binding, M itemData, int position, View v) {

    }

    public void itemLongClick(B binding, M itemData, int position, View v) {

    }

    private void bindItemViewClickListener(final DataBindViewHolder<B> vh, View v) {
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setItemClick(vh, v);
            }
        });
    }

    private void bindItemViewLongClickListener(final DataBindViewHolder<B> vh, View v) {
        v.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return setItemLongClick(vh, v);
            }
        });
    }


    private void dispatchItemClickListener(final DataBindViewHolder<B> vh) {
        try {

            if (setItemClick()) {
                vh.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setItemClick(vh, v);
                    }
                });
            }

            if (setItemLongClick()) {
                vh.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        return setItemLongClick(vh, v);
                    }
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setItemClick(final DataBindViewHolder<B> vh, View v) {
        try {
            if (vh.getBinding() == null || v == null || getData() == null || getData().size() == 0)
                return;
            if (mHeaderAndFooterAdapter != null && getHeadersCount() > 0) {
                itemClick(vh.getBinding(), getData().get(vh.getLayoutPosition() - getHeadersCount()), vh.getLayoutPosition() - getHeadersCount(), v);
                if (mOnItemClickListener != null)
                    mOnItemClickListener.onItemClick(vh.getBinding(), getData().get(vh.getLayoutPosition() - getHeadersCount()), vh.getLayoutPosition() - getHeadersCount(), v);
            } else {
                itemClick(vh.getBinding(), getData().get(vh.getLayoutPosition()), vh.getLayoutPosition(), v);
                if (mOnItemClickListener != null)
                    mOnItemClickListener.onItemClick(vh.getBinding(), getData().get(vh.getLayoutPosition()), vh.getLayoutPosition(), v);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private boolean setItemLongClick(final DataBindViewHolder<B> vh, View v) {
        try {
            if (vh.getBinding() == null || v == null || getData() == null || getData().size() == 0)
                return false;
            if (mHeaderAndFooterAdapter != null && getHeadersCount() > 0) {
                itemLongClick(vh.getBinding(), getData().get(vh.getLayoutPosition() - getHeadersCount()), vh.getLayoutPosition() - getHeadersCount(), v);
                if (mOnItemLongClickListener != null)
                    mOnItemLongClickListener.onItemLongClick(vh.getBinding(), getData().get(vh.getLayoutPosition() - getHeadersCount()), vh.getLayoutPosition() - getHeadersCount(), v);
            } else {
                itemLongClick(vh.getBinding(), getData().get(vh.getLayoutPosition()), vh.getLayoutPosition(), v);
                if (mOnItemLongClickListener != null)
                    mOnItemLongClickListener.onItemLongClick(vh.getBinding(), getData().get(vh.getLayoutPosition()), vh.getLayoutPosition(), v);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

//    public abstract class OnItemClickListener{
//        abstract void onItemClick(B itemBinding,M itemData,int position);
//    }

    public interface OnItemClickListener<M, B extends ViewDataBinding> {
        void onItemClick(B binding, M itemData, int position, View v);
    }

    public interface OnItemLongClickListener<M, B extends ViewDataBinding> {
        void onItemLongClick(B binding, M itemData, int position, View v);
    }

    public Context getContext() {
        return mContext;
    }
}
