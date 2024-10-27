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
import com.uniandes.project.abcall.databinding.FragmentCreateIncidencesBinding
import com.uniandes.project.abcall.ui.dashboard.ui.createIncidences.CreateIncidencesViewModel

// import com.uniandes.project.abcall.ui.dashboard.ui.createIncidences.CreateIncidencesViewModel

class CrateIncidencesFragment : Fragment() {

    private var _binding: FragmentCreateIncidencesBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // val createIncidencesViewModel = ViewModelProvider(this).get(CreateIncidencesViewModel::class.java)

        _binding = FragmentCreateIncidencesBinding.inflate(inflater, container, false)
        val root: View = binding.root

        /*
        var idIncidenceType = -1;

        val ilIncidenceType: TextInputLayout = binding.ilIncidenceType
        val etIncidenceType: TextInputEditText = binding.etIncidenceType


        val ilSubject: TextInputLayout = binding.ilSubject
        val etSubject: TextInputEditText = binding.etSubject


        val ilDetail: TextInputLayout = binding.ilDetail
        val etDetail: TextInputEditText = binding.etDetail

        val btnRegister: Button = binding.btnSend
        val btnCancel: Button = binding.btnCancel

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


        */

        val btnLoadFiles: Button = binding.btnLoadFiles

        btnLoadFiles.setOnClickListener {
            Log.d("Intent fail", "Antes de Buscar archivos")
            openDirectory()
            Log.d("Intent fail", "Después de Buscar archivos")
        }

        return root
    }

    fun openDirectory() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE).apply {
            putExtra(DocumentsContract.EXTRA_INITIAL_URI, ".")
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            intent.type = "*/*"
            intent.addCategory(Intent.CATEGORY_OPENABLE)

            try {
                startActivityForResult(Intent.createChooser(intent, "Select a file"), 100)
            } catch (exception: Exception) {
                Toast.makeText(requireContext(), "Please install a file manager", Toast.LENGTH_SHORT).show()
            }
        }
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