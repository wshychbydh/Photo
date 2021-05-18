package com.eye.cool.photo

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import com.eye.cool.photo.params.Params
import com.eye.cool.photo.support.*
import com.eye.cool.photo.utils.PhotoExecutor
import com.eye.cool.photo.view.DefaultView

/**
 * Compatible with android.support and others, but PhotoDialog is recommended
 *
 *Created by ycb on 2019/8/16 0016
 */
@Deprecated("Use @{PhotoDialog} instead")
class PhotoDialogActivity : AppCompatActivity(), DialogInterface, IWindowConfig {

  private val params = PhotoDialogActivity.params ?: Params.Builder().build()

  private lateinit var executor: PhotoExecutor

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    invasionStatusBar(this)

    executor = PhotoExecutor(CompatContext(this), params)
    params.onSelectListener = OnSelectListenerWrapper(
        this,
        params.onSelectListener
    )

    val lp = configLayoutParams(params.dialogParams, window)
    onWindowAttributesChanged(lp)

    val container = FrameLayout(this)
    container.layoutParams = ViewGroup.LayoutParams(-1, -1)
    val contentView = params.dialogParams.contentView ?: DefaultView(this)
    val layoutParams = FrameLayout.LayoutParams(-1, -2)
    layoutParams.gravity = Gravity.BOTTOM
    bindActionListener(contentView, executor)
    container.addView(contentView, layoutParams)
    setContentView(container)

    container.setOnClickListener {
      if (params.dialogParams.canceledOnTouchOutside) {
        cancel()
      }
    }

    executor.onActionClickListener(object : Params.OnActionListener {
      override fun onAction(action: Int) {
        when (action) {
          Action.TAKE_PHOTO,
          Action.SELECT_ALBUM -> playExitAnim(window, contentView)
          Action.CANCEL,
          Action.PERMISSION_DENIED -> dismiss()
        }
        params.onActionListener?.onAction(action)
      }
    })

    params.dialogParams.onShowListener?.onShow(this)
  }

  override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
    val handled = params.dialogParams.onKeyListener?.onKey(this, keyCode, event)
    return handled ?: super.onKeyDown(keyCode, event)
  }

  override fun cancel() {
    if (isFinishing) return
    params.dialogParams.onCancelListener?.onCancel(this)
    finish()
  }

  override fun dismiss() {
    if (isFinishing) return
    params.dialogParams.onDismissListener?.onDismiss(this)
    finish()
  }

  override fun onBackPressed() {
    if (params.dialogParams.cancelable) {
      dismiss()
    }
  }

  override fun onDestroy() {
    super.onDestroy()
    PhotoDialogActivity.params = null
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    if (BuildConfig.DEBUG) {
      Log.d(Constants.TAG, "requestCode-->$requestCode")
    }
    if (resultCode == RESULT_OK) {
      executor.onActivityResult(requestCode, data)
    } else if (resultCode == RESULT_CANCELED) {
      dismiss()
    }
  }

  private class OnSelectListenerWrapper(
      val activity: PhotoDialogActivity,
      val listener: Params.OnSelectListener?
  ) : Params.OnSelectListener {
    override fun onSelect(path: String) {
      activity.dismiss()
      listener?.onSelect(path)
    }
  }

  companion object {

    @Volatile
    private var params: Params? = null

    @JvmStatic
    fun show(context: Context) {
      show(context, Params.Builder().build())
    }

    @JvmStatic
    fun show(context: Context, params: Params) {
      this.params = params
      val intent = Intent(context, PhotoDialogActivity::class.java)
      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
      context.startActivity(intent)
    }

    /**
     * Set the content layout full the StatusBar, but do not hide StatusBar.
     */
    private fun invasionStatusBar(activity: Activity) {
      if (BuildVersion.isBuildOverLOLLIPOP()) {
        val window = activity.window
        val decorView = window.decorView
        decorView.systemUiVisibility = (
            decorView.systemUiVisibility
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            )
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = Color.TRANSPARENT
      }
    }
  }
}