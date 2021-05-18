package com.eye.cool.photo.support

import android.animation.ObjectAnimator
import android.app.Dialog
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.animation.LinearInterpolator
import com.eye.cool.photo.BuildConfig
import com.eye.cool.photo.params.DialogParams

/**
 * Created by cool on 18-3-9
 */
internal interface IWindowConfig {

  fun setupDialog(params: DialogParams, dialog: Dialog) {
    dialog.setCancelable(params.cancelable)
    dialog.setCanceledOnTouchOutside(params.canceledOnTouchOutside)

    dialog.setOnShowListener(params.onShowListener)
    dialog.setOnKeyListener(params.onKeyListener)

    dialog.onWindowAttributesChanged(configLayoutParams(params, dialog.window ?: return))
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
    val result = bindActionListener(view, "onActionClickListener", listener)
    if (!result) bindActionListener(view, "setOnActionClickListener", listener)
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