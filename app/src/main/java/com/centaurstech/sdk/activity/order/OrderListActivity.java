package com.centaurstech.sdk.activity.order;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.util.SparseArrayCompat;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.centaurstech.qiwu.utils.UIUtils;
import com.centaurstech.sdk.R;
import com.centaurstech.sdk.activity.BaseActivity;
import com.centaurstech.sdk.activity.BaseFragment;
import com.centaurstech.sdk.adapter.UIFragmentPageAdapter;
import com.centaurstech.sdk.databinding.ActivityListBinding;
import com.centaurstech.sdk.fragment.AllOrderListFragment;
import com.centaurstech.sdk.fragment.CancelOrderListFragment;
import com.centaurstech.sdk.fragment.NoTravelOrderListFragment;
import com.centaurstech.sdk.fragment.ProcessingFragment;
import com.centaurstech.sdk.fragment.TravelOrderListFragment;
import com.centaurstech.sdk.fragment.UnpaidOrderListFragment;

/**
 * @author Leon(黄长亮)
 * @describe 页面索引
 * @date 2019/6/22
 */
public class OrderListActivity extends BaseActivity {

    ActivityListBinding mListBinding;

    SparseArrayCompat<BaseFragment> mCachesFragment = new SparseArrayCompat<>();
    private int mCurrentPosition = 0;
    private static final int REQUEST_CODE_LOGIN = 12;
    private AllOrderListFragment mAllOrderListFragment = new AllOrderListFragment();

    private UnpaidOrderListFragment mUnpaidOrderListFragment = new UnpaidOrderListFragment();

    private ProcessingFragment mProcessingFragment = new ProcessingFragment();

    private NoTravelOrderListFragment mNoTravelOrderListFragment = new NoTravelOrderListFragment();

    private TravelOrderListFragment mTravelOrderListFragment = new TravelOrderListFragment();

    private CancelOrderListFragment mCancelOrderListFragment = new CancelOrderListFragment();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mListBinding = DataBindingUtil.setContentView(this, R.layout.activity_list);
        mCurrentPosition = getIntent().getIntExtra("position", 0);
        initView();
        initListener();
    }

    private void initView() {
        mListBinding.iHeadLayout.rlRoot.setBackgroundColor(UIUtils.getColor(R.color.colorWhite));
        mListBinding.iHeadLayout.tvTitle.setText(UIUtils.getString(R.string.my_order));
        mListBinding.iHeadLayout.tvTitle.setTextColor(UIUtils.getColor(R.color.colorFF000000));
        mListBinding.iHeadLayout.btnNavBack.setImageResource(R.mipmap.nav_btn_back);
        mCachesFragment.put(0, mAllOrderListFragment.setTargetPosition(mCurrentPosition));
        mCachesFragment.put(1, mUnpaidOrderListFragment.setTargetPosition(mCurrentPosition));
        mCachesFragment.put(2, mProcessingFragment.setTargetPosition(mCurrentPosition));
        mCachesFragment.put(3, mNoTravelOrderListFragment.setTargetPosition(mCurrentPosition));
        mCachesFragment.put(4, mTravelOrderListFragment.setTargetPosition(mCurrentPosition));
        mCachesFragment.put(5, mCancelOrderListFragment.setTargetPosition(mCurrentPosition));
        String[] titles = new String[]{getString(R.string.all),
                getString(R.string.unpaid),
                getString(R.string.processing),
                getString(R.string.no_use),
                getString(R.string.is_use),
                getString(R.string.cancel_refund)};
        UIFragmentPageAdapter fragmentPageAdapter = new UIFragmentPageAdapter(getSupportFragmentManager(), mCachesFragment, titles);
        mListBinding.viewPager.setAdapter(fragmentPageAdapter);
        mListBinding.tabOrder.setupWithViewPager(mListBinding.viewPager);
        mListBinding.viewPager.setCurrentItem(mCurrentPosition);
    }

    private void initListener() {
        mListBinding.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                mCurrentPosition = position;
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (state == 0) mCachesFragment.get(mCurrentPosition).lazyData();
            }
        });

        mListBinding.iHeadLayout.btnNavBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

}
