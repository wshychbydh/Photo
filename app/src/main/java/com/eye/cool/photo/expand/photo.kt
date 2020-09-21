package com.eye.cool.photo.expand

import android.content.Context
import com.eye.cool.photo.PhotoHelper
import com.eye.cool.photo.params.ImageParams
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * Created by cool on 20-9-21
 */
suspend fun Context.takePhoto() = suspendCoroutine<String> {
  val helper = PhotoHelper(this)
  helper.onTakePhoto(object : ImageParams.OnSelectListener {
    override suspend fun onSelect(path: String) {
      it.resume(path)
    }
  })
}

suspend fun Context.selectAlbum() = suspendCoroutine<String> {
  val helper = PhotoHelper(this)
  helper.onSelectAlbum(object : ImageParams.OnSelectListener {
    override suspend fun onSelect(path: String) {
      it.resume(path)
    }
  })
}