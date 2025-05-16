package org.dallas.smartshelf.view.tab

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import org.dallas.smartshelf.util.AppIcons
import org.dallas.smartshelf.view.screen.HomeScreen

object HomeTab : Tab {
    override val options: TabOptions
        @Composable
        get() {
            val title = "Home"
            return  TabOptions(
                index = 0u,
                title = title,
                icon = AppIcons.Home
            )
        }

    @Composable
    override fun Content() {
        HomeScreen.Content()
    }
}