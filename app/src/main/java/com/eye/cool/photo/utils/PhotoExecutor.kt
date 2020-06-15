package com.eye.cool.photo.utils

import android.content.Context
import android.content.Intent
import android.hardware.Camera
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.util.Log
import com.eye.cool.photo.BuildConfig
import com.eye.cool.photo.R
import com.eye.cool.photo.params.DialogParams
import com.eye.cool.photo.params.Params
import com.eye.cool.photo.support.CompatContext
import com.eye.cool.photo.support.Constants.ADJUST_PHOTO
import com.eye.cool.photo.support.Constants.CANCEL
import com.eye.cool.photo.support.Constants.PERMISSION_FORBID
import com.eye.cool.photo.support.Constants.SELECT_ALBUM
import com.eye.cool.photo.support.Constants.TAG
import com.eye.cool.photo.support.Constants.TAKE_PHOTO
import com.eye.cool.photo.support.OnActionListener
import com.eye.cool.photo.view.PhotoPermissionActivity
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File

/**
 *Created by cool on 2018/6/12
 */
internal class PhotoExecutor(
    private val compatContext: CompatContext,
    private val params: Params
) : OnActionListener {

  private var clickListener: DialogParams.OnClickListener? = null
  private var outputFile: File? = null
  private var photoFile: File? = null

  internal fun setOnClickListener(listener: DialogParams.OnClickListener) {
    clickListener = listener
  }

  fun onActivityResult(requestCode: Int, intent: Intent?) {
    GlobalScope.launch {
      when (requestCode) {
        TAKE_PHOTO -> {
          val uri = ImageFileProvider.uriFromFile(
              compatContext.context(),
              params.authority,
              photoFile ?: return@launch
          )
          if (params.imageParams.cutAble) {
            //After the photo is taken, crop the picture
            cut(uri)
          } else {
            onPhotoReady(uri)
          }
        }
        SELECT_ALBUM -> {
          val uri = intent?.data ?: return@launch
          if (params.imageParams.cutAble) {
            cut(uri)
          } else {
            onPhotoReady(uri)
          }
        }
        ADJUST_PHOTO -> {
          onPhotoReady(Uri.fromFile(outputFile ?: return@launch))
        }
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
    checkPermission(list.toTypedArray()) {
      if (it) {
        clickListener?.onClick(TAKE_PHOTO)
        photoFile = LocalStorage.composePhotoImageFile(compatContext.context())
        PhotoUtil.takePhoto(compatContext, params.authority, photoFile!!)
      } else {
        clickListener?.onClick(PERMISSION_FORBID)
        if (BuildConfig.DEBUG) {
          Log.d(TAG, compatContext.context().getString(
              if (params.requestCameraPermission)
                R.string.photo_permission_storage_and_camera
              else
                R.string.photo_permission_storage
          ))
        }
      }
    }
  }

  override fun onSelectAlbum() {
    val permissions = arrayOf(
        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
        android.Manifest.permission.READ_EXTERNAL_STORAGE
    )
    checkPermission(permissions) {
      if (it) {
        clickListener?.onClick(SELECT_ALBUM)
        PhotoUtil.takeAlbum(compatContext)
      } else {
        clickListener?.onClick(PERMISSION_FORBID)
        if (BuildConfig.DEBUG) {
          Log.d(TAG, compatContext.context().getString(R.string.photo_permission_storage))
        }
      }
    }
  }

  private fun checkPermission(permissions: Array<String>, invoker: (Boolean) -> Unit) {
    val target = compatContext.context().applicationInfo.targetSdkVersion
    if (target >= Build.VERSION_CODES.M && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      if (params.permissionInvoker == null) {
        PhotoPermissionActivity.requestPermission(compatContext.context(), permissions, invoker)
      } else {
        params.permissionInvoker!!.request(permissions, invoker)
      }
    } else {
      val checkStorage = isCacheDirAvailable(compatContext.context()) && isExternalDirAvailable(compatContext.context())
      val checkCamera = if (params.requestCameraPermission) isCameraAvailable() else true
      invoker.invoke(checkStorage && checkCamera)
    }
  }

  private fun isCacheDirAvailable(context: Context): Boolean {
    val appFile = context.externalCacheDir ?: return false
    return appFile.canWrite() && appFile.canRead()
  }

  private fun isExternalDirAvailable(context: Context): Boolean {
    val file = context.getExternalFilesDir(Environment.DIRECTORY_DCIM)
    return file != null && file.canWrite() && file.canRead()
  }

  private fun isCameraAvailable(): Boolean {
    var camera: Camera? = null
    return try {
      camera = Camera.open()
      // setParameters is Used for MeiZu MX5.
      camera!!.parameters = camera!!.parameters
      true
    } catch (e: Exception) {
      false
    } finally {
      try {
        camera?.release()
      } catch (ignore: Exception) {
      }
    }
  }

  override fun onCancel() {
    clickListener?.onClick(CANCEL)
  }

  private suspend fun onPhotoReady(uri: Uri) {
    if (BuildConfig.DEBUG) {
      Log.d(TAG, "outputFileUri : $uri")
    }
    val fileUrl = ImageFileProvider.getPathFromUri(compatContext.context(), uri)
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
    outputFile = LocalStorage.composeThumbFile(compatContext.context())
    outputFile!!.createNewFile()
    PhotoUtil.cut(
        compatContext,
        uri,
        outputFile!!,
        params.imageParams.outputW,
        params.imageParams.outputH
    )

    if (BuildConfig.DEBUG) {
      Log.d(TAG, "cutFileUrl : ${ImageFileProvider.getPathFromUri(compatContext.context(), uri)}")
    }
  }
}