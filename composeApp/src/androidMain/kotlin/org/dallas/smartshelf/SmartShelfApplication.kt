package org.dallas.smartshelf

import android.app.Application
import org.dallas.smartshelf.di.initKoin
import org.dallas.smartshelf.manager.SharedPreferencesManagerFactory
import org.dallas.smartshelf.util.FirebaseInitializer

class SmartShelfApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this
        SharedPreferencesManagerFactory.initialize(this)
        FirebaseInitializer.setup(this)
        initKoin()
        FirebaseInitializer.initialize()
    }

    override fun getApplicationContext(): SmartShelfApplication {
        return this
    }

    companion object {
        private lateinit var instance: SmartShelfApplication

        fun getAppContext(): SmartShelfApplication {
            if (!::instance.isInitialized) {
                throw IllegalStateException("Application context not initialized")
            }
            return instance
        }

        fun isInitialized(): Boolean {
            return ::instance.isInitialized
        }
    }
}