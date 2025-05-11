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
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.miruta.R
import com.example.miruta.ui.theme.AppTypography
import com.example.miruta.ui.viewmodel.AuthViewModel


import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.ui.platform.LocalConfiguration
import androidx.constraintlayout.compose.ConstraintLayout


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
fun ProfileScreen(navController: NavController, authViewModel: AuthViewModel = hiltViewModel()) {
    var selectedImage by remember { mutableStateOf(R.drawable.avatar_placeholder_1) }
    var showImagePopup by remember { mutableStateOf(false) }
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val titleFontSize = (screenWidth.value * 0.08).sp
    val subtitleFontSize = (screenWidth.value * 0.04).sp

    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF00933B))
    ) {
        val (header, form, image) = createRefs()
        val guidelineTop = createGuidelineFromTop(0.2f)

        // Header Section
        Box(
            modifier = Modifier
                .constrainAs(header) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
                .fillMaxWidth()
                .height(175.dp)
                .background(Color(0xFF00933B))
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Mi Perfil",
                    color = Color.White,
                    style = TextStyle(
                        fontFamily = AppTypography.h1.fontFamily,
                        fontWeight = AppTypography.h1.fontWeight,
                        fontSize = titleFontSize
                    ),
                    modifier = Modifier
                        .align(Alignment.Start)
                        .padding(start = 40.dp, top = 30.dp)
                )
                Text(
                    text = "Administra tu información personal",
                    color = Color.White,
                    style = TextStyle(
                        fontFamily = AppTypography.body1.fontFamily,
                        fontWeight = AppTypography.body1.fontWeight,
                        fontSize = subtitleFontSize
                    ),
                    modifier = Modifier
                        .align(Alignment.Start)
                        .padding(start = 45.dp, top = 10.dp)
                )
            }
        }

        // Main Content
        Box(
            modifier = Modifier
                .constrainAs(form) {
                    top.linkTo(guidelineTop)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    bottom.linkTo(parent.bottom)
                }
                .fillMaxHeight(0.85f)
                .background(
                    color = Color.White,
                    shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(24.dp))

                // Profile Image
                Box(
                    modifier = Modifier
                        .size(screenWidth * 0.3f)
                        .clip(CircleShape)
                        .clickable { showImagePopup = true }
                        .background(Color.LightGray)
                ) {
                    Image(
                        painter = painterResource(id = selectedImage),
                        contentDescription = "Profile Image",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    "User",
                    style = TextStyle(
                        fontFamily = AppTypography.h2.fontFamily,
                        fontWeight = AppTypography.h2.fontWeight,
                        fontSize = 24.sp
                    ),
                    color = Color(0xFF00933B)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Profile Options
                Column(modifier = Modifier.fillMaxWidth()) {
                    ProfileOption(
                        icon = Icons.Default.Edit,
                        text = "Editar perfil",
                        color = Color(0xFF00933B)
                    ) {
                        navController.navigate("editProfile")
                    }

                    Divider(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        color = Color.LightGray,
                        thickness = 1.dp
                    )

                    ProfileOption(
                        icon = Icons.Default.Notifications,
                        text = "Notificaciones",
                        color = Color(0xFF00933B)
                    ) {}

                    Divider(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        color = Color.LightGray,
                        thickness = 1.dp
                    )

                    ProfileOption(
                        icon = Icons.Default.ExitToApp,
                        text = "Cerrar sesión",
                        color = Color.Red
                    ) {
                        authViewModel.logout()
                        navController.navigate("LoginScreen") {
                            popUpTo("explore") { inclusive = true }
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

@Composable
fun ProfileOption(icon: ImageVector, text: String, color: Color = Color(0xFF00933B), onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 16.dp, horizontal = 24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = text,
            tint = color,
            modifier = Modifier.size(28.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = text,
            style = TextStyle(
                fontFamily = AppTypography.body1.fontFamily,
                fontWeight = AppTypography.body1.fontWeight,
                fontSize = 18.sp
            ),
            color = color
        )
    }
}