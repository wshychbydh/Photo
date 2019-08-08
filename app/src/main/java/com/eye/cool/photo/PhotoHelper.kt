package com.eye.cool.photo

import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import com.eye.cool.permission.Permission
import com.eye.cool.permission.PermissionHelper
import com.eye.cool.photo.params.Params
import com.eye.cool.photo.utils.FileProviderUtil
import com.eye.cool.photo.utils.LocalStorage
import com.eye.cool.photo.utils.PhotoUtil
import java.io.File

/**
 *Created by cool on 2018/6/12
 *
 *Provide picture operation
 *If you don't need a dialog box, you can use it
 */
class PhotoHelper(private val params: Params) : IPhotoListener {

  private val context = params.wrapper.context()

  private var onClickListener: (() -> Unit)? = null
  private var outputFile: File? = null
  private var photoFile: File? = null

  internal fun setOnClickListener(listener: (() -> Unit)) {
    onClickListener = listener
  }

  fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
    when (requestCode) {
      TAKE_PHOTO -> {
        if (resultCode == SURE) {
          val uri = FileProviderUtil.uriFromFile(context, photoFile ?: return)
          if (params.imageParams.cutAble) {
            //After the photo is taken, crop the picture
            cut(uri)
          } else {
            onPhotoReady(uri)
          }
        }
      }
      SELECT_ALBUM -> {
        if (resultCode == SURE) {
          val uri = intent?.data ?: return
          if (params.imageParams.cutAble) {
            cut(uri)
          } else {
            onPhotoReady(uri)
          }
        }
      }
      ADJUST_PHOTO -> {
        onPhotoReady(Uri.fromFile(outputFile ?: return))
      }
    }
  }

  override fun onTakePhoto() {
    PermissionHelper.Builder(context)
        .permission(Permission.CAMERA)
        .permissions(Permission.STORAGE)
        .rationale(params.rationale)
        .rationaleSetting(params.rationaleSetting)
        .permissionCallback {
          if (it) {
            photoFile = File(LocalStorage.composePhotoImageFile(context))
            PhotoUtil.takePhoto(params.wrapper, photoFile!!)
          } else {
            Toast.makeText(context, context.getString(R.string.permission_storage), Toast.LENGTH_SHORT).show()
          }
        }.build()
        .request()
    onClickListener?.invoke()
  }

  override fun onSelectAlbum() {
    PermissionHelper.Builder(context)
        .permissions(Permission.STORAGE)
        .rationale(params.rationale)
        .rationaleSetting(params.rationaleSetting)
        .permissionCallback {
          if (it) {
            PhotoUtil.takeAlbum(params.wrapper)
          } else {
            Toast.makeText(context, context.getString(R.string.permission_storage), Toast.LENGTH_SHORT).show()
          }
        }
        .build()
        .request()
    onClickListener?.invoke()
  }

  override fun onCancel() {
    onClickListener?.invoke()
  }

  private fun onPhotoReady(uri: Uri) {
    if (BuildConfig.DEBUG) {
      Log.d(TAG, "outputFileUri : $uri")
    }
    val fileUrl = FileProviderUtil.getPathFromUri(context, uri)
    if (BuildConfig.DEBUG) {
      Log.d(TAG, "outputFileUrl : $fileUrl")
    }
    if (fileUrl.isNullOrEmpty()) {
      Toast.makeText(context, "Error path '${uri.path}'", Toast.LENGTH_SHORT).show()
    } else {
      val convertUrl = convertUrl(fileUrl)
      if (BuildConfig.DEBUG) {
        Log.d(TAG, "convertUrl : $convertUrl")
      }
      params.imageParams.onSelectListener?.onSelected(convertUrl)
    }
  }

  private fun convertUrl(url: String): String {
    if (url.contains("file://")) {
      return url.replace("file://", "")
    }
    return url
  }

  private fun cut(uri: Uri) {
    outputFile = File(LocalStorage.composeThumbFile(context))
    outputFile!!.createNewFile()
    PhotoUtil.cut(params.wrapper, uri, outputFile!!, params.imageParams.outputW, params.imageParams.outputH)
    if (BuildConfig.DEBUG) {
      Log.d(TAG, "cutFileUrl : ${FileProviderUtil.getPathFromUri(context, uri)}")
    }
  }

  companion object {
    internal const val TAKE_PHOTO = 2001
    internal const val SELECT_ALBUM = 2002
    internal const val ADJUST_PHOTO = 2003

    internal const val SURE = -1
    internal const val CANCEL = 0

    internal const val TAG = "photo"
  }
}