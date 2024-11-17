package com.uniandes.project.abcall.models

import android.text.SpannableString
import com.uniandes.project.abcall.enums.MessageChatbotSentBy

data class ChatbotMessage(
    val text: SpannableString,
    val time: String,
    val sentBy: MessageChatbotSentBy
)