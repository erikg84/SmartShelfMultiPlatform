package org.dallas.smartshelf.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.dallas.smartshelf.model.EggNutrition
import org.dallas.smartshelf.model.EggType
import org.dallas.smartshelf.util.ApiResult

class EggNutritionViewModel(
    private val getEggNutritionByTypeUseCase: GetEggNutritionByTypeUseCase
) : ViewModel() {

    private val _eggNutrition = MutableStateFlow<ApiResult<List<EggNutrition>>>(ApiResult.Loading)
    val eggNutrition: StateFlow<ApiResult<List<EggNutrition>>> = _eggNutrition

    fun getEggNutritionByType(eggType: EggType) {
        viewModelScope.launch {
            _eggNutrition.value = ApiResult.Loading
            _eggNutrition.value = getEggNutritionByTypeUseCase(eggType)
        }
    }
}