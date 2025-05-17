package org.dallas.smartshelf.util

import kotlinx.cinterop.ExperimentalForeignApi
import platform.AVFoundation.*
import platform.darwin.NSObject

class IOSBarcodeScanner : BarcodeScanner {
    private var captureSession: AVCaptureSession? = null
    private var currentCallback: ((String, Int) -> Unit)? = null
    private var errorCallback: ((String?) -> Unit)? = null

    @OptIn(ExperimentalForeignApi::class)
    override fun startScanning(
        onDetected: (scanResult: String, format: Int) -> Unit,
        onNotDetected: (String?) -> Unit
    ) {
        currentCallback = onDetected
        errorCallback = onNotDetected

        captureSession = AVCaptureSession()

        val captureDevice = AVCaptureDevice.defaultDeviceWithMediaType(AVMediaTypeVideo)

        try {
            val deviceInput = AVCaptureDeviceInput.deviceInputWithDevice(captureDevice!!, null)
            captureSession?.addInput(deviceInput!!)

            val captureMetadataOutput = AVCaptureMetadataOutput()
            captureSession?.addOutput(captureMetadataOutput)

            captureMetadataOutput.setMetadataObjectsDelegate(BarcodeDelegate(onDetected), null)
            captureMetadataOutput.metadataObjectTypes = listOf(
                AVMetadataObjectTypeQRCode,
                AVMetadataObjectTypeEAN13Code,
                AVMetadataObjectTypeEAN8Code,
                AVMetadataObjectTypeCode128Code,
                AVMetadataObjectTypeCode39Code,
                AVMetadataObjectTypeCode93Code,
                AVMetadataObjectTypePDF417Code,
                AVMetadataObjectTypeAztecCode,
                AVMetadataObjectTypeDataMatrixCode
            )

            captureSession?.startRunning()
        } catch (e: Exception) {
            errorCallback?.invoke(e.message)
        }
    }

    override fun stopScanning() {
        captureSession?.stopRunning()
        captureSession = null
        currentCallback = null
        errorCallback = null
    }

    private class BarcodeDelegate(
        private val onDetected: (scanResult: String, format: Int) -> Unit
    ) : NSObject(), AVCaptureMetadataOutputObjectsDelegateProtocol {

        override fun captureOutput(
            output: AVCaptureOutput,
            didOutputMetadataObjects: List<*>,
            fromConnection: AVCaptureConnection
        ) {
            didOutputMetadataObjects.filterIsInstance<AVMetadataMachineReadableCodeObject>()
                .firstOrNull()?.let { barcodeObject ->
                    barcodeObject.stringValue?.let { barcodeValue ->
                        val format = when (barcodeObject.type) {
                            AVMetadataObjectTypeQRCode -> BarcodeFormats.QR_CODE
                            AVMetadataObjectTypeEAN13Code -> BarcodeFormats.EAN_13
                            AVMetadataObjectTypeEAN8Code -> BarcodeFormats.EAN_8
                            AVMetadataObjectTypeCode128Code -> BarcodeFormats.CODE_128
                            AVMetadataObjectTypeCode39Code -> BarcodeFormats.CODE_39
                            AVMetadataObjectTypeCode93Code -> BarcodeFormats.CODE_93
                            AVMetadataObjectTypePDF417Code -> BarcodeFormats.PDF417
                            AVMetadataObjectTypeAztecCode -> BarcodeFormats.AZTEC
                            AVMetadataObjectTypeDataMatrixCode -> BarcodeFormats.DATA_MATRIX
                            else -> BarcodeFormats.UNKNOWN
                        }

                        onDetected(barcodeValue, format)
                    }
                }
        }
    }
}