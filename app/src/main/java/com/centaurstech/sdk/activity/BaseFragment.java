package com.centaurstech.sdk.activity;


import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.centaurstech.qiwu.entity.EventBusEntity;
import com.centaurstech.sdk.R;
import com.centaurstech.sdk.databinding.FragmentBaseBinding;
import com.centaurstech.sdk.listener.OnDestroyListener;
import com.qiwu.ui.listener.OnLoadingDialogCancelListener;
import com.qiwu.ui.view.LoadingLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;


public abstract class BaseFragment<B extends ViewDataBinding> extends Fragment implements OnLoadingDialogCancelListener {

    private LoadingLayout mLoadingLayout;
    public FragmentBaseBinding mBaseBinding;
    public BaseActivity mActivity;
    private B mViewBinding;
    private boolean mIsVisibleToUser; // 是否可见
    private boolean mLoadedFlag; // 加载过数据标记
    private List<OnDestroyListener> mOnDestroyListeners;
    private String mBaseTag = UUID.randomUUID().toString();

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        mIsVisibleToUser = isVisibleToUser;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (BaseActivity) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return initView(inflater);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (useEventBus()) {
            EventBus.getDefault().register(this);
        }
        init();
        try {
            mActivity.addOnLoadingDialogCancelListener(this);
            if (autoLoadData()) {
                lazyData();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private View initView(LayoutInflater inflater) {
        mBaseBinding = DataBindingUtil.bind(inflater.inflate(R.layout.fragment_base, null));
        return mBaseBinding.contentContainer;
    }

    private void initContentView() {
        try {
            if (mLoadingLayout == null) {// 第一次执行
                mLoadingLayout = new LoadingLayout(mActivity) {
                    @Override
                    public void initData() {
                        mLoadingLayout = this;
                        BaseFragment.this.initData();
                    }

                    @Override
                    public View initSuccessView() {
                        View tempView = LayoutInflater.from(getContext()).inflate(BaseFragment.this.initLayout(), null);
                        mViewBinding = DataBindingUtil.bind(tempView);
                        return tempView;
                    }

                    @Override
                    public void initViewElement() {
                        BaseFragment.this.initView(mViewBinding);
                    }

                    @Override
                    public void displayView() {
                        BaseFragment.this.displayView(mViewBinding);
                    }

                    @Override
                    public void initListener(View successView) {
                        BaseFragment.this.initListener(mViewBinding);
                    }

                };
            } else {// 第2次执行
                ((ViewGroup) mLoadingLayout.getParent()).removeView(mLoadingLayout);
            }
            mBaseBinding.contentContainer.addView(mLoadingLayout);
        } catch (Exception e) {
            e.printStackTrace();
            mActivity.restartApp();
        }

    }


    /**
     * 加载initData方法拦截，避免Fragment切换时重复加载数据
     */
    public void lazyData() {
        try {
            if (!mLoadedFlag) {
                if (getContext() == null || getContext().getResources() == null) {
                    mActivity.restartApp();
                    return;
                }
                initContentView();
                if (displayView()) {
                    mLoadingLayout.refreshUI(LoadingLayout.LoadedResult.DISPLAY);
                }
                mLoadedFlag = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 初始化页面（该方法中不能初始化成功页面元素，成功页面为空）
     */
    public abstract void init();


    /**
     * 初始化数据 （加载数据在该方法中实现）
     */
    public abstract void initData();

    /**
     * 初始化成功页面
     *
     * @return R.layout.id
     */
    public abstract int initLayout();


    /**
     * 初始化页面（数据加载成功前）
     *
     * @return R.layout.id
     */
    public abstract void initView(B viewBinding);


    /**
     * 显示数据
     *
     * @param viewBinding successView的binding类
     */
    public abstract void displayView(B viewBinding);

    /**
     * 初始化监听
     *
     * @param viewBinding successView的binding类
     */
    public void initListener(B viewBinding) {
    }


    /**
     * 是否在加载数据前加载成功页面（如果需要在加载数据前显示成功页面，请在init方法中调用该方法）
     * 默认不主动加载
     */
    public boolean displayView() {
        return true;
    }

    /**
     * 是否使用EventBus
     *
     * @return
     */
    public boolean useEventBus() {
        return false;
    }

    /**
     * 设置是否自动加载数据（自动调用initData方法）
     * 默认为自动加载
     */
    public boolean autoLoadData() {
        return false;
    }

    /**
     * 显示加载对话框
     */
    public void showLoadingDialog() {
        if (mActivity != null) {
            mActivity.showLoadingDialog();
        }
    }

    /**
     * 隐藏加载对话框
     */
    public void hideLoadingDialog() {
        if (mActivity != null) {
            mActivity.hideLoadingDialog();
        }
    }

    public String getBaseTag() {
        return mBaseTag;
    }

    /**
     * @param clazz 跳转的activity.class
     */
    public void launchActivity(Class clazz) {
        mActivity.launchActivity(clazz);
    }

    public void launchActivity(Class clazz, int requestCode) {
        mActivity.launchActivity(clazz, requestCode);
    }

    /**
     * @param intent 使用扩展的Intent
     *               <p>
     *               示例
     *               launchActivity(new IntentExpand(Activity.class) {
     * @Override public void extraValue(Intent intent) {
     * intent.putExtra("key","value");
     * }
     * });
     */


    public void launchActivity(final IntentExpand intent) {
        if (!mActivity.isCanLaunch()) return;
        startActivity(intent.intent);
    }


    public void launchActivity(Class clazz, final IntentExpand intentExpand) {
        if (!mActivity.isCanLaunch()) return;
        intentExpand.intent.setClass(getContext(), clazz);
        startActivity(intentExpand.intent);
    }

    /**
     * 带请求码的Intent
     *
     * @param intentExpand
     * @param requestCode
     */
    public void launchActivity(Class clazz, final IntentExpand intentExpand, final int requestCode) {
        if (!mActivity.isCanLaunch()) return;
        intentExpand.intent.setClass(getContext(), clazz);
        startActivityForResult(intentExpand.intent, requestCode);
    }

    /**
     * 返回传值方法
     *
     * @param intent Intent的扩展类
     */
    public void resultActivity(final IntentExpand intent) {
        mActivity.setResult(RESULT_OK, intent.intent);
        mActivity.finish();
    }

    public abstract static class IntentExpand {
        public Intent intent;

        public IntentExpand() {
            intent = new Intent();
            extraValue(intent);
        }

        //        public IntentExpand(Class clazz) {
//            this(clazz,-100);
//        }
//        public IntentExpand(Class clazz,int requestCode) {
//            intent = new Intent(mActivity,clazz);
//            this.requestCode = requestCode;
//            extraValue(intent);
//        }
        public abstract void extraValue(Intent intent);
    }


    public void postEvent(String eventMessage) {
        postEvent(eventMessage, null);
    }

    public void postEvent(String eventMessage, Object model) {
        EventBus.getDefault().post(new EventBusEntity(eventMessage, model));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(EventBusEntity event) {

    }

    /**
     * 当用户取消LoadingDialog时，只取消当前的fragment网络请求
     * mIsVisibleToUser -> 判断当前fragment是否为显示状态
     */
    @Override
    public void onCancel() {
        if (mIsVisibleToUser) {
        }
    }


    @Override
    public void onDestroyView() {
        try {
            if (useEventBus()) {
                EventBus.getDefault().unregister(this);
            }
            mActivity.removeOnLoadingDialogCancelListener(this);
            if (mOnDestroyListeners != null && mOnDestroyListeners.size() > 0) {
                for (OnDestroyListener listener : mOnDestroyListeners) {
                    listener.onDestroy();
                }
                mOnDestroyListeners.clear();
                mOnDestroyListeners = null;
            }
            mActivity = null;
            mLoadingLayout = null;
            mBaseBinding = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    public interface OnFragmentDestroyListener {
        void onDestroy();
    }


    public void addDestroyListener(OnDestroyListener onDestroyListener) {
        if (mOnDestroyListeners == null) {
            mOnDestroyListeners = new ArrayList<>();
        }
        if (!mOnDestroyListeners.contains(onDestroyListener)) {
            mOnDestroyListeners.add(onDestroyListener);
        }
    }

    public void removeFragmentDestroyListener(OnDestroyListener onDestroyListener) {
        if (mOnDestroyListeners != null) {
            if (mOnDestroyListeners.contains(onDestroyListener)) {
                mOnDestroyListeners.remove(onDestroyListener);
            }
        }
    }

    public LoadingLayout getLoadingLayout() {
        if (mLoadingLayout == null) {
            if (getContext() != null) {
                initContentView();
            }
        }
        return mLoadingLayout;
    }


    public B getViewBinding() {
        return mViewBinding;
    }

}