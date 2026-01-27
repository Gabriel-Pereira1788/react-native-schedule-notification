package com.margelo.nitro.schedulednotifications.Activity

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.margelo.nitro.schedulednotifications.HybridScheduledNotifications
import com.margelo.nitro.schedulednotifications.ScheduleDateBase
import com.margelo.nitro.schedulednotifications.ScheduleFrequency

class PermissionRequesterActivity : ComponentActivity() {

    companion object {
        private const val EXTRA_ID = "notification_id"
        private const val EXTRA_TITLE = "notification_title"
        private const val EXTRA_MESSAGE = "notification_message"
        private const val EXTRA_FREQUENCY = "notification_frequency"
        private const val EXTRA_HOUR = "notification_hour"
        private const val EXTRA_MINUTE = "notification_minute"
        private const val EXTRA_DAY_OF_WEEK = "notification_day_of_week"
        private const val EXTRA_DAY_OF_MONTH = "notification_day_of_month"

        fun createIntent(
            context: Context,
            id: String,
            title: String,
            message: String,
            frequency: ScheduleFrequency,
            scheduleDate: ScheduleDateBase
        ): Intent {
            return Intent(context, PermissionRequesterActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                putExtra(EXTRA_ID, id)
                putExtra(EXTRA_TITLE, title)
                putExtra(EXTRA_MESSAGE, message)
                putExtra(EXTRA_FREQUENCY, frequency.ordinal)
                putExtra(EXTRA_HOUR, scheduleDate.hour)
                putExtra(EXTRA_MINUTE, scheduleDate.minute)
                scheduleDate.dayOfWeek?.let { putExtra(EXTRA_DAY_OF_WEEK, it) }
                scheduleDate.dayOfMonth?.let { putExtra(EXTRA_DAY_OF_MONTH, it) }
            }
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            scheduleNotification()
        }
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> {
                    scheduleNotification()
                    finish()
                }
                else -> {
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        } else {
            scheduleNotification()
            finish()
        }
    }

    private fun scheduleNotification() {
        val id = intent.getStringExtra(EXTRA_ID) ?: return
        val title = intent.getStringExtra(EXTRA_TITLE) ?: return
        val message = intent.getStringExtra(EXTRA_MESSAGE) ?: return
        val frequencyOrdinal = intent.getIntExtra(EXTRA_FREQUENCY, -1)
        if (frequencyOrdinal == -1) return

        val frequency = ScheduleFrequency.values()[frequencyOrdinal]
        val hour = intent.getDoubleExtra(EXTRA_HOUR, -1.0)
        val minute = intent.getDoubleExtra(EXTRA_MINUTE, -1.0)
        if (hour == -1.0 || minute == -1.0) return

        val dayOfWeek = if (intent.hasExtra(EXTRA_DAY_OF_WEEK)) {
            intent.getDoubleExtra(EXTRA_DAY_OF_WEEK, -1.0).takeIf { it != -1.0 }
        } else null

        val dayOfMonth = if (intent.hasExtra(EXTRA_DAY_OF_MONTH)) {
            intent.getDoubleExtra(EXTRA_DAY_OF_MONTH, -1.0).takeIf { it != -1.0 }
        } else null

        val scheduleDate = ScheduleDateBase(
            hour = hour,
            minute = minute,
            dayOfWeek = dayOfWeek,
            dayOfMonth = dayOfMonth
        )

        HybridScheduledNotifications.scheduleWithPermission(
            this,
            id,
            title,
            message,
            frequency,
            scheduleDate
        )
    }
}
