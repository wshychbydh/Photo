package com.eye.cool.photo

import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import com.eye.cool.photo.params.ImageParams
import com.eye.cool.photo.params.Params
import com.eye.cool.photo.support.*
import com.eye.cool.photo.utils.PhotoExecutor
import com.eye.cool.photo.view.DefaultView

/**
 * Compatible with android.support and others, but PhotoDialog is recommended
 *
 *Created by ycb on 2019/8/16 0016
 */
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
          Action.ADJUST_PHOTO,
          Action.SELECT_ALBUM -> playExitAnim(window, contentView)
          Action.CANCEL,
          Action.PERMISSION_DENIED -> dismiss()
        }
        params.onActionListener?.onAction(action)
      }
    })

    params.dialogParams.onShowListener?.onShow(this)
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

    /**
     * Clear old params
     */
    @JvmStatic
    fun reset(): Companion {
      this.params = null
      return this
    }

    /**
     * All settings for this call
     *
     * [params]
     */
    @JvmStatic
    fun params(params: Params): Companion {
      this.params = params
      return this
    }

    /**
     * If you only want to get the returned image, set it
     *
     * [onSelectListener] Image selection callback
     */
    @JvmStatic
    fun onSelectListener(onSelectListener: Params.OnSelectListener): Companion {
      if (params == null) params = Params.Builder().build()
      params!!.onSelectListener = onSelectListener
      return this
    }

    /**
     * Set a listener to be invoked when the action was happened.
     *
     * <p>
     *   Only one of these actions
     *   {@link Action#TAKE_PHOTO, SELECT_ALBUM, CANCEL, PERMISSION_FORBID}
     * </p>
     *
     * [listener]
     */
    fun onActionListener(listener: Params.OnActionListener?): Companion {
      params!!.onActionListener = listener
      return this
    }

    /**
     * The params for selected image
     *
     * [imageParams]
     */
    @JvmStatic
    fun imageParams(imageParams: ImageParams): Companion {
      if (params == null) params = Params.Builder().build()
      params!!.imageParams = imageParams
      return this
    }

    /**
     * Callback the request result after requesting permission
     *
     * [permissionInvoker] Permission invoker callback after to request permissions
     */
    @TargetApi(Build.VERSION_CODES.M)
    fun permissionInvoker(permissionInvoker: Params.PermissionInvoker): Companion {
      if (params == null) params = Params.Builder().build()
      params!!.permissionInvoker = permissionInvoker
      return this
    }

    /**
     * If registered permission of 'android.permission.CAMERA' in manifest, you must set it to true.
     *
     * [requestCameraPermission] default false
     */
    @JvmStatic
    @TargetApi(Build.VERSION_CODES.M)
    fun requestCameraPermission(requestCameraPermission: Boolean): Companion {
      if (params == null) params = Params.Builder().build()
      params!!.requestCameraPermission = requestCameraPermission
      return this
    }

    /**
     * If you specify a custom image path, you need to add a FileProvider above 7.0
     *
     * [authority] The authority of a {@link FileProvider} defined in a
     *            {@code <provider>} element in your app's manifest.
     */
    @TargetApi(Build.VERSION_CODES.N)
    @JvmStatic
    fun authority(authority: String): Companion {
      if (params == null) params = Params.Builder().build()
      params!!.authority = authority
      return this
    }

    @JvmStatic
    fun show(context: Context) {
      if (params == null) params = Params.Builder().build()
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