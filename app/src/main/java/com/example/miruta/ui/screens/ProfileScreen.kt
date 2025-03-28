package com.example.miruta.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.miruta.ui.viewmodel.AuthViewModel

@Composable
fun ProfileScreen(navController: NavController, authViewModel: AuthViewModel = hiltViewModel()) {
    val isUserLoggedIn by authViewModel.isUserLoggedIn.collectAsState()

    if (isUserLoggedIn) {
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
                authViewModel.logout()
                navController.navigate("LoginScreen") {
                    popUpTo("explore") { inclusive = true }
                }
            }) {
                Text(text = "Cerrar sesión")
            }

        }
    }
}