package org.dallas.smartshelf

import androidx.compose.ui.window.ComposeUIViewController
import org.dallas.smartshelf.di.initKoin
import org.dallas.smartshelf.util.FirebaseInitializer

fun MainViewController() = ComposeUIViewController {
    initializeApp()
    App()
}

private fun initializeApp() {
    initKoin()
    FirebaseInitializer.initialize()
}
