package org.dallas.smartshelf.usecase

import org.dallas.smartshelf.model.EggPriceHistory
import org.dallas.smartshelf.repository.EggRepository

class GetNationalEggPricesUseCase(
    private val eggRepository: EggRepository
) {
    suspend operator fun invoke(): Result<EggPriceHistory> {
        return eggRepository.getNationalEggPrices()
    }
}