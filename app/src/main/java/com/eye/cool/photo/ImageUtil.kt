package com.eye.cool.photo

import android.graphics.*
import android.support.annotation.WorkerThread


/**
 *Created by cool on 2018/7/5
 */
object ImageUtil {

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

  @JvmStatic
  @WorkerThread
  fun getBitmap(path: String, width: Int = -1, height: Int = -1): Bitmap {
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

  private fun getBitmapInSampleSize(reqWidth: Int, reqHeight: Int, options: BitmapFactory.Options): Int {
    var inSampleSize = 1
    if (options.outWidth > reqWidth || options.outHeight > reqHeight) {
      val widthRatio = Math.round(options.outWidth.toFloat() / reqWidth.toFloat())
      val heightRatio = Math.round(options.outHeight.toFloat() / reqHeight.toFloat())
      inSampleSize = Math.min(widthRatio, heightRatio)
    }
    return inSampleSize
  }
}