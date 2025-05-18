package org.dallas.smartshelf.util

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import androidx.core.content.edit

class AndroidTokenStorage(private val context: Context) : TokenStorage {
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val sharedPreferences = EncryptedSharedPreferences.create(
        context,
        "auth_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    override fun saveAccessToken(token: String) {
        sharedPreferences.edit { putString("access_token", token) }
    }

    override fun getAccessToken(): String? {
        return sharedPreferences.getString("access_token", null)
    }

    override fun saveRefreshToken(token: String) {
        sharedPreferences.edit { putString("refresh_token", token) }
    }

    override fun getRefreshToken(): String? {
        return sharedPreferences.getString("refresh_token", null)
    }

    override fun clearTokens() {
        sharedPreferences.edit {
            remove("access_token")
                .remove("refresh_token")
        }
    }
}