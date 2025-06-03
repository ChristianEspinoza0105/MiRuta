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
import android.Manifest
import android.content.Context
import android.location.Geocoder
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Lock
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.miruta.data.gtfs.parseRoutesFromGTFS
import com.example.miruta.data.models.FavoriteLocation
import com.example.miruta.data.models.FavoriteRoute
import com.example.miruta.data.models.Route
import com.example.miruta.data.models.Routine
import com.example.miruta.data.models.RoutineStop
import com.example.miruta.data.repository.AuthRepository
import com.example.miruta.ui.components.BottomNavigationBar
import com.example.miruta.ui.components.FavoriteRouteCard
import com.example.miruta.ui.components.SuccessMessageCard
import com.example.miruta.ui.navigation.BottomNavScreen
import com.example.miruta.ui.theme.AppTypography
import com.example.miruta.ui.viewmodel.AuthViewModel
import com.example.miruta.ui.viewmodel.AuthViewModelFactory
import com.example.miruta.util.parseRouteColor
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.LocalTime
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.RectangularBounds
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.firebase.annotations.concurrent.Background
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyRouteScreen(navController: NavHostController, repository: AuthRepository) {
    val factory = AuthViewModelFactory(repository)
    val viewModel: AuthViewModel = viewModel(factory = factory)

    val isUserLoggedIn by viewModel.isUserLoggedIn.collectAsState()
    val userData by viewModel.userData.collectAsState()
    val senderName = userData?.get("name")?.toString() ?: "Anónimo"

    // Estados para el BottomSheet de Routine
    val routineSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showRoutineSheet by remember { mutableStateOf(false) }

    // Estados para el BottomSheet de Favorites
    val favoriteSheetState = rememberModalBottomSheetState()
    var showFavoriteSheet by remember { mutableStateOf(false) }

    //BottomSheet Route Search
    val routeSearchSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showRouteSearchSheet by remember { mutableStateOf(false) }

    //BottomSheet Location Functions
    val locationSheetState = rememberModalBottomSheetState()
    var showLocationSheet by remember { mutableStateOf(false) }

    //BottomSheet ChoosemapLocation
    val chooseMapSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    var showChooseMapSheet by remember { mutableStateOf(false) }

    //Bottonsheet Current Location
    val currentLocationSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showCurrentLocationSheet by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()


    if (isUserLoggedIn) {
        Scaffold() { padding ->
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
    }else {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(32.dp)
        ) {
            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                androidx.compose.material.Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Authentication Required",
                    tint = Color(0xFF00933B),
                    modifier = Modifier.size(80.dp)
                )

                androidx.compose.material.Text(
                    text = "Your route. Your community.",
                    style = AppTypography.h1.copy(fontSize = 24.sp),
                    color = Color.Black,
                    textAlign = TextAlign.Center
                )

                androidx.compose.material.Text(
                    text = "Log in to access routes, chat with other passengers, and get real-time updates.",
                    style = AppTypography.body1.copy(fontSize = 18.sp),
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )

                androidx.compose.material.Button(
                    onClick = {
                        navController.navigate(BottomNavScreen.Auth(false).route) {
                            popUpTo(BottomNavScreen.Auth(false).route) { inclusive = true }
                            launchSingleTop = true
                        }
                    },
                    shape = RoundedCornerShape(50),
                    colors = androidx.compose.material.ButtonDefaults.buttonColors(
                        backgroundColor = Color(0xFF00933B),
                        contentColor = Color.White
                    ),
                    modifier = Modifier
                        .height(56.dp)
                        .widthIn(min = 200.dp)
                ) {
                    androidx.compose.material.Text(
                        text = "Sign in",
                        style = AppTypography.h1.copy(fontSize = 24.sp)
                    )
                }

            }
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
            RouteSearchBottomSheetContent(
                onDismiss = {
                    coroutineScope.launch {
                        routeSearchSheetState.hide(); showRouteSearchSheet = false
                    }
                },
                navController = navController
            )
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
                        locationSheetState.hide()
                        showLocationSheet = false
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
            onDismissRequest = { showChooseMapSheet = false },
            sheetState = chooseMapSheetState
        ) {
            ChooseMapBottomSheetContent(
                onDismiss = { showChooseMapSheet = false },
                onChooseMapButtonClick = { latLng, name ->
                    // Implement the logic to add the location to the favorites list
                    //viewModel.addFavoriteLocation(latLng, name)
                },
                onCloseALlBottomSheet = {
                    coroutineScope.launch {
                        locationSheetState.hide()
                        showLocationSheet = false
                        favoriteSheetState.hide()
                        showFavoriteSheet = false
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
                },
                onCloseAllSheets = {
                    coroutineScope.launch {
                        // Cierra todas las sheets relevantes
                        locationSheetState.hide()
                        showLocationSheet = false
                        favoriteSheetState.hide()
                        showFavoriteSheet = false
                    }
                }
            )
        }
    }
}

@Composable
fun CurrentLocationBottomSheetContent(
    onDismiss: () -> Unit,
    onCurrentLocationButtonClick: () -> Unit,
    onCloseAllSheets: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {

    val coroutineScope = rememberCoroutineScope()
    var userLocation by remember { mutableStateOf<LatLng?>(null) }
    var address by remember { mutableStateOf("") }
    val context = LocalContext.current
    var suggestions by remember { mutableStateOf(listOf<AutocompletePrediction>()) }
    val defaultLocation = LatLng(20.659699, -103.349609)
    val placesClient = remember { PlacesClientProvider.getClient(context) }
    var favoriteName by remember { mutableStateOf("") }
    var isFavorite by remember {mutableStateOf(false)}

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
                    .border(
                        width = 4.dp,
                        color = Color(0xFFF3CF21),
                        shape = RoundedCornerShape(15.dp)
                    )
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

            TextField(
                value = favoriteName,
                onValueChange = { favoriteName = it },
                label = { Text("Add favorite name") },
                modifier = Modifier
                    .shadow(
                        elevation = 10.600000381469727.dp,
                        spotColor = Color(0x40000000),
                        ambientColor = Color(0x40000000)
                    )
                    .background(Color.White, RoundedCornerShape(40))
                    .fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    cursorColor = Color.Black,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                    focusedLabelColor = Color.Black,
                ),
                shape = RoundedCornerShape(50),
                singleLine = true
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Address Section
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.Start
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {

                TextField(
                    value = address,
                    onValueChange = { query ->
                        address = query
                        if (query.isNotBlank()) {
                            val locationBias = RectangularBounds.newInstance(
                                LatLngBounds.builder()
                                    .include(defaultLocation)
                                    .build()
                            )

                            val request = FindAutocompletePredictionsRequest.builder()
                                .setQuery(query)
                                .setLocationBias(locationBias)
                                .build()

                            placesClient.findAutocompletePredictions(request)
                                .addOnSuccessListener { response ->
                                    suggestions = response.autocompletePredictions
                                }
                                .addOnFailureListener {
                                    suggestions = emptyList()
                                }
                        } else {
                            suggestions = emptyList()
                        }
                    },
                    label = { Text("Add address") },
                    modifier = Modifier
                        .weight(1f)
                        .shadow(
                            elevation = 10.600000381469727.dp,
                            spotColor = Color(0x40000000),
                            ambientColor = Color(0x40000000)
                        )
                        .background(Color.White, RoundedCornerShape(40)),
                    colors = TextFieldDefaults.colors(
                        cursorColor = Color.Black,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedContainerColor = Color.Transparent,
                        focusedLabelColor = Color.Black,
                    ),
                    shape = RoundedCornerShape(50),
                    singleLine = true
                )

                Spacer(modifier = Modifier.width(8.dp))



                IconButton(
                    onClick = {
                        if (favoriteName.isNotEmpty() && address.isNotEmpty()) {
                            // Solo activa el efecto visual si no estamos ya en proceso
                            if (!isFavorite) { // Evita múltiples clicks durante el reset
                                isFavorite = true // Cambia a amarillo

                                val location = FavoriteLocation(
                                    name = favoriteName,
                                    address = address,
                                    latitude = userLocation?.latitude ?: 0.0,
                                    longitude = userLocation?.longitude ?: 0.0,
                                    isFavorite = true
                                )
                                viewModel.addFavoriteLocation(location)

                                // Lanza la corrutina usando el coroutineScope
                                coroutineScope.launch {
                                    delay(500) // Medio segundo de retraso
                                    favoriteName = "" // Resetea el nombre
                                    address = ""      // Resetea la dirección
                                    isFavorite = false // Vuelve a gris
                                    onCloseAllSheets()
                                }
                            }
                        } else {
                            Toast.makeText(context, "Please fill all the spaces", Toast.LENGTH_SHORT).show()
                        }
                    },
                    //quizas un modifier aqui
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Filled.Star else Icons.Outlined.Star,
                        contentDescription = "Favorito",
                        tint = if (isFavorite) Color(0xFFF3CF21) else Color.Gray,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        if(suggestions.isNotEmpty()){
            LazyColumn (
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(8.dp)
            ){
                suggestions.forEach { prediction ->
                    item {
                        val focusManager = LocalFocusManager.current
                        val keyboardController = LocalSoftwareKeyboardController.current
                        Text(
                            text = prediction.getFullText(null).toString(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable{
                                    address = prediction.getFullText(null).toString()
                                    suggestions = emptyList()
                                    focusManager.clearFocus()
                                    keyboardController?.hide()
                                }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun RoutineBottomSheetContent(
    onDismiss: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    //Variables del Dialog
    var showDialog by remember { mutableStateOf(false) }
    var selectedStopIndex by remember { mutableStateOf(-1) }
    var selectedPlace by remember { mutableStateOf<Place?>(null) }
    var selectedTime by remember { mutableStateOf<LocalTime?>(null) }



    //Variables de LazyColumn
    val initialStops = List(6) { "" }
    var stops by remember { mutableStateOf(initialStops) }
    var routineName by remember { mutableStateOf("") }

    //Variables para el mapa
    val context = LocalContext.current
    val defaultLocation = LatLng(20.659699, -103.349609)
    var userLocation by remember { mutableStateOf<LatLng?>(null) }
    var selectedLocation by remember { mutableStateOf<LatLng?>(null) }
    var locationName by remember { mutableStateOf("Choose a location in the map") }
    var isLoading by remember { mutableStateOf(true) }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(defaultLocation, 12f)
    }

    suspend fun getLocationName(latLng: LatLng): String {
        return try {
            val geocoder = Geocoder(context, Locale.getDefault())
            val addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)

            if (!addresses.isNullOrEmpty()) {
                val address = addresses[0]
                val sb = StringBuilder()
                for (i in 0..address.maxAddressLineIndex) {
                    sb.append(address.getAddressLine(i))
                    if (i < address.maxAddressLineIndex) sb.append(", ")
                }
                sb.toString()
            } else {
                "${latLng.latitude}, ${latLng.longitude}"
            }
        } catch (e: Exception) {
            Log.e("Geocoder", "Error getting location name", e)
            "${latLng.latitude}, ${latLng.longitude}"
        }
    }

    LaunchedEffect(selectedLocation) {
        selectedLocation?.let { latLng ->
            locationName = "Getting location..."
            locationName = getLocationName(latLng)
        }
    }

    val locationPermissionsState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )

    LaunchedEffect(locationPermissionsState.allPermissionsGranted) {
        if (locationPermissionsState.allPermissionsGranted) {
            try {
                val location = getDeviceLocation(context).await()
                val target = LatLng(location.latitude, location.longitude)
                userLocation = target
                cameraPositionState.position = CameraPosition.fromLatLngZoom(target, 15f)
                isLoading = false
            } catch (e: Exception) {
                userLocation = defaultLocation
                cameraPositionState.position = CameraPosition.fromLatLngZoom(defaultLocation, 12f)
                isLoading = false
            }
        } else {
            locationPermissionsState.launchMultiplePermissionRequest()
        }
    }

    if (showDialog) {
        val timePickerState = rememberTimePickerState()

        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { "Add Bus Stop "},
            text = {
                Column {
                    // Map view
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(16.dp))
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                        } else {
                            GoogleMap(
                                modifier = Modifier.fillMaxSize(),
                                cameraPositionState = cameraPositionState,
                                onMapClick = { latLng ->
                                    selectedLocation = latLng
                                }
                            ) {
                                selectedLocation?.let { latLng ->
                                    Marker(
                                        state = MarkerState(position = latLng),
                                        title = "Selected Location"
                                    )
                                }
                            }
                        }
                    }

                    Text(
                        text = locationName,
                        modifier = Modifier.padding(8.dp),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Column {
                        Text("Select time:", modifier = Modifier.padding(bottom = 8.dp))
                        TimePicker(
                            state = timePickerState,
                            colors = TimePickerDefaults.colors(
                                selectorColor = Color(0xFF00933B),
                                timeSelectorSelectedContainerColor = Color(0xFF00933B),
                                periodSelectorSelectedContainerColor = Color(0xFF00933B),
                            )
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    colors = ButtonDefaults.buttonColors(Color(0xFF00933B)),
                    onClick = {
                        stops = stops.toMutableList().apply {
                            this[selectedStopIndex] =
                                "$locationName at ${String.format("%02d:%02d", timePickerState.hour, timePickerState.minute)}"
                        }
                        showDialog = false
                    }
                ) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                Button(
                    colors = ButtonDefaults.buttonColors(Color(0xFF00933B)),
                    onClick = { showDialog = false }
                ) {
                    Text("Cancel")
                }
            },
        )
    }

    Column(modifier = Modifier
        .padding(16.dp)
        .fillMaxHeight()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            //Header
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(Color(0xFF00933B), CircleShape),
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
            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = routineName,
                onValueChange = { routineName = it },
                label = { Text("Add routine name") },
                modifier = Modifier
                    .shadow(
                        elevation = 10.600000381469727.dp,
                        spotColor = Color(0x40000000),
                        ambientColor = Color(0x40000000)
                    )
                    .background(Color.White, RoundedCornerShape(40))
                    .fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    cursorColor = Color.Black,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                    focusedLabelColor = Color.Black,
                ),
                shape = RoundedCornerShape(50),
                singleLine = true
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
                    .padding(8.dp)
            ) {
                Row {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .width(24.dp)
                            .fillMaxHeight(),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Divider(
                            color = Color(0xFF00933B),
                            modifier = Modifier
                                .width(2.dp)
                                .height(150.dp)
                        )
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = "Flecha vertical",
                            tint = Color(0xFF00933B),
                            modifier = Modifier
                                .width(24.dp)
                                .fillMaxHeight()
                                .weight(1f)
                        )
                    }

                    // LazyColumn con los campos de texto
                    LazyColumn(
                        modifier = Modifier.weight(1f)
                    ) {
                        itemsIndexed(stops) { index, stopText ->

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .fillMaxHeight(1f)
                                    .background(Color.White)
                                    .clickable {
                                        selectedStopIndex = index
                                        showDialog = true
                                    }
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = if (stopText.isEmpty()) "Click to add stop" else stopText,
                                    color = if (stopText.isEmpty()) LocalContentColor.current.copy(alpha = 1f)
                                    else LocalContentColor.current,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                            Divider(modifier = Modifier.padding(vertical = 4.dp))
                        }
                    }
                }
            }
            Box(
                modifier = Modifier
                    .background(color = Color.White, shape = RoundedCornerShape(50))
                    .align(alignment = Alignment.End)
            ){
                IconButton(
                    onClick = {
                        stops = stops + ""
                    },
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add",
                        tint = Color(0xFF00933B),
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(modifier = Modifier.align(alignment = Alignment.CenterHorizontally)) {
            Button(
                onClick = {
                    if (routineName.isNotEmpty() && stops.any { it.isNotEmpty() }) {
                        val routine = Routine(
                            name = routineName,
                            stops = stops.filter { it.isNotEmpty() }.map { stopText ->

                                RoutineStop(
                                    locationName = stopText,
                                    time = "00:00",
                                    latitude = 0.0,
                                    longitude = 0.0
                                )
                            },
                            userId = viewModel.auth.currentUser?.uid ?: ""
                        )
                        viewModel.addRoutine(routine)
                        onDismiss()
                    }
                },
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00933B)),
                modifier = Modifier
                    .height(66.dp)
                    .width(204.dp),

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
                                color = Color(0xFFF3CF21),
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
                    Text(
                        text = "Favorites",
                        style = TextStyle(fontSize = 16.sp)
                    )

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
                                color = Color(0xFFF3CF21),
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
fun RouteSearchBottomSheetContent(
    onDismiss: () -> Unit,
    navController: NavController,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    var routes by remember { mutableStateOf<List<Route>>(emptyList()) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedTransport by remember { mutableStateOf<String?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }
    val favoriteRoutes by viewModel.favoriteRoutes.collectAsState()
    var showSuccessMessage by remember { mutableStateOf(false) }
    var successMessage by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        val list = withContext(Dispatchers.IO) {
            context.assets.open("rutas_gtfs.zip").use { parseRoutesFromGTFS(it) }
        }
        routes = list
    }

    val filtered = remember(routes, searchQuery, selectedTransport, favoriteRoutes) {
        routes.filter { route ->
            val matchesSearch = route.routeShortName.contains(searchQuery, ignoreCase = true) ||
                    route.routeLongName.contains(searchQuery, ignoreCase = true)

            val isNotFavorite = favoriteRoutes.none { favorite ->
                favorite.routeId == route.routeId
            }

            matchesSearch && isNotFavorite
        }
    }

    BackHandler {
        selectedTransport = null
    }

    Box(modifier = Modifier.fillMaxSize()) {
    Column(modifier = Modifier.padding(16.dp)) {
        // Header
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .border(
                        width = 4.dp,
                        color = Color(0xFFF3CF21),
                        shape = RoundedCornerShape(15.dp)
                    )
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
            Text(
                text = "Favorites",
                style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold)
            )
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

        TextField(
            value = searchQuery,
            onValueChange = { query -> searchQuery = query },
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
                cursorColor = Color.Black,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedContainerColor = Color.Transparent,
                focusedLabelColor = Color.Black
            ),
            shape = RoundedCornerShape(50),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(filtered) { route ->
                FavoriteRouteCard(
                    routeName = route.routeShortName,
                    description = route.routeLongName,
                    color = parseRouteColor(route.routeColor),
                    icon = painterResource(id = R.drawable.ic_busline),
                    onFavoriteClick = {
                        viewModel.addFavoriteRoute(
                            FavoriteRoute(
                                routeId = route.routeId,
                                routeName = route.routeShortName,
                                routeDescription = route.routeLongName,
                                isFavorite = true
                            )
                        )
                    },
                    showSuccessMessage = { message ->
                        successMessage = message
                        showSuccessMessage = true
                    }
                )
            }
        }
    }

        if (showSuccessMessage) {
            SuccessMessageCard(
                message = successMessage,
                details = "You can see it in your favorites section.",
                onDismiss = { showSuccessMessage = false },
                durationMillis = 3000
            )
        }
    }
}

@Composable
fun LocationBottomSheetContent(
    onDismiss: () -> Unit,
    onChooseMapButtonClick: () -> Unit,
    onCurrentLocationButtonClick: () -> Unit,
) {
    Column(modifier = Modifier.padding(12.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .border(
                        width = 4.dp,
                        color = Color(0xFFF3CF21),
                        shape = RoundedCornerShape(15.dp)
                    )
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
            Text(
                text = "Favorites",
                style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold)
            )
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
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ChooseMapBottomSheetContent(
    onDismiss: () -> Unit,
    onChooseMapButtonClick: (LatLng, String) -> Unit = { _, _ -> },
    onCloseALlBottomSheet: () -> Unit = {},
    viewModel: AuthViewModel = hiltViewModel()
) {
    val locationPermissionsState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )
    val coroutineScope = rememberCoroutineScope()
    var isFavorite by remember { mutableStateOf(false) }
    var favoriteName by remember { mutableStateOf("") }
    val context = LocalContext.current
    val defaultLocation = LatLng(20.659699, -103.349609)
    var userLocation by remember { mutableStateOf<LatLng?>(null) }
    var selectedLocation by remember { mutableStateOf<LatLng?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var showSuccessMessage by remember { mutableStateOf(false) }
    var successDetails by remember { mutableStateOf("") }
    var locationName by remember { mutableStateOf("Selecciona una ubicación en el mapa") }
    var successMessage by remember { mutableStateOf("Ubicación guardada como favorita") }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(defaultLocation, 12f)
    }

    suspend fun getLocationName(latLng: LatLng): String {
        return try {
            val geocoder = Geocoder(context, Locale.getDefault())
            val addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
            if (!addresses.isNullOrEmpty()) {
                val address = addresses[0]
                val sb = StringBuilder()
                for (i in 0..address.maxAddressLineIndex) {
                    sb.append(address.getAddressLine(i))
                    if (i < address.maxAddressLineIndex) sb.append(", ")
                }
                sb.toString()
            } else {
                "${latLng.latitude}, ${latLng.longitude}"
            }
        } catch (e: Exception) {
            Log.e("Geocoder", "Error getting location name", e)
            "${latLng.latitude}, ${latLng.longitude}"
        }
    }

    LaunchedEffect(selectedLocation) {
        selectedLocation?.let { latLng ->
            locationName = "Obteniendo dirección..."
            locationName = getLocationName(latLng)
        }
    }

    LaunchedEffect(locationPermissionsState.allPermissionsGranted) {
        if (locationPermissionsState.allPermissionsGranted) {
            try {
                val location = getDeviceLocation(context).await()
                val target = LatLng(location.latitude, location.longitude)
                userLocation = target
                cameraPositionState.position = CameraPosition.fromLatLngZoom(target, 15f)
                isLoading = false
            } catch (e: Exception) {
                userLocation = defaultLocation
                cameraPositionState.position = CameraPosition.fromLatLngZoom(defaultLocation, 12f)
                isLoading = false
            }
        } else {
            locationPermissionsState.launchMultiplePermissionRequest()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {

        if (showSuccessMessage) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .zIndex(1f),
                contentAlignment = Alignment.TopCenter
            ) {
                SuccessMessageCard(
                    message = successMessage,
                    details = successDetails,
                    onDismiss = { showSuccessMessage = false }
                )
            }

            LaunchedEffect(Unit) {
                delay(3000)
                showSuccessMessage = false
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .border(
                            width = 4.dp,
                            color = Color(0xFFF3CF21),
                            shape = RoundedCornerShape(15.dp)
                        )
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
                Text(
                    text = "Favorites",
                    style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.weight(1f))
                Card(
                    modifier = Modifier.align(Alignment.Top),
                    onClick = onDismiss,
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

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(16.dp))
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                } else {
                    GoogleMap(
                        modifier = Modifier.fillMaxSize(),
                        cameraPositionState = cameraPositionState,
                        properties = MapProperties(
                            isMyLocationEnabled = locationPermissionsState.allPermissionsGranted
                        ),
                        uiSettings = MapUiSettings(
                            myLocationButtonEnabled = false,
                            zoomControlsEnabled = false
                        ),
                        onMapClick = { latLng -> selectedLocation = latLng }
                    ) {
                        selectedLocation?.let { latLng ->
                            Marker(
                                state = MarkerState(position = latLng),
                                title = "Selected Location",
                                icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED),
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                TextField(
                    value = favoriteName,
                    onValueChange = { favoriteName = it },
                    label = {
                        Text(
                            "Add favorite name",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Light
                        )
                    },
                    modifier = Modifier
                        .weight(1f)
                        .shadow(
                            elevation = 10.6.dp,
                            spotColor = Color(0x40000000),
                            ambientColor = Color(0x40000000)
                        )
                        .background(Color.White, RoundedCornerShape(40)),
                    colors = TextFieldDefaults.colors(
                        cursorColor = Color.Black,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedContainerColor = Color.Transparent,
                        focusedLabelColor = Color.Black,
                    ),
                    shape = RoundedCornerShape(50),
                    singleLine = true
                )

                IconButton(
                    onClick = {
                        selectedLocation?.let { latLng ->
                            if (favoriteName.isNotEmpty() && locationName.isNotEmpty()) {
                                isFavorite = true

                                val favoriteLocation = FavoriteLocation(
                                    name = favoriteName,
                                    address = locationName,
                                    latitude = latLng.latitude,
                                    longitude = latLng.longitude,
                                    isFavorite = true
                                )
                                viewModel.addFavoriteLocation(favoriteLocation)

                                successDetails = "$favoriteName\n$locationName"
                                showSuccessMessage = true

                                coroutineScope.launch {
                                    delay(500)
                                    favoriteName = ""
                                    locationName = "Selecciona una ubicación en el mapa"
                                    selectedLocation = null
                                    isFavorite = false
                                    onCloseALlBottomSheet()
                                }
                            } else {
                                Toast.makeText(context, "Please fill the name and choose a location", Toast.LENGTH_SHORT).show()
                            }
                        } ?: run {
                            Toast.makeText(context, "Please choose a location", Toast.LENGTH_SHORT).show()
                        }
                    }
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Filled.Star else Icons.Outlined.Star,
                        contentDescription = "Favorito",
                        tint = if (isFavorite) Color(0xFFF3CF21) else Color.Gray,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }
    }
}


private fun getDeviceLocation(context: Context) =
    LocationServices.getFusedLocationProviderClient(context)
        .lastLocation


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

object PlacesClientProvider {
    fun getClient(context: Context): PlacesClient {
        if (!Places.isInitialized()) {
            Places.initialize(context.applicationContext, "AIzaSyBNbNDkpZPUO-jY3TzUUW_WqNmstyy3AuY", Locale.getDefault())
        }
        return Places.createClient(context)
    }
}


@Preview(showBackground = true)
@Composable
fun CurrentLocationBottomSheetPreview() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        RoutineBottomSheetContent(onDismiss = {})
    }
}