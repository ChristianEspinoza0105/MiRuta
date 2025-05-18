package com.example.miruta.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.miruta.R

@Composable
fun LoadingSpinner(
    modifier: Modifier = Modifier,
    isLoading: Boolean
) {
    if (isLoading) {
        val infiniteTransition = rememberInfiniteTransition()
        val angle by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(1000),
                repeatMode = RepeatMode.Restart
            )
        )

        Image(
            painter = painterResource(id = R.drawable.loading_spinner),
            contentDescription = "Loading spinner",
            modifier = modifier
                .size(100.dp)
                .rotate(angle)
        )
    }
}