权限请求使用说明
------
## 6.0以后版本上的权限管理
6.0之后的权限, 分为普通权限和危险权限; 使用危险权限时, 不仅要在Manifest中申请, 还要请求权限申请弹窗, 让用户进行授权;

- 危险的权限列表如下:

		group:android.permission-group.CONTACTS
		  permission:android.permission.WRITE_CONTACTS
		  permission:android.permission.GET_ACCOUNTS
		  permission:android.permission.READ_CONTACTS
		
		group:android.permission-group.PHONE
		  permission:android.permission.READ_CALL_LOG
		  permission:android.permission.READ_PHONE_STATE
		  permission:android.permission.CALL_PHONE
		  permission:android.permission.WRITE_CALL_LOG
		  permission:android.permission.USE_SIP
		  permission:android.permission.PROCESS_OUTGOING_CALLS
		  permission:com.android.voicemail.permission.ADD_VOICEMAIL
		
		group:android.permission-group.CALENDAR
		  permission:android.permission.READ_CALENDAR
		  permission:android.permission.WRITE_CALENDAR
		
		group:android.permission-group.CAMERA
		  permission:android.permission.CAMERA
		
		group:android.permission-group.SENSORS
		  permission:android.permission.BODY_SENSORS
		
		group:android.permission-group.LOCATION
		  permission:android.permission.ACCESS_FINE_LOCATION
		  permission:android.permission.ACCESS_COARSE_LOCATION
		
		group:android.permission-group.STORAGE
		  permission:android.permission.READ_EXTERNAL_STORAGE
		  permission:android.permission.WRITE_EXTERNAL_STORAGE
		
		group:android.permission-group.MICROPHONE
		  permission:android.permission.RECORD_AUDIO
		
		group:android.permission-group.SMS
		  permission:android.permission.READ_SMS
		  permission:android.permission.RECEIVE_WAP_PUSH
		  permission:android.permission.RECEIVE_MMS
		  permission:android.permission.RECEIVE_SMS
		  permission:android.permission.SEND_SMS
		  permission:android.permission.READ_CELL_BROADCASTS

> 权限是一组一组的, 权限授权机制也是按组来授权的;
> 如果申请一条权限, 用户同意授权, App将获得整组权限; 如果App申请某个危险权限, 而用户已经授权了同一组的某个权限, 那系统会立即授权, 不再弹出授权Dialog    
> 申请授权弹出的Dialog是针对整组权限的说明, App不可定制

- 应用的危险权限查看方法：

&emsp;&emsp;从Android 6.0开始，应用对于危险权限的使用需要得到用户允许，危险权限的申请分为一次性申请多个权限，或者在使用某个权限的时候申请。
imoo手机属于一次性申请，通过应用首次启动弹出的权限声明来提示用户，但有的应用在权限声明中并没有写全应用使用到的所有危险权限。
所以可以通过这个工具来查看你的应用使用到的权限中哪些属于危险权限，查看方法如下：

&emsp;&emsp;1、通过adb安装doc/AppInfos-debug.apk；

&emsp;&emsp;2、启动应用，点击单个应用的'Uses permissions'选项查看，黄底色的权限为敏感权限

## 关于

- 版本: V3.0.0


## 前置条件

- API>=15
- BuildTool >= 23

## 功能列表
参考使用说明


## 项目依赖
无


## 使用说明

### 1.添加依赖
	compile bfcBuildConfig.deps.'bfc-permission'

> 如果依赖使用的[网络配置](http://172.28.2.93/bfc/Bfc/blob/develop/public-config/%E4%BE%9D%E8%B5%96%E4%BD%BF%E7%94%A8%E8%AF%B4%E6%98%8E.md), 请参考网络配置使用; 添加`compile bfcBuildConfig.deps.'bfc-permission'`

### 2.使用
1. 在Activity或者Fragment中请求权限

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

2. 在`onRequestPermissionsResult`方法中, 将权限请求结果回调给库, 并实现自己的回调

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


### 3. 接口使用说明

#### BfcPermission
用于构建权限请求, 和分发权限请求的结果
- `with` 构建权限请求
- `onRequestPermissionsResult` 分发权限请求结果,并调用结果的回调

#### PermissionRequest
权限请求的业务类, 用`BfcPermission.with()`构建; 
- `requestCode()` 设置请求的id, ***必须设置*	
- `permission(String... permissions)` 设置请求的权限, 可以设置多个权限; **必须设置**
- `rationale` 设置RationaleListener, 具体参考下面的`RationaleListener`
- `request` 请求权限

#### PermissionResultListener
权限请求结果的回调接口, 包含2个方法;
- `PermissionResultListener`在获取权限成功时回调; 
- `onPermissionsDenied`在获取权限失败时回调; 当不在允许弹出权限弹窗时, neverAskAgain 返回值将为true;  

#### RationaleListener
用于添加自己提示的回调,在用户拒接过一次后,再次请求权限时, 添加App请求权限的说明回调; 
- `onShowRationale` 在App请求一次权限,被用户拒接后, 再次请求权限会回调; 用于添加App的逻辑, 告知用户为什么要请求权限


> 在部分国产的Rom中(eg.小米),  RationaleListener可能永远无法回调, 因为有的Rom一旦用户拒绝后, 在就无法弹出权限请求框, 只能引导用户去设置中打开;   
> 当权限被拒绝, 并且权限请求Dialog再无法弹出时, `onPermissionsDenied`方法的neverAskAgain将为true; 一般此时就需要添加自己的流程去引导用户打开权限