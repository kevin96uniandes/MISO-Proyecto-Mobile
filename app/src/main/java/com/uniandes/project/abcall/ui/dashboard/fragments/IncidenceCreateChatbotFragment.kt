package com.uniandes.project.abcall.ui.dashboard.fragments

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
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
import com.uniandes.project.abcall.config.JwtManager
import com.uniandes.project.abcall.config.PreferencesManager
import com.uniandes.project.abcall.databinding.FragmentIncidenceCreateChatbotBinding
import com.uniandes.project.abcall.enums.IncidenceType
import com.uniandes.project.abcall.enums.MessageChatbotSentBy
import com.uniandes.project.abcall.enums.Technology
import com.uniandes.project.abcall.models.ChatbotMessage
import com.uniandes.project.abcall.models.Incidence
import com.uniandes.project.abcall.repositories.rest.IncidenceClient
import com.uniandes.project.abcall.ui.dashboard.intefaces.FragmentChangeListener
import com.uniandes.project.abcall.viewmodels.CreateIncidenceViewModel
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
        SpannableString("Gracias por preferirnos"),
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
    private val selectedFiles = mutableListOf<Uri>()

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

        viewModel = CreateIncidenceViewModel(binding.root.context)

        val sendButton: ImageButton = binding.btnSendChatbot
        val messageInput: EditText = binding.etMessageChatbot

        val attachButton: ImageButton = binding.btnAttachFile
        attachButton.setOnClickListener {
            showFileSelectionDialog()
        }

        viewModel.result.observe(viewLifecycleOwner) { result ->
            Log.d("Result", result.toString())
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
                                    1 -> viewModel.createIncidence(responseStepsToIncidence())
                                    2 -> {
                                        addMessage(showSummaryAssistedIncidence())
                                        stepPosition--
                                        addMessageChatbot()
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == FILE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                if (selectedFiles.size < MAX_FILES) {
                    selectedFiles.add(uri)
                    showFileSelectionDialog()
                } else {
                    Toast.makeText(context, "Solo puedes agregar hasta $MAX_FILES archivos.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun openFileSelector() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.type = "*/*"
        startActivityForResult(intent, FILE_REQUEST_CODE)
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

    private fun removeFile(uri: Uri) {
        selectedFiles.remove(uri)
        Toast.makeText(context, "Archivo eliminado", Toast.LENGTH_SHORT).show()
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
            message += "${it.lastPathSegment}\n"
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