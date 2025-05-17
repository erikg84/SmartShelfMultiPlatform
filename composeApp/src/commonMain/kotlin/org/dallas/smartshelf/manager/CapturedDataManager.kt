package org.dallas.smartshelf.manager

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.dallas.smartshelf.model.CapturedScanData

/**
 * Manages captured scan data (barcodes, receipts) and processing state
 */
class CapturedDataManager {
    private val _captured = MutableStateFlow(CapturedScanData())
    val captured: StateFlow<CapturedScanData> = _captured.asStateFlow()

    private val _processing = MutableStateFlow(false)
    val processing: StateFlow<Boolean> = _processing.asStateFlow()

    /**
     * Update the captured scan data
     */
    fun updateCapturedData(data: CapturedScanData) {
        _captured.value = data
    }

    /**
     * Clear the captured scan data
     */
    fun clearCapturedData() {
        _captured.value = CapturedScanData()
    }

    /**
     * Set the processing state to true
     */
    fun startProcessing() {
        _processing.value = true
    }

    /**
     * Set the processing state to false
     */
    fun stopProcessing() {
        _processing.value = false
    }
}