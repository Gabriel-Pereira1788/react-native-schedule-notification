package com.margelo.nitro.schedulednotifications.Activity

import android.app.Activity
import android.os.Bundle
import com.margelo.nitro.schedulednotifications.HybridScheduledNotifications

class NotificationReceiverActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        intent?.getStringExtra("id")?.let {
            HybridScheduledNotifications.handleNotificationPress(it)
        }
        finish()
    }
}
