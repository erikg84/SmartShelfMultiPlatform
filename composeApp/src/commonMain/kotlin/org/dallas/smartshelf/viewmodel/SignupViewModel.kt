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
import org.dallas.smartshelf.manager.SharedPreferencesManager
import org.dallas.smartshelf.repository.AuthenticationRepository
import org.dallas.smartshelf.util.ConsumableEvent

class SignupViewModel(
    private val navigator: Navigator,
    private val authRepository: AuthenticationRepository,
    private val sharedPreferencesManager: SharedPreferencesManager
) : ScreenModel {
    private val statefulStore: ModelStore<ViewState> = StatefulStore(ViewState(), screenModelScope)
    val viewState: StateFlow<ViewState> get() = statefulStore.state

    fun onAction(action: Action) {
        when (action) {
            is Action.Register -> performRegistration()
            is Action.UpdateUsername -> updateUsername(action.username)
            is Action.UpdateEmail -> updateEmail(action.email)
            is Action.UpdatePassword -> updatePassword(action.password)
            is Action.NavigateToLogin -> navigateToLogin()
        }
    }

    private fun updateUsername(username: String) {
        statefulStore.process { oldState ->
            oldState.copy(
                username = username,
                isFormValid = username.isNotEmpty() && oldState.email.isNotEmpty() && oldState.password.isNotEmpty()
            )
        }
    }

    private fun updateEmail(email: String) {
        statefulStore.process { oldState ->
            oldState.copy(
                email = email,
                isFormValid = email.isNotEmpty() && oldState.username.isNotEmpty() && oldState.password.isNotEmpty()
            )
        }
    }

    private fun updatePassword(password: String) {
        statefulStore.process { oldState ->
            oldState.copy(
                password = password,
                isFormValid = password.isNotEmpty() && oldState.username.isNotEmpty() && oldState.email.isNotEmpty()
            )
        }
    }

    private fun performRegistration() {
        setLoadingState(true)
        val username = statefulStore.state.value.username
        val email = statefulStore.state.value.email
        val password = statefulStore.state.value.password

        screenModelScope.launch {
            authRepository.register(username, email, password)
                .onSuccess { user ->

                    statefulStore.process { oldState ->
                        oldState.copy(
                            isLoading = false,
                            showErrorDialog = false,
                            consumableEvent = ConsumableEvent.create(Event.SignupSuccess)
                        )
                    }

                    // Navigate to home or onboarding
                    val isFirstTimeLogin = sharedPreferencesManager.getBoolean("is_first_time_login", true)
                    if (isFirstTimeLogin) {
                        // Navigate to onboarding
                        // navigator.push(OnboardingScreen())
                        sharedPreferencesManager.putBoolean("is_first_time_login", false)
                    } else {
                        // Navigate to home screen
                        // navigator.push(HomeScreen())
                    }
                }
                .onFailure { exception ->
                    Napier.e(tag = "SignupViewModel", message = "Registration error: ${exception.message}")

                    statefulStore.process { oldState ->
                        oldState.copy(
                            isLoading = false,
                            showErrorDialog = true,
                            consumableEvent = ConsumableEvent.create(
                                Event.Error(exception.message ?: "Unknown error")
                            )
                        )
                    }
                }
        }
    }

    private fun navigateToLogin() {
        // Navigate to login screen
        navigator.pop()
    }

    private fun setLoadingState(isLoading: Boolean) {
        statefulStore.process { oldState -> oldState.copy(isLoading = isLoading) }
    }

    data class ViewState(
        val isLoading: Boolean = false,
        val username: String = "",
        val email: String = "",
        val password: String = "",
        val isFormValid: Boolean = false,
        val showErrorDialog: Boolean = false,
        val consumableEvent: ConsumableEvent<Event> = ConsumableEvent()
    )

    sealed interface Action {
        data class UpdateUsername(val username: String) : Action
        data class UpdateEmail(val email: String) : Action
        data class UpdatePassword(val password: String) : Action
        data object NavigateToLogin : Action
        data object Register : Action
    }

    sealed interface Event {
        data class Error(val message: String) : Event
        data object SignupSuccess : Event
    }

    override fun onDispose() {
        super.onDispose()
        screenModelScope.cancel()
    }
}