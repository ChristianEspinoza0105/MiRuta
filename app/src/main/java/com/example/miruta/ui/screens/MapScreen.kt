package com.example.miruta.ui.screens

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.example.miruta.R
import com.example.miruta.data.models.ShapePoint
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.*
import com.example.miruta.data.gtfs.parseShapeForRoute
import com.example.miruta.data.gtfs.parseStopsForRoute
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.MapStyleOptions
import kotlinx.coroutines.Dispatchers
import android.location.Location
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.miruta.ui.theme.AppTypography
import kotlin.math.*
import com.google.android.gms.maps.model.JointType
import kotlinx.coroutines.withContext

@Composable
fun MapScreen(
    routeId: String,
    routeColorHex: String,
    routeShortName: String
) {
    val context = LocalContext.current
    val mapStyleOptions = remember {
        MapStyleOptions.loadRawResourceStyle(context, R.raw.map_style)
    }

    LaunchedEffect(Unit) {
        MapsInitializer.initialize(context, MapsInitializer.Renderer.LATEST) {}
    }

    var rawPoints by remember { mutableStateOf<List<ShapePoint>?>(null) }
    var stopPoints by remember { mutableStateOf<List<LatLng>>(emptyList()) }

    LaunchedEffect(routeId) {
        rawPoints = withContext(Dispatchers.IO) {
            val allShapes = parseShapeForRoute(context, "rutas_gtfs.zip", routeId)
            allShapes.values.firstOrNull()
        }

        stopPoints = withContext(Dispatchers.IO) {
            parseStopsForRoute(context, "rutas_gtfs.zip", routeId).map { LatLng(it.lat, it.lng) }
        }
    }

    if (rawPoints == null || stopPoints.isEmpty()) {
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

    val segments = remember(shapePoints) {
        shapePoints.groupBy { it.shapeId }
            .mapValues { (_, points) -> points.sortedBy { it.sequence } }
    }

    val bounds = remember(shapePoints) {
        LatLngBounds.builder().apply {
            shapePoints.forEach { include(LatLng(it.lat, it.lng)) }
        }.build()
    }

    val cameraState = rememberCameraPositionState()

    LaunchedEffect(bounds) {
        try {
            cameraState.animate(CameraUpdateFactory.newLatLngBounds(bounds, 100))
        } catch (_: Throwable) {
            cameraState.move(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(shapePoints.first().lat, shapePoints.first().lng),
                    12f
                )
            )
        }
    }

    val strokeColor = runCatching {
        Color(android.graphics.Color.parseColor(
            if (routeColorHex.startsWith("#")) routeColorHex else "#$routeColorHex"
        ))
    }.getOrDefault(Color.Black)

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraState,
            properties = MapProperties(mapStyleOptions = mapStyleOptions)
        ) {
            segments.values.forEach { points ->
                Polyline(
                    points = points.map { LatLng(it.lat, it.lng) },
                    width = 20f,
                    color = strokeColor,
                    jointType = JointType.ROUND,
                    startCap = com.google.android.gms.maps.model.RoundCap(),
                    endCap = com.google.android.gms.maps.model.RoundCap()
                )
                drawStops(stopPoints, points, cameraState)
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Spacer(modifier = Modifier.height(14.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color.White)
                    .height(56.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxSize().padding(start = 16.dp)
                ) {
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = routeShortName,
                        color = strokeColor,
                        style = TextStyle(
                            fontFamily = AppTypography.h2.fontFamily,
                            fontWeight = AppTypography.h2.fontWeight,
                            fontSize = 34.sp
                        ),
                    )
                }
            }
        }
    }
}

@Composable
fun drawStops(
    stopPoints: List<LatLng>,
    shapePoints: List<ShapePoint>,
    cameraState: CameraPositionState
) {
    if (shapePoints.size <= 2) return

    val zoomLevel = cameraState.position.zoom

    if (zoomLevel < 20f) return

    val context = LocalContext.current

    val iconSize = (30 + (zoomLevel * 6)).toInt()

    val iconBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.ic_stop)
    val scaledIcon = Bitmap.createScaledBitmap(iconBitmap, iconSize, iconSize, false)

    stopPoints
        .filter { stop ->
            isStopCloseToShape(stop, shapePoints) &&
                    stop != LatLng(shapePoints.first().lat, shapePoints.first().lng) &&
                    stop != LatLng(shapePoints.last().lat, shapePoints.last().lng)
        }
        .forEach { stop ->
            Marker(
                state = rememberMarkerState(position = stop),
                title = "Parada",
                icon = BitmapDescriptorFactory.fromBitmap(scaledIcon)
            )
        }
}

fun distanceBetween(point1: LatLng, point2: LatLng): Float {
    val result = FloatArray(1)
    Location.distanceBetween(
        point1.latitude, point1.longitude,
        point2.latitude, point2.longitude,
        result
    )
    return result[0]
}

fun distanceToSegment(p: LatLng, start: LatLng, end: LatLng): Double {
    val lat1 = start.latitude
    val lng1 = start.longitude
    val lat2 = end.latitude
    val lng2 = end.longitude
    val lat = p.latitude
    val lng = p.longitude

    if (lat1 == lat2 && lng1 == lng2) {
        return distanceBetween(p, start).toDouble()
    }

    val t = ((lat - lat1) * (lat2 - lat1) + (lng - lng1) * (lng2 - lng1)) /
            ((lat2 - lat1).pow(2) + (lng2 - lng1).pow(2))

    val tClamped = max(0.0, min(1.0, t))
    val projLat = lat1 + tClamped * (lat2 - lat1)
    val projLng = lng1 + tClamped * (lng2 - lng1)

    return distanceBetween(p, LatLng(projLat, projLng)).toDouble()
}

fun isStopCloseToShape(stop: LatLng, shapePoints: List<ShapePoint>, maxDistance: Double = 30.0): Boolean {
    for (i in 0 until shapePoints.size - 1) {
        val start = LatLng(shapePoints[i].lat, shapePoints[i].lng)
        val end = LatLng(shapePoints[i + 1].lat, shapePoints[i + 1].lng)
        val distance = distanceToSegment(stop, start, end)
        if (distance <= maxDistance) return true
    }
    return false
}