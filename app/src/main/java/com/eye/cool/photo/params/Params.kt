package com.eye.cool.photo.params

import android.annotation.TargetApi
import android.os.Build
import com.eye.cool.photo.support.ActionDef

class Params private constructor() {

  internal var imageParams: ImageParams = ImageParams.Builder().build()

  internal var dialogParams: DialogParams = DialogParams.Builder().build()

  internal var permissionInvoker: PermissionInvoker? = null

  internal var requestCameraPermission = false

  internal var authority: String? = null

  internal var onSelectListener: OnSelectListener? = null

  internal var onActionListener: OnActionListener? = null

  class Builder {

    private var params = Params()

    /**
     * The configure for selected image
     *
     * [imageParams]
     */
    fun imageParams(imageParams: ImageParams): Builder {
      params.imageParams = imageParams
      return this
    }

    /**
     * The configure for shown dialog
     *
     * [dialogParams]
     */
    fun dialogParams(dialogParams: DialogParams): Builder {
      params.dialogParams = dialogParams
      return this
    }

    /**
     * Callback the request result after requesting permission
     *
     * [permissionInvoker] Permission invoker callback after to request permissions
     */
    @TargetApi(Build.VERSION_CODES.M)
    fun permissionInvoker(permissionInvoker: PermissionInvoker?): Builder {
      params.permissionInvoker = permissionInvoker
      return this
    }

    /**
     * If registered permission of 'android.permission.CAMERA' in manifest,
     * you must set it to true, default false
     *
     * [requestCameraPermission]
     */
    @TargetApi(Build.VERSION_CODES.M)
    fun requestCameraPermission(requestCameraPermission: Boolean): Builder {
      params.requestCameraPermission = requestCameraPermission
      return this
    }

    /**
     * If you specify a custom image path, you may need to add a FileProvider above 7.0
     *
     * [authority] The authority of a {@link FileProvider} defined in a
     *            {@code <provider>} element in your app's manifest.
     */
    @TargetApi(Build.VERSION_CODES.N)
    fun authority(authority: String?): Builder {
      params.authority = authority
      return this
    }

    /**
     * Callback after image selection, callback in ui thread
     *
     * [listener] must be set
     */
    fun onSelectListener(listener: OnSelectListener): Builder {
      params.onSelectListener = listener
      return this
    }

    /**
     * Set a listener to be invoked when the action was happened.
     *
     * <p>
     *   Only one of these actions
     *   {@link Action#TAKE_PHOTO, SELECT_ALBUM, CANCEL, PERMISSION_FORBID}
     * </p>
     *
     * [listener]
     */
    fun onActionListener(listener: OnActionListener?): Builder {
      params.onActionListener = listener
      return this
    }

    fun build() = params
  }

  interface PermissionInvoker {

    /**
     * Permission invoker to request permissions.
     *
     * [permissions] Permissions are need to be granted,
     * include {@WRITE_EXTERNAL_STORAGE} and {@READ_EXTERNAL_STORAGE} and maybe {@CAMERA}
     *
     * [invoker] call on permission granted or denied
     */
    fun request(permissions: Array<String>, invoker: (Boolean) -> Unit)
  }

  interface OnSelectListener {

    /**
     * return on UI Thread
     *
     * Path maybe context.externalCacheDir or context.cacheDir
     *
     * callback when select image successful
     *
     * [path] output image's local file path
     */
    fun onSelect(path: String)
  }

  interface OnActionListener {

    /**
     * {@link Action
     *    #TAKE_PHOTO,
     *    #SELECT_ALBUM,
     *    #CANCEL,
     *    #PERMISSION_DENIED
     * }
     *
     * [action] Which action that was happened.
     */
    fun onAction(@ActionDef action: Int)
  }
}