package com.example.miruta.ui.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.miruta.R
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.miruta.ui.theme.AppTypography
import com.example.miruta.ui.viewmodel.AuthViewModel


private val avatarResources = listOf(
    R.drawable.bus_verde_cono,
    R.drawable.bus_verde_cachucha,
    R.drawable.bus_blanco_copa,
    R.drawable.bus_blanco_vaquero,
    R.drawable.bus_rojo_ushanka,
    R.drawable.bus_rojo_boina
)

private fun getAvatarResource(index: Int): Int {
    return avatarResources.getOrElse(index) { R.drawable.bus_verde_cono }
}

@Composable
fun EditDriverScreen(navController: NavController, authViewModel: AuthViewModel = hiltViewModel()) {
    val selectedImage = remember { mutableStateOf(R.drawable.bus_verde_cono) }
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var plates by remember { mutableStateOf("") }
    var route by remember { mutableStateOf("") }


    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val context = LocalContext.current

    // Cargar datos del usuario al iniciar
    LaunchedEffect(Unit) {
        authViewModel.loadDriverData()
    }

    // Observar cambios en el ViewModel y reflejarlos
    LaunchedEffect(authViewModel.userName, authViewModel.userEmail, authViewModel.userPhone, authViewModel.photoIndex, authViewModel.plates, authViewModel.route) {
        name = authViewModel.userName
        phone = authViewModel.userPhone
        email = authViewModel.userEmail
        plates = authViewModel.plates
        route = authViewModel.route

        val index = authViewModel.photoIndex.toIntOrNull() ?: 0
        selectedImage.value = getAvatarResource(index)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF00933B))
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
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
                        .clickable {
                            navController.navigate("SelectImageScreen")
                        }
                ) {
                    Image(
                        painter = painterResource(id = selectedImage.value),
                        contentDescription = "Profile Image",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = name,
                    fontSize = 28.sp,
                    color = Color.White,
                    style = AppTypography.headlineLarge,
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
                        .background(
                            color = Color(0xFFE0E0E0),
                            shape = RoundedCornerShape(topStart = 50.dp, topEnd = 50.dp)
                        )
                ) {}

                Column(
                    modifier = Modifier
                        .fillMaxWidth(0.88f)
                        .fillMaxHeight()
                        .align(Alignment.TopCenter)
                        .background(
                            color = Color.White,
                            shape = RoundedCornerShape(topStart = 50.dp, topEnd = 50.dp)
                        )
                        .verticalScroll(rememberScrollState())
                        .padding(bottom = 60.dp)
                ) {
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
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Editar Perfil",
                            color = Color.Black,
                            fontSize = 24.sp,
                            style = AppTypography.headlineLarge
                        )
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = {
                                Text("Nombre", style = AppTypography.bodyLarge.copy(fontSize = 18.sp, color = Color.Gray))
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(60.dp),
                            shape = RoundedCornerShape(20.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF00933B),
                                unfocusedBorderColor = Color(0xFFE7E7E7),
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White
                            )
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = phone,
                            onValueChange = { phone = it },
                            label = {
                                Text("Teléfono", style = AppTypography.bodyLarge.copy(fontSize = 18.sp, color = Color.Gray))
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(60.dp),
                            shape = RoundedCornerShape(20.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF00933B),
                                unfocusedBorderColor = Color(0xFFE7E7E7),
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White
                            )

                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = {
                                Text("Correo electrónico", style = AppTypography.bodyLarge.copy(fontSize = 18.sp, color = Color.Gray))
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(60.dp),
                            shape = RoundedCornerShape(20.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF00933B),
                                unfocusedBorderColor = Color(0xFFE7E7E7),
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White
                            )
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = plates,
                            onValueChange = { plates = it },
                            label = {
                                Text("Placas", style = AppTypography.bodyLarge.copy(fontSize = 18.sp, color = Color.Gray))
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(60.dp),
                            shape = RoundedCornerShape(20.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF00933B),
                                unfocusedBorderColor = Color(0xFFE7E7E7),
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White
                            )
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = route,
                            onValueChange = { route = it },
                            label = {
                                Text("Ruta", style = AppTypography.bodyLarge.copy(fontSize = 18.sp, color = Color.Gray))
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(60.dp),
                            shape = RoundedCornerShape(20.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF00933B),
                                unfocusedBorderColor = Color(0xFFE7E7E7),
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White
                            )
                        )

                        Spacer(modifier = Modifier.height(32.dp))

                        Button(
                            onClick = {
                                authViewModel.updateDriverData(
                                    name = name,
                                    phone = phone,
                                    email = email,
                                    route = route,
                                    plates = plates,
                                    onResult = { success ->
                                        if (success) {
                                            Toast.makeText(context, "Datos actualizados correctamente", Toast.LENGTH_SHORT).show()
                                            navController.popBackStack()
                                        } else {
                                            Toast.makeText(context, "Error al actualizar datos", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(60.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00933B)),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Text(
                                text = "Guardar",
                                style = TextStyle(
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = Color.White
                            )
                        }


                        Spacer(modifier = Modifier.height(32.dp))
                    }
                }
            }
        }
    }
}