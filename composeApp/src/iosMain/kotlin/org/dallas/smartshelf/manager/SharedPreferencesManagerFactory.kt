package org.dallas.smartshelf.manager

actual object SharedPreferencesManagerFactory {
    actual fun createSharedPreferencesManager(): SharedPreferencesManager =
        IOSSharedPreferencesManager()
}
