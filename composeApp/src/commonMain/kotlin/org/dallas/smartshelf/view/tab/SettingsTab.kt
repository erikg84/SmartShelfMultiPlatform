package org.dallas.smartshelf.view.tab

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import org.dallas.smartshelf.util.AppIcons
import org.dallas.smartshelf.view.screen.model.SettingsModelScreen

object SettingsTab : Tab {
    override val options: TabOptions
        @Composable
        get() {
            val title = "Settings"
            return TabOptions(
                index = 4u,
                title = title,
                icon = AppIcons.Settings
            )
        }

    @Composable
    override fun Content() {
        SettingsModelScreen.Content()
    }
}