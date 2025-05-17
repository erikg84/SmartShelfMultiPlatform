package org.dallas.smartshelf.util

class BarcodeScannerManager {
    private val scanner = BarcodeScannerFactory.createBarcodeScanner()

    fun startScanning(
        onBarcodeDetected: (barcode: String) -> Unit,
        onError: (error: String?) -> Unit
    ) {
        scanner.startScanning(
            onDetected = { result, _ -> onBarcodeDetected(result) },
            onNotDetected = onError
        )
    }

    fun stopScanning() {
        scanner.stopScanning()
    }
}