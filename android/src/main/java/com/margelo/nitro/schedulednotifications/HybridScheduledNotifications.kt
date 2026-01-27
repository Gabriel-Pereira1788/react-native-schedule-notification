package com.margelo.nitro.schedulednotifications

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.margelo.nitro.NitroModules
import com.margelo.nitro.core.Promise
import com.margelo.nitro.schedulednotifications.Activity.PermissionRequesterActivity
import java.util.UUID
import java.util.concurrent.TimeUnit

object HybridScheduledNotifications : HybridScheduledNotificationsSpec() {
    private val listeners: MutableMap<String, (String) -> Unit> = mutableMapOf()
    private var storage: NotificationStorage? = null

    private fun getStorage(context: Context): NotificationStorage {
        if (storage == null) {
            storage = NotificationStorage(context)
        }
        return storage!!
    }

    private fun hasNotificationPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    override fun schedule(
        id: String,
        title: String,
        message: String,
        frequency: ScheduleFrequency,
        scheduleDate: ScheduleDateBase
    ) {
        val ctx = NitroModules.applicationContext ?: return

        if (hasNotificationPermission(ctx)) {
            scheduleNotificationInternal(ctx, id, title, message, frequency, scheduleDate)
        } else {
            val intent = PermissionRequesterActivity.createIntent(
                ctx,
                id,
                title,
                message,
                frequency,
                scheduleDate
            )
            ctx.startActivity(intent)
        }
    }

    fun scheduleWithPermission(
        context: Context,
        id: String,
        title: String,
        message: String,
        frequency: ScheduleFrequency,
        scheduleDate: ScheduleDateBase
    ) {
        scheduleNotificationInternal(context, id, title, message, frequency, scheduleDate)
    }

    private fun scheduleNotificationInternal(
        context: Context,
        id: String,
        title: String,
        message: String,
        frequency: ScheduleFrequency,
        scheduleDate: ScheduleDateBase
    ) {
        val frequencyString = when (frequency) {
            ScheduleFrequency.DAILY -> "daily"
            ScheduleFrequency.WEEKLY -> "weekly"
            ScheduleFrequency.MONTHLY -> "monthly"
        }

        val hour = scheduleDate.hour.toInt()
        val minute = scheduleDate.minute.toInt()
        val dayOfWeek = scheduleDate.dayOfWeek?.toInt()
        val dayOfMonth = scheduleDate.dayOfMonth?.toInt()

        val delay = NotificationWorker.calculateInitialDelay(
            frequencyString,
            hour,
            minute,
            dayOfWeek,
            dayOfMonth
        )

        val inputData = NotificationWorker.buildInputData(
            id,
            title,
            message,
            frequencyString,
            hour,
            minute,
            dayOfWeek,
            dayOfMonth
        )

        val workRequest = OneTimeWorkRequestBuilder<NotificationWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setInputData(inputData)
            .addTag("${NotificationWorker.TAG_PREFIX}$id")
            .build()

        WorkManager.getInstance(context)
            .enqueueUniqueWork(
                "${NotificationWorker.TAG_PREFIX}$id",
                ExistingWorkPolicy.REPLACE,
                workRequest
            )

        getStorage(context).save(
            NotificationData(
                id = id,
                title = title,
                message = message,
                frequency = frequencyString,
                hour = hour,
                minute = minute,
                dayOfWeek = dayOfWeek,
                dayOfMonth = dayOfMonth
            )
        )

        Log.i("HybridNotifications", "Notification scheduled successfully for id: $id, delay: ${delay}ms")
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
        val ctx = NitroModules.applicationContext ?: return

        WorkManager.getInstance(ctx)
            .cancelUniqueWork("${NotificationWorker.TAG_PREFIX}$id")

        getStorage(ctx).remove(id)

        Log.i("HybridNotifications", "Notification cancelled for id: $id")
    }

    override fun getPendingNotifications(): Promise<Array<String>> {
        return Promise.async {
            val ctx = NitroModules.applicationContext
                ?: return@async emptyArray()

            val storedIds = getStorage(ctx).getAllIds()
            val workManager = WorkManager.getInstance(ctx)
            val pendingIds = mutableListOf<String>()

            for (id in storedIds) {
                val workName = "${NotificationWorker.TAG_PREFIX}$id"
                val workInfos = workManager.getWorkInfosForUniqueWork(workName).get()

                val isActive = workInfos.any { workInfo ->
                    workInfo.state == WorkInfo.State.ENQUEUED ||
                    workInfo.state == WorkInfo.State.RUNNING ||
                    workInfo.state == WorkInfo.State.BLOCKED
                }

                if (isActive) {
                    pendingIds.add(id)
                } else {
                    getStorage(ctx).remove(id)
                }
            }

            pendingIds.toTypedArray()
        }
    }
}
