package org.dallas.smartshelf.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.interop.UIKitView
import io.github.aakira.napier.Napier
import kotlinx.cinterop.ExperimentalForeignApi
import platform.AVFoundation.*
import platform.CoreGraphics.CGRect
import platform.UIKit.UIView
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_global_queue
import platform.darwin.DISPATCH_QUEUE_PRIORITY_DEFAULT

@Composable
actual fun rememberOCRScanner(
    onTextDetected: (String) -> Unit,
    onError: (String?) -> Unit
): OCRScanner {
    return remember {
        IOSOCRScanner(onTextDetected, onError)
    }
}

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun CameraPreview(
    modifier: Modifier,
    ocrScanner: OCRScanner,
    isScanning: Boolean
) {
    val scanner = ocrScanner as? IOSOCRScanner ?: return

    DisposableEffect(Unit) {
        scanner.setupSession()

        onDispose {
            scanner.tearDown()
        }
    }

    UIKitView(
        modifier = modifier,
        factory = {
            val view = UIView()
            scanner.setupPreview(view)
            view
        },
        update = {
            // Update logic if needed
            if (isScanning) {
                scanner.startRunningCaptureSession()
            } else {
                scanner.stopRunningCaptureSession()
            }
        },
        onResize = { view, rect ->
            // Update preview layer frame when size changes
            scanner.updatePreviewLayerFrame(rect)
        }
    )
}

class IOSOCRScanner(
    private val onTextDetected: (String) -> Unit,
    private val onError: (String?) -> Unit
) : OCRScanner {
    private var captureSession: AVCaptureSession? = null
    private var previewLayer: AVCaptureVideoPreviewLayer? = null
    private var videoOutput: AVCaptureVideoDataOutput? = null

    @OptIn(ExperimentalForeignApi::class)
    fun setupSession() {
        captureSession = AVCaptureSession()
        captureSession?.beginConfiguration()

        // Set up capture device
        val discoverySession = AVCaptureDevice.defaultDeviceWithMediaType(AVMediaTypeVideo)
        discoverySession?.let { device ->
            try {
                // Create device input with proper null handling
                val input = AVCaptureDeviceInput.deviceInputWithDevice(device, null)
                if (input != null && captureSession?.canAddInput(input) == true) {
                    captureSession?.addInput(input)
                } else {
                    Napier.e("Could not create device input or add it to session")
                    captureSession?.commitConfiguration()
                    return
                }

                // Set up video output
                videoOutput = AVCaptureVideoDataOutput()
                if (videoOutput != null && captureSession?.canAddOutput(videoOutput!!) == true) {
                    captureSession?.addOutput(videoOutput!!)

                    // Configure output and add delegate
                    // This part is complex and would need a proper implementation with
                    // a delegate to process frames for OCR
                }

                captureSession?.commitConfiguration()
            } catch (e: Exception) {
                onError(e.toString())
            }
        }
    }

    @OptIn(ExperimentalForeignApi::class)
    fun setupPreview(view: UIView) {
        captureSession?.let { session ->
            previewLayer = AVCaptureVideoPreviewLayer(session = session)
            previewLayer?.videoGravity = AVLayerVideoGravityResizeAspectFill
            view.layer.addSublayer(previewLayer!!)
            previewLayer?.frame = view.bounds
        }
    }

    @OptIn(ExperimentalForeignApi::class)
    fun updatePreviewLayerFrame(rect: kotlinx.cinterop.CValue<CGRect>) {
        previewLayer?.frame = rect
    }

    fun startRunningCaptureSession() {
        // Start capture session in a background thread to avoid UI freezing
        dispatch_async(dispatch_get_global_queue(
            DISPATCH_QUEUE_PRIORITY_DEFAULT.toLong(), 0UL
        )) {
            if (captureSession?.isRunning() != true) {
                captureSession?.startRunning()
            }
        }
    }

    fun stopRunningCaptureSession() {
        dispatch_async(dispatch_get_global_queue(
            DISPATCH_QUEUE_PRIORITY_DEFAULT.toLong(), 0UL
        )) {
            if (captureSession?.isRunning() == true) {
                captureSession?.stopRunning()
            }
        }
    }

    fun tearDown() {
        if (captureSession?.isRunning() == true) {
            captureSession?.stopRunning()
        }
        captureSession = null
        previewLayer = null
        videoOutput = null
    }

    override fun startScanning() {
        startRunningCaptureSession()
    }

    override fun stopScanning() {
        stopRunningCaptureSession()
    }
}