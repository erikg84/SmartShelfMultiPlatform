package org.dallas.smartshelf.util

import android.content.Context
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner

@Composable
actual fun rememberOCRScanner(
    onTextDetected: (String) -> Unit,
    onError: (String?) -> Unit
): OCRScanner {
    val context = LocalContext.current
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current

    return remember {
        AndroidOCRScanner(context, lifecycleOwner, onTextDetected, onError)
    }
}

@Composable
actual fun CameraPreview(
    modifier: Modifier,
    ocrScanner: OCRScanner,
    isScanning: Boolean
) {
    val context = LocalContext.current

    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }

    AndroidView(
        modifier = modifier,
        factory = { ctx ->
            val previewView = PreviewView(ctx)
            val preview = Preview.Builder().build()
            val selector = CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build()

            preview.setSurfaceProvider(previewView.surfaceProvider)

            try {
                cameraProviderFuture.get().unbindAll()
                if (ocrScanner is AndroidOCRScanner) {
                    ocrScanner.setupCamera(preview, selector, previewView)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            previewView
        }
    )

    DisposableEffect(Unit) {
        onDispose {
            try {
                cameraProviderFuture.get().unbindAll()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}

class AndroidOCRScanner(
    private val context: Context,
    private val lifecycleOwner: LifecycleOwner,
    private val onTextDetected: (String) -> Unit,
    private val onError: (String?) -> Unit
) : OCRScanner {
    private var cameraProvider: ProcessCameraProvider? = null
    private var imageAnalysis: ImageAnalysis? = null
    private var preview: Preview? = null
    private var selector: CameraSelector? = null
    private var previewView: PreviewView? = null

    fun setupCamera(preview: Preview, selector: CameraSelector, previewView: PreviewView) {
        this.preview = preview
        this.selector = selector
        this.previewView = previewView

        try {
            cameraProvider = ProcessCameraProvider.getInstance(context).get()
        } catch (e: Exception) {
            onError(e.message)
        }
    }

    override fun startScanning() {
        if (cameraProvider == null || selector == null || preview == null) {
            onError("Camera not initialized")
            return
        }

        try {
            // Create the image analyzer
            imageAnalysis = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()

            // Create and set the OCR analyzer (this would be your existing OCRAnalyzer)
            val analyzer = AndroidOCRAnalyzer(onTextDetected, onError)
            imageAnalysis?.setAnalyzer(
                ContextCompat.getMainExecutor(context),
                analyzer
            )

            // Bind use cases to camera
            cameraProvider?.unbindAll()
            cameraProvider?.bindToLifecycle(
                lifecycleOwner,
                selector!!,
                preview!!,
                imageAnalysis!!
            )
        } catch (e: Exception) {
            onError(e.message)
        }
    }

    override fun stopScanning() {
        try {
            imageAnalysis?.clearAnalyzer()
            // Make sure camera is still bound to lifecycle
            if (preview != null && selector != null && cameraProvider != null) {
                cameraProvider?.unbindAll()
                cameraProvider?.bindToLifecycle(
                    lifecycleOwner,
                    selector!!,
                    preview!!
                )
            }
        } catch (e: Exception) {
            onError(e.message)
        }
    }
}
