package com.example.miruta.data.gtfs

import com.example.miruta.data.models.Route
import com.opencsv.CSVReader
import java.io.InputStream
import java.io.InputStreamReader
import java.util.zip.ZipInputStream

fun parseRoutesFromGTFS(zipInputStream: InputStream): List<Route> {
    val routes = mutableListOf<Route>()
    ZipInputStream(zipInputStream).use { zip ->
        var e = zip.nextEntry
        while (e != null) {
            if (e.name.equals("routes.txt", true)) {
                CSVReader(InputStreamReader(zip)).use { r ->
                    val all = r.readAll()
                    val h = all.first()
                    val idxId    = h.indexOf("route_id")
                    val idxShort = h.indexOf("route_short_name")
                    val idxLong  = h.indexOf("route_long_name")
                    val idxType  = h.indexOf("route_type")
                    val idxColor = h.indexOf("route_color")
                    all.drop(1).forEach { c ->
                        routes += Route(
                            routeId        = c[idxId],
                            routeShortName = c[idxShort],
                            routeLongName  = c[idxLong],
                            routeType      = c[idxType].toInt(),
                            routeColor     = c.getOrNull(idxColor)?.takeIf { it.isNotBlank() }
                        )
                    }
                }
                break
            }
            e = zip.nextEntry
        }
    }
    return routes
}