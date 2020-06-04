package com.eye.cool.photo.params

import android.annotation.TargetApi
import android.os.Build
import com.eye.cool.photo.support.PermissionInvoker

class Params private constructor() {

  internal var imageParams: ImageParams = ImageParams.Builder().build()

  internal var dialogParams: DialogParams = DialogParams.Builder().build()

  internal var permissionInvoker: PermissionInvoker? = null

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
     * Callback the request result after requesting permission
     *
     * @param permissionInvoker Permission invoker callback after to request permissions
     */
    @TargetApi(Build.VERSION_CODES.M)
    fun setPermissionInvoker(permissionInvoker: PermissionInvoker?): Builder {
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
     * If you specify a custom image path, you may need to add a FileProvider above 7.0
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