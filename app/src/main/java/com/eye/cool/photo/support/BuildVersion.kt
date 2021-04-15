package com.eye.cool.photo.support

import android.content.Context
import android.os.Build

/**
 * Created by cool on 2021/4/15.
 */
internal object BuildVersion {

  /**
   * android 10.0
   */
  fun isBuildOverQ() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q

  fun isBuildOverN() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.N

  fun isBuildOverLOLLIPOP() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP

  fun isBuildBelowH() = Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB

  fun isBuildBelowK() = Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT


  fun isBuildOverM() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
  fun isTargetOverM(context: Context) =
      context.applicationInfo.targetSdkVersion >= Build.VERSION_CODES.M
}