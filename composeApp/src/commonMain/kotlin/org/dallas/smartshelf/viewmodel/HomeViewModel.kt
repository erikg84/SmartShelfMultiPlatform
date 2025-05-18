package org.dallas.smartshelf.viewmodel

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.navigator.Navigator
import com.junevrtech.smartshelf.store.ModelStore
import com.junevrtech.smartshelf.store.StatefulStore
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.dallas.smartshelf.util.ConsumableEvent
import org.dallas.smartshelf.view.screen.model.BarcodeScanScreen
import org.dallas.smartshelf.view.screen.model.ReceiptScanScreen

class HomeViewModel(
    private val navigator: Navigator
) : ScreenModel {

    private val statefulStore: ModelStore<ViewState> = StatefulStore(ViewState(), screenModelScope)
    val viewState: StateFlow<ViewState> get() = statefulStore.state

    fun onAction(action: Action) {
        when (action) {
            is Action.ShowLogoutPrompt -> showLogoutPrompt()
            is Action.HideLogoutPrompt -> hideLogoutPrompt()
            is Action.Logout -> logout()
            is Action.NavigateToBarcodeScanner -> navigateToBarcodeScanner()
            is Action.NavigateToQrScanner -> navigateToQrScanner()
            is Action.NavigateToReceiptScanner -> navigateToReceiptScanner()
        }
    }

    private fun navigateToBarcodeScanner() {
        screenModelScope.launch {
            navigator.push(BarcodeScanScreen)
        }
    }

    private fun navigateToQrScanner() {
        // Implement QR scanner navigation if needed
    }

    private fun navigateToReceiptScanner() {
        screenModelScope.launch {
            navigator.push(ReceiptScanScreen)
        }
    }

    private fun logout() {
        // Implement logout logic
        navigator.popUntilRoot()
        // Replace with your login screen
        // navigator.replaceAll(LoginScreen)

        statefulStore.process { oldState ->
            oldState.copy(consumableEvent = ConsumableEvent.create(Event.LogoutSuccessful))
        }
    }

    private fun hideLogoutPrompt() {
        statefulStore.process { oldState ->
            oldState.copy(isLogoutPromptVisible = false)
        }
    }

    private fun showLogoutPrompt() {
        statefulStore.process { oldState ->
            oldState.copy(isLogoutPromptVisible = true)
        }
    }

    data class ViewState(
        val isLogoutPromptVisible: Boolean = false,
        val consumableEvent: ConsumableEvent<Event> = ConsumableEvent()
    )

    sealed interface Action {
        data object ShowLogoutPrompt : Action
        data object HideLogoutPrompt : Action
        data object Logout : Action
        data object NavigateToQrScanner : Action
        data object NavigateToBarcodeScanner : Action
        data object NavigateToReceiptScanner : Action
    }

    sealed interface Event {
        data object LogoutSuccessful : Event
    }
}