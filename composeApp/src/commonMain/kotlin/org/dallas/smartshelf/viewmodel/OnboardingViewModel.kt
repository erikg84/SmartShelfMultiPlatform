package org.dallas.smartshelf.viewmodel

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.navigator.Navigator
import com.junevrtech.smartshelf.store.ModelStore
import com.junevrtech.smartshelf.store.StatefulStore
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.dallas.smartshelf.manager.SharedPreferencesManager
import org.dallas.smartshelf.util.ConsumableEvent
import org.dallas.smartshelf.view.screen.model.MainScreen

class OnboardingViewModel(
    private val navigator: Navigator,
    private val sharedPreferencesManager: SharedPreferencesManager
) : ScreenModel {

    private val statefulStore: ModelStore<ViewState> = StatefulStore(ViewState(), screenModelScope)
    val viewState: StateFlow<ViewState> get() = statefulStore.state

    fun onAction(action: Action) {
        when (action) {
            is Action.RequestCameraPermission -> requestCameraPermission()
            is Action.CompleteOnboarding -> completeOnboarding()
            is Action.CameraPermissionGranted -> cameraPermissionGranted()
            is Action.CameraPermissionDenied -> cameraPermissionDenied()
            is Action.CameraPermissionDeniedDialogConfirmed -> cameraPermissionDeniedDialogConfirmed()
            is Action.DismissDialog -> dismissDialog()
        }
    }

    private fun cameraPermissionDeniedDialogConfirmed() {
        statefulStore.process { oldState ->
            oldState.copy(
                isPermissionDeniedDialogVisible = false,
                consumableEvent = ConsumableEvent.create(Event.CameraPermissionDeniedDialogConfirmed)
            )
        }
    }

    private fun dismissDialog() {
        statefulStore.process { oldState ->
            oldState.copy(
                isPermissionDeniedDialogVisible = false
            )
        }
    }

    private fun cameraPermissionDenied() {
        statefulStore.process { oldState ->
            oldState.copy(
                isPermissionDeniedDialogVisible = true
            )
        }
    }

    private fun cameraPermissionGranted() {
        statefulStore.process { oldState ->
            oldState.copy(
                consumableEvent = ConsumableEvent.create(Event.CameraPermissionGranted)
            )
        }
    }

    private fun requestCameraPermission() {
        statefulStore.process { oldState ->
            oldState.copy(
                consumableEvent = ConsumableEvent.create(Event.RequestCameraPermission)
            )
        }
    }

    private fun completeOnboarding() {
        sharedPreferencesManager.putBoolean("is_first_time_login", false)
        screenModelScope.launch {
            navigator.replaceAll(MainScreen)
        }
    }

    data class ViewState(
        val isPermissionDeniedDialogVisible: Boolean = false,
        val consumableEvent: ConsumableEvent<Event> = ConsumableEvent()
    )

    sealed interface Action {
        data object RequestCameraPermission : Action
        data object CameraPermissionGranted : Action
        data object CompleteOnboarding : Action
        data object CameraPermissionDenied : Action
        data object CameraPermissionDeniedDialogConfirmed : Action
        data object DismissDialog : Action
    }

    sealed interface Event {
        data object RequestCameraPermission : Event
        data object CameraPermissionGranted : Event
        data object CameraPermissionDeniedDialogConfirmed : Event
    }
}