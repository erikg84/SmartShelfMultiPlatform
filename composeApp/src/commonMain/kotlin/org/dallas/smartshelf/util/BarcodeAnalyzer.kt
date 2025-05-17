package org.dallas.smartshelf.util

interface BarcodeScanner {
    fun startScanning(
        onDetected: (scanResult: String, format: Int) -> Unit,
        onNotDetected: (String?) -> Unit
    )

    fun stopScanning()
}

object BarcodeFormats {
    const val UNKNOWN = 0
    const val CODE_128 = 1
    const val CODE_39 = 2
    const val CODE_93 = 3
    const val CODABAR = 4
    const val DATA_MATRIX = 5
    const val EAN_13 = 6
    const val EAN_8 = 7
    const val ITF = 8
    const val QR_CODE = 9
    const val UPC_A = 10
    const val UPC_E = 11
    const val PDF417 = 12
    const val AZTEC = 13
}