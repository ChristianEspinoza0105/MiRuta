package com.example.miruta.ui.screens

import android.Manifest
import android.content.Context
import android.location.Location
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ExploreScreen() {
    val context = LocalContext.current
    val defaultLocation = LatLng(19.4326, -99.1332)
    var userLocation by remember { mutableStateOf<LatLng?>(null) }
    var isLoading by remember { mutableStateOf(true) }

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
            zoomControlsEnabled = true,
            myLocationButtonEnabled = true,
            mapToolbarEnabled = true,
            tiltGesturesEnabled = true,
            rotationGesturesEnabled = true
        )
    }

    // Configuramos el estado inicial de la cámara con un zoom más bajo
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(defaultLocation, 10f) // Zoom inicial más lejano
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

                // Primera animación: mover a la ubicación del usuario con zoom inicial
                cameraPositionState.move(
                    CameraUpdateFactory.newCameraPosition(
                        CameraPosition.Builder()
                            .target(target)
                            .zoom(10f) // Zoom inicial
                            .tilt(0f) // Sin inclinación al inicio
                            .build()
                    )
                )

                // Segunda animación: zoom suave hacia el nivel final
                cameraPositionState.animate(
                    CameraUpdateFactory.newCameraPosition(
                        CameraPosition.Builder()
                            .target(target)
                            .zoom(17f) // Zoom final
                            .tilt(45f) // Inclinación final
                            .build()
                    ),
                    durationMs = 1500 // Duración de la animación en milisegundos
                )

                userLocation = target
                isLoading = false
            } catch (e: Exception) {
                // En caso de error, también aplicamos la animación al defaultLocation
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
        )

        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    }
}

private fun getDeviceLocation(context: Context) =
    LocationServices.getFusedLocationProviderClient(context).lastLocation