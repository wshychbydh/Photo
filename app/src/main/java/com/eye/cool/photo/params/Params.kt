package com.eye.cool.photo.params

import android.app.Activity
import android.support.v4.app.Fragment
import com.eye.cool.permission.Rationale
import com.eye.cool.photo.support.ContextWrapper

class Params private constructor() {

  internal lateinit var wrapper: ContextWrapper

  internal var imageParams: ImageParams = ImageParams.Builder().build()

  internal var dialogParams: DialogParams = DialogParams.Builder().build()

  internal var rationale: Rationale? = null

  internal var rationaleSetting: Rationale? = null

  class Builder {

    private var params = Params()

    constructor(supportFragment: Fragment) {
      params.wrapper = ContextWrapper(supportFragment)
    }

    constructor(activity: Activity) {
      params.wrapper = ContextWrapper(activity)
    }

    //params for picked picture
    fun setImageParams(imageParams: ImageParams): Builder {
      params.imageParams = imageParams
      return this
    }

    //params for shown dialog
    fun setDialogParams(dialogParams: DialogParams): Builder {
      params.dialogParams = dialogParams
      return this
    }

    //permission prompt box
    fun setRationale(rationale: Rationale): Builder {
      params.rationale = rationale
      return this
    }

    //Boot setup authorization prompt box
    fun setRationaleSetting(rationaleSetting: Rationale): Builder {
      params.rationaleSetting = rationaleSetting
      return this
    }

    fun build() = params
  }
}