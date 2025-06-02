package com.example.miruta.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.miruta.ui.viewmodel.AuthViewModel

@Composable
fun SelectImageScreen(navController: NavController, authViewModel: AuthViewModel = hiltViewModel()) {

    val avatars = listOf(
        R.drawable.bus_verde_cono,
        R.drawable.bus_verde_cachucha,
        R.drawable.bus_blanco_copa,
        R.drawable.bus_blanco_vaquero,
        R.drawable.bus_rojo_ushanka,
        R.drawable.bus_rojo_boina
    )

    val viewModelAvatarIndex by remember { derivedStateOf { authViewModel.photoIndex.toIntOrNull()?.coerceIn(0, avatars.lastIndex) ?: 0 } }

    var selectedAvatar by remember { mutableStateOf(avatars[viewModelAvatarIndex]) }

    // Escuchar cambios en photoIndex del ViewModel
    LaunchedEffect(viewModelAvatarIndex) {
        selectedAvatar = avatars[viewModelAvatarIndex]
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Fila superior: flecha + título centrado
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                modifier = Modifier
                    .size(28.dp)
                    .clickable { navController.popBackStack() }
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "Select Image",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                modifier = Modifier.weight(1f),
            )
        }

        Spacer(modifier = Modifier.height(64.dp))

        // Imagen seleccionada (previsualización)
        Box(
            modifier = Modifier
                .size(190.dp)
                .align(Alignment.CenterHorizontally)
                .clip(CircleShape)
                .background(Color(0xFFC8E6C9))
                .border(3.dp, Color(0xFF2E7D32), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = selectedAvatar),
                contentDescription = "Selected Image",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
            )
        }

        Spacer(modifier = Modifier.height(32.dp)) // Aumenté el espacio aquí de 24.dp a 32.dp

        // Grilla de imágenes
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            contentPadding = PaddingValues(8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            items(avatars) { avatar ->
                Box(
                    modifier = Modifier
                        //.size(80.dp)
                        .clip(CircleShape)
                        .clickable { selectedAvatar = avatar }
                        .border(
                            width = if (selectedAvatar == avatar) 3.dp else 1.dp,
                            color = if (selectedAvatar == avatar) Color(0xFF2E7D32) else Color.Gray,
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = avatar),
                        contentDescription = "Avatar",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize().clip(CircleShape)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(36.dp))

        androidx.compose.material3.Button(
            onClick = {
                val avatarIndex = avatars.indexOf(selectedAvatar)
                authViewModel.updateUserAvatar(avatarIndex) { success ->
                    if (success) {
                        navController.popBackStack()
                    } else {

                    }
                }
            },
            modifier = Modifier
                .width(300.dp)
                .height(60.dp)
                .align(alignment = Alignment.CenterHorizontally),
            colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                containerColor = Color(0xFF00933B)
            ),
            shape = RoundedCornerShape(20.dp)

        ) {
            androidx.compose.material3.Text(
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