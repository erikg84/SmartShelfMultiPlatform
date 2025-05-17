package org.dallas.smartshelf.util

expect object BarcodeScannerFactory {
    fun createBarcodeScanner(): BarcodeScanner
}