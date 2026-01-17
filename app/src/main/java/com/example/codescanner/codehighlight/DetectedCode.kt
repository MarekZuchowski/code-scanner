package com.example.codescanner.codehighlight

import android.graphics.Point
import com.example.codescanner.data.model.Code

data class DetectedCode(
    val code : Code,
    val corners: List<Point>,
)
