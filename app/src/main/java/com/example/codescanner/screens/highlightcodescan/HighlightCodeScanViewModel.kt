package com.example.codescanner.screens.highlightcodescan

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.codescanner.settings.SettingsManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HighlightCodeScanViewModel(
    private val settingsManager: SettingsManager,
) : ViewModel() {
    private val _state = MutableStateFlow(HighlightCodeScanState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            val isSoundSignalOn = settingsManager.getSoundSignalOption()
            _state.update {
                it.copy(
                    isSoundSignalOn = isSoundSignalOn,
                    isLoading = false,
                )
            }
        }
    }

    fun onEvent(event : HighlightCodeScanEvent) {
        when(event) {
            is HighlightCodeScanEvent.SetDetectedCodes -> {
                _state.update {
                    it.copy(
                        detectedCodes = event.detectedCodes,
                        currentWidth = event.width,
                        currentHeight = event.height,
                        currentRotationDegrees = event.rotationDegrees,
                        isDetected = if(event.detectedCodes.any { it.code.value == state.value.enteredCode }) true else false
                    )
                }
            }
            HighlightCodeScanEvent.ClearDetectedCodes -> {
                _state.update { it.copy(
                    detectedCodes = emptyList(),
                    isDetected = false
                ) }
            }
            HighlightCodeScanEvent.HideDialog -> {
                _state.update { it.copy(
                    textFieldValue = "",
                    isEnteringCode = false
                ) }
            }
            HighlightCodeScanEvent.SaveEnteredCode -> {
                _state.update { it.copy(
                    enteredCode = it.textFieldValue,
                    textFieldValue = "",
                    lastDetectionTime = 0,
                    isEnteringCode = false
                ) }
            }
            is HighlightCodeScanEvent.SetEnteredCode -> {
                _state.update { it.copy(
                    textFieldValue = event.enteredCode
                ) }
            }
            HighlightCodeScanEvent.ShowDialog -> {
                _state.update { it.copy(
                    textFieldValue = it.enteredCode,
                    isEnteringCode = true
                ) }
            }

            is HighlightCodeScanEvent.SetLastDetectionTime -> {
                _state.update { it.copy(
                    lastDetectionTime = event.lastDetectionTime
                ) }
            }
        }
    }
}
