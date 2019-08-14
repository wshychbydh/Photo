package com.eye.cool.photo.support

/**
 *Created by ycb on 2019/8/14 0014
 */
interface IClickListener {

  /**
   * @param which which the button that was clicked ({@link PhotoConstants#TAKE_PHOTO, SELECT_ALBUM, CANCEL})
   */
  fun onClicked(which: Int)
}