package com.example.miruta.data.gtfs

import android.content.Context
import com.example.miruta.data.models.ShapePoint
import com.opencsv.CSVReader
import java.io.InputStreamReader
import java.util.zip.ZipInputStream

fun parseShapeForRoute(
    context: Context,
    zipAssetName: String,
    routeId: String
): List<ShapePoint> {
    val shapeIds = mutableSetOf<String>()
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
                        all.drop(1).forEach { cols ->
                            if (cols[idxRoute] == routeId) {
                                shapeIds += cols[idxShape]
                            }
                        }
                    }
                    break
                }
                entry = zip.nextEntry
            }
        }
    }
    if (shapeIds.isEmpty()) return emptyList()

    val result = mutableListOf<ShapePoint>()
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
                            if (sid in shapeIds) {
                                result += ShapePoint(
                                    shapeId  = sid,
                                    lat      = cols[idxLat].toDouble(),
                                    lng      = cols[idxLng].toDouble(),
                                    sequence = cols[idxSequence].toInt()
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

    return result
}
