package com.uniandes.project.abcall.ui.dashboard.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.uniandes.project.abcall.adapter.IncidentChatbotAdapter
import com.uniandes.project.abcall.databinding.FragmentIncidenceCreateChatbotBinding
import com.uniandes.project.abcall.models.ChatbotMessage
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class IncidenceCreateChatbotFragment : Fragment() {

    private val steps: List<String> = listOf(
        ""
    )

    private lateinit var messageAdapter: IncidentChatbotAdapter
    private val messageList = mutableListOf<ChatbotMessage>()

    private var _binding: FragmentIncidenceCreateChatbotBinding? = null
    private val binding get() = _binding!!
    private var messageIsSent: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentIncidenceCreateChatbotBinding.inflate(inflater, container, false)

        val recyclerView: RecyclerView = binding.recyclerChatbotViewMessages
        messageAdapter = IncidentChatbotAdapter(messageList)
        recyclerView.adapter = messageAdapter
        recyclerView.layoutManager = LinearLayoutManager(context)

        val sendButton: Button = binding.btnSendChatbot
        val messageInput: EditText = binding.etMessageChatbot

        sendButton.setOnClickListener {
            val messageText = messageInput.text.toString().trim()
            if (messageText.isNotEmpty()) {
                messageIsSent = !messageIsSent
                addMessage(messageText, isSent = messageIsSent)  // o isSent = false si es un mensaje recibido
                messageInput.text.clear()
            }
        }

        return binding.root
    }

    private fun addMessage(text: String, isSent: Boolean) {
        val message = ChatbotMessage(
            text = text,
            time = getCurrentTime(),
            isSent = isSent
        )
        messageList.add(message)
        messageAdapter.notifyItemInserted(messageList.size - 1)

        binding.recyclerChatbotViewMessages.scrollToPosition(messageList.size - 1)
    }

    private fun getCurrentTime(): String {
        val currentTime = Calendar.getInstance().time
        val formatter = SimpleDateFormat("HH:mm", Locale.getDefault())
        return formatter.format(currentTime)
    }

    companion object {
        const val TITLE = "Chatbot"
        @JvmStatic
        fun newInstance() =
            IncidenceCreateChatbotFragment()
    }
}