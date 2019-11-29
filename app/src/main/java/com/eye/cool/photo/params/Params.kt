package com.eye.cool.photo.params

import android.app.Activity
import android.app.Fragment
import com.eye.cool.photo.support.CompatContext

class Params private constructor() {

  internal lateinit var wrapper: CompatContext

  internal var imageParams: ImageParams = ImageParams.Builder().build()

  internal var dialogParams: DialogParams = DialogParams.Builder().build()

  internal var permissionInvoker: ((Array<String>) -> Boolean)? = null

  internal var showRationaleWhenRequest = false

  internal var showRationaleSettingWhenDenied = true

  internal var requestCameraPermission = false

  class Builder {

    private var params = Params()

    /**
     * Used for PhotoDialogActivity
     * init wrapper after activity inited
     */
    internal constructor()

    constructor(fragment: Fragment) {
      params.wrapper = CompatContext(fragment)
    }

    constructor(fragmentX: androidx.fragment.app.Fragment) {
      params.wrapper = CompatContext(fragmentX)
    }

    constructor(activity: Activity) {
      params.wrapper = CompatContext(activity)
    }

    /**
     * The params for selected image
     * @param imageParams
     */
    fun setImageParams(imageParams: ImageParams): Builder {
      params.imageParams = imageParams
      return this
    }

    /**
     * The params for shown dialog
     * @param dialogParams
     */
    fun setDialogParams(dialogParams: DialogParams): Builder {
      params.dialogParams = dialogParams
      return this
    }

    /**
     * Permission invoker to request permissions
     * @param permissionInvoker
     */
    fun setPermissionInvoker(permissionInvoker: (Array<String>) -> Boolean): Builder {
      params.permissionInvoker = permissionInvoker
      return this
    }

    /**
     * If registered permission of 'android.permission.CAMERA' in manifest,
     * you must set it to true, default false
     * @param requestCameraPermission
     */
    fun requestCameraPermission(requestCameraPermission: Boolean): Builder {
      params.requestCameraPermission = requestCameraPermission
      return this
    }

    fun build() = params
  }
}