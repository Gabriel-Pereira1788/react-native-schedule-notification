package com.margelo.nitro.schedulednotifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.margelo.nitro.NitroModules

class NotificationClickReceiver : BroadcastReceiver() {


    override fun onReceive(context: Context?, intent: Intent?) {

        val id = intent?.getStringExtra("id") ?: return

        Log.d("CLICKER-RECEICER","CLICKED")
         HybridScheduledNotifications.handleNotificationPress(id)

    }
}
