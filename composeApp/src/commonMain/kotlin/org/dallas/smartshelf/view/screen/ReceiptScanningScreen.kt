package org.dallas.smartshelf.view.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import org.dallas.smartshelf.viewmodel.ReceiptScanningViewModel

@Composable
fun ReceiptScanningScreen(
    viewState: ReceiptScanningViewModel.ViewState,
    onAction: (ReceiptScanningViewModel.Action) -> Unit
) {
    var isScanning by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        verticalArrangement = Arrangement.Top,
    ) {
        val lifecycleOwner = LocalLifecycleOwner.current
        val context = LocalContext.current
        val cameraProviderFuture =
            remember { runCatching { ProcessCameraProvider.getInstance(context) }.getOrNull() }
        val previewView = remember { PreviewView(context) }
        val selector = remember {
            CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build()
        }

        val preview = remember {
            androidx.camera.core.Preview.Builder()
                .build()
        }
        val imageAnalysis = remember {
            ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
        }
        preview.setSurfaceProvider(previewView.surfaceProvider)

        val analyzer = remember {
            OCRAnalyzer(
                onDetected = { detectedText: String ->
                    isScanning = false
                    cameraProviderFuture?.get()?.unbindAll()
                    onAction(ReceiptScanningViewModel.Action.OCRSuccess(detectedText))
                },
                onNotDetected = { message ->
                    Log.e("OCRScanningScreen", "onNotDetected: $message")
                    onAction(ReceiptScanningViewModel.Action.OCRFailed(message ?: "Unknown error"))
                }
            )
        }

        LaunchedEffect(isScanning) {
            if (isScanning) {
                imageAnalysis.setAnalyzer(
                    ContextCompat.getMainExecutor(context),
                    analyzer
                )
            } else {
                imageAnalysis.clearAnalyzer()
            }
        }

        Box(modifier = Modifier.fillMaxSize()) {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = {
                    enableCamera(
                        cameraProviderFuture,
                        lifecycleOwner,
                        selector,
                        preview,
                        imageAnalysis
                    )
                    previewView
                }
            )

            Button(
                onClick = {
                    isScanning = !isScanning
                    onAction(ReceiptScanningViewModel.Action.StartProcessing)
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 32.dp)
            ) {
                Text("Start Scanning")
            }
        }

        DisposeCameraAfterFinish {
            cameraProviderFuture?.get()?.unbindAll()
        }
    }
    handleEvent(viewState.consumableEvent, LocalContext.current)
}

private fun handleEvent(
    consumableEvent: ConsumableEvent<ReceiptScanningViewModel.Event>,
    context: Context
) {
    consumableEvent.handleEvent { event ->
        when (event) {
            is ReceiptScanningViewModel.Event.OCRFailed -> {
                Toast.makeText(
                    context,
                    "Failed to scan text",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}

private fun enableCamera(
    cameraProviderFuture: ListenableFuture<ProcessCameraProvider>?,
    lifecycleOwner: LifecycleOwner,
    selector: CameraSelector,
    preview: androidx.camera.core.Preview,
    imageAnalysis: ImageAnalysis
) {
    try {
        cameraProviderFuture?.get()?.unbindAll()
        cameraProviderFuture?.get()?.bindToLifecycle(
            lifecycleOwner,
            selector,
            preview,
            imageAnalysis
        )
    } catch (e: IllegalStateException) {
        Log.e("CameraScanner", "Error:${e.message}")
    } catch (e: IllegalArgumentException) {
        Log.e("CameraScanner", "Error:${e.message}")
    }
}

@Composable
private fun DisposeCameraAfterFinish(action: () -> Unit) {
    DisposableEffect(Unit) {
        onDispose {
            action()
        }
    }
}