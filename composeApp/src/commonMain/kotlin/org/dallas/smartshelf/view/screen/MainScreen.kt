package org.dallas.smartshelf.view.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.DrawerValue
import androidx.compose.material.ModalDrawer
import androidx.compose.material.rememberDrawerState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.tab.CurrentTab
import cafe.adriel.voyager.navigator.tab.TabNavigator
import kotlinx.coroutines.launch
import org.dallas.smartshelf.view.AppDrawerContent
import org.dallas.smartshelf.view.AppTopBar
import org.dallas.smartshelf.view.TabNavigationBar
import org.dallas.smartshelf.view.tab.CalendarTab
import org.dallas.smartshelf.view.tab.HistoryTab
import org.dallas.smartshelf.view.tab.HomeTab
import org.dallas.smartshelf.view.tab.InventoryTab
import org.dallas.smartshelf.view.tab.SettingsTab

object MainScreen : Screen {

    @Composable
    override fun Content() {
        val rootNavigator = LocalNavigator.currentOrThrow
        val drawerState = rememberDrawerState(DrawerValue.Closed)
        val coroutineScope = rememberCoroutineScope()

        TabNavigator(HomeTab) { tabNavigator ->
            val currentTab = tabNavigator.current

            ModalDrawer(
                drawerState = drawerState,
                drawerContent = {
                    AppDrawerContent(
                        tabNavigator = tabNavigator,
                        rootNavigator = rootNavigator,
                        closeDrawer = { coroutineScope.launch { drawerState.close() } }
                    )
                }
            ) {
                Scaffold(
                    topBar = {
                        AppTopBar(
                            title = currentTab.options.title,
                            onMenuClick = { coroutineScope.launch { drawerState.open() } }
                        )
                    },
                    bottomBar = {
                        TabNavigationBar(tabNavigator, tabItems)
                    }
                ) { paddingValues ->
                    Box(modifier = Modifier.padding(paddingValues)) {
                        CurrentTab()
                    }
                }
            }
        }
    }
}

val tabItems = listOf(HomeTab, InventoryTab, HistoryTab, CalendarTab, SettingsTab)