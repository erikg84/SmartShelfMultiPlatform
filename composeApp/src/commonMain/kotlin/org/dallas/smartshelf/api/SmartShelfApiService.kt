package org.dallas.smartshelf.api

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import org.dallas.smartshelf.model.EggNutritionResponse
import org.dallas.smartshelf.model.EggType

class SmartShelfApiService(private val client: HttpClient) {

    suspend fun getEggNutritionById(fdcId: Long): EggNutritionResponse =
        client.get("api/v1/egg-nutrition/$fdcId")

    suspend fun getEggNutritionComparison(): List<EggNutritionResponse> =
        client.get("api/v1/egg-nutrition/compare")

    suspend fun searchEggNutrition(query: String, pageSize: Int = 25, pageNumber: Int = 1): Map<String, Any> =
        client.get("api/v1/egg-nutrition/search") {
            parameter("query", query)
            parameter("pageSize", pageSize)
            parameter("pageNumber", pageNumber)
        }

    suspend fun getEggNutritionByType(eggType: EggType): List<EggNutritionResponse> =
        client.get("api/v1/egg-nutrition/by-type/${eggType.name}")

    suspend fun refreshNutritionData(): Map<String, String> =
        client.get("api/v1/egg-nutrition/refresh")

    // Add egg price endpoints similarly
}