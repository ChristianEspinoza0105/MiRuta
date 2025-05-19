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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.miruta.R

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextAlign
import com.example.miruta.ui.theme.AppTypography


private val avatarResources = listOf(
    R.drawable.avatar_placeholder_1,
    R.drawable.avatar_placeholder_2,
    R.drawable.avatar_placeholder_3,
    R.drawable.avatar_placeholder_4,
    R.drawable.avatar_placeholder_5,
    R.drawable.avatar_placeholder_6,
    R.drawable.avatar_placeholder_7,
    R.drawable.avatar_placeholder_8,
    R.drawable.avatar_placeholder_9,
    R.drawable.avatar_placeholder_10
)

private fun getAvatarResource(index: Int): Int {
    return avatarResources.getOrElse(index) { R.drawable.avatar_placeholder_1 }
}

@Composable
fun EditProfileScreen(navController: NavController) {
    var selectedImage by remember { mutableStateOf(R.drawable.avatar_placeholder_1) }
    var showImagePopup by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF00933B))
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Parte superior: Imagen de perfil centrada
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 48.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(150.dp) // Cambiado de 100.dp a 150.dp
                        .clip(CircleShape)
                        .background(Color.LightGray)
                        .clickable { navController.navigate("SelectImageScreen") }
                ) {
                    Image(
                        painter = painterResource(id = selectedImage),
                        contentDescription = "Profile Image",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "User",
                    fontSize = 28.sp, // Cambiado de 20.sp a 28.sp
                    color = Color.White,
                    fontFamily = AppTypography.h1.fontFamily,
                    fontWeight = AppTypography.h1.fontWeight,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Fondo gris con tarjeta blanca encima
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF00933B))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.90f)
                        .align(Alignment.BottomCenter)
                        .background(
                            color = Color(0xFFE0E0E0),
                            shape = RoundedCornerShape(topStart = 50.dp, topEnd = 50.dp)
                        )
                ) {  }

                Column(
                    modifier = Modifier
                        .fillMaxWidth(0.88f)
                        .fillMaxHeight()
                        .align(Alignment.TopCenter)
                        .background(
                            color = Color.White,
                            shape = RoundedCornerShape(topStart = 50.dp, topEnd = 50.dp)
                        )
                ) {
                    // Título "Edit Profile" con icono de lápiz
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 24.dp, start = 32.dp, end = 32.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Icon",
                            tint = Color(0xFFFFC107),
                            modifier = Modifier.size(32.dp) // Cambiado de 24.dp a 32.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Edit Profile",
                            color = Color.Black,
                            fontSize = 24.sp, // Cambiado de 18.sp a 24.sp
                            fontFamily = AppTypography.h1.fontFamily,
                            fontWeight = AppTypography.h1.fontWeight
                        )
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(modifier = Modifier.height(16.dp))

                        // Form Fields
                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = {
                                Text(
                                    "Name",
                                    style = TextStyle(
                                        color = Color.Gray,
                                        fontFamily = AppTypography.body1.fontFamily,
                                        fontWeight = AppTypography.body1.fontWeight,
                                        fontSize = 18.sp // Cambiado de 16.sp a 18.sp
                                    )
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(60.dp), // Aumentado de 56.dp a 60.dp
                            shape = RoundedCornerShape(20.dp),
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                focusedBorderColor = Color(0xFF00933B),
                                unfocusedBorderColor = Color(0xFFE7E7E7),
                                backgroundColor = Color.White
                            )
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = phone,
                            onValueChange = { phone = it },
                            label = {
                                Text(
                                    "Phone Number",
                                    style = TextStyle(
                                        color = Color.Gray,
                                        fontFamily = AppTypography.body1.fontFamily,
                                        fontWeight = AppTypography.body1.fontWeight,
                                        fontSize = 18.sp // Cambiado de 16.sp a 18.sp
                                    )
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(60.dp), // Aumentado de 56.dp a 60.dp
                            shape = RoundedCornerShape(20.dp),
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                focusedBorderColor = Color(0xFF00933B),
                                unfocusedBorderColor = Color(0xFFE7E7E7),
                                backgroundColor = Color.White
                            )
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = {
                                Text(
                                    "Email",
                                    style = TextStyle(
                                        color = Color.Gray,
                                        fontFamily = AppTypography.body1.fontFamily,
                                        fontWeight = AppTypography.body1.fontWeight,
                                        fontSize = 18.sp // Cambiado de 16.sp a 18.sp
                                    )
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(60.dp), // Aumentado de 56.dp a 60.dp
                            shape = RoundedCornerShape(20.dp),
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                focusedBorderColor = Color(0xFF00933B),
                                unfocusedBorderColor = Color(0xFFE7E7E7),
                                backgroundColor = Color.White
                            )
                        )

                        Spacer(modifier = Modifier.height(32.dp))

                        // Save Button
                        Button(
                            onClick = { navController.popBackStack() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(60.dp), // Aumentado de 56.dp a 60.dp
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF00933B)
                            ),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Text(
                                "Save",
                                style = TextStyle(
                                    fontFamily = AppTypography.button.fontFamily,
                                    fontWeight = AppTypography.button.fontWeight,
                                    fontSize = 24.sp
                                ),
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }

        // Avatar Selection Dialog
        if (showImagePopup) {
            AlertDialog(
                onDismissRequest = { showImagePopup = false },
                text = {
                    LazyColumn {
                        items((0 until 19 step 4).toList()) { rowIndex ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                for (columnIndex in 0 until 4) {
                                    val index = rowIndex + columnIndex
                                    if (index < 10) {
                                        val imgId = getAvatarResource(index)
                                        Image(
                                            painter = painterResource(id = imgId),
                                            contentDescription = "Avatar $index",
                                            modifier = Modifier
                                                .size(screenWidth * 0.15f)
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