package org.dallas.smartshelf.view.component

import platform.UIKit.UIAlertController
import platform.UIKit.UIAlertControllerStyleAlert
import platform.UIKit.UIApplication
import platform.Foundation.NSTimer
import kotlinx.cinterop.ExperimentalForeignApi

@OptIn(ExperimentalForeignApi::class)
actual fun showToast(message: String) {
    val alertController = UIAlertController.alertControllerWithTitle(
        title = null,
        message = message,
        preferredStyle = UIAlertControllerStyleAlert
    )

    val keyWindow = UIApplication.sharedApplication.keyWindow
    val rootViewController = keyWindow?.rootViewController

    rootViewController?.presentViewController(
        alertController,
        animated = true,
        completion = null
    )

    NSTimer.scheduledTimerWithTimeInterval(
        interval = 1.5,
        false
    ) { _ ->
        alertController.dismissViewControllerAnimated(true, null)
    }
}