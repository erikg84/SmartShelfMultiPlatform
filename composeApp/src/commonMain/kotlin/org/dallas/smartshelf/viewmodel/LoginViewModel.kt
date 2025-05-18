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

class LoginViewModel(
    private val navigator: Navigator,
    private val authRepository: AuthenticationRepository,
    private val sharedPreferencesManager: SharedPreferencesManager
) : ScreenModel {
    private val statefulStore: ModelStore<ViewState> = StatefulStore(ViewState(), screenModelScope)
    val viewState: StateFlow<ViewState> get() = statefulStore.state

    init {
        loadRememberedCredentials()
    }

    fun onAction(action: Action) {
        when (action) {
            is Action.Login -> performLogin()
            is Action.UpdateUsername -> updateUsername(action.username)
            is Action.UpdatePassword -> updatePassword(action.password)
            is Action.ToggleRememberMe -> toggleRememberMe(action.isChecked)
            is Action.NavigateToRegister -> navigateToRegister()
            is Action.ForgotPassword -> navigateToForgotPassword()
            is Action.UpdateEmail -> updateEmail(action.email)
        }
    }

    private fun updateEmail(string: String) {
        statefulStore.process { oldState ->
            oldState.copy(
                email = string,
                isFormValid = string.isNotEmpty() && oldState.password.isNotEmpty()
            )
        }
    }

    private fun toggleRememberMe(isChecked: Boolean) {
        statefulStore.process { oldState -> oldState.copy(rememberMe = isChecked) }
    }

    private fun navigateToForgotPassword() {
        // Navigate to forgot password screen
        // navigator.push(ForgotPasswordScreen())
    }

    private fun updateUsername(username: String) {
        statefulStore.process { oldState ->
            oldState.copy(
                username = username,
                isFormValid = username.isNotEmpty() && oldState.password.isNotEmpty()
            )
        }
    }

    private fun updatePassword(password: String) {
        statefulStore.process { oldState ->
            oldState.copy(
                password = password,
                isFormValid = password.isNotEmpty() && oldState.username.isNotEmpty()
            )
        }
    }

    private fun navigateToRegister() {
        // Navigate to register screen
        // navigator.push(RegisterScreen())
    }

    private fun performLogin() {
        setLoadingState(true)
        val username = statefulStore.state.value.username
        val password = statefulStore.state.value.password

        screenModelScope.launch {
            authRepository.login(username, password)
                .onSuccess { user ->
                    if (statefulStore.state.value.rememberMe) {
                        sharedPreferencesManager.putString("remembered_username", username)
                    } else {
                        sharedPreferencesManager.remove("remembered_username")
                    }

                    statefulStore.process { oldState ->
                        oldState.copy(
                            isLoading = false,
                            consumableEvent = ConsumableEvent.create(Event.LoginSuccess)
                        )
                    }

                    // Check if first time login and navigate accordingly
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
                    Napier.e(tag = "LoginViewModel", message = "Login error: ${exception.message}")

                    statefulStore.process { oldState ->
                        oldState.copy(
                            isLoading = false,
                            showErrorDialog = true,
                            consumableEvent = ConsumableEvent.create(Event.Error(exception.message ?: "Login failed"))
                        )
                    }
                }
        }
    }

    private fun loadRememberedCredentials() {
        val username = sharedPreferencesManager.getString("remembered_username", null)
        statefulStore.process { oldState ->
            oldState.copy(
                username = username ?: "",
                rememberMe = username != null
            )
        }
    }

    private fun setLoadingState(isLoading: Boolean) {
        statefulStore.process { oldState -> oldState.copy(isLoading = isLoading) }
    }

    data class ViewState(
        val isLoading: Boolean = false,
        val username: String = "",
        val password: String = "",
        val email: String = "",
        val isFormValid: Boolean = false,
        val showErrorDialog: Boolean = false,
        val rememberMe: Boolean = false,
        val consumableEvent: ConsumableEvent<Event> = ConsumableEvent()
    )

    sealed interface Action {
        data class UpdateEmail(val email: String) : Action
        data class UpdateUsername(val username: String) : Action
        data class UpdatePassword(val password: String) : Action
        data object NavigateToRegister : Action
        data object ForgotPassword : Action
        data object Login : Action
        data class ToggleRememberMe(val isChecked: Boolean) : Action
    }

    sealed interface Event {
        data class Error(val message: String) : Event
        data object LoginSuccess : Event
    }

    override fun onDispose() {
        super.onDispose()
        screenModelScope.cancel()
    }
}