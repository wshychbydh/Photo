package com.eye.cool.photo.params

import android.content.res.Resources
import android.support.annotation.StyleRes
import android.view.View
import com.eye.cool.photo.R

/**
 *Created by ycb on 2019/8/8 0008
 */
class DialogParams private constructor() {

  internal var contentView: View? = null

  @StyleRes
  internal var dialogStyle: Int = R.style.PhotoDialog

  @StyleRes
  internal var animStyle: Int = R.style.AnimBottom

  internal var xPos = 0

  internal var yPos = Resources.getSystem().displayMetrics.heightPixels

  class Builder {

    private var params = DialogParams()

    //dialog's contentView for shown
    fun setContentView(view: View): Builder {
      params.contentView = view
      return this
    }

    //Pop-up animation style，default from bottom
    fun setAnimStyle(animStyle: Int): Builder {
      params.animStyle = animStyle
      return this
    }

    //dialog' style
    fun setDialogStyle(dialogStyle: Int): Builder {
      params.dialogStyle = dialogStyle
      return this
    }

    //Dialog popup location
    fun setCoordinate(x: Int, y: Int): Builder {
      params.xPos = x
      params.yPos = y
      return this
    }

    fun build() = params
  }
}