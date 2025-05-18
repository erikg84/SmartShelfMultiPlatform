package org.dallas.smartshelf.repository

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.Serializable
import org.dallas.smartshelf.manager.JwtAuthManager
import org.dallas.smartshelf.model.Product
import org.dallas.smartshelf.model.ProductCategory
import org.dallas.smartshelf.util.PlatformContext
import kotlin.random.Random

interface ProductRepository {
    suspend fun saveProduct(product: Product): Result<Product>
    suspend fun getProducts(): Result<List<Product>>
    suspend fun updateProduct(product: Product): Result<Product>
    suspend fun deleteProduct(productId: String): Result<Unit>
    suspend fun getProductByBarcode(barcode: String): Result<Product?>

    fun productsFlow(): Flow<List<Product>>
}

class ProductRepositoryImpl(
    httpClient: HttpClient,
    jwtAuthManager: JwtAuthManager,
    platformContext: PlatformContext
) : BaseRepository(httpClient, jwtAuthManager, platformContext), ProductRepository {

    private val productsFlow = MutableStateFlow<List<Product>>(emptyList())

    @Serializable
    data class ProductDto(
        val id: String = "product-${Random.nextInt(1000, 9999)}",
        val barcode: String?,
        val name: String,
        val quantity: Int = 0,
        val purchaseDate: String,
        val expiryDate: String?,
        val category: String,
        val lastModified: String = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).toString(),
        val userId: String = ""
    )

    private fun Product.toDto(): ProductDto = ProductDto(
        id = this.id,
        barcode = this.barcode,
        name = this.name,
        quantity = this.quantity,
        purchaseDate = this.purchaseDate.toString(),
        expiryDate = this.expiryDate?.toString(),
        category = this.category.name,
        lastModified = this.lastModified.toString(),
        userId = this.userId
    )

    private fun ProductDto.toModel(): Product = Product(
        id = this.id,
        barcode = this.barcode,
        name = this.name,
        quantity = this.quantity,
        purchaseDate = LocalDateTime.parse(this.purchaseDate),
        expiryDate = this.expiryDate?.let { LocalDateTime.parse(it) },
        category = try {
            ProductCategory.valueOf(this.category)
        } catch (e: Exception) {
            ProductCategory.OTHER
        },
        lastModified = LocalDateTime.parse(this.lastModified),
        userId = this.userId
    )

    override suspend fun saveProduct(product: Product): Result<Product> {
        return try {
            val response = httpClient.post("$baseUrl/products") {
                contentType(ContentType.Application.Json)
                authorizedRequest()
                setBody(product.toDto())
            }

            handleApiResponse(response) {
                val savedProduct = it.body<ProductDto>().toModel()

                // Update the flow with the new product
                val currentProducts = productsFlow.value.toMutableList()
                currentProducts.add(savedProduct)
                productsFlow.value = currentProducts

                savedProduct
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getProducts(): Result<List<Product>> {
        return try {
            val response = httpClient.get("$baseUrl/products") {
                authorizedRequest()
            }

            handleApiResponse(response) {
                val products = it.body<List<ProductDto>>().map { dto -> dto.toModel() }

                // Update the flow
                productsFlow.value = products

                products
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateProduct(product: Product): Result<Product> {
        return try {
            val response = httpClient.put("$baseUrl/products/${product.id}") {
                contentType(ContentType.Application.Json)
                authorizedRequest()
                setBody(product.toDto())
            }

            handleApiResponse(response) {
                val updatedProduct = it.body<ProductDto>().toModel()

                // Update the flow
                productsFlow.update { products ->
                    products.map {
                        if (it.id == updatedProduct.id) updatedProduct else it
                    }
                }

                updatedProduct
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteProduct(productId: String): Result<Unit> {
        return try {
            val response = httpClient.delete("$baseUrl/products/$productId") {
                authorizedRequest()
            }

            handleApiResponse(response) {
                // Update the flow
                productsFlow.update { products ->
                    products.filter { it.id != productId }
                }

                Unit
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getProductByBarcode(barcode: String): Result<Product?> {
        return try {
            val response = httpClient.get("$baseUrl/products/barcode/$barcode") {
                authorizedRequest()
            }

            if (response.status == HttpStatusCode.NotFound) {
                return Result.success(null)
            }

            handleApiResponse(response) {
                it.body<ProductDto>().toModel()
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun productsFlow(): Flow<List<Product>> = productsFlow
}