package com.example.miruta.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.miruta.data.gtfs.parseRoutesFromGTFS
import com.example.miruta.data.models.Route
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun LinesScreen(navController: NavController) {
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
            .padding(16.dp)
    ) {
        TextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Buscar ruta") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        LazyColumn {
            items<Route>(filtered) { route ->
                Text(
                    text = "${route.routeShortName} - ${route.routeLongName}",
                    style = MaterialTheme.typography.body1,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            val colorHex = route.routeColor ?: "000000"
                            navController.navigate("routeMap/${route.routeId}/$colorHex")
                        }
                        .padding(vertical = 8.dp)
                )
                Divider()
            }
        }
    }
}
