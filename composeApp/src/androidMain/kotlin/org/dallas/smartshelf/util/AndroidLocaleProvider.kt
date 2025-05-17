package org.dallas.smartshelf.util

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import org.dallas.smartshelf.manager.SharedPreferencesManager
import java.util.Locale

class AndroidLocaleProvider(
    private val sharedPreferencesManager: SharedPreferencesManager
) : LocaleProvider {

    override fun getCurrentLocale(): String {
        return sharedPreferencesManager.getLanguageCode() ?: Locale.getDefault().language
    }

    override fun setLocale(languageCode: String) {
        sharedPreferencesManager.setLanguageCode(languageCode)
        AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(languageCode))
    }

    override fun resetLocale() {
        val systemDefaultLanguage = Locale.getDefault().language
        AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(systemDefaultLanguage))
    }
}