package org.dallas.smartshelf.manager

import android.content.Context

actual object SharedPreferencesManagerFactory {
    private lateinit var appContext: Context

    fun initialize(context: Context) {
        appContext = context.applicationContext
    }

    actual fun createSharedPreferencesManager(): SharedPreferencesManager =
        AndroidSharedPreferencesManager(appContext)
}