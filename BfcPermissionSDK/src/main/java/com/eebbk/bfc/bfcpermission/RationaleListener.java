package com.eebbk.bfc.bfcpermission;

/**
 * 请求权限, 用于添加自己提示的回调
 * <p>
 * Created by Simon on 2017/3/31.
 */

public interface RationaleListener{

    /**
     * 回调,
     * 可以添加自己的Dialog之类的, 提示用户为什么要请求权限;
     * <p>
     * 需要调用 {@code {@link PermissionRequest#process()}} 继续请求权限 或者
     * 调用 {@code {@link PermissionRequest#cancel()} ()}} 取消请求权限
     */
    void onShowRationale(PermissionRequest request);
}
