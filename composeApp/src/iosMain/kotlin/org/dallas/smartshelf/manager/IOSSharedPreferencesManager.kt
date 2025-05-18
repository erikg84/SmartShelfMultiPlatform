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

    override fun getString(key: String, defaultValue: String?): String? {
        val value = userDefaults.stringForKey(key)
        return value ?: defaultValue
    }

    override fun putString(key: String, value: String) {
        userDefaults.setObject(value, forKey = key)
        userDefaults.synchronize()
    }

    override fun getBoolean(key: String, defaultValue: Boolean): Boolean {
        return userDefaults.boolForKey(key) ?: defaultValue
    }

    override fun putBoolean(key: String, value: Boolean) {
        userDefaults.setBool(value, forKey = key)
        userDefaults.synchronize()
    }

    override fun getInt(key: String, defaultValue: Int): Int {
        // NSUserDefaults doesn't have a direct intForKey method that returns nullable
        // We need to check if the object exists first
        val number = userDefaults.objectForKey(key)
        return if (number != null) {
            userDefaults.integerForKey(key).toInt()
        } else {
            defaultValue
        }
    }

    override fun putInt(key: String, value: Int) {
        userDefaults.setInteger(value.toLong(), forKey = key)
        userDefaults.synchronize()
    }

    override fun getLong(key: String, defaultValue: Long): Long {
        val number = userDefaults.objectForKey(key)
        return if (number != null) {
            userDefaults.integerForKey(key)
        } else {
            defaultValue
        }
    }

    override fun putLong(key: String, value: Long) {
        userDefaults.setInteger(value, forKey = key)
        userDefaults.synchronize()
    }

    override fun remove(key: String) {
        userDefaults.removeObjectForKey(key)
        userDefaults.synchronize()
    }

    override fun clear() {
        // Get all keys registered in this app's standard user defaults
        val dictionary = userDefaults.dictionaryRepresentation()
        for (key in dictionary.keys) {
            if (key is String) {
                userDefaults.removeObjectForKey(key)
            }
        }
        userDefaults.synchronize()
    }

    companion object {
        private const val KEY_LANGUAGE_CODE = "language_code"
    }
}