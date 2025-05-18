package org.dallas.smartshelf.view.tab

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import org.dallas.smartshelf.util.AppIcons
import org.dallas.smartshelf.view.screen.model.InventoryModelScreen

object InventoryTab : Tab {
    override val options: TabOptions
        @Composable
        get() {
            val title = "Stock"
            return TabOptions(
                index = 1u,
                title = title,
                icon = AppIcons.Inventory
            )
        }

    @Composable
    override fun Content() {
        InventoryModelScreen.Content()
    }
}