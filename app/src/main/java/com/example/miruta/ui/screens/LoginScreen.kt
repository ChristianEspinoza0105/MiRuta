package com.example.miruta.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.miruta.ui.viewmodel.AuthViewModel

@Composable
fun LoginScreen(
    navController: NavController,
    authViewModel: AuthViewModel = hiltViewModel()
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val loginState by authViewModel.loginState

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Iniciar sesión", fontSize = 24.sp)

        // Campo de correo electrónico
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Correo electrónico") }
        )

        // Campo de contraseña
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation()
        )

        // Botón de inicio de sesión
        Button(
            onClick = { authViewModel.login(email, password) },
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("Ingresar")
        }

        // Mostrar mensaje de estado de login
        loginState?.let {
            Text(
                text = it,
                color = if (it == "Login exitoso") Color.Green else Color.Red,
                modifier = Modifier.padding(top = 16.dp)
            )

            // Si el login fue exitoso, navegar a ProfileScreen
            if (it == "Login exitoso") {
                LaunchedEffect(true) {
                    navController.navigate("ProfileScreen") {
                        popUpTo("LoginScreen") { inclusive = true }
                    }
                }
            }
        }
    }
}
