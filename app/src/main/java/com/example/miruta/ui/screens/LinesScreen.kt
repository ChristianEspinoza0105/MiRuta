package com.example.miruta.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.miruta.R
import com.example.miruta.data.gtfs.parseRoutesFromGTFS
import com.example.miruta.data.models.Route
import com.example.miruta.ui.components.RouteCard
import com.example.miruta.ui.theme.AppTypography
import com.example.miruta.util.parseRouteColor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun LinesScreen(navController: NavController) {
    val context = LocalContext.current
    var routes by remember { mutableStateOf<List<Route>>(emptyList()) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedTransport by remember { mutableStateOf<String?>(null) }

    val listState = rememberLazyListState()

    val imageFilters = listOf(
        R.drawable.mi_transporte to "mi_transporte",
        R.drawable.mi_tren to "mi_tren",
        R.drawable.mi_macro to "mi_macro"
    )

    val infiniteImages = remember {
        List(1000) { index -> imageFilters[index % imageFilters.size] }
    }

    val autoScrollJob = remember { mutableStateOf<Job?>(null) }

    LaunchedEffect(listState) {
        val job = launchAutoScroll(listState, infiniteImages)
        autoScrollJob.value = job

        snapshotFlow { listState.isScrollInProgress }.collect { isScrolling ->
            if (isScrolling) {
                autoScrollJob.value?.cancel()
            } else {
                delay(3000)
                if (!listState.isScrollInProgress) {
                    autoScrollJob.value = launchAutoScroll(listState, infiniteImages)
                }
            }
        }
    }


    LaunchedEffect(Unit) {
        val list = withContext(Dispatchers.IO) {
            context.assets.open("rutas_gtfs.zip").use { parseRoutesFromGTFS(it) }
        }
        routes = list
    }

    val filtered = remember(routes, searchQuery, selectedTransport) {
        routes.filter { route ->
            val matchesSearch = route.routeShortName.contains(searchQuery, ignoreCase = true) ||
                    route.routeLongName.contains(searchQuery, ignoreCase = true)

            val matchesFilter = when (selectedTransport) {
                "mi_transporte" -> route.routeShortName.startsWith("C", ignoreCase = true) ||
                        route.routeShortName.startsWith("T", ignoreCase = true)
                "mi_tren" -> route.routeLongName.equals("Linea 1 Periferico Sur-Auditorio", ignoreCase = true) ||
                        route.routeLongName.equals("Linea 2 Juarez-Tetlan", ignoreCase = true) ||
                        route.routeLongName.equals("Linea 3 Arcos Zapopan - Central Camionera", ignoreCase = true)
                "mi_macro" -> route.routeShortName.startsWith("M", ignoreCase = true)
                else -> true
            }

            matchesSearch && matchesFilter
        }
    }

    BackHandler {
        selectedTransport = null
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Box(modifier = Modifier.padding(horizontal = 16.dp)) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { query -> searchQuery = query },
                placeholder = { Text("Search route", color = Color.Gray, fontSize = 16.sp) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(35.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    backgroundColor = Color.White,
                    focusedBorderColor = Color(0xFF00933B),
                    unfocusedBorderColor = Color(0xFFE7E7E7)
                ),
                leadingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_app),
                        contentDescription = "Icono ruta",
                        modifier = Modifier
                            .padding(8.dp)
                            .size(24.dp),
                        tint = Color(0xFF00933B)
                    )
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

        val configuration = LocalConfiguration.current
        val screenWidth = configuration.screenWidthDp.dp
        val titleFontSize = (screenWidth.value * 0.20).sp

        Text(
            text = "Lines",
            fontSize = 28.sp,
            color = Color.Black,
            style = TextStyle(
                fontFamily = AppTypography.h1.fontFamily,
                fontWeight = AppTypography.h1.fontWeight,
                fontSize = titleFontSize
            ),
            modifier = Modifier
                .padding(horizontal = 30.dp, vertical = 8.dp)
        )

        Divider(
            color = Color(0xFFE0E0E0),
            thickness = 1.dp,
            modifier = Modifier.fillMaxWidth()
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
        ) {
            item {
                AnimatedVisibility(
                    visible = searchQuery.isEmpty() && selectedTransport == null,
                    enter = expandVertically() + fadeIn(),
                    exit = shrinkVertically() + fadeOut()
                ) {

                    LazyRow(
                        state = listState,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        items(infiniteImages) { (imageRes, filterType) ->
                            Box(
                                modifier = Modifier
                                    .width(200.dp)
                                    .fillMaxHeight()
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable {
                                        selectedTransport = if (selectedTransport == filterType) null else filterType
                                    }
                            ) {
                                Image(
                                    painter = painterResource(id = imageRes),
                                    contentDescription = "Option image",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }
                    }
                }
            }

            items(filtered) { route ->
                val icon = painterResource(id = R.drawable.ic_busline)

                RouteCard(
                    routeName = route.routeShortName,
                    description = route.routeLongName,
                    color = parseRouteColor(route.routeColor),
                    icon = icon,
                    onClick = {
                        val colorHex = route.routeColor ?: "000000"
                        navController.navigate("routeMap/${route.routeId}/$colorHex/${route.routeShortName}")
                    }
                )
            }
        }
    }
}

fun CoroutineScope.launchAutoScroll(
    listState: LazyListState,
    infiniteImages: List<Pair<Int, String>>
): Job {
    return launch {
        while (true) {
            listState.scrollBy(1f)
            delay(15)

            if (listState.firstVisibleItemIndex > infiniteImages.size - 20) {
                listState.scrollToItem(infiniteImages.size / 2)
            }
        }
    }
}