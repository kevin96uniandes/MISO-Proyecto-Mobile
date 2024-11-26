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
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.uniandes.project.abcall.R
import com.uniandes.project.abcall.adapter.SelectedFilesAdapter
import com.uniandes.project.abcall.config.ApiResult
import com.uniandes.project.abcall.config.PreferencesManager
import com.uniandes.project.abcall.databinding.FragmentCreateIncidencesBinding
import com.uniandes.project.abcall.enums.IncidenceType
import com.uniandes.project.abcall.getCustomSharedPreferences
import com.uniandes.project.abcall.models.Incidence
import com.uniandes.project.abcall.models.Principal
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

    private lateinit var sendingDialog: CustomDialogFragment

    private lateinit var principal: Principal

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateIncidencesBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val sPreferences = getCustomSharedPreferences(binding.root.context)
        preferencesManager = PreferencesManager(sPreferences)
        viewModel = CreateIncidenceViewModel()

        principal = preferencesManager.getAuth()

        var idIncidenceType = "";

        val etIncidenceType: TextInputEditText = binding.etIncidenceType
        etIncidenceType.setOnClickListener {
            val items = IncidenceType.entries.map { "${it.id} - ${it.incidence}" }.toTypedArray()

            val builder = AlertDialog.Builder(requireContext())
                .setTitle(getString(R.string.select_an_option))
                .setSingleChoiceItems(items, -1) { dialog, which ->
                    val selectedItem = items[which]
                    etIncidenceType.setText(selectedItem)
                    idIncidenceType = selectedItem.split("-")[1].trim().toString()
                    //clearIdentificationTypeError()
                    dialog.dismiss()
                }
                .setNegativeButton(getString(R.string.cancel)) { dialog, which ->
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

        val btnLoadFiles: Button = binding.btnLoadFiles

        btnLoadFiles.setOnClickListener {
            showFileSelectionDialog()
        }

        val btnRegister: Button = binding.btnSend

        btnRegister.setOnClickListener {
            if ( validateForm() ){
                viewModel.createIncidence(responseStepsToIncidence(
                    subject = etSubject.text.toString(),
                    detail = etDetail.text.toString(),
                    type = idIncidenceType
                ))
                sendingDialog = CustomDialogFragment().newInstance(
                    getString(R.string.submitting_issue),
                    getString(R.string.please_wait_a_moment_while_the_attachment_is_are_being_sent),
                    R.raw.sending,
                    false
                )
                sendingDialog.show(parentFragmentManager, "CustomDialog")
            }
        }

        viewModel.result.observe(viewLifecycleOwner) { result ->
            sendingDialog.dismiss()
            when(result) {
                is ApiResult.Success -> {
                    cleanUpTempFile()
                    val dialog = CustomDialogFragment().newInstance(
                        getString(R.string.issue_successfully_submitted),
                        getString(R.string.your_issue_was_successfully_registered),
                        R.raw.success
                    ) {
                        fragmentChangeListener?.onFragmentChange(IncidencesFragment.newInstance())
                    }
                    dialog.show(parentFragmentManager, "CustomDialog")
                }
                is ApiResult.Error -> {
                    val dialog = CustomDialogFragment().newInstance(
                        getString(R.string.error_submitting_issue),
                        getString(R.string.please_try_again_in_a_few_minutes),
                        R.raw.error
                    )
                    dialog.show(parentFragmentManager, "CustomDialog")
                }
                ApiResult.NetworkError -> {
                    val dialog = CustomDialogFragment().newInstance(
                        getString(R.string.user_registration),
                        getString(R.string.unable_to_connect_to_the_server),
                        R.raw.no_network
                    )
                    dialog.show(parentFragmentManager, "CustomDialog")
                }
            }
        }



        val btnCancel: Button = binding.btnCancel

        btnCancel.setOnClickListener({
            etIncidenceType.text?.clear()
            etSubject.text?.clear()
            etDetail.text?.clear()
        })

        setupTextWatchers()

        return root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is FragmentChangeListener) {
            fragmentChangeListener = context
            context.getString(R.string.create_issues)
        }
    }

    fun cleanUpTempFile() {
        selectedFiles.map {
            if (it.exists()) {
                val deleted = it.delete()
                if (deleted) {
                    Log.d("FileCleanup", "Temporary file deleted: ${it.name}")
                } else {
                    Log.e("FileCleanup", "Failed to delete temporary file: ${it.name}")
                }
            }
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
        Toast.makeText(context, getString(R.string.file_deleted), Toast.LENGTH_SHORT).show()
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
            files = selectedFiles,
            idCompany = principal.idCompany
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
                        Toast.makeText(context,
                            getString(R.string.file_could_not_be_retrieved), Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(context,
                        "${getString(R.string.you_can_only_add_up_to, MAX_FILES.toString())}}", Toast.LENGTH_SHORT).show()
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
        var TITLE = ""

        @JvmStatic
        fun newInstance () =
            CrateIncidencesFragment()
    }

    private fun validateForm(): Boolean{
        var isValid = true

        binding.ilIncidenceType.error = null
        binding.ilSubject.error = null
        binding.ilDetail.error = null

        val incidenceType = binding.etIncidenceType.text.toString().trim()
        val subject = binding.etSubject.text.toString().trim()
        val detail = binding.etDetail.text.toString().trim()


        if (TextUtils.isEmpty(incidenceType)) {
            binding.ilIncidenceType.error = getString(R.string.issue_type_is_required)
            scrollToView(binding.ilIncidenceType)
            isValid = false
        }

        if (TextUtils.isEmpty(subject)) {
            binding.ilSubject.error = getString(R.string.subject_required)
            scrollToView(binding.ilSubject)
            isValid = false
        }

        if (TextUtils.isEmpty(detail)) {
            binding.ilDetail.error = getString(R.string.detail_required)
            scrollToView(binding.ilDetail)
            isValid = false
        }
        return isValid
    }

    private fun scrollToView(view: View) {
        view.parent.requestChildFocus(view, view)
    }


    private fun setupTextWatchers() {
        binding.etIncidenceType.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                clearIncidenceTypeError()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        binding.etSubject.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                clearSubjectError()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        binding.etDetail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                clearDetailError()
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {}

        })
    }

    private fun clearIncidenceTypeError() {
        binding.ilIncidenceType.error = null
    }

    private fun clearSubjectError() {
        binding.ilSubject.error = null
    }

    private fun clearDetailError() {
        binding.ilDetail.error = null
    }
}