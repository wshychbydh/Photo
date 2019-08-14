package com.eye.cool.photo

import android.content.DialogInterface
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.eye.cool.photo.params.DialogParams
import com.eye.cool.photo.params.ImageParams
import com.eye.cool.photo.support.SelectListenerWrapper
import com.eye.cool.photo.view.EmptyView

/**
 *Created by ycb on 2019/8/14 0014
 */
class PhotoHelper(private val activity: AppCompatActivity) {

  private var dialog: PhotoDialogFragment? = null

  fun takePhoto(params: ImageParams) {
    val contentView = EmptyView(activity)
    val builder = createDefaultDialogParams(contentView)
    builder.setOnShownListener(DialogInterface.OnShowListener {
      contentView.takePhoto()
    })
    execute(builder.build(), params)
  }

  fun selectAlbum(params: ImageParams) {
    val contentView = EmptyView(activity)
    val builder = createDefaultDialogParams(contentView)
    builder.setOnShownListener(DialogInterface.OnShowListener {
      contentView.selectAlbum()
    })
    execute(builder.build(), params)
  }

  private fun createDefaultDialogParams(contentView: View): DialogParams.Builder {
    return DialogParams.Builder()
        .setDialogStyle(R.style.PhotoDialog_Translucent)
        .setCancelable(false)
        .setCanceledOnTouchOutside(false)
        .setContentView(contentView)
  }

  private fun execute(dialogParams: DialogParams, params: ImageParams) {
    params.onSelectListener = SelectListenerWrapper(dialog, params.onSelectListener)
    dialog = PhotoDialogFragment.Builder()
        .setImageParams(params)
        .setDialogParams(dialogParams)
        .build()
    dialog!!.show(activity.supportFragmentManager)
  }
}