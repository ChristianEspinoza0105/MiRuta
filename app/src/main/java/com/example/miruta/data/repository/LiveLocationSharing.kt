package com.example.miruta.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import android.content.Context
import android.location.Location
import com.google.firebase.firestore.FieldValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class LiveLocationSharing(
    private val collectionPath: String,
    private val documentId: String,
    private val subcollectionPath: String,
) {
    private val firestore = FirebaseFirestore.getInstance()
    private var liveLocationDocId: String? = null
    private var job: Job? = null

    fun startSharing(context: Context, locationFlow: Flow<Location>, onError: (String) -> Unit) {
        job = CoroutineScope(Dispatchers.IO).launch {
            locationFlow.collect { loc ->
                try {
                    if (liveLocationDocId == null) {
                        val docRef = firestore.collection("chats")
                            .document(collectionPath)
                            .collection("messages")
                            .document()

                        liveLocationDocId = docRef.id

                        docRef.set(
                            mapOf(
                                "type" to "live_location",
                                "latitude" to loc.latitude,
                                "longitude" to loc.longitude,
                                "senderId" to subcollectionPath,
                                "senderName" to documentId,
                                "timestamp" to FieldValue.serverTimestamp(),
                                "liveLocationDocId" to docRef.id
                            )
                        ).await()
                    } else {
                        firestore.collection("chats")
                            .document(collectionPath)
                            .collection("messages")
                            .document(liveLocationDocId!!)
                            .update(
                                mapOf(
                                    "latitude" to loc.latitude,
                                    "longitude" to loc.longitude,
                                    "timestamp" to FieldValue.serverTimestamp()
                                )
                            ).await()
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        onError("Error al compartir ubicación: ${e.message}")
                    }
                }
            }
        }
    }

    fun stopSharing() {
        job?.cancel()
        liveLocationDocId = null
    }
}