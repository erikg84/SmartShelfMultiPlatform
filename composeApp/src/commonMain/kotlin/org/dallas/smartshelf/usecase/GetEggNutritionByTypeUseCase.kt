package org.dallas.smartshelf.usecase

import org.dallas.smartshelf.model.EggNutrition
import org.dallas.smartshelf.model.EggType
import org.dallas.smartshelf.repository.EggRepository

class GetEggNutritionByTypeUseCase(
    private val eggRepository: EggRepository
) {
    suspend operator fun invoke(eggType: EggType): Result<List<EggNutrition>> {
        return eggRepository.getEggNutritionByType(eggType)
    }
}