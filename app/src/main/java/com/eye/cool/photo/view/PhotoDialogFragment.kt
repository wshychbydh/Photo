package com.eye.cool.photo.view

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.FragmentManager
import android.support.v7.app.AppCompatDialog
import android.support.v7.app.AppCompatDialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.eye.cool.permission.Rationale
import com.eye.cool.photo.IPhotoListener
import com.eye.cool.photo.ISelectListener
import com.eye.cool.photo.PhotoHelper
import com.eye.cool.photo.params.DialogParams
import com.eye.cool.photo.params.ImageParams
import com.eye.cool.photo.params.Params

/**
 * Created by cool on 18-3-9
 */
class PhotoDialogFragment : AppCompatDialogFragment() {

  private lateinit var params: Params
  private lateinit var photoHelper: PhotoHelper
  private var createByBuilder = false

  override fun onAttach(context: Context?) {
    if (!createByBuilder) throw IllegalStateException("You must create it in builder mode")
    super.onAttach(context)
    photoHelper = PhotoHelper(params)
  }

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    var view: View? = params.dialogParams.contentView
    if (view == null) {
      view = DefaultView(context)
      view.setPhotoListener(photoHelper)
    } else {
      val method = view.javaClass.getDeclaredMethod("setPhotoListener", IPhotoListener::class.java)
          ?: throw IllegalArgumentException("Custom View must has public method setPhotoListener(IPhotoListener)")
      method.invoke(view, photoHelper)
    }
    return view
  }

  override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
    val dialogParams = params.dialogParams
    val dialog = AppCompatDialog(context, dialogParams.dialogStyle)
    val window = dialog.window ?: return dialog
    window.setWindowAnimations(dialogParams.animStyle)
    val layoutParams = window.attributes
    layoutParams.x = dialogParams.xPos
    layoutParams.y = dialogParams.yPos
    dialog.onWindowAttributesChanged(layoutParams)
    return dialog
  }

  override fun onActivityCreated(savedInstanceState: Bundle?) {
    super.onActivityCreated(savedInstanceState)
    photoHelper.setOnClickListener {
      view?.visibility = View.GONE
    }
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
    super.onActivityResult(requestCode, resultCode, intent)
    photoHelper.onActivityResult(requestCode, resultCode, intent)
  }

  fun show(manager: FragmentManager) {
    show(manager, javaClass.simpleName)
  }

  class Builder {
    private val dialog = PhotoDialogFragment()
    private val paramsBuilder = Params.Builder(dialog)

    /**
     * The params for shown dialog
     * @param dialogParams
     */
    fun setDialogParams(dialogParams: DialogParams): Builder {
      paramsBuilder.setDialogParams(dialogParams)
      return this
    }

    /**
     * The params for selected image
     * @param imageParams
     */
    fun setImageParams(imageParams: ImageParams): Builder {
      paramsBuilder.setImageParams(imageParams)
      return this
    }

    /**
     * Permission rationale when need
     * @param rationale
     */
    fun setRationale(rationale: Rationale): Builder {
      paramsBuilder.rationale(rationale)
      return this
    }

    /**
     * Permission setting's rationale when need
     * @param rationale
     */
    fun setSettingRationale(rationale: Rationale): Builder {
      paramsBuilder.rationaleSetting(rationale)
      return this
    }

    fun build(): PhotoDialogFragment {
      val params = paramsBuilder.build()
      val wrapper = SelectListenerWrapper(dialog, params.imageParams.onSelectListener)
      params.imageParams.onSelectListener = wrapper
      dialog.params = params
      dialog.createByBuilder = true
      return dialog
    }
  }

  private class SelectListenerWrapper(
      private val dialogFragment: PhotoDialogFragment,
      private val listener: ISelectListener?
  ) : ISelectListener {
    override fun onSelected(imageUrl: String) {
      dialogFragment.dismissAllowingStateLoss()
      listener?.onSelected(imageUrl)
    }
  }
}