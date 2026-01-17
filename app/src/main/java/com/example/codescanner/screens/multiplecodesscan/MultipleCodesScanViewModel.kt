package com.example.codescanner.screens.multiplecodesscan

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.codescanner.data.dao.CodeScannerDao
import com.example.codescanner.data.model.Code
import com.example.codescanner.data.model.Scan
import com.example.codescanner.data.model.ScanType
import com.example.codescanner.screens.singlecodescan.BufferItem
import com.example.codescanner.settings.SettingsManager
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MultipleCodesScanViewModel(
    private val dao : CodeScannerDao,
    private val settingsManager: SettingsManager,
) : ViewModel() {
    var isLoading by mutableStateOf(true)
        private set
    lateinit var options : BarcodeScannerOptions
    val scan : Scan = Scan(scanType = ScanType.MULTIPLE_CODES)
    private val _scannedCodesBuffer = mutableMapOf<String, BufferItem>()
    private val _codes = MutableStateFlow<MutableList<Code>>(mutableStateListOf())
    val codes = _codes.asStateFlow()
    var scanId : Long = 0

    init {
        viewModelScope.launch {
            options = settingsManager.getScannerOptions()
            isLoading = false
        }
    }

    fun onCodeScanned(code: Code) : Boolean {
        if(_codes.value.any { it.value == code.value }) {
            return false
        }
        if(_scannedCodesBuffer.containsKey(code.value)) {
            _scannedCodesBuffer[code.value]?.let {
                it.count++
                if(it.count == 8) {
                    _codes.value += code
                    _scannedCodesBuffer.clear()
                    return true
                }
                else {
                    return false
                }
            }
        }

        _scannedCodesBuffer[code.value] = BufferItem(code.value, 1)
        return false
    }

    fun removeCode(index : Int) {
        _codes.value.removeAt(index)
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
}
