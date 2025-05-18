package org.dallas.smartshelf.view.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import org.dallas.smartshelf.viewmodel.BarcodeScanningViewModel

@Composable
fun BarcodeScanningScreen(
    viewState: BarcodeScanningViewModel.ViewState,
    onAction: (BarcodeScanningViewModel.Action) -> Unit
) {
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
        val analyzer = BarcodeAnalyzer(
            onDetected = { scanResult: String, _: Int ->
                cameraProviderFuture?.get()?.unbindAll()
                onAction(BarcodeScanningViewModel.Action.ScanSuccess(scanResult))
            },
            onNotDetected = { message ->
                Log.e("BarcodeScanningScreen", "onNotDetected: $message")
                onAction(BarcodeScanningViewModel.Action.ScanFailed(message ?: "Unknown error"))
            },
        )

        imageAnalysis.setAnalyzer(
            ContextCompat.getMainExecutor(context),
            analyzer
        )
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
        DisposeCameraAfterFinish {
            cameraProviderFuture?.get()?.unbindAll()
        }
    }
    handleEvent(viewState.consumableEvent, LocalContext.current)
}

private fun handleEvent(
    consumableEvent: ConsumableEvent<BarcodeScanningViewModel.Event>,
    context: Context
) {
    consumableEvent.handleEvent { event ->
        when (event) {
            is BarcodeScanningViewModel.Event.ScanFailed -> {
                Toast.makeText(
                    context,
                    "Failed to scan barcode",
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