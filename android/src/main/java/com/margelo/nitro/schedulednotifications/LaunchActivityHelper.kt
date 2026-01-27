package com.margelo.nitro.schedulednotifications

import android.content.Context

object LaunchActivityHelper {
    fun getMainActivityClass(context: Context): Class<*>? {
        val packageName = context.packageName
        val launchIntent = context.packageManager.getLaunchIntentForPackage(packageName)
        val className = launchIntent?.component?.className ?: return null
        return try {
            Class.forName(className)
        } catch (e: ClassNotFoundException) {
            null
        }
    }
}
