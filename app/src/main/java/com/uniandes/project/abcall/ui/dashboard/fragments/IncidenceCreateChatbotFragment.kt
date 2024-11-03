package com.uniandes.project.abcall.ui.dashboard.fragments

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
import com.uniandes.project.abcall.models.ChatbotMessage
import com.uniandes.project.abcall.models.Incidence
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

    private val steps: List<SpannableString> = listOf(
        SpannableString("Bienvenido a nuestro chat, es un placer guiarlo para crear una nueva incidencia.\nEn cualquier momento puede agregar documentos en el ícono de adjuntar que aparece en la parte inferior"),
        SpannableString("Tipo de incidencia:\n1. ${IncidenceType.PROBLEM.incidence}\n2. ${IncidenceType.QUESTION_REQUEST.incidence}\n3. ${IncidenceType.SUGGESTION.incidence}"),
        SpannableString("Asunto"),
        SpannableString("Detalle"),
        SpannableString("1. Enviar\n2. Ver resumen de la incidencia\n3. Salir"),
    )

    private var canAddResponseStep: Boolean = true

    private lateinit var responseSteps: MutableList<String>

    private var stepPosition = 0

    private lateinit var messageAdapter: IncidentChatbotAdapter
    private val messageList = mutableListOf<ChatbotMessage>()

    private var _binding: FragmentIncidenceCreateChatbotBinding? = null
    private val binding get() = _binding!!
    private var currentTurn: MessageChatbotSentBy = MessageChatbotSentBy.CHATBOT

    private lateinit var viewModel: CreateIncidenceViewModel
    private lateinit var fileUri: Uri
    private lateinit var preferencesManager: PreferencesManager
    private lateinit var jwtManager: JwtManager
    private lateinit var sharedPreferences: SharedPreferences

    private val MAX_FILES = 3
    private val selectedFiles = mutableListOf<File>()

    private lateinit var sendingDialog: CustomDialogFragment

    private var fragmentChangeListener: FragmentChangeListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentIncidenceCreateChatbotBinding.inflate(inflater, container, false)

        val recyclerView: RecyclerView = binding.recyclerChatbotViewMessages
        messageAdapter = IncidentChatbotAdapter(messageList)
        recyclerView.adapter = messageAdapter
        recyclerView.layoutManager = LinearLayoutManager(context)

        preferencesManager = PreferencesManager(binding.root.context)

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
                        "Incidencia envia satisfactoiamente",
                        "Su incidencia fué registrada exitosanmente, podrá visualizarla en su listado de incidencias",
                        R.raw.success
                    ) {
                        fragmentChangeListener?.onFragmentChange(IncidencesFragment.newInstance())
                        Toast.makeText(binding.root.context, "Error de red", Toast.LENGTH_SHORT).show()
                    }
                    dialog.show(parentFragmentManager, "CustomDialog")
                }
                is ApiResult.Error -> {
                    val dialog = CustomDialogFragment().newInstance(
                        "Error enviando incidencia",
                        "Por favor vuelva a intentarlo en unos minutos",
                        R.raw.error
                    )
                    dialog.show(parentFragmentManager, "CustomDialog")
                }
                ApiResult.NetworkError -> {
                    val dialog = CustomDialogFragment().newInstance(
                        "Registro de usuario",
                        "No se pudo conectar con el servidor. Por favor, verifica tu conexión a internet.",
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
                    addMessage(SpannableString("Tipo de incidencia desconocida"))
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
                            if (!validateDigitTyped(messageText, 1, 2)){
                                addMessage(SpannableString("Opción desconocida"))
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
                                            "Enviando incidencia",
                                            "Espere un momento mientras se envía(n) el(los) adjunto(s)",
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
        Toast.makeText(context, "Archivo eliminado", Toast.LENGTH_SHORT).show()
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
        message.append("Resumen de la incidencia\n\n")
        message.append("Tipo de incidencia:\n")
        message.append("${IncidenceType.entries[responseSteps[0].toInt() - 1].incidence}\n\n")
        message.append("Asunto:\n${responseSteps[1]}\n\n")
        message.append("Detalle:\n${responseSteps[2]}\n\n")
        message.append("Adjuntos:\n${summaryMessafeFiles()}")

        val spannableMessage = SpannableString(message.toString())

        spannableMessage.setSpan(
            StyleSpan(Typeface.BOLD),
            0,
            24,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        spannableMessage.setSpan(
            StyleSpan(Typeface.BOLD),
            message.indexOf("Tipo de incidencia:"),
            message.indexOf("\n", message.indexOf("Tipo de incidencia:")) + 1,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        spannableMessage.setSpan(
            StyleSpan(Typeface.BOLD),
            message.indexOf("Asunto:"),
            message.indexOf("\n", message.indexOf("Asunto:")) + 1,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        spannableMessage.setSpan(
            StyleSpan(Typeface.BOLD),
            message.indexOf("Detalle:"),
            message.indexOf("\n", message.indexOf("Detalle:")) + 1,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        spannableMessage.setSpan(
            StyleSpan(Typeface.BOLD),
            message.indexOf("Adjuntos:"),
            message.indexOf("\n", message.indexOf("Adjuntos:")) + 1,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        return spannableMessage
    }

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
            files = selectedFiles
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