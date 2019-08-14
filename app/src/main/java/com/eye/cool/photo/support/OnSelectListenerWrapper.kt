package com.eye.cool.photo.support

import android.support.v4.app.DialogFragment

/**
 *Created by ycb on 2019/8/14 0014
 */
internal class OnSelectListenerWrapper(
    private val dialogFragment: DialogFragment?,
    private val listener: OnSelectListener?
) : OnSelectListener {
  override fun onSelect(path: String) {
    dialogFragment?.dismissAllowingStateLoss()
    listener?.onSelect(path)
  }
}