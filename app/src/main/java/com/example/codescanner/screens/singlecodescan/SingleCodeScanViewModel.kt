package com.example.codescanner.screens.singlecodescan

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.codescanner.data.dao.CodeScannerDao
import com.example.codescanner.data.model.Code
import com.example.codescanner.data.model.Scan
import com.example.codescanner.data.model.ScanType
import com.example.codescanner.settings.SettingsManager
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.common.Barcode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Date

class SingleCodeScanViewModel(
    private val dao: CodeScannerDao,
    private val settingsManager: SettingsManager,
    private val goToScanDetails: (Long) -> Unit,
) : ViewModel() {
    var isLoading by mutableStateOf(true)
        private set
    private val date = Date()
    lateinit var options : BarcodeScannerOptions
        private set
    private val scannedCodesBuffer = mutableStateMapOf<String, BufferItem>()

    init {
        viewModelScope.launch {
            options = settingsManager.getScannerOptions()
            isLoading = false
        }
    }

    fun onCodeScanned(codes : List<Barcode>, width : Int, height : Int, rotationDegrees : Int) {
        val code = codes[0].rawValue.toString()
        if(scannedCodesBuffer.containsKey(code)) {
            scannedCodesBuffer[code]?.let {
                it.count++
                if(it.count == 8) {
                    scannedCodesBuffer.clear()
                    saveScan(code)
                }
            }
        }
        else {
            scannedCodesBuffer[code] = BufferItem(code, 1)
        }
    }

    private fun saveScan(code : String) {
        val scan = Scan(timestamp = date, scanType = ScanType.SINGLE_CODE, shortenValue = code)
        var scanId : Long = -1
        viewModelScope.launch {
            scanId = dao.insertScan(scan)
            dao.insertCode(Code(scanId = scanId, timestamp = Date(), value = code))
            withContext(Dispatchers.Main) {
                goToScanDetails(scanId)
            }
        }
    }

}