package org.dallas.smartshelf.view.component.dialog

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import org.dallas.smartshelf.theme.SmartShelfTheme
import org.dallas.smartshelf.theme.dimens

@Composable
fun CameraPermissionDeniedDialog(
    onConfirm: () -> Unit,
    onCancel: () -> Unit
) {
    SmartShelfTheme {
        AlertDialog(
            containerColor = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(MaterialTheme.dimens.dp8),
            onDismissRequest = { /* no-op */ },
            title = { Text(text = "Limited Functionality") },
            text = {
                Text(
                    text = "Without granting camera permissions, some features like receipt scanning will not be available. You can continue, but functionality may be limited."
                )
            },
            confirmButton = {
                Button(
                    shape = RoundedCornerShape(MaterialTheme.dimens.dp8),
                    onClick = { onConfirm() }) {
                    Text(text = "OK")
                }
            },
            dismissButton = {
                Button(
                    shape = RoundedCornerShape(MaterialTheme.dimens.dp8),
                    onClick = { onCancel() }) {
                    Text(text = "Cancel")
                }
            }
        )
    }
}

