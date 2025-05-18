package org.dallas.smartshelf.repository

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import org.dallas.smartshelf.manager.JwtAuthManager
import org.dallas.smartshelf.util.PlatformContext

abstract class BaseRepository(
    protected val httpClient: HttpClient,
    protected val jwtAuthManager: JwtAuthManager,
    protected val platformContext: PlatformContext
) {
    protected val baseUrl = platformContext.getApiBaseUrl()

    /**
     * Add authorization header to the request
     */
    protected fun HttpRequestBuilder.authorizedRequest() {
        val token = jwtAuthManager.getAccessToken()
        if (token != null) {
            bearerAuth(token)
        }
    }

    /**
     * Handle API errors and token refresh
     */
    protected suspend fun <T> handleApiResponse(
        response: HttpResponse,
        transform: suspend (HttpResponse) -> T
    ): Result<T> {
        return try {
            if (response.status.isSuccess()) {
                Result.success(transform(response))
            } else {
                // Check if it's an auth error and try to refresh token
                if (response.status == HttpStatusCode.Unauthorized) {
                    val refreshResult = jwtAuthManager.refreshToken()
                    if (refreshResult.isSuccess) {
                        // Token refreshed, but the caller should retry the operation
                        return Result.failure(Exception("Token expired, please retry the operation"))
                    }
                }

                Result.failure(Exception("API error: ${response.status}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}