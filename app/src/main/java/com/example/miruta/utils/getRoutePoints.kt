package com.example.miruta.utils

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.PolyUtil
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

suspend fun getRoutePoints(
    origin: LatLng,
    destination: LatLng,
    apiKey: String
): List<LatLng> = withContext(Dispatchers.IO) {
    val urlStr = "https://maps.googleapis.com/maps/api/directions/json?" +
            "origin=${origin.latitude},${origin.longitude}" +
            "&destination=${destination.latitude},${destination.longitude}" +
            "&key=$apiKey"

    val url = URL(urlStr)
    val connection = url.openConnection() as HttpURLConnection
    try {
        connection.connect()
        val inputStream = connection.inputStream
        val response = inputStream.bufferedReader().use { it.readText() }

        val gson = Gson()
        val directionsResponse = gson.fromJson(response, DirectionsResponse::class.java)
        val points = directionsResponse.routes.firstOrNull()
            ?.overviewPolyline?.points

        if (points != null) {
            PolyUtil.decode(points)
        } else {
            emptyList()
        }
    } finally {
        connection.disconnect()
    }
}

data class DirectionsResponse(
    val routes: List<Route>
)

data class Route(
    val overviewPolyline: OverviewPolyline
)

data class OverviewPolyline(
    val points: String
)
