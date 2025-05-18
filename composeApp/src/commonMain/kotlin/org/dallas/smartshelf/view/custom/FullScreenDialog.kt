package org.dallas.smartshelf.view.custom

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

class FullScreenDialog {
    private val isShowing = mutableStateOf(false)
    private val dialogTitle = mutableStateOf<String?>(null)
    private val cancelable = mutableStateOf(false)
    private var onCancelListener: (() -> Unit)? = null

    fun show(): MutableState<Boolean> {
        return show(null)
    }

    fun show(title: String?): MutableState<Boolean> {
        return show(title, false)
    }

    fun show(title: String?, cancelable: Boolean): MutableState<Boolean> {
        return show(title, cancelable, null)
    }

    fun show(
        title: String?,
        cancelable: Boolean,
        cancelListener: (() -> Unit)?
    ): MutableState<Boolean> {
        dialogTitle.value = title
        this.cancelable.value = cancelable
        onCancelListener = cancelListener
        isShowing.value = true
        return isShowing
    }

    fun dismiss() {
        isShowing.value = false
    }

    @Composable
    fun DialogContent() {
        if (isShowing.value) {
            Dialog(
                onDismissRequest = {
                    if (cancelable.value) {
                        onCancelListener?.invoke()
                        isShowing.value = false
                    }
                },
                properties = DialogProperties(
                    dismissOnBackPress = cancelable.value,
                    dismissOnClickOutside = cancelable.value,
                    usePlatformDefaultWidth = false // for full screen
                )
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0x99000000) // semi-transparent background
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(56.dp),
                                color = MaterialTheme.colors.primary
                            )

                            if (dialogTitle.value != null) {
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = dialogTitle.value ?: "",
                                    style = MaterialTheme.typography.body1,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }

        // Clean up when leaving composition
        DisposableEffect(Unit) {
            onDispose {
                // No need to do anything here since state is handled by the class
            }
        }
    }
}