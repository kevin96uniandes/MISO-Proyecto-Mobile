package com.uniandes.project.abcall.adapter

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.uniandes.project.abcall.R
import com.uniandes.project.abcall.models.ChatbotMessage

class IncidentChatbotAdapter (private val messages: List<ChatbotMessage>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_SENT = 1
        private const val VIEW_TYPE_RECEIVED = 2
    }

    override fun getItemViewType(position: Int): Int {
        return if (messages[position].isSent) VIEW_TYPE_SENT else VIEW_TYPE_RECEIVED
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_TYPE_SENT -> {
                val view = layoutInflater.inflate(R.layout.item_message_sent, parent, false)
                SentMessageViewHolder(view)
            }

            VIEW_TYPE_RECEIVED -> {
                val view = layoutInflater.inflate(R.layout.item_message_received, parent, false)
                ReceivedMessageViewHolder(view)
            }

            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messages[position]
        when (holder) {
            is SentMessageViewHolder -> holder.bind(message)
            is ReceivedMessageViewHolder -> holder.bind(message)
        }
    }

    override fun getItemCount(): Int = messages.size

    inner class SentMessageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val messageTextView: TextView = view.findViewById(R.id.textViewMessageSent)
        private val timeTextView: TextView = view.findViewById(R.id.textViewTimestampSent)
        fun bind(message: ChatbotMessage) {
            messageTextView.text = message.text
            timeTextView.text = message.time
        }
    }

    inner class ReceivedMessageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val messageTextView: TextView = view.findViewById(R.id.textViewMessageReceived)
        private val timeTextView: TextView = view.findViewById(R.id.textViewTimestampReceived)
        fun bind(message: ChatbotMessage) {
            messageTextView.text = message.text
            timeTextView.text = message.time
        }
    }
}