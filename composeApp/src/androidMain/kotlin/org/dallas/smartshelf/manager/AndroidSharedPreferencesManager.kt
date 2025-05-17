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

    companion object {
        private const val PREFS_NAME = "SmartShelfPrefs"
        private const val KEY_LANGUAGE_CODE = "language_code"
    }
}