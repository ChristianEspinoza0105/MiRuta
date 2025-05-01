package com.example.miruta.ui.screens

import com.google.android.gms.maps.MapsInitializer
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color
import com.example.miruta.data.gtfs.parseShapeForRoute
import com.example.miruta.data.models.ShapePoint
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun MapScreen(
    routeId: String,
    routeColorHex: String
) {
    val context = LocalContext.current

    // 1) Inicializa el SDK de Google Maps
    LaunchedEffect(Unit) {
        MapsInitializer.initialize(context, MapsInitializer.Renderer.LATEST) { renderer ->
        }
    }

    // 2) Carga los puntos de la ruta
    var rawPoints by remember { mutableStateOf<List<ShapePoint>?>(null) }
    LaunchedEffect(routeId) {
        rawPoints = withContext(Dispatchers.IO) {
            parseShapeForRoute(context, "rutas_gtfs.zip", routeId)
        }
    }

    // 3) Loader y mensaje vacío
    if (rawPoints == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }
    val shapePoints = rawPoints!!
    if (shapePoints.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No se encontraron datos de ruta")
        }
        return
    }

    // 4) Agrupar por shapeId y ordenar
    val segments = remember(shapePoints) {
        shapePoints.groupBy { it.shapeId }
            .mapValues { it.value.sortedBy { p -> p.sequence } }
    }

    // 5) Calcular bounds
    val bounds = remember(shapePoints) {
        LatLngBounds.builder().apply {
            shapePoints.forEach { p ->
                include(LatLng(p.lat, p.lng))
            }
        }.build()
    }

    // 6) Cámara
    val cameraState = rememberCameraPositionState()
    LaunchedEffect(bounds) {
        try {
            cameraState.animate(
                CameraUpdateFactory.newLatLngBounds(bounds, 100)
            )
        } catch (t: Throwable) {
            cameraState.move(CameraUpdateFactory.newLatLngZoom(
                LatLng(shapePoints.first().lat, shapePoints.first().lng),
                12f
            ))
        }
    }

    // 7) Color de la ruta
    val strokeColor = runCatching {
        Color(android.graphics.Color.parseColor(if (routeColorHex.startsWith("#")) routeColorHex else "#$routeColorHex"))
    }.getOrDefault(Color.Black)

    // 8) Renderiza el mapa
    GoogleMap(
        modifier            = Modifier.fillMaxSize(),
        cameraPositionState = cameraState
    ) {
        segments.values.forEach { pts ->
            Polyline(
                points = pts.map { LatLng(it.lat, it.lng) },
                width  = 6f,
                color  = strokeColor
            )
        }
    }
}
