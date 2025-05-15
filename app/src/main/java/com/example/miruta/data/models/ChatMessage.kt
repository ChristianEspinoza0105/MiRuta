package com.example.miruta.data.models

data class ChatMessage(
    val text: String = "",
    val senderId: String = "",
    val senderName: String = "",
    val timestamp: com.google.firebase.Timestamp? = null
)