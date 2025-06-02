package com.example.miruta.data.models

data class ChatMessage(
    val id: String = "",
    val text: String = "",
    val senderId: String = "",
    val senderName: String = "",
    val type: String = "text",
    val latitude: Double? = null,
    val longitude: Double? = null,
    val timestamp: com.google.firebase.Timestamp? = null,
    val liveLocationDocId: String? = null
)