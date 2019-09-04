# Photo
## Android拍照适配
解决7.0及以上拍照，兼容6.0以下权限适配

#### 使用方法：

1、在root目录的build.gradle目录中添加
```
    allprojects {
        repositories {
            ...
            maven { url 'https://jitpack.io' }
        }
    }
```

2、在项目的build.gradle中添加依赖
```
    dependencies {
        implementation 'com.github.wshychbydh:photo:1.2.1'
    }
```

3、调用系统相机不需要Camera权限，请检查项目中是否存在**android.permission.CAMERA**权限，若存在请删除或设置requestCameraPermission为true（必须）

4、构建Params实例
```
     val imageParams = ImageParams.Builder()
        .setOnSelectListener()          //图片选择后回调（必填）
        .setOutput()                    //设置输出图片大小（可选）
        .setCutAble()                   //是否剪切图片，默认true（可选）
        .build()
         
     val dialogParams = DialogParams.Builder()
        .setContentView()               //自定义对话框视图（可选），注：自定义View必须拥有setOnActionListener(OnActionListener)方法
        .setDialogStyle()               //自定义对话框的样式（可选）
        .setAnimStyle()                 //自定义对话框的动画样式（可选）
        .setCoordinate()                //设置对话框弹出的XY坐标，默认从底部弹出（可选）
        .setCancelable()                //同dialog的setCancelable（可选）
        .setCanceledOnTouchOutside()    //同dialog的setCanceledOnTouchOutside（可选）
        .setOnCancelListener()          //同dialog的OnCancelListener（可选）
        .setOnDismissListener()         //同dialog的setOnDismissListener（可选）
        .setOnShowListener()            //同dialog的setOnShownListener（可选）
        .setOnClickListener()           //按钮点击时回调，回调@link{Constants#TAKE_PHOTO | SELECT_ALBUM | CANCEL | PERMISSION_FORBID}（可选）
        .build()
        
     val params = Params.Builder(this)
        .setDialogParams(dialogParams)                  //对话框参数（可选）
        .setImageParams(imageParams)                    //图片参数（可选）
        .setRationale(Rationale, Boolean)               //自定义请求权限对话框（可选）
        .setRationaleSetting(rationaleSetting, Boolean) //自定义引导授权对话框（可选）
        .requestCameraPermission(false)                 //是否请求相机权限（默认false），若Manifest中配置了Camera权限，则必须主动设置为true
        .build()
     
```

5、如果只需要调用拍照或选图片，可使用**PhotoHelper**，并按需调用onTakePhoto()或onSelectAlbum()方法
```
    helper.onTakePhoto(ImageParams, requestCameraPermission)    //调用相册
    
    helper.onSelectAlbum(ImageParams)  //调用相机
```

6、需要弹框可使用**PhotoDialog**或**PhotoDialogFragment**(<font color=#FF0000>**推荐**</font>)，区别在于PhotoDialog必须设置onActivityResult回调

**注**：
   
   1）PhotoDialogFragment和support.v4.PhotoDialogFragment分别对应不同包的Fragment，按需调用
   
   2）使用PhotoDialog时，需在相应的onActivityResult中设置如下回调：
```
    //在调用的Activity或Fragment中调用（必须设置）
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
      super.onActivityResult(requestCode, resultCode, data)
      dialog.onActivityResult(requestCode, data)
    }
```

7、若使用了其他包，如**androidx**时，可使用**通用**方式：**PhotoDialog**或**PhotoPickerDialog**

**注**：使用PhotoPickerDialog时无须设置onActivityResult回调，但DialogParams类的部分属性无效
```
   PhotoPickerDialog.setDialogParams(dialogParams)            //（可选）
   PhotoPickerDialog.setImageParams(imageParams)              // 必须设置setOnSelectListener方法，否则无回调
   PhotoPickerDialog.setRationale(rationale, boolean)         // 自定义请求权限对话框（可选）
   PhotoPickerDialog.setRationaleSetting(rationale, boolean)  // 自定义引导授权对话框（可选）
   PhotoPickerDialog.requestCameraPermission(boolean)         // 是否请求相机权限（默认false），若Manifest中配置了Camera权限，则必须主动设置为true
   PhotoPickerDialog.show(context)                            // 启动对话框，在设置完参数后调用
```

8、因为选择图片和拍照在6.0及以上需要运行时权限，该库已包含权限请求库，若有相关需求则无需再单独引入

9、其他注意事项

**注**：

   1）部分机型如模拟器在剪切图片后回调异常，导致程序无法达到预期效果，可尝试禁用剪切（设置ImageParams的setCutAble为false）修复

   2）切记检查Manifest是否配置了Camera权限，并做相应权限请求，否则可能会出现调用相机crash
    
#### 联系方式 wshychbydh@gmail.com


[![](https://jitpack.io/v/wshychbydh/photo.svg)](https://jitpack.io/#wshychbydh/photo)
