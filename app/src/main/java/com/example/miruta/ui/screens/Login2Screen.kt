package com.example.miruta.ui.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.miruta.R
import com.example.miruta.ui.theme.AppTypography
import com.example.miruta.ui.viewmodel.AuthViewModel

@Composable
fun Login2Screen(
    navController: NavController,
    authViewModel: AuthViewModel = hiltViewModel()
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf("") }

    val loginState by authViewModel.loginState

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Sección verde
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(Color(0xFF00933B)),
            ) {
                Text(
                    text = "Welcome Back",
                    color = Color.White,
                    style = TextStyle(
                        fontFamily = AppTypography.h1.fontFamily,
                        fontWeight = AppTypography.h1.fontWeight,
                        fontSize = 40.sp
                    )
                )
                Text(
                    text = "Always on route. Log in.",
                    color = Color.White,

                    style = TextStyle(
                        fontFamily = AppTypography.h1.fontFamily,
                        fontWeight = AppTypography.h1.fontWeight,
                        fontSize = 16.sp
                    )
                )
                Image(
                    painter = painterResource(id = R.drawable.ic_routelogin),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(155.dp, 144.dp)
                )
            }

            // Sección blanca con esquinas redondeadas
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(50.dp))
                    .background(Color.White, shape = RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp))
                    .padding(16.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Log In",
                        modifier = Modifier.align(Alignment.Start),
                        color = Color(0xFF00933B),
                        style = TextStyle(
                            fontFamily = AppTypography.h1.fontFamily,
                            fontWeight = AppTypography.h1.fontWeight,
                            fontSize = 48.sp
                        )
                    )

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Correo electrónico") },
                        isError = emailError.isNotEmpty(),
                        modifier = Modifier.padding(top = 15.dp)
                    )
                    if (emailError.isNotEmpty()) {
                        Text(text = emailError, color = Color.Red, fontSize = 14.sp)
                    }

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Contraseña") },
                        visualTransformation = PasswordVisualTransformation(),
                        isError = passwordError.isNotEmpty(),
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (passwordError.isNotEmpty()) {
                        Text(text = passwordError, color = Color.Red, fontSize = 14.sp)
                    }

                    Button(
                        onClick = {
                            emailError = ""
                            passwordError = ""

                            val trimmedEmail = email.trim()
                            val trimmedPassword = password.trim()

                            if (trimmedEmail.isEmpty()) {
                                emailError = "El correo electrónico no puede estar vacío."
                            } else if (!trimmedEmail.contains("@") || !trimmedEmail.contains(".")) {
                                emailError = "Correo electrónico no válido."
                            }

                            if (trimmedPassword.isEmpty()) {
                                passwordError = "La contraseña no puede estar vacía."
                            }

                            if (emailError.isEmpty() && passwordError.isEmpty()) {
                                authViewModel.login(trimmedEmail, trimmedPassword)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00933B)),
                        modifier = Modifier.padding(top = 25.dp)
                    ) {
                        Text("Iniciar sesión", color = Color.White)
                    }

                    loginState?.let {
                        Text(
                            text = it,
                            color = if (it == "Login exitoso") Color.Green else Color.Red,
                            modifier = Modifier.padding(top = 16.dp)
                        )

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
        }
    }
}

