package org.dallas.smartshelf.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.tab.TabNavigator
import org.dallas.smartshelf.view.screen.model.LoginModelScreen
import org.dallas.smartshelf.view.tab.CalendarTab
import org.dallas.smartshelf.view.tab.HistoryTab
import org.dallas.smartshelf.view.tab.InventoryTab
import org.dallas.smartshelf.view.tab.SettingsTab

@Composable
fun AppDrawerContent(
    tabNavigator: TabNavigator,
    rootNavigator: Navigator,
    closeDrawer: () -> Unit
) {
    Column(
        modifier = Modifier
            .width(280.dp)
            .fillMaxHeight()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Text(
            text = "Smart Shelf",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary
        )

        Divider(modifier = Modifier.padding(vertical = 8.dp))

        Spacer(modifier = Modifier.height(16.dp))

        DrawerItem("Stock") {
            tabNavigator.current = InventoryTab
            closeDrawer()
        }

        DrawerItem("History") {
            tabNavigator.current = HistoryTab
            closeDrawer()
        }

        DrawerItem("Calendar") {
            tabNavigator.current = CalendarTab
            closeDrawer()
        }

        DrawerItem("Settings") {
            tabNavigator.current = SettingsTab
            closeDrawer()
        }

        Spacer(modifier = Modifier.weight(1f))

        DrawerItem("Logout") {
            // For logout, we need to use the root navigator
            rootNavigator.replaceAll(LoginModelScreen)
            closeDrawer()
        }
    }
}

@Composable
private fun DrawerItem(
    text: String,
    onClick: () -> Unit
) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onBackground,
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp)
            .fillMaxHeight()
    )
}