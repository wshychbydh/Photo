package com.eye.cool.photo.view

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.eye.cool.photo.IPhotoListener
import com.eye.cool.photo.PhotoHelper
import com.eye.cool.photo.params.Params

/**
 * Created by cool on 18-3-9
 */
class PhotoDialog(private val params: Params) : Dialog(params.wrapper.context(), params.dialogParams.dialogStyle) {

  private var photoHelper: PhotoHelper = PhotoHelper(params)

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    photoHelper.setOnClickListener {
      dismiss()
    }
    var view: View? = params.dialogParams.contentView
    if (view == null) {
      view = DefaultView(context)
      view.setPhotoListener(photoHelper)
    } else {
      val method = view.javaClass.getDeclaredMethod("setPhotoListener", IPhotoListener::class.java)
          ?: throw IllegalArgumentException("Custom View must has public method setPhotoListener(IPhotoListener)")
      method.invoke(view, photoHelper)
    }
    setContentView(view)
    setParams()
  }

  private fun setParams() {
    val window = window ?: return
    window.setWindowAnimations(params.dialogParams.animStyle)
    val layoutParams = window.attributes
    layoutParams.x = params.dialogParams.xPos
    layoutParams.y = params.dialogParams.yPos
    onWindowAttributesChanged(layoutParams)
  }

  /**
   * Call back the interface after taking a photo and call it in the corresponding OnActivityResult.
   */
  fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
    photoHelper.onActivityResult(requestCode, resultCode, intent)
  }
}