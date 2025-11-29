package com.margelo.nitro.schedulednotifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.content.ContextCompat
import com.margelo.nitro.NitroModules
import com.margelo.nitro.schedulednotifications.HybridScheduledNotificationsSpec
import com.margelo.nitro.schedulednotifications.ScheduleNotificationReceiver
import java.util.Calendar
import java.util.UUID

object HybridScheduledNotifications: HybridScheduledNotificationsSpec() {
    private val listeners: MutableMap<String, (String) -> Unit> = mutableMapOf()
    override fun schedule(
        id: String,
        title: String,
        message: String,
        frequency: ScheduleFrequency,
        scheduleDate: ScheduleDateBase
    ) {
        val ctx = NitroModules.applicationContext
        Log.d("SCHEDULE","PASSOU CONTEXTO")
        if(ctx  == null) return;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(ctx, android.Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                Log.w("HybridNotifications", "⚠️ Permissão não concedida. Solicite via UI.")
                return
            }
        }
        Log.d("SCHEDULE","PASSOU CONTEXTO")
        createDailyNotification(ctx,id,title,message,frequency,scheduleDate)
    }

    private fun createDailyNotification(context:Context,id: String, title: String, message: String, frequency: ScheduleFrequency,
                                        scheduleDate: ScheduleDateBase) {
        val intent = Intent(context, ScheduleNotificationReceiver::class.java).apply {
            putExtra("id", id)
            putExtra("title", title)
            putExtra("message", message)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            id.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val calendar = Calendar.getInstance().apply {
            if(scheduleDate.dayOfMonth != null) {
                set(Calendar.DAY_OF_MONTH,scheduleDate.dayOfMonth.toInt())
            }
            if(scheduleDate.dayOfWeek != null) {
                set(Calendar.DAY_OF_WEEK,scheduleDate.dayOfWeek.toInt())
            }
            set(Calendar.HOUR_OF_DAY, scheduleDate.hour.toInt())
            set(Calendar.MINUTE, scheduleDate.minute.toInt())
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)

            if (before(Calendar.getInstance())) {
                add(Calendar.DAY_OF_MONTH, 1)
            }
        }


        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        alarmManager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )


        Log.i("HybridNotifications", "⏱️ Notificação agendada com sucesso!${calendar}")
    }


    override fun addListener(callback: (String) -> Unit): String {
        val identifier = UUID.randomUUID().toString()
        listeners[identifier] = callback
        return identifier
    }

    override fun removeListener(identifier: String) {
        listeners.remove(identifier)
    }

    fun handleNotificationPress(id: String) {
        listeners.values.forEach { callback -> callback(id) }
    }

    override fun cancel(id: String) {
        val ctx = NitroModules.applicationContext
        val intent = Intent(ctx, ScheduleNotificationReceiver::class.java)
        val pending = PendingIntent.getBroadcast(
            ctx,
            id.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val alarm = ctx?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarm.cancel(pending)
    }
}
