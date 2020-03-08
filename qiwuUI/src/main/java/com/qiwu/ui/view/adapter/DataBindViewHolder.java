package com.qiwu.ui.view.adapter;

import android.databinding.ViewDataBinding;



/**
 * @data 2019/12/25
 * @author: 樊德鹏
 * @description:
 */
public class DataBindViewHolder<B extends ViewDataBinding> extends BGABindingViewHolder<B> {

    public DataBindViewHolder(DataBindRecyclerViewAdapter dataBindRecyclerViewAdapter, B binding) {
        super(dataBindRecyclerViewAdapter, binding);
    }
}
