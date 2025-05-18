package org.dallas.smartshelf.view.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import org.dallas.smartshelf.theme.dimens
import org.dallas.smartshelf.util.ConsumableEvent
import org.dallas.smartshelf.util.handleEvent
import org.dallas.smartshelf.view.component.AppLogo
import org.dallas.smartshelf.view.component.dialog.ErrorDialogComponent
import org.dallas.smartshelf.view.component.InputFieldComponent
import org.dallas.smartshelf.view.component.LoadingComponent
import org.dallas.smartshelf.view.component.Spacer16
import org.dallas.smartshelf.view.component.Spacer32
import org.dallas.smartshelf.view.component.Spacer8
import org.dallas.smartshelf.view.component.WeightedSpacer
import org.dallas.smartshelf.viewmodel.LoginViewModel

@Composable
fun LoginScreen(
    viewState: LoginViewModel.ViewState,
    onAction: (LoginViewModel.Action) -> Unit
) {
    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .fillMaxSize()
            .padding(MaterialTheme.dimens.dp16),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer32()

        AppLogo()

        WeightedSpacer()

        InputFieldComponent(
            maskEmail = viewState.rememberMe,
            initialValue = viewState.email,
            imeAction = ImeAction.Next,
            onValueChange = {
                onAction(LoginViewModel.Action.UpdateEmail(it))
            },
            label = "Email",
            modifier = Modifier
                .fillMaxWidth()
        )

        Spacer8()

        InputFieldComponent(
            onImeAction = {
                onAction(LoginViewModel.Action.Login)
            },
            onValueChange = {
                onAction(LoginViewModel.Action.UpdatePassword(it))
            },
            label = "Password",
            modifier = Modifier
                .fillMaxWidth(),
            isPassword = true
        )

        Spacer16()

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                enabled = viewState.isFormValid,
                checked = viewState.rememberMe,
                onCheckedChange = { isChecked ->
                    onAction(LoginViewModel.Action.ToggleRememberMe(isChecked))
                }
            )
            Text(text = "Remember Me")
        }

        Spacer16()

        Button(
            enabled = viewState.isFormValid,
            onClick = {
                onAction(LoginViewModel.Action.Login)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Submit")
        }

        TextButton(onClick = {
            onAction(LoginViewModel.Action.ForgotPassword)
        }) {
            Text(text = "Forgot Password?", color = MaterialTheme.colorScheme.primary)
        }

        Spacer8()

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "Don't have an account?", color = MaterialTheme.colorScheme.primary)
            TextButton(onClick = { onAction(LoginViewModel.Action.NavigateToRegister) }) {
                Text(
                    "Sign Up",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }

    if (viewState.isLoading) LoadingComponent()
    if (viewState.showErrorDialog) ErrorDialogComponent()

    handleEvent(consumableEvent = viewState.consumableEvent)
}

private fun handleEvent(consumableEvent: ConsumableEvent<LoginViewModel.Event>) {
    consumableEvent.handleEvent { event ->
        when (event) {
            is LoginViewModel.Event.Error -> Unit
            is LoginViewModel.Event.LoginSuccess -> Unit
        }
    }
}