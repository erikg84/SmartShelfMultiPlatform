package org.dallas.smartshelf.util

interface LocaleProvider {
    fun getCurrentLocale(): String
    fun setLocale(languageCode: String)
    fun resetLocale()
}