package com.eebbk.bfc.bfcpermission;

import android.content.Context;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Simon on 2017/3/31.
 */
public class BfcPermission{

    public static PermissionRequest with(@NonNull android.app.Activity activity){
        return new PermissionRequest(activity);
    }

    public static PermissionRequest with(@NonNull android.app.Fragment fragment){
        return new PermissionRequest(fragment);
    }

    public static PermissionRequest with(@NonNull android.support.v4.app.Fragment fragment){
        return new PermissionRequest(fragment);
    }

    /**
     * 检查是否有权限
     *
     * @param context
     * @param perms
     *
     * @return
     */
    public static boolean hasPermissions(@NonNull Context context, @NonNull String... perms){
        return Utils.hasPermissions(context, perms);
    }

    /**
     * 获取权限是否未获取, 并且不允许权限弹窗
     *
     * @return true 有权限不被允许, 并且不在允许权限弹窗; 此时一般需要引导用户去设置手动打开权限
     */
    private static boolean hasAlwaysDeniedPermission(@NonNull android.app.Activity activity, @NonNull String[] permissions){
        return hasAlwaysDeniedPermissionObj(activity, permissions);
    }

    /**
     * 获取权限是否未获取, 并且不允许权限弹窗
     *
     * @return true 有权限不被允许, 并且不在允许权限弹窗; 此时一般需要引导用户去设置手动打开权限
     */
    private static boolean hasAlwaysDeniedPermission(@NonNull android.app.Fragment fragment, @NonNull String[] permissions){
        return hasAlwaysDeniedPermissionObj(fragment, permissions);
    }

    /**
     * 暂时没看见需求, 不对外开放
     * <p>
     * 获取权限是否未获取, 并且不允许权限弹窗
     *
     * @return true 有权限不被允许, 并且不在允许权限弹窗; 此时一般需要引导用户去设置手动打开权限
     */
    private static boolean hasAlwaysDeniedPermission(@NonNull android.support.v4.app.Fragment fragment, @NonNull String[] permissions){
        return hasAlwaysDeniedPermissionObj(fragment, permissions);
    }

    private static boolean hasAlwaysDeniedPermissionObj(Object obj, @NonNull String[] permissions){
        for(String permission : permissions){
            if(Utils.hasAlwaysDeniedPermission(obj, permission)){
                return true;
            }
        }

        return false;
    }

    /**
     * 权限请求的回调
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     * @param activity     当前activity
     * @param listener
     */
    public static void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[]
            grantResults, android.app.Activity activity, PermissionResultListener listener){
        onRequestPermissionsResultObj(requestCode, permissions, grantResults, activity, listener);
    }

    /**
     * 权限请求的回调
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     * @param fragment     当前fragment
     * @param listener
     */
    public static void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[]
            grantResults, android.app.Fragment fragment, PermissionResultListener listener){
        onRequestPermissionsResultObj(requestCode, permissions, grantResults, fragment, listener);
    }

    /**
     * 权限请求的回调
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     * @param fragment     当前fragment
     * @param listener
     */
    public static void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[]
            grantResults, android.support.v4.app.Fragment fragment, PermissionResultListener listener){
        onRequestPermissionsResultObj(requestCode, permissions, grantResults, fragment, listener);
    }

    private static void onRequestPermissionsResultObj(int requestCode, @NonNull String[] permissions, @NonNull int[]
            grantResults, Object obj, PermissionResultListener listener){
        if(listener == null){
            return;
        }

        List<String> deniedList = new ArrayList<>();
        for(int i = 0, z = permissions.length; i < z; i++){
            if(grantResults[i] == PackageManager.PERMISSION_DENIED){
                deniedList.add(permissions[i]);
            }
        }

        if(deniedList.size() == 0){
            listener.onPermissionsGranted(requestCode);
        }else{
            boolean neverAskAgain = hasAlwaysDeniedPermissionObj(obj, deniedList.toArray(new String[]{}));
            listener.onPermissionsDenied(requestCode, deniedList, neverAskAgain);
        }
    }
}
