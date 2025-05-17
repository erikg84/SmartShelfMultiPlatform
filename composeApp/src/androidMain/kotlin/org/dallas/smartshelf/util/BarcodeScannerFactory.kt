package org.dallas.smartshelf.util

actual object BarcodeScannerFactory {
    actual fun createBarcodeScanner(): BarcodeScanner = AndroidBarcodeScanner()
}