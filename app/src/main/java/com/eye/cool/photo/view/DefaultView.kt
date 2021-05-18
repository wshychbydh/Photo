package com.eye.cool.photo.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import com.eye.cool.photo.R
import com.eye.cool.photo.support.OnActionClickListener

/**
 * Created by cool on 2018/6/12
 */
internal class DefaultView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : LinearLayout(context, attrs, defStyle), View.OnClickListener {

  private var listener: OnActionClickListener? = null

  //necessary
  private fun onActionClickListener(listener: OnActionClickListener) {
    this.listener = listener
  }

  init {
    orientation = VERTICAL
    val padding = (context.resources.displayMetrics.density * 20f).toInt()
    setPadding(padding, padding, padding, padding)
    layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
    val view = LayoutInflater.from(context).inflate(R.layout.photo_layout, this, true)
    view.findViewById<View>(R.id.albumBtn).setOnClickListener(this)
    view.findViewById<View>(R.id.photoBtn).setOnClickListener(this)
    view.findViewById<View>(R.id.cancelBtn).setOnClickListener(this)
  }

  override fun onClick(v: View) {
    when (v.id) {
      R.id.albumBtn -> listener?.onSelectAlbum()
      R.id.photoBtn -> listener?.onTakePhoto()
      R.id.cancelBtn -> listener?.onCancel()
    }
  }
}
