package com.eye.cool.photo

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDialogFragment
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

  override fun onAttach(context: Context) {
    check(createByBuilder) { "You must create it by PhotoDialog.create()!" }
    super.onAttach(context)
    executor = PhotoExecutor(CompatContext(this), params)
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
    return createDialog(requireContext(), params)
  }

  override fun onActivityCreated(savedInstanceState: Bundle?) {
    super.onActivityCreated(savedInstanceState)
    executor.onActionClickListener(object : Params.OnActionListener {
      override fun onAction(action: Int) {
        when (action) {
          Action.ADJUST_PHOTO,
          Action.SELECT_ALBUM -> playExitAnim(dialog?.window, view)
          Action.CANCEL,
          Action.PERMISSION_DENIED -> dismissAllowingStateLoss()
        }
        params.onActionListener?.onAction(action)
      }
    })
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
    super.onActivityResult(requestCode, resultCode, intent)
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