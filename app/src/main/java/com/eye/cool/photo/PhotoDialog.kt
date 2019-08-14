package com.eye.cool.photo

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.View
import com.eye.cool.photo.params.ImageParams
import com.eye.cool.photo.params.Params
import com.eye.cool.photo.support.IClickListener
import com.eye.cool.photo.support.IPhotoListener
import com.eye.cool.photo.support.ISelectListener
import com.eye.cool.photo.utils.PhotoExecutor
import com.eye.cool.photo.view.DefaultView

/**
 * Created by cool on 18-3-9
 */
class PhotoDialog : Dialog {

  private var params: Params
  private var executor: PhotoExecutor

  constructor(activity: Activity, onSelectListener: ISelectListener) : super(activity, R.style.PhotoDialog) {
    params = Params.Builder(activity)
        .setImageParams(ImageParams.Builder()
            .setOnSelectedListener(onSelectListener)
            .build())
        .build()
    executor = PhotoExecutor(params)
  }

  constructor(fragment: Fragment, onSelectListener: ISelectListener) : super(fragment.context, R.style.PhotoDialog) {
    params = Params.Builder(fragment)
        .setImageParams(ImageParams.Builder()
            .setOnSelectedListener(onSelectListener)
            .build())
        .build()
    executor = PhotoExecutor(params)
  }

  constructor(params: Params) : super(params.wrapper.context(), params.dialogParams.dialogStyle) {
    this.params = params
    executor = PhotoExecutor(params)
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    var view: View? = params.dialogParams.contentView
    if (view == null) {
      view = DefaultView(context)
      view.setPhotoListener(executor)
    } else {
      val method = view.javaClass.getDeclaredMethod("setPhotoListener", IPhotoListener::class.java)
          ?: throw IllegalArgumentException("Custom View must has public method setPhotoListener(IPhotoListener)")
      method.invoke(view, executor)
    }
    setContentView(view)
    setParams()

    executor.setOnClickListener(object : IClickListener {
      override fun onClicked(which: Int) {
        dismiss()
        params.dialogParams.onClickListener?.onClicked(which)
      }
    })
  }

  private fun setParams() {
    val dialogParams = params.dialogParams
    setCancelable(dialogParams.cancelable)
    setCanceledOnTouchOutside(dialogParams.canceledOnTouchOutside)
    setOnCancelListener(dialogParams.onCancelListener)
    setOnDismissListener(dialogParams.onDismissListener)
    setOnShowListener(dialogParams.onShownListener)

    val window = window ?: return
    window.setWindowAnimations(dialogParams.animStyle)
    val layoutParams = window.attributes
    layoutParams.x = dialogParams.xPos
    layoutParams.y = dialogParams.yPos
    onWindowAttributesChanged(layoutParams)
  }

  /**
   * Call back the interface after taking a photo and call it in the corresponding OnActivityResult.
   */
  fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
    if (resultCode == Activity.RESULT_OK) {
      executor.onActivityResult(requestCode, intent)
    } else if (resultCode == Activity.RESULT_CANCELED) {
      dismiss()
    }
  }
}