package com.example.miruta.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.miruta.R
import com.example.miruta.ui.navigation.BottomNavScreen
import com.example.miruta.ui.theme.AppTypography
import com.google.accompanist.permissions.ExperimentalPermissionsApi

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun BottomNavigationBar(navController: NavController, isUserLoggedIn: Boolean) {
    val items = listOf(
        BottomNavScreen.Lines,
        BottomNavScreen.Community,
        BottomNavScreen.Explore,
        BottomNavScreen.MyRoute,
        BottomNavScreen.Auth(isUserLoggedIn),
    )

    NavigationBar(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .paint(
                painterResource(id = R.drawable.menu),
                contentScale = ContentScale.Crop
            ),
        containerColor = Color.Transparent,
        tonalElevation = 0.dp
    ) {
        val navBackStackEntry = navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry.value?.destination?.route

        items.forEach { screen ->
            val isSelected = currentRoute == screen.route
            val iconOffset by animateDpAsState(
                targetValue = if (isSelected) (-3).dp else 0.dp,
                animationSpec = tween(durationMillis = 300)
            )

            NavigationBarItem(
                icon = {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .padding(top = 15.dp)
                            .offset(y = iconOffset)
                    ) {
                        Image(
                            painter = painterResource(id = if (isSelected) screen.selectedIcon else screen.icon),
                            contentDescription = screen.title,
                            modifier = Modifier.size(35.dp)
                        )
                    }
                },
                label = {
                    Text(
                        screen.title,
                        style = TextStyle(
                            fontFamily = AppTypography.bodyLarge.fontFamily,
                            fontWeight = AppTypography.bodyLarge.fontWeight,
                            fontSize = if (isSelected) 11.sp else 10.sp,
                            color = if (isSelected) Color(0xFF00933B) else Color.Gray
                        )
                    )
                },
                selected = isSelected,
                alwaysShowLabel = true,
                onClick = {
                    if (!isSelected) {
                        navController.navigate(screen.route) {
                            popUpTo(navController.graph.startDestinationId) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color(0xFF00933B),
                    unselectedIconColor = Color.Gray,
                    selectedTextColor = Color(0xFF00933B),
                    unselectedTextColor = Color.Gray
                )
            )
        }
    }
}