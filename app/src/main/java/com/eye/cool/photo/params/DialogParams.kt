package com.eye.cool.photo.params

import android.content.DialogInterface
import android.content.res.Resources
import android.view.View
import androidx.annotation.StyleRes
import com.eye.cool.photo.R

/**
 *Created by ycb on 2019/8/8 0008
 */
class DialogParams private constructor(
    internal val contentView: View?,

    @StyleRes
    internal val themeStyle: Int?,

    internal val cancelable: Boolean,
    internal val canceledOnTouchOutside: Boolean,

    internal val xPos: Int,
    internal val yPos: Int,

    @StyleRes
    internal val windowAnimations: Int?,

    internal val gravity: Int?,
    internal val width: Int?,
    internal val height: Int?,

    internal val dimAmount: Float?,
    internal val alpha: Float?,

    internal val horizontalMargin: Float?,
    internal val verticalMargin: Float?,

    internal val systemUiVisibility: Int?,
    internal val softInputMode: Int?,

    internal val onShowListener: DialogInterface.OnShowListener?,
    internal val onDismissListener: DialogInterface.OnDismissListener?,
    internal val onCancelListener: DialogInterface.OnCancelListener?,
    internal val onKeyListener: DialogInterface.OnKeyListener?
) {

  companion object {
    inline fun build(block: Builder.() -> Unit) = Builder().apply(block).build()
  }

  data class Builder(
      var contentView: View? = null,

      @StyleRes
      var themeStyle: Int? = R.style.photo_dialog,

      var cancelable: Boolean = true,
      var canceledOnTouchOutside: Boolean = true,

      var xPos: Int = 0,
      var yPos: Int = Resources.getSystem().displayMetrics.heightPixels,

      @StyleRes
      var windowAnimations: Int? = R.style.photo_anim_bottom,

      var gravity: Int? = null,
      var width: Int? = null,
      var height: Int? = null,

      var dimAmount: Float? = null,
      var alpha: Float? = null,

      var horizontalMargin: Float? = null,
      var verticalMargin: Float? = null,

      var systemUiVisibility: Int? = null,
      var softInputMode: Int? = null,

      var onShowListener: DialogInterface.OnShowListener? = null,
      var onDismissListener: DialogInterface.OnDismissListener? = null,
      var onCancelListener: DialogInterface.OnCancelListener? = null,
      var onKeyListener: DialogInterface.OnKeyListener? = null
  ) {

    /**
     * Dialog's contentView to be shown
     * The view must have an {onActionClickListener(OnActionClickListener)} method
     *
     * [view] the shown view, default[R.layout.photo_layout]
     */
    fun contentView(view: View) = apply { this.contentView = view }

    /**
     * Sets whether this dialog is cancelable with the
     * {@link KeyEvent#KEYCODE_BACK BACK} key.
     *
     * [cancelable] default true
     */
    fun cancelable(cancelable: Boolean) = apply { this.cancelable = cancelable }

    /**
     * Sets whether this dialog is canceled when touched outside the window's
     * bounds. If setting to true, the dialog is set to be cancelable if not
     * already set.
     *
     * [cancelable] Whether the dialog should be canceled when touched outside the window.
     * default true
     */
    fun canceledOnTouchOutside(cancelable: Boolean) = apply {
      this.canceledOnTouchOutside = cancelable
    }

    /**
     * A style resource describing the theme to use for the window,
     * or {@code 0} to use the default dialog theme
     *
     * [themeStyle] default [R.style.photo_dialog]
     */
    fun themeStyle(themeStyle: Int) = apply { this.themeStyle = themeStyle }

    /**
     * Sets a listener to be invoked when the dialog is shown.
     *
     * [listener] The {@link DialogInterface.OnShowListener} to use.
     */
    fun onShowListener(listener: DialogInterface.OnShowListener) = apply {
      this.onShowListener = listener
    }

    /**
     * Set a listener to be invoked when the dialog is dismissed.
     *
     * [listener] The {@link DialogInterface.OnDismissListener} to use.
     */
    fun onDismissListener(listener: DialogInterface.OnDismissListener) = apply {
      this.onDismissListener = listener
    }

    /**
     * Set a listener to be invoked when the dialog is canceled.
     *
     * <p>This will only be invoked when the dialog is canceled.
     * Cancel events alone will not capture all ways that
     * the dialog might be dismissed. If the creator needs
     * to know when a dialog is dismissed in general, use
     * {@link #setOnDismissListener}.</p>
     *
     * [listener] The {@link DialogInterface.OnCancelListener} to use.
     */
    fun onCancelListener(listener: DialogInterface.OnCancelListener) = apply {
      this.onCancelListener = listener
    }

    fun onKeyListener(listener: DialogInterface.OnKeyListener) = apply {
      this.onKeyListener = listener
    }

    /**
     * Placement of window within the screen as per {@link Gravity}.  Both
     * {@link Gravity#apply(int, int, int, android.graphics.Rect, int, int,
     * android.graphics.Rect) Gravity.apply} and
     * {@link Gravity#applyDisplay(int, android.graphics.Rect, android.graphics.Rect)
     * Gravity.applyDisplay} are used during window layout, with this value
     * given as the desired gravity.  For example you can specify
     * {@link Gravity#DISPLAY_CLIP_HORIZONTAL Gravity.DISPLAY_CLIP_HORIZONTAL} and
     * {@link Gravity#DISPLAY_CLIP_VERTICAL Gravity.DISPLAY_CLIP_VERTICAL} here
     * to control the behavior of
     * {@link Gravity#applyDisplay(int, android.graphics.Rect, android.graphics.Rect)
     * Gravity.applyDisplay}.
     *
     * [gravity] default null
     */
    fun gravity(gravity: Int) = apply { this.gravity = gravity }

    /**
     * When {@link #FLAG_DIM_BEHIND} is set, this is the amount of dimming
     * to apply.  Range is from 1.0 for completely opaque to 0.0 for no dim.
     *
     * [dimAmount] default by themeStyle's config
     */
    fun dimAmount(dimAmount: Float) = apply { this.dimAmount = dimAmount }

    /**
     * @see {WindowManager.horizontalMargin}
     *
     * [margin] default null
     */
    fun horizontalMargin(margin: Float) = apply { this.horizontalMargin = margin }

    /**
     * @see {WindowManager.verticalMargin}
     *
     * [margin] default null
     */
    fun verticalMargin(margin: Float) = apply { this.verticalMargin = margin }

    /**
     * @see {WindowManager.systemUiVisibility}
     *
     * [systemUiVisibility] default null
     */
    fun systemUiVisibility(systemUiVisibility: Int) = apply {
      this.systemUiVisibility = systemUiVisibility
    }

    /**
     * @see {WindowManager.softInputMode}
     *
     * [softInputMode] default null
     */
    fun softInputMode(softInputMode: Int) = apply { this.softInputMode = softInputMode }

    /**
     * Dialog popup location
     *
     * [x] default 0
     * [y] default Resources.getSystem().displayMetrics.heightPixels
     */
    fun position(x: Int, y: Int) = apply {
      this.xPos = x
      this.yPos = y
    }

    /**
     * Dialog' alpha
     *
     * [alpha] default by themeStyle's config
     */
    fun alpha(alpha: Float) = apply { this.alpha = alpha }

    /**
     * The ratio of the screen's width of the dialog
     *
     * [ratio] value range [0.0~1.0], not include
     */
    fun widthRatio(ratio: Float) = apply {
      if (ratio <= 0.0 && ratio > 1.0) throw IllegalArgumentException("Invalid ratio")
      val width = Resources.getSystem().displayMetrics.widthPixels
      this.width = (width * ratio).toInt()
    }

    /**
     * The ratio of the screen's height of the dialog
     *
     * [ratio] value range [0.0~1.0], not include
     */
    fun heightRatio(ratio: Float) = apply {
      if (ratio <= 0.0 && ratio > 1.0) throw IllegalArgumentException("Invalid ratio")
      val height = Resources.getSystem().displayMetrics.heightPixels
      this.height = (height * ratio).toInt()
      return this
    }

    /**
     * Information about how wide the view wants to be. Can be one of the
     * constants FILL_PARENT (replaced by MATCH_PARENT
     * in API Level 8) or WRAP_CONTENT, or an exact size.
     *
     * [width] dialog's width
     */
    fun width(width: Int) = apply { this.width = width }

    /**
     * Information about how tall the view wants to be. Can be one of the
     * constants FILL_PARENT (replaced by MATCH_PARENT
     * in API Level 8) or WRAP_CONTENT, or an exact size.
     *
     * [height] dialog's height
     */
    fun height(height: Int) = apply { this.height = height }

    /**
     * Pop-up animation style
     *
     * [anim] default [R.style.photo_anim_bottom]
     */
    fun windowAnimations(@StyleRes anim: Int?) = apply { this.windowAnimations = anim }

    fun build() = DialogParams(
        contentView = contentView,
        themeStyle = themeStyle,
        cancelable = cancelable,
        canceledOnTouchOutside = canceledOnTouchOutside,
        xPos = xPos,
        yPos = yPos,
        windowAnimations = windowAnimations,
        gravity = gravity,
        width = width,
        height = height,
        dimAmount = dimAmount,
        alpha = alpha,
        horizontalMargin = horizontalMargin,
        verticalMargin = verticalMargin,
        systemUiVisibility = systemUiVisibility,
        softInputMode = softInputMode,
        onShowListener = onShowListener,
        onDismissListener = onDismissListener,
        onCancelListener = onCancelListener,
        onKeyListener = onKeyListener
    )
  }
}