package com.example.miruta.data.models

data class ChatMessage(
    val senderId: String = "",
    val groupId: String = "",
    val message: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
