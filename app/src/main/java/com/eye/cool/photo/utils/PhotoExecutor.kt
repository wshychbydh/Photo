package com.eye.cool.photo.utils

import android.content.Intent
import android.net.Uri
import android.util.Log
import com.eye.cool.photo.BuildConfig
import com.eye.cool.photo.R
import com.eye.cool.photo.params.Params
import com.eye.cool.photo.support.Constants.ADJUST_PHOTO
import com.eye.cool.photo.support.Constants.CANCEL
import com.eye.cool.photo.support.Constants.PERMISSION_FORBID
import com.eye.cool.photo.support.Constants.SELECT_ALBUM
import com.eye.cool.photo.support.Constants.TAG
import com.eye.cool.photo.support.Constants.TAKE_PHOTO
import com.eye.cool.photo.support.OnActionListener
import com.eye.cool.photo.support.OnClickListener
import com.eye.cool.photo.view.PhotoPermissionActivity
import java.io.File

/**
 *Created by cool on 2018/6/12
 */
internal class PhotoExecutor(private val params: Params) : OnActionListener {

  private val context = params.wrapper.context()

  private var clickListener: OnClickListener? = null
  private var outputFile: File? = null
  private var photoFile: File? = null

  internal fun setOnClickListener(listener: OnClickListener) {
    clickListener = listener
  }

  fun onActivityResult(requestCode: Int, intent: Intent?) {
    when (requestCode) {
      TAKE_PHOTO -> {
        val uri = FileProviderUtil.uriFromFile(context, photoFile ?: return)
        if (params.imageParams.cutAble) {
          //After the photo is taken, crop the picture
          cut(uri)
        } else {
          onPhotoReady(uri)
        }
      }
      SELECT_ALBUM -> {
        val uri = intent?.data ?: return
        if (params.imageParams.cutAble) {
          cut(uri)
        } else {
          onPhotoReady(uri)
        }
      }
      ADJUST_PHOTO -> {
        onPhotoReady(Uri.fromFile(outputFile ?: return))
      }
    }
  }

  override fun onTakePhoto() {
    val list = arrayListOf<String>()
    list.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
    list.add(android.Manifest.permission.READ_EXTERNAL_STORAGE)
    if (params.requestCameraPermission) {
      list.add(android.Manifest.permission.CAMERA)
    }
    val permissions = list.toTypedArray()
    val granted = params.permissionInvoker?.invoke(permissions) ?: false
    if (granted) {
      clickListener?.onClick(TAKE_PHOTO)
      photoFile = File(LocalStorage.composePhotoImageFile(context))
      PhotoUtil.takePhoto(params.wrapper, photoFile!!)
    } else {
      PhotoPermissionActivity.requestPermission(context, permissions) {
        if (it) {
          clickListener?.onClick(TAKE_PHOTO)
          photoFile = File(LocalStorage.composePhotoImageFile(context))
          PhotoUtil.takePhoto(params.wrapper, photoFile!!)
        } else {
          clickListener?.onClick(PERMISSION_FORBID)
          if (BuildConfig.DEBUG) {
            Log.d(TAG, context.getString(R.string.permission_storage))
          }
        }
      }
    }
  }

  override fun onSelectAlbum() {
    val list = arrayListOf<String>()
    list.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
    list.add(android.Manifest.permission.READ_EXTERNAL_STORAGE)
    if (params.requestCameraPermission) {
      list.add(android.Manifest.permission.CAMERA)
    }
    val permissions = list.toTypedArray()
    val granted = params.permissionInvoker?.invoke(permissions) ?: false
    if (granted) {
      clickListener?.onClick(SELECT_ALBUM)
      PhotoUtil.takeAlbum(params.wrapper)
    } else {
      PhotoPermissionActivity.requestPermission(context, permissions) {
        if (it) {
          clickListener?.onClick(SELECT_ALBUM)
          PhotoUtil.takeAlbum(params.wrapper)
        } else {
          clickListener?.onClick(PERMISSION_FORBID)
          if (BuildConfig.DEBUG) {
            Log.d(TAG, context.getString(R.string.permission_storage))
          }
        }
      }
    }
  }

  override fun onCancel() {
    clickListener?.onClick(CANCEL)
  }

  private fun onPhotoReady(uri: Uri) {
    if (BuildConfig.DEBUG) {
      Log.d(TAG, "outputFileUri : $uri")
    }
    val fileUrl = FileProviderUtil.getPathFromUri(context, uri)
    if (BuildConfig.DEBUG) {
      Log.d(TAG, "outputFileUrl : $fileUrl")
    }
    if (!fileUrl.isNullOrEmpty()) {
      val convertUrl = convertUrl(fileUrl)
      if (BuildConfig.DEBUG) {
        Log.d(TAG, "convertUrl : $convertUrl")
      }
      params.imageParams.onSelectListener?.onSelect(convertUrl)
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
}