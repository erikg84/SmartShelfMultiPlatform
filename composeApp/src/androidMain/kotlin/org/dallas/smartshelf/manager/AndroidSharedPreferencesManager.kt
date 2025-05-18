package org.dallas.smartshelf.manager

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class AndroidSharedPreferencesManager(
    private val context: Context
) : SharedPreferencesManager {

    private val prefs: SharedPreferences by lazy {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    override fun getLanguageCode(): String? {
        return prefs.getString(KEY_LANGUAGE_CODE, null)
    }

    override fun setLanguageCode(languageCode: String) {
        prefs.edit { putString(KEY_LANGUAGE_CODE, languageCode) }
    }

    override fun clearLanguageCode() {
        prefs.edit { remove(KEY_LANGUAGE_CODE) }
    }

    override fun getString(key: String, defaultValue: String?): String? {
        return prefs.getString(key, defaultValue)
    }

    override fun putString(key: String, value: String) {
        prefs.edit { putString(key, value) }
    }

    override fun getBoolean(key: String, defaultValue: Boolean): Boolean {
        return prefs.getBoolean(key, defaultValue)
    }

    override fun putBoolean(key: String, value: Boolean) {
        prefs.edit { putBoolean(key, value) }
    }

    override fun getInt(key: String, defaultValue: Int): Int {
        return prefs.getInt(key, defaultValue)
    }

    override fun putInt(key: String, value: Int) {
        prefs.edit { putInt(key, value) }
    }

    override fun getLong(key: String, defaultValue: Long): Long {
        return prefs.getLong(key, defaultValue)
    }

    override fun putLong(key: String, value: Long) {
        prefs.edit { putLong(key, value) }
    }

    override fun remove(key: String) {
        prefs.edit { remove(key) }
    }

    override fun clear() {
        prefs.edit { clear() }
    }

    companion object {
        private const val PREFS_NAME = "SmartShelfPrefs"
        private const val KEY_LANGUAGE_CODE = "language_code"
    }
}