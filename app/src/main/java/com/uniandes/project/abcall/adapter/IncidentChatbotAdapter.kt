package com.uniandes.project.abcall.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.uniandes.project.abcall.R
import com.uniandes.project.abcall.enums.MessageChatbotSentBy
import com.uniandes.project.abcall.models.ChatbotMessage

class IncidentChatbotAdapter (private val messages: List<ChatbotMessage>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_SENT = 1
        private const val VIEW_TYPE_RECEIVED = 2
    }

    override fun getItemViewType(position: Int): Int {
        return if (messages[position].sentBy == MessageChatbotSentBy.USER) {
            VIEW_TYPE_SENT
        } else {
            VIEW_TYPE_RECEIVED
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_SENT) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_message_sent, parent, false)
            SentMessageViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_message_received, parent, false)
            ReceivedMessageViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messages[position]
        if (holder is SentMessageViewHolder) {
            holder.bind(message)
        } else if (holder is ReceivedMessageViewHolder) {
            holder.bind(message)
        }
    }

    override fun getItemCount(): Int = messages.size
}

class SentMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val messageTextView: TextView = itemView.findViewById(R.id.textViewMessageSent)
    private val timestampTextView: TextView = itemView.findViewById(R.id.textViewTimestampSent)

    fun bind(message: ChatbotMessage) {
        messageTextView.text = message.text
        timestampTextView.text = message.time
    }
}

class ReceivedMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val messageTextView: TextView = itemView.findViewById(R.id.textViewMessageReceived)
    private val timestampTextView: TextView = itemView.findViewById(R.id.textViewTimestampReceived)

    fun bind(message: ChatbotMessage) {
        messageTextView.text = message.text
        timestampTextView.text = message.time
    }
}