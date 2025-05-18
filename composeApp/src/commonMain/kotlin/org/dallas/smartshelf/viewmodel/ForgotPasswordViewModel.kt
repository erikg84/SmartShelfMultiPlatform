package org.dallas.smartshelf.viewmodel

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.navigator.Navigator
import com.junevrtech.smartshelf.store.ModelStore
import com.junevrtech.smartshelf.store.StatefulStore
import io.github.aakira.napier.Napier
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.dallas.smartshelf.repository.AuthenticationRepository
import org.dallas.smartshelf.util.ConsumableEvent

class ForgotPasswordViewModel(
    private val navigator: Navigator,
    private val authRepository: AuthenticationRepository
) : ScreenModel {
    private val statefulStore: ModelStore<ViewState> = StatefulStore(ViewState(), screenModelScope)
    val viewState: StateFlow<ViewState> get() = statefulStore.state

    fun onAction(action: Action) {
        when (action) {
            is Action.UpdateEmail -> updateEmail(action.email)
            is Action.SendPasswordReset -> sendPasswordReset()
            is Action.NavigateBack -> navigateBack()
        }
    }

    private fun updateEmail(email: String) {
        statefulStore.process { oldState ->
            oldState.copy(
                email = email,
                isEmailValid = email.isNotBlank() && email.contains('@')
            )
        }
    }

    private fun sendPasswordReset() {
        setLoadingState(true)
        val email = statefulStore.state.value.email

        screenModelScope.launch {
            authRepository.sendPasswordReset(email)
                .onSuccess {
                    statefulStore.process { oldState ->
                        oldState.copy(
                            isLoading = false,
                            isResetSuccess = true,
                            showErrorDialog = false
                        )
                    }
                }
                .onFailure { exception ->
                    Napier.e(tag = "ForgotPasswordViewModel", message = "Password reset error: ${exception.message}")

                    statefulStore.process { oldState ->
                        oldState.copy(
                            isLoading = false,
                            isResetSuccess = false,
                            showErrorDialog = true,
                            consumableEvent = ConsumableEvent.create(
                                Event.Error(exception.message ?: "Error sending reset email")
                            )
                        )
                    }
                }
        }
    }

    private fun navigateBack() {
        navigator.pop()
    }

    private fun setLoadingState(isLoading: Boolean) {
        statefulStore.process { oldState -> oldState.copy(isLoading = isLoading) }
    }

    data class ViewState(
        val isLoading: Boolean = false,
        val email: String = "",
        val isEmailValid: Boolean = false,
        val isResetSuccess: Boolean = false,
        val showErrorDialog: Boolean = false,
        val consumableEvent: ConsumableEvent<Event> = ConsumableEvent()
    )

    sealed interface Action {
        data class UpdateEmail(val email: String) : Action
        data object SendPasswordReset : Action
        data object NavigateBack : Action
    }

    sealed interface Event {
        data class Error(val message: String) : Event
    }

    override fun onDispose() {
        super.onDispose()
        screenModelScope.cancel()
    }
}