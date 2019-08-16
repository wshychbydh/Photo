# Photo
## Android7.0以上拍照适配

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
        implementation 'com.github.wshychbydh:photo:1.1.3'
    }
```

3、构建Params实例。
```
     val imageParams = ImageParams.Builder()
        .setOnSelectListener()          //图片选择回调（必填）
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
        .setDialogParams(dialogParams)         //对话框参数（可选）
        .setImageParams(imageParams)           //图片参数（可选）
        .setRationale(Rationale)               //自定义请求权限对话框（可选）
        .setRationaleSetting(rationaleSetting) //自定义引导授权对话框（可选）
        .build()
     
```

4、如果只需要调用拍照或选图片，可使用**PhotoHelper**，并按需调用onTakePhoto()或onSelectAlbum()方法
```
    helper.onTakePhoto(ImageParams)    //调用相册
    
    helper.onSelectAlbum(ImageParams)  //调用相机
```

5、弹框选择可使用**PhotoDialog**或**PhotoDialogFragment**(<font color=#FF0000>**推荐**</font>)，区别在于PhotoDialog必须设置onActivityResult回调

**注**：使用PhotoDialog时，需在相应的onActivityResult中设置如下回调：
```
    //在调用的Activity或Fragment中调用（必须设置）
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
      super.onActivityResult(requestCode, resultCode, data)
      dialog.onActivityResult(requestCode, data)
    }
```

6、若使用了**androidx**的包，可使用通用方法：**PhotoDialog**或**PhotoPickerDialog**

**注**：使用PhotoPickerDialog时无须设置onActivityResult回调，但DialogParams类的部分属性无效
```
   PhotoPickerDialog.setDialogParams(dialogParams)   //（可选）
   PhotoPickerDialog.setImageParams(imageParams)     // 必须设置setOnSelectListener方法，否则无回调
   PhotoPickerDialog.setRationale(rationale)         //（可选）
   PhotoPickerDialog.setRationaleSetting(rationale)  //（可选）
   PhotoPickerDialog.show(context)                   // 启动对话框，在设置完参数后调用
```

7、因为选择图片和拍照在6.0及以上需要运行时权限，该库已包含权限请求库，若有相关需求则无需再单独引入

[![](https://jitpack.io/v/wshychbydh/Photo.svg)](https://jitpack.io/#wshychbydh/Photo)
