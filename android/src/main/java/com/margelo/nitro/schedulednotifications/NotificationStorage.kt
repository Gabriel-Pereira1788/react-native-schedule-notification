package com.margelo.nitro.schedulednotifications

import android.content.Context
import org.json.JSONObject

data class NotificationData(
    val id: String,
    val title: String,
    val message: String,
    val frequency: String,
    val hour: Int,
    val minute: Int,
    val dayOfWeek: Int?,
    val dayOfMonth: Int?
)

class NotificationStorage(context: Context) {
    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREFS_NAME = "scheduled_notifications_storage"
    }

    fun save(notification: NotificationData) {
        val json = JSONObject().apply {
            put("id", notification.id)
            put("title", notification.title)
            put("message", notification.message)
            put("frequency", notification.frequency)
            put("hour", notification.hour)
            put("minute", notification.minute)
            notification.dayOfWeek?.let { put("dayOfWeek", it) }
            notification.dayOfMonth?.let { put("dayOfMonth", it) }
        }
        prefs.edit().putString(notification.id, json.toString()).apply()
    }

    fun remove(id: String) {
        prefs.edit().remove(id).apply()
    }

    fun getAllIds(): List<String> {
        return prefs.all.keys.toList()
    }
}
