package com.example.miruta.data.models

import androidx.annotation.Keep
import com.google.android.gms.maps.model.LatLng
import java.time.LocalTime

data class FavoriteLocation(
    val id: String = "",
    val name: String = "",
    val address: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val isFavorite: Boolean = true
)

data class FavoriteRoute(
    val id: String = "",
    val routeId: String = "",
    val routeName: String = "",
    val routeDescription: String = "",
    val isFavorite: Boolean = true
)

data class Routine(
    val id: String = "",
    val name: String = "",
    val stops: List<RoutineStop> = emptyList(),
    val userId: String = "",
)

data class StopInfo(
    val name: String = "",
    val time: LocalTime? = null,
    val location: LatLng? = null
)

@Keep
data class RoutineStop(
    val locationName: String = "",
    val time: String = "00:00",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0
) {

    @Keep constructor() : this("", "00:00", 0.0, 0.0)
}

