package org.dallas.smartshelf

import androidx.compose.ui.window.ComposeUIViewController
import org.dallas.smartshelf.di.initKoin

fun MainViewController() = ComposeUIViewController {
    initKoin()
    App()
}
