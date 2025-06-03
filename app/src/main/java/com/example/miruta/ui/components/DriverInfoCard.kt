package com.example.miruta.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.miruta.data.models.DriverLocation
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun DriverInfoCard(
    driver: DriverLocation,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    onContactClick: (() -> Unit)? = null
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = driver.driverName,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Cerrar")
                }
            }

            Spacer(Modifier.height(8.dp))

            // Información de ubicación
            LocationInfoRow("Última actualización:", formatTime(driver.lastUpdate))
            LocationInfoRow("Velocidad:", "${driver.speed?.toInt() ?: 0} km/h")
            LocationInfoRow("Precisión:", "${driver.accuracy?.toInt() ?: 0} m")

            // Botón de contacto
            onContactClick?.let {
                Spacer(Modifier.height(16.dp))
                Button(
                    onClick = it,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Contactar conductor")
                }
            }
        }
    }
}

@Composable
private fun LocationInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, color = Color.Gray)
        Text(text = value, fontWeight = FontWeight.Bold)
    }
    Spacer(Modifier.height(4.dp))
}

private fun formatTime(timestamp: Long): String {
    val sdf = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
    return sdf.format(Date(timestamp))
}