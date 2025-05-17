package org.dallas.smartshelf.util

import android.annotation.SuppressLint
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.Executors

class AndroidBarcodeScanner : BarcodeScanner {
    private var imageAnalyzer: ImageAnalysis? = null
    private var analyzer: BarcodeAnalyzer? = null

    override fun startScanning(
        onDetected: (scanResult: String, format: Int) -> Unit,
        onNotDetected: (String?) -> Unit
    ) {
        analyzer = BarcodeAnalyzer(onDetected, onNotDetected)

        imageAnalyzer = ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()
            .also {
                it.setAnalyzer(
                    Executors.newSingleThreadExecutor(),
                    analyzer!!
                )
            }
    }

    override fun stopScanning() {
        imageAnalyzer?.clearAnalyzer()
        analyzer = null
        imageAnalyzer = null
    }

    private class BarcodeAnalyzer(
        private val onDetected: (scanResult: String, format: Int) -> Unit,
        private val onNotDetected: (String?) -> Unit
    ) : ImageAnalysis.Analyzer {
        @SuppressLint("UnsafeOptInUsageError")
        override fun analyze(imageProxy: ImageProxy) {
            val mediaImage = imageProxy.image
            val rotation = imageProxy.imageInfo.rotationDegrees
            mediaImage ?: return
            val image = InputImage.fromMediaImage(mediaImage, rotation)
            val scanner = BarcodeScanning.getClient()
            scanner.process(image)
                .addOnSuccessListener { barcodes ->
                    for (barcode in barcodes) {
                        val text: String? = barcode.rawValue
                        text?.let {
                            onDetected(
                                it.trimEnd(),
                                barcode.format
                            )
                        }
                    }
                }
                .addOnFailureListener {
                    onNotDetected(it.message)
                }.addOnCompleteListener {
                    imageProxy.close()
                }
        }
    }
}