package org.dallas.smartshelf.viewmodel

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.navigator.Navigator
import com.junevrtech.smartshelf.store.ModelStore
import com.junevrtech.smartshelf.store.StatefulStore
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.dallas.smartshelf.manager.CapturedDataManager
import org.dallas.smartshelf.model.CapturedScanData
import org.dallas.smartshelf.util.ConsumableEvent
import org.dallas.smartshelf.view.screen.model.CaptureDataModelScreen

class BarcodeScanningViewModel(
    private val navigator: Navigator,
    private val capturedDataManager: CapturedDataManager
) : ScreenModel {

    private val statefulStore: ModelStore<ViewState> = StatefulStore(ViewState(), screenModelScope)
    val viewState: StateFlow<ViewState> get() = statefulStore.state

    fun onAction(action: Action) {
        when (action) {
            is Action.ScanSuccess -> handleScanSuccess(action.scanResult)
            is Action.ScanFailed -> handleScanFailed(action.errorMessage)
        }
    }

    private fun handleScanSuccess(scanResult: String) {
        capturedDataManager.updateCapturedData(CapturedScanData(barcode = scanResult))
        screenModelScope.launch {
            navigator.push(CaptureDataModelScreen)
        }
    }

    private fun handleScanFailed(errorMessage: String) {
        statefulStore.process { oldState ->
            oldState.copy(
                consumableEvent = ConsumableEvent.create(Event.ScanFailed)
            )
        }
    }

    data class ViewState(
        val consumableEvent: ConsumableEvent<Event> = ConsumableEvent()
    )

    sealed interface Action {
        data class ScanSuccess(val scanResult: String) : Action
        data class ScanFailed(val errorMessage: String) : Action
    }

    sealed interface Event {
        data object ScanFailed : Event
    }
}