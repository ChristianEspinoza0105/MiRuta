package com.example.miruta.ui.screens

import com.example.miruta.R
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.shadow
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyRouteScreen() {
    // Estados para el BottomSheet de Routine
    val routineSheetState = rememberModalBottomSheetState()
    var showRoutineSheet by remember { mutableStateOf(false) }

    // Estados para el BottomSheet de Favorites
    val favoriteSheetState = rememberModalBottomSheetState()
    var showFavoriteSheet by remember { mutableStateOf(false) }

    //BottomSheet Route Search
    val routeSearchSheetState = rememberModalBottomSheetState()
    var showRouteSearchSheet by remember { mutableStateOf(false) }

    //BottomSheet Location Functions
    val locationSheetState = rememberModalBottomSheetState()
    var showLocationSheet by remember { mutableStateOf(false) }

    //BottomSheet ChoosemapLocation
    val chooseMapSheetState = rememberModalBottomSheetState()
    var showChooseMapSheet by remember { mutableStateOf(false) }

    //Bottonsheet Current Location
    val currentLocationSheetState = rememberModalBottomSheetState()
    var showCurrentLocationSheet by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()

    Scaffold(

    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Header()
            Spacer(modifier = Modifier.height(16.dp))
            RouteButtons(
                onFavoriteClick = {
                    showFavoriteSheet = true
                },
                onRoutineClick = {
                    showRoutineSheet = true
                }
            )
        }
    }

    // BottomSheet para Routine
    if (showRoutineSheet) {
        ModalBottomSheet(
            modifier = Modifier.fillMaxHeight(),
            onDismissRequest = { showRoutineSheet = false },
            sheetState = routineSheetState
        ) {
            RoutineBottomSheetContent(onDismiss = {
                coroutineScope.launch { routineSheetState.hide(); showRoutineSheet = false }
            })
        }
    }

    // BottomSheet para Favorites
    if (showFavoriteSheet) {
        ModalBottomSheet(
            modifier = Modifier.fillMaxHeight(),
            onDismissRequest = { showFavoriteSheet = false },
            sheetState = favoriteSheetState
        ) {
            FavoriteBottomSheetContent(
                onDismiss = {
                    coroutineScope.launch { favoriteSheetState.hide(); showFavoriteSheet = false }
                },
                onRouteButtonClick = {
                    showRouteSearchSheet = true
                },
                onLocationButtonClick = {
                    showLocationSheet = true
                }
            )
        }
    }


// BottomSheet para Route Search
    if (showRouteSearchSheet) {
        ModalBottomSheet(
            modifier = Modifier.fillMaxHeight(),
            onDismissRequest = { showRouteSearchSheet = false },
            sheetState = routeSearchSheetState
        ) {
            RouteSearchBottomSheetContent(onDismiss = {
                coroutineScope.launch { routeSearchSheetState.hide(); showRouteSearchSheet = false }
            })
        }
    }


//BottomSheet Location Functions
    if (showLocationSheet) {
        ModalBottomSheet(
            modifier = Modifier.fillMaxHeight(),
            onDismissRequest = { showLocationSheet = false },
            sheetState = locationSheetState
        ) {
            LocationBottomSheetContent(
                onDismiss = {
                    coroutineScope.launch {
                        locationSheetState.hide()
                        showLocationSheet = false
                    }
                },
                onChooseMapButtonClick = {
                    coroutineScope.launch {
                        // 1. Cerrar el sheet actual
                        locationSheetState.hide()
                        showLocationSheet = false
                        // 2. Abrir el nuevo sheet
                        chooseMapSheetState.show()
                        showChooseMapSheet = true
                    }
                },
                onCurrentLocationButtonClick = {
                    coroutineScope.launch {
                        locationSheetState.hide()
                        showLocationSheet = false
                        currentLocationSheetState.show()
                        showCurrentLocationSheet = true
                    }
                }
            )
        }
    }

    //BottomSheet ChoosemapLocation
    if (showChooseMapSheet) {
        ModalBottomSheet(
            modifier = Modifier.fillMaxHeight(),
            onDismissRequest = { showChooseMapSheet = false },
            sheetState = chooseMapSheetState
        ) {
            ChooseMapBottomSheetContent(
                onDismiss = {
                    coroutineScope.launch {
                        chooseMapSheetState.hide()
                        showChooseMapSheet = false
                    }
                },
                onChooseMapButtonClick = {
                    // Lógica cuando se confirma la ubicación en el mapa
                    coroutineScope.launch {
                        chooseMapSheetState.hide()
                        showChooseMapSheet = false
                        // Aquí puedes hacer algo con la ubicación seleccionada
                    }
                }
            )
        }
    }


//BottomSheet Current Location
    if (showCurrentLocationSheet) {
        ModalBottomSheet(
            modifier = Modifier.fillMaxHeight(),
            onDismissRequest = { showCurrentLocationSheet = false },
            sheetState = currentLocationSheetState
        ) {
            CurrentLocationBottomSheetContent(
                onDismiss = {
                    coroutineScope.launch {
                        currentLocationSheetState.hide()
                        showCurrentLocationSheet = false
                    }
                },
                onCurrentLocationButtonClick = {
                    coroutineScope.launch {
                        currentLocationSheetState.hide()
                        showCurrentLocationSheet = false

                    }
                }
            )
        }
    }
}

@Composable
fun CurrentLocationBottomSheetContent(
    onDismiss: () -> Unit,
    onCurrentLocationButtonClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .border(width = 4.dp, color = Color.Yellow, shape = RoundedCornerShape(15.dp))
                    .size(44.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_location_myroute),
                    contentDescription = null,
                    modifier = Modifier.size(36.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Location", fontSize = 18.sp)
            Spacer(modifier = Modifier.weight(1f))
            Card(
                modifier = Modifier.align(Alignment.Top),
                onClick = onDismiss,
            ) {
                Box() {
                    Image(
                        painter = painterResource(id = R.drawable.ic_exit),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Favorite Name Section
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "Favorite Name:",
                style = TextStyle(fontSize = 16.sp),
                modifier = Modifier.padding(bottom = 8.dp)
            )
            var favoriteName by remember { mutableStateOf("") }
            TextField(
                value = favoriteName,
                onValueChange = { favoriteName = it },
                modifier = Modifier
                    .shadow(
                        elevation = 10.600000381469727.dp,
                        spotColor = Color(0x40000000),
                        ambientColor = Color(0x40000000)
                    )
                    .background(Color.White, RoundedCornerShape(40))
                    .fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                ),
                shape = RoundedCornerShape(50)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Address Section
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "Address:",
                style = TextStyle(fontSize = 16.sp),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                var address by remember { mutableStateOf("") }
                TextField(
                    value = address,
                    onValueChange = { address = it },
                    modifier = Modifier
                        .shadow(
                            elevation = 10.600000381469727.dp,
                            spotColor = Color(0x40000000),
                            ambientColor = Color(0x40000000)
                        )
                        .background(Color.White, RoundedCornerShape(40))
                        .fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedContainerColor = Color.Transparent,
                    ),
                    shape = RoundedCornerShape(50)
                )

                Spacer(modifier = Modifier.width(8.dp))

                var isFavorite by remember { mutableStateOf(false) }
                IconButton(
                    onClick = { isFavorite = !isFavorite },
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Filled.Star else Icons.Outlined.Star,
                        contentDescription = "Favorito",
                        tint = if (isFavorite) Color.Yellow else Color.Gray,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun RoutineBottomSheetContent(onDismiss: () -> Unit) {
    Column(modifier = Modifier.padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            //Header
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(Color(0xFFFFFFFF), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_white_clock),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Routine",
                style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.weight(1f))
            Card(
                modifier = Modifier.align(Alignment.Top),
                onClick = onDismiss,
            ) {
                Box() {
                    Image(
                        painter = painterResource(id = R.drawable.ic_exit),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        //RT Name button
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                modifier = Modifier.align(alignment = Alignment.Start),
                text = "Routine Name:",
                style = TextStyle(fontSize = 16.sp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            var text by remember { mutableStateOf("") }
            TextField(
                value = text,
                onValueChange = { text = it },
                label = { null },
                modifier = Modifier
                    .shadow(
                        elevation = 10.600000381469727.dp,
                        spotColor = Color(0x40000000),
                        ambientColor = Color(0x40000000)
                    )
                    .background(Color.White, RoundedCornerShape(40))
                    .fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                ),
                shape = RoundedCornerShape(50)
            )
            Spacer(modifier = Modifier.height(16.dp))
            //Stops buttons
            Text(
                modifier = Modifier.align(alignment = Alignment.Start),
                text = "Stops:",
                style = TextStyle(fontSize = 16.sp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .height(200.dp)
                    .width(280.dp)
                    .background(Color.White, RoundedCornerShape(16.dp))
                    .padding(8.dp),

                ) {
                LazyColumn() {
                    items(6) { index ->
                        var text by remember { mutableStateOf("") }
                        TextField(
                            value = text,
                            onValueChange = { text = it },
                            label = { Text("Add stop") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.White),
                            colors = TextFieldDefaults.colors(
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                disabledIndicatorColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedContainerColor = Color.Transparent
                            )
                        )
                        Divider(modifier = Modifier.padding(vertical = 4.dp))
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(modifier = Modifier.align(alignment = Alignment.CenterHorizontally)) {
            Button(
                onClick = onDismiss,
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00933B)),
                modifier = Modifier
                    .height(66.dp)
                    .width(204.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    //Add Button
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(Color.White, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_clock_route),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Text(
                        text = "Add routine",
                        style = TextStyle(
                            fontSize = 18.sp,
                            fontFamily = FontFamily(Font(R.font.poppins_bold)),
                            fontWeight = FontWeight(600),
                            color = Color(0xFFFFFFFF)
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun FavoriteBottomSheetContent(
    onRouteButtonClick: () -> Unit,
    onLocationButtonClick: () -> Unit,
    onDismiss: () -> Unit
) {
    Column(modifier = Modifier.padding(16.dp)) {
        //Header
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(Color(0xFFFFC800), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_white_star),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Favorites",
                style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.weight(1f))
            Card(
                modifier = Modifier.align(Alignment.Top),
                onClick = onDismiss,
            ) {
                Box() {
                    Image(
                        painter = painterResource(id = R.drawable.ic_exit),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            // Route Card
            Card(
                onClick = { onRouteButtonClick() },
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                modifier = Modifier
                    .width(80.dp)
                    .height(120.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .height(120.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .border(
                                width = 4.dp,
                                color = Color.Yellow,
                                shape = RoundedCornerShape(15.dp)
                            )
                            .size(68.dp)

                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_route_favorite),
                            contentDescription = null,
                            modifier = Modifier
                                .size(62.dp)
                                .align(Alignment.Center)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "Route", fontSize = 16.sp)

                }
            }
            //Location Card
            Card(
                onClick = { onLocationButtonClick() },
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                modifier = Modifier
                    .width(80.dp)
                    .height(120.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .height(120.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .border(
                                width = 4.dp,
                                color = Color.Yellow,
                                shape = RoundedCornerShape(15.dp)
                            )
                            .size(68.dp)

                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_location_myroute),
                            contentDescription = null,
                            modifier = Modifier
                                .size(62.dp)
                                .align(Alignment.Center)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "Location", fontSize = 16.sp)

                }
            }
        }
    }
}

@Composable
fun RouteSearchBottomSheetContent(onDismiss: () -> Unit) {
    Column(modifier = Modifier.padding(16.dp)) {
        // Header
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .border(width = 4.dp, color = Color.Yellow, shape = RoundedCornerShape(15.dp))
                    .size(44.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_route_favorite),
                    contentDescription = null,
                    modifier = Modifier.size(36.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Route", fontSize = 18.sp)
            Spacer(modifier = Modifier.weight(1f))
            Card(
                modifier = Modifier.align(Alignment.Top),
                onClick = onDismiss
            ) {
                Box {
                    Image(
                        painter = painterResource(id = R.drawable.ic_exit),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        var text by remember { mutableStateOf("") }
        TextField(
            value = text,
            onValueChange = { text = it },
            label = { Text("Write your route") },
            modifier = Modifier
                .shadow(
                    elevation = 10.6.dp,
                    spotColor = Color(0x40000000),
                    ambientColor = Color(0x40000000)
                )
                .background(Color.White, RoundedCornerShape(40))
                .fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedContainerColor = Color.Transparent
            ),
            shape = RoundedCornerShape(50)
        )
    }
}

@Composable
fun LocationBottomSheetContent(
    onDismiss: () -> Unit,
    onChooseMapButtonClick: () -> Unit,
    onCurrentLocationButtonClick: () -> Unit,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .border(width = 4.dp, color = Color.Yellow, shape = RoundedCornerShape(15.dp))
                .size(44.dp),
            contentAlignment = Alignment.Center

        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_location_myroute),
                contentDescription = null,
                modifier = Modifier.size(36.dp)
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = "Location", fontSize = 18.sp)
        Spacer(modifier = Modifier.weight(1f))
        Card(
            modifier = Modifier.align(Alignment.Top),
            onClick = onDismiss
        ) {
            Box() {
                Image(
                    painter = painterResource(id = R.drawable.ic_exit),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
    Spacer(modifier = Modifier.height(16.dp))
    var text by remember { mutableStateOf("") }
    TextField(
        value = text,
        onValueChange = { text = it },
        label = { Text("Write address") },
        modifier = Modifier
            .shadow(
                elevation = 10.6.dp,
                spotColor = Color(0x40000000),
                ambientColor = Color(0x40000000)
            )
            .background(Color.White, RoundedCornerShape(40))
            .fillMaxWidth(),
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            focusedContainerColor = Color.Transparent
        ),
        shape = RoundedCornerShape(50)
    )
    Spacer(modifier = Modifier.height(16.dp))

    TextButton(
        onClick = { onCurrentLocationButtonClick() },
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = R.drawable.ic_routelogin),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Your current location", fontSize = 18.sp, color = Color.Black)
            }
        }
    }

    Divider(modifier = Modifier.padding(vertical = 4.dp))
    TextButton(
        onClick = { onChooseMapButtonClick() },
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = R.drawable.ic_mapa),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Choose on map", fontSize = 18.sp, color = Color.Black)
            }
        }
    }
}

@Composable
fun ChooseMapBottomSheetContent(
    onDismiss: () -> Unit,
    onChooseMapButtonClick: () -> Unit = {}  // Parámetro opcional con valor por defecto
) {
    Column(modifier = Modifier.padding(16.dp)) {
        // Contenido del mapa aquí...

        Button(
            onClick = onChooseMapButtonClick,  // Usar el callback
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Confirmar ubicación")
        }

        Button(
            onClick = onDismiss,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Cerrar")
        }
    }
}

@Composable
fun Header() {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(Color(0xFF00933B), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = null,
                tint = Color.White
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "My routes",
            style = TextStyle(
                fontSize = 24.sp,
                fontFamily = FontFamily(Font(R.font.poppins_bold)),
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                textAlign = TextAlign.Center
            )
        )
    }
}


@Composable
fun RouteButtons(
    onFavoriteClick: () -> Unit,
    onRoutineClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Favorites Card
        Card(
            modifier = Modifier
                .clickable { onFavoriteClick() }
                .weight(1f)
                .padding(end = 8.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFC800)),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(Color.White, CircleShape)
                        .align(Alignment.Start),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_star),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Favorites",
                    style = TextStyle(
                        fontSize = 24.sp,
                        fontFamily = FontFamily(Font(R.font.poppins_bold)),
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                )
            }
        }

        // Routine Card
        Card(
            modifier = Modifier
                .clickable { onRoutineClick() }
                .weight(1f)
                .padding(start = 8.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF00933B)),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(Color.White, CircleShape)
                        .align(Alignment.Start),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_clock_route),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Routine",
                    style = TextStyle(
                        fontSize = 24.sp,
                        fontFamily = FontFamily(Font(R.font.poppins_bold)),
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                )
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun CurrentLocationBottomSheetPreview() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        CurrentLocationBottomSheetContent(
            onDismiss = {},
            onCurrentLocationButtonClick = {}
        )
    }
}