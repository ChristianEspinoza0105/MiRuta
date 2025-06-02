package com.example.miruta.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
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
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.miruta.R
import com.example.miruta.ui.theme.AppTypography
import com.example.miruta.ui.viewmodel.AuthViewModel

fun getAvatarResourceByIndex(index: Int): Int {
    return when (index) {
        0 -> R.drawable.bus_verde_cono
        1 -> R.drawable.bus_verde_cachucha
        2 -> R.drawable.bus_blanco_copa
        3 -> R.drawable.bus_blanco_vaquero
        4 -> R.drawable.bus_rojo_ushanka
        5 -> R.drawable.bus_rojo_boina
        else -> R.drawable.bus_verde_cono
    }
}

@Composable
fun ProfileScreen(navController: NavController, authViewModel: AuthViewModel = hiltViewModel()) {
    val selectedImage = getAvatarResourceByIndex(authViewModel.photoIndex.toIntOrNull() ?: 0)

    val currentBackStackEntry by navController.currentBackStackEntryAsState()

    LaunchedEffect(currentBackStackEntry) {
        authViewModel.refreshUserData()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF00933B))
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 48.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(150.dp)
                        .clip(CircleShape)
                        .background(Color.LightGray)
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
                    text = authViewModel.userName,
                    fontSize = 28.sp,
                    color = Color.White,
                    fontFamily = AppTypography.headlineLarge.fontFamily,
                    fontWeight = AppTypography.headlineLarge.fontWeight,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

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
                    ProfileOption(
                        icon = Icons.Default.Edit,
                        iconColor = Color(0xFFFFC107),
                        text = "Edit profile",
                        textColor = Color.Black
                    ) {
                        if (authViewModel.role == "user") {
                            navController.navigate("EditProfileScreen") {
                                popUpTo("ProfileScreen") { inclusive = false }
                            }
                        } else {
                            navController.navigate("EditDriverScreen") {
                                popUpTo("ProfileScreen") { inclusive = false }
                            }
                        }
                    }

                    Divider(modifier = Modifier.padding(horizontal = 16.dp), color = Color.LightGray)

                    ProfileOption(
                        icon = Icons.Default.Notifications,
                        iconColor = Color(0xFFFFC107),
                        text = "Notifications",
                        textColor = Color.Black
                    ) {
                        // acción para notificaciones
                    }

                    Divider(modifier = Modifier.padding(horizontal = 16.dp), color = Color.LightGray)

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
            modifier = Modifier.size(32.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = text,
            fontSize = 20.sp,
            color = textColor,
            fontFamily = AppTypography.bodyLarge.fontFamily,
            fontWeight = AppTypography.bodyLarge.fontWeight,
        )
    }
}