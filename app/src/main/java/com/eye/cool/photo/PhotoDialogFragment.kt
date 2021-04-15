package com.eye.cool.photo

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.app.Activity
import android.app.Dialog
import android.app.DialogFragment
import android.app.FragmentManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import com.eye.cool.photo.params.Params
import com.eye.cool.photo.support.*
import com.eye.cool.photo.support.CompatContext
import com.eye.cool.photo.support.Constants
import com.eye.cool.photo.support.IWindowConfig
import com.eye.cool.photo.support.OnSelectListenerWrapper
import com.eye.cool.photo.utils.PhotoExecutor
import com.eye.cool.photo.view.DefaultView

/**
 * Created by cool on 18-3-9
 */
@Deprecated("Use @{PhotoDialog} instead")
class PhotoDialogFragment : DialogFragment(), IWindowConfig {

  private lateinit var executor: PhotoExecutor
  private lateinit var params: Params
  private var createByBuilder = false

  override fun onAttach(context: Context?) {
    if (!createByBuilder)
      throw IllegalStateException("You must create it by PhotoDialogFragment.create()!")
    super.onAttach(context)
    executor = PhotoExecutor(CompatContext(this), params)
  }

  override fun onCreateView(
      inflater: LayoutInflater,
      container: ViewGroup?,
      savedInstanceState: Bundle?
  ): View? {
    val view = params.dialogParams.contentView ?: DefaultView(activity)
    bindActionListener(view, executor)
    return view
  }

  override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
    return createDialog(activity, params)
  }

  override fun onActivityCreated(savedInstanceState: Bundle?) {
    super.onActivityCreated(savedInstanceState)
    executor.onActionClickListener(object : Params.OnActionListener {
      override fun onAction(action: Int) {
        when (action) {
          Action.ADJUST_PHOTO,
          Action.SELECT_ALBUM -> playExitAnim(dialog.window, view)
          Action.CANCEL,
          Action.PERMISSION_DENIED -> dismissAllowingStateLoss()
        }
        params.onActionListener?.onAction(action)
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

  companion object {

    /**
     * Create a photo dialog with listener
     *
     * [onSelectListener]
     */
    @JvmStatic
    fun create(
        onSelectListener: Params.OnSelectListener
    ): PhotoDialogFragment {
      return create(
          Params.Builder()
              .onSelectListener(onSelectListener)
              .build()
      )
    }

    /**
     * Create a photo dialog with config
     *
     * [params] all the configure in it, {Params.onSelectListener} must be set
     */
    @JvmStatic
    fun create(params: Params): PhotoDialogFragment {
      val dialog = PhotoDialogFragment()
      val listenerWrapper = OnSelectListenerWrapper(
          dialogFragment = dialog,
          listener = params.onSelectListener
      )
      params.onSelectListener = listenerWrapper
      dialog.params = params
      dialog.createByBuilder = true
      return dialog
    }
  }
}