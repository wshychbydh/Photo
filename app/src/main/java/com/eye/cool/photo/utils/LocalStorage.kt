package com.eye.cool.photo.utils

import android.annotation.TargetApi
import android.content.Context
import android.content.res.AssetFileDescriptor
import android.net.Uri
import android.os.Build
import android.os.Environment
import com.eye.cool.photo.BuildConfig
import com.eye.cool.photo.support.BuildVersion
import java.io.File
import java.io.FileNotFoundException


/**
 * Created by cool on 2018/3/9.
 */
internal object LocalStorage {

  private const val PHOTO = "photo" // folder root
  private const val PHOTO_PRE = "photo_"
  private const val IMAGE_SUFFIX = ".jpeg"
  private const val THUMB = "thumb" //thumbnail catalog
  private const val THUMB_PRE = "thumb_" //thumbnail catalog

  fun getCacheDir(context: Context): File = context.externalCacheDir ?: context.cacheDir

  // create image storage dir
  fun composePhotoImageDir(context: Context): StringBuilder {
    val parent = getCacheDir(context)
    val sb = StringBuilder()
    sb.append(parent.absolutePath)
    sb.append(File.separator)
    sb.append(PHOTO)
    return sb
  }

  fun createCachedFile(context: Context, path: String): String {
    if (path.startsWith(getCacheDir(context).absolutePath)) return path
    val file = File(path)
    return try {
      val copiedFile = file.copyTo(composeCacheThumbFile(context), true)
      file.delete()
      copiedFile.absolutePath
    } catch (e: Exception) {
      if (BuildConfig.DEBUG) {
        e.printStackTrace()
      }
      path
    }
  }

  fun getExternalStorageDir(): String =
      Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).path

  // create image storage dir
  private fun composeExternalImageDir(): StringBuilder {
    val path = getExternalStorageDir()
    val sb = StringBuilder()
    sb.append(path)
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
      File(path).mkdirs()
    }
    return File.createTempFile(PHOTO_PRE, IMAGE_SUFFIX, File(sb.toString()))
  }

  /**
   * The parent folder may be null
   *
   * @return composed file path
   */
  @JvmStatic
  fun composeThumbFile(context: Context): File {
    val sb = if (BuildVersion.isBuildOverQ()) {
      composeExternalImageDir()
    } else {
      composeThumbDir(context)
    }
    val path = sb.toString()
    if (!isFileExist(context, path)) {
      File(path).mkdirs()
    }
    return File.createTempFile(THUMB_PRE, IMAGE_SUFFIX, File(sb.toString()))
  }

  @JvmStatic
  private fun composeCacheThumbFile(context: Context): File {
    val sb = composeThumbDir(context)
    val path = sb.toString()
    if (!isFileExist(context, path)) {
      File(path).mkdirs()
    }
    return File.createTempFile(THUMB_PRE, IMAGE_SUFFIX, File(sb.toString()))
  }

  private fun isFileExist(context: Context, path: String): Boolean {
    return if (BuildVersion.isBuildOverQ()) {
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
      if (BuildConfig.DEBUG) {
        e.printStackTrace()
      }
      false
    } finally {
      afd?.close()
    }
  }
}