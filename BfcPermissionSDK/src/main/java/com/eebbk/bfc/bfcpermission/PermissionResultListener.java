package com.eebbk.bfc.bfcpermission;

import java.util.List;

/**
 * 权限请求的结果处理
 * <p>
 * Created by Simon on 2017/3/31.
 */

public interface PermissionResultListener{

    /**
     * 权限获取成功
     *
     * @param requestCode
     */
    void onPermissionsGranted(int requestCode);

    /**
     * 权限获取失败
     *
     * @param requestCode
     * @param list
     * @param neverAskAgain 用户是否勾选了不在允许请求权限弹窗; 一旦为 true, 说明不在可能请求权限弹窗, 应该引导用户去设置里面开启权限
     */
    void onPermissionsDenied(int requestCode, List<String> list, boolean neverAskAgain);
}
