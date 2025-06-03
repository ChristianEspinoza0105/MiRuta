package com.example.miruta

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.miruta.ui.navigation.BottomNavGraph
import com.example.miruta.ui.components.BottomNavigationBar
import com.example.miruta.ui.theme.MiRutaTheme
import dagger.hilt.android.AndroidEntryPoint
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.miruta.ui.viewmodel.AuthViewModel
import com.google.android.libraries.places.api.Places


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, "AIzaSyBNbNDkpZPUO-jY3TzUUW_WqNmstyy3AuY")
        }
        setContent {
            MiRutaTheme {
                val navController = rememberNavController()
                val authViewModel: AuthViewModel = hiltViewModel()
                val isUserLoggedIn by authViewModel.isUserLoggedIn.collectAsState()

                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                val showBottomBar = currentRoute?.startsWith("chat/") == false &&
                        currentRoute?.startsWith("routeMap/") == false &&
                        currentRoute?.startsWith("live_location_map/") == false &&
                        currentRoute != "RecoverPasswordScreen"


                Scaffold(
                    bottomBar = {
                        if (showBottomBar) {
                            BottomNavigationBar(
                                navController = navController,
                                isUserLoggedIn = isUserLoggedIn
                            )
                        }
                    }
                ) { _ ->
                    Box(modifier = Modifier) {
                        BottomNavGraph(
                            navController = navController,
                            authViewModel = authViewModel
                        )
                    }
                }
            }
        }
    }
}
