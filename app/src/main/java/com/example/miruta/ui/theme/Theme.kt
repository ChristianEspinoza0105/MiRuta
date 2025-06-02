package com.example.miruta.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
fun MiRutaTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        typography = AppTypography,
        content = content
    )
}
