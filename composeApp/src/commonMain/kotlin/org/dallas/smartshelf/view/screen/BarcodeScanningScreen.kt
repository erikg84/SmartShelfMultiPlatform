package org.dallas.smartshelf.view.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.aakira.napier.Napier
import org.dallas.smartshelf.util.BarcodeScannerManager
import org.dallas.smartshelf.util.CameraPreview
import org.dallas.smartshelf.util.ConsumableEvent
import org.dallas.smartshelf.util.handleEvent
import org.dallas.smartshelf.util.rememberOCRScanner
import org.dallas.smartshelf.view.component.showToast
import org.dallas.smartshelf.viewmodel.BarcodeScanningViewModel

@Composable
fun BarcodeScanningScreen(
    viewState: BarcodeScanningViewModel.ViewState,
    onAction: (BarcodeScanningViewModel.Action) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Create OCR scanner for camera preview
        val ocrScanner = rememberOCRScanner(
            onTextDetected = { /* Not used in barcode screen */ },
            onError = { /* Not used in barcode screen */ }
        )

        // Use camera preview from OCR
        CameraPreview(
            modifier = Modifier.fillMaxSize(),
            ocrScanner = ocrScanner,
            isScanning = true // Always show camera preview
        )

        // Create barcode scanner manager
        val barcodeScannerManager = remember { BarcodeScannerManager() }

        // Start barcode scanning and handle lifecycle
        DisposableEffect(Unit) {
            barcodeScannerManager.startScanning(
                onBarcodeDetected = { barcode ->
                    Napier.d("Barcode detected: $barcode")
                    onAction(BarcodeScanningViewModel.Action.ScanSuccess(barcode))
                },
                onError = { message ->
                    Napier.e("Barcode scanning error: $message")
                    onAction(BarcodeScanningViewModel.Action.ScanFailed(message ?: "Unknown error"))
                }
            )

            onDispose {
                barcodeScannerManager.stopScanning()
            }
        }

        // Helper text
        Text(
            text = "Point camera at barcode",
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 48.dp)
        )

        // Cancel button
        Button(
            onClick = {
                barcodeScannerManager.stopScanning()
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp)
        ) {
            Text("Cancel")
        }
    }

    // Handle events
    handleEvent(viewState.consumableEvent)
}

private fun handleEvent(
    consumableEvent: ConsumableEvent<BarcodeScanningViewModel.Event>
) {
    consumableEvent.handleEvent { event ->
        when (event) {
            is BarcodeScanningViewModel.Event.ScanFailed -> {
                showToast("Failed to scan barcode")
            }
        }
    }
}