package com.eye.cool.photo

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import java.io.File


/**
 * Created by cool on 2018/6/12
 */
internal object PhotoUtil {

  fun takePhoto(wrapper: ContextWrapper, outputFile: File) {
    val intent = Intent()
    intent.action = "android.media.action.IMAGE_CAPTURE"
    intent.addCategory("android.intent.category.DEFAULT")
    val uri = FileProviderUtil.uriFromFile(wrapper.activity(), outputFile)
    intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
    wrapper.startActivityForResult(intent, PhotoHelper.TAKE_PHOTO)
  }

  fun takeAlbum(wrapper: ContextWrapper) {
    val intent = Intent()
    intent.type = "image/*"
    intent.action = Intent.ACTION_PICK
    intent.data = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
    intent.addCategory("android.intent.category.DEFAULT")
    wrapper.startActivityForResult(intent, PhotoHelper.SELECT_ALBUM)
  }

  fun cut(wrapper: ContextWrapper, uri: Uri, outputFile: File, outputW: Int = 300, outputH: Int = 300) {
    val intent = Intent("com.android.camera.action.CROP")
    FileProviderUtil.setIntentDataAndType(intent, "image/*", uri, true)
    intent.putExtra("crop", true)
    intent.putExtra("aspectX", 1)
    intent.putExtra("aspectY", outputW.toFloat() / outputH.toFloat())
    intent.putExtra("outputW", outputW)
    intent.putExtra("outputH", outputH)
    //return-data为true时直接返回bitmap，会很占内存，不建议
    //裁切后保存的URI，不属于我们向外共享的，所以可以使用file://类型的URI
    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(outputFile))
    intent.putExtra("return-data", false)

    intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString())
    intent.putExtra("noFaceDetection", true)
    wrapper.startActivityForResult(intent, PhotoHelper.ADJUST_PHOTO)
  }
}