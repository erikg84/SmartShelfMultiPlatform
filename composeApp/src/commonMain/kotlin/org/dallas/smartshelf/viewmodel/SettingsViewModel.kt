package org.dallas.smartshelf.viewmodel

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.navigator.Navigator
import com.junevrtech.smartshelf.store.ModelStore
import com.junevrtech.smartshelf.store.StatefulStore
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.dallas.smartshelf.manager.LocaleManager
import org.dallas.smartshelf.util.ConsumableEvent
import org.dallas.smartshelf.view.screen.DeleteAccountScreen

class SettingsViewModel(
    private val navigator: Navigator,
    private val localeManager: LocaleManager
) : ScreenModel {

    private val statefulStore: ModelStore<ViewState> = StatefulStore(ViewState(), screenModelScope)
    val viewState: StateFlow<ViewState> get() = statefulStore.state

    init {
        statefulStore.process { oldState ->
            oldState.copy(
                isDarkTheme = false, // Implement theme detection
                isLanguageEnglish = localeManager.getCurrentLocale() == "en"
            )
        }
    }

    fun onAction(action: Action) {
        when (action) {
            is Action.ToggleTheme -> toggleTheme()
            is Action.ToggleLanguage -> toggleLanguage()
            is Action.ManageNotifications -> manageNotifications()
            is Action.ShowPrivacyPolicy -> showPrivacyPolicy()
            is Action.DeleteAccount -> navigateToDeleteAccountScreen()
        }
    }

    private fun toggleTheme() {
        statefulStore.process { oldState ->
            val isDarkTheme = !oldState.isDarkTheme
            // Implement theme toggling
            oldState.copy(isDarkTheme = isDarkTheme)
        }
    }

    private fun toggleLanguage() {
        statefulStore.process { oldState ->
            val isEnglish = oldState.isLanguageEnglish
            val newLanguage = if (isEnglish) "es" else "en"
            localeManager.setLocale(newLanguage)
            oldState.copy(
                isLanguageEnglish = !isEnglish,
                consumableEvent = ConsumableEvent.create(Event.LanguageChanged)
            )
        }
    }

    private fun manageNotifications() {
        statefulStore.process { oldState ->
            oldState.copy(
                consumableEvent = ConsumableEvent.create(Event.ShowNotificationsScreen)
            )
        }
    }

    private fun showPrivacyPolicy() {
        statefulStore.process { oldState ->
            oldState.copy(
                consumableEvent = ConsumableEvent.create(Event.ShowPrivacyPolicy)
            )
        }
    }

    private fun navigateToDeleteAccountScreen() {
        screenModelScope.launch {
            navigator.push(DeleteAccountScreen)
        }
    }

    data class ViewState(
        val isDarkTheme: Boolean = false,
        val isLanguageEnglish: Boolean = true,
        val consumableEvent: ConsumableEvent<Event> = ConsumableEvent()
    )

    sealed interface Action {
        data object ToggleTheme : Action
        data object ToggleLanguage : Action
        data object ManageNotifications : Action
        data object ShowPrivacyPolicy : Action
        data object DeleteAccount : Action
    }

    sealed interface Event {
        data object ThemeChanged : Event
        data object LanguageChanged : Event
        data object ShowNotificationsScreen : Event
        data object ShowPrivacyPolicy : Event
        data object NavigateToDeleteAccountScreen : Event
    }
}