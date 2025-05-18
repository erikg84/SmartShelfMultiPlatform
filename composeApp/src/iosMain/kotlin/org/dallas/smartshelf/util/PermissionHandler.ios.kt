package org.dallas.smartshelf.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import platform.AVFoundation.AVAuthorizationStatusAuthorized
import platform.AVFoundation.AVCaptureDevice
import platform.AVFoundation.AVMediaTypeVideo
import platform.AVFoundation.authorizationStatusForMediaType
import platform.AVFoundation.requestAccessForMediaType

@Composable
actual fun rememberPermissionHandler(
    onPermissionResult: (Boolean) -> Unit
): PermissionHandler {
    return remember {
        object : PermissionHandler {
            override fun requestCameraPermission() {
                AVCaptureDevice.requestAccessForMediaType(AVMediaTypeVideo) { granted ->
                    onPermissionResult(granted)
                }
            }

            override fun checkCameraPermission(): Boolean {
                return AVCaptureDevice.authorizationStatusForMediaType(AVMediaTypeVideo) ==
                        AVAuthorizationStatusAuthorized
            }
        }
    }
}