package org.dallas.smartshelf.util

import android.content.Context
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.initialize

actual object FirebaseInitializer {
    private lateinit var appContext: Context
    private var initialized = false

    fun setup(context: Context) {
        appContext = context.applicationContext
    }

    actual fun initialize() {
        if (!initialized && ::appContext.isInitialized) {
            Firebase.initialize(appContext)
            initialized = true
        }
    }
}