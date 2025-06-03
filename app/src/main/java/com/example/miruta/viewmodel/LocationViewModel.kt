package com.example.miruta.viewmodel

import android.Manifest
import android.location.Location
import android.os.Looper
import androidx.annotation.RequiresPermission
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.miruta.data.models.DriverLocation
import com.example.miruta.data.repository.LiveLocationRepository
import com.example.miruta.data.repository.LiveLocationSharingDrivers
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class LocationViewModel @Inject constructor(
    private val locationRepository: LiveLocationRepository,
    private val liveLocationSharing: LiveLocationSharingDrivers
) : ViewModel() {
    private val _userLocation = MutableStateFlow<LatLng?>(null)
    val userLocation = _userLocation.asStateFlow()

    suspend fun initializeSharing(driverId: String, driverName: String) {
        liveLocationSharing.initialize(driverId, driverName)
    }

    val activeDrivers: StateFlow<List<DriverLocation>> =
        locationRepository.getActiveDrivers()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    suspend fun startSharingLocation(
        locationFlow: Flow<Location>,
        onError: (String) -> Unit
    ) {
        liveLocationSharing.startSharing(
            locationFlow = locationFlow,
            onError = onError
        )
    }

    fun stopSharingLocation() {
        liveLocationSharing.stopSharing()
    }

    var driverMarkerPosition by mutableStateOf<LatLng?>(null)

    fun clearDriverMarker() {
        driverMarkerPosition = null
    }
}
