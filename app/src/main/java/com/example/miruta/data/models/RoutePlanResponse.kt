package com.example.miruta.data.models

data class RoutePlanResponse(
    val plan: Plan?
)

data class Plan(
    val itineraries: List<Itinerary>?
)

data class Itinerary(
    val duration: Int?,
    val legs: List<Leg>?
)

data class Leg(
    val mode: String?,
    val from: Place?,
    val to: Place?,
    val duration: Int?,
    val startTime: Long?,
    val endTime: Long?,
    val route: String?,
    val routeColor: String?,
    val legGeometry: LegGeometry?,
)

data class Place(
    val name: String?
)

data class LegGeometry(
    val points: String?
)

data class LattLng(
    val latitude: Double,
    val longitude: Double
)
