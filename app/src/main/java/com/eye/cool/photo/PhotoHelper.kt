package com.eye.cool.photo

import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.os.Build
import android.view.View
import androidx.fragment.app.FragmentActivity
import com.eye.cool.photo.params.DialogParams
import com.eye.cool.photo.params.ImageParams
import com.eye.cool.photo.params.Params
import com.eye.cool.photo.view.EmptyView

/**
 *Created by ycb on 2019/8/14 0014
 */
class PhotoHelper(private val context: Context) {

  /**
   * Take a photo
   *
   * [onSelectListener] Callback after picture selection
   * [imageParams] The configure of image
   * [requestCameraPermission] If registered permission of 'android.permission.CAMERA' in manifest,
   * you must set it to true, default false
   * [permissionInvoker] Permission invoker callback after to request permissions
   * [authority] The authority of a {@link FileProvider}
   * defined in a {@code <provider>} element in your app's manifest.
   */
  @TargetApi(Build.VERSION_CODES.N)
  fun onTakePhoto(
      onSelectListener: Params.OnSelectListener,
      imageParams: ImageParams = ImageParams.Builder().build(),
      requestCameraPermission: Boolean = false,
      permissionInvoker: Params.PermissionInvoker? = null,
      onActionClickListener: Params.OnActionListener? = null,
      authority: String? = null
  ) {
    val contentView = EmptyView(context)
    val builder = createDefaultDialogParams(contentView)
    builder.onShowListener {
      contentView.onTakePhoto()
    }
    execute(
        onSelectListener,
        builder.build(),
        imageParams,
        requestCameraPermission,
        permissionInvoker,
        onActionClickListener,
        authority
    )
  }

  /**
   * Select from album
   *
   * [onSelectListener] Callback after picture selection
   * [imageParams] The configure of image
   * [requestCameraPermission] If registered permission of 'android.permission.CAMERA' in manifest,
   * you must set it to true, default false
   * [permissionInvoker] Permission invoker callback after to request permissions
   * [authority] The authority of a {@link FileProvider}
   * defined in a {@code <provider>} element in your app's manifest.
   */
  @TargetApi(Build.VERSION_CODES.N)
  fun onSelectAlbum(
      onSelectListener: Params.OnSelectListener,
      imageParams: ImageParams = ImageParams.Builder().build(),
      requestCameraPermission: Boolean = false,
      permissionInvoker: Params.PermissionInvoker? = null,
      onActionListener: Params.OnActionListener? = null,
      authority: String? = null
  ) {
    val contentView = EmptyView(context)
    val builder = createDefaultDialogParams(contentView)
    builder.onShowListener {
      contentView.onSelectAlbum()
    }
    execute(
        onSelectListener,
        builder.build(),
        imageParams,
        requestCameraPermission,
        permissionInvoker,
        onActionListener,
        authority
    )
  }

  private fun createDefaultDialogParams(contentView: View): DialogParams.Builder {
    return DialogParams.Builder()
        .themeStyle(R.style.photo_dialog_translucent)
        .cancelable(false)
        .canceledOnTouchOutside(false)
        .contentView(contentView)
  }

  private fun execute(
      onSelectListener: Params.OnSelectListener,
      dialogParams: DialogParams,
      imageParams: ImageParams,
      requestCameraPermission: Boolean = false,
      permissionInvoker: Params.PermissionInvoker? = null,
      onActionListener: Params.OnActionListener? = null,
      authority: String? = null
  ) {
    when (context) {
      is FragmentActivity -> {
        createAppDialogFragment(
            onSelectListener,
            dialogParams,
            imageParams,
            requestCameraPermission,
            permissionInvoker,
            onActionListener,
            authority
        ).show(context.supportFragmentManager)
      }
      is Activity -> {
        createDialogFragment(
            onSelectListener,
            dialogParams,
            imageParams,
            requestCameraPermission,
            permissionInvoker,
            onActionListener,
            authority
        ).show(context.fragmentManager)
      }
      else -> {
        val params = Params.build {
          this.imageParams = imageParams
          this.dialogParams = dialogParams
          this.authority = authority
          this.requestCameraPermission = requestCameraPermission
          this.permissionInvoker = permissionInvoker
          this.onActionListener = onActionListener
        }
        PhotoDialogActivity.show(context, params)
      }
    }
  }

  private fun createDialogFragment(
      onSelectListener: Params.OnSelectListener,
      dialogParams: DialogParams,
      imageParams: ImageParams,
      requestCameraPermission: Boolean = false,
      permissionInvoker: Params.PermissionInvoker? = null,
      onActionListener: Params.OnActionListener? = null,
      authority: String? = null
  ): PhotoDialogFragment {
    return PhotoDialogFragment.create(
        Params.build {
          this.onSelectListener = onSelectListener
          this.dialogParams = dialogParams
          this.imageParams = imageParams
          this.requestCameraPermission = requestCameraPermission
          this.permissionInvoker = permissionInvoker
          this.onActionListener = onActionListener
          this.authority = authority
        }
    )
  }

  private fun createAppDialogFragment(
      onSelectListener: Params.OnSelectListener,
      dialogParams: DialogParams,
      imageParams: ImageParams,
      requestCameraPermission: Boolean,
      permissionInvoker: Params.PermissionInvoker? = null,
      onActionListener: Params.OnActionListener? = null,
      authority: String?
  ): PhotoDialog {
    return PhotoDialog.create(
        Params.build {
          this.onSelectListener = onSelectListener
          this.dialogParams = dialogParams
          this.imageParams = imageParams
          this.requestCameraPermission = requestCameraPermission
          this.permissionInvoker = permissionInvoker
          this.onActionListener = onActionListener
          this.authority = authority
        }
    )
  }
}