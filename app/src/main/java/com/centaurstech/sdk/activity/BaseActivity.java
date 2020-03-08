package com.centaurstech.sdk.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.centaurstech.qiwu.utils.LogUtils;
import com.centaurstech.sdk.R;
import com.centaurstech.sdk.callback.PermissionCallback;
import com.centaurstech.qiwu.entity.EventBusEntity;
import com.centaurstech.sdk.listener.OnDestroyListener;
import com.centaurstech.sdk.utils.StatusBarUtil;
import com.centaurstech.voice.QiWuVoice;
import com.qiwu.ui.dialog.LoadingDialog;
import com.qiwu.ui.listener.OnLoadingDialogCancelListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static com.centaurstech.qiwu.QiWu.getContext;

/**
 * @author Leon(黄长亮)
 * @date 2019/6/22
 */
@SuppressLint("Registered")
public class BaseActivity extends AppCompatActivity {

    private LoadingDialog mLoadingDialog;
    private boolean isUsed;
    private PermissionCallback mPermissionCallback;
    private List<OnLoadingDialogCancelListener> mOnLoadingDialogCancelListenerLists;

    private static List<BaseActivity> activities = new LinkedList<>();

    private List<OnDestroyListener> mOnDestroyListeners;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        if (immerse()) {
            StatusBarUtil.transparencyBar(this);
        } else {
            setStatusBarColor(statusBarColor());
        }
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            ViewGroup view = (ViewGroup) getWindow().getDecorView();
            view.findViewById(android.R.id.content).setPadding(0, getStatusBarHeight(), 0, 0);
        }
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        addActivity(this);
    }

    /**
     * 状态栏颜色
     */
    public int statusBarColor() {
        return R.color.color2A2A2A;
    }

    /**
     * 设置是否为沉浸式
     */
    public boolean immerse() {
        return false;
    }

    /**
     * 设置状态栏颜色
     */
    public void setStatusBarColor(@ColorRes int color) {
        StatusBarUtil.setStatusBarColor(this, color);
    }

    /**
     * 获取状态栏高度
     *
     * @return
     */
    public int getStatusBarHeight() {
        int height = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            height = getResources().getDimensionPixelSize(resourceId);
        }
        return height;
    }

    public static void addActivity(BaseActivity activity) {
        if (!activities.contains(activity)) {
            activities.add(activity);
        }
    }

    public static void removeActivity(BaseActivity activity) {
        if (activities.contains(activity)) {
            activities.remove(activity);
        }
    }

    public void launchActivity(Class clazz) {
        QiWuVoice.getTTS().stop();
        Intent intent = new Intent(this, clazz);
        startActivity(intent);
    }

    public void launchActivity(Class clazz, String key, String value) {
        QiWuVoice.getTTS().stop();
        Intent intent = new Intent(this, clazz);
        intent.putExtra(key, value);
        startActivity(intent);
    }

    /**
     * 带请求码的Intent
     *
     * @param intentExpand
     * @param requestCode
     */
    public void launchActivity(Class clazz, final IntentExpand intentExpand, final int requestCode) {
        QiWuVoice.getTTS().stop();
        if (isContainsActivity(clazz.getName())) return;
        if (!isCanLaunch()) return;
        intentExpand.intent.setClass(this, clazz);
        startActivityForResult(intentExpand.intent, requestCode);
    }

    public void launchActivity(Class clazz, final IntentExpand intentExpand) {
        if (!isCanLaunch()) return;
        intentExpand.intent.setClass(getContext(), clazz);
        startActivity(intentExpand.intent);
    }

    public abstract static class IntentExpand{
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

    public boolean isContainsActivity(String className) {
        for (BaseActivity activity : activities) {
            if (activity.getClass().getName().contains(className))
                return true;
        }
        return false;
    }

    public void launchActivity(Class clazz, int requestCode) {
        if (isContainsActivity(clazz.getName())) return;
        if (!isCanLaunch()) return;
        Intent intent = new Intent(this, clazz);
        startActivityForResult(intent, requestCode);
    }

    private long mLastClickTimestamp;

    public boolean isCanLaunch() {
        boolean b = System.currentTimeMillis() - mLastClickTimestamp > 1000;
        if (b) {
            mLastClickTimestamp = System.currentTimeMillis();
        }
        return b;
    }

    public void launchActivity(Class clazz, Map<String, Serializable> data) {
        QiWuVoice.getTTS().stop();
        Intent intent = new Intent(this, clazz);
        for (Map.Entry<String, Serializable> entry : data.entrySet()) {
            intent.putExtra(entry.getKey(), entry.getValue());
        }
        startActivity(intent);
    }

    public boolean useEventBus() {
        return false;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (useEventBus() && !isUsed) {
            isUsed = true;
            EventBus.getDefault().register(this);
        }
    }

    @Override
    protected void onDestroy() {
        try {
            removeActivity(this);
            if (useEventBus()) {
                EventBus.getDefault().unregister(this);
            }
            if (mOnDestroyListeners != null && mOnDestroyListeners.size() > 0) {
                for (OnDestroyListener onDestroyListener : mOnDestroyListeners) {
                    onDestroyListener.onDestroy();
                }
                mOnDestroyListeners.clear();
                mOnDestroyListeners = null;
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }

    public void applyPermission(String permission, PermissionCallback permissionCallback) {
        applyPermission(new String[]{permission}, permissionCallback);
    }

    public void applyPermission(String[] permissions, PermissionCallback permissionCallback) {
        LogUtils.sf(Arrays.toString(permissions));
        mPermissionCallback = permissionCallback;
        LinkedList<String> deniedPermissions = new LinkedList<>();
        for (String permission : permissions) {
            if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                deniedPermissions.add(permission);
            }
        }
        String[] dps = new String[deniedPermissions.size()];
        int index = 0;
        for (String permission : deniedPermissions) {
            dps[index] = permission;
            index++;
        }
        if (deniedPermissions.isEmpty()) {
            if (mPermissionCallback != null) {
                mPermissionCallback.onGranted();
            }
        } else {
            ActivityCompat.requestPermissions(this, dps, 0);
        }
    }

    @Override
    public void finish() {
        try {
            hintKbTwo();
            removeActivity(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.finish();
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(EventBusEntity event) {

    }

    public void addDestroyListener(OnDestroyListener onDestroyListener) {
        if (mOnDestroyListeners == null) {
            mOnDestroyListeners = new ArrayList<>();
        }
        if (!mOnDestroyListeners.contains(onDestroyListener)) {
            mOnDestroyListeners.add(onDestroyListener);
        }
    }

    public void removeDestroyListener(OnDestroyListener onDestroyListener) {
        if (mOnDestroyListeners != null) {
            if (mOnDestroyListeners.contains(onDestroyListener)) {
                mOnDestroyListeners.remove(onDestroyListener);
            }
        }
    }

    private void initLoadingDialog() {
        mLoadingDialog = new LoadingDialog(this);
        mLoadingDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                try {
                    if (mOnLoadingDialogCancelListenerLists != null && mOnLoadingDialogCancelListenerLists.size() > 0) {
                        for (OnLoadingDialogCancelListener dialogCancel : mOnLoadingDialogCancelListenerLists) {
                            if (dialogCancel != null) {
                                dialogCancel.onCancel();
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
    }

    /**
     * 显示加载对话框
     */
    public void showLoadingDialog() {
        if (mLoadingDialog == null) {
            initLoadingDialog();
        }
        if (!mLoadingDialog.isShowing()) {
            mLoadingDialog.show();
        }
    }

    /**
     * 隐藏加载对话框
     */
    public void hideLoadingDialog() {
        if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
            mLoadingDialog.dismiss();
        }
    }

    /**
     * 返回传值方法
     */
    public void resultActivity() {
        setResult(RESULT_OK, new Intent());
        finish();
    }

    /**
     * 返回传值方法
     *
     * @param intent Intent的扩展类
     */
    public void resultActivity(final IntentExpand intent) {
        setResult(RESULT_OK, intent.intent);
        finish();
    }

    /**
     * 重启App
     */
    public void restartApp() {
        BaseActivity.finishAllActivity();
        final Intent intent = getPackageManager()
                .getLaunchIntentForPackage(getPackageName());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        android.os.Process.killProcess(android.os.Process.myPid());  //结束进程之前可以把你程序的注销或者退出代码放在这段代码之前
    }

    public static void finishAllActivity() {
        try {
            for (int i = activities.size()-1;i>=0;i--){
                activities.get(i).finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addOnLoadingDialogCancelListener(OnLoadingDialogCancelListener onLoadingDialogCancelListener) {
        if (mOnLoadingDialogCancelListenerLists == null) {
            mOnLoadingDialogCancelListenerLists = new ArrayList<>();
        }
        if (!mOnLoadingDialogCancelListenerLists.contains(onLoadingDialogCancelListener)) {
            mOnLoadingDialogCancelListenerLists.add(onLoadingDialogCancelListener);
        }
    }

    public void removeOnLoadingDialogCancelListener(OnLoadingDialogCancelListener onLoadingDialogCancelListener) {
        if (mOnLoadingDialogCancelListenerLists != null) {
            if (mOnLoadingDialogCancelListenerLists.contains(onLoadingDialogCancelListener)) {
                mOnLoadingDialogCancelListenerLists.remove(onLoadingDialogCancelListener);
            }
        }
    }

    /**
     * 退出软键盘
     */
    private void hintKbTwo() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive() && getCurrentFocus() != null) {
            if (getCurrentFocus().getWindowToken() != null) {
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }

    public static List<BaseActivity> getActivities() {
        return activities;
    }
}
