package org.dallas.smartshelf.viewmodel

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.dallas.smartshelf.model.EggNutrition
import org.dallas.smartshelf.model.EggType
import org.dallas.smartshelf.usecase.GetEggNutritionByTypeUseCase
import org.dallas.smartshelf.util.ApiResult

class EggNutritionViewModel(
    private val getEggNutritionByTypeUseCase: GetEggNutritionByTypeUseCase
) : ScreenModel {

    private val _eggNutrition = MutableStateFlow<ApiResult<List<EggNutrition>>>(ApiResult.Loading)
    val eggNutrition: StateFlow<ApiResult<List<EggNutrition>>> = _eggNutrition

    fun getEggNutritionByType(eggType: EggType) {
        screenModelScope.launch {
            _eggNutrition.value = ApiResult.Loading
            getEggNutritionByTypeUseCase(eggType)
                .onSuccess {
                    _eggNutrition.value = ApiResult.Success(it)
                }.onFailure {
                    _eggNutrition.value = ApiResult.Error(Exception(it))
                }
        }
    }
}