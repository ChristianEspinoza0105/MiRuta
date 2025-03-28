package com.example.miruta.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.miruta.ui.components.BottomNavigationBar
import com.example.miruta.ui.screens.CommunityScreen
import com.example.miruta.ui.screens.ExploreScreen
import com.example.miruta.ui.screens.LinesScreen
import com.example.miruta.ui.screens.LoginScreen
import com.example.miruta.ui.screens.MyRouteScreen
import com.example.miruta.ui.screens.ProfileScreen
import com.example.miruta.ui.viewmodel.AuthViewModel
@Composable
fun BottomNavGraph(
    navController: NavHostController,
    padding: Modifier,
    authViewModel: AuthViewModel
) {
    val isUserLoggedIn by authViewModel.isUserLoggedIn.collectAsState()

    NavHost(navController, startDestination = BottomNavScreen.Explore.route) {
        composable(BottomNavScreen.Explore.route) { ExploreScreen() }
        composable(BottomNavScreen.Community.route) { CommunityScreen() }
        composable(BottomNavScreen.Lines.route) { LinesScreen() }
        composable(BottomNavScreen.MyRoute.route) { MyRouteScreen() }
        composable("LoginScreen") { LoginScreen(navController) }
        composable(BottomNavScreen.Auth(isUserLoggedIn).route) {
            if (isUserLoggedIn) {
                ProfileScreen(navController)
            } else {
                LoginScreen(navController)
            }
        }
    }
}
