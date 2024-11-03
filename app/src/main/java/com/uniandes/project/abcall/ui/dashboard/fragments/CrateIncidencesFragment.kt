package com.uniandes.project.abcall.ui.dashboard.fragments

import android.app.Activity
import android.app.AlertDialog
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.uniandes.project.abcall.IncidenceType
import com.uniandes.project.abcall.R
import com.uniandes.project.abcall.adapter.SelectedFilesAdapter
import com.uniandes.project.abcall.config.PreferencesManager
import com.uniandes.project.abcall.databinding.FragmentCreateIncidencesBinding
import com.uniandes.project.abcall.models.Incidence
import com.uniandes.project.abcall.ui.dashboard.fragments.IncidenceCreateChatbotFragment.Companion
import com.uniandes.project.abcall.ui.dashboard.intefaces.FragmentChangeListener
import com.uniandes.project.abcall.ui.dialogs.CustomDialogFragment
import com.uniandes.project.abcall.viewmodels.CreateIncidenceViewModel
import java.io.File

class CrateIncidencesFragment : Fragment() {

    private var _binding: FragmentCreateIncidencesBinding? = null
    private val binding get() = _binding!!

    private lateinit var preferencesManager: PreferencesManager
    private lateinit var viewModel: CreateIncidenceViewModel

    private val MAX_FILES = 3
    private val selectedFiles = mutableListOf<File>()

    private var fragmentChangeListener: FragmentChangeListener? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateIncidencesBinding.inflate(inflater, container, false)
        val root: View = binding.root

        preferencesManager = PreferencesManager(binding.root.context)
        viewModel = CreateIncidenceViewModel()

        var idIncidenceType = "";
        val etIncidenceType: TextInputEditText = binding.etIncidenceType

        etIncidenceType.setOnClickListener {
            val items = IncidenceType.entries.map { "${it.id} - ${it.type}" }.toTypedArray()

            val builder = AlertDialog.Builder(requireContext())
                .setTitle("Selecciona una opción")
                .setSingleChoiceItems(items, -1) { dialog, which ->
                    val selectedItem = items[which]
                    etIncidenceType.setText(selectedItem)
                    idIncidenceType = selectedItem.split("-")[1].trim().toString()
                    //clearIdentificationTypeError()
                    dialog.dismiss()
                }
                .setNegativeButton("Cancelar") { dialog, which ->
                    dialog.dismiss()
                }

                .create()

            builder.show()
        }

        val ilIncidenceType: TextInputLayout = binding.ilIncidenceType

        val ilSubject: TextInputLayout = binding.ilSubject
        val etSubject: TextInputEditText = binding.etSubject

        val ilDetail: TextInputLayout = binding.ilDetail
        val etDetail: TextInputEditText = binding.etDetail

        val btnCancel: Button = binding.btnCancel
        val btnLoadFiles: Button = binding.btnLoadFiles

        btnLoadFiles.setOnClickListener {
            showFileSelectionDialog()
        }

        val btnRegister: Button = binding.btnSend

        btnRegister.setOnClickListener {
            viewModel.createIncidence(responseStepsToIncidence(
                subject = etSubject.text.toString(),
                detail = etDetail.text.toString(),
                type = idIncidenceType
            ))
            val dialog = CustomDialogFragment().newInstance(
                "Incidencia envia satisfactoiamente",
                "Su incidencia fué registrada exitosanmente, podrá visualizarla en su listado de incidencias",
                R.raw.success
            ) {
                fragmentChangeListener?.onFragmentChange(IncidencesFragment.newInstance())
                //Toast.makeText(binding.root.context, "Error de red", Toast.LENGTH_SHORT).show()
            }
            dialog.show(parentFragmentManager, "CustomDialog")
        }

        return root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is FragmentChangeListener) {
            fragmentChangeListener = context
        }
    }

    private fun showFileSelectionDialog() {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_file_selection, null)
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        val recyclerSelectedFiles = dialogView.findViewById<RecyclerView>(R.id.recyclerSelectedFiles)
        val adapter = SelectedFilesAdapter(selectedFiles.toMutableList()) { uri -> removeFile(uri) }
        recyclerSelectedFiles.adapter = adapter
        recyclerSelectedFiles.layoutManager = LinearLayoutManager(context)

        val attachButton = dialogView.findViewById<ImageButton>(R.id.iv_dialog_attach_file)

        attachButton.setOnClickListener {
            openFileSelector()
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun removeFile(file: File) {
        selectedFiles.remove(file)
        Toast.makeText(context, "Archivo eliminado", Toast.LENGTH_SHORT).show()
    }

    private fun openFileSelector() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.type = "*/*"
        startActivityForResult(intent, FILE_REQUEST_CODE)
    }

    fun responseStepsToIncidence(subject: String, detail: String, type: String) : Incidence {
        return Incidence(
            type = type,
            subject = subject,
            detail = detail,
            personId = preferencesManager.getAuth().idPerson!!,
            files = selectedFiles
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == FILE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                if (selectedFiles.size < MAX_FILES) {
                    // Convertir el Uri a un File
                    val file = uriToFile(uri)

                    if (file != null && file.exists()) {
                        selectedFiles.add(file)
                        showFileSelectionDialog()
                    } else {
                        Toast.makeText(context, "No se pudo obtener el archivo.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(context, "Solo puedes agregar hasta $MAX_FILES archivos.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun uriToFile(uri: Uri): File? {
        return try {
            if (uri.scheme == ContentResolver.SCHEME_CONTENT) {
                // Crea un archivo temporal en el directorio de caché
                val tempFile = File.createTempFile("tempFile", null, binding.root.context.cacheDir)

                // Abre el InputStream desde el Uri y copia el contenido al archivo temporal
                binding.root.context.contentResolver.openInputStream(uri)?.use { inputStream ->
                    inputStream.copyTo(tempFile.outputStream())
                }

                // Obtiene el nombre original del archivo
                var fileName = getFileName(uri, binding.root.context.contentResolver) ?: "file"

                // Verifica si el nombre ya contiene una extensión
                val hasExtension = fileName.contains(".")

                // Obtiene la extensión del archivo a partir del MIME type si es necesario
                if (!hasExtension) {
                    val mimeType = binding.root.context.contentResolver.getType(uri)
                    val extension = when (mimeType) {
                        "image/jpeg" -> ".jpg"
                        "image/png" -> ".png"
                        "application/pdf" -> ".pdf"
                        // Agrega más tipos según tus necesidades
                        else -> ""
                    }
                    fileName += extension // Añade la extensión si no existe
                }

                // Renombra el archivo temporal con el nombre original y la extensión correcta
                val renamedFile = File(tempFile.parent, fileName)
                if (tempFile.renameTo(renamedFile)) {
                    renamedFile // Retorna el archivo renombrado
                } else {
                    Log.e("FileConversion", "Error renaming temporary file.")
                    tempFile // Retorna el archivo temporal si falla el renombrado
                }
            } else {
                // Si no es un contenido, intenta tratarlo como un Uri de archivo
                File(uri.path!!)
            }
        } catch (e: Exception) {
            Log.e("FileConversion", "Error converting Uri to File: ${e.message}")
            null
        }
    }

    private fun getFileName(uri: Uri, contentResolver: ContentResolver): String? {
        var name: String? = null
        val cursor: Cursor? = contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val nameIndex = it.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME)
                if (nameIndex != -1) {
                    name = it.getString(nameIndex)
                }
            }
        }
        return name
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }



    companion object {
        private val FILE_REQUEST_CODE = 1001
        const val TITLE = "CreateIncidences"

        @JvmStatic
        fun newInstance () =
            CrateIncidencesFragment()
    }


}