package com.example.miruta.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.delay
import com.example.miruta.ui.viewmodel.AuthViewModel

@Composable
fun ProfileScreen(navController: NavController, authViewModel: AuthViewModel = hiltViewModel()) {
    val isUserLoggedIn by authViewModel.isUserLoggedIn.collectAsState()
    var isVisible by remember { mutableStateOf(true) }
    var shouldNavigate by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (isUserLoggedIn) {
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "Bienvenido al perfil")
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = {
                        isVisible = false
                        authViewModel.logout()
                        shouldNavigate = true
                    }) {
                        Text(text = "Cerrar sesión")
                    }
                }
            }
        }
    }

    if (shouldNavigate) {
        LaunchedEffect(Unit) {
            delay(500)
            navController.navigate("LoginScreen") {
                popUpTo("explore") { inclusive = true }
            }
            shouldNavigate = false
        }
    }
}