# Photo
## 7.0拍照适配

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
            implementation 'com.github.wshychbydh:Photo:Tag'
    }
```

3、PhotoDialog.Builder创建实例。

4、设置相应参数,如设置回调setOnPickedListener()

5、在调用的Activity或Fragment中重写onActivityResult(必须)

#### 示例

```
    PhotoDialog.Builder(this)
        .setContentView()   //设置对话框视图（可以不设置）
        .cutAble(cutAble)   //是否剪切图片，默认true
        .setDialogStyle()   //设置对话框的样式（可以不设置）
        .setAnimStyle()     //设置对话框的动画样式（可以不设置）
        .setOutputXY()      //设置输出图片大小（可以不设置）
        .setCoordinate()    //设置对话框弹出的XY坐标（默认从底部）
        .setOnPickedListener { path-> //path为文件路径
            //当图片选择成功回调
        }.build()
        .show()

    //在调用的Activity或Fragment中调用（必须设置）
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
      super.onActivityResult(requestCode, resultCode, data)
      dialog.onActivityResult(requestCode, resultCode, data)
    }
```

[![](https://jitpack.io/v/wshychbydh/Photo.svg)](https://jitpack.io/#wshychbydh/Photo)
