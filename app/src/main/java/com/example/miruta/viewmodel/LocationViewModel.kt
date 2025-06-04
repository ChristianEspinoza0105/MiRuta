package com.example.miruta.viewmodel

import android.location.Location
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.miruta.data.models.DriverLocation
import com.example.miruta.data.repository.LiveLocationRepository
import com.example.miruta.data.repository.LiveLocationSharingDrivers
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
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
            .onEach { drivers ->
                drivers.forEach { driver ->
                    println("DRIVER_UPDATE - ID: ${driver.driverId}, " +
                            "Lat: ${driver.latitude}, Lon: ${driver.longitude}, " +
                            "Bearing: ${driver.bearing}, Time: ${driver.lastUpdate}")
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    suspend fun startSharingLocation(
        locationFlow: Flow<Location>,
        onError: (String) -> Unit
    ) {
        locationFlow
            .onEach { location ->
                println("MY_LOCATION_UPDATE - Lat: ${location.latitude}, " +
                        "Lon: ${location.longitude}, " +
                        "Bearing: ${location.bearing ?: 0f}")
            }
            .let { flow ->
                liveLocationSharing.startSharing(
                    locationFlow = flow,
                    onError = onError
                )
            }
    }

    fun stopSharingLocation() {
        viewModelScope.launch {
            liveLocationSharing.stopSharing()
            driverMarkerPosition = null
        }
    }
    var driverMarkerPosition by mutableStateOf<LatLng?>(null)

    fun clearDriverLocation(driverId: String) {
        viewModelScope.launch {
            try {
                liveLocationSharing.stopSharingForDriver(driverId)
            } catch (e: Exception) {
                // ERROR
            }
        }
    }
}
