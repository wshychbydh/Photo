package com.eye.cool.photo.params

import android.content.DialogInterface
import android.content.res.Resources
import android.view.View
import androidx.annotation.StyleRes
import com.eye.cool.photo.R

/**
 *Created by ycb on 2019/8/8 0008
 */
class DialogParams private constructor() {

  internal var contentView: View? = null

  /**
   * Nonsupport for PhotoDialogActivity
   */
  @StyleRes
  internal var themeStyle: Int? = R.style.photo_dialog

  internal var cancelable: Boolean = true

  internal var canceledOnTouchOutside: Boolean = true

  /**
   * Nonsupport for PhotoDialogActivity
   */
  internal var xPos = 0

  /**
   * Nonsupport for PhotoDialogActivity
   */
  internal var yPos = Resources.getSystem().displayMetrics.heightPixels

  @StyleRes
  internal var windowAnimations: Int? = R.style.photo_anim_bottom

  internal var gravity: Int? = null

  internal var width: Int? = null
  internal var height: Int? = null


  internal var dimAmount: Float? = null

  internal var alpha: Float? = null

  internal var horizontalMargin: Float? = null
  internal var verticalMargin: Float? = null

  internal var systemUiVisibility: Int? = null

  internal var softInputMode: Int? = null

  internal var onShowListener: DialogInterface.OnShowListener? = null

  internal var onDismissListener: DialogInterface.OnDismissListener? = null

  internal var onCancelListener: DialogInterface.OnCancelListener? = null

  class Builder {

    private var params = DialogParams()

    /**
     * Dialog's contentView to be shown
     *
     * [view] the shown view, default[R.layout.photo_layout]
     */
    fun contentView(view: View): Builder {
      params.contentView = view
      return this
    }

    /**
     * Sets whether this dialog is cancelable with the
     * {@link KeyEvent#KEYCODE_BACK BACK} key.
     *
     * [cancelable] default true
     */
    fun cancelable(cancelable: Boolean): Builder {
      params.cancelable = cancelable
      return this
    }

    /**
     * Sets whether this dialog is canceled when touched outside the window's
     * bounds. If setting to true, the dialog is set to be cancelable if not
     * already set.
     *
     * [cancelable] Whether the dialog should be canceled when touched outside the window.
     * default true
     */
    fun canceledOnTouchOutside(cancelable: Boolean): Builder {
      params.canceledOnTouchOutside = cancelable
      return this
    }

    /**
     * A style resource describing the theme to use for the window,
     * or {@code 0} to use the default dialog theme
     *
     * [themeStyle] default [R.style.photo_dialog]
     */
    fun themeStyle(themeStyle: Int): Builder {
      params.themeStyle = themeStyle
      return this
    }

    /**
     * Sets a listener to be invoked when the dialog is shown.
     *
     * [listener] The {@link DialogInterface.OnShowListener} to use.
     */
    fun onShowListener(listener: DialogInterface.OnShowListener): Builder {
      params.onShowListener = listener
      return this
    }

    /**
     * Set a listener to be invoked when the dialog is dismissed.
     *
     * [listener] The {@link DialogInterface.OnDismissListener} to use.
     */
    fun onDismissListener(listener: DialogInterface.OnDismissListener): Builder {
      params.onDismissListener = listener
      return this
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
    fun onCancelListener(listener: DialogInterface.OnCancelListener): Builder {
      params.onCancelListener = listener
      return this
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
    fun gravity(gravity: Int): Builder {
      params.gravity = gravity
      return this
    }

    /**
     * When {@link #FLAG_DIM_BEHIND} is set, this is the amount of dimming
     * to apply.  Range is from 1.0 for completely opaque to 0.0 for no dim.
     *
     * [dimAmount] default by themeStyle's config
     */
    fun dimAmount(dimAmount: Float): Builder {
      params.dimAmount = dimAmount
      return this
    }

    /**
     * @see {WindowManager.horizontalMargin}
     *
     * [margin] default null
     */
    fun horizontalMargin(margin: Float): Builder {
      params.horizontalMargin = margin
      return this
    }

    /**
     * @see {WindowManager.verticalMargin}
     *
     * [margin] default null
     */
    fun verticalMargin(margin: Float): Builder {
      params.verticalMargin = margin
      return this
    }

    /**
     * @see {WindowManager.systemUiVisibility}
     *
     * [systemUiVisibility] default null
     */
    fun systemUiVisibility(systemUiVisibility: Int): Builder {
      params.systemUiVisibility = systemUiVisibility
      return this
    }

    /**
     * @see {WindowManager.softInputMode}
     *
     * [softInputMode] default null
     */
    fun softInputMode(softInputMode: Int): Builder {
      params.softInputMode = softInputMode
      return this
    }

    /**
     * Dialog popup location
     *
     * [x] default 0
     * [y] default Resources.getSystem().displayMetrics.heightPixels
     */
    fun position(x: Int, y: Int): Builder {
      params.xPos = x
      params.yPos = y
      return this
    }

    /**
     * Dialog' alpha
     *
     * [alpha] default by themeStyle's config
     */
    fun alpha(alpha: Float): Builder {
      params.alpha = alpha
      return this
    }

    /**
     * The ratio of the screen's width of the dialog
     *
     * [ratio] value range [0.0~1.0], not include
     */
    fun widthRatio(ratio: Float): Builder {
      if (ratio <= 0.0 && ratio > 1.0) throw IllegalArgumentException("Invalid ratio")
      val width = Resources.getSystem().displayMetrics.widthPixels
      params.width = (width * ratio).toInt()
      return this
    }

    /**
     * The ratio of the screen's height of the dialog
     *
     * [ratio] value range [0.0~1.0], not include
     */
    fun heightRatio(ratio: Float): Builder {
      if (ratio <= 0.0 && ratio > 1.0) throw IllegalArgumentException("Invalid ratio")
      val height = Resources.getSystem().displayMetrics.heightPixels
      params.height = (height * ratio).toInt()
      return this
    }

    /**
     * Information about how wide the view wants to be. Can be one of the
     * constants FILL_PARENT (replaced by MATCH_PARENT
     * in API Level 8) or WRAP_CONTENT, or an exact size.
     *
     * [width] dialog's width
     */
    fun width(width: Int): Builder {
      params.width = width
      return this
    }

    /**
     * Information about how tall the view wants to be. Can be one of the
     * constants FILL_PARENT (replaced by MATCH_PARENT
     * in API Level 8) or WRAP_CONTENT, or an exact size.
     *
     * [height] dialog's height
     */
    fun height(height: Int): Builder {
      params.height = height
      return this
    }

    /**
     * Pop-up animation style
     *
     * [anim] default [R.style.photo_anim_bottom]
     */
    fun windowAnimations(@StyleRes anim: Int?): Builder {
      params.windowAnimations = anim
      return this
    }

    fun build() = params
  }
}