package com.example.miruta.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.miruta.ui.components.LoadingSpinner
import com.example.miruta.ui.theme.AppTypography
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.maps.android.compose.*

@Composable
fun LiveLocationMapScreen(
    liveLocationDocId: String,
    routeId: String,
    navController: NavHostController
) {
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
    var currentLocation by remember { mutableStateOf(LatLng(0.0, 0.0)) }
    var isLoading by remember { mutableStateOf(true) }
    var isUserOwner by remember { mutableStateOf(false) }
    var documentExists by remember { mutableStateOf(true) }

    val docRef = remember {
        FirebaseFirestore.getInstance()
            .collection("chats")
            .document(routeId)
            .collection("messages")
            .document(liveLocationDocId)
    }

    LaunchedEffect(liveLocationDocId) {
        docRef.get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                val lat = snapshot.getDouble("latitude") ?: 0.0
                val lng = snapshot.getDouble("longitude") ?: 0.0
                val senderId = snapshot.getString("senderId") ?: ""

                currentLocation = LatLng(lat, lng)
                isUserOwner = senderId == currentUserId
                isLoading = false
            } else {
                documentExists = false
            }
        }

        docRef.addSnapshotListener { snapshot, _ ->
            if (snapshot == null || !snapshot.exists()) {
                documentExists = false
                return@addSnapshotListener
            }

            val lat = snapshot.getDouble("latitude") ?: return@addSnapshotListener
            val lng = snapshot.getDouble("longitude") ?: return@addSnapshotListener
            currentLocation = LatLng(lat, lng)
        }
    }

    LaunchedEffect(documentExists) {
        if (!documentExists) {
            navController.popBackStack()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (isLoading) {
            LoadingSpinner(isLoading = true, modifier = Modifier.align(Alignment.Center))
        } else {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = rememberCameraPositionState().apply {
                    move(CameraUpdateFactory.newLatLngZoom(currentLocation, 15f))
                }
            ) {
                Marker(
                    state = MarkerState(position = currentLocation),
                    title = "Ubicación en vivo"
                )
            }

            if (isUserOwner) {
                Button(
                    onClick = {
                        docRef.delete()
                        navController.popBackStack()
                    },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp),
                    shape = RoundedCornerShape(30.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00933B))
                ) {
                    Text(
                        "Dejar de compartir",
                        color = Color.White,
                        style = AppTypography.headlineMedium,
                        fontSize = 18.sp
                    )
                }
            }
        }
    }
}
