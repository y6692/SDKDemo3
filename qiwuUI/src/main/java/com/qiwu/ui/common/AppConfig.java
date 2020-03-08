package com.qiwu.ui.common;

import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;

/**
 * App配置中心
 */

public class AppConfig {


    private static Application application;
    private static String environmen;

    public static void init(Application application){
        AppConfig.application = application;
    }

    public static Context getContext(){
        return application;
    }

    public static Application getApplication() {
        return application;
    }

    public static boolean isDebuggable(){
        try {
            ApplicationInfo info = application.getApplicationInfo();
            return (info.flags & ApplicationInfo.FLAG_DEBUGGABLE) !=0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}
