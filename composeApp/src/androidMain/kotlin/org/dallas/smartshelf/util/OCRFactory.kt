package org.dallas.smartshelf.util

import android.annotation.SuppressLint
import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlin.math.abs

actual object OCRFactory {
    actual fun createOCRAnalyzer(
        onDetected: (String) -> Unit,
        onNotDetected: (String?) -> Unit
    ): OCRProcessor = AndroidOCRAnalyzer(onDetected, onNotDetected)
}

class AndroidOCRAnalyzer(
    private val onDetected: (String) -> Unit,
    private val onNotDetected: (String?) -> Unit
) : OCRProcessor, ImageAnalysis.Analyzer {

    companion object {
        private const val TAG = "OCR"
        private const val Y_THRESHOLD = 8f
    }

    private var isProcessing = false

    override fun processImage(
        onTextDetected: (String) -> Unit,
        onTextNotDetected: (String?) -> Unit
    ) {
        isProcessing = true
        // The actual processing happens in analyze() when called by CameraX
    }

    override fun stopProcessing() {
        isProcessing = false
    }

    @SuppressLint("UnsafeOptInUsageError")
    override fun analyze(imageProxy: ImageProxy) {
        if (!isProcessing) {
            imageProxy.close()
            return
        }

        val mediaImage = imageProxy.image ?: return
        val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

        recognizer.process(image)
            .addOnSuccessListener { visionText ->
                val textElements = visionText.textBlocks.flatMap { block ->
                    block.lines.mapNotNull { line ->
                        line.boundingBox?.let { box ->
                            TextElement(
                                text = line.text.trim(),
                                y = (box.top + box.bottom) / 2f,
                                x = box.left.toFloat()
                            )
                        }
                    }
                }

                val formattedText = StringBuilder()
                val currentLine = mutableListOf<TextElement>()
                var currentY = textElements.firstOrNull()?.y ?: 0f

                textElements.sortedWith(compareBy({ it.y }, { it.x })).forEach { element ->
                    if (currentLine.isNotEmpty() && abs(element.y - currentY) > Y_THRESHOLD) {
                        formattedText.appendLine(currentLine.joinToString(" ") { it.text })
                        currentLine.clear()
                        currentY = element.y
                    }
                    currentLine.add(element)
                }

                if (currentLine.isNotEmpty()) {
                    formattedText.appendLine(currentLine.joinToString(" ") { it.text })
                }

                val result = formattedText.toString().trim()
                if (result.isNotEmpty()) {
                    onDetected(result)
                } else {
                    onNotDetected("No text detected")
                }
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "OCR failed: ${exception.message}")
                onNotDetected(exception.message)
            }
            .addOnCompleteListener {
                imageProxy.close()
            }
    }

    private data class TextElement(
        val text: String,
        val y: Float,
        val x: Float
    )

    fun getAnalyzer(): ImageAnalysis.Analyzer {
        return this
    }
}