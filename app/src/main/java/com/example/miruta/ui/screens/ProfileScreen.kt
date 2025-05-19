package com.example.miruta.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.miruta.R
import com.example.miruta.ui.theme.AppTypography
import com.example.miruta.ui.viewmodel.AuthViewModel

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
    return avatarResources.getOrElse(index % avatarResources.size) { R.drawable.avatar_placeholder_1 }
}

@Composable
fun ProfileScreen(navController: NavController, authViewModel: AuthViewModel = hiltViewModel()) {
    var selectedImage by remember { mutableStateOf(R.drawable.avatar_placeholder_1) }
    var showImagePopup by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF00933B))
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Parte superior: Imagen de perfil y texto centrado
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
                        .clickable { showImagePopup = true }
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
                        .background(Color(0xFFE0E0E0), shape = RoundedCornerShape(topStart = 50.dp, topEnd = 50.dp))
                ) {  }

                Column(
                    modifier = Modifier
                        .fillMaxWidth(0.88f)
                        .fillMaxHeight()
                        .align(Alignment.TopCenter)
                        .background(Color.White, shape = RoundedCornerShape(topStart = 50.dp, topEnd = 50.dp))
                        .padding(vertical = 24.dp)
                ) {
                    // Opción: Edit profile
                    ProfileOption(
                        icon = Icons.Default.Edit,
                        iconColor = Color(0xFFFFC107),
                        text = "Edit profile",
                        textColor = Color.Black
                    ) {
                        navController.navigate("EditProfileScreen") {
                            popUpTo("EditProfileScreen") { inclusive = true }
                        }
                    }

                    Divider(modifier = Modifier.padding(horizontal = 16.dp), color = Color.LightGray)

                    // Opción: Notifications
                    ProfileOption(
                        icon = Icons.Default.Notifications,
                        iconColor = Color(0xFFFFC107),
                        text = "Notifications",
                        textColor = Color.Black
                    ) {
                        // acción para notificaciones (si aplica)
                    }

                    Divider(modifier = Modifier.padding(horizontal = 16.dp), color = Color.LightGray)

                    // Opción: Log out
                    ProfileOption(
                        icon = Icons.Default.ExitToApp,
                        iconColor = Color.Red,
                        text = "Log out",
                        textColor = Color.Black
                    ) {
                        authViewModel.logout()
                        navController.navigate("LoginScreen") {
                            popUpTo("explore") { inclusive = true }
                        }
                    }
                }
            }
        }

        // Popup de selección de imagen
        if (showImagePopup) {
            AlertDialog(
                onDismissRequest = { showImagePopup = false },
                text = {
                    LazyColumn {
                        items((0 until 10 step 4).toList()) { rowIndex ->
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

@Composable
fun ProfileOption(
    icon: ImageVector,
    iconColor: Color,
    text: String,
    textColor: Color = Color.Black,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconColor,
            modifier = Modifier.size(32.dp) // Cambiado de 24.dp a 32.dp
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = text,
            fontSize = 20.sp, // Cambiado de 16.sp a 20.sp
            color = textColor,
            fontFamily = AppTypography.body1.fontFamily,
            fontWeight = AppTypography.body1.fontWeight,
        )
    }
}