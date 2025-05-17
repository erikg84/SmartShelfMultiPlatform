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
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.dallas.smartshelf.manager.CapturedDataManager
import org.dallas.smartshelf.manager.FirebaseAuthManager
import org.dallas.smartshelf.model.Product
import org.dallas.smartshelf.model.ProductCategory
import org.dallas.smartshelf.repository.ProductRepository
import org.dallas.smartshelf.util.ReceiptItem
import org.dallas.smartshelf.util.ReceiptParser

class CapturedDataViewModel(
    private val navigator: Navigator,
    private val capturedDataManager: CapturedDataManager,
    private val productRepository: ProductRepository,
    private val authManager: FirebaseAuthManager
) : ScreenModel {
    private val statefulStore: ModelStore<ViewState> = StatefulStore(ViewState(), screenModelScope)
    val viewState: StateFlow<ViewState> get() = statefulStore.state

    private val receiptParser = ReceiptParser()

    init {
        screenModelScope.launch {
            capturedDataManager.captured.collect { capturedData ->
                val receiptItems = capturedData.receipt?.let {
                    receiptParser.parseReceipt(it)
                }.orEmpty()

                // Update barcode from captured data
                statefulStore.process { oldState ->
                    oldState.copy(
                        barcode = capturedData.barcode,
                        receipt = capturedData.receipt,
                        receiptItems = receiptItems
                    )
                }
            }
        }
    }

    fun onAction(action: Action) {
        when (action) {
            is Action.UpdateProductName -> updateProductName(action.name)
            is Action.UpdateQuantity -> updateQuantity(action.quantity)
            is Action.UpdateCategory -> updateCategory(action.category)
            is Action.UpdateExpiryDate -> updateExpiryDate(action.date)
            is Action.SaveProduct -> saveProduct()
            is Action.Cancel -> navigateBack()
            is Action.StopProcessing -> stopProcessing()
        }
    }

    private fun stopProcessing() {
        capturedDataManager.stopProcessing()
    }

    private fun updateProductName(name: String) {
        statefulStore.process { oldState ->
            oldState.copy(
                productName = name
            )
        }
    }

    private fun updateQuantity(quantity: Int) {
        statefulStore.process { oldState ->
            oldState.copy(
                quantity = quantity.coerceIn(1, 999)  // Prevent negative or extremely large quantities
            )
        }
    }

    private fun updateCategory(category: ProductCategory) {
        statefulStore.process { oldState ->
            oldState.copy(
                category = category
            )
        }
    }

    private fun updateExpiryDate(date: LocalDateTime) {
        statefulStore.process { oldState ->
            oldState.copy(
                expiryDate = date
            )
        }
    }

    private fun navigateBack() {
        navigator.pop()
    }

    private fun saveProduct() {
        screenModelScope.launch {
            statefulStore.process { it.copy(isLoading = true) }

            val currentUser = authManager.getCurrentUser()
            if (currentUser == null) {
                statefulStore.process { oldState ->
                    oldState.copy(
                        error = "User not authenticated",
                        isLoading = false
                    )
                }
                return@launch
            }

            val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
            val product = Product(
                barcode = viewState.value.barcode,
                name = viewState.value.productName,
                quantity = viewState.value.quantity,
                purchaseDate = now,  // Set to current time
                expiryDate = viewState.value.expiryDate,
                category = viewState.value.category,
                lastModified = now,  // Set to current time
                userId = currentUser.userId  // Set the user ID
            )

            productRepository.saveProduct(product)
                .onSuccess {
                    // Clear the captured data after successful save
                    capturedDataManager.clearCapturedData()

                    // Navigate back
                    navigator.pop()
                }
                .onFailure { error ->
                    Napier.e(tag = "CapturedDataViewModel", message = "Error saving product: ${error.message}")

                    statefulStore.process { oldState ->
                        oldState.copy(
                            error = error.message,
                            isLoading = false
                        )
                    }
                }
        }
    }

    data class ViewState(
        val barcode: String? = null,
        val receipt: String? = null,
        val productName: String = "",
        val quantity: Int = 1,
        val category: ProductCategory = ProductCategory.OTHER,
        val expiryDate: LocalDateTime? = null,
        val error: String? = null,
        val isLoading: Boolean = false,
        val receiptItems: List<ReceiptItem> = emptyList()
    )

    sealed interface Action {
        data class UpdateProductName(val name: String) : Action
        data class UpdateQuantity(val quantity: Int) : Action
        data class UpdateCategory(val category: ProductCategory) : Action
        data class UpdateExpiryDate(val date: LocalDateTime) : Action
        data object SaveProduct : Action
        data object Cancel : Action
        data object StopProcessing : Action
    }

    override fun onDispose() {
        super.onDispose()
        screenModelScope.cancel()
    }
}