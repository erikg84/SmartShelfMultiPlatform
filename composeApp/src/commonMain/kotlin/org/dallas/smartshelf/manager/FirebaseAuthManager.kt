package org.dallas.smartshelf.manager

import dev.gitlive.firebase.auth.FirebaseAuth
import dev.gitlive.firebase.auth.FirebaseAuthException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.dallas.smartshelf.model.User

class FirebaseAuthManager(
    private val auth: FirebaseAuth
) {

    /**
     * Sign up a new user with email and password
     */
    suspend fun signUp(email: String, password: String): Result<User> {
        return try {
            val authResult = auth.createUserWithEmailAndPassword(email, password)
            val user = authResult.user
            Result.success(
                User(
                    userId = user?.uid.orEmpty(),
                    email = user?.email.orEmpty(),
                    displayName = user?.displayName.orEmpty()
                )
            )
        } catch (e: FirebaseAuthException) {
            Result.failure(e)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Sign in with email and password
     */
    suspend fun signIn(email: String, password: String): Result<User> {
        return try {
            val authResult = auth.signInWithEmailAndPassword(email, password)
            val user = authResult.user
            Result.success(
                User(
                    userId = user?.uid.orEmpty(),
                    email = user?.email.orEmpty(),
                    displayName = user?.displayName.orEmpty()
                )
            )
        } catch (e: FirebaseAuthException) {
            Result.failure(e)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Send a password reset email
     */
    suspend fun sendPasswordReset(email: String): Result<Unit> {
        return try {
            auth.sendPasswordResetEmail(email)
            Result.success(Unit)
        } catch (e: FirebaseAuthException) {
            Result.failure(e)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Delete the current user account
     */
    suspend fun deleteAccount(): Result<Unit> {
        return try {
            val user = auth.currentUser
                ?: return Result.failure(Exception("No user signed in"))
            user.delete()
            Result.success(Unit)
        } catch (e: FirebaseAuthException) {
            Result.failure(e)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Sign out the current user
     */
    suspend fun signOut() {
        auth.signOut()
    }

    /**
     * Get the current signed-in user
     */
    suspend fun getCurrentUser(): User? {
        return auth.currentUser?.let { user ->
            User(
                userId = user.uid,
                email = user.email ?: "",
                displayName = user.displayName ?: ""
            )
        }
    }

    /**
     * Check if a user is currently signed in
     */
    suspend fun isUserSignedIn(): Boolean {
        return auth.currentUser != null
    }

    /**
     * Get a flow of the current authentication state
     */
    fun authStateFlow(): Flow<Boolean> {
        return auth.authStateChanged.map { it != null }
    }
}