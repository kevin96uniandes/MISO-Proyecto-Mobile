package com.uniandes.project.abcall.ui.dashboard.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.database.Cursor
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.uniandes.project.abcall.R
import com.uniandes.project.abcall.adapter.IncidentChatbotAdapter
import com.uniandes.project.abcall.adapter.SelectedFilesAdapter
import com.uniandes.project.abcall.config.ApiResult
import com.uniandes.project.abcall.config.JwtManager
import com.uniandes.project.abcall.config.PreferencesManager
import com.uniandes.project.abcall.databinding.FragmentIncidenceCreateChatbotBinding
import com.uniandes.project.abcall.enums.IncidenceType
import com.uniandes.project.abcall.enums.MessageChatbotSentBy
import com.uniandes.project.abcall.enums.Technology
import com.uniandes.project.abcall.getCustomSharedPreferences
import com.uniandes.project.abcall.models.ChatbotMessage
import com.uniandes.project.abcall.models.Incidence
import com.uniandes.project.abcall.models.Principal
import com.uniandes.project.abcall.repositories.rest.IncidenceClient
import com.uniandes.project.abcall.ui.LoginActivity
import com.uniandes.project.abcall.ui.dashboard.intefaces.FragmentChangeListener
import com.uniandes.project.abcall.ui.dialogs.CustomDialogFragment
import com.uniandes.project.abcall.viewmodels.CreateIncidenceViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class IncidenceCreateChatbotFragment : Fragment() {

    private var steps: List<SpannableString> = listOf()

    private var canAddResponseStep: Boolean = true

    private lateinit var responseSteps: MutableList<String>

    private var stepPosition = 0

    private lateinit var messageAdapter: IncidentChatbotAdapter
    private val messageList = mutableListOf<ChatbotMessage>()

    private var _binding: FragmentIncidenceCreateChatbotBinding? = null
    private val binding get() = _binding!!
    private var currentTurn: MessageChatbotSentBy = MessageChatbotSentBy.CHATBOT

    private lateinit var viewModel: CreateIncidenceViewModel
    private lateinit var preferencesManager: PreferencesManager

    private val MAX_FILES = 3
    private val selectedFiles = mutableListOf<File>()

    private lateinit var sendingDialog: CustomDialogFragment

    private var fragmentChangeListener: FragmentChangeListener? = null

    private lateinit var principal: Principal

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentIncidenceCreateChatbotBinding.inflate(inflater, container, false)

        steps = listOf(
        SpannableString(getString(R.string.welcome_chatbot)),
        SpannableString("${getString(R.string.issue_type)}:\n1. ${IncidenceType.PROBLEM.incidence}\n2. ${IncidenceType.QUESTION_REQUEST.incidence}\n3. ${IncidenceType.SUGGESTION.incidence}"),
        SpannableString(getString(R.string.subject)),
        SpannableString(getString(R.string.detail)),
        SpannableString("1. ${getString(R.string.send)}\n2. ${getString (R.string.show_issue_summary)}\n3. ${getString (R.string.exit)}"),
        )

        val recyclerView: RecyclerView = binding.recyclerChatbotViewMessages
        messageAdapter = IncidentChatbotAdapter(messageList)
        recyclerView.adapter = messageAdapter
        recyclerView.layoutManager = LinearLayoutManager(context)

        val sPreferences = getCustomSharedPreferences(binding.root.context)
        preferencesManager = PreferencesManager(sPreferences)

        principal = preferencesManager.getAuth()

        viewModel = CreateIncidenceViewModel()

        val sendButton: ImageButton = binding.btnSendChatbot
        val messageInput: EditText = binding.etMessageChatbot

        val attachButton: ImageButton = binding.btnAttachFile

        attachButton.setOnClickListener {
            showFileSelectionDialog()
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

        responseSteps = mutableListOf()

        sendButton.setOnClickListener {
            val messageText = messageInput.text.toString().trim()
            if (messageText.isNotEmpty()) {

                if (stepPosition == 1 && !validateDigitTyped(messageText, 1, 3)) {
                    addMessage(SpannableString(messageText))
                    messageInput.text.clear()
                    changeTurn()
                    addMessage(SpannableString(getString(R.string.unknown_issue_type)))
                    addMessage(steps[stepPosition])
                    changeTurn()
                }else {
                    addMessage(SpannableString(messageText))
                    messageInput.text.clear()
                    if (canAddResponseStep && currentTurn == MessageChatbotSentBy.USER) responseSteps.add(messageText)
                    changeTurn()
                    stepPosition++
                    when (stepPosition) {
                        5 -> {
                            if (!validateDigitTyped(messageText, 1, 3)){
                                addMessage(SpannableString(getString(R.string.unknown_option)))
                                stepPosition--
                                currentTurn = MessageChatbotSentBy.CHATBOT
                                addMessage(steps[stepPosition])
                                currentTurn = MessageChatbotSentBy.USER
                            } else {
                                when(messageText.toInt()){
                                    1 -> {
                                        //Copiar: Envio al servidor
                                        viewModel.createIncidence(responseStepsToIncidence())
                                        sendingDialog = CustomDialogFragment().newInstance(
                                            getString(R.string.submitting_issue),
                                            getString(R.string.please_wait_a_moment_while_the_attachment_is_are_being_sent),
                                            R.raw.sending,
                                            false
                                        )
                                        sendingDialog.show(parentFragmentManager, "CustomDialog")
                                    }
                                    2 -> {
                                        addMessage(showSummaryAssistedIncidence())
                                        stepPosition = steps.size - 1
                                        addMessageChatbot()
                                        stepPosition = 5
                                    }
                                    3 -> fragmentChangeListener?.onFragmentChange(IncidencesFragment.newInstance())
                                }
                                stepPosition--
                            }
                        }
                        else -> {
                            addMessageChatbot()
                        }
                    }
                }

            }
        }

        addMessageChatbot()

        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is FragmentChangeListener) {
            fragmentChangeListener = context
        }
    }

    //Copiar: Resultado del selector de archivos
    @SuppressLint("StringFormatInvalid")
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

    //Copiar: Transformar path uri en File
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


    //Copiar: Obrener nombre del archivo
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

    //Copiar: Abrir selector de archivos
    private fun openFileSelector() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.type = "*/*"
        startActivityForResult(intent, FILE_REQUEST_CODE)
    }

    //Copiar: Ver el dialogo con los archivos adjuntos
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

    //Copiar: Quitar los archivos de la lista cuando se han seleccionado
    private fun removeFile(file: File) {
        selectedFiles.remove(file)
        Toast.makeText(context, getString(R.string.file_deleted), Toast.LENGTH_SHORT).show()
    }

    //Copiar: Limpiar los archivos temporales luego de haberlo enviado al servidor
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

    private fun addMessageChatbot(){
        if (stepPosition < steps.size) {
            if (stepPosition == 0) {
                addMessage(steps[stepPosition])
                stepPosition++
                addMessage(steps[stepPosition])
            } else {
                addMessage(steps[stepPosition])
            }
        }else {
            canAddResponseStep = false
        }
        changeTurn()
    }

    private fun addMessage(text: SpannableString) {
        val message = ChatbotMessage(
            text = text,
            time = getCurrentTime(),
            sentBy = currentTurn
        )
        messageList.add(message)
        messageAdapter.notifyItemInserted(messageList.size - 1)

        binding.recyclerChatbotViewMessages.scrollToPosition(messageList.size - 1)
    }

    private fun changeTurn(){
        if (currentTurn == MessageChatbotSentBy.CHATBOT) {
            currentTurn = MessageChatbotSentBy.USER
        }else{
            currentTurn = MessageChatbotSentBy.CHATBOT
        }
    }

    private fun getCurrentTime(): String {
        val currentTime = Calendar.getInstance().time
        val formatter = SimpleDateFormat("HH:mm", Locale.getDefault())
        return formatter.format(currentTime)
    }

    private fun showSummaryAssistedIncidence(): SpannableString {
        val message = StringBuilder()
        message.append("${getString(R.string.summary_issue)}\n\n")
        message.append("${getString(R.string.issue_type)}:\n")
        message.append("${IncidenceType.entries[responseSteps[0].toInt() - 1].incidence}\n\n")
        message.append("${getString(R.string.subject_required)}:\n${responseSteps[1]}\n\n")
        message.append("${getString(R.string.detail)}:\n${responseSteps[2]}\n\n")
        message.append("${getString(R.string.attaches)}:\n${summaryMessafeFiles()}")

        val spannableMessage = SpannableString(message.toString())

        fun applyBoldSpan(label: String) {
            val startIndex = message.indexOf("$label:")
            if (startIndex != -1) {
                val endIndex = message.indexOf("\n", startIndex) + 1
                if (endIndex > startIndex) {
                    spannableMessage.setSpan(
                        StyleSpan(Typeface.BOLD),
                        startIndex,
                        endIndex,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
            }
        }

        // Aplica estilos en los encabezados que existen
        applyBoldSpan(getString(R.string.summary_issue))
        applyBoldSpan(getString(R.string.issue_type))
        applyBoldSpan(getString(R.string.subject_required))
        applyBoldSpan(getString(R.string.detail))
        applyBoldSpan(getString(R.string.attaches))

        return spannableMessage
    }

/*
    private fun showSummaryAssistedIncidence(): SpannableString {
        val message = StringBuilder()
        message.append("${getString(R.string.summary_issue)}\n\n")
        message.append("${getString(R.string.issue_type)}:\n")
        message.append("${IncidenceType.entries[responseSteps[0].toInt() - 1].incidence}\n\n")
        message.append("${getString(R.string.subject_required)}:\n${responseSteps[1]}\n\n")
        message.append("${getString(R.string.detail)}:\n${responseSteps[2]}\n\n")
        message.append("${getString(R.string.attaches)}:\n${summaryMessafeFiles()}")

        val spannableMessage = SpannableString(message.toString())

        spannableMessage.setSpan(
            StyleSpan(Typeface.BOLD),
            0,
            24,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        spannableMessage.setSpan(
            StyleSpan(Typeface.BOLD),
            message.indexOf("${getString(R.string.issue_type)}:"),
            message.indexOf("\n", message.indexOf("${getString(R.string.issue_type)}:")) + 1,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        spannableMessage.setSpan(
            StyleSpan(Typeface.BOLD),
            message.indexOf("${getString(R.string.subject)}:"),
            message.indexOf("\n", message.indexOf("${getString(R.string.subject)}:")) + 1,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        spannableMessage.setSpan(
            StyleSpan(Typeface.BOLD),
            message.indexOf("${getString(R.string.detail)}:"),
            message.indexOf("\n", message.indexOf("${getString(R.string.detail)}:")) + 1,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        spannableMessage.setSpan(
            StyleSpan(Typeface.BOLD),
            message.indexOf("${getString(R.string.attaches)}:"),
            message.indexOf("\n", message.indexOf("${getString(R.string.attaches)}:")) + 1,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        return spannableMessage
    }
*/
    fun validateDigitTyped(pos: String, start: Int, end: Int): Boolean {
        var position = 0
        return try {
            position = pos.toInt()
            position in end downTo start
        }catch (e: Exception) {
            return false
        }
    }

    //Copiar: Función que contruye el objeto para el viewmodel
    fun responseStepsToIncidence() : Incidence {
        return Incidence(
            type = IncidenceType.entries[responseSteps[0].toInt() - 1].incidence,
            subject = responseSteps[1],
            detail = responseSteps[2],
            personId = preferencesManager.getAuth().idPerson!!,
            files = selectedFiles,
            idCompany = principal.idCompany
        )
    }

    fun summaryMessafeFiles() : String{
        var message = ""
        selectedFiles.map {
            message += "${it.name}\n"
        }
        return message
    }

    companion object {
        private const val FILE_REQUEST_CODE = 1001
        const val TITLE = "Chatbot"
        @JvmStatic
        fun newInstance() =
            IncidenceCreateChatbotFragment()
    }
}