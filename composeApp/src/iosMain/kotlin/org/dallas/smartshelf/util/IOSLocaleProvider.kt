package org.dallas.smartshelf.util

import org.dallas.smartshelf.manager.SharedPreferencesManager
import platform.Foundation.NSLocale
import platform.Foundation.NSUserDefaults
import platform.Foundation.preferredLanguages

class IOSLocaleProvider(
    private val sharedPreferencesManager: SharedPreferencesManager
) : LocaleProvider {

    override fun getCurrentLocale(): String {
        return sharedPreferencesManager.getLanguageCode() ?: getSystemLanguage()
    }

    override fun setLocale(languageCode: String) {
        sharedPreferencesManager.setLanguageCode(languageCode)

        // Setting the locale for iOS apps typically involves more complex steps
        // 1. Save the preference
        NSUserDefaults.standardUserDefaults.setObject(
            languageCode,
            forKey = "AppleLanguages"
        )
        NSUserDefaults.standardUserDefaults.synchronize()

        // 2. Note: For iOS, you often need to restart the app to apply language changes
        // This would typically be handled in the UI layer with an alert
    }

    override fun resetLocale() {
        // Remove the saved preference
        NSUserDefaults.standardUserDefaults.removeObjectForKey("AppleLanguages")
        NSUserDefaults.standardUserDefaults.synchronize()

        // Same note about restarting applies here
    }

    private fun getSystemLanguage(): String {
        // Get the device language
        val languages = NSLocale.preferredLanguages
        return if (languages.isEmpty()) {
            "en" // Default to English
        } else {
            languages[0] as String
        }
    }
}