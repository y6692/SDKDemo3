package com.centaurstech.sdk.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.util.SparseArrayCompat;
import android.view.ViewGroup;

import com.centaurstech.sdk.activity.BaseFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Time:2019/12/25
 * Author: 樊德鹏
 * Description:
 */
public class UIFragmentPageAdapter extends FragmentPagerAdapter {
    private SparseArrayCompat<BaseFragment> mCachesFragment;
    private FragmentManager mFragmentManager;
    private List<String> mTags;
    private String[] mTitles ;
    private Bundle mBundle ;

    public UIFragmentPageAdapter(FragmentManager fm, SparseArrayCompat<BaseFragment> cachesFragment) {
        this(fm,cachesFragment,"");
    }

    public UIFragmentPageAdapter(FragmentManager fm, SparseArrayCompat<BaseFragment> cachesFragment, Bundle bundle) {
        this(fm,cachesFragment,bundle,"");
    }


    public UIFragmentPageAdapter(FragmentManager fm, SparseArrayCompat<BaseFragment> cachesFragment, String...titles) {
        this(fm,cachesFragment,null,titles);
    }

    public UIFragmentPageAdapter(FragmentManager fm, SparseArrayCompat<BaseFragment> cachesFragment, Bundle bundle, String...titles) {
        super(fm);
        this.mTags = new ArrayList<>();
        this.mFragmentManager = fm;
        this.mCachesFragment = cachesFragment;
        this.mBundle = bundle;
        mTitles = titles;
    }

    public void setNewFragments(SparseArrayCompat<BaseFragment> cachesFragment) {
        if (this.mTags != null) {
            FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
            for (int i = 0; i < mTags.size(); i++) {
                fragmentTransaction.remove(mFragmentManager.findFragmentByTag(mTags.get(i)));
            }
            fragmentTransaction.commit();
            mFragmentManager.executePendingTransactions();
            mTags.clear();
        }
        this.mCachesFragment = cachesFragment;
        notifyDataSetChanged();
    }

    @Override
    public Fragment getItem(int position) {
        if (mBundle != null)mCachesFragment.get(position).setArguments(mBundle);
        return mCachesFragment.get(position);
    }

    @Override
    public int getCount() {
        return mCachesFragment.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        mTags.add(makeFragmentName(container.getId(), getItemId(position)));
        Fragment fragment = (Fragment) super.instantiateItem(container, position);
        this.mFragmentManager.beginTransaction().show(fragment).commitAllowingStateLoss();
        return fragment;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        Fragment fragment = mCachesFragment.get(position);
        if (fragment == null || fragment.getActivity() == null){
            mCachesFragment.clear();
            return;
        }
        mFragmentManager.beginTransaction().hide(fragment).commitAllowingStateLoss();
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    //此方法用来显示tab上的名字
    @Override
    public CharSequence getPageTitle(int position) {
        if (mTitles.length < mCachesFragment.size()){
            return "";
        }
        return mTitles[position % mTitles.length];
    }

    private static String makeFragmentName(int viewId, long id) {
        return "android:switcher:" + viewId + ":" + id;
    }
}
