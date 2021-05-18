package com.eye.cool.photo

import android.app.Activity
import android.app.Dialog
import android.app.DialogFragment
import android.app.FragmentManager
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDialog
import com.eye.cool.photo.params.Params
import com.eye.cool.photo.support.*
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

  override fun onCreate(savedInstanceState: Bundle?) {
    if (!createByBuilder)
      throw IllegalStateException("You must create it by PhotoDialogFragment.create()!")
    super.onCreate(savedInstanceState)
    executor = PhotoExecutor(CompatContext(this), params)
    executor.onActionClickListener(object : Params.OnActionListener {
      override fun onAction(action: Int) {
        when (action) {
          Action.TAKE_PHOTO,
          Action.SELECT_ALBUM -> playExitAnim(dialog.window, view)
          Action.CANCEL,
          Action.PERMISSION_DENIED -> dismissAllowingStateLoss()
        }
        params.onActionListener?.onAction(action)
      }
    })
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
    val window = params.dialogParams
    return AppCompatDialog(activity, window.themeStyle ?: 0)
  }

  override fun onActivityCreated(savedInstanceState: Bundle?) {
    super.onActivityCreated(savedInstanceState)
    setupDialog(params.dialogParams, dialog ?: return)
  }

  override fun onDismiss(dialog: DialogInterface) {
    super.onDismiss(dialog)
    params.dialogParams?.onDismissListener?.onDismiss(dialog)
  }

  override fun onCancel(dialog: DialogInterface) {
    super.onCancel(dialog)
    params.dialogParams?.onCancelListener?.onCancel(dialog)
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