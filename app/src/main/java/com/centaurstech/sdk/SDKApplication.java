package com.centaurstech.sdk;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.text.TextUtils;

import com.centaurstech.qiwu.manager.AudioPlayManager;
import com.centaurstech.qiwu.utils.LogUtils;
import com.centaurstech.sdk.activity.BaseActivity;
import com.centaurstech.sdk.activity.MainActivity;
import com.centaurstech.sdk.common.Const2;
import com.centaurstech.qiwu.entity.EventBusEntity;
import com.centaurstech.sdk.utils.ToastUtils;
import com.centaurstech.voice.QiWuVoice;
import com.qiwu.ui.common.AppConfig;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * @author Leon(黄长亮)
 * @describe SDKApplication
 * @date 2019/5/22
 */
public class SDKApplication extends MultiDexApplication implements Application.ActivityLifecycleCallbacks {

    private static SDKApplication mApp;

    private boolean mIsEnterBackstage = true;

    public static SDKApplication getInstance() {
        return mApp;
    }

    private int appOrder = 2;

    public int getAppOrder() {
        return appOrder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //
        ToastUtils.init(this);
    }


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        mApp = this;
        MultiDex.install(base);
        AppConfig.init(this);
//        StubAppUtils.attachBaseContext(base);
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle bundle) {

    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {
        LogUtils.sf("onActivityStopped " + isForeground() + " " + isAppOnForeground());
        if (!isForeground() || !isAppOnForeground()) {
            SDKApplication.getInstance().setEnterBackstage(true);
            AudioPlayManager.getInstance().stop();
            QiWuVoice.getTTS().stop();
            //如果需要退出后台还可唤醒 请注释dormant（）方法
            QiWuVoice.getWakeup().dormant();
//            EventBus.getDefault().post(new EventBusEntity(Const2.close_audio));
        }
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }

    /**
     * 程序是否在前台运行
     *
     * @return
     */
    public boolean isAppOnForeground() {
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        String packageName = getApplicationContext().getPackageName();
        assert activityManager != null;
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager
                .getRunningAppProcesses();
        if (appProcesses == null)
            return false;
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(packageName)
                    && appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                return true;
            }
        }
        return false;
    }

    @RequiresApi(api = 29)
    public boolean isForeground() {
        ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        if (am != null && am.getRunningTasks(1).size() > 0) {
            ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
            String currentPackageName = cn.getPackageName();
            return !TextUtils.isEmpty(currentPackageName) && currentPackageName.equals(getPackageName());
        } else {
            return false;
        }
    }

    /**
     * 返回主页
     */
    public static void returnMainActivity() {
        try {
            List<BaseActivity> activities = BaseActivity.getActivities();
            if (activities != null && activities.size() > 0) {
                for (int i = activities.size() - 1; i >= 0; i--) {
                    BaseActivity activity = activities.get(i);
                    if (activity instanceof MainActivity) continue;
                    activity.finish();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void setEnterBackstage(boolean enterBackstage) {
        mIsEnterBackstage = enterBackstage;
    }

    public boolean isEnterBackstage() {
        return mIsEnterBackstage;
    }
}
