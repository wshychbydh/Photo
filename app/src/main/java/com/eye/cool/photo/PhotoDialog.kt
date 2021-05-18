package com.eye.cool.photo

import android.app.Activity
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDialog
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.eye.cool.photo.params.Params
import com.eye.cool.photo.support.Action
import com.eye.cool.photo.support.CompatContext
import com.eye.cool.photo.support.IWindowConfig
import com.eye.cool.photo.support.OnSelectListenerWrapper
import com.eye.cool.photo.utils.PhotoExecutor
import com.eye.cool.photo.view.DefaultView

/**
 * Created by cool on 18-3-9
 */
class PhotoDialog : AppCompatDialogFragment(), IWindowConfig {

  private lateinit var executor: PhotoExecutor
  private lateinit var params: Params
  private var createByBuilder = false

  override fun onCreate(savedInstanceState: Bundle?) {
    check(createByBuilder) { "You must create it by PhotoDialog.create()!" }
    super.onCreate(savedInstanceState)
    executor = PhotoExecutor(CompatContext(this), params)
    executor.onActionClickListener(object : Params.OnActionListener {
      override fun onAction(action: Int) {
        when (action) {
          Action.TAKE_PHOTO,
          Action.SELECT_ALBUM -> playExitAnim(dialog?.window, view)
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
    val view = params.dialogParams.contentView ?: DefaultView(requireContext())
    bindActionListener(view, executor)
    return view
  }

  override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
    val window = params.dialogParams
    return AppCompatDialog(context, window.themeStyle ?: 0)
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
    if (resultCode == Activity.RESULT_OK) {
      executor.onActivityResult(requestCode, intent)
    } else if (resultCode == Activity.RESULT_CANCELED) {
      dismissAllowingStateLoss()
    }
  }

  fun show(activity: FragmentActivity) {
    show(activity.supportFragmentManager)
  }

  fun show(fragment: Fragment) {
    show(fragment.childFragmentManager)
  }

  fun show(manager: FragmentManager) {
    val current = System.currentTimeMillis()
    if (current - lastShownTime < SHOWN_INTERVAL) return
    lastShownTime = current
    show(manager, javaClass.simpleName)
  }

  override fun onDestroy() {
    super.onDestroy()
    lastShownTime = 0L
  }


  companion object {

    @Volatile
    private var lastShownTime = 0L

    private const val SHOWN_INTERVAL = 800L

    /**
     * Create a photo dialog with listener
     */
    @JvmStatic
    fun create(onSelectListener: Params.OnSelectListener): PhotoDialog {
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
    fun create(params: Params): PhotoDialog {
      val dialog = PhotoDialog()
      val listenerWrapper = OnSelectListenerWrapper(
          compatDialogFragment = dialog,
          listener = params.onSelectListener
      )
      params.onSelectListener = listenerWrapper
      dialog.params = params
      dialog.createByBuilder = true
      return dialog
    }
  }
}