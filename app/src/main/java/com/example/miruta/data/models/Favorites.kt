package com.example.miruta.data.models

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

data class RoutineStop(
    val locationName: String = "",
    val time: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
)
