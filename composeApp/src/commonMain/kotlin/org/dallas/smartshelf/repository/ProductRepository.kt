package org.dallas.smartshelf.repository

import kotlinx.coroutines.flow.Flow
import org.dallas.smartshelf.model.Product
import dev.gitlive.firebase.firestore.FirebaseFirestore
import dev.gitlive.firebase.firestore.where
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.dallas.smartshelf.manager.FirebaseAuthManager
import org.dallas.smartshelf.model.ProductCategory

interface ProductRepository {
    suspend fun saveProduct(product: Product): Result<Product>
    suspend fun getProducts(): Flow<List<Product>>
    suspend fun updateProduct(product: Product): Result<Product>
    suspend fun deleteProduct(productId: String): Result<Unit>
    suspend fun getProductByBarcode(barcode: String): Result<Product?>
}

class ProductRepositoryImpl(
    private val firestore: FirebaseFirestore,
    private val authManager: FirebaseAuthManager
) : ProductRepository {

    private val productsCollection get() = firestore.collection("products")

    override suspend fun saveProduct(product: Product): Result<Product> {
        return try {
            val currentUser = authManager.getCurrentUser()
                ?: return Result.failure(Exception("User not authenticated"))

            // Ensure the product has the current user ID
            val productToSave = if (product.userId == currentUser.userId) {
                product
            } else {
                product.copy(userId = currentUser.userId)
            }

            // Update lastModified to current time
            val updatedProduct = productToSave.copy(
                lastModified = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
            )

            // Convert product to map
            val productData = mapOf(
                "id" to updatedProduct.id,
                "barcode" to updatedProduct.barcode,
                "name" to updatedProduct.name,
                "quantity" to updatedProduct.quantity,
                "purchaseDate" to updatedProduct.purchaseDate.toString(),
                "expiryDate" to updatedProduct.expiryDate?.toString(),
                "category" to updatedProduct.category.name,
                "lastModified" to updatedProduct.lastModified.toString(),
                "userId" to updatedProduct.userId
            )

            productsCollection.document(updatedProduct.id).set(productData)

            Napier.d(tag = "ProductRepository", message = "Product saved successfully: ${updatedProduct.id}")
            Result.success(updatedProduct)
        } catch (e: Exception) {
            Napier.e(tag = "ProductRepository", message = "Error saving product: ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun getProducts(): Flow<List<Product>> {
        val currentUser = authManager.getCurrentUser()
            ?: throw Exception("User not authenticated")

        return productsCollection
            .where("userId", "==", currentUser.userId)
            .snapshots
            .map { snapshot ->
                snapshot.documents.mapNotNull { document ->
                    try {
                        val data = document.data<Map<String, Any>>()

                        // Parse dates
                        val purchaseDateStr = data["purchaseDate"] as? String
                            ?: return@mapNotNull null
                        val expiryDateStr = data["expiryDate"] as? String
                        val lastModifiedStr = data["lastModified"] as? String
                            ?: return@mapNotNull null

                        Product(
                            id = data["id"] as? String ?: return@mapNotNull null,
                            barcode = data["barcode"] as? String,
                            name = data["name"] as? String ?: return@mapNotNull null,
                            quantity = (data["quantity"] as? Number)?.toInt() ?: 0,
                            purchaseDate = LocalDateTime.parse(purchaseDateStr),
                            expiryDate = expiryDateStr?.let { LocalDateTime.parse(it) },
                            category = ProductCategory.valueOf(
                                (data["category"] as? String) ?: ProductCategory.OTHER.name
                            ),
                            lastModified = LocalDateTime.parse(lastModifiedStr),
                            userId = data["userId"] as? String ?: return@mapNotNull null
                        )
                    } catch (e: Exception) {
                        Napier.e(tag = "ProductRepository", message = "Error parsing product: ${e.message}")
                        null
                    }
                }
            }
    }

    override suspend fun updateProduct(product: Product): Result<Product> {
        return try {
            val currentUser = authManager.getCurrentUser()
                ?: return Result.failure(Exception("User not authenticated"))

            // Ensure the product belongs to the current user
            if (product.userId != currentUser.userId) {
                return Result.failure(Exception("Cannot update a product that doesn't belong to the current user"))
            }

            // Update lastModified to current time
            val updatedProduct = product.copy(
                lastModified = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
            )

            // Convert product to map
            val productData = mapOf(
                "id" to updatedProduct.id,
                "barcode" to updatedProduct.barcode,
                "name" to updatedProduct.name,
                "quantity" to updatedProduct.quantity,
                "purchaseDate" to updatedProduct.purchaseDate.toString(),
                "expiryDate" to updatedProduct.expiryDate?.toString(),
                "category" to updatedProduct.category.name,
                "lastModified" to updatedProduct.lastModified.toString(),
                "userId" to updatedProduct.userId
            )

            productsCollection.document(updatedProduct.id).update(productData)

            Napier.d(tag = "ProductRepository", message = "Product updated successfully: ${updatedProduct.id}")
            Result.success(updatedProduct)
        } catch (e: Exception) {
            Napier.e(tag = "ProductRepository", message = "Error updating product: ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun deleteProduct(productId: String): Result<Unit> {
        return try {
            val currentUser = authManager.getCurrentUser()
                ?: return Result.failure(Exception("User not authenticated"))

            // Verify the product belongs to the current user
            val productDoc = productsCollection.document(productId).get()
            val productData = productDoc.data<Map<String, Any>>()
            val productUserId = productData["userId"] as? String

            if (productUserId != currentUser.userId) {
                return Result.failure(Exception("Cannot delete a product that doesn't belong to the current user"))
            }

            productsCollection.document(productId).delete()

            Napier.d(tag = "ProductRepository", message = "Product deleted successfully: $productId")
            Result.success(Unit)
        } catch (e: Exception) {
            Napier.e(tag = "ProductRepository", message = "Error deleting product: ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun getProductByBarcode(barcode: String): Result<Product?> {
        return try {
            val currentUser = authManager.getCurrentUser()
                ?: return Result.failure(Exception("User not authenticated"))

            val snapshot = productsCollection
                .where("userId", "==", currentUser.userId)
                .where("barcode", "==", barcode)
                .get()

            val product = snapshot.documents.firstOrNull()?.let { document ->
                try {
                    val data = document.data<Map<String, Any>>()

                    // Parse dates
                    val purchaseDateStr = data["purchaseDate"] as? String
                        ?: return@let null
                    val expiryDateStr = data["expiryDate"] as? String
                    val lastModifiedStr = data["lastModified"] as? String
                        ?: return@let null

                    Product(
                        id = data["id"] as? String ?: return@let null,
                        barcode = data["barcode"] as? String,
                        name = data["name"] as? String ?: return@let null,
                        quantity = (data["quantity"] as? Number)?.toInt() ?: 0,
                        purchaseDate = LocalDateTime.parse(purchaseDateStr),
                        expiryDate = expiryDateStr?.let { LocalDateTime.parse(it) },
                        category = ProductCategory.valueOf(
                            (data["category"] as? String) ?: ProductCategory.OTHER.name
                        ),
                        lastModified = LocalDateTime.parse(lastModifiedStr),
                        userId = data["userId"] as? String ?: return@let null
                    )
                } catch (e: Exception) {
                    Napier.e(tag = "ProductRepository", message = "Error parsing product: ${e.message}")
                    null
                }
            }

            Napier.d(tag = "ProductRepository", message = "Product by barcode query successful: $barcode")
            Result.success(product)
        } catch (e: Exception) {
            Napier.e(tag = "ProductRepository", message = "Error getting product by barcode: ${e.message}")
            Result.failure(e)
        }
    }
}