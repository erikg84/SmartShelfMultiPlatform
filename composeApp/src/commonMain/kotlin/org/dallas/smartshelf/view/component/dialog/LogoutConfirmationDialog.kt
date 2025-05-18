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
fun LogoutConfirmationDialog(
    onConfirm: () -> Unit,
    onCancel: () -> Unit
) {
    SmartShelfTheme {
        AlertDialog(
            containerColor = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(MaterialTheme.dimens.dp8),
            onDismissRequest = {  },
            title = { Text(text = "Confirm Exit") },
            text = { Text(text = "Are you sure you want to exit?") },
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
