package org.dallas.smartshelf.util

interface OCRProcessor {
    fun processImage(
        onTextDetected: (String) -> Unit,
        onTextNotDetected: (String?) -> Unit
    )

    fun stopProcessing()
}

expect object OCRFactory {
    fun createOCRAnalyzer(
        onDetected: (String) -> Unit,
        onNotDetected: (String?) -> Unit
    ): OCRProcessor
}