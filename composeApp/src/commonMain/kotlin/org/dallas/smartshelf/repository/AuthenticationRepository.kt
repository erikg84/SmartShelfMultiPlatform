package org.dallas.smartshelf.repository

import org.dallas.smartshelf.manager.FirebaseAuthManager
import org.dallas.smartshelf.model.User

class AuthenticationRepository(
    private val firebaseAuthManager: FirebaseAuthManager
) {
    /**
     * Login with email and password
     * @return Result containing User on success or Exception on failure
     */
    suspend fun login(email: String, password: String): Result<User> {
        return try {
            firebaseAuthManager.signIn(email, password)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Register a new user with email and password
     * @return Result containing User on success or Exception on failure
     */
    suspend fun register(email: String, password: String): Result<User> {
        return try {
            firebaseAuthManager.signUp(email, password)
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
            firebaseAuthManager.sendPasswordReset(email)
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
            firebaseAuthManager.deleteAccount()
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Sign out the current user
     */
    suspend fun signOut() {
        firebaseAuthManager.signOut()
    }

    /**
     * Check if a user is currently signed in
     * @return true if user is signed in, false otherwise
     */
    suspend fun isUserSignedIn(): Boolean {
        return firebaseAuthManager.isUserSignedIn()
    }

    /**
     * Get the current signed-in user
     * @return User object or null if no user is signed in
     */
    suspend fun getCurrentUser(): User? {
        return firebaseAuthManager.getCurrentUser()
    }
}