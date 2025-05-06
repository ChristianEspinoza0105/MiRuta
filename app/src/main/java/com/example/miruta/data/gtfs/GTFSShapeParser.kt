package com.example.miruta.data.gtfs

import android.content.Context
import com.example.miruta.data.models.ShapePoint
import com.opencsv.CSVReader
import java.io.InputStreamReader
import java.util.zip.ZipInputStream
import com.example.miruta.data.models.StopPoint

fun parseStopsForRoute(
    context: Context,
    zipAssetName: String,
    routeId: String
): List<StopPoint> {
    val tripIds = mutableSetOf<String>()
    val stopIds = mutableSetOf<String>()
    val stopPoints = mutableMapOf<String, StopPoint>()

    context.assets.open(zipAssetName).use { raw ->
        ZipInputStream(raw).use { zip ->
            var entry = zip.nextEntry
            while (entry != null) {
                if (entry.name.equals("trips.txt", true)) {
                    CSVReader(InputStreamReader(zip)).use { r ->
                        val all = r.readAll()
                        val header = all.first()
                        val idxRoute = header.indexOf("route_id")
                        val idxTrip = header.indexOf("trip_id")
                        all.drop(1).forEach { cols ->
                            if (cols[idxRoute] == routeId) {
                                tripIds.add(cols[idxTrip])
                            }
                        }
                    }
                    break
                }
                entry = zip.nextEntry
            }
        }
    }

    context.assets.open(zipAssetName).use { raw ->
        ZipInputStream(raw).use { zip ->
            var entry = zip.nextEntry
            while (entry != null) {
                if (entry.name.equals("stop_times.txt", true)) {
                    CSVReader(InputStreamReader(zip)).use { r ->
                        val all = r.readAll()
                        val header = all.first()
                        val idxTrip = header.indexOf("trip_id")
                        val idxStop = header.indexOf("stop_id")
                        all.drop(1).forEach { cols ->
                            if (cols[idxTrip] in tripIds) {
                                stopIds.add(cols[idxStop])
                            }
                        }
                    }
                    break
                }
                entry = zip.nextEntry
            }
        }
    }

    context.assets.open(zipAssetName).use { raw ->
        ZipInputStream(raw).use { zip ->
            var entry = zip.nextEntry
            while (entry != null) {
                if (entry.name.equals("stops.txt", true)) {
                    CSVReader(InputStreamReader(zip)).use { r ->
                        val all = r.readAll()
                        val header = all.first()
                        val idxStop = header.indexOf("stop_id")
                        val idxLat = header.indexOf("stop_lat")
                        val idxLng = header.indexOf("stop_lon")
                        all.drop(1).forEach { cols ->
                            if (cols[idxStop] in stopIds) {
                                stopPoints[cols[idxStop]] = StopPoint(
                                    stopId = cols[idxStop],
                                    lat = cols[idxLat].toDouble(),
                                    lng = cols[idxLng].toDouble()
                                )
                            }
                        }
                    }
                    break
                }
                entry = zip.nextEntry
            }
        }
    }

    return stopPoints.values.toList()
}

fun parseShapeForRoute(
    context: Context,
    zipAssetName: String,
    routeId: String
): Map<Int, List<ShapePoint>> {
    val shapeIdsByDirection = mutableMapOf<Int, MutableSet<String>>()
    context.assets.open(zipAssetName).use { raw ->
        ZipInputStream(raw).use { zip ->
            var entry = zip.nextEntry
            while (entry != null) {
                if (entry.name.equals("trips.txt", true)) {
                    CSVReader(InputStreamReader(zip)).use { r ->
                        val all = r.readAll()
                        val header = all.first()
                        val idxRoute = header.indexOf("route_id")
                        val idxShape = header.indexOf("shape_id")
                        val idxDirection = header.indexOf("direction_id")
                        all.drop(1).forEach { cols ->
                            if (cols[idxRoute] == routeId) {
                                val dirId = cols[idxDirection].toIntOrNull() ?: 0
                                shapeIdsByDirection.getOrPut(dirId) { mutableSetOf() }
                                    .add(cols[idxShape])
                            }
                        }
                    }
                    break
                }
                entry = zip.nextEntry
            }
        }
    }

    val shapePointsById = mutableMapOf<String, MutableList<ShapePoint>>()
    context.assets.open(zipAssetName).use { raw ->
        ZipInputStream(raw).use { zip ->
            var entry = zip.nextEntry
            while (entry != null) {
                if (entry.name.equals("shapes.txt", true)) {
                    CSVReader(InputStreamReader(zip)).use { r ->
                        val all = r.readAll()
                        val header = all.first()
                        val idxShapeId  = header.indexOf("shape_id")
                        val idxLat      = header.indexOf("shape_pt_lat")
                        val idxLng      = header.indexOf("shape_pt_lon")
                        val idxSequence = header.indexOf("shape_pt_sequence")
                        all.drop(1).forEach { cols ->
                            val sid = cols[idxShapeId]
                            val point = ShapePoint(
                                shapeId = sid,
                                lat = cols[idxLat].toDouble(),
                                lng = cols[idxLng].toDouble(),
                                sequence = cols[idxSequence].toInt()
                            )
                            shapePointsById.getOrPut(sid) { mutableListOf() }.add(point)
                        }
                    }
                    break
                }
                entry = zip.nextEntry
            }
        }
    }

    val result = mutableMapOf<Int, List<ShapePoint>>()
    shapeIdsByDirection.forEach { (directionId, shapeIds) ->
        val points = shapeIds.flatMap { shapePointsById[it].orEmpty() }
            .sortedBy { it.sequence }
        result[directionId] = points
    }

    return result
}