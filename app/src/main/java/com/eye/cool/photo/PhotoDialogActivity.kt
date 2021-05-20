package com.eye.cool.photo

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import com.eye.cool.photo.params.Params
import com.eye.cool.photo.params.WindowParams
import com.eye.cool.photo.support.Action
import com.eye.cool.photo.support.CompatContext
import com.eye.cool.photo.support.IActionConfig
import com.eye.cool.photo.utils.PhotoExecutor
import com.eye.cool.photo.view.DefaultView
import com.eye.cool.photo.view.DialogActivity

/**
 * Compatible with android.support and others, but PhotoDialog is recommended
 *
 *Created by ycb on 2019/8/16 0016
 */
@Deprecated("Use @{PhotoDialog} instead")
class PhotoDialogActivity : DialogActivity(), IActionConfig {

  private val params = PhotoDialogActivity.params ?: Params.Builder().build()

  override val windowParams: WindowParams? = params.dialogParams.windowParams

  private lateinit var executor: PhotoExecutor

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    executor = PhotoExecutor(CompatContext(this), params)
    params.onSelectListener = OnSelectListenerWrapper(
        this,
        params.onSelectListener
    )

    val contentView = createContentView()
    setContentView(contentView)


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
  }

  private fun createContentView(): View {
    val dpm = params.dialogParams
    val view = when {
      dpm.contentView != null -> dpm.contentView
      dpm.contentLayoutId != null ->
        LayoutInflater.from(this).inflate(dpm.contentLayoutId, null)
      else -> DefaultView(this).view
    }
    bindViewAction(view, executor)
    return view
  }

  override fun onDestroy() {
    super.onDestroy()
    PhotoDialogActivity.params = null
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
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
  }
}