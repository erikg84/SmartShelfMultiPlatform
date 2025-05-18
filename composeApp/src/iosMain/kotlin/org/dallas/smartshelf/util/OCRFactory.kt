package org.dallas.smartshelf.util

import io.github.aakira.napier.Napier
import kotlinx.cinterop.*
import platform.Foundation.*
import platform.darwin.*
import platform.CoreMedia.CMSampleBufferGetImageBuffer
import platform.CoreMedia.CMSampleBufferRef
import platform.Vision.*
import platform.AVFoundation.*
import platform.CoreVideo.CVPixelBufferRef

actual object OCRFactory {
    actual fun createOCRAnalyzer(
        onDetected: (String) -> Unit,
        onNotDetected: (String?) -> Unit
    ): OCRProcessor = IOSOCRAnalyzer(onDetected, onNotDetected)
}

class IOSOCRAnalyzer(
    private val onDetected: (String) -> Unit,
    private val onNotDetected: (String?) -> Unit
) : OCRProcessor {
    private var captureSession: AVCaptureSession? = null
    private var captureDevice: AVCaptureDevice? = null
    private var textRecognitionRequest: VNRecognizeTextRequest? = null
    private var isProcessing = false

    init {
        setupVisionRequest()
    }

    private fun setupVisionRequest() {
        textRecognitionRequest = VNRecognizeTextRequest { request, error ->
            if (error != null) {
                Napier.e("Vision request error: ${error.localizedDescription}")
                onNotDetected(error.localizedDescription)
                return@VNRecognizeTextRequest
            }

            val observations = request?.results as? List<VNRecognizedTextObservation>
            if (observations.isNullOrEmpty()) {
                onNotDetected("No text found")
                return@VNRecognizeTextRequest
            }

            // Process observations
            val detectedText = StringBuilder()
            observations.forEach { observation ->
                // VNRecognizedTextObservation.topCandidates returns VNRecognizedText objects
                val candidate = observation.topCandidates(1u).firstOrNull()
                val text = candidate as? String
                if (!text.isNullOrEmpty()) {
                    detectedText.append(text).append("\n")
                }
            }

            val result = detectedText.toString().trim()
            if (result.isNotEmpty()) {
                onDetected(result)
            } else {
                onNotDetected("No readable text detected")
            }
        }

        // Configure the recognition level
        textRecognitionRequest?.recognitionLevel = VNRequestTextRecognitionLevelAccurate
        textRecognitionRequest?.usesLanguageCorrection = true
    }

    override fun processImage(
        onTextDetected: (String) -> Unit,
        onTextNotDetected: (String?) -> Unit
    ) {
        isProcessing = true
        Napier.d("iOS OCR processing started")
    }

    override fun stopProcessing() {
        isProcessing = false
        Napier.d("iOS OCR processing stopped")
        stopCameraSession()
    }

    @OptIn(ExperimentalForeignApi::class)
    fun processImageBuffer(imageBuffer: CVPixelBufferRef) {
        if (!isProcessing) return

        // Create request handler options
        val options = mapOf<Any?, Any>()

        // Use the CVPixelBuffer constructor directly
        val requestHandler = VNImageRequestHandler(
            imageBuffer,
            0u, // orientation parameter as UInt (0 = Up)
            options
        )

        // Perform the request
        try {
            textRecognitionRequest?.let { request ->
                // Proper way to perform Vision requests
                requestHandler.performRequests(listOf(request), null)
            }
        } catch (e: Exception) {
            Napier.e("Error processing image: $e")
            onNotDetected("Error processing image: ${e.message}")
        }
    }

    // Methods to control the camera
    @OptIn(ExperimentalForeignApi::class)
    fun setupCameraSession() {
        captureSession = AVCaptureSession()
        captureSession?.sessionPreset = AVCaptureSessionPresetHigh

        // Get the back camera using the proper API
        val discoverySession = AVCaptureDeviceDiscoverySession.discoverySessionWithDeviceTypes(
            listOf(AVCaptureDeviceTypeBuiltInWideAngleCamera),
            AVMediaTypeVideo,
            AVCaptureDevicePositionBack
        )

        captureDevice = discoverySession.devices?.firstOrNull() as? AVCaptureDevice

        if (captureDevice == null) {
            Napier.e("No camera device found")
            onNotDetected("Camera not available")
            return
        }

        // Configure device input
        var deviceInput: AVCaptureDeviceInput? = null
        try {
            deviceInput = AVCaptureDeviceInput.deviceInputWithDevice(captureDevice!!, null)
        } catch (e: Exception) {
            Napier.e("Exception creating device input: $e")
            onNotDetected("Error setting up camera: ${e.message}")
            return
        }

        if (deviceInput == null) {
            Napier.e("Failed to create device input (null return)")
            onNotDetected("Failed to create camera input")
            return
        }

        // Configure output
        val videoDataOutput = AVCaptureVideoDataOutput()

        // Create a dispatch queue for sample buffer delegate
        val outputQueue = dispatch_queue_create("OutputQueue", null)

        // Set sample buffer delegate using platform-specific API
        videoDataOutput.setSampleBufferDelegate(
            object : NSObject(), AVCaptureVideoDataOutputSampleBufferDelegateProtocol {
                override fun captureOutput(
                    output: AVCaptureOutput,
                    didDropSampleBuffer: CMSampleBufferRef?,
                    fromConnection: AVCaptureConnection
                ) {
                    val imageBuffer = CMSampleBufferGetImageBuffer(didDropSampleBuffer)
                    if (imageBuffer != null) {
                        processImageBuffer(imageBuffer)
                    }
                }
            },
            outputQueue
        )

        // Configure capture session
        captureSession?.beginConfiguration()

        if (deviceInput != null && captureSession?.canAddInput(deviceInput) == true) {
            captureSession?.addInput(deviceInput)
        } else {
            Napier.e("Could not add device input to session")
            onNotDetected("Error setting up camera input")
            captureSession?.commitConfiguration()
            return
        }

        if (captureSession?.canAddOutput(videoDataOutput) == true) {
            captureSession?.addOutput(videoDataOutput)
        } else {
            Napier.e("Could not add video output to session")
            onNotDetected("Error setting up camera output")
            captureSession?.commitConfiguration()
            return
        }

        captureSession?.commitConfiguration()
    }

    fun startCameraSession() {
        if (captureSession?.isRunning() != true) {
            captureSession?.startRunning()
        }
    }

    fun stopCameraSession() {
        if (captureSession?.isRunning() == true) {
            captureSession?.stopRunning()
        }
    }
}

// Helper class for NSError exceptions
class NSErrorException(val nsError: NSError) : Exception(nsError.localizedDescription)