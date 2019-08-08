package com.eye.cool.photo

import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import com.eye.cool.permission.Permission
import com.eye.cool.permission.PermissionHelper
import java.io.File

/**
 *Created by cool on 2018/6/12
 */
class PhotoHelper(private val params: PhotoDialog.Params) : IPhotoListener {

  private var onClickListener: (() -> Unit)? = null

  private var uri: Uri? = null
  private var outputFile: File? = null
  private var photoFile: File? = null

  internal fun setOnClickListener(listener: (() -> Unit)) {
    onClickListener = listener
  }

  fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
    when (requestCode) {
      TAKE_PHOTO -> {
        if (resultCode == SURE) {
          //拍照完成，进行图片裁切
          uri = FileProviderUtil.uriFromFile(params.wrapper.context(), photoFile!!)
          if (params.cutAble) {
            cut()
          } else {
            onPhotoReady()
          }
        }
      }
      SELECT_ALBUM -> {
        if (resultCode == SURE) {
          uri = intent?.data
          if (params.cutAble) {
            cut()
          } else {
            onPhotoReady()
          }
        }
      }
      ADJUST_PHOTO -> {
        uri = Uri.fromFile(outputFile)
        onPhotoReady()
      }
    }
  }

  override fun onTakePhoto() {
    //拍照指定的是缓存路径，18及以下需要权限，在manifest中已申明；19以上默认拥有权限
    PermissionHelper.Builder(params.wrapper.context())
        .permission(Permission.CAMERA)
        .permissions(Permission.STORAGE)
        .rationale(params.rationale)
        .rationaleSetting(params.rationaleSetting)
        .permissionCallback {
          if (it) {
            photoFile = File(LocalStorage.composePhotoImageFile(params.wrapper.context()))
            PhotoUtil.takePhoto(params.wrapper, photoFile!!)
          } else {
            Toast.makeText(
                params.wrapper.context(), params.wrapper.context()
                .getString(R.string.permission_storage), Toast.LENGTH_SHORT
            ).show()
          }
        }.build()
        .request()

    onClickListener?.invoke()
  }

  override fun onSelectAlbum() {
    //选择相册需要读写权限
    PermissionHelper.Builder(params.wrapper.context())
        .permissions(Permission.STORAGE)
        .permissionCallback {
          if (it) {
            PhotoUtil.takeAlbum(params.wrapper)
          } else {
            Toast.makeText(
                params.wrapper.context(), params.wrapper.context()
                .getString(R.string.permission_storage), Toast.LENGTH_SHORT
            ).show()
          }
        }
        .build()
        .request()
    onClickListener?.invoke()
  }

  override fun onCancel() {
    onClickListener?.invoke()
  }

  private fun onPhotoReady() {
    if (BuildConfig.DEBUG) {
      Log.d(TAG, "outputFileUri : ${uri!!}")
    }
    val fileUrl = FileProviderUtil.getPathFromUri(params.wrapper.context(), uri!!)
    if (BuildConfig.DEBUG) {
      Log.d(TAG, "outputFileUrl : $fileUrl")
    }
    if (fileUrl.isNullOrEmpty()) {
      Toast.makeText(params.wrapper.context(), "Error path '${uri!!.path}'", Toast.LENGTH_SHORT).show()
    } else {
      val convertUrl = convertUrl(fileUrl)
      if (BuildConfig.DEBUG) {
        Log.d(TAG, "convertUrl : $convertUrl")
      }
      params.onPickedListener?.invoke(convertUrl)
    }
  }

  private fun convertUrl(url: String): String {
    if (url.contains("file://")) {
      return url.replace("file://", "")
    }
    return url
  }

  /**
   * 裁剪图片方法实现
   *
   * @param uri
   */
  private fun cut() {
    outputFile = File(LocalStorage.composeThumbFile(params.wrapper.context()))
    outputFile!!.createNewFile()
    PhotoUtil.cut(params.wrapper, this.uri!!, outputFile!!, params.outputW, params.outputH)
    if (BuildConfig.DEBUG) {
      Log.d(TAG, "cutFileUrl : ${FileProviderUtil.getPathFromUri(params.wrapper.context(), uri!!)}")
    }
  }

  companion object {
    /**
     * 拍照标识
     */
    internal const val TAKE_PHOTO = 2001
    internal const val SELECT_ALBUM = 2002
    internal const val ADJUST_PHOTO = 2003

    internal const val SURE = -1 // 确定拍照/选照片
    internal const val CANCEL = 0 // 取消

    internal const val TAG = "photo"
  }
}