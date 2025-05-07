package com.example.miruta.ui.navigation

import androidx.annotation.DrawableRes
import com.example.miruta.R

sealed class BottomNavScreen(
    val route: String,
    val title: String,
    @DrawableRes val icon: Int,
    @DrawableRes val selectedIcon: Int
) {
    object Explore : BottomNavScreen("explore", "Explore", R.drawable.ic_inicio, R.drawable.ic_inicio_selected)
    object Community : BottomNavScreen("community", "Chat", R.drawable.ic_groups, R.drawable.ic_groups_selected)
    object Lines : BottomNavScreen("lines", "Lines", R.drawable.ic_lines, R.drawable.ic_lines_selected)
    object MyRoute : BottomNavScreen("myroute", "Route", R.drawable.ic_myroute, R.drawable.ic_myroute_selected)
    data class Auth(val isUserLoggedIn: Boolean) : BottomNavScreen(
        route = "auth",
        title = if (isUserLoggedIn) "Profile" else "Login",
        icon = R.drawable.ic_perfil,
        selectedIcon = R.drawable.ic_perfil_selected
    )
}
