package com.uniandes.project.abcall.ui.components

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.uniandes.project.abcall.R


class CustomSpinnerAdapter(
    context: Context,
    textViewResourceId: Int,
    values: List<String>
) : ArrayAdapter<String>(context, textViewResourceId, values) {

    // Resto del código del adaptador
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getView(position, convertView, parent!!)
        val textView = view.findViewById<TextView>(android.R.id.text1)
        // Cambiar el color del texto del elemento seleccionado
        textView.setTextColor(ContextCompat.getColor(context, R.color.abcall_third_one))

        return view
    }


    // En el método getDropDownView, establece el color del texto usando el valor textColor
    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getDropDownView(position, convertView, parent)
        val textView = view.findViewById<TextView>(android.R.id.text1)
        textView.setTextColor(ContextCompat.getColor(context, R.color.black))

        return view
    }
}
