package com.eye.cool.photo.view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import com.eye.cool.photo.R
import com.eye.cool.photo.support.OnActionClickListener

/**
 * Created by cool on 2018/6/12
 */
internal class DefaultView(context: Context) : View.OnClickListener {

  private var listener: OnActionClickListener? = null

  val view: View = LayoutInflater.from(context).inflate(R.layout.photo_layout, null)

  //necessary
  private fun onActionClickListener(listener: OnActionClickListener) {
    this.listener = listener
  }

  init {
    view.findViewById<View>(R.id.album_btn).setOnClickListener(this)
    view.findViewById<View>(R.id.photo_btn).setOnClickListener(this)
    view.findViewById<View>(R.id.cancel_btn).setOnClickListener(this)
  }

  override fun onClick(v: View) {
    when (v.id) {
      R.id.album_btn -> listener?.onSelectAlbum()
      R.id.photo_btn -> listener?.onTakePhoto()
      R.id.cancel_btn -> listener?.onCancel()
    }
  }
}
