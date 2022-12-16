package com.garan.tempo.mapping

import androidx.compose.ui.graphics.vector.PathNode
import java.nio.ByteBuffer

data class LatLng(val lat: Double, val lng: Double)

data class RouteMap private constructor(
    val coordsList: List<LatLng>,
    val maxLat: Double,
    val minLat: Double,
    val maxLng: Double,
    val minLng: Double,
) {
    private val latExtent = maxLat - minLat
    private val lngExtent = maxLng - minLng

    fun toPathNodeList(viewPortX: Double, viewPortY: Double): List<PathNode> {
        val xScale = viewPortX / lngExtent
        val yScale = viewPortY / latExtent
        val scale = minOf(xScale, yScale)

        // The vector, for now is a fixed aspect ratio; translate horizontally or vertically within
        // this space depending on whether the route is wider or taller.
        val xTrans = if (xScale > yScale) {
            (viewPortX - lngExtent * scale) / 2
        } else 0.0
        val yTrans = if (yScale > xScale) {
            (viewPortY - latExtent * scale) / 2
        } else 0.0
        val nodes = mutableListOf<PathNode>()
        if (coordsList.isNotEmpty()) {
            nodes.add(
                PathNode.MoveTo(
                    ((coordsList[0].lng - minLng) * scale + xTrans).toFloat(),
                    ((coordsList[0].lat - minLat) * scale + yTrans).toFloat()
                )
            )
            coordsList.drop(1).forEach { coord ->
                nodes.add(
                    PathNode.LineTo(
                        ((coord.lng - minLng) * scale + xTrans).toFloat(),
                        ((coord.lat - minLat) * scale + yTrans).toFloat()
                    )
                )
            }
        }
        return nodes
    }

    companion object {
        fun fromByteArray(byteArray: ByteArray): RouteMap {
            val latLongs = mutableListOf<LatLng>()
            val buffer = ByteBuffer.wrap(byteArray).asDoubleBuffer()
            var maxLat = Double.NEGATIVE_INFINITY
            var maxLng = Double.NEGATIVE_INFINITY
            var minLat = Double.POSITIVE_INFINITY
            var minLng = Double.POSITIVE_INFINITY
            while (buffer.hasRemaining()) {
                val lng = buffer.get()
                val lat = buffer.get()
                maxLat = maxOf(maxLat, lat)
                minLat = minOf(minLat, lat)
                maxLng = maxOf(maxLng, lng)
                minLng = minOf(minLng, lng)
                latLongs.add(
                    LatLng(lat = lat, lng = lng)
                )
            }
            return RouteMap(
                coordsList = latLongs,
                maxLat = maxLat,
                minLat = minLat,
                maxLng = maxLng,
                minLng = minLng
            )
        }
    }
}
