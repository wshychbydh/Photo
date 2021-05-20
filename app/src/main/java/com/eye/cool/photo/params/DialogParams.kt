package com.eye.cool.photo.params

import android.view.View
import androidx.annotation.LayoutRes
import androidx.annotation.StyleRes
import com.eye.cool.photo.R

/**
 *Created by ycb on 2019/8/8 0008
 */
class DialogParams private constructor(
    @StyleRes
    internal val themeStyle: Int?,

    @LayoutRes
    internal val contentLayoutId: Int?,
    internal val contentView: View?,  //check first

    internal val windowParams: WindowParams?
) {

  companion object {
    inline fun build(block: Builder.() -> Unit) = Builder().apply(block).build()
  }

  data class Builder(
      @StyleRes
      var themeStyle: Int? = R.style.photo_dialog,

      @LayoutRes
      var contentLayoutId: Int? = R.layout.photo_layout,
      var contentView: View? = null,

      var windowParams: WindowParams = WindowParams.Builder().build()
  ) {

    /**
     * Dialog's contentView to be shown
     * The view must have an {onActionClickListener(OnActionClickListener)} method
     *
     * [view] the shown view, If neither [contentView] nor [contentLayoutId] is set,
     * default @link {DefaultView}
     */
    fun contentView(view: View) = apply { this.contentView = view }

    /**
     * Dialog's contentView to be shown
     * The layout's child view must set tags of {album, photo, cancel}.
     * @link {Constants.TAG_ALBUM}, {Constants.TAG_PHOTO}, {Constants.TAG_CANCEl},
     *
     * [layoutId] the shown view, default[R.layout.photo_layout]
     */
    fun contentLayoutId(@LayoutRes layoutId: Int) = apply { this.contentLayoutId = layoutId }

    /**
     *  AppCompatDialog themeId
     */
    fun themeStyle(themeStyle: Int) = apply { this.themeStyle = themeStyle }

    /**
     * Dialog window params
     */
    fun windowParams(windowParams: WindowParams) = apply { this.windowParams = windowParams }

    fun build() = DialogParams(
        contentView = contentView,
        contentLayoutId = contentLayoutId,
        themeStyle = themeStyle,
        windowParams = windowParams
    )
  }
}