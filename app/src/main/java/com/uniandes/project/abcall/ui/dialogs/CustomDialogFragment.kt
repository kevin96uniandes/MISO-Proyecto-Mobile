package com.uniandes.project.abcall.ui.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.airbnb.lottie.LottieAnimationView
import com.uniandes.project.abcall.R

class CustomDialogFragment : DialogFragment() {

    private lateinit var title: String
    private lateinit var description: String
    private var isVisibleButton = true
    private var lottieImage: Int = 0
    private var onDismissListener: (() -> Unit)? = null

    fun newInstance(title: String, description: String, lottieImage: Int, isVisibleButton:Boolean  = true, onDismissListener: (() -> Unit)? = null): CustomDialogFragment {
        val dialog = CustomDialogFragment()
        dialog.title = title
        dialog.description = description
        dialog.lottieImage = lottieImage
        dialog.onDismissListener = onDismissListener
        dialog.isVisibleButton = isVisibleButton
        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return layoutInflater.inflate(R.layout.custom_dialog, container, false)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setCanceledOnTouchOutside(false)
        return dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val lottieAnimationView: LottieAnimationView = view.findViewById(R.id.animation_view)
        val titleTextView: TextView = view.findViewById(R.id.dialog_title)
        val descriptionTextView: TextView = view.findViewById(R.id.dialog_description)
        val button: Button = view.findViewById(R.id.dialog_button)

        button.visibility = if (isVisibleButton) View.VISIBLE else View.GONE

        lottieAnimationView.setAnimation(lottieImage)
        lottieAnimationView.playAnimation()


        titleTextView.text = title
        descriptionTextView.text = description

        button.setOnClickListener {
            dismiss()
        }
    }

    override fun onResume() {
        super.onResume()
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    override fun onDismiss(dialog: android.content.DialogInterface) {
        super.onDismiss(dialog)
        onDismissListener?.invoke()  // Llama al callback cuando el diálogo se cierra
    }

}