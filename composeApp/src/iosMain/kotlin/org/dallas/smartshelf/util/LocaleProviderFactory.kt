package org.dallas.smartshelf.util

import org.dallas.smartshelf.manager.SharedPreferencesManager

actual object LocaleProviderFactory {
    actual fun createLocaleProvider(sharedPreferencesManager: SharedPreferencesManager): LocaleProvider =
        IOSLocaleProvider(sharedPreferencesManager)
}