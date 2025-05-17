package org.dallas.smartshelf.manager

import org.dallas.smartshelf.util.LocaleProviderFactory

class LocaleManager(
    private val sharedPreferencesManager: SharedPreferencesManager
) {
    private val provider = LocaleProviderFactory.createLocaleProvider(sharedPreferencesManager)

    fun getCurrentLocale(): String {
        return provider.getCurrentLocale()
    }

    fun setLocale(languageCode: String) {
        provider.setLocale(languageCode)
    }

    fun resetLocale() {
        provider.resetLocale()
    }
}