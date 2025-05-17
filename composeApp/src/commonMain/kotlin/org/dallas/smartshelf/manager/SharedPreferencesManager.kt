package org.dallas.smartshelf.manager

interface SharedPreferencesManager {
    fun getLanguageCode(): String?
    fun setLanguageCode(languageCode: String)
    fun clearLanguageCode()
}
