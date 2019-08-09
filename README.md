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
        implementation 'com.github.wshychbydh:photo:1.1.0'
    }
```

3、构建Params实例。
```
     val imageParams = ImageParams.Builder()
        .setOnSelectedListener()        //图片选择成功后回调
        .setOutput()                    //设置输出图片大小（可选）
        .cutAble()                      //是否剪切图片，默认true
        .build()
         
     val dialogParams = DialogParams.Builder()
        .setContentView()               //设置对话框视图（可选），自定义View必须拥有setPhotoListener(IPhotoListener)方法
        .setDialogStyle()               //设置对话框的样式（可选）
        .setAnimStyle()                 //设置对话框的动画样式（可选）
        .setCoordinate()                //设置对话框弹出的XY坐标，默认从底部弹出
        .build()
        
     val params = Params.Builder(this)
        .setDialogParams(dialogParams)  //对话框（可选）
        .setImageParams(imageParams)    //图片（可选）
        .rationale(Rationale)           //自定义请求权限对话框（可选）
        .rationale(rationaleSetting)    //引导授权对话框（可选）
        .build()
     
```

4、如果只需要调用拍照或选图片，可使用PhotoHelper，并按需调用onTakePhoto()或onSelectAlbum()方法
```
    helper.onTakePhoto()    //调用相册
    
    helper.onSelectAlbum()  //调用相机
```

5、弹框选择可使用PhotoDialog 或 PhotoDialogFragment，区别在于PhotoDialogFragment无需设置回调

**注**：使用PhotoDialog或PhotoHelper时，需在相应的onActivityResult中设置如下回调：
```
    //在调用的Activity或Fragment中调用（必须设置）
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
      super.onActivityResult(requestCode, resultCode, data)
      dialog.onActivityResult(requestCode, resultCode, data)
    }
```

6、因为选择图片和拍照在6.0及以上需要运行时权限，该库已包含权限请求库，若有相关需求则无需再单独引入

[![](https://jitpack.io/v/wshychbydh/Photo.svg)](https://jitpack.io/#wshychbydh/Photo)
