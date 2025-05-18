package org.dallas.smartshelf.util

import androidx.compose.runtime.Composable

@Composable
expect fun rememberPermissionHandler(
    onPermissionResult: (Boolean) -> Unit
): PermissionHandler

interface PermissionHandler {
    fun requestCameraPermission()
    fun checkCameraPermission(): Boolean
}