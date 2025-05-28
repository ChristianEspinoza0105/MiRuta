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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberBottomSheetScaffoldState
import com.example.miruta.data.models.Itinerary
import androidx.compose.material3.SheetValue

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ExploreScreen(navController: NavHostController? = null) {
    val context = LocalContext.current
    val defaultLocation = LatLng(20.659699, -103.349609)
    var userLocation by remember { mutableStateOf<LatLng?>(null) }
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

    var routePoints by remember { mutableStateOf<List<LatLng>>(emptyList()) }

    val bottomSheetScaffoldState = rememberBottomSheetScaffoldState()

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(defaultLocation, 10f)
    }

    val locationPermissionsState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )

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

    fun getCurrentTimeAmPm(): String {
        val sdf = SimpleDateFormat("hh:mma", Locale.US)
        return sdf.format(Date()).lowercase(Locale.US)
    }

    fun fetchRoutePlan(
        from: LatLng,
        to: LatLng,
        date: String = "2024-06-10",
        time: String? = null
    ) {
        isLoadingRoute = true
        errorMessage = null

        coroutineScope.launch {
            try {
                val currentTime = time ?: getCurrentTimeAmPm()

                val response = RetrofitClient.otpApiService.getRoutePlan(
                    fromPlace = "${from.latitude},${from.longitude}",
                    toPlace = "${to.latitude},${to.longitude}",
                    mode = "TRANSIT",
                    date = date,
                    time = currentTime,
                    ignoreRealtimeUpdates = true
                )

                routePlan = response
                sheetState.expand()
                routePlan = response
                val newPoints = mutableListOf<LatLng>()
                response.plan?.itineraries?.firstOrNull()?.legs?.forEach { leg ->
                    leg.legGeometry?.points?.let { encoded ->
                        newPoints += decodePolyline(encoded)
                    }
                }
                routePoints = newPoints
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

    BottomSheetScaffold(
        scaffoldState = bottomSheetScaffoldState,
        sheetPeekHeight = 64.dp,
        sheetContent = {
            if (routePlan != null) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .imePadding()
                ) {
                    RouteDetailsContent(routePlan!!)
                }
            } else {
                Spacer(modifier = Modifier.height(1.dp))
            }
        }
    ) {
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
            if (routePoints.isNotEmpty()) {
                routePlan?.plan?.itineraries?.firstOrNull()?.legs?.forEach { leg ->
                    leg.legGeometry?.points?.let { encoded ->
                        val points = decodePolyline(encoded)
                        Polyline(
                            points = points,
                            color = parseRouteColor(leg.routeColor),
                            width = 8f
                        )
                    }
                }
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
                .align(Alignment.TopCenter)
                .padding(16.dp)
        ) {
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
                placeholder = { Text("Buscar origen", color = Color.Gray, fontSize = 16.sp) },
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
                                        LatLngBounds.builder().include(defaultLocation).build()
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
                            "Buscar destino",
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

            Spacer(modifier = Modifier.height(8.dp))

            if (suggestions.isNotEmpty() || destinoSuggestions.isNotEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(horizontal = 8.dp)
                ) {
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
                            }
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
                            }
                        )
                    }
                }
            }
        }

        if (isLoadingRoute) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }

        errorMessage?.let {
            Text(
                text = it,
                color = Color.Red,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        }
    }
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
        var b: Int
        var shift = 0
        var result = 0
        do {
            b = encoded[index++].code - 63
            result = result or (b and 0x1f shl shift)
            shift += 5
        } while (b >= 0x20)
        val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
        lat += dlat

        shift = 0
        result = 0
        do {
            b = encoded[index++].code - 63
            result = result or (b and 0x1f shl shift)
            shift += 5
        } while (b >= 0x20)
        val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
        lng += dlng

        val point = LatLng(lat / 1E5, lng / 1E5)
        poly.add(point)
    }
    return poly
}

@Composable
private fun RouteDetailsContent(routePlan: RoutePlanResponse) {
    val scrollState = rememberScrollState()
    var expandedRoute by remember { mutableStateOf(-1) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .verticalScroll(scrollState)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Rutas Disponibles",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            IconButton(
                onClick = {  },
                modifier = Modifier.size(24.dp)
            ) {
                Icon(Icons.Default.Close, contentDescription = "Cerrar")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        routePlan.plan?.itineraries?.let { itineraries ->
            val filteredRoutes = filterTopRoutes(itineraries)
            if (filteredRoutes.isEmpty()) {
                EmptyRouteContent()
            } else {
                filteredRoutes.forEachIndexed { index, itinerary ->
                    RouteSummaryCard(
                        itinerary = itinerary,
                        index = index,
                        expanded = expandedRoute == index,
                        onClick = {
                            expandedRoute = if (expandedRoute == index) -1 else index
                        },
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }
                if (expandedRoute != -1) {
                    val itinerary = filteredRoutes[expandedRoute]
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFFFFFFF), shape = RoundedCornerShape(8.dp))
                            .padding(16.dp)
                    ) {
                        itinerary.legs?.forEachIndexed { legIndex, leg ->
                            RouteLegItem(
                                leg = leg,
                                isFirst = legIndex == 0,
                                isLast = legIndex == (itinerary.legs.size - 1)
                            )
                            if (legIndex < itinerary.legs.size - 1) {
                                ConnectionLine()
                            }
                        }
                    }
                }
            }
        }
    }
}

fun filterTopRoutes(itineraries: List<Itinerary>): List<Itinerary> {
    val seen = mutableSetOf<String>()

    val uniqueItineraries = itineraries.filter { itinerary ->
        val key = (itinerary.duration ?: 0).toString() + "_" + (itinerary.legs?.map { it.route ?: "" }?.joinToString("-") ?: "")
        if (seen.contains(key)) {
            false
        } else {
            seen.add(key)
            true
        }
    }

    return uniqueItineraries.sortedBy { it.duration ?: Int.MAX_VALUE }.take(5)
}

@Composable
private fun RouteSummaryCard(
    itinerary: Itinerary,
    index: Int,
    expanded: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val baseRouteColor = itinerary.legs
        ?.mapNotNull { it.routeColor }
        ?.firstOrNull()
        ?.let { parseRouteColor(it) }
        ?: Color(0xFFCCCCCC)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = baseRouteColor.copy(alpha = 0.1f)
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_route_favorite),
                    contentDescription = "Ruta",
                    tint = baseRouteColor,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Opción ${index + 1}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Tiempo total: ${(itinerary.duration?.div(60) ?: 0)} min",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
                Icon(
                    imageVector = if (expanded) Icons.Default.Close else Icons.Default.Check,
                    contentDescription = if (expanded) "Mostrar menos" else "Mostrar más",
                    tint = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Trasbordos: ${itinerary.legs?.size?.minus(1) ?: 0}",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )

            val routeNames = itinerary.legs?.mapNotNull { it.route }?.distinct() ?: emptyList()
            if (routeNames.isNotEmpty()) {
                Text(
                    text = "Rutas: ${routeNames.joinToString(", ")}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    itinerary.legs?.forEach { leg ->
                        val color = parseRouteColor(leg.routeColor)
                        if (leg.mode == "WALK") {
                            Box(
                                modifier = Modifier
                                    .background(Color.LightGray, RoundedCornerShape(12.dp))
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
                                    style = MaterialTheme.typography.bodySmall.copy(color = Color.White)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RouteLegItem(leg: Leg, isFirst: Boolean, isLast: Boolean = false) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 8.dp)
            .clickable { expanded = !expanded }
            .background(color = if (expanded) Color(0xFFE0F7FA) else Color.Transparent)
            .padding(8.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .background(
                        color = parseRouteColor(leg.routeColor),
                        shape = CircleShape
                    )
            )
            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (isFirst) "Desde: ${leg.from?.name ?: "Desconocido"}"
                    else if (isLast) "Hasta: ${leg.to?.name ?: "Desconocido"}"
                    else "Parada: ${leg.from?.name ?: "Desconocido"}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = if (isFirst || isLast) FontWeight.SemiBold else FontWeight.Normal
                )
                leg.startTime?.let {
                    Text(
                        text = "Salida: ${SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(it))}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.padding(start = 28.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(
                    id = when (leg.mode) {
                        "BUS" -> R.drawable.ic_route_favorite
                        "WALK" -> R.drawable.ic_route_favorite
                        "SUBWAY" -> R.drawable.ic_route_favorite
                        else -> R.drawable.ic_route_favorite
                    }
                ),
                contentDescription = leg.mode,
                modifier = Modifier.size(16.dp),
                tint = parseRouteColor(leg.routeColor)
            )
            Spacer(modifier = Modifier.width(8.dp))

            val modeDisplayName = when (leg.mode) {
                "BUS" -> leg.route ?: "Bus"
                "SUBWAY" -> "Metro"
                "WALK" -> "Caminata"
                else -> leg.mode ?: "Transporte"
            }

            Text(
                text = "$modeDisplayName - ${leg.duration?.div(60) ?: "?"} min",
                style = MaterialTheme.typography.bodyMedium
            )
        }

        if (expanded) {
            val polyline = leg.legGeometry?.points
            val decodedPoints = polyline?.let { decodePolyline(it) }
            decodedPoints?.let { points ->
                Text(
                    text = "Puntos decodificados:",
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold)
                )
                points.take(5).forEach { point ->
                    Text(
                        text = "(${point.latitude}, ${point.longitude})",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.DarkGray
                    )
                }
            } ?: Text("No hay polyline disponible", color = Color.Gray)
            Spacer(modifier = Modifier.height(12.dp))
            Text("Ruta: ${leg.route ?: "No disponible"}", style = MaterialTheme.typography.bodySmall)
            Text("Puntos de geometría: ${leg.legGeometry?.points ?: "No disponible"}", style = MaterialTheme.typography.bodySmall)
            Text("Desde: ${leg.from?.name ?: "Desconocido"}", style = MaterialTheme.typography.bodySmall)
            Text("Hasta: ${leg.to?.name ?: "Desconocido"}", style = MaterialTheme.typography.bodySmall)
            Text("Duración en segundos: ${leg.duration ?: "No disponible"}", style = MaterialTheme.typography.bodySmall)
            Text("Hora inicio: ${leg.startTime?.let { SimpleDateFormat("hh:mm:ss a", Locale.getDefault()).format(Date(it)) } ?: "No disponible"}", style = MaterialTheme.typography.bodySmall)
            Text("Hora fin: ${leg.endTime?.let { SimpleDateFormat("hh:mm:ss a", Locale.getDefault()).format(Date(it)) } ?: "No disponible"}", style = MaterialTheme.typography.bodySmall)
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
            contentDescription = "Sin rutas",
            modifier = Modifier.size(48.dp),
            tint = Color(0xFFFFA000)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "No se encontraron rutas disponibles",
            fontSize = 16.sp,
            color = Color.Gray,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun ConnectionLine() {
    Box(
        modifier = Modifier
            .padding(start = 7.dp)
            .width(2.dp)
            .height(24.dp)
            .background(Color.Gray.copy(alpha = 0.5f))
    )
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
    onClick: () -> Unit
) {
    val (mainAddress, secondaryAddress) = parseAddress(fullText)

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
                imageVector = Icons.Default.Place,
                contentDescription = "Tiempo estimado",
                tint = Color(0xFF00933B),
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
        Color.Black
    }
}