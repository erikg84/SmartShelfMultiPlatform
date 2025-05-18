package org.dallas.smartshelf.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

interface OCRScanner {
    fun startScanning()
    fun stopScanning()
}

@Composable
expect fun rememberOCRScanner(
    onTextDetected: (String) -> Unit,
    onError: (String?) -> Unit
): OCRScanner

@Composable
expect fun CameraPreview(
    modifier: Modifier,
    ocrScanner: OCRScanner,
    isScanning: Boolean
)