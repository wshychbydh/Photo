package com.eye.cool.photo

import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.os.Build
import android.view.View
import androidx.fragment.app.FragmentActivity
import com.eye.cool.photo.params.DialogParams
import com.eye.cool.photo.params.ImageParams
import com.eye.cool.photo.params.Params
import com.eye.cool.photo.support.OnSelectListener
import com.eye.cool.photo.view.EmptyView

/**
 *Created by ycb on 2019/8/14 0014
 */
class PhotoHelper(private val context: Context) {

  /**
   * Take a photo
   *
   * @param onSelectListener Image selection callback
   */
  fun onTakePhoto(onSelectListener: OnSelectListener) {
    onTakePhoto(
        ImageParams.Builder()
            .setOnSelectListener(onSelectListener)
            .build()
    )
  }

  /**
   * Take a photo
   *
   * @param params The configure of image
   */
  fun onTakePhoto(params: ImageParams) {
    onTakePhoto(params, false, null)
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
  @TargetApi(Build.VERSION_CODES.M)
  fun onTakePhoto(
      params: ImageParams,
      requestCameraPermission: Boolean = false,
      permissionInvoker: ((Array<String>) -> Boolean)? = null
  ) {
    onTakePhoto(params, requestCameraPermission, permissionInvoker, null)
  }

  /**
   * Take a photo
   *
   * @param params The configure of image
   * @param requestCameraPermission If registered permission of 'android.permission.CAMERA' in manifest,
   * you must set it to true, default false
   * @param permissionInvoker Permission request executor.
   * Permissions are need to be granted, include {@WRITE_EXTERNAL_STORAGE} and {@READ_EXTERNAL_STORAGE} and maybe {@CAMERA}
   * @param authority The authority of a {@link FileProvider} defined in a {@code <provider>} element in your app's manifest.
   */
  @TargetApi(Build.VERSION_CODES.N)
  fun onTakePhoto(
      params: ImageParams,
      requestCameraPermission: Boolean = false,
      permissionInvoker: ((Array<String>) -> Boolean)? = null,
      authority: String? = null
  ) {
    val contentView = EmptyView(context)
    val builder = createDefaultDialogParams(contentView)
    builder.setOnShowListener(DialogInterface.OnShowListener {
      contentView.onTakePhoto()
    })
    execute(builder.build(), params, requestCameraPermission, permissionInvoker, authority)
  }

  /**
   * Select from album
   *
   * @param onSelectListener Image selection callback
   */
  fun onSelectAlbum(onSelectListener: OnSelectListener) {
    onSelectAlbum(
        ImageParams.Builder()
            .setOnSelectListener(onSelectListener)
            .build()
    )
  }

  /**
   * Select from album
   *
   * @param imageParams The configure of image
   */
  fun onSelectAlbum(imageParams: ImageParams) {
    onSelectAlbum(imageParams, null)
  }


  /**
   * Select from album
   *
   * @param imageParams The configure of image
   * @param permissionInvoker Permission request executor.
   * Permissions are need to be granted, include {@WRITE_EXTERNAL_STORAGE} and {@READ_EXTERNAL_STORAGE}
   */
  @TargetApi(Build.VERSION_CODES.M)
  fun onSelectAlbum(
      imageParams: ImageParams,
      permissionInvoker: ((Array<String>) -> Boolean)? = null
  ) {
    onSelectAlbum(imageParams, permissionInvoker, null)
  }

  /**
   * Select from album
   *
   * @param imageParams The configure of image
   * @param permissionInvoker Permission request executor.
   * Permissions are need to be granted, include {@WRITE_EXTERNAL_STORAGE} and {@READ_EXTERNAL_STORAGE}
   */
  @TargetApi(Build.VERSION_CODES.N)
  fun onSelectAlbum(
      imageParams: ImageParams,
      permissionInvoker: ((Array<String>) -> Boolean)? = null,
      authority: String? = null
  ) {
    val contentView = EmptyView(context)
    val builder = createDefaultDialogParams(contentView)
    builder.setOnShowListener(DialogInterface.OnShowListener {
      contentView.onSelectAlbum()
    })
    execute(builder.build(), imageParams, false, permissionInvoker, authority)
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
      imageParams: ImageParams,
      requestCameraPermission: Boolean = false,
      permissionInvoker: ((Array<String>) -> Boolean)? = null,
      authority: String? = null
  ) {
    when (context) {
      is FragmentActivity -> {
        createAppDialogFragment(
            dialogParams,
            imageParams,
            requestCameraPermission,
            permissionInvoker,
            authority
        ).show(context.supportFragmentManager)
      }
      is Activity -> {
        createDialogFragment(
            dialogParams,
            imageParams,
            requestCameraPermission,
            permissionInvoker,
            authority
        ).show(context.fragmentManager)
      }
      else -> {
        PhotoDialogActivity
            .resetParams()
            .setParams(
                Params.Builder()
                    .setImageParams(imageParams)
                    .setDialogParams(dialogParams)
                    .setAuthority(authority)
                    .requestCameraPermission(requestCameraPermission)
                    .setPermissionInvoker(permissionInvoker)
                    .build()
            )
            .show(context)
      }
    }
  }

  private fun createDialogFragment(
      dialogParams: DialogParams,
      imageParams: ImageParams,
      requestCameraPermission: Boolean = false,
      permissionInvoker: ((Array<String>) -> Boolean)? = null,
      authority: String? = null
  ): PhotoDialogFragment {
    return PhotoDialogFragment.create(
        Params.Builder()
            .setDialogParams(dialogParams)
            .setImageParams(imageParams)
            .requestCameraPermission(requestCameraPermission)
            .setPermissionInvoker(permissionInvoker)
            .setAuthority(authority)
            .build()
    )
  }

  private fun createAppDialogFragment(
      dialogParams: DialogParams,
      imageParams: ImageParams,
      requestCameraPermission: Boolean,
      permissionInvoker: ((Array<String>) -> Boolean)? = null,
      authority: String?
  ): PhotoDialog {
    return PhotoDialog.create(
        Params.Builder()
            .setDialogParams(dialogParams)
            .setImageParams(imageParams)
            .requestCameraPermission(requestCameraPermission)
            .setPermissionInvoker(permissionInvoker)
            .setAuthority(authority)
            .build()
    )
  }
}