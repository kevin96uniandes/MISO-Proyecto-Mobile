package com.uniandes.project.abcall.ui.dashboard.fragments

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.provider.DocumentsContract
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.uniandes.project.abcall.IncidenceType
import com.uniandes.project.abcall.config.PreferencesManager
import com.uniandes.project.abcall.databinding.FragmentCreateIncidencesBinding
import com.uniandes.project.abcall.models.Incidence
import com.uniandes.project.abcall.viewmodels.CreateIncidenceViewModel
import java.io.File

class CrateIncidencesFragment : Fragment() {

    private var _binding: FragmentCreateIncidencesBinding? = null
    private val binding get() = _binding!!

    private lateinit var preferencesManager: PreferencesManager
    private lateinit var viewModel: CreateIncidenceViewModel

    private val selectedFiles = mutableListOf<File>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateIncidencesBinding.inflate(inflater, container, false)
        val root: View = binding.root

        preferencesManager = PreferencesManager(binding.root.context)
        viewModel = CreateIncidenceViewModel()



        var idIncidenceType = -1;
        val etIncidenceType: TextInputEditText = binding.etIncidenceType

        etIncidenceType.setOnClickListener {
            val items = IncidenceType.entries.map { "${it.id} - ${it.type}" }.toTypedArray()

            val builder = AlertDialog.Builder(requireContext())
                .setTitle("Selecciona una opción")
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

                .create()

            builder.show()
        }

        /*
        val ilIncidenceType: TextInputLayout = binding.ilIncidenceType

        val ilSubject: TextInputLayout = binding.ilSubject
        val etSubject: TextInputEditText = binding.etSubject

        val ilDetail: TextInputLayout = binding.ilDetail
        val etDetail: TextInputEditText = binding.etDetail

        val btnCancel: Button = binding.btnCancel
        */

        val btnLoadFiles: Button = binding.btnLoadFiles

        btnLoadFiles.setOnClickListener {
            //
        }

        val btnRegister: Button = binding.btnSend

        btnRegister.setOnClickListener {
            viewModel.createIncidence(responseStepsToIncidence(
                subject = "Testing subject",
                detail = "Testing detail",
                type = 1
            ))
        }

        return root
    }


    fun responseStepsToIncidence(subject: String, detail: String, type: Int) : Incidence {
        return Incidence(
            type = com.uniandes.project.abcall.enums.IncidenceType.entries[type].incidence,
            subject = subject,
            detail = detail,
            personId = preferencesManager.getAuth().idPerson!!,
            files = selectedFiles
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }



    companion object {
        const val TITLE = "CreateIncidences"

        @JvmStatic
        fun newInstance() =
            CrateIncidencesFragment()
    }


}