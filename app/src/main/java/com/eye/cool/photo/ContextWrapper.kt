package com.eye.cool.photo

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity

/**
 *Created by cool on 2018/6/12
 */
class ContextWrapper {

  private var activity: AppCompatActivity? = null
  private var fragment: Fragment? = null

  constructor(supportFragment: Fragment) {
    this.fragment = supportFragment
  }

  constructor(activity: AppCompatActivity) {
    this.activity = activity
  }

  fun startActivityForResult(intent: Intent?, requestCode: Int, bundle: Bundle? = null) {
    when {
      activity != null -> activity!!.startActivityForResult(intent, requestCode, bundle)
      fragment != null -> fragment!!.startActivityForResult(intent, requestCode, bundle)
      else -> throw IllegalStateException("ContextWrapper init error")
    }
  }

  fun context(): Context {
    return activity ?: fragment?.requireContext()
    ?: throw IllegalStateException("ContextWrapper init error")
  }

  fun activity(): Activity {
    return activity ?: fragment?.requireActivity()
    ?: throw IllegalStateException("ContextWrapper init error")
  }
}