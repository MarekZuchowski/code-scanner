package com.example.codescanner.screens.singlecodescan

import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.codescanner.camera.CameraPermissionHandler
import com.example.codescanner.camera.CameraPreview
import com.example.codescanner.camera.checkCameraPermission
import com.example.codescanner.data.CodeScannerDatabase
import com.example.codescanner.navigation.Route
import com.example.codescanner.settings.SettingsManager

@Composable
fun SingleCodeScanScreen(
    navController: NavController
) {
    val context = LocalContext.current
    val controller = remember {
        LifecycleCameraController(context).apply {
            setEnabledUseCases(CameraController.IMAGE_ANALYSIS)
        }
    }
    val dao = CodeScannerDatabase.getInstance(context).codeScannerDao()
    val settingsManager = SettingsManager(context)
    var hasCameraPermission by remember { mutableStateOf(checkCameraPermission(context.applicationContext)) }
    val viewModel : SingleCodeScanViewModel = viewModel {
        SingleCodeScanViewModel(dao, settingsManager) { scanId ->
            navController.navigate(
                Route.ScanDetailsRoute(
                    scanId = scanId
                )
            )
        }
    }
    val isLoading = viewModel.isLoading

    LifecycleResumeEffect(Unit) {
        hasCameraPermission = checkCameraPermission(context.applicationContext)
        onPauseOrDispose { }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center,
        ) {
            if(!hasCameraPermission) {
                CameraPermissionHandler()
            }
            else if(!isLoading) {
                CameraPreview(
                    controller = controller,
                    scannerOptions = viewModel.options,
                    onBarcodesScanned = viewModel::onCodeScanned,
                    onBarcodeUndetected = {},
                    modifier = Modifier
                        .fillMaxSize()
                )
            }
        }
    }
}

data class BufferItem(
    val codeValue : String,
    var count : Int
)
