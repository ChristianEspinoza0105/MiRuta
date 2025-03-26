package com.example.miruta

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyComposeScreen()
        }
    }
}

@Composable
fun MyComposeScreen() {
    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        Text(text = "¡Hola, Jetpack Compose!")
        Button(onClick = { }) {
            Text("Presionar")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewScreen() {
    MyComposeScreen()
}