package org.dallas.smartshelf.util

expect object LocaleProviderFactory {
    fun createLocaleProvider(sharedPreferencesManager: org.dallas.smartshelf.manager.SharedPreferencesManager): LocaleProvider
}