package org.dallas.smartshelf.viewmodel

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.navigator.Navigator
import com.junevrtech.smartshelf.store.ModelStore
import com.junevrtech.smartshelf.store.StatefulStore
import io.github.aakira.napier.Napier
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.dallas.smartshelf.manager.SharedPreferencesManager
import org.dallas.smartshelf.repository.AuthenticationRepository
import org.dallas.smartshelf.util.ConsumableEvent

class DeleteAccountViewModel(
    private val navigator: Navigator,
    private val authRepository: AuthenticationRepository,
    private val sharedPreferencesManager: SharedPreferencesManager
) : ScreenModel {
    private val statefulStore: ModelStore<ViewState> = StatefulStore(ViewState(), screenModelScope)
    val viewState: StateFlow<ViewState> get() = statefulStore.state

    fun onAction(action: Action) {
        when (action) {
            is Action.DeleteAccount -> deleteAccount()
            is Action.CancelDeleteAccount -> cancelDeleteAccount()
        }
    }

    private fun setLoadingState(isLoading: Boolean) {
        statefulStore.process { oldState -> oldState.copy(isLoading = isLoading) }
    }

    private fun cancelDeleteAccount() {
        navigator.pop()
    }

    private fun deleteAccount() {
        setLoadingState(true)

        screenModelScope.launch {
            authRepository.deleteAccount()
                .onSuccess {
                    statefulStore.process { oldState ->
                        oldState.copy(
                            isLoading = false,
                            showErrorDialog = false,
                            consumableEvent = ConsumableEvent.create(Event.AccountDeleted)
                        )
                    }

                    // Perform logout and cleanup
                    delay(500)
                    sharedPreferencesManager.clear()

                    // Navigate to login screen
                    // navigator.replaceAll(LoginScreen())
                }
                .onFailure { exception ->
                    Napier.e(tag = "DeleteAccountViewModel", message = "Account deletion error: ${exception.message}")

                    statefulStore.process { oldState ->
                        oldState.copy(
                            isLoading = false,
                            showErrorDialog = true,
                            consumableEvent = ConsumableEvent.create(Event.AccountDeletionFailed)
                        )
                    }
                }
        }
    }

    data class ViewState(
        val isLoading: Boolean = false,
        val showErrorDialog: Boolean = false,
        val consumableEvent: ConsumableEvent<Event> = ConsumableEvent()
    )

    sealed interface Action {
        data object DeleteAccount : Action
        data object CancelDeleteAccount : Action
    }

    sealed interface Event {
        data object AccountDeleted : Event
        data object AccountDeletionFailed : Event
    }

    override fun onDispose() {
        super.onDispose()
        screenModelScope.cancel()
    }
}