package org.dallas.smartshelf.view.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import org.dallas.smartshelf.theme.dimens
import org.dallas.smartshelf.util.ConsumableEvent
import org.dallas.smartshelf.util.handleEvent
import org.dallas.smartshelf.view.component.Spacer16
import org.dallas.smartshelf.view.component.Spacer32
import org.dallas.smartshelf.view.component.Spacer8
import org.dallas.smartshelf.view.component.WeightedSpacer
import org.dallas.smartshelf.view.component.dialog.LogoutConfirmationDialog
import org.dallas.smartshelf.view.component.showToast
import org.dallas.smartshelf.viewmodel.HomeViewModel
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import smartshelf.composeapp.generated.resources.Res
import smartshelf.composeapp.generated.resources.chevron_right_icon
import smartshelf.composeapp.generated.resources.graphic_eq_icon
import smartshelf.composeapp.generated.resources.text_fields_icon

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun HomeScreen(
    viewState: HomeViewModel.ViewState,
    onAction: (HomeViewModel.Action) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = MaterialTheme.dimens.dp16),
            text = "Welcome to Smart Shelf!",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
        Spacer16()
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = MaterialTheme.dimens.dp16),
            text = "Please choose one of the following options to begin scanning:",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
        Spacer32()
        ScanningOption(
            label = "Scan Barcode",
            icon = Res.drawable.graphic_eq_icon,
            onClick = { onAction(HomeViewModel.Action.NavigateToBarcodeScanner) }
        )
        Spacer8()
        ScanningOption(
            label = "Scan Receipt",
            icon = Res.drawable.text_fields_icon,
            onClick = { onAction(HomeViewModel.Action.NavigateToReceiptScanner) }
        )
    }

    if (viewState.isLogoutPromptVisible) {
        LogoutConfirmationDialog(
            onConfirm = { onAction(HomeViewModel.Action.Logout) },
            onCancel = { onAction(HomeViewModel.Action.HideLogoutPrompt) }
        )
    }

    BackHandler(enabled = true) {
        onAction(HomeViewModel.Action.ShowLogoutPrompt)
    }

    handleEvent(viewState.consumableEvent)
}

@Composable
fun ScanningOption(label: String, icon: DrawableResource, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .clickable(onClick = onClick)
            .fillMaxWidth()
            .padding(horizontal = MaterialTheme.dimens.dp16),
        color = MaterialTheme.colorScheme.primaryContainer,
        shadowElevation = MaterialTheme.dimens.dp8,
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier.padding(MaterialTheme.dimens.dp16),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Icon(
                painter = painterResource(icon),
                contentDescription = label,
                tint = MaterialTheme.colorScheme.background
            )
            Spacer16()
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.background
            )
            WeightedSpacer()
            Icon(
                tint = MaterialTheme.colorScheme.background,
                painter = painterResource(Res.drawable.chevron_right_icon),
                contentDescription = "Navigate to $label",
            )
        }
    }
}

private fun handleEvent(
    consumableEvent: ConsumableEvent<HomeViewModel.Event>,
) {
    consumableEvent.handleEvent { event ->
        when (event) {
            is HomeViewModel.Event.LogoutSuccessful -> {
                showToast("Logged out")
            }
        }
    }
}
