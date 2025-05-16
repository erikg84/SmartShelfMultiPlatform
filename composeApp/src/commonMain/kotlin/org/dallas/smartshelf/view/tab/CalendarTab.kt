package org.dallas.smartshelf.view.tab

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import org.dallas.smartshelf.util.AppIcons
import org.dallas.smartshelf.view.screen.CalendarScreen

object CalendarTab : Tab {
    override val options: TabOptions
        @Composable
        get() {
            val title = "Calendar"
            return TabOptions(
                index = 3u,
                title = title,
                icon = AppIcons.Calendar
            )
        }

    @Composable
    override fun Content() {
        CalendarScreen.Content()
    }
}