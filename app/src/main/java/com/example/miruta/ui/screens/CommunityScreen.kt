package com.example.miruta.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.miruta.R
import com.example.miruta.data.gtfs.parseRoutesFromGTFS
import com.example.miruta.data.models.Route
import com.example.miruta.ui.components.ChatCard
import com.example.miruta.ui.theme.AppTypography
import com.example.miruta.util.parseRouteColor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun CommunityScreen(navController: NavController) {
    val context = LocalContext.current
    var routes by remember { mutableStateOf<List<Route>>(emptyList()) }
    var searchQuery by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        val list = withContext(Dispatchers.IO) {
            context.assets.open("rutas_gtfs.zip").use { parseRoutesFromGTFS(it) }
        }
        routes = list
    }

    val filtered = remember(routes, searchQuery) {
        routes.filter { r ->
            r.routeShortName.contains(searchQuery, ignoreCase = true) ||
                    r.routeLongName.contains(searchQuery, ignoreCase = true)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier.padding(horizontal = 16.dp)
        )
            {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { query -> searchQuery = query },
                placeholder = { Text("Search community", color = Color.Gray, fontSize = 16.sp) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(35.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF00933B),
                    unfocusedBorderColor = Color(0xFFE7E7E7),
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                ),
                leadingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_app),
                        contentDescription = "Icono ruta",
                        modifier = Modifier
                            .padding(8.dp)
                            .size(24.dp),
                        tint = Color(0xFF00933B))
                },
                trailingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_search),
                        contentDescription = "Buscar",
                        modifier = Modifier
                            .padding(8.dp)
                            .size(24.dp)
                    )
                },
                singleLine = true
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Communities",
            fontSize = 28.sp,
            color = Color.Black,
            style = TextStyle(
                fontFamily = AppTypography.headlineLarge.fontFamily,
                fontWeight = AppTypography.headlineLarge.fontWeight
            ),
            modifier = Modifier.padding(horizontal = 30.dp, vertical = 8.dp)
        )

        Divider(
            color = Color(0xFFE0E0E0),
            thickness = 1.dp,
            modifier = Modifier
                .padding(vertical = 0.dp)
                .fillMaxWidth()
        )

        LazyColumn {
            items(filtered) { route ->
                val icon = painterResource(id = R.drawable.ic_chat)

                ChatCard(
                    title = route.routeShortName,
                    subtitle = route.routeLongName,
                    color = parseRouteColor(route.routeColor),
                    icon = icon,
                    onClick = {
                        navController.navigate("chat/${route.routeShortName}")
                    }
                )
            }
        }
    }
}
