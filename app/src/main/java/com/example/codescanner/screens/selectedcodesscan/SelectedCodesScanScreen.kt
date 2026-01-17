package com.example.codescanner.screens.selectedcodesscan

import android.widget.Toast
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DocumentScanner
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.codescanner.R
import com.example.codescanner.camera.CameraPermissionHandler
import com.example.codescanner.camera.CameraPreview
import com.example.codescanner.data.CodeScannerDatabase
import com.example.codescanner.settings.SettingsManager
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.example.codescanner.camera.checkCameraPermission
import com.example.codescanner.codehighlight.DetectedCode
import com.example.codescanner.codehighlight.drawPolygon
import com.example.codescanner.codehighlight.getScaleFactors
import com.example.codescanner.codehighlight.isPointInsidePolygon
import com.example.codescanner.codehighlight.scalePoints
import com.example.codescanner.data.model.Code
import com.example.codescanner.navigation.Route
import com.example.codescanner.screens.multiplecodesscan.MultipleCodesScanBottomSheetContent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectedCodesScanScreen(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val controller = remember {
        LifecycleCameraController(context).apply {
            setEnabledUseCases(CameraController.IMAGE_ANALYSIS)
        }
    }

    val dao = CodeScannerDatabase.getInstance(context).codeScannerDao()
    val settingsManager = SettingsManager(context)
    var hasCameraPermission by remember { mutableStateOf(checkCameraPermission(context.applicationContext)) }
    val scope = rememberCoroutineScope()
    val scaffoldState = rememberBottomSheetScaffoldState()

    val viewModel : SelectedCodesScanViewModel = viewModel {
        SelectedCodesScanViewModel(dao, settingsManager)
    }
    val codes by viewModel.codes.collectAsStateWithLifecycle(lifecycleOwner = lifecycleOwner)
    val detectedBarcodes by viewModel.detectedCodes.collectAsStateWithLifecycle(lifecycleOwner = lifecycleOwner)
    val isLoading = viewModel.isLoading

    LifecycleResumeEffect(Unit) {
        hasCameraPermission = checkCameraPermission(context.applicationContext)
        onPauseOrDispose { }
    }

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetPeekHeight = 0.dp,
        sheetContent = {
            MultipleCodesScanBottomSheetContent(
                codes = codes,
                onRemove = { index ->
                    viewModel.removeCode(index)
                },
                onSave = {
                    scope.launch(Dispatchers.IO) {
                        viewModel.saveScanWithCodes()
                        withContext(Dispatchers.Main) {
                            navController.navigate(
                                Route.ScanDetailsRoute(
                                    scanId = viewModel.scanId
                                )
                            )
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
            )
        },
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
            else if(!isLoading) {
                CameraPreview(
                    controller = controller,
                    scannerOptions = viewModel.options,
                    onBarcodesScanned = { barcodes, width, height, rotationDegrees ->
                        viewModel.clearDetectedCodes()
                        viewModel.currentWidth = width
                        viewModel.currentHeight = height
                        viewModel.currentRotationDegrees = rotationDegrees
                        for (barcode in barcodes) {
                            val cornerPoints = barcode.cornerPoints
                            if (cornerPoints != null) {
                                viewModel.addDetectedCode(
                                    DetectedCode(
                                        Code(value = barcode.rawValue.toString()),
                                        listOf(
                                            cornerPoints[0],
                                            cornerPoints[1],
                                            cornerPoints[2],
                                            cornerPoints[3]
                                        )
                                    )
                                )
                            }
                        }
                    },
                    onBarcodeUndetected = {
                        viewModel.clearDetectedCodes()
                    },
                    modifier = Modifier
                        .fillMaxSize()
                )


                if (detectedBarcodes.isNotEmpty()) {
                    MultipleCodesHighlight(
                        detectedBarcodes,
                        codes,
                        viewModel.currentWidth,
                        viewModel.currentHeight,
                        viewModel.currentRotationDegrees,
                        { code ->
                            viewModel.addCode(code)
                            Toast.makeText(context, context.getString(R.string.scanned) + ": ${code.code.value}", Toast.LENGTH_SHORT).show()
                        },
                        { code ->
                            viewModel.remove(code)
                            Toast.makeText(context, context.getString(R.string.removed) + ": ${code.code.value}", Toast.LENGTH_SHORT).show()
                        },
                    )
                }

                Button(
                    onClick = {
                        scope.launch {
                            scaffoldState.bottomSheetState.expand()
                        }
                    },
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .padding(50.dp)
                        .align(Alignment.BottomCenter)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.DocumentScanner,
                            contentDescription = "Open code list",
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text(text = codes.size.toString())
                    }
                }
            }
        }
    }
}

@Composable
fun MultipleCodesHighlight(
    detectedBarcodes : List<DetectedCode>,
    codes : List<Code>,
    width : Int,
    height : Int,
    rotationDegrees : Int,
    addCode : (DetectedCode) -> Unit,
    removeCode : (DetectedCode) -> Unit,
    modifier: Modifier = Modifier
) {
    Canvas(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    val scaleFactors = getScaleFactors(size.width.toFloat(), size.height.toFloat(), width, height, rotationDegrees)

                    detectedBarcodes.forEach { barcode ->
                        val scaledPoints = scalePoints(scaleFactors, barcode.corners)
                        if(isPointInsidePolygon(offset, scaledPoints)) {
                            if(codes.none { it.value == barcode.code.value }) {
                                addCode(barcode)
                            }
                            else {
                                removeCode(barcode)
                            }
                        }

                    }
                }
            }
    ) {

        val scaleFactors = getScaleFactors(size.width, size.height, width, height, rotationDegrees)

        for(barcode in detectedBarcodes) {
            if (barcode.corners.size == 4) {
                val scaledPoints = scalePoints(scaleFactors, barcode.corners)
                val color = if(codes.none { it.value == barcode.code.value }) Color.Red else Color.Green

                drawPolygon(scaledPoints, color)
            }
        }
    }
}
