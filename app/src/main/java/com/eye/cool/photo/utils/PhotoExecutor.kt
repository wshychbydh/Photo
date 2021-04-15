package com.eye.cool.photo.utils

import android.content.Context
import android.content.Intent
import android.hardware.Camera
import android.net.Uri
import android.util.Log
import com.eye.cool.photo.BuildConfig
import com.eye.cool.photo.R
import com.eye.cool.photo.params.Params
import com.eye.cool.photo.support.Action
import com.eye.cool.photo.support.Action.ADJUST_PHOTO
import com.eye.cool.photo.support.Action.CANCEL
import com.eye.cool.photo.support.Action.SELECT_ALBUM
import com.eye.cool.photo.support.Action.TAKE_PHOTO
import com.eye.cool.photo.support.BuildVersion
import com.eye.cool.photo.support.CompatContext
import com.eye.cool.photo.support.Constants.TAG
import com.eye.cool.photo.support.OnActionClickListener
import com.eye.cool.photo.view.PhotoPermissionActivity
import java.io.File

/**
 *Created by cool on 2018/6/12
 */
internal class PhotoExecutor(
    private val compatContext: CompatContext,
    private val params: Params
) : OnActionClickListener {

  private var actionListener: Params.OnActionListener? = null
  private var outputFile: File? = null

  internal fun onActionClickListener(listener: Params.OnActionListener) {
    actionListener = listener
  }

  fun onActivityResult(requestCode: Int, intent: Intent?) {
    when (requestCode) {
      TAKE_PHOTO -> {
        val uri = ImageFileProvider.uriFromFile(
            compatContext.context(),
            params.authority,
            outputFile ?: return
        )
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
        if (outputFile?.exists() == true) {
          handleResultPath(outputFile!!.absolutePath)
        } else {
          onPhotoReady(intent?.data ?: return)
        }
      }
    }
  }

  override fun onTakePhoto() {
    val permissions = getPermissions(params.requestCameraPermission)
    checkPermission(permissions) {
      if (it) {
        actionListener?.onAction(TAKE_PHOTO)
        outputFile = LocalStorage.composePhotoImageFile(compatContext.context())
        try {
          PhotoUtil.takePhoto(compatContext, params.authority, outputFile!!)
        } catch (e: Exception) {
          if (BuildConfig.DEBUG) {
            e.printStackTrace()
          }
          onPermissionDenied()
        }
      } else {
        onPermissionDenied()
      }
    }
  }

  private fun onPermissionDenied() {
    actionListener?.onAction(Action.PERMISSION_DENIED)
    if (BuildConfig.DEBUG) {
      Log.d(TAG, compatContext.context().getString(R.string.photo_permission_denied))
    }
  }

  override fun onSelectAlbum() {
    val permissions = getPermissions(false)
    checkPermission(permissions) {
      if (it) {
        actionListener?.onAction(SELECT_ALBUM)
        try {
          PhotoUtil.takeAlbum(compatContext)
        } catch (e: Exception) {
          if (BuildConfig.DEBUG) {
            e.printStackTrace()
          }
          onPermissionDenied()
        }
      } else {
        onPermissionDenied()
      }
    }
  }

  private fun getPermissions(needCamera: Boolean): Array<String> {
    return if (needCamera) {
      arrayOf(
          android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
          android.Manifest.permission.READ_EXTERNAL_STORAGE,
          android.Manifest.permission.CAMERA
      )
    } else {
      arrayOf(
          android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
          android.Manifest.permission.READ_EXTERNAL_STORAGE,
      )
    }
  }

  private fun checkPermission(permissions: Array<String>, invoker: (Boolean) -> Unit) {
    if (permissions.isNullOrEmpty()) {
      invoker.invoke(true)
      return
    }
    val context = compatContext.context()
    if (BuildVersion.isTargetOverM(context) && BuildVersion.isBuildOverM()) {
      if (params.permissionInvoker == null) {
        PhotoPermissionActivity.requestPermission(compatContext.context(), permissions, invoker)
      } else {
        params.permissionInvoker!!.request(permissions, invoker)
      }
    } else {
      val hasStorage = isCacheDirAvailable(compatContext.context()) && isExternalDirAvailable()
      val hasCamera = if (params.requestCameraPermission) isCameraAvailable() else true
      invoker.invoke(hasStorage && hasCamera)
    }
  }

  private fun isCacheDirAvailable(context: Context): Boolean {
    val appFile = LocalStorage.getCacheDir(context)
    return appFile.canWrite() && appFile.canRead()
  }

  private fun isExternalDirAvailable(): Boolean {
    val file = File(LocalStorage.getExternalStorageDir())
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
    actionListener?.onAction(CANCEL)
  }

  private fun onPhotoReady(uri: Uri) {
    if (BuildConfig.DEBUG) {
      Log.d(TAG, "outputFileUri : $uri")
    }
    val fileUrl = ImageFileProvider.getPathFromUri(compatContext.context(), uri)
    if (BuildConfig.DEBUG) {
      Log.d(TAG, "outputFileUrl : $fileUrl")
    }
    if (!fileUrl.isNullOrEmpty()) {
      handleResultPath(fileUrl)
    }
  }

  private fun handleResultPath(path: String) {
    val convertUrl = convertUrl(path)
    val result = LocalStorage.createCachedFile(compatContext.context(), convertUrl)
    if (BuildConfig.DEBUG) {
      Log.d(TAG, "convertUrl : $result")
    }
    compatContext.activity().runOnUiThread {
      params.onSelectListener?.onSelect(result)
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
    PhotoUtil.cut(
        compatContext,
        uri,
        outputFile!!,
        params.imageParams.outputW,
        params.imageParams.outputH
    )

    if (BuildConfig.DEBUG) {
      Log.d(TAG, "cutFileUri: $uri")
      Log.d(TAG, "outputFile: ${outputFile?.absolutePath} ; exists:${outputFile?.exists()}")
    }
  }
}