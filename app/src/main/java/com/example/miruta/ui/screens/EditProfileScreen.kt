package com.example.miruta.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.miruta.R

@Composable
fun EditProfileScreen(navController: NavController) {
    var selectedImage by remember { mutableStateOf(R.drawable.avatar_placeholder_1) }
    var showImagePopup by remember { mutableStateOf(false) }

    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF008000))
                .padding(top = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(modifier = Modifier.size(120.dp)) {
                Image(
                    painter = painterResource(id = selectedImage),
                    contentDescription = "Profile Image",
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .clickable { showImagePopup = true },
                    contentScale = ContentScale.Crop
                )
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit Icon",
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(24.dp),
                    tint = Color.White
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text("Editar perfil", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Color.White)

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = MaterialTheme.shapes.medium,
                elevation = 8.dp
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Nombre") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("Teléfono") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Correo") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            // Guardar y volver
                            navController.popBackStack()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF008000))
                    ) {
                        Text("Guardar", color = Color.White)
                    }
                }
            }
        }

        // AlertDialog para seleccionar imagen
        if (showImagePopup) {
            AlertDialog(
                onDismissRequest = { showImagePopup = false },
                text = {
                    LazyColumn {
                        items((0 until 20 step 4).toList()) { rowIndex ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                for (columnIndex in 0 until 4) {
                                    val index = rowIndex + columnIndex
                                    if (index < 20) {
                                        val imgId = getAvatarResource(index)
                                        Image(
                                            painter = painterResource(id = imgId),
                                            contentDescription = "Avatar $index",
                                            modifier = Modifier
                                                .size(60.dp)
                                                .padding(4.dp)
                                                .clip(CircleShape)
                                                .clickable {
                                                    selectedImage = imgId
                                                    showImagePopup = false
                                                }
                                        )
                                    }
                                }
                            }
                        }
                    }
                },
                buttons = {}
            )
        }
    }
}
