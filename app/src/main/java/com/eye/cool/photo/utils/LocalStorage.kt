package com.eye.cool.photo.utils

import android.content.Context
import java.io.File

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
    val sb = StringBuilder()
    sb.append(context.externalCacheDir)
    sb.append(File.separator)
    sb.append(PHOTO)
    return sb
  }

  // create image storage dir
  private fun composeThumbDir(context: Context): StringBuilder {
    return composePhotoImageDir(context).append(File.separator).append(THUMB)
  }

  @JvmStatic
  fun composePhotoImageFile(context: Context): File {
    val sb = composePhotoImageDir(context)
    val dir = File(sb.toString())
    if (!dir.exists()) dir.mkdirs()
    sb.append(File.separator).append("$PHOTO_PRE${System.currentTimeMillis()}$IMAGE_SUFFIX")
    return File(sb.toString())
  }

  @JvmStatic
  fun composeThumbFile(context: Context): File {
    val sb = composeThumbDir(context)
    val dir = File(sb.toString())
    if (!dir.exists()) dir.mkdirs()
    sb.append(File.separator).append("$PHOTO_PRE${System.currentTimeMillis()}$IMAGE_SUFFIX")
    return File(sb.toString())
  }
}