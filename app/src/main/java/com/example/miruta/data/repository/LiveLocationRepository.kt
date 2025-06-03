package com.example.miruta.data.repository

import com.example.miruta.data.models.DriverLocation
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class LiveLocationRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    fun getActiveDrivers(): Flow<List<DriverLocation>> {
        return firestore.collectionGroup("live_data")
            .whereGreaterThan("lastUpdate", System.currentTimeMillis() - 300000) // 5 minutos
            .snapshots()
            .map { snapshot ->
                snapshot.documents.mapNotNull { doc ->
                    try {
                        DriverLocation(
                            driverId = doc.getString("driverId") ?: "",
                            driverName = doc.getString("driverName") ?: "Conductor",
                            latitude = doc.getDouble("latitude") ?: 0.0,
                            longitude = doc.getDouble("longitude") ?: 0.0,
                            lastUpdate = doc.getLong("lastUpdate") ?: 0,
                            speed = doc.getDouble("speed"),
                            bearing = doc.getDouble("bearing")?.toFloat(),
                            accuracy = doc.getDouble("accuracy")?.toFloat()
                        )
                    } catch (e: Exception) {
                        null
                    }
                }
            }
    }
}