package org.dallas.smartshelf.viewmodel

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.navigator.Navigator
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.dallas.smartshelf.view.screen.model.LoginModelScreen

class SplashViewModel(
    private val navigator: Navigator
) : ScreenModel {

    fun onAction(action: Action) {
        when (action) {
            is Action.NavigateToLogin -> navigateToLogin()
        }
    }

    private fun navigateToLogin() {
        screenModelScope.launch {
            delay(2000)
            navigator.replaceAll(LoginModelScreen)
        }
    }

    sealed interface Action {
        data object NavigateToLogin : Action
    }
}