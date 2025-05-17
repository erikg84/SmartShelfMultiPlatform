package org.dallas.smartshelf.util

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.initialize

actual object FirebaseInitializer {
    actual fun initialize() {
        Firebase.initialize()
    }
}