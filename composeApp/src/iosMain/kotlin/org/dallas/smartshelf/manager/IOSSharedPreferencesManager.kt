package org.dallas.smartshelf.manager

import platform.Foundation.NSUserDefaults

class IOSSharedPreferencesManager : SharedPreferencesManager {

    private val userDefaults = NSUserDefaults.standardUserDefaults

    override fun getLanguageCode(): String? {
        return userDefaults.stringForKey(KEY_LANGUAGE_CODE)
    }

    override fun setLanguageCode(languageCode: String) {
        userDefaults.setObject(languageCode, forKey = KEY_LANGUAGE_CODE)
        userDefaults.synchronize()
    }

    override fun clearLanguageCode() {
        userDefaults.removeObjectForKey(KEY_LANGUAGE_CODE)
        userDefaults.synchronize()
    }

    companion object {
        private const val KEY_LANGUAGE_CODE = "language_code"
    }
}