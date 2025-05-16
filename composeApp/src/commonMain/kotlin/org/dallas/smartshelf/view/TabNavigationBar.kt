package org.dallas.smartshelf.view

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabNavigator

@Composable
fun TabNavigationBar(
    tabNavigator: TabNavigator,
    tabItems: List<Tab>
) {
    NavigationBar {
        tabItems.forEach { tab ->
            NavigationBarItem(
                selected = tabNavigator.current == tab,
                onClick = { tabNavigator.current = tab },
                icon = {
                    tab.options.icon?.let {
                        Icon(it, contentDescription = tab.options.title)
                    }
                },
                label = {
                    Text(tab.options.title)
                }
            )
        }
    }
}