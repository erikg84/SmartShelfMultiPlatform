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
import org.dallas.smartshelf.util.ConsumableEvent
import org.dallas.smartshelf.util.handleEvent
import org.dallas.smartshelf.view.component.dialog.ErrorDialogComponent
import org.dallas.smartshelf.view.component.LoadingComponent
import org.dallas.smartshelf.view.component.Spacer16
import org.dallas.smartshelf.view.component.showToast
import org.dallas.smartshelf.viewmodel.DeleteAccountViewModel

@Composable
fun DeleteAccountScreen(
    viewState: DeleteAccountViewModel.ViewState,
    onAction: (action: DeleteAccountViewModel.Action) -> Unit
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

        Text(
            text = "Are you sure you want to delete your account?",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer16()

        Button(
            colors = ButtonDefaults.buttonColors().copy(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ),
            onClick = { onAction(DeleteAccountViewModel.Action.DeleteAccount) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Delete Account")
        }

        Spacer16()

        Button(
            colors = ButtonDefaults.buttonColors().copy(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ),
            modifier = Modifier.fillMaxWidth(),
            onClick = { onAction(DeleteAccountViewModel.Action.CancelDeleteAccount) },
        ) {
            Text(text = "Cancel")
        }
    }

    if (viewState.isLoading) LoadingComponent()
    if (viewState.showErrorDialog) ErrorDialogComponent()

    handleEvent(viewState.consumableEvent)
}

private fun handleEvent(
    consumableEvent: ConsumableEvent<DeleteAccountViewModel.Event>,
) {
    consumableEvent.handleEvent { event ->
        when (event) {
            is DeleteAccountViewModel.Event.AccountDeleted -> {
                showToast("Account deleted")
            }
            is DeleteAccountViewModel.Event.AccountDeletionFailed -> Unit
        }
    }
}
