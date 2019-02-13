package com.eye.cool.photo

import android.app.Dialog
import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.annotation.StyleRes
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.View

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

    fun setContentView(@LayoutRes layoutResID: Int): Builder {
      params.layoutResID = layoutResID
      return this
    }

    fun setContentView(view: View): Builder {
      params.contentView = view
      return this
    }

    fun setOnPickedListener(listener: (fileUrl: String) -> Unit): Builder {
      params.onPickedListener = listener
      return this
    }

    fun cutAble(cutAble: Boolean): Builder {
      params.cutAble = cutAble
      return this
    }

    /**
     * 输出的图片大小
     */
    fun setOutputXY(outputX: Int, outputY: Int): Builder {
      params.outputX = outputX
      params.outputY = outputY
      return this
    }

    /**
     * 设置弹框显示的x，y坐标
     */
    fun setCoordinate(x: Int, y: Int): Builder {
      params.xPos = x
      params.yPos = y
      return this
    }

    fun setAnimStyle(animStyle: Int): Builder {
      params.animStyle = animStyle
      return this
    }

    fun setDialogStyle(dialogStyle: Int): Builder {
      params.dialogStyle = dialogStyle
      return this
    }

    fun build(): PhotoDialog {
      return PhotoDialog(params)
    }
  }

  class Params {
    lateinit var wrapper: ContextWrapper
    var contentView: View? = null
    @LayoutRes
    var layoutResID: Int = -1
    var cutAble = true
    var onPickedListener: ((fileUrl: String) -> Unit)? = null
    @StyleRes
    var dialogStyle: Int = R.style.PhotoDialog
    @StyleRes
    var animStyle: Int = R.style.AnimBottom
    var outputX = 300
    var outputY = 300
    var xPos = 0
    var yPos = Resources.getSystem().displayMetrics.heightPixels
  }
}