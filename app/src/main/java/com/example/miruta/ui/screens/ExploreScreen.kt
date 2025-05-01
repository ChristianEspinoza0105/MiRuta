
package com.example.miruta.ui.screens

import android.Manifest
import android.content.Context
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material3.Text
import kotlinx.coroutines.tasks.await
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.google.maps.android.compose.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.*
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.rememberCameraPositionState
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import com.example.miruta.R
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.libraries.places.api.model.LocationBias
import com.google.android.libraries.places.api.model.RectangularBounds
import com.google.android.libraries.places.api.net.PlacesClient
import kotlinx.coroutines.launch

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ExploreScreen() {
    val context = LocalContext.current
    val defaultLocation = LatLng(20.659699, -103.349609)
    var userLocation by remember { mutableStateOf<LatLng?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    var origen by remember { mutableStateOf("") }
    var destino by remember { mutableStateOf("") }

    var suggestions by remember { mutableStateOf(listOf<AutocompletePrediction>()) }
    var destinoSuggestions by remember { mutableStateOf(listOf<AutocompletePrediction>()) }

    val placesClient = remember { PlacesClientProvider.getClient(context) }
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    var isOrigenFocused by remember { mutableStateOf(false) }
    var isDestinoFocused by remember { mutableStateOf(false) }

    var origenLatLng by remember { mutableStateOf<LatLng?>(null) }
    var destinoLatLng by remember { mutableStateOf<LatLng?>(null) }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(defaultLocation, 10f)
    }

    val locationPermissionsState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )

    val coroutineScope = rememberCoroutineScope()

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

    LaunchedEffect(locationPermissionsState.allPermissionsGranted) {
        if (locationPermissionsState.allPermissionsGranted) {
            try {
                val location = getDeviceLocation(context).await()
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

    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(isMyLocationEnabled = true),
            uiSettings = MapUiSettings(myLocationButtonEnabled = false)
        ) {
            origenLatLng?.let { latLng ->
                Marker(
                    state = MarkerState(position = latLng),
                    title = "Origen",
                    icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_marker)
                )
            }
            destinoLatLng?.let { latLng ->
                Marker(
                    state = MarkerState(position = latLng),
                    title = "Destino",
                    icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_marker)
                )
            }
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
                        updateCameraPosition(origenLatLng, destinoLatLng)
                    }
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
                placeholder = { Text("Search origin", color = Color.Gray, fontSize = 16.sp) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .onFocusChanged { isOrigenFocused = it.isFocused },
                shape = RoundedCornerShape(35.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    backgroundColor = Color.White,
                    focusedBorderColor = Color(0xFF00933B),
                    unfocusedBorderColor = Color(0xFFE7E7E7)
                ),
                leadingIcon = {
                    Icon(
                        painter = painterResource(
                            id = if (isOrigenFocused) R.drawable.ic_back else R.drawable.ic_app
                        ),
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
                        painter = painterResource(id = R.drawable.search),
                        contentDescription = "Buscar",
                        modifier = Modifier
                            .padding(8.dp)
                            .size(24.dp)
                    )
                },
                singleLine = true
            )

            if (origen.isNotBlank() || isOrigenFocused) {
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = destino,
                    onValueChange = { query ->
                        destino = query
                        if (query.isBlank()) {
                            destinoLatLng = null
                            updateCameraPosition(origenLatLng, destinoLatLng)
                        }
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
                                    destinoSuggestions = response.autocompletePredictions
                                }
                                .addOnFailureListener {
                                    destinoSuggestions = emptyList()
                                }
                        } else {
                            destinoSuggestions = emptyList()
                        }
                    },
                    placeholder = { Text("Search destination", color = Color.Gray, fontSize = 16.sp) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .onFocusChanged { isDestinoFocused = it.isFocused },
                    shape = RoundedCornerShape(35.dp),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        backgroundColor = Color.White,
                        focusedBorderColor = Color(0xFF00933B),
                        unfocusedBorderColor = Color(0xFFE7E7E7)
                    ),
                    leadingIcon = {
                        Icon(
                            painter = painterResource(
                                id = if (isDestinoFocused) R.drawable.ic_back else R.drawable.ic_app
                            ),
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
                            painter = painterResource(id = R.drawable.search),
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
                        .padding(8.dp)
                ) {
                    suggestions.forEach { prediction ->
                        Text(
                            text = prediction.getFullText(null).toString(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    origen = prediction.getFullText(null).toString()
                                    suggestions = emptyList()
                                    focusManager.clearFocus()
                                    keyboardController?.hide()

                                    val placeId = prediction.placeId
                                    coroutineScope.launch {
                                        val latLng = fetchPlaceLatLng(context, placeId)
                                        if (latLng != null) {
                                            origenLatLng = latLng
                                            updateCameraPosition(origenLatLng, destinoLatLng)
                                        }
                                    }
                                }
                                .padding(8.dp)
                        )
                    }
                    destinoSuggestions.forEach { prediction ->
                        Text(
                            text = prediction.getFullText(null).toString(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    destino = prediction.getFullText(null).toString()
                                    destinoSuggestions = emptyList()
                                    focusManager.clearFocus()
                                    keyboardController?.hide()

                                    val placeId = prediction.placeId
                                    coroutineScope.launch {
                                        val latLng = fetchPlaceLatLng(context, placeId)
                                        if (latLng != null) {
                                            destinoLatLng = latLng
                                            updateCameraPosition(origenLatLng, destinoLatLng)
                                        }
                                    }
                                }
                                .padding(8.dp)
                        )
                    }
                }
            }
        }
    }
}

object PlacesClientProvider {
    private var placesClient: PlacesClient? = null

    fun getClient(context: Context): PlacesClient {
        if (placesClient == null) {
            placesClient = Places.createClient(context)
        }
        return placesClient!!
    }
}

suspend fun fetchPlaceLatLng(context: Context, placeId: String): LatLng? {
    return try {
        val placesClient = PlacesClientProvider.getClient(context)
        val placeFields = listOf(Place.Field.LAT_LNG)
        val request = FetchPlaceRequest.builder(placeId, placeFields).build()
        val response = placesClient.fetchPlace(request).await()
        response.place.latLng
    } catch (e: Exception) {
        Log.e("FetchPlaceLatLng", "Error fetching place LatLng", e)
        null
    }
}

private fun getDeviceLocation(context: Context) =
    LocationServices.getFusedLocationProviderClient(context)
        .lastLocation