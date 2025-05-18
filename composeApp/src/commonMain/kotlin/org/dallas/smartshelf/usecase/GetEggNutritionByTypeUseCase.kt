package org.dallas.smartshelf.usecase

import org.dallas.smartshelf.model.EggType
import org.dallas.smartshelf.util.ApiResult

class GetEggNutritionByTypeUseCase(
    private val repository: SmartShelfRepositoryImpl
) {
    suspend operator fun invoke(eggType: EggType): ApiResult<List<EggNutrition>> {
        return repository.getEggNutritionByType(eggType)
    }
}