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

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.platform.LocalConfiguration
import androidx.constraintlayout.compose.ConstraintLayout
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
    val titleFontSize = (screenWidth.value * 0.08).sp
    val subtitleFontSize = (screenWidth.value * 0.04).sp

    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF00933B))
    ) {
        val (header, form) = createRefs()
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
                    text = "Editar Perfil",
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
                    text = "Actualiza tu información personal",
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
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(24.dp))

                // Profile Image with Edit Button
                Box(
                    modifier = Modifier
                        .size(screenWidth * 0.3f)
                ) {
                    Image(
                        painter = painterResource(id = selectedImage),
                        contentDescription = "Profile Image",
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                            .clickable { showImagePopup = true },
                        contentScale = ContentScale.Crop
                    )

                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Icon",
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .size(28.dp)
                            .background(Color(0xFF00933B), CircleShape)
                            .padding(6.dp),
                        tint = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Form Fields
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = {
                        Text(
                            "Nombre",
                            style = TextStyle(
                                color = Color.DarkGray,
                                fontFamily = AppTypography.body1.fontFamily,
                                fontWeight = AppTypography.body1.fontWeight,
                                fontSize = 16.sp
                            )
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Color(0xFF00933B),
                        unfocusedBorderColor = Color(0xFFE7E7E7),
                        backgroundColor = Color(0xFFE7E7E7)
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = {
                        Text(
                            "Teléfono",
                            style = TextStyle(
                                color = Color.DarkGray,
                                fontFamily = AppTypography.body1.fontFamily,
                                fontWeight = AppTypography.body1.fontWeight,
                                fontSize = 16.sp
                            )
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Color(0xFF00933B),
                        unfocusedBorderColor = Color(0xFFE7E7E7),
                        backgroundColor = Color(0xFFE7E7E7)
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = {
                        Text(
                            "Correo electrónico",
                            style = TextStyle(
                                color = Color.DarkGray,
                                fontFamily = AppTypography.body1.fontFamily,
                                fontWeight = AppTypography.body1.fontWeight,
                                fontSize = 16.sp
                            )
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Color(0xFF00933B),
                        unfocusedBorderColor = Color(0xFFE7E7E7),
                        backgroundColor = Color(0xFFE7E7E7)
                    )
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Save Button
                Button(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF00933B)
                    ),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text(
                        "Guardar cambios",
                        style = TextStyle(
                            fontFamily = AppTypography.button.fontFamily,
                            fontWeight = AppTypography.button.fontWeight,
                            fontSize = 18.sp
                        ),
                        color = Color.White
                    )
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