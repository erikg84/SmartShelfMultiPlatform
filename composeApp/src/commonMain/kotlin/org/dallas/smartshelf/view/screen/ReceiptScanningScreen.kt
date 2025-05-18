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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.aakira.napier.Napier
import org.dallas.smartshelf.util.CameraPreview
import org.dallas.smartshelf.util.ConsumableEvent
import org.dallas.smartshelf.util.handleEvent
import org.dallas.smartshelf.util.rememberOCRScanner
import org.dallas.smartshelf.view.component.showToast
import org.dallas.smartshelf.viewmodel.ReceiptScanningViewModel

@Composable
fun ReceiptScanningScreen(
    viewState: ReceiptScanningViewModel.ViewState,
    onAction: (ReceiptScanningViewModel.Action) -> Unit
) {
    var isScanning by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Create OCR scanner using rememberOCRScanner
        val ocrScanner = rememberOCRScanner(
            onTextDetected = { detectedText ->
                Napier.d("OCR detected text: $detectedText")
                isScanning = false
                onAction(ReceiptScanningViewModel.Action.OCRSuccess(detectedText))
            },
            onError = { message ->
                Napier.e("OCR error: $message")
                onAction(ReceiptScanningViewModel.Action.OCRFailed(message ?: "Unknown error"))
            }
        )

        // Control OCR scanner based on isScanning state
        DisposableEffect(isScanning) {
            if (isScanning) {
                ocrScanner.startScanning()
            } else {
                ocrScanner.stopScanning()
            }

            onDispose {
                ocrScanner.stopScanning()
            }
        }

        // Camera preview
        CameraPreview(
            modifier = Modifier.fillMaxSize(),
            ocrScanner = ocrScanner,
            isScanning = true // Always show camera preview, but OCR processing is controlled separately
        )

        // Helper text
        Text(
            text = if (isScanning) "Scanning receipt..." else "Press button to scan receipt",
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 48.dp)
        )

        // Start/Stop button
        Button(
            onClick = {
                isScanning = !isScanning
                if (isScanning) {
                    onAction(ReceiptScanningViewModel.Action.StartProcessing)
                }
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp)
        ) {
            Text(if (isScanning) "Stop Scanning" else "Start Scanning")
        }
    }

    // Handle events for showing toast messages
    handleEvent(viewState.consumableEvent)
}

private fun handleEvent(
    consumableEvent: ConsumableEvent<ReceiptScanningViewModel.Event>
) {
    consumableEvent.handleEvent { event ->
        when (event) {
            is ReceiptScanningViewModel.Event.OCRFailed -> {
                showToast("Failed to scan text")
            }
        }
    }
}