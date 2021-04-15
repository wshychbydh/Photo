package com.eye.cool.photo.utils

import android.graphics.*
import androidx.annotation.WorkerThread
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.min
import kotlin.math.roundToInt


/**
 *Created by cool on 2018/7/5
 */
object ImageUtil {

  /**
   * Create rounded corner images
   *
   * [path] The path of the rounded picture
   * [bitmapSize] The output bitmap size
   */
  @JvmStatic
  @WorkerThread
  fun createCircleImage(path: String, bitmapSize: Float): Bitmap {
    val source = getBitmapFromFile(path, bitmapSize.toInt(), bitmapSize.toInt())
    return createCircleImage(source, bitmapSize)
  }

  /**
   * @param callback result in ui thread
   */
  @JvmStatic
  suspend fun createCircleImage(path: String, bitmapSize: Float, callback: (Bitmap) -> Unit) {
    val source = getBitmapFromFile(path, bitmapSize.toInt(), bitmapSize.toInt())
    val result = createCircleImage(source, bitmapSize)
    withContext(Dispatchers.Main) {
      callback.invoke(result)
    }
  }

  /**
   * @param callback result in ui thread
   */
  @JvmStatic
  suspend fun createCircleImage(source: Bitmap, bitmapSize: Float, callback: (Bitmap) -> Unit) {
    val result = createCircleImage(source, bitmapSize)
    withContext(Dispatchers.Main) {
      callback.invoke(result)
    }
  }

  /**
   * Create rounded corner images
   *
   * [source] The source of the rounded picture
   * [bitmapSize] The output bitmap size
   */
  @JvmStatic
  @WorkerThread
  fun createCircleImage(source: Bitmap, bitmapSize: Float): Bitmap {
    val matrix = Matrix()
    matrix.postScale(bitmapSize / source.width.toFloat(), bitmapSize / source.height.toFloat())
    val bitmap = Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, false)

    val paint = Paint()
    paint.isAntiAlias = true
    paint.isDither = true
    paint.isFilterBitmap = true
    val target = Bitmap.createBitmap(bitmapSize.toInt(), bitmapSize.toInt(), Bitmap.Config.ARGB_8888)
    val canvas = Canvas(target)
    canvas.drawCircle(bitmapSize / 2f, bitmapSize / 2f, bitmapSize / 2, paint)
    paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
    canvas.drawBitmap(bitmap, 0f, 0f, paint)

    return target
  }

  /**
   * Convert image to bitmap
   * If the width and height are smaller than the image itself, it will not compress
   *
   * [path] The path of picture
   * [width] output bitmap's width, default -1
   * [height] output bitmap's height, default -1
   */
  @JvmStatic
  @WorkerThread
  fun getBitmapFromFile(path: String, width: Int = -1, height: Int = -1): Bitmap {
    if (width <= 0 || height <= 0) {
      return BitmapFactory.decodeFile(path)
    }
    val options = BitmapFactory.Options()
    options.inJustDecodeBounds = true
    BitmapFactory.decodeFile(path, options)
    options.inSampleSize = getBitmapInSampleSize(width, height, options)
    options.inJustDecodeBounds = false
    return BitmapFactory.decodeFile(path, options)
  }

  /**
   * @param callback result in ui thread
   */
  @JvmStatic
  suspend fun getBitmapFromFile(path: String, width: Int = -1, height: Int = -1, callback: (Bitmap) -> Unit) {
    val result = getBitmapFromFile(path, width, height)
    withContext(Dispatchers.Main) {
      callback.invoke(result)
    }
  }

  private fun getBitmapInSampleSize(reqWidth: Int, reqHeight: Int, options: BitmapFactory.Options): Int {
    var inSampleSize = 1
    if (options.outWidth > reqWidth || options.outHeight > reqHeight) {
      val widthRatio = (options.outWidth.toFloat() / reqWidth.toFloat()).roundToInt()
      val heightRatio = (options.outHeight.toFloat() / reqHeight.toFloat()).roundToInt()
      inSampleSize = min(widthRatio, heightRatio)
    }
    return inSampleSize
  }
}