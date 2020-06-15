package com.eye.cool.photo.utils

import android.annotation.TargetApi
import android.content.Context
import android.content.res.AssetFileDescriptor
import android.net.Uri
import android.os.Build
import android.os.Environment
import java.io.File
import java.io.FileNotFoundException


/**
 * Created by cool on 2018/3/9.
 */
internal object LocalStorage {

  private const val PHOTO = "photo" // folder root
  private const val PHOTO_PRE = "photo_"
  private const val IMAGE_SUFFIX = ".jpg"
  private const val THUMB = "thumb" //thumbnail catalog
  private const val LOG = "log"

  // create image storage dir
  fun composePhotoImageDir(context: Context): StringBuilder {
    val parent = context.externalCacheDir
        ?: context.getExternalFilesDir(Environment.DIRECTORY_DCIM)
        ?: throw IllegalStateException("Invalid storage path!")
    val sb = StringBuilder()
    sb.append(parent.absolutePath)
    sb.append(File.separator)
    sb.append(PHOTO)
    return sb
  }

  // create image storage dir
  private fun composeThumbDir(context: Context): StringBuilder {
    return composePhotoImageDir(context).append(File.separator).append(THUMB)
  }

  /**
   * The parent folder may be null
   *
   * @return composed file path
   */
  @JvmStatic
  fun composePhotoImageFile(context: Context): File {
    val sb = composePhotoImageDir(context)
    val path = sb.toString()
    if (!isFileExist(context, path)) {
      File(path).mkdirs() //fixme likely to fail
    }
    sb.append(File.separator).append("$PHOTO_PRE${System.currentTimeMillis()}$IMAGE_SUFFIX")
    return File(sb.toString())
  }

  /**
   * The parent folder may be null
   *
   * @return composed file path
   */
  @JvmStatic
  fun composeThumbFile(context: Context): File {
    val sb = composeThumbDir(context)
    val path = sb.toString()
    if (!isFileExist(context, path)) {
      File(path).mkdirs() //fixme likely to fail
    }
    sb.append(File.separator).append("$PHOTO_PRE${System.currentTimeMillis()}$IMAGE_SUFFIX")
    return File(sb.toString())
  }

  private fun isFileExist(context: Context, path: String): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
      isFileExistsAboveQ(context, path)
    } else {
      File(path).exists()
    }
  }

  @TargetApi(Build.VERSION_CODES.Q)
  private fun isFileExistsAboveQ(context: Context, path: String): Boolean {
    var afd: AssetFileDescriptor? = null
    val cr = context.contentResolver
    return try {
      val afd = cr.openAssetFileDescriptor(Uri.parse(path), "r")
      if (afd == null) {
        false
      } else {
        afd.close()
        true
      }
    } catch (e: FileNotFoundException) {
      false
    } finally {
      afd?.close()
    }
  }
}