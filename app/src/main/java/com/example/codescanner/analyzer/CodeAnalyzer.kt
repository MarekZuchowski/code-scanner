package com.example.codescanner.analyzer

import android.annotation.SuppressLint
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage

class CodeAnalyzer(
    scannerOptions: BarcodeScannerOptions,
    private val onCodeUndetected: () -> Unit,
    private val onCodeScanned: (List<Barcode>, Int, Int, Int) -> Unit,
) : ImageAnalysis.Analyzer {

    private val scanner = BarcodeScanning.getClient(scannerOptions)

    @SuppressLint("UnsafeExperimentalUsageError", "UnsafeOptInUsageError")
    override fun analyze(imageProxy: ImageProxy) {
        val image = InputImage.fromMediaImage(imageProxy.image!!, imageProxy.imageInfo.rotationDegrees)

        scanner.process(image)
            .addOnSuccessListener { codes ->
                if (codes.isNotEmpty()) {
                    val filteredBarcodes: List<Barcode> = codes.filter { it.rawValue != null && it.rawValue.toString().isNotBlank() }
                    onCodeScanned(filteredBarcodes, image.width, image.height, image.rotationDegrees)
                }
                else {
                    onCodeUndetected()
                }
            }
            .addOnCompleteListener {
                imageProxy.close()
            }

    }

}
