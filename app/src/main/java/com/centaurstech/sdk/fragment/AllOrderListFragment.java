package com.centaurstech.sdk.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.centaurstech.qiwu.utils.LogUtils;

/**
 * Time:2019/12/25
 * Author: 樊德鹏
 * Description:
 */
public class AllOrderListFragment extends OrderListFragment  {

    @Override
    public int getPosition() {
        return 0;
    }



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtils.sf("Order Fragment onCreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LogUtils.sf("Order Fragment onCreateView");
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        LogUtils.sf("Order Fragment onActivityCreated");
    }

    @Override
    public void onStart() {
        super.onStart();
        LogUtils.sf("Order Fragment onStart");
    }

    @Override
    public void onResume() {
        super.onResume();
        LogUtils.sf("Order Fragment onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        LogUtils.sf("Order Fragment onPause");
    }

    @Override
    public void onStop() {
        super.onStop();
        LogUtils.sf("Order Fragment onStop");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        LogUtils.sf("Order Fragment onDestroyView");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtils.sf("Order Fragment onDestroy");
    }
}
