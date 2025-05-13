package com.example.miruta.utils

fun getChatId(user1Id: String, user2Id: String): String {
    return listOf(user1Id, user2Id).sorted().joinToString("_")
}