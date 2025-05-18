package org.dallas.smartshelf.viewmodel

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.navigator.Navigator
import com.junevrtech.smartshelf.store.ModelStore
import com.junevrtech.smartshelf.store.StatefulStore
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.dallas.smartshelf.util.ConsumableEvent
import org.jetbrains.compose.resources.DrawableResource
import smartshelf.composeapp.generated.resources.Res
import smartshelf.composeapp.generated.resources.back_icon
import smartshelf.composeapp.generated.resources.home_icon

class DashboardViewModel(
    private val navigator: Navigator
) : ScreenModel {

    private val statefulStore: ModelStore<AppState> = StatefulStore(AppState(), screenModelScope)
    val appState: StateFlow<AppState> get() = statefulStore.state

    fun onAction(action: Action) {
        when (action) {
            is Action.UpdateCurrentRoute -> updateCurrentRoute(action.route)
            is Action.NavigateBack -> navigateBack()
            is Action.CloseDrawer -> closeDrawer()
            is Action.OpenDrawer -> openDrawer()
            is Action.NavClick -> navClick()
            is Action.Logout -> logout()
        }
    }

    private fun logout() {
        // Implement logout logic and navigate to login screen
        navigator.popUntilRoot()
        // Replace with your login screen
        // navigator.replaceAll(LoginScreen)
    }

    private fun navClick() {
        if (!statefulStore.state.value.isHomeScreen) {
            navigateBack()
        } else {
            openDrawer()
        }
    }

    private fun openDrawer() {
        statefulStore.process { oldState ->
            oldState.copy(
                isDrawerOpen = true,
                consumableEvent = ConsumableEvent.create(Event.OpenDrawer)
            )
        }
    }

    private fun closeDrawer() {
        statefulStore.process { oldState ->
            oldState.copy(
                isDrawerOpen = false,
                consumableEvent = ConsumableEvent.create(Event.CloseDrawer)
            )
        }
    }

    private fun navigateBack() {
        screenModelScope.launch {
            navigator.pop()
        }
    }

    private fun updateCurrentRoute(route: String?) {
        // Simplified route detection logic
        val isHome = route == "home"
        val isSplash = route == "splash"
        val isLogin = route == "login"
        val isOnboarding = route == "onboarding"

        statefulStore.process { oldState ->
            oldState.copy(
                imageVector = when {
                    isSplash || isLogin || isOnboarding -> null
                    isHome -> Res.drawable.home_icon
                    else ->  Res.drawable.back_icon
                },
                isHomeScreen = isHome
            )
        }
    }

    data class AppState(
        val isDrawerOpen: Boolean = false,
        val isHomeScreen: Boolean = false,
        val imageVector: DrawableResource? = null,
        val consumableEvent: ConsumableEvent<Event> = ConsumableEvent()
    )

    sealed interface Action {
        data class UpdateCurrentRoute(val route: String?): Action
        data object NavigateBack: Action
        data object NavClick : Action
        data object CloseDrawer : Action
        data object OpenDrawer : Action
        data object Logout : Action
    }

    sealed interface Event {
        data object OpenDrawer : Event
        data object CloseDrawer : Event
    }
}