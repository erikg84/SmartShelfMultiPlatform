package org.dallas.smartshelf.util

import platform.Foundation.NSUserDefaults

class IosTokenStorage : TokenStorage {
    private val userDefaults = NSUserDefaults.standardUserDefaults

    override fun saveAccessToken(token: String) {
        userDefaults.setObject(token, "access_token")
        // For a more secure implementation, consider using the Keychain
    }

    override fun getAccessToken(): String? {
        return userDefaults.stringForKey("access_token")
    }

    override fun saveRefreshToken(token: String) {
        userDefaults.setObject(token, "refresh_token")
    }

    override fun getRefreshToken(): String? {
        return userDefaults.stringForKey("refresh_token")
    }

    override fun clearTokens() {
        userDefaults.removeObjectForKey("access_token")
        userDefaults.removeObjectForKey("refresh_token")
    }
}