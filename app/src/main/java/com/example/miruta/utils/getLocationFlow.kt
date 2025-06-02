package com.example.miruta.utils

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import com.google.android.gms.location.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

@SuppressLint("MissingPermission")
fun getLocationFlow(context: Context): Flow<Location> = callbackFlow {
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000L)
        .setMinUpdateIntervalMillis(2000L)
        .build()

    val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            for (location in result.locations) {
                trySend(location)
            }
        }
    }

    fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, context.mainLooper)

    awaitClose {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }
}
