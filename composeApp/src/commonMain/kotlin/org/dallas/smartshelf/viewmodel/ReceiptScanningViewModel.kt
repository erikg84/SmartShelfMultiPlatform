package org.dallas.smartshelf.viewmodel

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.navigator.Navigator
import com.junevrtech.smartshelf.store.ModelStore
import com.junevrtech.smartshelf.store.StatefulStore
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.dallas.smartshelf.manager.CapturedDataManager
import org.dallas.smartshelf.model.CapturedScanData
import org.dallas.smartshelf.util.ConsumableEvent
import org.dallas.smartshelf.view.screen.model.CaptureDataModelScreen

class ReceiptScanningViewModel(
    private val navigator: Navigator,
    private val capturedDataManager: CapturedDataManager
) : ScreenModel {

    private val statefulStore: ModelStore<ViewState> = StatefulStore(ViewState(), screenModelScope)
    val viewState: StateFlow<ViewState> get() = statefulStore.state

    fun onAction(action: Action) {
        when (action) {
            is Action.OCRSuccess -> handleOCRSuccess(action.text)
            is Action.OCRFailed -> handleOCRFailed(action.errorMessage)
            is Action.StartProcessing -> startProcessing()
        }
    }

    private fun startProcessing() {
        capturedDataManager.startProcessing()
    }

    private fun handleOCRSuccess(text: String) {
        Napier.d(tag = TAG, message = "handleOCRSuccess: $text")
        statefulStore.process { oldState ->
            oldState.copy(
                isSuccessScan = true
            )
        }
        capturedDataManager.updateCapturedData(CapturedScanData(receipt = text))
        screenModelScope.launch {
            navigator.push(CaptureDataModelScreen)
        }
    }

    private fun handleOCRFailed(errorMessage: String) {
        statefulStore.process { oldState ->
            oldState.copy(
                consumableEvent = ConsumableEvent.create(Event.OCRFailed)
            )
        }
    }

    data class ViewState(
        val isSuccessScan: Boolean = false,
        val consumableEvent: ConsumableEvent<Event> = ConsumableEvent()
    )

    sealed interface Action {
        data class OCRSuccess(val text: String) : Action
        data class OCRFailed(val errorMessage: String) : Action
        data object StartProcessing : Action
    }

    sealed interface Event {
        data object OCRFailed : Event
    }

    companion object {
        private const val TAG = "ReceiptScanningViewModel"
    }
}