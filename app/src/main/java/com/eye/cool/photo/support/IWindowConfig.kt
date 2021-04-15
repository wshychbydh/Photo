package com.eye.cool.photo.support

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.app.Dialog
import android.content.Context
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.animation.LinearInterpolator
import androidx.appcompat.app.AppCompatDialog
import com.eye.cool.photo.BuildConfig
import com.eye.cool.photo.params.DialogParams
import com.eye.cool.photo.params.Params

/**
 * Created by cool on 18-3-9
 */
internal interface IWindowConfig {

  fun createDialog(
      context: Context,
      params: Params
  ): Dialog {
    val dialogParams = params.dialogParams
    val dialog = AppCompatDialog(context, dialogParams.themeStyle ?: 0)
    dialog.setCancelable(dialogParams.cancelable)
    dialog.setCanceledOnTouchOutside(dialogParams.canceledOnTouchOutside)
    dialog.setOnShowListener(dialogParams.onShowListener)
    dialog.setOnDismissListener(dialogParams.onDismissListener)
    dialog.setOnCancelListener(dialogParams.onCancelListener)
    val window = dialog.window ?: return dialog
    val layoutParams = configLayoutParams(params.dialogParams, window)
    dialog.onWindowAttributesChanged(layoutParams)
    return dialog
  }

  fun configLayoutParams(
      params: DialogParams,
      window: Window
  ): WindowManager.LayoutParams {

    if (params.windowAnimations != null) {
      window.setWindowAnimations(params.windowAnimations!!)
    }

    val layoutParams = window.attributes
    if (params.alpha != null) {
      layoutParams.alpha = params.alpha!!
    }
    layoutParams.x = params.xPos
    layoutParams.y = params.yPos
    if (params.gravity != null) {
      layoutParams.gravity = params.gravity!!
    }
    if (params.dimAmount != null) {
      layoutParams.dimAmount = params.dimAmount!!
    }
    if (params.horizontalMargin != null) {
      layoutParams.horizontalMargin = params.horizontalMargin!!
    }
    if (params.verticalMargin != null) {
      layoutParams.verticalMargin = params.verticalMargin!!
    }
    if (params.width == null) {
      layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT
    } else {
      layoutParams.width = params.width!!
    }
    if (params.height == null) {
      layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT
    } else {
      layoutParams.height = params.height!!
    }
    if (params.systemUiVisibility != null) {
      layoutParams.systemUiVisibility = params.systemUiVisibility!!
    }
    if (params.softInputMode != null) {
      layoutParams.softInputMode = params.softInputMode!!
    }
    return layoutParams
  }

  fun bindActionListener(view: View, listener: OnActionClickListener) {
    val result = bindActionListener(view, "onActionListener", listener)
    if (!result) bindActionListener(view, "setOnActionListener", listener)
  }

  private fun bindActionListener(
      view: View,
      name: String,
      listener: OnActionClickListener
  ): Boolean {
    return try {
      val method = view.javaClass.getDeclaredMethod(
          name,
          OnActionClickListener::class.java
      )
      method.isAccessible = true
      method.invoke(view, listener)
      true
    } catch (e: Exception) {
      if (BuildConfig.DEBUG) {
        e.printStackTrace()
      }
      false
    }
  }

  fun playExitAnim(window: Window?, view: View?) {
    if (window == null || view == null) return
    val animator1 = ObjectAnimator.ofFloat(
        view,
        "translationY",
        0f,
        view!!.height.toFloat()
    )
//    val animator2 = ValueAnimator.ofFloat(window.attributes.dimAmount, 0f)
//    animator2.addUpdateListener {
//      val dim = it.animatedValue as Float
//      val lp = window.attributes
//      lp.dimAmount = dim
//      window.attributes = lp
//    }
    animator1.duration = 100
    animator1.interpolator = LinearInterpolator()
    animator1.start()
//    val set = AnimatorSet()
//    set.interpolator = LinearInterpolator()
//    set.duration = 100
//    set.playTogether(animator1, animator2)
//    set.start()
  }
}