package com.centaurstech.sdk.fragment;

import android.support.v7.widget.LinearLayoutManager;

import com.centaurstech.qiwu.QiWuAPI;
import com.centaurstech.qiwu.callback.APICallback;
import com.centaurstech.qiwu.entity.APIEntity;
import com.centaurstech.qiwu.entity.EventBusEntity;
import com.centaurstech.qiwu.entity.OrderEntity;
import com.centaurstech.qiwu.utils.GsonUtils;
import com.centaurstech.qiwu.utils.LogUtils;
import com.centaurstech.sdk.R;
import com.centaurstech.sdk.activity.BaseFragment;
import com.centaurstech.sdk.adapter.OrderAdapter;
import com.centaurstech.sdk.common.Const2;
import com.centaurstech.sdk.databinding.FragmentListBinding;
import com.qiwu.ui.view.LoadingLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Time:2019/12/25
 * Author: 樊德鹏
 * Description:
 */
public class OrderListFragment extends BaseFragment<FragmentListBinding> {

    private OrderAdapter mMeOrderAdapter;
    private int mTargetPosition;
    private List<OrderEntity> data = new ArrayList<>();
    private int type;


    @Override
    public boolean displayView() {
        return false;
    }

    @Override
    public boolean useEventBus() {
        return true;
    }

    public int getPosition() {
        return 0;
    }

    @Override
    public void initListener(final FragmentListBinding viewBinding) {
        super.initListener(viewBinding);
    }

    public OrderListFragment setTargetPosition(int targetPosition) {
        mTargetPosition = targetPosition;
        return this;
    }

    @Override
    public boolean autoLoadData() {
        return getPosition() == mTargetPosition;
    }

    @Override
    public void init() {

    }

    @Override
    public void initData() {

    }

    @Override
    public int initLayout() {
        return R.layout.fragment_list;
    }

    @Override
    public void initView(FragmentListBinding viewBinding) {
        type = 100;
        switch (getPosition()) {
            case 0:
                type = 0;
                break;
            case 1:
                type = 1;
                break;
            case 2:
                type = 100;
                break;
            case 3:
                type = 2;
                break;
            case 4:
                type = 3;
                break;
            case 5:
                type = 200;
                break;
        }
        viewBinding.rvList.setBackgroundResource(R.color.colorF7F7F7);
        viewBinding.rvList.setLayoutManager(new LinearLayoutManager(getContext()));
        mMeOrderAdapter = new OrderAdapter(getContext(), this);
        viewBinding.rvList.setAdapter(mMeOrderAdapter);
        loadData();
    }

    public void loadData() {
        QiWuAPI.order.getOrders(type, null, 50, new APICallback<APIEntity<ArrayList<OrderEntity>>>() {
            @Override
            public void onSuccess(APIEntity<ArrayList<OrderEntity>> response) {
                if (mMeOrderAdapter == null || mMeOrderAdapter.getData() == null) {
                    return;
                }
                if (mMeOrderAdapter.getData().size() == 0) {
                    getLoadingLayout().refreshUI(LoadingLayout.LoadedResult.DISPLAY);
                }
                if (response.isSuccess()) {
                    mMeOrderAdapter.setData(response.getData());
                }
            }
        });
    }

    @Override
    public void displayView(FragmentListBinding viewBinding) {

    }

    @Override
    public void onEvent(EventBusEntity event) {
        super.onEvent(event);
        switch (event.getMessage()) {
            case Const2.update_order:
                loadData();
                break;
        }
    }
}
