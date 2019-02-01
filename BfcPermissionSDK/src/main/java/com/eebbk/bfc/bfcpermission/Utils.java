package com.eebbk.bfc.bfcpermission;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

/**
 * Created by Simon on 2017/3/31.
 */

class Utils{

    private Utils(){

    }

    /**
     * Check if the calling context has a set of permissions.
     *
     * @param context the calling context.
     * @param perms   one ore more permissions
     *
     * @return true if all permissions are already granted, false if at least one permission is not
     * yet granted.
     */
    static boolean hasPermissions(@NonNull Context context, @NonNull String... perms){
        // Always return true for SDK < M, let the system deal with the permissions
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M){
            return true;
        }

        for(String perm : perms){
            boolean hasPerm = (ContextCompat.checkSelfPermission(context, perm) == PackageManager.PERMISSION_GRANTED);
            if(!hasPerm){
                return false;
            }
        }

        return true;
    }

    /**
     * should show permission rationale
     */
    static boolean shouldShowRequestPermissionRationale(@NonNull Object o, @NonNull String... permissions){
        // Always return true for SDK < M,
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M){
            return false;
        }

        for(String permission : permissions){
            if(o instanceof Activity){
                return ActivityCompat.shouldShowRequestPermissionRationale((Activity) o, permission);
            }else if(o instanceof android.support.v4.app.Fragment){
                return ((android.support.v4.app.Fragment) o).shouldShowRequestPermissionRationale(permission);
            }else if(o instanceof android.app.Fragment){
                return ((android.app.Fragment) o).shouldShowRequestPermissionRationale(permission);
            }
        }

        return false;
    }

    /**
     * 是否被用户禁止权限, 并且不在允许权限弹出
     *
     * @param obj
     * @param permission
     *
     * @return
     */
    static boolean hasAlwaysDeniedPermission(Object obj, String permission){
        return !shouldShowRequestPermissionRationale(obj, permission);
    }
}
