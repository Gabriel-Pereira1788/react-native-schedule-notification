package com.margelo.nitro.schedulednotifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.margelo.nitro.schedulednotifications.Activity.NotificationReceiverActivity
import java.util.Calendar
import java.util.concurrent.TimeUnit

class NotificationWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    companion object {
        const val TAG_PREFIX = "notification_work_"
        private const val KEY_ID = "id"
        private const val KEY_TITLE = "title"
        private const val KEY_MESSAGE = "message"
        private const val KEY_FREQUENCY = "frequency"
        private const val KEY_HOUR = "hour"
        private const val KEY_MINUTE = "minute"
        private const val KEY_DAY_OF_WEEK = "dayOfWeek"
        private const val KEY_DAY_OF_MONTH = "dayOfMonth"

        fun calculateInitialDelay(
            frequency: String,
            hour: Int,
            minute: Int,
            dayOfWeek: Int?,
            dayOfMonth: Int?
        ): Long {
            val now = Calendar.getInstance()
            val target = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, hour)
                set(Calendar.MINUTE, minute)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)

                when (frequency) {
                    "daily" -> if (before(now)) add(Calendar.DAY_OF_MONTH, 1)
                    "weekly" -> {
                        dayOfWeek?.let { set(Calendar.DAY_OF_WEEK, it) }
                        if (before(now)) add(Calendar.WEEK_OF_YEAR, 1)
                    }
                    "monthly" -> {
                        dayOfMonth?.let { set(Calendar.DAY_OF_MONTH, it) }
                        if (before(now)) add(Calendar.MONTH, 1)
                    }
                }
            }
            return target.timeInMillis - now.timeInMillis
        }

        fun buildInputData(
            id: String,
            title: String,
            message: String,
            frequency: String,
            hour: Int,
            minute: Int,
            dayOfWeek: Int?,
            dayOfMonth: Int?
        ): Data = Data.Builder()
            .putString(KEY_ID, id)
            .putString(KEY_TITLE, title)
            .putString(KEY_MESSAGE, message)
            .putString(KEY_FREQUENCY, frequency)
            .putInt(KEY_HOUR, hour)
            .putInt(KEY_MINUTE, minute)
            .apply {
                dayOfWeek?.let { putInt(KEY_DAY_OF_WEEK, it) }
                dayOfMonth?.let { putInt(KEY_DAY_OF_MONTH, it) }
            }
            .build()
    }

    override suspend fun doWork(): Result {
        val id = inputData.getString(KEY_ID) ?: return Result.failure()
        val title = inputData.getString(KEY_TITLE) ?: return Result.failure()
        val message = inputData.getString(KEY_MESSAGE) ?: return Result.failure()
        val frequency = inputData.getString(KEY_FREQUENCY) ?: return Result.failure()
        val hour = inputData.getInt(KEY_HOUR, -1).takeIf { it != -1 } ?: return Result.failure()
        val minute = inputData.getInt(KEY_MINUTE, -1).takeIf { it != -1 } ?: return Result.failure()
        val dayOfWeek = inputData.getInt(KEY_DAY_OF_WEEK, -1).takeIf { it != -1 }
        val dayOfMonth = inputData.getInt(KEY_DAY_OF_MONTH, -1).takeIf { it != -1 }

        showNotification(id, title, message)
        scheduleNext(id, title, message, frequency, hour, minute, dayOfWeek, dayOfMonth)

        Log.i("NotificationWorker", "Notification displayed for id: $id")
        return Result.success()
    }

    private fun showNotification(id: String, title: String, message: String) {
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(id, "Scheduled Notifications", NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }

        val pendingIntent = buildPendingIntent(id)

        val notification = NotificationCompat.Builder(applicationContext, id)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(android.R.drawable.ic_popup_reminder)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(id.hashCode(), notification)
    }

    private fun buildPendingIntent(notificationId: String): PendingIntent? {
        val launchActivityClass = LaunchActivityHelper.getMainActivityClass(applicationContext) ?: return null

        val launchIntent = Intent(applicationContext, launchActivityClass).apply {
            putExtra("notification_action", "PRESS_ACTION")
        }

        val receiverIntent = Intent(applicationContext, NotificationReceiverActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            putExtra("id", notificationId)
        }

        return PendingIntent.getActivities(
            applicationContext,
            notificationId.hashCode(),
            arrayOf(launchIntent, receiverIntent),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun scheduleNext(
        id: String,
        title: String,
        message: String,
        frequency: String,
        hour: Int,
        minute: Int,
        dayOfWeek: Int?,
        dayOfMonth: Int?
    ) {
        val delay = calculateInitialDelay(frequency, hour, minute, dayOfWeek, dayOfMonth)
        val data = buildInputData(id, title, message, frequency, hour, minute, dayOfWeek, dayOfMonth)

        val workRequest = OneTimeWorkRequestBuilder<NotificationWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setInputData(data)
            .addTag("$TAG_PREFIX$id")
            .build()

        WorkManager.getInstance(applicationContext)
            .enqueueUniqueWork("$TAG_PREFIX$id", ExistingWorkPolicy.REPLACE, workRequest)
    }
}
