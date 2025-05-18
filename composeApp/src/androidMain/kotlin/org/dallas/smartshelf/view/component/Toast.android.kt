package org.dallas.smartshelf.view.component

import android.widget.Toast
import org.dallas.smartshelf.SmartShelfApplication

actual fun showToast(message: String) {
    val context = SmartShelfApplication.getAppContext()
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}