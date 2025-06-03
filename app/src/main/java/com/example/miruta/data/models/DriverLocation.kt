package com.example.miruta.data.models

data class DriverLocation(
    val driverId: String,
    val driverName: String,
    val latitude: Double,
    val longitude: Double,
    val lastUpdate: Long,
    val speed: Double? = null,
    val bearing: Float? = null,
    val accuracy: Float? = null
)