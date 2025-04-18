
package com.example.miruta.ui.screens

import android.Manifest
import android.content.Context
import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.ui.text.TextStyle
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
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.rememberCameraPositionState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import com.example.miruta.R
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import androidx.compose.ui.text.input.ImeAction
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import com.google.android.gms.maps.model.BitmapDescriptorFactory
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
    val placesClient = remember { PlacesClientProvider.getClient(context) }
    var suggestions by remember { mutableStateOf(listOf<AutocompletePrediction>()) }
    val keyboardController = LocalSoftwareKeyboardController.current
    var selectedLocation by remember { mutableStateOf<LatLng?>(null) }
    val coroutineScope = rememberCoroutineScope()

    val mapProperties = remember {
        MapProperties(
            mapType = MapType.NORMAL,
            isIndoorEnabled = true,
            isTrafficEnabled = false,
            isMyLocationEnabled = true
        )
    }

    val mapUiSettings = remember {
        MapUiSettings(
            zoomControlsEnabled = false,
            myLocationButtonEnabled = false,
            mapToolbarEnabled = false,
            compassEnabled = false,
            tiltGesturesEnabled = true,
            rotationGesturesEnabled = true
        )
    }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(defaultLocation, 10f)
    }

    val locationPermissionsState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )

    val focusManager = LocalFocusManager.current
    var isFieldFocused by remember { mutableStateOf(false) }

    LaunchedEffect(locationPermissionsState.allPermissionsGranted) {
        if (locationPermissionsState.allPermissionsGranted) {
            try {
                val location = getDeviceLocation(context).await()
                val target = LatLng(location.latitude, location.longitude)

                cameraPositionState.move(
                    CameraUpdateFactory.newCameraPosition(
                        CameraPosition.Builder()
                            .target(target)
                            .zoom(10f)
                            .tilt(0f)
                            .build()
                    )
                )

                cameraPositionState.animate(
                    CameraUpdateFactory.newCameraPosition(
                        CameraPosition.Builder()
                            .target(target)
                            .zoom(17f)
                            .tilt(45f)
                            .build()
                    ),
                    durationMs = 1500
                )

                userLocation = target
                isLoading = false
            } catch (e: Exception) {
                cameraPositionState.move(
                    CameraUpdateFactory.newLatLngZoom(defaultLocation, 10f)
                )
                cameraPositionState.animate(
                    CameraUpdateFactory.newCameraPosition(
                        CameraPosition.Builder()
                            .target(defaultLocation)
                            .zoom(12f)
                            .tilt(0f)
                            .build()
                    ),
                    durationMs = 1500
                )
                userLocation = defaultLocation
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
            properties = mapProperties,
            uiSettings = mapUiSettings
        ) {
            selectedLocation?.let { location ->
                Marker(
                    state = MarkerState(position = location),
                    icon = BitmapDescriptorFactory.fromBitmap(
                        BitmapFactory.decodeResource(context.resources, R.drawable.ic_marker)
                    ),
                    title = "Ubicación Seleccionada",
                    snippet = "Marcador desde autocompletado"
                )
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(16.dp)
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = origen,
                onValueChange = { query ->
                    origen = query
                    if (query.isNotBlank()) {
                        val request = FindAutocompletePredictionsRequest.builder()
                            .setQuery(query)
                            .build()
                        placesClient.findAutocompletePredictions(request)
                            .addOnSuccessListener { response ->
                                suggestions = response.autocompletePredictions
                            }
                            .addOnFailureListener {
                                Log.e("PLACES_API", "Error al obtener sugerencias")
                                suggestions = emptyList()
                            }
                    } else {
                        suggestions = emptyList()
                    }
                },
                placeholder = {
                    Text(
                        text = "Search here",
                        style = TextStyle(color = Color.Gray, fontSize = 16.sp)
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .onFocusChanged { isFieldFocused = it.isFocused },
                shape = RoundedCornerShape(35.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    backgroundColor = Color.White,
                    focusedBorderColor = Color(0xFF00933B),
                    unfocusedBorderColor = Color(0xFFE7E7E7)
                ),
                leadingIcon = {
                    Icon(
                        painter = painterResource(
                            id = if (isFieldFocused) R.drawable.ic_back else R.drawable.ic_app
                        ),
                        contentDescription = if (isFieldFocused) "Back" else "Location",
                        tint = Color.Unspecified,
                        modifier = Modifier
                            .size(if (isFieldFocused) 24.dp else 32.dp)
                            .clickable {
                                if (isFieldFocused) {
                                    origen = ""
                                    suggestions = emptyList()
                                    focusManager.clearFocus()
                                }
                            }
                    )
                },
                trailingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.search),
                        contentDescription = "Search",
                        tint = Color.Unspecified,
                        modifier = Modifier
                            .size(32.dp)
                            .clickable {
                                if (suggestions.isNotEmpty()) {
                                    origen = suggestions[0].getFullText(null).toString()
                                    suggestions = emptyList()
                                    focusManager.clearFocus()
                                    keyboardController?.hide()
                                }
                            }
                    )
                },
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Search,
                    keyboardType = KeyboardType.Text
                ),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        if (suggestions.isNotEmpty()) {
                            origen = suggestions[0].getFullText(null).toString()
                            suggestions = emptyList()
                            focusManager.clearFocus()
                            keyboardController?.hide()
                        }
                    }
                )
            )

            if (suggestions.isNotEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(top = 4.dp)
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

                                    coroutineScope.launch {
                                        val location = fetchPlaceLatLng(context, prediction.placeId)
                                        location?.let {
                                            selectedLocation = it
                                            cameraPositionState.animate(
                                                CameraUpdateFactory.newCameraPosition(
                                                    CameraPosition.Builder()
                                                        .target(it)
                                                        .zoom(18f)
                                                        .tilt(45f)
                                                        .build()
                                                ),
                                                durationMs = 1000
                                            )
                                        } ?: run {
                                            Log.e("MAP", "Error obteniendo coordenadas")
                                        }
                                    }
                                }
                                .padding(16.dp)
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
            placesClient = Places.createClient(context.applicationContext)
        }
        return placesClient!!
    }
}

suspend fun fetchPlaceLatLng(context: Context, placeId: String): LatLng? {
    return try {
        val placesClient = PlacesClientProvider.getClient(context)
        val request = FetchPlaceRequest.builder(placeId, listOf(Place.Field.LAT_LNG)).build()
        val response = placesClient.fetchPlace(request).await()
        response.place.latLng?.also {
            Log.d("MAP", "Coordenadas obtenidas: $it")
        }
    } catch (e: Exception) {
        Log.e("MAP", "Error fetchPlace: ${e.message}")
        null
    }
}

private fun getDeviceLocation(context: Context) =
    LocationServices.getFusedLocationProviderClient(context)
        .lastLocation