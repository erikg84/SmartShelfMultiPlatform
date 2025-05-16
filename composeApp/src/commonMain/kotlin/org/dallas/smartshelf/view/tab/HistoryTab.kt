package org.dallas.smartshelf.view.tab

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import org.dallas.smartshelf.util.AppIcons
import org.dallas.smartshelf.view.screen.HistoryScreen

object HistoryTab : Tab {
    override val options: TabOptions
        @Composable
        get() {
            val title = "History"
            return TabOptions(
                index = 2u,
                title = title,
                icon = AppIcons.History
            )
        }

    @Composable
    override fun Content() {
        HistoryScreen.Content()
    }
}