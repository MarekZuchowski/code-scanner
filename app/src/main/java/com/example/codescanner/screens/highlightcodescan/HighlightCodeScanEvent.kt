package com.example.codescanner.screens.highlightcodescan

import com.example.codescanner.codehighlight.DetectedCode

sealed interface HighlightCodeScanEvent {
    data class SetDetectedCodes(
        val detectedCodes: List<DetectedCode>,
        val width: Int,
        val height: Int,
        val rotationDegrees: Int
    ) : HighlightCodeScanEvent
    object ClearDetectedCodes : HighlightCodeScanEvent
    object ShowDialog : HighlightCodeScanEvent
    object HideDialog : HighlightCodeScanEvent
    data class SetEnteredCode(val enteredCode : String) : HighlightCodeScanEvent
    object SaveEnteredCode : HighlightCodeScanEvent
    data class SetLastDetectionTime(val lastDetectionTime: Long) : HighlightCodeScanEvent
}