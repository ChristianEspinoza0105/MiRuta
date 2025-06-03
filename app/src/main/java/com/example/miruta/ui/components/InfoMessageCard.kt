package com.example.miruta.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.miruta.ui.theme.AppTypography

@Composable
fun InfoMessageCard(
    message: String,
    color: Color,
    textColor: Color = Color.Black,
    icon: @Composable (() -> Unit)? = null,
    onDismiss: () -> Unit,
    durationMillis: Int = 4000
) {
    var visible by remember { mutableStateOf(true) }
    var progress by remember { mutableStateOf(1f) }

    LaunchedEffect(Unit) {
        val animationDuration = durationMillis.toLong()
        val startTime = withFrameNanos { it }
        while (true) {
            val elapsed = withFrameNanos { it } - startTime
            progress = 1f - (elapsed.toFloat() / (animationDuration * 1_000_000))
            if (progress <= 0f) {
                visible = false
                onDismiss()
                break
            }
            kotlinx.coroutines.yield()
        }
    }

    AnimatedVisibility(visible = visible) {
        Card(
            colors = CardDefaults.cardColors(containerColor = color),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (icon != null) {
                        icon()
                        Spacer(modifier = Modifier.width(12.dp))
                    }

                    Text(
                        text = message,
                        style = AppTypography.body1.copy(
                            fontSize = 16.sp,
                            color = textColor
                        ),
                        modifier = Modifier.weight(1f)
                    )

                    IconButton(onClick = {
                        visible = false
                        onDismiss()
                    }) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "Cerrar"
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth(progress)
                        .height(4.dp)
                        .background(textColor)
                )
            }
        }
    }
}
