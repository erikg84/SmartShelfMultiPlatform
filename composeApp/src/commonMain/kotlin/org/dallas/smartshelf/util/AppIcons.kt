package org.dallas.smartshelf.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import org.jetbrains.compose.resources.painterResource
import smartshelf.composeapp.generated.resources.Res
import smartshelf.composeapp.generated.resources.add_icon
import smartshelf.composeapp.generated.resources.back_icon
import smartshelf.composeapp.generated.resources.calendar_icon
import smartshelf.composeapp.generated.resources.delete_icon
import smartshelf.composeapp.generated.resources.edit_icon
import smartshelf.composeapp.generated.resources.forward_icon
import smartshelf.composeapp.generated.resources.history_icon
import smartshelf.composeapp.generated.resources.home_icon
import smartshelf.composeapp.generated.resources.inventory_icon
import smartshelf.composeapp.generated.resources.logout_icon
import smartshelf.composeapp.generated.resources.menu_icon
import smartshelf.composeapp.generated.resources.notification_icon
import smartshelf.composeapp.generated.resources.profile_icon
import smartshelf.composeapp.generated.resources.settings_icon

object AppIcons {
    val Menu: Painter
        @Composable
        get() = painterResource(Res.drawable.menu_icon)

    val Home: Painter
        @Composable
        get() = painterResource(Res.drawable.home_icon)

    val Inventory: Painter
        @Composable
        get() = painterResource(Res.drawable.inventory_icon)

    val History: Painter
        @Composable
        get() = painterResource(Res.drawable.history_icon)

    val Calendar: Painter
        @Composable
        get() = painterResource(Res.drawable.calendar_icon)

    val Settings: Painter
        @Composable
        get() = painterResource(Res.drawable.settings_icon)

    val Back: Painter
        @Composable
        get() = painterResource(Res.drawable.back_icon)

    val Forward: Painter
        @Composable
        get() = painterResource(Res.drawable.forward_icon)

    val Add: Painter
        @Composable
        get() = painterResource(Res.drawable.add_icon)

    val Delete: Painter
        @Composable
        get() = painterResource(Res.drawable.delete_icon)

    val Edit: Painter
        @Composable
        get() = painterResource(Res.drawable.edit_icon)

    val Search: Painter
        @Composable
        get() = painterResource(Res.drawable.settings_icon)

    val Logout: Painter
        @Composable
        get() = painterResource(Res.drawable.logout_icon)

    val Person: Painter
        @Composable
        get() = painterResource(Res.drawable.profile_icon)

    val Notifications: Painter
        @Composable
        get() = painterResource(Res.drawable.notification_icon)
}