package org.dallas.smartshelf.repository

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.parameter
import io.ktor.http.isSuccess
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.datetime.LocalDate
import org.dallas.smartshelf.manager.JwtAuthManager
import org.dallas.smartshelf.model.EggNutrition
import org.dallas.smartshelf.model.EggNutritionResponse
import org.dallas.smartshelf.model.EggNutritionSearchResult
import org.dallas.smartshelf.model.EggNutritionSearchResultDto
import org.dallas.smartshelf.model.EggPrice
import org.dallas.smartshelf.model.EggPriceHistory
import org.dallas.smartshelf.model.EggPriceResponse
import org.dallas.smartshelf.model.EggType
import org.dallas.smartshelf.model.RefreshResult
import org.dallas.smartshelf.model.RefreshResultDto
import org.dallas.smartshelf.model.toEggNutrition
import org.dallas.smartshelf.model.toEggPriceHistory
import org.dallas.smartshelf.util.PlatformContext
import org.dallas.smartshelf.util.parseToLocalDateTime

interface EggRepository {
    // Nutrition endpoints
    suspend fun getEggNutritionById(fdcId: Long): Result<EggNutrition?>
    suspend fun getEggNutritionComparison(): Result<List<EggNutrition>>
    suspend fun searchEggNutrition(query: String, pageSize: Int = 25, pageNumber: Int = 1): Result<EggNutritionSearchResult>
    suspend fun getEggNutritionByType(eggType: EggType): Result<List<EggNutrition>>
    suspend fun refreshEggNutrition(): Result<RefreshResult>

    // Price endpoints
    suspend fun getNationalEggPrices(): Result<EggPriceHistory>
    suspend fun getEggPricesByType(eggType: EggType): Result<List<EggPrice>>
    suspend fun getHistoricalEggPrices(
        startDate: LocalDate? = null,
        endDate: LocalDate? = null,
        eggType: EggType? = null
    ): Result<EggPriceHistory>
    suspend fun refreshEggPrices(): Result<RefreshResult>

    // Reactive data streams
    fun eggNutritionFlow(): Flow<List<EggNutrition>>
    fun eggPricesFlow(): Flow<EggPriceHistory?>
}

class EggRepositoryImpl(
    httpClient: HttpClient,
    jwtAuthManager: JwtAuthManager,
    platformContext: PlatformContext
) : BaseRepository(httpClient, jwtAuthManager, platformContext), EggRepository {

    private val nutritionFlow = MutableStateFlow<List<EggNutrition>>(emptyList())
    private val pricesFlow = MutableStateFlow<EggPriceHistory?>(null)

    // API paths
    private val nutritionBasePath = "$baseUrl/api/v1/egg-nutrition"
    private val pricesBasePath = "$baseUrl/api/v1/egg-prices"

    private fun EggNutritionSearchResultDto.toModel(): EggNutritionSearchResult = EggNutritionSearchResult(
        items = this.items.map { it.toEggNutrition() },
        totalItems = this.totalItems,
        totalPages = this.totalPages,
        currentPage = this.currentPage,
        pageSize = this.pageSize
    )

    private fun RefreshResultDto.toModel(): RefreshResult = RefreshResult(
        status = this.status,
        message = this.message,
        timestamp = this.timestamp,
        duration = this.duration
    )

    /* ---------- Nutrition API Implementation ---------- */

    override suspend fun getEggNutritionById(fdcId: Long): Result<EggNutrition?> {
        return try {
            val response = httpClient.get("$nutritionBasePath/$fdcId") {
                authorizedRequest()
            }

            if (response.status.isSuccess()) {
                val nutritionResponse = response.body<EggNutritionResponse>()
                Result.success(nutritionResponse.toEggNutrition())
            } else if (response.status.value == 404) {
                Result.success(null)
            } else {
                Result.failure(Exception("Failed to get egg nutrition: ${response.status}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getEggNutritionComparison(): Result<List<EggNutrition>> {
        return try {
            val response = httpClient.get("$nutritionBasePath/compare") {
                authorizedRequest()
            }

            return handleApiResponse(response) {
                val nutritionResponses = it.body<List<EggNutritionResponse>>()
                val nutritionList = nutritionResponses.map { response -> response.toEggNutrition() }

                // Update the flow
                nutritionFlow.value = nutritionList

                nutritionList
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun searchEggNutrition(query: String, pageSize: Int, pageNumber: Int): Result<EggNutritionSearchResult> {
        return try {
            val response = httpClient.get("$nutritionBasePath/search") {
                authorizedRequest()
                parameter("query", query)
                parameter("pageSize", pageSize)
                parameter("pageNumber", pageNumber)
            }

            return handleApiResponse(response) {
                val searchResultDto = it.body<EggNutritionSearchResultDto>()
                searchResultDto.toModel()
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getEggNutritionByType(eggType: EggType): Result<List<EggNutrition>> {
        return try {
            val response = httpClient.get("$nutritionBasePath/by-type/${eggType.name}") {
                authorizedRequest()
            }

            return handleApiResponse(response) {
                val nutritionResponses = it.body<List<EggNutritionResponse>>()
                val nutritionList = nutritionResponses.map { response -> response.toEggNutrition() }

                // Update the flow with filtered results
                val currentList = nutritionFlow.value.toMutableList()
                nutritionList.forEach { nutrition ->
                    val index = currentList.indexOfFirst { it.fdcId == nutrition.fdcId }
                    if (index >= 0) {
                        currentList[index] = nutrition
                    } else {
                        currentList.add(nutrition)
                    }
                }
                nutritionFlow.value = currentList

                nutritionList
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun refreshEggNutrition(): Result<RefreshResult> {
        return try {
            val response = httpClient.get("$nutritionBasePath/refresh") {
                authorizedRequest()
            }

            return handleApiResponse(response) {
                val refreshResult = it.body<RefreshResultDto>()

                // After refresh, update our cached data
                getEggNutritionComparison()

                refreshResult.toModel()
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /* ---------- Price API Implementation ---------- */

    override suspend fun getNationalEggPrices(): Result<EggPriceHistory> {
        return try {
            val response = httpClient.get("$pricesBasePath/national") {
                authorizedRequest()
            }

            return handleApiResponse(response) {
                val priceResponse = it.body<EggPriceResponse>()
                val priceHistory = priceResponse.toEggPriceHistory()

                // Update the flow
                pricesFlow.value = priceHistory

                priceHistory
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getEggPricesByType(eggType: EggType): Result<List<EggPrice>> {
        return try {
            val response = httpClient.get("$pricesBasePath/by-type/${eggType.name}") {
                authorizedRequest()
            }

            return handleApiResponse(response) {
                val priceDataList = it.body<List<EggPriceResponse.EggPriceData>>()
                priceDataList.map { priceData ->
                    EggPrice(
                        eggType = priceData.eggType,
                        price = priceData.price,
                        date = parseToLocalDateTime(priceData.date),
                        source = priceData.source
                    )
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getHistoricalEggPrices(
        startDate: LocalDate?,
        endDate: LocalDate?,
        eggType: EggType?
    ): Result<EggPriceHistory> {
        return try {
            val response = httpClient.get("$pricesBasePath/historical") {
                authorizedRequest()
                startDate?.let { parameter("startDate", it.toString()) }
                endDate?.let { parameter("endDate", it.toString()) }
                eggType?.let { parameter("eggType", it.name) }
            }

            return handleApiResponse(response) {
                val priceResponse = it.body<EggPriceResponse>()
                priceResponse.toEggPriceHistory()
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun refreshEggPrices(): Result<RefreshResult> {
        return try {
            val response = httpClient.post("$pricesBasePath/refresh") {
                authorizedRequest()
            }

            return handleApiResponse(response) {
                val refreshResult = it.body<RefreshResultDto>()

                // After refresh, update our cached data
                getNationalEggPrices()

                refreshResult.toModel()
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /* ---------- Flow Getters ---------- */

    override fun eggNutritionFlow(): Flow<List<EggNutrition>> = nutritionFlow
    override fun eggPricesFlow(): Flow<EggPriceHistory?> = pricesFlow
}