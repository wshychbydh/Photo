package com.eye.cool.photo

/**
 *Created by ycb on 2019/8/8 0008
 */
interface ISelectListener {

  /**
   * callback when selected image successful
   * @param imageUrl output image url
   */
  fun onSelected(imageUrl: String)
}