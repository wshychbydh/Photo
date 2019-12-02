package com.eye.cool.photo.support

import android.app.Activity
import android.app.Fragment
import android.content.Context
import android.content.Intent

/**
 *Created by cool on 2018/6/12
 */
internal class CompatContext {

  private var fragment: Fragment? = null
  private var fragmentX: androidx.fragment.app.Fragment? = null
  private var activity: Activity? = null

  constructor(fragmentX: androidx.fragment.app.Fragment) {
    this.fragmentX = fragmentX
  }

  constructor(fragment: Fragment) {
    this.fragment = fragment
  }

  constructor(activity: Activity) {
    this.activity = activity
  }

  fun startActivityForResult(intent: Intent?, requestCode: Int) {
    when {
      fragmentX != null -> fragmentX!!.startActivityForResult(intent, requestCode)
      fragment != null -> fragment!!.startActivityForResult(intent, requestCode)
      activity != null -> activity!!.startActivityForResult(intent, requestCode)
      else -> throw IllegalStateException("CompatContext init error")
    }
  }

  fun context(): Context {
    return fragmentX?.context ?: fragment?.activity ?: activity
    ?: throw IllegalStateException("CompatContext init error")
  }

  fun activity(): Activity {
    return fragmentX?.activity ?: fragment?.activity ?: activity
    ?: throw IllegalStateException("CompatContext init error")
  }
}