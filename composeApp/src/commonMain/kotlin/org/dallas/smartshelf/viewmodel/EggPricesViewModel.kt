package org.dallas.smartshelf.viewmodel

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.dallas.smartshelf.model.EggPriceHistory
import org.dallas.smartshelf.usecase.GetNationalEggPricesUseCase
import org.dallas.smartshelf.util.ApiResult

class EggPricesViewModel(
    private val getNationalEggPricesUseCase: GetNationalEggPricesUseCase
) : ScreenModel {

    private val _eggNutrition = MutableStateFlow<ApiResult<EggPriceHistory>>(ApiResult.Loading)
    val eggNutrition: StateFlow<ApiResult<EggPriceHistory>> = _eggNutrition

    fun loadNationalEggPrices() {
        screenModelScope.launch {
            _eggNutrition.value = ApiResult.Loading
            getNationalEggPricesUseCase()
                .onSuccess {
                    _eggNutrition.value = ApiResult.Success(it)
                }.onFailure {
                    _eggNutrition.value = ApiResult.Error(Exception(it))
                }
        }
    }
}