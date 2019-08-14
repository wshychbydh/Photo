package com.eye.cool.photo.support

import android.support.v4.app.DialogFragment

/**
 *Created by ycb on 2019/8/14 0014
 */
internal class SelectListenerWrapper(
    private val dialogFragment: DialogFragment?,
    private val listener: ISelectListener?
) : ISelectListener {
  override fun onSelected(imageUrl: String) {
    dialogFragment?.dismissAllowingStateLoss()
    listener?.onSelected(imageUrl)
  }
}