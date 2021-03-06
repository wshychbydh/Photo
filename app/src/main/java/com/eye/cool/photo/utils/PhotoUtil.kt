package com.eye.cool.photo.utils

import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import com.eye.cool.photo.support.Action
import com.eye.cool.photo.support.CompatContext
import java.io.File


/**
 * Created by cool on 2018/6/12
 */
internal object PhotoUtil {

  /**
   * take a photo
   * [wrapper] context
   * [outputFile] The path for photo output
   */
  @JvmStatic
  fun takePhoto(wrapper: CompatContext, authority: String?, outputFile: File) {
    val intent = Intent()
    intent.action = "android.media.action.IMAGE_CAPTURE"
    intent.addCategory("android.intent.category.DEFAULT")
    val uri = ImageFileProvider.uriFromFile(wrapper.context(), authority, outputFile)
    intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
    wrapper.startActivityForResult(intent, Action.TAKE_PHOTO)
  }

  /**
   * select image from album
   * [wrapper] context
   */
  @JvmStatic
  fun takeAlbum(wrapper: CompatContext) {
    val intent = Intent()
    intent.type = "image/*"
    intent.action = Intent.ACTION_PICK
    intent.data = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
    intent.addCategory("android.intent.category.DEFAULT")
    wrapper.startActivityForResult(intent, Action.SELECT_ALBUM)
  }

  /**
   * Shear pictures
   * [wrapper] context
   * [uri] The image uri to be clipped
   * [outputFile] The path for clipped image output
   * [outputW] output width, default 300px
   * [outputH] output height, default 300px
   */
  @JvmStatic
  fun cut(wrapper: CompatContext, uri: Uri, outputFile: File, outputW: Int = 300, outputH: Int = 300) {
    val intent = Intent("com.android.camera.action.CROP")
    ImageFileProvider.setIntentDataAndType(intent, "image/*", uri, true)
    intent.putExtra("crop", true)
    intent.putExtra("aspectX", 1)
    intent.putExtra("aspectY", outputW.toFloat() / outputH.toFloat())
    intent.putExtra("outputW", outputW)
    intent.putExtra("outputH", outputH)
    //When return-data is true, it directly returns bitmap, which will occupy a lot of memory. It is not recommended
    //The URI saved after cutting is not one we share outwards, so we can use the URI of type file://
    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(outputFile))
    intent.putExtra("return-data", false)

    intent.putExtra("noFaceDetection", true)
    wrapper.startActivityForResult(intent, Action.ADJUST_PHOTO)
  }
}