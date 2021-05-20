# Android图片选取/拍照适配

解决6.0及7.0以上拍照，兼容6.0以下权限适配，兼容11.0强制分区


### 功能介绍：

1、适配target23以下及以上运行时权限

2、适配7.0以上文件访问权限

3、提供拍照和选择图片功能调用

4、支持自定义选择弹框和权限申请

5、支持自定义临时授权FileProvider

6、图片剪切支持11.0强制分区


#### 使用方法：

1、在root目录的build.gradle目录中添加
```groovy
    allprojects {
        repositories {
            maven { url 'https://jitpack.io' }
        }
    }
```


2、在项目的build.gradle中添加依赖
```groovy
    dependencies {
        implementation 'com.github.wshychbydh:photo:Tag'
    }
```

**注**：如果编译的时候报重复的'META-INF/app_release.kotlin_module'时，在app的build.gradle文件的android下添加
```groovy
    packagingOptions {
        exclude 'META-INF/app_release.kotlin_module'
    }
```
报其他类似的重复错误时，添加方式同上。


3、调用系统相机不需要Camera权限，请检查项目中是配置有**android.permission.CAMERA**权限，若存在请删除或设置requestCameraPermission为true（必须）


4、构建Params实例
```kotlin
     //kotlin使用 val imageParams = ImageParams.build{} （推荐）
     val imageParams = ImageParams.Builder()
        .output()                    //设置输出图片大小，默认300x300（可选）
        .cutAble()                   //是否剪切图片，默认true（可选）
        .build()

     //kotlin使用 val dialogParams = DialogParams.build{} （推荐）
     val dialogParams = DialogParams.Builder()
        .contentView()               //自定义对话框视图（可选），注：自定义View必须拥有onActionClickListener(OnActionClickListener)方法
        .contentLayoutId()           //自定义布局（可选），注：布局中需对view设置对应的Tag: @{album，photo，cancel}，如android:tag="album"
        .themeStyle()                //自定义对话框的样式, 默认R.style.photo_dialog（可选）
        .windowParams()              //弹框窗体设置（可选）
        .build()

     //kotlin使用 val params = Params.build{} （推荐）
     val params = Params.Builder()
        .onSelectListener()                   //图片选择后回调（必填）
        .dialogParams(dialogParams)           //对话框参数（可选）
        .imageParams(imageParams)             //图片参数（可选）
        .permissionInvoker(PermissionInvoker) //自定义请求权限（可选）
        .requestCameraPermission(false)       //是否请求相机权限（默认false），若Manifest中配置了Camera权限，需设置为true
        .authority(String)                    //自定义的FileProvider，默认授权external目录
        .onActionListener()                   //触发行为回调@link{Action#TAKE_PHOTO | SELECT_ALBUM | CANCEL | PERMISSION_DENIED}（可选）
        .build()
     
```


5、若只需要调用拍照或选图片，可使用**PhotoHelper**，按需调用onTakePhoto()或onSelectAlbum()方法，如：

```kotlin
    helper.onTakePhoto(OnSelectListener)     //调用相册    
    
    helper.onSelectAlbum(OnSelectListener)   //调用相机
```


6、需要弹框可使用**PhotoDialog**(<font color=#FF0000>**推荐**</font>)或**PhotoDialogFragment**或**PhotoDialogActivity**
   
   1）PhotoDialog对应的包为androidx.appcompat.app.AppCompatDialogFragment
   
   2）PhotoDialogFragment对应的包为android.app.DialogFragment（Deprecated）（不推荐）

   3）在**android.support.fragment**等其他环境中可调用**PhotoDialogActivity**（不推荐）

```kotlin
   
   //推荐
   PhotoDialog.create(onSelectListener)      //默认参数，简单调用
   PhotoDialog.create(params)                //自定义的参数

   //不推荐
   PhotoDialogActivity.show(context, params) //启动对话框，参数可选
```

7、支持在协程中调用
```kotlin
    launch {
      val result = select(context)      //选择拍照or相册
    }

    launch {
      val result = selectAlbum(context) //相册
    }

    launch {
      val result = takePhoto(context)   //拍照
    }
```

8、其他注意事项

   1）因为选择图片和拍照在6.0及以上需要运行时权限，该库包含默认权限请求，无需额外添加

   2）切记检查Manifest是否配置了Camera权限，并做相应权限请求，否则会调用相机失败
   
   3）为了适配7.0以上文件权限，仅添加了external-path中的Pictures目录的临时权限，若所需其他目录的临时权限则需自行添加
   
#####   
 
**Demo地址：(https://github.com/wshychbydh/SampleDemo)**    
    
##

###### **欢迎fork，期待你的宝贵意见.** (*￣︶￣)

###### 联系方式 wshychbydh@gmail.com

[![](https://jitpack.io/v/wshychbydh/photo.svg)](https://jitpack.io/#wshychbydh/photo)
