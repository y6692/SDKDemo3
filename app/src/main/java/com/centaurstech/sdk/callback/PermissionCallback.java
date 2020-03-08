package com.centaurstech.sdk.callback;

import java.util.List;

/**
 * @author Leon(黄长亮)
 * @describe 权限回调
 * @date 2018/7/16
 */

public interface PermissionCallback {

    void onGranted(); // 用户全部同意回调
    void onDenied(List<String> permissions); // 用户拒绝或同意一部分回调

}
