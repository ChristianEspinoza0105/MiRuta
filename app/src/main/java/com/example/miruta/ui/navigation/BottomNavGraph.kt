package com.example.miruta.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.miruta.ui.screens.ChatScreen
import com.example.miruta.ui.screens.CommunityScreen
import com.example.miruta.ui.screens.EditProfileScreen
import com.example.miruta.ui.screens.ExploreScreen
import com.example.miruta.ui.screens.LinesScreen
import com.example.miruta.ui.screens.MapScreen
import com.example.miruta.ui.screens.MyRouteScreen
import com.example.miruta.ui.screens.LoginScreen
import com.example.miruta.ui.screens.ProfileScreen
import com.example.miruta.ui.screens.RegisterDriverScreen
import com.example.miruta.ui.screens.RegisterScreen
import com.example.miruta.ui.viewmodel.AuthViewModel

@Composable
fun BottomNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    authViewModel: AuthViewModel
) {
    val isUserLoggedIn by authViewModel.isUserLoggedIn.collectAsState()

    Box(modifier = Modifier) {
        NavHost(navController, startDestination = BottomNavScreen.Explore.route) {
            composable(BottomNavScreen.Explore.route) {
                ExploreScreen()
            }
            composable(BottomNavScreen.Community.route) {
                CommunityScreen(navController)
            }
            composable("chat/{routeName}") { backStackEntry ->
                val routeName = backStackEntry.arguments?.getString("routeName") ?: "Unknown"
                ChatScreen(routeName = routeName, repository = authViewModel.authRepository, navController = navController, onBackClick = { navController.popBackStack() }, onCircleIconClick = {})
            }

            composable(BottomNavScreen.Lines.route) {
                LinesScreen(navController)
            }
            composable(BottomNavScreen.MyRoute.route) {
                MyRouteScreen(navController)
            }

            composable("EditProfileScreen") {
                EditProfileScreen(navController)
            }

            composable(
                route = "routeMap/{routeId}/{color}/{routeShortName}",
                arguments = listOf(
                    navArgument("routeId") { type = NavType.StringType },
                    navArgument("color") { type = NavType.StringType },
                    navArgument("routeShortName") { type = NavType.StringType }
                )
            ) { back ->
                val id = back.arguments!!.getString("routeId")!!
                val hex = back.arguments!!.getString("color")!!
                val string = back.arguments!!.getString("routeShortName")!!
                MapScreen(routeId = id, routeColorHex = hex, routeShortName = string)
            }

            composable("RegisterScreen") {
                RegisterScreen(navController)
            }
            composable("LoginScreen") {
                LoginScreen(navController)
            }
            composable("ProfileScreen") {
                ProfileScreen(navController)
            }
            composable("RegisterDriverScreen") {
                RegisterDriverScreen(navController)
            }

            composable(BottomNavScreen.Auth(isUserLoggedIn).route) {
                if (isUserLoggedIn) {
                    ProfileScreen(navController)
                } else {
                    LoginScreen(navController)
                }
            }
        }
    }
}

