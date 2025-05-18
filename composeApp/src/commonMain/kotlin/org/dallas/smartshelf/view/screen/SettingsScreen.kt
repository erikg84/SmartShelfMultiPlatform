package org.dallas.smartshelf.view.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.dallas.smartshelf.theme.dimens
import org.dallas.smartshelf.viewmodel.SettingsViewModel

@Composable
fun SettingsScreen(
    viewState: SettingsViewModel.ViewState,
    onAction: (action: SettingsViewModel.Action) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(MaterialTheme.dimens.dp16),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top
    ) {
        SettingsItem(
            title = "Notifications",
            onClick = { onAction(SettingsViewModel.Action.ManageNotifications) }
        )

        HorizontalDivider(modifier = Modifier.padding(vertical = MaterialTheme.dimens.dp8))

        ThemeToggleItem(
            isDarkTheme = viewState.isDarkTheme,
            onToggleTheme = { onAction(SettingsViewModel.Action.ToggleTheme) }
        )

        HorizontalDivider(modifier = Modifier.padding(vertical = MaterialTheme.dimens.dp8))

        LanguageToggleItem(
            isLanguageEnglish = viewState.isLanguageEnglish,
            onToggleLanguage = { onAction(SettingsViewModel.Action.ToggleLanguage) }
        )

        HorizontalDivider(modifier = Modifier.padding(vertical = MaterialTheme.dimens.dp8))

        SettingsItem(
            title = "Privacy Policy",
            onClick = { onAction(SettingsViewModel.Action.ShowPrivacyPolicy) }
        )

        HorizontalDivider(modifier = Modifier.padding(vertical = MaterialTheme.dimens.dp8))

        SettingsItem(
            title = "Delete Account",
            onClick = { onAction(SettingsViewModel.Action.DeleteAccount) }
        )
    }
}

@Composable
private fun SettingsItem(
    title: String,
    onClick: () -> Unit
) {
    Text(
        text = title,
        style = MaterialTheme.typography.bodyLarge,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(PaddingValues(vertical = MaterialTheme.dimens.dp16)),
        color = MaterialTheme.colorScheme.onPrimaryContainer
    )
}

@Composable
private fun ThemeToggleItem(
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onToggleTheme)
            .padding(PaddingValues(vertical = MaterialTheme.dimens.dp16)),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "Dark Theme",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
        Switch(
            checked = isDarkTheme,
            onCheckedChange = { onToggleTheme() },
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.onTertiary
            )
        )
    }
}

@Composable
private fun LanguageToggleItem(
    isLanguageEnglish: Boolean,
    onToggleLanguage: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onToggleLanguage)
            .padding(PaddingValues(vertical = MaterialTheme.dimens.dp16)),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = if (isLanguageEnglish) "Language: English" else "Language: Spanish",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
        Switch(
            checked = isLanguageEnglish,
            onCheckedChange = { onToggleLanguage() },
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.onTertiary
            )
        )
    }
}
