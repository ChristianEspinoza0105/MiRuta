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
    object Community : BottomNavScreen("community", "Community", R.drawable.ic_groups, R.drawable.ic_groups_selected)
    object Lines : BottomNavScreen("lines", "Lines", R.drawable.ic_lines, R.drawable.ic_lines_selected)
    object MyRoute : BottomNavScreen("myroute", "My Route", R.drawable.ic_myroute, R.drawable.ic_myroute_selected)
    object Profile : BottomNavScreen("profile", "Profile", R.drawable.ic_perfil, R.drawable.ic_perfil_selected)
}
