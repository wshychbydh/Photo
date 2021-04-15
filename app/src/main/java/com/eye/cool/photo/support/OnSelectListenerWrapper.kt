package com.eye.cool.photo.support

import android.app.DialogFragment
import androidx.appcompat.app.AppCompatDialogFragment
import com.eye.cool.photo.params.Params

/**
 *Created by ycb on 2019/8/14 0014
 */
internal class OnSelectListenerWrapper(
    private val dialogFragment: DialogFragment? = null,
    private val compatDialogFragment: AppCompatDialogFragment? = null,
    private val listener: Params.OnSelectListener?
) : Params.OnSelectListener {
  override fun onSelect(path: String) {
    dialogFragment?.dismissAllowingStateLoss()
    compatDialogFragment?.dismissAllowingStateLoss()
    listener?.onSelect(path)
  }
}