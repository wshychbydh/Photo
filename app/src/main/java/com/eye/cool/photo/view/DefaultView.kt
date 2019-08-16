package com.eye.cool.photo.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.widget.LinearLayout
import com.eye.cool.photo.R
import com.eye.cool.photo.support.OnActionListener
import kotlinx.android.synthetic.main.layout_photo.view.*

/**
 * Created by cool on 2018/6/12
 */
internal class DefaultView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) :
    LinearLayout(context, attrs, defStyle), OnClickListener {

  private var listener: OnActionListener? = null

  private fun setActionListener(listener: OnActionListener) {
    this.listener = listener
  }

  init {
    orientation = VERTICAL
    val padding = (context.resources.displayMetrics.density * 20f).toInt()
    setPadding(padding, padding, padding, padding)
    layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
    val view = LayoutInflater.from(context).inflate(R.layout.layout_photo, this, true)
    view.albumBtn.setOnClickListener(this)
    view.photoBtn.setOnClickListener(this)
    view.cancelBtn.setOnClickListener(this)
  }

  override fun onClick(v: View) {
    when (v) {
      albumBtn -> listener?.onSelectAlbum()
      photoBtn -> listener?.onTakePhoto()
      cancelBtn -> listener?.onCancel()
    }
  }
}
