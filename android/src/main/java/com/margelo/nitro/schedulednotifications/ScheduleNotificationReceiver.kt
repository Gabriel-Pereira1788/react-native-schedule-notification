package com.margelo.nitro.schedulednotifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.margelo.nitro.schedulednotifications.Activity.NotificationReceiverActivity
import com.margelo.nitro.schedulednotifications.Utils.HandlerLaunchActivity


class ScheduleNotificationReceiver: BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        val title = intent?.getStringExtra("title") ?: ""
        val message = intent?.getStringExtra("message") ?: ""
        val id = intent?.getStringExtra("id") ?: "notification_id"

        val notificationManager = context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Android 8+ precisa de channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                id,
                "Daily Notifications",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        val launchActivityClass = HandlerLaunchActivity.instance.getMainActivityClass(context)
        val pendingIntent = buildPendingIntent(context,launchActivityClass,id)

        val notification = NotificationCompat.Builder(context, id)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(android.R.drawable.ic_popup_reminder)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .build()
        notificationManager.notify(id.hashCode(), notification)
    }

    private fun buildPendingIntent(
        context: Context?,
        launchActivityClass: Class<*>?,
        notificationId: String
    ): PendingIntent? {
        val launchIntent = Intent(context, launchActivityClass)
        launchIntent.putExtra("notification_action", "PRESS_ACTION")

        val receiverIntent = Intent(context, NotificationReceiverActivity::class.java)
        receiverIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        receiverIntent.putExtra("id", notificationId)

        val intents = arrayOfNulls<Intent>(2)
        intents[0] = launchIntent
        intents[1] = receiverIntent

        return PendingIntent.getActivities(
            context,
            notificationId.hashCode(),
            intents,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}