package com.eye.cool.photo.support

import androidx.annotation.IntDef
import com.eye.cool.photo.support.Action.CANCEL
import com.eye.cool.photo.support.Action.PERMISSION_DENIED
import com.eye.cool.photo.support.Action.SELECT_ALBUM
import com.eye.cool.photo.support.Action.TAKE_PHOTO

/**
 *Created by ycb on 2019/8/14 0014
 */
object Action {
  const val TAKE_PHOTO = 2001
  const val SELECT_ALBUM = 2002
  const val CANCEL = 2003
  const val PERMISSION_DENIED = 2020

  internal const val ADJUST_PHOTO = 1011
}

@Retention(AnnotationRetention.RUNTIME)
@IntDef(
    TAKE_PHOTO,
    SELECT_ALBUM,
    CANCEL,
    PERMISSION_DENIED
)
annotation class ActionDef