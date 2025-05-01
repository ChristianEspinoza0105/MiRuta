package com.example.miruta.util

import androidx.compose.ui.graphics.Color

fun parseRouteColor(routeColor: String?): Color {
    return try {
        Color(android.graphics.Color.parseColor("#${routeColor ?: "000000"}"))
    } catch (e: Exception) {
        Color.Black
    }
}