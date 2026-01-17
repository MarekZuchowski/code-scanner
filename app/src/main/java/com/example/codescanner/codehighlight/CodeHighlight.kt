package com.example.codescanner.codehighlight

import android.graphics.Point
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import kotlin.math.max
import kotlin.math.min

fun getScaleFactors(canvaWidth : Float, canvaHeight : Float, imageWidth : Int, imageHeight : Int, rotationDegrees : Int) : Pair<Float, Float> {
    var scaleX = canvaWidth / imageHeight
    var scaleY = canvaHeight / imageWidth
    if(rotationDegrees == 0 || rotationDegrees == 180) {
        scaleX = canvaWidth / imageWidth
        scaleY = canvaHeight / imageHeight
    }

    return Pair(scaleX, scaleY)
}

fun scalePoints(scaleFactors : Pair<Float, Float>, points : List<Point>) : List<Offset> {
    val scaledPoints = mutableListOf<Offset>()

    for(point in points) {
        scaledPoints.add(Offset(point.x * scaleFactors.first, point.y * scaleFactors.second))
    }

    return scaledPoints
}

fun DrawScope.drawPolygon(points: List<Offset>, color : Color) {
    if (points.size < 4) return
    val path = androidx.compose.ui.graphics.Path().apply {
        moveTo(points[0].x, points[0].y)
        for (i in 1 until points.size) {
            lineTo(points[i].x, points[i].y)
        }
        close()
    }
    drawPath(
        path = path,
        color = color,
        style = Stroke(width = 5f)
    )
}

fun isPointInsidePolygon(point: Offset, polygon: List<Offset>): Boolean {
    val n = polygon.size
    var inside = false

    if (n < 3) return false

    var p1 = polygon[0]
    for (i in 1..n) {
        val p2 = polygon[i % n]
        if (point.y > min(p1.y, p2.y)) {
            if (point.y <= max(p1.y, p2.y)) {
                if (point.x <= max(p1.x, p2.x)) {
                    if (p1.y != p2.y) {
                        val xinters = (point.y - p1.y) * (p2.x - p1.x) / (p2.y - p1.y) + p1.x
                        if (p1.x == p2.x || point.x <= xinters) {
                            inside = !inside
                        }
                    }
                }
            }
        }
        p1 = p2
    }

    return inside
}
