package org.dallas.smartshelf.manager

expect object SharedPreferencesManagerFactory {
    fun createSharedPreferencesManager(): SharedPreferencesManager
}