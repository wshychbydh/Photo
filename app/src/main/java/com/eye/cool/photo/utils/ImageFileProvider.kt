package com.eye.cool.photo.utils

import android.annotation.TargetApi
import android.content.*
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.StrictMode
import android.provider.DocumentsContract
import android.provider.MediaStore
import androidx.core.content.FileProvider
import com.eye.cool.photo.support.BuildVersion
import java.io.*

/**
 * Created by cool on 2018/6/12
 */
object ImageFileProvider {
  /**
   * Get the URI from the file
   *
   * [context]
   * [file] A {@link File} pointing to the filename for which you want a
   * <code>content</code> {@link Uri}.
   */
  @JvmStatic
  fun uriFromFile(context: Context, file: File): Uri {
    return uriFromFile(context, composeAuthority(context), file)
  }

  /**
   * Get the URI from the file
   *
   * [context]
   * [authority] The authority of a {@link FileProvider} defined in a
   *            {@code <provider>} element in your app's manifest.
   * [file] A {@link File} pointing to the filename for which you want a
   * <code>content</code> {@link Uri}.
   */
  @JvmStatic
  fun uriFromFile(context: Context, authority: String?, file: File): Uri {
    return if (BuildVersion.isBuildOverN()) {
      FileProvider.getUriForFile(context, authority ?: composeAuthority(context), file)
    } else {
      Uri.fromFile(file)
    }
  }

  private fun composeAuthority(context: Context): String {
    return context.packageName + ".Photo.FileProvider"
  }

  /**
   * Grant temporary access to files
   *
   * [context]
   * [intent] The intent to access the file
   * [uri]
   */
  @JvmStatic
  fun grantUriPermission(context: Context, intent: Intent, uri: Uri): Uri {
    if (BuildVersion.isBuildOverN()) {
      val result = context.packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
      result.forEach {
        context.grantUriPermission(it.activityInfo.packageName, uri,
            Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
      }
    }
    return uri
  }

  /**
   * Grant temporary access to files
   *
   * [context]
   * [intent] The intent to access the file
   * [file] The file to be granted
   */
  @JvmStatic
  fun grantUriPermission(context: Context, intent: Intent, file: File): Uri {
    val uri = uriFromFile(context, file)
    return grantUriPermission(context, intent, uri)
  }

  /**
   * Sets the data and type of the Intent and gives the target temporary URI read and write permissions
   *
   * [context]
   * [intent] The intent to access the file
   * [type] The MIME type of the data being handled by this intent.
   * [file] The file to be granted
   * [writeAble] Whether to grant permissions to writable uris
   */
  @JvmStatic
  fun setIntentDataAndType(context: Context, intent: Intent, type: String, file: File, writeAble: Boolean) {
    if (BuildVersion.isBuildOverN()) {
      intent.setDataAndType(uriFromFile(context, file), type)
      //Temporarily grant read and write Uri permissions
      intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
      if (writeAble) {
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
      }
    } else {
      intent.setDataAndType(Uri.fromFile(file), type)
    }
  }

  /**
   * Sets the data and type of the Intent and gives the target temporary URI read and write permissions
   *
   * [intent]
   * [type] The MIME type of the data being handled by this intent.
   * [fileUri] The file's uri to be granted
   * [writeAble] Whether to grant permissions to writable uris
   */
  @JvmStatic
  fun setIntentDataAndType(intent: Intent, type: String, fileUri: Uri, writeAble: Boolean) {
    if (BuildVersion.isBuildOverN()) {
      intent.setDataAndType(fileUri, type)
      //Temporarily grant read and write Uri permissions
      intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
      if (writeAble) {
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
      }
    } else {
      intent.setDataAndType(fileUri, type)
    }
  }

  /**
   * Get the absolute path to the image based on its Uri (multiple apis have been adapted)
   * If the image corresponding to the Uri exists,
   * then the absolute path to the image is returned,
   * otherwise null is returned
   *
   * [context]
   * [uri] The file's uri to parse
   *
   */
  @JvmStatic
  fun getPathFromUri(context: Context, uri: Uri): String? {
    return when {
      ContentResolver.SCHEME_FILE == uri.scheme -> uri.path

      BuildVersion.isBuildBelowH() -> {
        return getRealPathFromUriBelowApi11(context, uri)   // SDK < Api11
      }
      BuildVersion.isBuildBelowK() -> {
        getRealPathFromUriApi11To18(context, uri) // SDK > 11 && SDK < 19
      }
      else -> getRealPathFromUriAboveApi19(context, uri) // SDK > 19
    }
  }

  /**
   * Api19 above, according to the uri to get the absolute path of the picture
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
          // Use ':' to split
          val id = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1]

          if (BuildVersion.isBuildOverQ()) {
            return getMediaPathAboveQ(id)
          }

          val projection = arrayOf(MediaStore.Images.Media.DATA)
          val selection = MediaStore.Images.Media._ID + "=?"
          val selectionArgs = arrayOf(id)

          val cursor = context.contentResolver.query(
              MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
              projection,
              selection,
              selectionArgs,
              null
          ) ?: return null

          cursor.use {
            if (it.moveToFirst()) {
              return cursor.getString(cursor.getColumnIndexOrThrow(projection[0]))
            }
          }

          return null
        }
      } else {
        return getRealPathFromUriBelowApi11(context, uri)
      }
    }
    return null
  }

  @TargetApi(Build.VERSION_CODES.Q)
  private fun getMediaPathAboveQ(id: String): String {
    return MediaStore.Images.Media
        .EXTERNAL_CONTENT_URI
        .buildUpon()
        .appendPath(id).build().toString()
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
   * Api11-api18, which gets the absolute path of the image based on the uri
   */
  private fun getRealPathFromUriApi11To18(context: Context, uri: Uri): String? {
    val projection = arrayOf(MediaStore.Images.Media.DATA)

    val loader = CursorLoader(context, uri, projection, null, null, null)
    val cursor = loader.loadInBackground()
    cursor?.use {
      if (it.moveToFirst()) {
        return cursor.getString(0)
      }
    }
    return null
  }

  /**
   * Api11 is applied below (excluding api11), and the absolute path of the image is obtained according to the uri
   */
  private fun getRealPathFromUriBelowApi11(context: Context, uri: Uri): String? {
    val projection = arrayOf(MediaStore.Images.Media.DATA)
    val cursor = context.contentResolver.query(uri, projection, null, null, null)
    cursor?.use {
      if (it.moveToFirst()) {
        return it.getString(0)
      }
    }
    return null
  }

  /**
   * Detect when the calling application exposes a {@code file://} {@link android.net.Uri}
   * to another app.
   *
   * <p>This exposure is discouraged since the receiving app may not have access to the
   * shared path. For example, the receiving app may not have requested the {@link
   * android.Manifest.permission#READ_EXTERNAL_STORAGE} runtime permission, or the
   * platform may be sharing the {@link android.net.Uri} across user profile boundaries.
   *
   * <p>Instead, apps should use {@code content://} Uris so the platform can extend
   * temporary permission for the receiving app to access the resource.
   *
   * @see android.support.v4.content.FileProvider
   * @see Intent#FLAG_GRANT_READ_URI_PERMISSION
   */
  @Deprecated("Use file uri instead.")
  @JvmStatic
  fun detectFileUriExposure() {
    val builder = StrictMode.VmPolicy.Builder()
    StrictMode.setVmPolicy(builder.build())
    if (BuildVersion.isBuildOverN()) {
      builder.detectFileUriExposure()
    }
  }
}