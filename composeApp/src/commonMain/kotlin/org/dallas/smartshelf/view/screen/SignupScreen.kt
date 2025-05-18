package org.dallas.smartshelf.view.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import org.dallas.smartshelf.theme.dimens
import org.dallas.smartshelf.util.ConsumableEvent
import org.dallas.smartshelf.util.handleEvent
import org.dallas.smartshelf.view.component.dialog.ErrorDialogComponent
import org.dallas.smartshelf.view.component.InputFieldComponent
import org.dallas.smartshelf.view.component.LoadingComponent
import org.dallas.smartshelf.view.component.Spacer16
import org.dallas.smartshelf.view.component.Spacer8
import org.dallas.smartshelf.viewmodel.SignupViewModel

@Composable
fun SignupScreen(
    viewState: SignupViewModel.ViewState,
    onAction: (action: SignupViewModel.Action) -> Unit
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

        InputFieldComponent(onValueChange = {
            onAction(SignupViewModel.Action.UpdateEmail(it))
        }, label = "Email", modifier = Modifier.fillMaxWidth())

        Spacer8()

        InputFieldComponent(onValueChange = {
            onAction(SignupViewModel.Action.UpdatePassword(it))
        }, label = "Password", modifier = Modifier.fillMaxWidth(), isPassword = true)

        Spacer16()

        Button(
            enabled = viewState.isFormValid,
            onClick = {
                onAction(SignupViewModel.Action.Register)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Register")
        }

        Spacer8()

        TextButton(onClick = { onAction(SignupViewModel.Action.NavigateToLogin) }) {
            Text(
                text = "Already have an account? Log in",
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
    if (viewState.isLoading) LoadingComponent()
    if (viewState.showErrorDialog) ErrorDialogComponent()
    handleEvent(viewState.consumableEvent)
}

private fun handleEvent(
    consumableEvent: ConsumableEvent<SignupViewModel.Event>
) {
    consumableEvent.handleEvent { event ->
        when (event) {
            is SignupViewModel.Event.SignupSuccess -> Unit

            is SignupViewModel.Event.Error -> Unit
        }
    }
}
