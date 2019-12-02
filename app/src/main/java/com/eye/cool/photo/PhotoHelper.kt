package com.eye.cool.photo

import android.app.Activity
import android.app.Fragment
import android.content.DialogInterface
import android.view.View
import androidx.fragment.app.FragmentActivity
import com.eye.cool.photo.params.DialogParams
import com.eye.cool.photo.params.ImageParams
import com.eye.cool.photo.params.Params
import com.eye.cool.photo.support.CompatContext
import com.eye.cool.photo.support.OnSelectListener
import com.eye.cool.photo.support.OnSelectListenerWrapper
import com.eye.cool.photo.view.EmptyView

/**
 *Created by ycb on 2019/8/14 0014
 */
class PhotoHelper {

  private val compat: CompatContext

  constructor(fragmentX: androidx.fragment.app.Fragment) {
    compat = CompatContext(fragmentX)
  }

  constructor(fragment: Fragment) {
    compat = CompatContext(fragment)
  }

  constructor(activity: Activity) {
    compat = CompatContext(activity)
  }

  /**
   * Take a photo
   *
   * @param onSelectListener Image selection callback
   * @param requestCameraPermission If registered permission of 'android.permission.CAMERA' in manifest,
   * you must set it to true, default false
   * @param permissionInvoker Permission request executor.
   * Permissions are need to be granted, include {@WRITE_EXTERNAL_STORAGE} and {@READ_EXTERNAL_STORAGE} and maybe {@CAMERA}
   */
  fun onTakePhoto(
      onSelectListener: OnSelectListener,
      requestCameraPermission: Boolean = false,
      permissionInvoker: ((Array<String>) -> Boolean)? = null
  ) {
    onTakePhoto(
        ImageParams.Builder()
            .setOnSelectListener(onSelectListener)
            .build(),
        requestCameraPermission,
        permissionInvoker
    )
  }

  /**
   * Take a photo
   *
   * @param params The configure of image
   * @param requestCameraPermission If registered permission of 'android.permission.CAMERA' in manifest,
   * you must set it to true, default false
   * @param permissionInvoker Permission request executor.
   * Permissions are need to be granted, include {@WRITE_EXTERNAL_STORAGE} and {@READ_EXTERNAL_STORAGE} and maybe {@CAMERA}
   */
  fun onTakePhoto(
      params: ImageParams,
      requestCameraPermission: Boolean = false,
      permissionInvoker: ((Array<String>) -> Boolean)? = null
  ) {
    val contentView = EmptyView(compat.context())
    val builder = createDefaultDialogParams(contentView)
    builder.setOnShowListener(DialogInterface.OnShowListener {
      contentView.onTakePhoto()
    })
    execute(builder.build(), params, requestCameraPermission, permissionInvoker)
  }

  /**
   * Select from album
   *
   * @param onSelectListener Image selection callback
   * @param permissionInvoker Permission request executor.
   * Permissions are need to be granted, include {@WRITE_EXTERNAL_STORAGE} and {@READ_EXTERNAL_STORAGE}
   */
  fun onSelectAlbum(onSelectListener: OnSelectListener, permissionInvoker: ((Array<String>) -> Boolean)? = null) {
    onSelectAlbum(
        ImageParams.Builder()
            .setOnSelectListener(onSelectListener)
            .build(),
        permissionInvoker
    )
  }


  /**
   * Select from album
   *
   * @param imageParams The configure of image
   * @param permissionInvoker Permission request executor.
   * Permissions are need to be granted, include {@WRITE_EXTERNAL_STORAGE} and {@READ_EXTERNAL_STORAGE}
   */
  fun onSelectAlbum(imageParams: ImageParams, permissionInvoker: ((Array<String>) -> Boolean)? = null) {
    val contentView = EmptyView(compat.context())
    val builder = createDefaultDialogParams(contentView)
    builder.setOnShowListener(DialogInterface.OnShowListener {
      contentView.onSelectAlbum()
    })
    execute(builder.build(), imageParams, permissionInvoker = permissionInvoker)
  }

  private fun createDefaultDialogParams(contentView: View): DialogParams.Builder {
    return DialogParams.Builder()
        .setDialogStyle(R.style.PhotoDialog_Translucent)
        .setCancelable(false)
        .setCanceledOnTouchOutside(false)
        .setContentView(contentView)
  }

  private fun execute(
      dialogParams: DialogParams,
      params: ImageParams,
      requestCameraPermission: Boolean = false,
      permissionInvoker: ((Array<String>) -> Boolean)? = null
  ) {
    val activity = compat.activity()
    if (activity is FragmentActivity) {
      val dialog = createAppDialogFragment(dialogParams, params, requestCameraPermission, permissionInvoker)
      params.onSelectListener = OnSelectListenerWrapper(
          compatDialogFragment = dialog,
          listener = params.onSelectListener
      )
      dialog.show(activity.supportFragmentManager)
    } else {
      val dialog = createDialogFragment(dialogParams, params, requestCameraPermission, permissionInvoker)
      params.onSelectListener = OnSelectListenerWrapper(
          dialogFragment = dialog,
          listener = params.onSelectListener
      )
      dialog.show(activity.fragmentManager)
    }
  }

  private fun createDialogFragment(
      dialogParams: DialogParams,
      imageParams: ImageParams,
      requestCameraPermission: Boolean,
      permissionInvoker: ((Array<String>) -> Boolean)? = null
  ): PhotoDialogFragment {
    return PhotoDialogFragment.create(
        Params.Builder()
            .setDialogParams(dialogParams)
            .setImageParams(imageParams)
            .requestCameraPermission(requestCameraPermission)
            .setPermissionInvoker(permissionInvoker)
            .build()
    )
  }

  private fun createAppDialogFragment(
      dialogParams: DialogParams,
      imageParams: ImageParams,
      requestCameraPermission: Boolean,
      permissionInvoker: ((Array<String>) -> Boolean)? = null
  ): PhotoDialog {
    return PhotoDialog.create(
        Params.Builder()
            .setDialogParams(dialogParams)
            .setImageParams(imageParams)
            .requestCameraPermission(requestCameraPermission)
            .setPermissionInvoker(permissionInvoker)
            .build()
    )
  }
}