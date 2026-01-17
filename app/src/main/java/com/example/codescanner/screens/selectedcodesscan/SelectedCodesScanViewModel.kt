package com.example.codescanner.screens.selectedcodesscan

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.codescanner.codehighlight.DetectedCode
import com.example.codescanner.data.dao.CodeScannerDao
import com.example.codescanner.data.model.Code
import com.example.codescanner.data.model.Scan
import com.example.codescanner.data.model.ScanType
import com.example.codescanner.settings.SettingsManager
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SelectedCodesScanViewModel(
    private val dao : CodeScannerDao,
    private val settingsManager: SettingsManager,
) : ViewModel() {
    var isLoading by mutableStateOf(true)
        private set
    lateinit var options : BarcodeScannerOptions
    val scan : Scan = Scan(scanType = ScanType.SELECTED_CODES)
    private val _codes = MutableStateFlow<MutableList<Code>>(mutableStateListOf())
    val codes = _codes.asStateFlow()
    var scanId : Long = 0

    private val _detectedCodes = MutableStateFlow<MutableList<DetectedCode>>(mutableStateListOf())
    val detectedCodes = _detectedCodes.asStateFlow()
    var currentWidth by mutableIntStateOf(1280)
    var currentHeight by mutableIntStateOf(720)
    var currentRotationDegrees by mutableIntStateOf(90)

    init {
        viewModelScope.launch {
            options = settingsManager.getScannerOptions()
            isLoading = false
        }
    }

    fun addCode(code: DetectedCode) {
        if(_codes.value.none { it.value == code.code.value }) {
            _codes.value += code.code
        }
    }

    fun removeCode(index : Int) {
        _codes.value.removeAt(index)
    }

    fun remove(code : DetectedCode) {
        _codes.value.removeIf {it.value == code.code.value}
    }

    suspend fun saveScanWithCodes() {
        var comment = ""
        for(code in _codes.value.take(5)) {
            if(code.value.length > 30) {
                comment += "${code.value.take(30)}..., "
            }
            else {
                comment += "${code.value}, "
            }
        }
        scanId = dao.insertScan(Scan(timestamp = scan.timestamp, scanType = scan.scanType ,shortenValue = comment))
        codes.value.forEach { it.scanId = scanId }
        dao.insertCodes(codes.value)
    }

    fun addDetectedCode(code : DetectedCode) {
        _detectedCodes.value += code
    }

    fun clearDetectedCodes() {
        _detectedCodes.value.clear()
    }

}
