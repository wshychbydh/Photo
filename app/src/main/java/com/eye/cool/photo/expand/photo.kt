package com.eye.cool.photo.expand

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.FragmentActivity
import com.eye.cool.photo.PhotoDialog
import com.eye.cool.photo.PhotoDialogActivity
import com.eye.cool.photo.PhotoHelper
import com.eye.cool.photo.params.ImageParams
import com.eye.cool.photo.params.Params
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

/**
 * Created by cool on 20-9-21
 */
suspend fun Context.takePhoto(params: ImageParams) = suspendCancellableCoroutine<String> {
  runOnUiThread(this) {
    val helper = PhotoHelper(this)
    params.onSelectListener = object : ImageParams.OnSelectListener {
      override fun onSelect(path: String) {
        it.complete(path)
      }
    }
    helper.onTakePhoto(params)
  }
}

suspend fun Context.selectAlbum(params: ImageParams) = suspendCancellableCoroutine<String> {
  runOnUiThread(this) {
    val helper = PhotoHelper(this)
    params.onSelectListener = object : ImageParams.OnSelectListener {
      override fun onSelect(path: String) {
        it.complete(path)
      }
    }
    helper.onSelectAlbum(params)
  }
}

suspend fun Context.select(params: Params) = suspendCancellableCoroutine<String> {
  runOnUiThread(this) {
    params.imageParams.onSelectListener = object : ImageParams.OnSelectListener {
      override fun onSelect(path: String) {
        it.complete(path)
      }
    }
    if (this is FragmentActivity) {
      PhotoDialog.create(params).show(supportFragmentManager)
    } else {
      PhotoDialogActivity.setParams(params).show(this)
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