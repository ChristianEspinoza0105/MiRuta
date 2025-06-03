package com.example.miruta.data.repository

import android.content.Context
import android.location.Location
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class LiveLocationSharingDrivers @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {
    private var sharingJob: Job? = null
    private var liveLocationRef: DocumentReference? = null
    private var isInitialized = false

    suspend fun initialize(driverId: String, driverName: String) {
        try {
            liveLocationRef = firestore.collection("drivers")
                .document(driverId)
                .collection("live_data")
                .document("location")

            // Verificar si el documento ya existe
            val snapshot = liveLocationRef?.get()?.await()

            val initialData = mapOf(
                "driverId" to driverId,
                "driverName" to driverName,
                "isActive" to false,
                "lastUpdate" to System.currentTimeMillis()
            )

            if (snapshot?.exists() == true) {
                // Actualizar solo los campos necesarios
                liveLocationRef?.update(initialData)?.await()
            } else {
                // Crear nuevo documento
                liveLocationRef?.set(initialData)?.await()
            }

            isInitialized = true
        } catch (e: Exception) {
            isInitialized = false
            throw Exception("Error inicializando LiveLocation: ${e.message}")
        }
    }

    fun startSharing(
        locationFlow: Flow<Location>,
        onError: (String) -> Unit
    ) {
        if (!isInitialized) {
            onError("LiveLocation no inicializado. Llama a initialize() primero.")
            return
        }

        stopSharing() // Detener cualquier compartición previa

        sharingJob = CoroutineScope(Dispatchers.IO).launch {
            try {
                locationFlow.collect { location ->
                    try {
                        updateLocation(location)
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            onError("Error actualizando ubicación: ${e.localizedMessage}")
                        }
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    onError("Error en el flujo de ubicación: ${e.localizedMessage}")
                }
            }
        }
    }

    private suspend fun updateLocation(location: Location) {
        val user = auth.currentUser ?: throw Exception("Usuario no autenticado")

        val locationData = mapOf<String, Any>(
            "latitude" to location.latitude,
            "longitude" to location.longitude,
            "timestamp" to FieldValue.serverTimestamp(),
            "lastUpdate" to System.currentTimeMillis(),
            "driverId" to user.uid,
            "driverName" to (user.displayName ?: "Conductor"),
            "isActive" to true,
            "speed" to (location.speed * 3.6),
            "bearing" to location.bearing.toDouble(),
            "accuracy" to location.accuracy.toDouble()
        )

        liveLocationRef?.set(locationData, SetOptions.merge())?.await()
    }

    fun stopSharing() {
        sharingJob?.cancel()
        sharingJob = null

        CoroutineScope(Dispatchers.IO).launch {
            try {
                liveLocationRef?.update(
                    "isActive", false,
                    "lastUpdate", System.currentTimeMillis()
                )?.await()
            } catch (e: Exception) {
                // Puedes loggear el error si es necesario
            }
        }
    }
}