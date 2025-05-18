package org.dallas.smartshelf.view.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.dallas.smartshelf.theme.dimens
import org.dallas.smartshelf.view.component.dialog.ErrorDialogComponent
import org.dallas.smartshelf.view.component.InputFieldComponent
import org.dallas.smartshelf.view.component.LoadingComponent
import org.dallas.smartshelf.view.component.Spacer16
import org.dallas.smartshelf.viewmodel.ForgotPasswordViewModel

@Composable
fun ForgotPasswordScreen(
    viewState: ForgotPasswordViewModel.ViewState,
    onAction: (action: ForgotPasswordViewModel.Action) -> Unit
) {
    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .fillMaxSize()
            .padding(MaterialTheme.dimens.dp16),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Spacer16()

        InputFieldComponent(
            onValueChange = { onAction(ForgotPasswordViewModel.Action.UpdateEmail(it)) },
            label = "Email",
            modifier = Modifier.fillMaxWidth()
        )

        Spacer16()

        Button(
            colors = ButtonDefaults.buttonColors().copy(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ),
            enabled = viewState.isEmailValid,
            onClick = { onAction(ForgotPasswordViewModel.Action.SendPasswordReset) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Send Reset Email")
        }

        Spacer16()

        if (viewState.isResetSuccess) {
            Text(
                text = "Password reset email sent! Check your inbox.",
                color = MaterialTheme.colorScheme.primary
            )
        }
    }

    if (viewState.isLoading) LoadingComponent()
    if (viewState.showErrorDialog) ErrorDialogComponent()
}
