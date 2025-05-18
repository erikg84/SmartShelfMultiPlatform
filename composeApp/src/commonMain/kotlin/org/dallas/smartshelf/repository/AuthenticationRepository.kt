package org.dallas.smartshelf.repository

import org.dallas.smartshelf.manager.JwtAuthManager
import org.dallas.smartshelf.model.User

class AuthenticationRepository(
    private val jwtAuthManager: JwtAuthManager
) {
    /**
     * Login with username and password
     * @return Result containing User on success or Exception on failure
     */
    suspend fun login(username: String, password: String): Result<User> {
        return try {
            jwtAuthManager.signIn(username, password)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Register a new user with username, email and password
     * @return Result containing User on success or Exception on failure
     */
    suspend fun register(username: String, email: String, password: String): Result<User> {
        return try {
            jwtAuthManager.signUp(username, email, password)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Send password reset email
     * @return Result containing Unit on success or Exception on failure
     */
    suspend fun sendPasswordReset(email: String): Result<Unit> {
        return try {
            jwtAuthManager.sendPasswordReset(email)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Delete the current user account
     * @return Result containing Unit on success or Exception on failure
     */
    suspend fun deleteAccount(): Result<Unit> {
        return try {
            jwtAuthManager.deleteAccount()
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Sign out the current user
     */
    suspend fun signOut() {
        jwtAuthManager.signOut()
    }

    /**
     * Check if a user is currently signed in
     * @return true if user is signed in, false otherwise
     */
    suspend fun isUserSignedIn(): Boolean {
        return jwtAuthManager.isUserSignedIn()
    }

    /**
     * Get the current signed-in user
     * @return User object or null if no user is signed in
     */
    suspend fun getCurrentUser(): User? {
        return jwtAuthManager.getCurrentUser()
    }

    fun getAccessToken(): String? {
        return jwtAuthManager.getAccessToken()
    }
}