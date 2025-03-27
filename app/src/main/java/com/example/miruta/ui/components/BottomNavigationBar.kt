package com.example.miruta.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.Dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.miruta.ui.navigation.BottomNavScreen

@Composable
fun BottomNavigationBar(navController: NavController) {
    val items = listOf(
        BottomNavScreen.Lines,
        BottomNavScreen.Community,
        BottomNavScreen.Explore,
        BottomNavScreen.MyRoute,
        BottomNavScreen.Profile
    )

    BottomNavigation(
        backgroundColor = Color.White,
        contentColor = Color.Black,
        elevation = 8.dp
    ) {
        val navBackStackEntry = navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry.value?.destination?.route

        items.forEach { screen ->
            val isSelected = currentRoute == screen.route
            BottomNavigationItem(
                icon = {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            painter = painterResource(id = if (isSelected) screen.selectedIcon else screen.icon),
                            contentDescription = screen.title,
                            modifier = Modifier.size(60.dp)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                    }
                },
                label = { Text(screen.title) },
                selected = isSelected,
                alwaysShowLabel = true,
                onClick = {
                    if (!isSelected) {
                        navController.navigate(screen.route)
                    }
                }
            )
        }
    }
}
