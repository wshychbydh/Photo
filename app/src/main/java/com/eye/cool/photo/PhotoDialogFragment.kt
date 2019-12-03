package com.eye.cool.photo

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.annotation.TargetApi
import android.app.Activity
import android.app.Dialog
import android.app.DialogFragment
import android.app.FragmentManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import com.eye.cool.photo.params.ImageParams
import com.eye.cool.photo.params.Params
import com.eye.cool.photo.support.*
import com.eye.cool.photo.utils.PhotoExecutor
import com.eye.cool.photo.view.DefaultView

/**
 * Created by cool on 18-3-9
 */
@Deprecated("Use @{PhotoDialog} instead")
class PhotoDialogFragment : DialogFragment() {

  private lateinit var executor: PhotoExecutor
  private lateinit var params: Params
  private var createByBuilder = false

  override fun onAttach(context: Context?) {
    if (!createByBuilder) throw IllegalStateException("You must create it by PhotoDialogFragment.create()!")
    super.onAttach(context)
    executor = PhotoExecutor(CompatContext(this), params)
  }

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    val view = params.dialogParams.contentView ?: DefaultView(activity)
    val method = view.javaClass.getDeclaredMethod("setOnActionListener", OnActionListener::class.java)
    method.isAccessible = true
    method.invoke(view, executor)
    return view
  }

  override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
    val dialogParams = params.dialogParams
    val dialog = Dialog(activity, dialogParams.dialogStyle)
    val window = dialog.window ?: return dialog
    window.setWindowAnimations(dialogParams.animStyle)
    val layoutParams = window.attributes
    layoutParams.x = dialogParams.xPos
    layoutParams.y = dialogParams.yPos
    dialog.onWindowAttributesChanged(layoutParams)
    dialog.setOnShowListener(dialogParams.onShowListener)
    dialog.setOnDismissListener(dialogParams.onDismissListener)
    dialog.setOnCancelListener(dialogParams.onCancelListener)
    return dialog
  }

  override fun onActivityCreated(savedInstanceState: Bundle?) {
    super.onActivityCreated(savedInstanceState)
    executor.setOnClickListener(object : OnClickListener {
      override fun onClick(which: Int) {
        when (which) {
          Constants.ADJUST_PHOTO, Constants.SELECT_ALBUM -> playExitAnim()
          Constants.CANCEL, Constants.PERMISSION_FORBID -> dismissAllowingStateLoss()
        }
        params.dialogParams.onClickListener?.onClick(which)
      }
    })
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
    super.onActivityResult(requestCode, resultCode, intent)
    if (BuildConfig.DEBUG) {
      Log.d(Constants.TAG, "requestCode-->$requestCode")
    }
    if (resultCode == Activity.RESULT_OK) {
      executor.onActivityResult(requestCode, intent)
    } else if (resultCode == Activity.RESULT_CANCELED) {
      dismissAllowingStateLoss()
    }
  }

  fun show(manager: FragmentManager) {
    show(manager, javaClass.simpleName)
  }

  private fun playExitAnim() {
    if (dialog == null || view == null) return
    val animator1 = ObjectAnimator.ofFloat(
        view,
        "translationY",
        0f,
        view!!.height.toFloat()
    )
    val window = dialog?.window ?: return
    val animator2 = ValueAnimator.ofFloat(window.attributes.dimAmount, 0f)
    animator2.addUpdateListener {
      val dim = it.animatedValue as Float
      val lp = window.attributes
      lp.dimAmount = dim
      window.attributes = lp
    }
    val set = AnimatorSet()
    set.interpolator = LinearInterpolator()
    set.duration = 150
    set.playTogether(animator1, animator2)
    set.start()
  }

  companion object {

    /**
     * If you only want to get the returned image, set it
     *
     * @param onSelectListener Image selection callback
     * @return A instance of PhotoDialogFragment
     */
    @JvmStatic
    fun create(onSelectListener: OnSelectListener): PhotoDialogFragment {
      return create(
          ImageParams.Builder()
              .setOnSelectListener(onSelectListener)
              .build()
      )
    }

    /**
     * If you want to configure the returned image, set it
     *
     * @param imageParams image configure
     * @return A instance of PhotoDialogFragment
     */
    @JvmStatic
    fun create(imageParams: ImageParams): PhotoDialogFragment {
      return create(Params.Builder().setImageParams(imageParams).build())
    }

    /**
     * If you only want to custom permission, set it
     *
     * @param onSelectListener Image selection callback
     * @param requestCamera Is camera permission applied in the Manifest, default false
     * @param permissionInvoker Permission invoker callback after to request permissions
     * @return A instance of PhotoDialogFragment
     */
    @TargetApi(Build.VERSION_CODES.M)
    @JvmStatic
    fun create(
        onSelectListener: OnSelectListener,
        requestCamera: Boolean = false,
        permissionInvoker: PermissionInvoker? = null
    ): PhotoDialogFragment {
      return create(
          ImageParams.Builder()
              .setOnSelectListener(onSelectListener)
              .build(),
          requestCamera,
          permissionInvoker
      )
    }

    /**
     * Custom image return and permission
     *
     * @param imageParams image configure
     * @param requestCamera Is camera permission applied in the Manifest, default false
     * @param permissionInvoker Permission invoker callback after to request permissions
     * @return A instance of PhotoDialogFragment
     */
    @TargetApi(Build.VERSION_CODES.M)
    @JvmStatic
    fun create(
        imageParams: ImageParams,
        requestCamera: Boolean = false,
        permissionInvoker: PermissionInvoker? = null
    ): PhotoDialogFragment {
      return create(
          Params.Builder()
              .setImageParams(imageParams)
              .requestCameraPermission(requestCamera)
              .setPermissionInvoker(permissionInvoker)
              .build()
      )
    }

    /**
     * Custom all of this call
     *
     * @param params all the configure of this call
     * @return A instance of PhotoDialogFragment
     */
    @JvmStatic
    fun create(params: Params): PhotoDialogFragment {
      val dialog = PhotoDialogFragment()
      val listenerWrapper = OnSelectListenerWrapper(
          dialogFragment = dialog,
          listener = params.imageParams.onSelectListener
      )
      params.imageParams.onSelectListener = listenerWrapper
      dialog.params = params
      dialog.createByBuilder = true
      return dialog
    }
  }
}