package com.eye.cool.photo.support

import android.animation.ObjectAnimator
import android.view.View
import android.view.Window
import android.view.animation.LinearInterpolator

/**
 * Created by cool on 18-3-9
 */
internal interface IActionConfig {

  fun bindViewAction(view: View, listener: OnActionClickListener) {
    if (!bindActionListener(view, listener)) {
      val album = view.findViewWithTag<View>(Constants.TAG_ALBUM)
      val photo = view.findViewWithTag<View>(Constants.TAG_PHOTO)
      val cancel = view.findViewWithTag<View>(Constants.TAG_CANCEL)
      album?.setOnClickListener { listener.onSelectAlbum() }
      photo?.setOnClickListener { listener.onTakePhoto() }
      cancel?.setOnClickListener { listener.onCancel() }
    }
  }

  private fun bindActionListener(
      view: View,
      listener: OnActionClickListener
  ): Boolean {
    var result = bindActionListener(view, Constants.ACTION_LISTENER, listener)
    if (!result) result = bindActionListener(view, Constants.ACTION_LISTENER_SET, listener)
    return result
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