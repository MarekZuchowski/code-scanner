package com.example.codescanner.screens.highlightcodescan

import android.media.Ringtone
import android.media.RingtoneManager
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.codescanner.R
import com.example.codescanner.camera.CameraPermissionHandler
import com.example.codescanner.camera.CameraPreview
import com.example.codescanner.camera.checkCameraPermission
import com.example.codescanner.codehighlight.DetectedCode
import com.example.codescanner.codehighlight.drawPolygon
import com.example.codescanner.codehighlight.getScaleFactors
import com.example.codescanner.codehighlight.scalePoints
import com.example.codescanner.data.model.Code
import com.example.codescanner.settings.SettingsManager
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.common.Barcode

@Composable
fun HighlightCodeScreen(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val controller = remember {
        LifecycleCameraController(context).apply {
            setEnabledUseCases(CameraController.IMAGE_ANALYSIS)
        }
    }
    var hasCameraPermission by remember { mutableStateOf(checkCameraPermission(context.applicationContext)) }
    val settingsManager = SettingsManager(context)

    val viewModel : HighlightCodeScanViewModel = viewModel {
        HighlightCodeScanViewModel(settingsManager)
    }
    val state by viewModel.state.collectAsStateWithLifecycle(lifecycleOwner = lifecycleOwner)
//    var isSoundSignalOn by rememberSaveable { mutableStateOf(true) }
//    LaunchedEffect(Unit) {
//        isSoundSignalOn = settingsManager.getSoundSignalOption()
//    }
    val notificationUri = rememberSaveable { RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION) }
    val ringtone : Ringtone = remember { RingtoneManager.getRingtone(context, notificationUri) }

    if(state.isSoundSignalOn) {
        LaunchedEffect(state.isDetected) {
            if(state.isDetected) {
                val currentTime = System.currentTimeMillis()
                if(!ringtone.isPlaying && (currentTime - state.lastDetectionTime > 5000)) {
                    ringtone.play()
                    viewModel.onEvent(HighlightCodeScanEvent.SetLastDetectionTime(currentTime))
                }
            }
        }
    }

    LifecycleResumeEffect(Unit) {
        hasCameraPermission = checkCameraPermission(context.applicationContext)
        onPauseOrDispose { }
    }

    fun onBarcodesScanned(barcodes : List<Barcode>, width : Int, height : Int, rotationDegrees : Int) {
        if(state.enteredCode.isNotBlank()) {
            val codeList : MutableList<DetectedCode> = mutableListOf()
            for(barcode in barcodes) {
                val cornerPoints = barcode.cornerPoints
                if(cornerPoints != null) {
                    codeList += DetectedCode(
                        Code(value = barcode.rawValue.toString()),
                        listOf(
                            cornerPoints[0],
                            cornerPoints[1],
                            cornerPoints[2],
                            cornerPoints[3]
                        )
                    )
                }
            }
            viewModel.onEvent(HighlightCodeScanEvent.SetDetectedCodes(codeList, width, height, rotationDegrees))
        }
    }

    Scaffold(

    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            if (!hasCameraPermission) {
                CameraPermissionHandler()
            }
            else if(!state.isLoading){
                CameraPreview(
                    controller = controller,
                    scannerOptions = BarcodeScannerOptions.Builder().build(),
                    onBarcodesScanned = ::onBarcodesScanned,
                    onBarcodeUndetected = {
                        viewModel.onEvent(HighlightCodeScanEvent.ClearDetectedCodes)
                    },
                    modifier = Modifier
                        .fillMaxSize()
                )

                if(state.detectedCodes.isNotEmpty()) {
                    EnteredCodeHighlight(state)
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .background(MaterialTheme.colorScheme.background)
                        .padding(horizontal = 20.dp, vertical = 12.dp),

                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = state.enteredCode,
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Button(
                        onClick = {
                            viewModel.onEvent(HighlightCodeScanEvent.ShowDialog)
                        }
                    ) {
                        Text(text = stringResource(R.string.enter_code))
                    }
                }
            }
        }
        if(hasCameraPermission && state.isEnteringCode) {
            AlertDialog(
                onDismissRequest = {
                    viewModel.onEvent(HighlightCodeScanEvent.HideDialog)
                },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.onEvent(HighlightCodeScanEvent.SaveEnteredCode)
                        },
                        enabled = state.textFieldValue.isNotEmpty()
                    ) {
                        Text(text = stringResource(R.string.save))
                    }
                },
                text = {
                    Column {
                        TextField(
                            value = state.textFieldValue,
                            label = {
                                Text(text = stringResource(R.string.textfield_enter_code))
                            },
                            onValueChange = {
                                viewModel.onEvent(HighlightCodeScanEvent.SetEnteredCode(it))
                            }
                        )
                    }
                }
            )
        }
    }
}

@Composable
fun EnteredCodeHighlight(
    state : HighlightCodeScanState,
    modifier: Modifier = Modifier
) {
    Canvas(
        modifier = modifier
            .fillMaxSize()
    ) {
        val scaleFactors = getScaleFactors(size.width, size.height, state.currentWidth, state.currentHeight, state.currentRotationDegrees)
        for(code in state.detectedCodes) {
            if (code.corners.size == 4) {
                val scaledPoints = scalePoints(scaleFactors, code.corners)
                val color = if(code.code.value.equals(state.enteredCode)) Color.Green else Color.Red
                drawPolygon(scaledPoints, color)
            }
        }
    }
}
