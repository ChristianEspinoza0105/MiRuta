package com.example.miruta.data.models

data class DirectionsResponse(
    val routes: List<RouteRetroFit>
)

data class RouteRetroFit(
    val overview_polyline: OverviewPolyline
)

data class OverviewPolyline(
    val points: String
)
