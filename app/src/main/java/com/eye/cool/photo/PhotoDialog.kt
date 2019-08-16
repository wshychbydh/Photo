package com.eye.cool.photo

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import com.eye.cool.photo.params.Params
import com.eye.cool.photo.support.OnActionListener
import com.eye.cool.photo.support.OnClickListener
import com.eye.cool.photo.utils.PhotoExecutor
import com.eye.cool.photo.view.DefaultView

/**
 * Created by cool on 18-3-9
 */
class PhotoDialog(
    private val params: Params
) : Dialog(params.wrapper.context(), params.dialogParams.dialogStyle) {

  private val executor = PhotoExecutor(params)

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    val view = params.dialogParams.contentView ?: DefaultView(context)
    val method = view.javaClass.getDeclaredMethod("setActionListener", OnActionListener::class.java)
        ?: throw IllegalArgumentException("Custom View must declare method setActionListener(OnActionListener)")
    method.isAccessible = true
    method.invoke(view, executor)
    setContentView(view)
    setParams()

    executor.setOnClickListener(object : OnClickListener {
      override fun onClick(which: Int) {
        dismiss()
        params.dialogParams.onClickListener?.onClick(which)
      }
    })
  }

  private fun setParams() {
    val dialogParams = params.dialogParams
    setCancelable(dialogParams.cancelable)
    setCanceledOnTouchOutside(dialogParams.canceledOnTouchOutside)
    setOnCancelListener(dialogParams.onCancelListener)
    setOnDismissListener(dialogParams.onDismissListener)
    setOnShowListener(dialogParams.onShowListener)

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