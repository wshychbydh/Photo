package com.eye.cool.photo

/**
 *Created by cool on 2018/6/12
 */
internal interface IPhotoListener {

  /**
   * 拍照
   */
  fun onTakePhoto() {}

  /**
   * 从相册选取
   */
  fun onSelectAlbum() {}

  fun onCancel() {}
}