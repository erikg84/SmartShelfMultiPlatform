package org.dallas.smartshelf

import android.app.Application
import org.dallas.smartshelf.di.initKoin
import org.dallas.smartshelf.manager.SharedPreferencesManagerFactory
import org.dallas.smartshelf.util.FirebaseInitializer

class SmartShelfApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        SharedPreferencesManagerFactory.initialize(this)
        FirebaseInitializer.setup(this)
        initKoin()
        FirebaseInitializer.initialize()
    }
}