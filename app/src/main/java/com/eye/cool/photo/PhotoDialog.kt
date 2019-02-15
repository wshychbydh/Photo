package com.eye.cool.photo

import android.app.Dialog
import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.support.annotation.StyleRes
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.eye.cool.permission.Rationale

/**
 * Created by cool on 18-3-9
 */
class PhotoDialog internal constructor(private val params: Params) : Dialog(params.wrapper.activity(), params.dialogStyle) {

  private var photoHelper: PhotoHelper = PhotoHelper(params)
  private var view: View? = null
  private var animStyle: Int = R.style.AnimBottom

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    photoHelper.setOnClickListener {
      dismiss()
    }
    if (view == null) {
      view = DefaultView(context)
      (view as DefaultView).setPhotoListener(photoHelper)
    } else {
      val method = view!!.javaClass.getDeclaredMethod("setPhotoListener", IPhotoListener::class.java)
          ?: throw IllegalArgumentException("Custom View must has public method setPhotoListener(IPhotoListener)")
      method.invoke(view, photoHelper)
    }
    setContentView(view!!)
    setParams()
  }

  private fun setParams() {
    val window = window ?: return
    window.setWindowAnimations(animStyle)
    val layoutParams = window.attributes
    layoutParams.x = params.xPos
    layoutParams.y = params.yPos
    onWindowAttributesChanged(layoutParams)
  }

  /**
   * 拍照后回调接口，在相应的OnActivityResult里面调用，必须手动调用
   */
  fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
    photoHelper.onActivityResult(requestCode, resultCode, intent)
  }

  class Builder {

    private lateinit var params: Params

    constructor(supportFragment: Fragment) {
      params.wrapper = ContextWrapper(supportFragment)
    }

    constructor(activity: AppCompatActivity) {
      params.wrapper = ContextWrapper(activity)
    }

    //弹框视图
    fun setContentView(view: View): Builder {
      params.contentView = view
      return this
    }

    //图片选择后回调,UI线程
    fun setOnPickedListener(listener: (fileUrl: String) -> Unit): Builder {
      params.onPickedListener = listener
      return this
    }

    //只是支持剪切，默认true
    fun cutAble(cutAble: Boolean): Builder {
      params.cutAble = cutAble
      return this
    }

    /**
     * 输出的图片大小，默认300,300
     */
    fun setOutput(outputW: Int, outputH: Int): Builder {
      params.outputW = outputW
      params.outputH = outputH
      return this
    }

    /**
     * 设置弹框显示的x，y坐标，默认从屏幕最底部
     */
    fun setCoordinate(x: Int, y: Int): Builder {
      params.xPos = x
      params.yPos = y
      return this
    }

    //弹出框动画样式，默认从底部弹出
    fun setAnimStyle(animStyle: Int): Builder {
      params.animStyle = animStyle
      return this
    }

    //弹出框样式
    fun setDialogStyle(dialogStyle: Int): Builder {
      params.dialogStyle = dialogStyle
      return this
    }

    //权限提示框
    fun rationale(rationale: Rationale): Builder {
      params.rationale = rationale
      return this
    }

    //引导设置授权提示框
    fun rationaleSetting(rationaleSetting: Rationale): Builder {
      params.rationaleSetting = rationaleSetting
      return this
    }

    fun build(): PhotoDialog {
      return PhotoDialog(params)
    }
  }

  class Params {
    lateinit var wrapper: ContextWrapper
    var contentView: View? = null
    var cutAble = true
    var onPickedListener: ((fileUrl: String) -> Unit)? = null
    @StyleRes
    var dialogStyle: Int = R.style.PhotoDialog
    @StyleRes
    var animStyle: Int = R.style.AnimBottom
    var outputW = 300
    var outputH = 300
    var xPos = 0
    var yPos = Resources.getSystem().displayMetrics.heightPixels
    var rationale: Rationale? = null
    var rationaleSetting: Rationale? = null
  }
}