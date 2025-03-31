package com.example.miruta.ui.screens

import android.util.Log
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
fun LogiinScreen(
    navController: NavController,
    authViewModel: AuthViewModel = hiltViewModel()
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf("") }

    val loginState by authViewModel.loginState

    // Función para validar el correo electrónico
    fun isValidEmail(email: String): Boolean {
        return email.contains("@") && email.contains(".")
    }

    // Función para validar la contraseña (no vacía en este caso)
    fun isValidPassword(password: String): Boolean {
        return password.isNotEmpty()
    }

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
            label = { Text("Correo electrónico") },
            isError = emailError.isNotEmpty()
        )
        if (emailError.isNotEmpty()) {
            Text(
                text = emailError,
                color = Color.Red,
                style = MaterialTheme.typography.body2
            )
        }

        // Campo de contraseña
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            isError = passwordError.isNotEmpty()
        )
        if (passwordError.isNotEmpty()) {
            Text(
                text = passwordError,
                color = Color.Red,
                style = MaterialTheme.typography.body2
            )
        }

        // Botón de inicio de sesión
        Button(
            onClick = {
                // Resetear los errores
                emailError = ""
                passwordError = ""

                // Eliminar espacios en blanco al inicio y al final de los campos
                val trimmedEmail = email.trim()
                val trimmedPassword = password.trim()

                // Validar los campos
                if (trimmedEmail.isEmpty()) {
                    emailError = "El correo electrónico no puede estar vacío."
                } else if (!isValidEmail(trimmedEmail)) {
                    emailError = "Correo electrónico no válido."
                }

                if (trimmedPassword.isEmpty()) {
                    passwordError = "La contraseña no puede estar vacía."
                }

                if (emailError.isEmpty() && passwordError.isEmpty()) {
                    // Intentar iniciar sesión si no hay errores de validación
                    authViewModel.login(trimmedEmail, trimmedPassword)
                }
            },
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("Ingresar")
        }

        // Mostrar el mensaje de estado de login
        loginState?.let {
            Text(
                text = it,
                color = if (it == "Login exitoso") Color.Green else Color.Red,
                modifier = Modifier.padding(top = 16.dp)
            )

            // Si el login fue exitoso, navegar a ProfileScreen
            if (it == "Login exitoso") {
                LaunchedEffect(true) {
                    Log.d("LoginScreen", "Navegando a ProfileScreen después del login exitoso")
                    navController.navigate("ProfileScreen") {
                        popUpTo("LoginScreen") { inclusive = true }
                    }
                }
            }
        }
    }
}