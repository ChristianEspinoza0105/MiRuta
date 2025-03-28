package com.example.miruta

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.miruta.ui.navigation.BottomNavGraph
import com.example.miruta.ui.components.BottomNavigationBar
import com.example.miruta.ui.theme.MiRutaTheme
import dagger.hilt.android.AndroidEntryPoint
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.miruta.ui.viewmodel.AuthViewModel

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MiRutaTheme {
                val navController = rememberNavController()
                val authViewModel: AuthViewModel = hiltViewModel()
                val isUserLoggedIn by authViewModel.isUserLoggedIn.collectAsState()

                Scaffold(
                    bottomBar = {
                        BottomNavigationBar(
                            navController = navController,
                            isUserLoggedIn = isUserLoggedIn
                        )
                    }
                ) { paddingValues ->
                    BottomNavGraph(
                        navController = navController,
                        padding = Modifier.padding(paddingValues),
                        authViewModel = authViewModel
                    )
                }
            }
        }
    }
}

