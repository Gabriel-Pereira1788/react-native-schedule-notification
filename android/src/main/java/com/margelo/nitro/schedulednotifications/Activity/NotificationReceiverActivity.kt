package com.margelo.nitro.schedulednotifications.Activity

import android.app.Activity
import android.os.Bundle
import com.margelo.nitro.schedulednotifications.HybridScheduledNotifications


class NotificationReceiverActivity: Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val intent = getIntent()

        if (intent != null && intent.getExtras() != null){
            val id = intent.getStringExtra("id")

            if(id != null) {
                HybridScheduledNotifications.handleNotificationPress(id)
            }
        }
    }

}