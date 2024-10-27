package com.uniandes.project.abcall.ui.dashboard.ui.createIncidences

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.uniandes.project.abcall.IncidenceType
import com.uniandes.project.abcall.databinding.FragmentCreateIncidencesBinding
import com.uniandes.project.abcall.ui.dashboard.ui.createIncidences.CreateIncidencesViewModel

class CrateIncidencesFragment : Fragment() {

    private var _binding: FragmentCreateIncidencesBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        var idIncidenceType = -1;

        val createIncidencesViewModel =
            ViewModelProvider(this).get(CreateIncidencesViewModel::class.java)

        _binding = FragmentCreateIncidencesBinding.inflate(inflater, container, false)
        val root: View = binding.root


        val ilIncidenceType: TextInputLayout = binding.ilIncidenceType
        val etIncidenceType: TextInputEditText = binding.etIncidenceType

        etIncidenceType.setOnClickListener {
            val items = IncidenceType.entries.map { "${it.id} - ${it.type}" }.toTypedArray()

            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Selecciona una opción")
                .setSingleChoiceItems(items, -1) { dialog, which ->
                    val selectedItem = items[which]
                    etIncidenceType.setText(selectedItem)
                    idIncidenceType = selectedItem.split("-")[0].trim().toInt()
                    //clearIdentificationTypeError()
                    dialog.dismiss()
                }
                .setNegativeButton("Cancelar") { dialog, which ->
                    dialog.dismiss()
                }
            builder.show()
        }



        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}