package com.eye.cool.photo.params

import android.annotation.TargetApi
import android.os.Build

class Params private constructor() {

  internal var imageParams: ImageParams = ImageParams.Builder().build()

  internal var dialogParams: DialogParams = DialogParams.Builder().build()

  internal var permissionInvoker: ((Array<String>) -> Boolean)? = null

  internal var requestCameraPermission = false

  internal var authority: String? = null

  class Builder {

    private var params = Params()

    /**
     * The configure for selected image
     *
     * @param imageParams
     */
    fun setImageParams(imageParams: ImageParams): Builder {
      params.imageParams = imageParams
      return this
    }

    /**
     * The configure for shown dialog
     *
     * @param dialogParams
     */
    fun setDialogParams(dialogParams: DialogParams): Builder {
      params.dialogParams = dialogParams
      return this
    }

    /**
     * Permission invoker to request permissions,
     *
     * @param permissionInvoker Permission request executor. Permissions are need to be granted, include {@WRITE_EXTERNAL_STORAGE} and {@READ_EXTERNAL_STORAGE} and maybe {@CAMERA}
     */
    @TargetApi(Build.VERSION_CODES.M)
    fun setPermissionInvoker(permissionInvoker: ((Array<String>) -> Boolean)? = null): Builder {
      params.permissionInvoker = permissionInvoker
      return this
    }

    /**
     * If registered permission of 'android.permission.CAMERA' in manifest,
     * you must set it to true, default false
     *
     * @param requestCameraPermission
     */
    @TargetApi(Build.VERSION_CODES.M)
    fun requestCameraPermission(requestCameraPermission: Boolean): Builder {
      params.requestCameraPermission = requestCameraPermission
      return this
    }

    /**
     * If you specify a custom image path, you need to add a FileProvider above 7.0
     *
     * @param authority The authority of a {@link FileProvider} defined in a
     *            {@code <provider>} element in your app's manifest.
     */
    @TargetApi(Build.VERSION_CODES.N)
    fun setAuthority(authority: String?): Builder {
      params.authority = authority
      return this
    }

    fun build() = params
  }
}