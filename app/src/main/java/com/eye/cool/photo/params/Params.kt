package com.eye.cool.photo.params

import android.annotation.TargetApi
import android.os.Build
import com.eye.cool.photo.support.ActionDef

class Params private constructor(
    internal val requestCameraPermission: Boolean,
    internal var onSelectListener: OnSelectListener?,
    internal val onActionListener: OnActionListener?,
    internal val authority: String?,
    internal val permissionInvoker: PermissionInvoker?,
    internal val imageParams: ImageParams,
    internal val dialogParams: DialogParams
) {

  companion object {
    inline fun build(block: Builder.() -> Unit) = Builder().apply(block).build()
  }

  class Builder(
      var requestCameraPermission: Boolean = false,
      var onSelectListener: OnSelectListener? = null,
      var onActionListener: OnActionListener? = null,
      var authority: String? = null,
      var permissionInvoker: PermissionInvoker? = null,
      var imageParams: ImageParams = ImageParams.Builder().build(),
      var dialogParams: DialogParams = DialogParams.Builder().build()
  ) {

    /**
     * The configure for selected image
     *
     * [imageParams]
     */
    fun imageParams(imageParams: ImageParams) = apply { this.imageParams = imageParams }

    /**
     * The configure for shown dialog
     *
     * [dialogParams]
     */
    fun dialogParams(dialogParams: DialogParams) = apply { this.dialogParams = dialogParams }

    /**
     * Callback the request result after requesting permission
     *
     * [permissionInvoker] Permission invoker callback after to request permissions
     */
    @TargetApi(Build.VERSION_CODES.M)
    fun permissionInvoker(permissionInvoker: PermissionInvoker?) = apply {
      this.permissionInvoker = permissionInvoker
    }

    /**
     * If registered permission of 'android.permission.CAMERA' in manifest,
     * you must set it to true, default false
     *
     * [requestCameraPermission]
     */
    @TargetApi(Build.VERSION_CODES.M)
    fun requestCameraPermission(requestCameraPermission: Boolean) = apply {
      this.requestCameraPermission = requestCameraPermission
    }

    /**
     * If you specify a custom image path, you may need to add a FileProvider above 7.0
     *
     * [authority] The authority of a {@link FileProvider} defined in a
     *            {@code <provider>} element in your app's manifest.
     */
    @TargetApi(Build.VERSION_CODES.N)
    fun authority(authority: String?) = apply { this.authority = authority }

    /**
     * Callback after image selection, callback in ui thread
     *
     * [listener] must be set
     */
    fun onSelectListener(listener: OnSelectListener) = apply { this.onSelectListener = listener }

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
    fun onActionListener(listener: OnActionListener?) = apply { this.onActionListener = listener }

    fun build() = Params(
        requestCameraPermission = requestCameraPermission,
        onSelectListener = onSelectListener,
        onActionListener = onActionListener,
        authority = authority,
        permissionInvoker = permissionInvoker,
        imageParams = imageParams,
        dialogParams = dialogParams
    )
  }

  fun interface PermissionInvoker {

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

  fun interface OnSelectListener {

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

  fun interface OnActionListener {

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