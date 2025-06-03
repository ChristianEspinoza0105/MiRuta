package com.example.miruta.ui.screens

import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import com.example.miruta.R
import com.example.miruta.ui.components.LoadingSpinner
import com.example.miruta.ui.theme.AppTypography
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
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
    val context = LocalContext.current
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

    val cameraPositionState = rememberCameraPositionState()

    val originalBitmap = remember {
        val drawable = ContextCompat.getDrawable(context, R.drawable.ic_bus_live)!!
        val width = drawable.intrinsicWidth.takeIf { it > 0 } ?: 100
        val height = drawable.intrinsicHeight.takeIf { it > 0 } ?: 100
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, width, height)
        drawable.draw(canvas)
        bitmap
    }

    fun scaleBitmapForZoom(bitmap: Bitmap, zoom: Float): Bitmap {
        val minSize = 80f
        val maxSize = 170f
        val clampedZoom = zoom.coerceIn(10f, 20f)
        val scaleFactor = (clampedZoom - 5f) / (20f - 5f)
        val size = (minSize + scaleFactor * (maxSize - minSize)).toInt()
        return Bitmap.createScaledBitmap(bitmap, size, size, true)
    }

    val scaledIcon = remember(cameraPositionState.position.zoom, originalBitmap) {
        val zoom = cameraPositionState.position.zoom
        BitmapDescriptorFactory.fromBitmap(scaleBitmapForZoom(originalBitmap, zoom))
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
                cameraPositionState.move(CameraUpdateFactory.newLatLngZoom(currentLocation, 15f))
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
                cameraPositionState = cameraPositionState
            ) {
                Marker(
                    state = rememberMarkerState(position = currentLocation),
                    title = "Ubicación en vivo",
                    icon = scaledIcon
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
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF00933B))
                ) {
                    Text(
                        "Dejar de compartir",
                        color = Color.White,
                        style = AppTypography.h2,
                        fontSize = 18.sp
                    )
                }
            }
        }
    }
}
