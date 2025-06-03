package com.example.miruta.data.repository

import com.example.miruta.data.models.DriverLocation
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class LiveLocationRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {

    fun getActiveDrivers(): Flow<List<DriverLocation>> = callbackFlow {
        val listenerRegistration = firestore.collection("drivers")
            .addSnapshotListener { snapshots, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val drivers = snapshots?.documents?.mapNotNull { doc ->
                    val latitude = doc.getDouble("latitude") ?: return@mapNotNull null
                    val longitude = doc.getDouble("longitude") ?: return@mapNotNull null
                    val driverId = doc.getString("driverId") ?: return@mapNotNull null
                    val driverName = doc.getString("driverName") ?: "Conductor"
                    val lastUpdate = doc.getLong("lastUpdate") ?: 0L

                    DriverLocation(driverId, driverName, latitude, longitude, lastUpdate)
                } ?: emptyList()

                trySend(drivers)
            }

        awaitClose {
            listenerRegistration.remove()
        }
    }
}
