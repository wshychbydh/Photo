package com.eye.cool.photo

import android.annotation.TargetApi
import android.content.*
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.support.v4.content.FileProvider
import java.io.File


/**
 * Created by cool on 2018/6/12
 */
object FileProviderUtil {
  /**
   * 从文件获得URI
   *
   * @param context 上下文
   * @param file    文件
   * @return 文件对应的URI
   */
  fun uriFromFile(context: Context, file: File): Uri {
    //7.0以上进行适配
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
      FileProvider.getUriForFile(context, composeAuthority(context), file)
    } else {
      Uri.fromFile(file)
    }
  }

  private fun composeAuthority(context: Context): String {
    return context.packageName + ".FileProvider"
  }

  fun grantUriPermission(context: Context, intent: Intent, file: File): Uri {
    val uri = uriFromFile(context, file)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
      val result = context.packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
      result.forEach {
        context.grantUriPermission(it.activityInfo.packageName, uri,
            Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
      }
    }
    return uri
  }

  /**
   * 设置Intent的data和类型，并赋予目标程序临时的URI读写权限
   *
   * @param context   上下文
   * @param intent    意图
   * @param type      类型
   * @param file      文件
   * @param writeAble 是否赋予可写URI的权限
   */
  fun setIntentDataAndType(context: Context, intent: Intent, type: String, file: File, writeAble: Boolean) {
    //7.0以上进行适配
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
      intent.setDataAndType(uriFromFile(context, file), type)
      //临时赋予读写Uri的权限
      intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
      if (writeAble) {
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
      }
    } else {
      intent.setDataAndType(Uri.fromFile(file), type)
    }
  }

  /**
   * 设置Intent的data和类型，并赋予目标程序临时的URI读写权限
   *
   * @param intent    意图
   * @param type      类型
   * @param fileUri   文件uri
   * @param writeAble 是否赋予可写URI的权限
   */
  fun setIntentDataAndType(intent: Intent, type: String, fileUri: Uri, writeAble: Boolean) {
    //7.0以上进行适配
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
      intent.setDataAndType(fileUri, type)
      //临时赋予读写Uri的权限
      intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
      if (writeAble) {
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
      }
    } else {
      intent.setDataAndType(fileUri, type)
    }
  }

  /**
   * 根据图片的Uri获取图片的绝对路径(已经适配多种API)
   * @return 如果Uri对应的图片存在,那么返回该图片的绝对路径,否则返回null
   */
  fun getPathFromUri(context: Context, uri: Uri): String? {
    if (ContentResolver.SCHEME_FILE == uri.scheme) {
      return uri.path
    } else {
      val sdkVersion = Build.VERSION.SDK_INT
      if (sdkVersion < 11) {
        // SDK < Api11
        return getRealPathFromUriBelowApi11(context, uri)
      }
      return if (sdkVersion < 19) {
        // SDK > 11 && SDK < 19
        getRealPathFromUriApi11To18(context, uri)
      } else getRealPathFromUriAboveApi19(context, uri)
      // SDK > 19
    }
  }

  /**
   * 适配api19以上,根据uri获取图片的绝对路径
   */
  @TargetApi(19)
  private fun getRealPathFromUriAboveApi19(context: Context, uri: Uri): String? {
    if (ContentResolver.SCHEME_CONTENT == uri.scheme) {
      if (isCustomAuthority(context, uri)) {
        val path = uri.path ?: return null
        val name = path.substring(path.lastIndexOf("/"))
        return LocalStorage.composePhotoImageDir(context).append(name).toString()
      } else if (DocumentsContract.isDocumentUri(context, uri)) {
        val docId = DocumentsContract.getDocumentId(uri) ?: return null
        if (isExternalStorageDocument(uri)) {
          val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
          if ("primary".equals(split[0], ignoreCase = true)) {
            return "${Environment.getExternalStorageDirectory()}/" + split[1]
          }
        } else if (isDownloadsDocument(uri)) {
          val contentUri = ContentUris.withAppendedId(
              Uri.parse("content://downloads/public_downloads"), docId.toLong())
          return getRealPathFromUriBelowApi11(context, contentUri)
        } else if (isMediaDocument(uri)) {
          var filePath: String? = null
          // 使用':'分割
          val id = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1]

          val projection = arrayOf(MediaStore.Images.Media.DATA)
          val selection = MediaStore.Images.Media._ID + "=?"
          val selectionArgs = arrayOf(id)

          val cursor = context.contentResolver.query(
              MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection,
              selection, selectionArgs, null)
          val columnIndex = cursor!!.getColumnIndex(projection[0])

          if (cursor.moveToFirst()) {
            filePath = cursor.getString(columnIndex)
          }
          cursor.close()
          return filePath
        }
      } else {
        return getRealPathFromUriBelowApi11(context, uri)
      }
    }
    return null
  }

  private fun isCustomAuthority(context: Context, uri: Uri): Boolean {
    return composeAuthority(context) == uri.authority
  }

  private fun isExternalStorageDocument(uri: Uri): Boolean {
    return "com.android.externalstorage.documents" == uri.authority
  }

  private fun isDownloadsDocument(uri: Uri): Boolean {
    return "com.android.providers.downloads.documents" == uri.authority
  }

  private fun isMediaDocument(uri: Uri): Boolean {
    return "com.android.providers.media.documents" == uri.authority
  }

  /**
   * 适配api11-api18,根据uri获取图片的绝对路径
   */
  private fun getRealPathFromUriApi11To18(context: Context, uri: Uri): String? {
    var filePath: String? = null
    val projection = arrayOf(MediaStore.Images.Media.DATA)

    val loader = CursorLoader(context, uri, projection, null, null, null)
    val cursor = loader.loadInBackground()

    if (cursor != null) {
      cursor.moveToFirst()
      filePath = cursor.getString(0)
      cursor.close()
    }
    return filePath
  }

  /**
   * 适配api11以下(不包括api11),根据uri获取图片的绝对路径
   */
  private fun getRealPathFromUriBelowApi11(context: Context, uri: Uri): String? {
    val projection = arrayOf(MediaStore.Images.Media.DATA)
    val cursor = context.contentResolver.query(uri, projection, null, null, null)
    cursor?.use {
      cursor.moveToFirst()
      return it.getString(0)
    }
    return null
  }
}