package com.eye.cool.photo.expand

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.FragmentActivity
import com.eye.cool.photo.PhotoDialog
import com.eye.cool.photo.PhotoDialogActivity
import com.eye.cool.photo.PhotoHelper
import com.eye.cool.photo.params.DialogParams
import com.eye.cool.photo.params.ImageParams
import com.eye.cool.photo.params.Params
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

/**
 * Created by cool on 20-9-21
 */
object Photo {

  suspend fun takePhoto(
      context: Context,
      imageParams: ImageParams = ImageParams.Builder().build(),
      requestCameraPermission: Boolean = false,
      permissionInvoker: Params.PermissionInvoker? = null,
      onActionClickListener: Params.OnActionListener? = null,
      authority: String? = null
  ) = suspendCancellableCoroutine<String> {
    runOnUiThread(context) {
      val onSelectListener = Params.OnSelectListener { path -> it.complete(path) }
      PhotoHelper(context).onTakePhoto(
          onSelectListener,
          imageParams,
          requestCameraPermission,
          permissionInvoker,
          onActionClickListener,
          authority
      )
    }
  }

  suspend fun selectAlbum(
      context: Context,
      imageParams: ImageParams = ImageParams.Builder().build(),
      requestCameraPermission: Boolean = false,
      permissionInvoker: Params.PermissionInvoker? = null,
      onActionClickListener: Params.OnActionListener? = null,
      authority: String? = null
  ) = suspendCancellableCoroutine<String> {
    runOnUiThread(context) {
      val onSelectListener = Params.OnSelectListener { path -> it.complete(path) }
      PhotoHelper(context).onSelectAlbum(
          onSelectListener,
          imageParams,
          requestCameraPermission,
          permissionInvoker,
          onActionClickListener,
          authority
      )
    }
  }

  suspend fun select(
      context: Context,
      imageParams: ImageParams = ImageParams.Builder().build(),
      dialogParams: DialogParams = DialogParams.Builder().build(),
      requestCameraPermission: Boolean = false,
      permissionInvoker: Params.PermissionInvoker? = null,
      onActionListener: Params.OnActionListener? = null,
      authority: String? = null
  ) = suspendCancellableCoroutine<String> {
    runOnUiThread(context) {
      val params = Params.build {
        onSelectListener = Params.OnSelectListener { path -> it.complete(path) }
        this.imageParams = imageParams
        this.dialogParams = dialogParams
        this.requestCameraPermission = requestCameraPermission
        this.permissionInvoker = permissionInvoker
        this.onActionListener = onActionListener
        this.authority = authority
      }

      if (context is FragmentActivity) {
        PhotoDialog.create(params).show(context.supportFragmentManager)
      } else {
        PhotoDialogActivity.show(context, params)
      }
    }
  }

  private fun runOnUiThread(context: Context, block: () -> Unit) {
    if (Looper.getMainLooper() == Looper.myLooper()) {
      block.invoke()
    } else {
      if (context is Activity) {
        context.runOnUiThread { block.invoke() }
      } else {
        Handler(Looper.getMainLooper()).post { block.invoke() }
      }
    }
  }

  private fun <T> CancellableContinuation<T>.complete(data: T) {
    if (isCompleted) return
    if (isActive) {
      resume(data)
    } else {
      cancel()
    }
  }
}