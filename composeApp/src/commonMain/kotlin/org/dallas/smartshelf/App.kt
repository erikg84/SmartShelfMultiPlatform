package org.dallas.smartshelf

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.SlideTransition
import org.dallas.smartshelf.view.screen.model.SplashModelScreen

@Composable
fun App() {
    MaterialTheme {
        Navigator(SplashModelScreen) { navigator ->
            SlideTransition(navigator)
        }
    }
}