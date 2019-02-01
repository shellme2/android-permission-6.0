package com.eebbk.bfc.demo.permission;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import com.eebbk.bfc.bfcpermission.BfcPermission;
import com.eebbk.bfc.bfcpermission.PermissionResultListener;
import com.eebbk.bfc.common.app.AppUtils;
import com.eebbk.bfc.common.app.ToastUtils;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_REQUEST_PERMISSION = 100;
    private static final int REQUEST_CODE_REQUEST_PERMISSION_WITH_RATIONAL = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // 将请求结果分发给回调
        BfcPermission.onRequestPermissionsResult(requestCode, permissions, grantResults, this,
                new PermissionResultListener() {
                    @Override
                    public void onPermissionsGranted(int requestCode) {
                        ToastUtils.getInstance(MainActivity.this).s("请求权限成功");
                    }

                    @Override
                    public void onPermissionsDenied(int requestCode, List<String> list, boolean neverAskAgain) {
                        ToastUtils.getInstance(MainActivity.this).s("请求权限失败 neverAskAgain:" + neverAskAgain);

                        // 无法弹出权限请求的Dialog了  一般需要添加自己的逻辑
                        if (neverAskAgain) {
                            new AlertDialog.Builder(MainActivity.this)
                                    .setMessage("权限请求弹窗已经无法打开, 请去设置里面开启权限")
                                    .setPositiveButton("去设置", (dialog, which) -> AppUtils.openAppInfoSettings(MainActivity.this))
                                    .setNegativeButton("取消", (dialog, which) -> dialog.dismiss())
                                    .show();
                        }
                    }
                });
    }

    @OnClick(R.id.btn_request_permission)
    public void requestPermission() {
        BfcPermission.with(this)
                .permission(Manifest.permission.WRITE_CALENDAR)
                .requestCode(REQUEST_CODE_REQUEST_PERMISSION)
                .request();
    }

    @OnClick(R.id.btn_request_permission_with_rational)
    public void requestPermissionWithRational() {
        BfcPermission.with(this)
                // requestCode 和 permission 必须设置
                .permission(Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_EXTERNAL_STORAGE)
                .requestCode(REQUEST_CODE_REQUEST_PERMISSION_WITH_RATIONAL)
                .rationale(request -> {
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle("请求权限")
                            .setMessage("需要请求权限才能让程序正常运行, 请求权限弹窗弹出时, 请选择允许;")
                            .setCancelable(false)
                            .setPositiveButton("继续请求", (dialog, which) -> request.process())
                            .setNegativeButton("取消", (dialog, which) -> request.cancel())
                            .setOnCancelListener(dialog -> request.cancel())
                            .show();
                })
                .request();
    }
}
