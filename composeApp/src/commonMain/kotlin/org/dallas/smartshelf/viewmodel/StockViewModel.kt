package org.dallas.smartshelf.viewmodel

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.junevrtech.smartshelf.store.ModelStore
import com.junevrtech.smartshelf.store.StatefulStore
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import org.dallas.smartshelf.model.Product
import org.dallas.smartshelf.model.ProductCategory
import org.dallas.smartshelf.model.ProductFilter
import org.dallas.smartshelf.model.ProductSortOption
import org.dallas.smartshelf.repository.ProductRepository

class StockViewModel(
    private val productRepository: ProductRepository
) : ScreenModel {

    private val statefulStore: ModelStore<ViewState> = StatefulStore(ViewState(), screenModelScope)
    val viewState: StateFlow<ViewState> get() = statefulStore.state

    init {
        loadProducts()
    }

    fun onAction(action: Action) {
        when (action) {
            is Action.UpdateSearchQuery -> updateSearchQuery(action.query)
            is Action.ToggleCategory -> toggleCategory(action.category)
            is Action.UpdateSortOption -> updateSortOption(action.option)
            is Action.ToggleExpiredFilter -> toggleExpiredFilter()
            is Action.ToggleExpiringSoonFilter -> toggleExpiringSoonFilter()
            is Action.RefreshProducts -> loadProducts()
        }
    }

    private fun updateSearchQuery(query: String) {
        statefulStore.process { oldState ->
            val newFilter = oldState.filter.copy(searchQuery = query)
            oldState.copy(
                filter = newFilter,
                filteredProducts = applyFilter(oldState.products, newFilter)
            )
        }
    }

    private fun toggleCategory(category: ProductCategory) {
        statefulStore.process { oldState ->
            val currentCategories = oldState.filter.categories.toMutableSet()
            if (currentCategories.contains(category)) {
                currentCategories.remove(category)
            } else {
                currentCategories.add(category)
            }

            val newFilter = oldState.filter.copy(categories = currentCategories)
            oldState.copy(
                filter = newFilter,
                filteredProducts = applyFilter(oldState.products, newFilter)
            )
        }
    }

    private fun updateSortOption(option: ProductSortOption) {
        statefulStore.process { oldState ->
            val newFilter = oldState.filter.copy(sortBy = option)
            oldState.copy(
                filter = newFilter,
                filteredProducts = applyFilter(oldState.products, newFilter)
            )
        }
    }

    private fun toggleExpiredFilter() {
        statefulStore.process { oldState ->
            val newFilter = oldState.filter.copy(
                showExpiredOnly = !oldState.filter.showExpiredOnly,
                showExpiringSoonOnly = false
            )
            oldState.copy(
                filter = newFilter,
                filteredProducts = applyFilter(oldState.products, newFilter)
            )
        }
    }

    private fun toggleExpiringSoonFilter() {
        statefulStore.process { oldState ->
            val newFilter = oldState.filter.copy(
                showExpiringSoonOnly = !oldState.filter.showExpiringSoonOnly,
                showExpiredOnly = false
            )
            oldState.copy(
                filter = newFilter,
                filteredProducts = applyFilter(oldState.products, newFilter)
            )
        }
    }

    private fun applyFilter(products: List<Product>, filter: ProductFilter): List<Product> {
        return products
            .filter { it.matchesFilter(filter) }
            .sortedWith(filter.sortBy.comparator())
    }

    private fun loadProducts() {
        screenModelScope.launch {
            statefulStore.process { it.copy(isLoading = true) }

            productRepository.productsFlow()
                .catch { error ->
                    statefulStore.process {
                        it.copy(
                            isLoading = false,
                            error = error.message
                        )
                    }
                }
                .collect { products ->
                    statefulStore.process { oldState ->
                        oldState.copy(
                            isLoading = false,
                            products = products,
                            filteredProducts = applyFilter(products, oldState.filter),
                            error = null
                        )
                    }
                }
        }
    }

    data class ViewState(
        val products: List<Product> = emptyList(),
        val filteredProducts: List<Product> = emptyList(),
        val filter: ProductFilter = ProductFilter(),
        val isLoading: Boolean = false,
        val error: String? = null
    )

    sealed interface Action {
        data class UpdateSearchQuery(val query: String) : Action
        data class ToggleCategory(val category: ProductCategory) : Action
        data class UpdateSortOption(val option: ProductSortOption) : Action
        data object ToggleExpiredFilter : Action
        data object ToggleExpiringSoonFilter : Action
        data object RefreshProducts : Action
    }
}