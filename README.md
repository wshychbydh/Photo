# Photo
## 7.0拍照适配

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

    //在调用的相应Activity或Fragment中调用（必须设置）
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
      super.onActivityResult(requestCode, resultCode, data)
      dialog.onActivityResult(requestCode, resultCode, data)
    }
```

[![](https://jitpack.io/v/wshychbydh/Photo.svg)](https://jitpack.io/#wshychbydh/Photo)
