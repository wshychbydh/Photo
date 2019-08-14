package com.eye.cool.photo

import android.content.DialogInterface
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.eye.cool.photo.params.DialogParams
import com.eye.cool.photo.params.ImageParams
import com.eye.cool.photo.support.OnSelectListenerWrapper
import com.eye.cool.photo.view.EmptyView

/**
 *Created by ycb on 2019/8/14 0014
 */
class PhotoHelper(private val activity: AppCompatActivity) {

  fun onTakePhoto(params: ImageParams) {
    val contentView = EmptyView(activity)
    val builder = createDefaultDialogParams(contentView)
    builder.setOnShowListener(DialogInterface.OnShowListener {
      contentView.onTakePhoto()
    })
    execute(builder.build(), params)
  }

  fun onSelectAlbum(params: ImageParams) {
    val contentView = EmptyView(activity)
    val builder = createDefaultDialogParams(contentView)
    builder.setOnShowListener(DialogInterface.OnShowListener {
      contentView.onSelectAlbum()
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
    val dialog = PhotoDialogFragment.Builder()
        .setImageParams(params)
        .setDialogParams(dialogParams)
        .build()
    params.onSelectListener = OnSelectListenerWrapper(dialog, params.onSelectListener)
    dialog.show(activity.supportFragmentManager)
  }
}