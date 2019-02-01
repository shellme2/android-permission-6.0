package com.eebbk.bfc.bfcpermission;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.eebbk.bfc.bfcpermission.version.SDKVersion;

import java.util.ArrayList;
import java.util.List;

/**
 * 权限请求的实际操作类
 * <p>
 * Created by Simon on 2017/3/31.
 */
public class PermissionRequest{
    private Object mRequestObject;
    private int mRequestCode;
    private String[] mRequestPermissions;
    private RationaleListener mRationalListener;

    // 要请求的权限中, 还未获取权限的列表
    private String[] mUnauthorizedPermissions;

    /**
     * @param activity Activity或者fragment实例
     */
    PermissionRequest(Object activity){
        mRequestObject = activity;
        Log.i(SDKVersion.getLibraryName(), " " + SDKVersion.getLibraryName() + " init, version: " +
                SDKVersion.getVersionName() + "  code: " + SDKVersion.getSDKInt() + " build: " + SDKVersion.getBuildName());
    }

    private Context getContext(){
        if(mRequestObject instanceof Activity){
            return (Activity) mRequestObject;
        }else if(mRequestObject instanceof android.support.v4.app.Fragment){
            return ((android.support.v4.app.Fragment) mRequestObject).getActivity();
        }else if(mRequestObject instanceof android.app.Fragment){
            return ((android.app.Fragment) mRequestObject).getActivity();
        }else{
            throw new IllegalArgumentException("The " + mRequestObject.getClass().getName() + " is not support.");
        }
    }

    public PermissionRequest requestCode(int requestCode){
        mRequestCode = requestCode;

        return this;
    }

    public PermissionRequest permission(String... permissions){
        if(permissions == null){
            throw new IllegalArgumentException("The request permission can't be null");
        }

        mRequestPermissions = permissions;

        return this;
    }

    /**
     * 已经弹出过一次, 再次弹出时. 用户可以勾选不再弹出的
     * <p>
     * 设置展示向用户说明为什么要请求权限的回调
     *
     * @param listener
     *
     * @return
     */
    public PermissionRequest rationale(RationaleListener listener){
        mRationalListener = listener;

        return this;
    }

    /**
     * 请求权限
     */
    public void request(){
        final Context context = getContext();
        // SDK < M; call the onRequestPermissionsResult method;
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M){
            final int[] grantResult = new int[mRequestPermissions.length];
            final PackageManager packageManager = context.getPackageManager();
            final String packageName = context.getPackageName();
            for(int i = 0, z = mRequestPermissions.length; i < z; i++){
                grantResult[i] = packageManager.checkPermission(mRequestPermissions[i], packageName);
            }

            invokeOnRequestPermissionsResult(mRequestObject, mRequestCode, mRequestPermissions, grantResult);
            return;
        }

        List<String> deniedPermissionList = getDeniedPermissions(context, mRequestPermissions);
        // 如果已经全部获取, 则直接回调成功
        if(deniedPermissionList.size() == 0){
            final int[] grantResult = new int[mRequestPermissions.length];
            for(int i = 0, z = mRequestPermissions.length; i < z; i++){
                grantResult[i] = PackageManager.PERMISSION_GRANTED;
            }

            invokeOnRequestPermissionsResult(mRequestObject, mRequestCode, mRequestPermissions, grantResult);
            return;
        }

        mUnauthorizedPermissions = deniedPermissionList.toArray(new String[]{});
        if(mRationalListener != null && Utils.shouldShowRequestPermissionRationale(mRequestObject, mUnauthorizedPermissions)){
            mRationalListener.onShowRationale(this);
        }else{
            reallyRequestPermission();
        }
    }

    /**
     * 获取还没有授权的权限
     *
     * @param context
     * @param permissions 需要请求的所有权限
     *
     * @return 还未获取的权限
     */
    private List<String> getDeniedPermissions(Context context, String[] permissions){
        List<String> deniedPermissions = new ArrayList<>();
        for(String permission : permissions){
            if(!Utils.hasPermissions(context, permission)){
                deniedPermissions.add(permission);
            }
        }

        return deniedPermissions;
    }


    /**
     * 直接调用onRequestPermissionsResult方法, 统一 M 一下系统版本的风格
     */
    private void invokeOnRequestPermissionsResult(Object object, int requestCode, String[] permissions, int[] grantResults){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(object instanceof android.app.Activity){
                ((Activity) object).onRequestPermissionsResult(requestCode, permissions, grantResults);
                return;
            }else if(object instanceof android.app.Fragment){
                ((android.app.Fragment) object).onRequestPermissionsResult(requestCode, permissions, grantResults);
                return;
            }
        }

        if(object instanceof ActivityCompat.OnRequestPermissionsResultCallback){
            ((ActivityCompat.OnRequestPermissionsResultCallback) object).onRequestPermissionsResult(requestCode, permissions, grantResults);
        }else if(object instanceof android.support.v4.app.Fragment){
            ((android.support.v4.app.Fragment) object).onRequestPermissionsResult(requestCode, permissions,
                    grantResults);
        }
    }


    /**
     * 真正请求权限的方法
     */
    @TargetApi(Build.VERSION_CODES.M)
    private void reallyRequestPermission(){
        if(mRequestObject instanceof Activity)
            ActivityCompat.requestPermissions(((Activity) mRequestObject), mUnauthorizedPermissions, mRequestCode);
        else if(mRequestObject instanceof android.support.v4.app.Fragment)
            ((android.support.v4.app.Fragment) mRequestObject).requestPermissions(mUnauthorizedPermissions, mRequestCode);
        else if(mRequestObject instanceof android.app.Fragment){
            ((android.app.Fragment) mRequestObject).requestPermissions(mUnauthorizedPermissions, mRequestCode);
        }
    }

    /**
     * 用于 rationale 的回调中
     * 继续请求权限
     */
    public void process(){
        reallyRequestPermission();
    }

    /**
     * 用于 rationale 的回调中
     * 取消请求权限
     */
    public void cancel(){
        mRationalListener = null;
    }
}
