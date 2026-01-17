package com.example.codescanner.screens.highlightcodescan

import com.example.codescanner.codehighlight.DetectedCode

data class HighlightCodeScanState(
    val isLoading : Boolean = true,
    val isSoundSignalOn : Boolean = false,
    val detectedCodes : List<DetectedCode> = emptyList(),
    val enteredCode: String = "",
    val textFieldValue : String = "",
    val isEnteringCode : Boolean = true,
    val currentWidth : Int = 1280,
    val currentHeight : Int = 720,
    val currentRotationDegrees : Int = 90,
    val isDetected : Boolean = false,
    val lastDetectionTime : Long = 0L
)