package com.example.miruta.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import com.example.miruta.ui.components.NarrowBottomSheetScaffold
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.core.app.ActivityCompat
import com.example.miruta.R
import com.example.miruta.data.models.RoutePlanResponse
import com.example.miruta.data.network.RetrofitClient
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.RectangularBounds
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.navigation.NavHostController
import com.example.miruta.data.models.Leg
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberBottomSheetScaffoldState
import com.example.miruta.data.models.Itinerary
import androidx.compose.material3.SheetValue
import androidx.compose.ui.text.TextStyle
import com.example.miruta.ui.theme.AppTypography
import androidx.compose.foundation.Image
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.FloatingActionButton
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.miruta.data.models.DriverLocation
import com.example.miruta.data.repository.LiveLocationRepository
import com.example.miruta.data.repository.LiveLocationSharing
import com.example.miruta.data.repository.LiveLocationSharingDrivers
import com.example.miruta.ui.components.ActiveDriversMarkers
import com.example.miruta.ui.components.DriverInfoCard
import com.example.miruta.ui.components.LoadingSpinner
import com.example.miruta.ui.theme.PoppinsFontFamily
import com.example.miruta.ui.viewmodel.AuthViewModel
import com.example.miruta.utils.getLocationFlow
import com.example.miruta.viewmodel.LocationViewModel
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.Dot
import com.google.android.gms.maps.model.Gap
import com.google.android.gms.maps.model.JointType
import com.google.android.gms.maps.model.RoundCap
import com.google.firebase.auth.FirebaseAuth
import com.google.maps.android.compose.rememberMarkerState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ExploreScreen(
    navController: NavHostController? = null,
    authViewModel: AuthViewModel = hiltViewModel(),
    locationViewModel: LocationViewModel = hiltViewModel(),
    liveLocationSharing: LiveLocationSharingDrivers = hiltViewModel()
) {
    // Estados
    val userRole by authViewModel.userRole.collectAsState()
    val isDriver = userRole == "driver"
    var isSharingLocation by remember { mutableStateOf(false) }

    // Ubicación y conductores
    var userLocation by remember { mutableStateOf<LatLng?>(null) }
    val activeDrivers by locationViewModel.activeDrivers.collectAsState()
    var selectedDriver by remember { mutableStateOf<DriverLocation?>(null) }

    val context = LocalContext.current
    val defaultLocation = LatLng(20.659699, -103.349609)

    val currentUser = FirebaseAuth.getInstance().currentUser
    val cameraPositionState = rememberCameraPositionState()

    val locationPermissionsState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )

    val markersState = remember { mutableStateMapOf<String, MarkerState>() }

    LaunchedEffect(activeDrivers) {
        activeDrivers.forEach { driver ->
            markersState.getOrPut(driver.driverId) {
                MarkerState(position = LatLng(driver.latitude, driver.longitude))
            }.position = LatLng(driver.latitude, driver.longitude)
        }
    }

    LaunchedEffect(userLocation) {
        userLocation?.let {
            cameraPositionState.move(CameraUpdateFactory.newLatLngZoom(it, 15f))
        }
    }

    LaunchedEffect(Unit) {
        authViewModel.checkUserRole()
    }

    DisposableEffect(Unit) {
        onDispose {
            locationViewModel.stopSharingLocation()
            if (isSharingLocation) {
                liveLocationSharing.stopSharing()
            }
        }
    }

    var isLoading by remember { mutableStateOf(true) }

    var origen by remember { mutableStateOf("") }
    var destino by remember { mutableStateOf("") }

    var suggestions by remember { mutableStateOf(listOf<AutocompletePrediction>()) }
    var destinoSuggestions by remember { mutableStateOf(listOf<AutocompletePrediction>()) }

    val placesClient = remember { Places.createClient(context) }
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    var isOrigenFocused by remember { mutableStateOf(false) }
    var isDestinoFocused by remember { mutableStateOf(false) }

    var origenLatLng by remember { mutableStateOf<LatLng?>(null) }
    var destinoLatLng by remember { mutableStateOf<LatLng?>(null) }

    var routePlan by remember { mutableStateOf<RoutePlanResponse?>(null) }
    var isLoadingRoute by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val coroutineScope = rememberCoroutineScope()

    var selectedItineraryIndex by remember { mutableStateOf(0) }
    var routePoints by remember { mutableStateOf<Map<Int, List<LatLng>>>(emptyMap()) }

    val bottomSheetScaffoldState = rememberBottomSheetScaffoldState()

    var showRouteSteps by remember { mutableStateOf(false) }
    var selectedItineraryForSteps by remember { mutableStateOf<Itinerary?>(null) }

    var showBottomSheet by remember { mutableStateOf(false) }

    val shouldShowInputs = !isLoadingRoute && routePlan == null
    val shouldShowBottomSheet = (routePlan != null ||
            (origen.isEmpty() && destino.isEmpty())) &&
            suggestions.isEmpty() &&
            destinoSuggestions.isEmpty() &&
            !isOrigenFocused &&
            !isDestinoFocused &&
            !isLoadingRoute


    LaunchedEffect(shouldShowBottomSheet) {
        showBottomSheet = shouldShowBottomSheet
        if (shouldShowBottomSheet) {
            bottomSheetScaffoldState.bottomSheetState.expand()
        } else {
            bottomSheetScaffoldState.bottomSheetState.partialExpand()
        }
    }

    LaunchedEffect(routePlan) {
        if (routePlan != null) {
            bottomSheetScaffoldState.bottomSheetState.expand()
        }
    }

    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = false,
        confirmValueChange = { it != SheetValue.Hidden }
    )

    fun updateCameraPosition(origen: LatLng?, destino: LatLng?) {
        val positions = listOfNotNull(origen, destino)
        when (positions.size) {
            0 -> return
            1 -> cameraPositionState.move(
                CameraUpdateFactory.newLatLngZoom(positions[0], 12f)
            )

            else -> {
                val bounds = LatLngBounds.builder()
                positions.forEach { bounds.include(it) }
                try {
                    cameraPositionState.move(
                        CameraUpdateFactory.newLatLngBounds(bounds.build(), 100)
                    )
                } catch (e: Exception) {
                    cameraPositionState.move(
                        CameraUpdateFactory.newLatLngZoom(positions.first(), 12f)
                    )
                }
            }
        }
    }

    fun updateCameraForSelectedItinerary(itinerary: Itinerary?) {
        itinerary?.let {
            val bounds = LatLngBounds.builder()
            it.legs?.forEach { leg ->
                leg.legGeometry?.points?.let { encoded ->
                    decodePolyline(encoded).forEach { point ->
                        bounds.include(point)
                    }
                }
            }
            try {
                cameraPositionState.move(
                    CameraUpdateFactory.newLatLngBounds(bounds.build(), 100)
                )
            } catch (e: Exception) {
            }
        }
    }

    LaunchedEffect(selectedItineraryIndex) {
        routePlan?.plan?.itineraries?.get(selectedItineraryIndex)?.let {
            updateCameraForSelectedItinerary(it)
        }
    }

    fun getCurrentTimeAmPm(): String {
        val sdf = SimpleDateFormat("hh:mma", Locale.US)
        return sdf.format(Date()).lowercase(Locale.US)
    }

    fun getCurrentDate(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        return sdf.format(Date())
    }

    fun fetchRoutePlan(
        from: LatLng,
        to: LatLng,
        date: String? = null,
        time: String? = null
    ) {
        isLoadingRoute = true
        errorMessage = null

        coroutineScope.launch {
            try {
                val currentDate = date ?: getCurrentDate()
                val currentTime = time ?: getCurrentTimeAmPm()

                val response = RetrofitClient.otpApiService.getRoutePlan(
                    fromPlace = "${from.latitude},${from.longitude}",
                    toPlace = "${to.latitude},${to.longitude}",
                    mode = "TRANSIT",
                    date = currentDate,
                    time = currentTime,
                    ignoreRealtimeUpdates = true
                )

                routePlan = response
                sheetState.expand()

                val newPointsMap = mutableMapOf<Int, List<LatLng>>()
                response.plan?.itineraries?.forEachIndexed { index, itinerary ->
                    val points = mutableListOf<LatLng>()
                    itinerary.legs?.forEach { leg ->
                        leg.legGeometry?.points?.let { encoded ->
                            points += decodePolyline(encoded)
                        }
                    }
                    newPointsMap[index] = points
                }

                routePoints = newPointsMap
                selectedItineraryIndex = 0
                sheetState.expand()
            } catch (e: Exception) {
                errorMessage = "Error al obtener ruta: ${e.localizedMessage}"
                routePlan = null
            } finally {
                isLoadingRoute = false
            }
        }
    }

    LaunchedEffect(origenLatLng, destinoLatLng) {
        if (origenLatLng != null && destinoLatLng != null) {
            fetchRoutePlan(origenLatLng!!, destinoLatLng!!)
        }
    }

    LaunchedEffect(locationPermissionsState.allPermissionsGranted) {
        if (locationPermissionsState.allPermissionsGranted) {
            try {
                val location = getDeviceLocation(context)
                val target = LatLng(location.latitude, location.longitude)
                userLocation = target
                updateCameraPosition(target, null)
                isLoading = false
            } catch (e: Exception) {
                userLocation = defaultLocation
                updateCameraPosition(defaultLocation, null)
                isLoading = false
            }
        } else {
            locationPermissionsState.launchMultiplePermissionRequest()
        }
    }

    NarrowBottomSheetScaffold(
        showSheet = shouldShowBottomSheet,
        scaffoldState = bottomSheetScaffoldState,
        sheetPeekHeight = if (showRouteSteps) 100.dp else 100.dp,
        sheetContent = {
            if (routePlan != null) {
                if (showRouteSteps && selectedItineraryForSteps != null) {
                    RouteStepsView(
                        itinerary = selectedItineraryForSteps!!,
                        onBackClick = { showRouteSteps = false }
                    )
                } else {
                    RouteDetailsContent(
                        routePlan = routePlan!!,
                        selectedItineraryIndex = selectedItineraryIndex,
                        onItinerarySelected = { index ->
                            selectedItineraryIndex = index
                            updateCameraForSelectedItinerary(routePlan!!.plan?.itineraries?.get(index))
                        },
                        onShowSteps = { itinerary ->
                            selectedItineraryForSteps = itinerary
                            showRouteSteps = true
                        }
                    )
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "¿Cómo vas, Alex?",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                }
            }
        }
    ) { _ ->
        Box(modifier = Modifier.fillMaxSize()) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                properties = MapProperties(isMyLocationEnabled = true),
                uiSettings = MapUiSettings(myLocationButtonEnabled = false)
            ) {
                origenLatLng?.let {
                    Marker(
                        state = MarkerState(position = it),
                        title = "Origen",
                        icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_marker)
                    )
                }
                destinoLatLng?.let {
                    Marker(
                        state = MarkerState(position = it),
                        title = "Destino",
                        icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_marker)
                    )
                }

                routePoints[selectedItineraryIndex]?.let { points ->
                    routePlan?.plan?.itineraries?.get(selectedItineraryIndex)?.legs?.forEach { leg ->
                        leg.legGeometry?.points?.let { encoded ->
                            val decodedPoints = decodePolyline(encoded)
                            Polyline(
                                points = decodedPoints,
                                color = if (leg.mode == "WALK") Color.Gray else parseRouteColor(leg.routeColor),
                                width = 20f,
                                pattern = if (leg.mode == "WALK") listOf(Dot(), Gap(20f)) else null,
                                startCap = RoundCap(),
                                endCap = RoundCap(),
                                jointType = JointType.ROUND
                            )
                        }
                    }
                }

                userLocation?.let {
                    Marker(
                        state = MarkerState(position = it),
                        title = "Mi ubicación",
                        icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)
                    )
                }

                markersState.values.forEach { markerState ->
                    val driver = activeDrivers.find { driver ->
                        LatLng(driver.latitude, driver.longitude) == markerState.position
                    }

                    driver?.let { driverLocation ->
                        Marker(
                            state = markerState,
                            title = driverLocation.driverName,
                            snippet = "Actualizado: ${formatTime(driverLocation.lastUpdate)}",
                            onClick = {
                                selectedDriver = driverLocation
                                cameraPositionState.move(
                                    CameraUpdateFactory.newLatLngZoom(
                                        markerState.position,
                                        15f
                                    )
                                )
                                true
                            },
                            icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)
                        )
                    }
                }

            }

            if (isLoadingRoute) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    LoadingSpinner(isLoading = true)
                }
            }

            val showOverlay = isOrigenFocused || isDestinoFocused

            if (showOverlay) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White)
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .align(Alignment.TopCenter)
            ) {
                if (shouldShowInputs) {
                    OutlinedTextField(
                        value = origen,
                        onValueChange = { query ->
                            origen = query
                            if (query.isBlank()) {
                                origenLatLng = null
                                suggestions = emptyList()
                                updateCameraPosition(origenLatLng, destinoLatLng)
                            } else {
                                val request = FindAutocompletePredictionsRequest.builder()
                                    .setQuery(query)
                                    .setLocationBias(
                                        RectangularBounds.newInstance(
                                            LatLngBounds.builder().include(defaultLocation).build()
                                        )
                                    )
                                    .build()

                                placesClient.findAutocompletePredictions(request)
                                    .addOnSuccessListener { response ->
                                        suggestions = response.autocompletePredictions
                                    }
                                    .addOnFailureListener {
                                        suggestions = emptyList()
                                    }
                            }
                        },
                        placeholder = {
                            Text(
                                "Search origin",
                                color = Color.Gray,
                                fontSize = 16.sp
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .onFocusChanged { isOrigenFocused = it.isFocused },
                        shape = RoundedCornerShape(35.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedIndicatorColor = Color(0xFF00933B),
                            unfocusedIndicatorColor = Color(0xFFE7E7E7)
                        ),
                        leadingIcon = {
                            Icon(
                                painter = painterResource(id = if (isOrigenFocused) R.drawable.ic_back else R.drawable.ic_app),
                                contentDescription = "Icono origen",
                                modifier = Modifier
                                    .padding(8.dp)
                                    .size(24.dp)
                                    .clickable {
                                        if (isOrigenFocused) {
                                            origen = ""
                                            origenLatLng = null
                                            suggestions = emptyList()
                                            focusManager.clearFocus()
                                            updateCameraPosition(origenLatLng, destinoLatLng)
                                        }
                                    },
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

                    Spacer(modifier = Modifier.height(12.dp))

                    val mostrarDestino = origenLatLng != null

                    if (mostrarDestino) {
                        OutlinedTextField(
                            value = destino,
                            onValueChange = { query ->
                                destino = query
                                if (query.isBlank()) {
                                    destinoLatLng = null
                                    destinoSuggestions = emptyList()
                                    updateCameraPosition(origenLatLng, destinoLatLng)
                                } else {
                                    val request = FindAutocompletePredictionsRequest.builder()
                                        .setQuery(query)
                                        .setLocationBias(
                                            RectangularBounds.newInstance(
                                                LatLngBounds.builder().include(defaultLocation)
                                                    .build()
                                            )
                                        )
                                        .build()

                                    placesClient.findAutocompletePredictions(request)
                                        .addOnSuccessListener { response ->
                                            destinoSuggestions = response.autocompletePredictions
                                        }
                                        .addOnFailureListener {
                                            destinoSuggestions = emptyList()
                                        }
                                }
                            },
                            placeholder = {
                                Text(
                                    "Search destination",
                                    color = Color.Gray,
                                    fontSize = 16.sp
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .onFocusChanged { isDestinoFocused = it.isFocused },
                            shape = RoundedCornerShape(35.dp),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                                focusedIndicatorColor = Color(0xFF00933B),
                                unfocusedIndicatorColor = Color(0xFFE7E7E7)
                            ),
                            leadingIcon = {
                                Icon(
                                    painter = painterResource(id = if (isDestinoFocused) R.drawable.ic_back else R.drawable.ic_myroute_selected),
                                    contentDescription = "Icono destino",
                                    modifier = Modifier
                                        .padding(8.dp)
                                        .size(24.dp)
                                        .clickable {
                                            if (isDestinoFocused) {
                                                destino = ""
                                                destinoLatLng = null
                                                destinoSuggestions = emptyList()
                                                focusManager.clearFocus()
                                                updateCameraPosition(origenLatLng, destinoLatLng)
                                            }
                                        },
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
                } else if (routePlan != null) {
                        Column(
                            horizontalAlignment = Alignment.Start,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .shadow(elevation = 6.dp, shape = CircleShape, clip = false)
                                    .background(color = Color.White, shape = CircleShape)
                            ) {
                                IconButton(
                                    onClick = {
                                        origen = ""
                                        destino = ""
                                        origenLatLng = null
                                        destinoLatLng = null
                                        suggestions = emptyList()
                                        destinoSuggestions = emptyList()
                                        routePlan = null
                                        routePoints = emptyMap()
                                        updateCameraPosition(userLocation, null)
                                    },
                                    modifier = Modifier
                                        .size(50.dp)
                                ) {
                                    Image(
                                        painter = painterResource(id = R.drawable.ic_back),
                                        contentDescription = "Regresar",
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                }

                if (isOrigenFocused || isDestinoFocused) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White)
                            .padding(horizontal = 8.dp)
                    ) {
                        if (isOrigenFocused && userLocation != null && destinoLatLng != userLocation) {
                            SuggestionItem(
                                fullText = "Use current location",
                                onClick = {
                                    origen = "Ubicación actual"
                                    origenLatLng = userLocation
                                    suggestions = emptyList()
                                    focusManager.clearFocus()
                                    keyboardController?.hide()
                                    updateCameraPosition(origenLatLng, destinoLatLng)
                                },
                                isOrigin = true
                            )
                        }

                        suggestions.forEach { prediction ->
                            SuggestionItem(
                                fullText = prediction.getFullText(null).toString(),
                                onClick = {
                                    origen = prediction.getFullText(null).toString()
                                    suggestions = emptyList()
                                    focusManager.clearFocus()
                                    keyboardController?.hide()

                                    coroutineScope.launch {
                                        fetchPlaceLatLng(context, prediction.placeId)?.let {
                                            origenLatLng = it
                                            updateCameraPosition(origenLatLng, destinoLatLng)
                                        }
                                    }
                                },
                                isOrigin = true
                            )
                        }

                        if (isDestinoFocused && userLocation != null && origenLatLng != userLocation) {
                            SuggestionItem(
                                fullText = "Use current location",
                                onClick = {
                                    destino = "Ubicación actual"
                                    destinoLatLng = userLocation
                                    destinoSuggestions = emptyList()
                                    focusManager.clearFocus()
                                    keyboardController?.hide()
                                    updateCameraPosition(origenLatLng, destinoLatLng)
                                },
                                isOrigin = false
                            )
                        }

                        destinoSuggestions.forEach { prediction ->
                            SuggestionItem(
                                fullText = prediction.getFullText(null).toString(),
                                onClick = {
                                    destino = prediction.getFullText(null).toString()
                                    destinoSuggestions = emptyList()
                                    focusManager.clearFocus()
                                    keyboardController?.hide()

                                    coroutineScope.launch {
                                        fetchPlaceLatLng(context, prediction.placeId)?.let {
                                            destinoLatLng = it
                                            updateCameraPosition(origenLatLng, destinoLatLng)
                                        }
                                    }
                                },
                                isOrigin = false
                            )
                        }
                    }
                }

                errorMessage?.let {
                    Box(
                        modifier = Modifier
                            .fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = it,
                            color = Color.Red
                        )
                    }
                }
            }

            if (isDriver) {
                SharingLocationFAB(
                    isSharing = isSharingLocation,
                    onToggleSharing = { sharing ->
                        isSharingLocation = sharing
                    },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(horizontal = 20.dp, vertical = 120.dp)
                )
            }

            selectedDriver?.let { driver ->
                DriverInfoCard(
                    driver = driver,
                    onDismiss = { selectedDriver = null },
                    onContactClick = {
                        navController?.navigate("chat/${driver.driverId}")
                    },
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(16.dp)
                )
            }

            errorMessage?.let {
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(16.dp)
                        .background(Color.White, RoundedCornerShape(8.dp))
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = it,
                        color = Color.Red
                    )
                }
            }
        }
    }
}

private fun formatTime(timestamp: Long): String {
    val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
    return sdf.format(Date(timestamp))
}

private suspend fun getDeviceLocation(context: Context): android.location.Location {
    val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)

    if (ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED &&
        ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        throw SecurityException("Se requieren permisos de ubicación")
    }

    return suspendCoroutine { continuation ->
        fusedLocationProviderClient.lastLocation
            .addOnSuccessListener { location ->
                if (location != null) {
                    continuation.resume(location)
                } else {
                    continuation.resumeWithException(Exception("No se pudo obtener la ubicación"))
                }
            }
            .addOnFailureListener { e ->
                continuation.resumeWithException(e)
            }
    }
}

private suspend fun fetchPlaceLatLng(context: Context, placeId: String): LatLng? {
    val placesClient = Places.createClient(context)
    val placeFields = listOf(Place.Field.LAT_LNG)
    val request = FetchPlaceRequest.newInstance(placeId, placeFields)

    return try {
        val response = placesClient.fetchPlace(request).await()
        response.place.latLng
    } catch (e: Exception) {
        null
    }
}

fun decodePolyline(encoded: String): List<LatLng> {
    val poly = ArrayList<LatLng>()
    var index = 0
    val len = encoded.length
    var lat = 0
    var lng = 0

    while (index < len) {
        var result = 0
        var shift = 0
        var b: Int

        do {
            b = encoded[index++].code - 63
            result = result or ((b and 0x1f) shl shift)
            shift += 5
        } while (b >= 0x20)

        val dlat = if ((result and 1) != 0) (result shr 1).inv() else (result shr 1)
        lat += dlat

        result = 0
        shift = 0

        do {
            b = encoded[index++].code - 63
            result = result or ((b and 0x1f) shl shift)
            shift += 5
        } while (b >= 0x20)

        val dlng = if ((result and 1) != 0) (result shr 1).inv() else (result shr 1)
        lng += dlng

        poly.add(LatLng(lat / 1E5, lng / 1E5))
    }

    return poly
}

fun sortRoutesByDuration(itineraries: List<Itinerary>): List<Pair<Int, Itinerary>> {
    return itineraries.mapIndexed { index, itinerary ->
        index to itinerary
    }.sortedBy { (_, itinerary) ->
        itinerary.duration ?: Int.MAX_VALUE
    }
}

@Composable
fun RouteStepsView(
    itinerary: Itinerary,
    onBackClick: () -> Unit
) {
    val steps = itinerary.legs ?: emptyList()
    val enableAnimations = steps.size <= 10

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_back),
                    contentDescription = "Volver"
                )
            }
            Text(
                text = "Route details",
                style = AppTypography.h2,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier.fillMaxWidth()
        ) {
            itemsIndexed(
                items = steps,
                key = { _, step -> step.hashCode() }
            ) { index, leg ->
                val isLast = index == steps.lastIndex

                RouteStepItem(
                    leg = leg,
                    isFirst = index == 0,
                    isLast = isLast,
                    showConnector = !isLast,
                    delay = index * 90,
                    enableAnimations = enableAnimations
                )

                if (!isLast) {
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
fun RouteStepItem(
    leg: Leg,
    isFirst: Boolean,
    isLast: Boolean,
    showConnector: Boolean,
    delay: Int = 0,
    enableAnimations: Boolean = true
) {
    val routeColor = parseRouteColor(leg.routeColor)
    val iconSize = 38.dp
    val lineWidth = 6.dp
    val fullLineHeight = 48.dp
    val horizontalPadding = 56.dp
    val durationMinutes = leg.duration?.div(60) ?: 0

    var isVisible by remember { mutableStateOf(!enableAnimations) }
    val currentLeg by rememberUpdatedState(leg)

    LaunchedEffect(currentLeg) {
        if (enableAnimations) {
            delay(delay.toLong())
            isVisible = true
        }
    }

    val springAnimSpecDp: AnimationSpec<Dp> =
        if (enableAnimations) spring(dampingRatio = 0.7f, stiffness = 180f) else snap()

    val tweenAnimSpecDp: AnimationSpec<Dp> =
        if (enableAnimations) tween(durationMillis = 500, easing = FastOutSlowInEasing) else snap()

    val tweenAnimSpecFloat: AnimationSpec<Float> =
        if (enableAnimations) tween(durationMillis = 500, easing = FastOutSlowInEasing) else snap()

    val floatSpringAnimSpec: AnimationSpec<Float> =
        if (enableAnimations) spring(dampingRatio = 0.6f) else snap()

    val lineHeight by animateDpAsState(
        targetValue = if (isVisible && showConnector) fullLineHeight else 0.dp,
        animationSpec = springAnimSpecDp,
        label = "lineHeight"
    )


    val iconFloat by animateDpAsState(
        targetValue = if (isVisible) 0.dp else 12.dp,
        animationSpec = spring(dampingRatio = 0.6f),
        label = "iconFloat"
    )

    val iconScale by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0.8f,
        animationSpec = floatSpringAnimSpec,
        label = "iconScale"
    )

    val contentSlide by animateDpAsState(
        targetValue = if (isVisible) 0.dp else 16.dp,
        animationSpec = tweenAnimSpecDp,
        label = "contentSlide"
    )

    val contentAlpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tweenAnimSpecFloat,
        label = "contentAlpha"
    )

    val timeText = remember(leg.startTime, leg.from?.name) {
        leg.startTime?.let {
            "${SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(it))} ${leg.from?.name ?: "Origen"}"
        } ?: "--:-- Origen"
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        verticalAlignment = Alignment.Top
    ) {
        Column(
            modifier = Modifier.width(horizontalPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(iconSize)
                    .offset(y = iconFloat)
                    .scale(iconScale)
                    .background(routeColor, shape = CircleShape)
                    .padding(4.dp)
                    .zIndex(1f),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(
                        id = when (leg.mode) {
                            "WALK" -> R.drawable.ic_walk
                            "SUBWAY" -> R.drawable.ic_tram
                            "TRAM" -> R.drawable.ic_tram
                            "BUS" -> R.drawable.ic_busline
                            else -> R.drawable.ic_busline
                        }
                    ),
                    contentDescription = leg.mode,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }

            if (showConnector) {
                Spacer(modifier = Modifier.height(1.dp))
                Box(
                    modifier = Modifier
                        .width(lineWidth)
                        .height(lineHeight)
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    routeColor,
                                    routeColor.copy(alpha = 0.8f)
                                )
                            ),
                            shape = RoundedCornerShape(6.dp)
                        )
                )
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 16.dp, top = 8.dp)
                .offset(x = contentSlide)
                .alpha(contentAlpha)
        ) {
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(
                    animationSpec = tween(400, 100, easing = LinearOutSlowInEasing)
                ) + slideInVertically(
                    animationSpec = tween(500, 100)
                ) { height -> height / 4 },
                exit = fadeOut()
            ) {
                Column {
                    Text(
                        text = timeText,
                        style = AppTypography.body1.copy(fontWeight = FontWeight.SemiBold),
                        modifier = Modifier.padding(bottom = 4.dp)
                    )

                    when (leg.mode) {
                        "WALK" -> {
                            Text(
                                text = "Walk for $durationMinutes min",
                                style = AppTypography.body2.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                                )
                            )
                        }
                        else -> {
                            Text(
                                text = "Take ${leg.route ?: "transport"}",
                                style = AppTypography.body2.copy(
                                    color = routeColor,
                                    fontWeight = FontWeight.Bold
                                ),
                                modifier = Modifier.padding(bottom = 2.dp)
                            )
                            Text(
                                text = "To ${leg.to?.name ?: "destination"}",
                                style = AppTypography.body2.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyRouteContent() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_exit),
            contentDescription = "No service outside GDL metro area.",
            modifier = Modifier.size(48.dp),
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Invalid route",
            fontSize = 16.sp,
            color = Color.Gray,
            fontWeight = FontWeight.Medium
        )
    }
}

fun parseAddress(fullText: String): Pair<String, String> {
    val parts = fullText.split(",")
    val main = parts.firstOrNull() ?: fullText
    val rest = if (parts.size > 1) parts.drop(1).joinToString(",").trim() else ""
    return main to rest
}

@Composable
fun SuggestionItem(
    fullText: String,
    onClick: () -> Unit,
    isOrigin: Boolean
) {
    val (mainAddress, secondaryAddress) = parseAddress(fullText)

    val iconPainter = painterResource(
        id = if (isOrigin) R.drawable.ic_app else R.drawable.ic_busline
    )

    val iconTint = if (isOrigin) Color(0xFF00933B) else Color(0xFF00933B)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp, horizontal = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = mainAddress,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color(0xFF222222)
                )
                if (secondaryAddress.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = secondaryAddress,
                        fontWeight = FontWeight.Normal,
                        fontSize = 14.sp,
                        color = Color(0xFF666666)
                    )
                }
            }
            Icon(
                painter = iconPainter,
                contentDescription = if (isOrigin) "Icono Origen" else "Icono Destino",
                tint = iconTint,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Divider(color = Color(0xFFE0E0E0), thickness = 1.dp)
    }
}

fun parseRouteColor(routeColor: String?): Color {
    return try {
        Color(android.graphics.Color.parseColor("#${routeColor ?: "000000"}"))
    } catch (e: Exception) {
        Color.Gray
    }
}

@Composable
private fun RouteSummary(
    itinerary: Itinerary,
    index: Int,
    expanded: Boolean,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val baseRouteColor = itinerary.legs
        ?.mapNotNull { it.routeColor }
        ?.firstOrNull()
        ?.let { parseRouteColor(it) }
        ?: Color(0xFFCCCCCC)

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(5.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(scrollState),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_route_favorite),
                contentDescription = "Ruta",
                tint = baseRouteColor,
                modifier = Modifier.size(35.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (index == 0) "Faster route" else "Trip ${index + 1}",
                    style = AppTypography.h2.copy(
                        fontFamily = PoppinsFontFamily,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    text = "Estimated time: ${(itinerary.duration?.div(60) ?: 0)} min",
                    style = AppTypography.body2.copy(
                        fontFamily = PoppinsFontFamily,
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Transfers needed: ${itinerary.legs?.size?.minus(1) ?: 0}",
            style = AppTypography.body2.copy(
                fontFamily = PoppinsFontFamily,
                fontSize = 13.sp,
                color = Color.Gray
            )
        )

        val routeNames = itinerary.legs?.mapNotNull { it.route }?.distinct() ?: emptyList()
        if (routeNames.isNotEmpty()) {
            Text(
                text = "Transit lines: ${routeNames.joinToString(", ")}",
                style = AppTypography.body2.copy(
                    fontFamily = PoppinsFontFamily,
                    fontSize = 13.sp,
                    color = Color.Gray
                )
            )
        }

            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
            ) {
                Row(
                    modifier = Modifier
                        .padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    itinerary.legs?.forEach { leg ->
                        val color = parseRouteColor(leg.routeColor)
                        if (leg.mode == "WALK") {
                            Box(
                                modifier = Modifier
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.ic_walk),
                                    contentDescription = "Caminata",
                                    modifier = Modifier.size(16.dp),
                                    tint = Color.Unspecified
                                )
                            }
                        } else {
                            Box(
                                modifier = Modifier
                                    .background(color, RoundedCornerShape(12.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = leg.route ?: "N/A",
                                    style = AppTypography.body2.copy(
                                        fontFamily = PoppinsFontFamily,
                                        color = Color.White
                                    )
                                )
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Divider(color = Color.LightGray, thickness = 1.dp)
        }
    }

@Composable
private fun RouteDetailsContent(
    routePlan: RoutePlanResponse,
    selectedItineraryIndex: Int,
    onItinerarySelected: (Int) -> Unit,
    onShowSteps: (Itinerary) -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 5.dp)
            .verticalScroll(scrollState)
    ) {
        Text(
            text = "Suggested Routes:",
            fontSize = 20.sp,
            color = Color.Black,
            style = TextStyle(
                fontFamily = AppTypography.h1.fontFamily,
                fontWeight = AppTypography.h1.fontWeight,
            ),
            modifier = Modifier
                .padding(horizontal = 5.dp, vertical = 5.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        routePlan.plan?.itineraries?.let { itineraries ->
            val sortedRoutes = sortRoutesByDuration(itineraries)

            if (sortedRoutes.isEmpty()) {
                EmptyRouteContent()
            } else {
                sortedRoutes.forEach { (originalIndex, itinerary) ->
                    val sortedIndex = sortedRoutes.indexOf(originalIndex to itinerary)
                    RouteSummary(
                        itinerary = itinerary,
                        index = sortedIndex,
                        expanded = false,
                        isSelected = originalIndex == selectedItineraryIndex,
                        onClick = {
                            onItinerarySelected(originalIndex)
                            onShowSteps(itinerary)
                        },
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(60.dp))

    }
}

@Composable
private fun SharingLocationFAB(
    isSharing: Boolean,
    onToggleSharing: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val locationViewModel: LocationViewModel = hiltViewModel()
    val currentUser = FirebaseAuth.getInstance().currentUser

    FloatingActionButton(
        onClick = {
            scope.launch {
                if (!isSharing) {
                    currentUser?.let { user ->
                        try {
                            // 1. Inicializar
                            locationViewModel.initializeSharing(user.uid, user.displayName ?: "Conductor")

                            // 2. Obtener flujo de ubicación
                            val locationFlow = getLocationFlow(context)

                            // 3. Comenzar a compartir
                            locationViewModel.startSharingLocation(
                                locationFlow = locationFlow,
                                onError = { error ->
                                    // Si hay error, revertir el estado
                                    scope.launch(Dispatchers.Main) {
                                        Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                                        onToggleSharing(false)
                                    }
                                }
                            )

                            // Solo actualizar estado si todo salió bien
                            onToggleSharing(true)
                        } catch (e: Exception) {
                            scope.launch(Dispatchers.Main) {
                                Toast.makeText(
                                    context,
                                    "Error: ${e.localizedMessage}",
                                    Toast.LENGTH_SHORT
                                ).show()
                                onToggleSharing(false)
                            }
                        }
                    } ?: run {
                        scope.launch(Dispatchers.Main) {
                            Toast.makeText(context, "Usuario no autenticado", Toast.LENGTH_SHORT).show()
                            onToggleSharing(false)
                        }
                    }
                } else {
                    // Detener el compartir
                    locationViewModel.stopSharingLocation()
                    onToggleSharing(false)
                }
            }
        },
        containerColor = if (isSharing) Color.Red else Color(0xFF00933B),
        modifier = modifier.size(56.dp)
    ) {
        Icon(
            painter = painterResource(
                id = if (isSharing) R.drawable.ic_app
                else R.drawable.ic_app
            ),
            contentDescription = if (isSharing) "Stop sharing" else "Share location",
            tint = Color.White,
            modifier = Modifier.size(24.dp)
        )
    }
}