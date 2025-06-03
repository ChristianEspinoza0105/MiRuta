package com.example.miruta.ui.components

import androidx.compose.runtime.Composable
import com.example.miruta.data.models.DriverLocation
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberMarkerState

@Composable
fun ActiveDriversMarkers(
    drivers: List<DriverLocation>,
    onDriverSelected: (DriverLocation) -> Unit
) {
    drivers.forEach { driver ->
        Marker(
            state = rememberMarkerState(position = LatLng(driver.latitude, driver.longitude)),
            title = driver.driverName,
            onClick = {
                onDriverSelected(driver)
                true
            },
            icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)
        )
    }
}