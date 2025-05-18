package org.dallas.smartshelf.manager

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.Serializable
import org.dallas.smartshelf.model.User

class JwtAuthManager(
    private val httpClient: HttpClient,
    private val sharedPreferencesManager: SharedPreferencesManager
) {
    // Base URL - ideally should be configurable
    private val baseUrl = "http://localhost:8080/api"

    private val _authStateFlow = MutableStateFlow<Boolean>(false)
    val authStateFlow: StateFlow<Boolean> = _authStateFlow

    // Load tokens from secure storage on initialization
    init {
        val savedToken = sharedPreferencesManager.getString(ACCESS_TOKEN_KEY, null)

        if (savedToken != null) {
            _authStateFlow.value = true
        }
    }

    @Serializable
    data class LoginRequest(val username: String, val password: String)

    @Serializable
    data class RegisterRequest(
        val username: String,
        val email: String,
        val password: String
    )

    @Serializable
    data class AuthResponse(
        val token: String,
        val refreshToken: String? = null,
        val username: String,
        val expiresIn: Long
    )

    @Serializable
    data class ErrorResponse(val message: String)

    // Sign up a new user
    suspend fun signUp(username: String, email: String, password: String): Result<User> {
        return try {
            val response = httpClient.post("$baseUrl/auth/register") {
                contentType(ContentType.Application.Json)
                setBody(RegisterRequest(username, email, password))
            }

            if (response.status.isSuccess()) {
                // Return a user object
                Result.success(User(
                    userId = "", // Server doesn't return this yet
                    username = username,
                    email = email,
                    displayName = username
                ))
            } else {
                val errorResponse = response.body<ErrorResponse>()
                Result.failure(Exception(errorResponse.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Sign in with username and password
    suspend fun signIn(username: String, password: String): Result<User> {
        return try {
            val response = httpClient.post("$baseUrl/auth/login") {
                contentType(ContentType.Application.Json)
                setBody(LoginRequest(username, password))
            }

            if (response.status.isSuccess()) {
                val authResponse = response.body<AuthResponse>()

                // Store tokens securely
                sharedPreferencesManager.putString(ACCESS_TOKEN_KEY, authResponse.token)
                authResponse.refreshToken?.let {
                    sharedPreferencesManager.putString(REFRESH_TOKEN_KEY, it)
                }

                // Update auth state
                _authStateFlow.value = true

                Result.success(User(
                    userId = "", // Server doesn't return this yet
                    username = authResponse.username,
                    email = "", // Server doesn't return this yet
                    displayName = authResponse.username
                ))
            } else {
                val errorResponse = response.body<ErrorResponse>()
                Result.failure(Exception(errorResponse.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Send a password reset email
    suspend fun sendPasswordReset(email: String): Result<Unit> {
        return try {
            val response = httpClient.post("$baseUrl/auth/password-reset") {
                contentType(ContentType.Application.Json)
                setBody(mapOf("email" to email))
            }

            if (response.status.isSuccess()) {
                Result.success(Unit)
            } else {
                val errorResponse = response.body<ErrorResponse>()
                Result.failure(Exception(errorResponse.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Delete the current user account
    suspend fun deleteAccount(): Result<Unit> {
        return try {
            val token = getAccessToken() ?: return Result.failure(Exception("Not authenticated"))

            val response = httpClient.delete("$baseUrl/auth/account") {
                headers {
                    append("Authorization", "Bearer $token")
                }
            }

            if (response.status.isSuccess()) {
                // Clear tokens and update state
                clearTokens()
                _authStateFlow.value = false

                Result.success(Unit)
            } else {
                val errorResponse = response.body<ErrorResponse>()
                Result.failure(Exception(errorResponse.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Sign out the current user
    suspend fun signOut() {
        // Clear tokens and update state
        clearTokens()
        _authStateFlow.value = false
    }

    // Check if a user is currently signed in
    suspend fun isUserSignedIn(): Boolean {
        return getAccessToken() != null
    }

    // Get the current signed-in user
    suspend fun getCurrentUser(): User? {
        // For a full implementation, you might want to call an endpoint to get user details
        return if (isUserSignedIn()) {
            // Here you would make a call to get the user profile
            // For now, return a minimal user object
            User(
                userId = "",
                username = "",
                email = "",
                displayName = ""
            )
        } else {
            null
        }
    }

    // Refresh the token
    suspend fun refreshToken(): Result<String> {
        return try {
            val currentRefreshToken = getRefreshToken()
                ?: return Result.failure(Exception("No refresh token available"))

            val response = httpClient.post("$baseUrl/auth/refresh-token") {
                contentType(ContentType.Application.Json)
                setBody(mapOf("refreshToken" to currentRefreshToken))
            }

            if (response.status.isSuccess()) {
                val authResponse = response.body<AuthResponse>()

                // Store the new tokens
                sharedPreferencesManager.putString(ACCESS_TOKEN_KEY, authResponse.token)
                authResponse.refreshToken?.let {
                    sharedPreferencesManager.putString(REFRESH_TOKEN_KEY, it)
                }

                Result.success(authResponse.token)
            } else {
                val errorResponse = response.body<ErrorResponse>()
                Result.failure(Exception(errorResponse.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Helper methods for token management

    fun getAccessToken(): String? {
        return sharedPreferencesManager.getString(ACCESS_TOKEN_KEY, null)
    }

    private fun getRefreshToken(): String? {
        return sharedPreferencesManager.getString(REFRESH_TOKEN_KEY, null)
    }

    private fun clearTokens() {
        sharedPreferencesManager.remove(ACCESS_TOKEN_KEY)
        sharedPreferencesManager.remove(REFRESH_TOKEN_KEY)
    }

    companion object {
        private const val ACCESS_TOKEN_KEY = "jwt_access_token"
        private const val REFRESH_TOKEN_KEY = "jwt_refresh_token"
    }
}