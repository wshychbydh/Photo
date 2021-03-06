package com.eye.cool.photo.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup

/**
 *Created by ycb on 2019/8/14 0014
 */
internal class EmptyView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

  init {
    layoutParams = ViewGroup.LayoutParams(1, 1)
  }

  private var listener: com.eye.cool.photo.support.OnActionClickListener? = null

  //necessary
  private fun onActionClickListener(listener: com.eye.cool.photo.support.OnActionClickListener) {
    this.listener = listener
  }

  fun onTakePhoto() {
    listener?.onTakePhoto()
  }

  fun onSelectAlbum() {
    listener?.onSelectAlbum()
  }
}