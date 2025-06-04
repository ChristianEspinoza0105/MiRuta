package com.example.miruta.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Star
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.miruta.R
import com.example.miruta.ui.theme.AppTypography

@Composable
fun SuccessRoutineMessageCard(
    message: String,
    details: String = "",
    onDismiss: () -> Unit,
    durationMillis: Int = 3000
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
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E1)),
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
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(Color(0xFF00933B), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_white_clock),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = message,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontSize = 18.sp,
                                color = Color(0xFF52885C)
                            )
                        )
                        if (details.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = details,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontSize = 14.sp,
                                    color = Color(0xFF52885C)
                                )
                            )
                        }
                    }

                    IconButton(onClick = {
                        visible = false
                        onDismiss()
                    }) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "Cerrar",
                            tint = Color(0xFF52885C)
                        )
                    }
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progress)
                        .height(4.dp)
                        .background(Color(0xFF00933B))
                )
            }
        }
    }
}