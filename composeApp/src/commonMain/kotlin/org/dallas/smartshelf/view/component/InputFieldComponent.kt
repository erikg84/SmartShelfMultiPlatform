package org.dallas.smartshelf.view.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import org.jetbrains.compose.resources.painterResource
import smartshelf.composeapp.generated.resources.Res
import smartshelf.composeapp.generated.resources.visibility_icon
import smartshelf.composeapp.generated.resources.visibility_off_icon

@Composable
fun InputFieldComponent(
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    isPassword: Boolean = false,
    imeAction: ImeAction = ImeAction.Done,
    onImeAction: () -> Unit = {},
    initialValue: String = "",
    maskEmail: Boolean = false
) {
    val text = remember { mutableStateOf(initialValue) }
    val isPasswordVisible = remember { mutableStateOf(false) }
    val displayValue = if (maskEmail) maskEmailAddress(text.value) else text.value

    OutlinedTextField(
        value = displayValue,
        onValueChange = {
            text.value = it
            onValueChange(it)
        },
        label = {
            Text(label)
        },
        modifier = modifier.fillMaxWidth(),
        visualTransformation = if (isPassword && !isPasswordVisible.value) PasswordVisualTransformation() else VisualTransformation.None,
        trailingIcon = {
            if (isPassword) {
                val image =
                    if (isPasswordVisible.value) Res.drawable.visibility_icon else Res.drawable.visibility_off_icon
                val description = if (isPasswordVisible.value) "Hide password" else "Show password"
                IconButton(onClick = { isPasswordVisible.value = !isPasswordVisible.value }) {
                    Icon(painter = painterResource(image), contentDescription = description)
                }
            }
        },
        colors = TextFieldDefaults.outlinedTextFieldColors(
            textColor = MaterialTheme.colorScheme.onBackground,
            focusedBorderColor = MaterialTheme.colorScheme.primaryContainer,
            focusedLabelColor = MaterialTheme.colorScheme.primaryContainer
        ),
        keyboardOptions = KeyboardOptions.Default.copy(imeAction = imeAction),
        keyboardActions = KeyboardActions(onAny = { onImeAction() })
    )
}

private fun maskEmailAddress(email: String): String {
    if (email.isEmpty()) return email
    val atIndex = email.indexOf("@")
    return if (atIndex != -1) {
        email.replaceRange(atIndex until email.length, "****")
    } else email
}
