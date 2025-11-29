package com.margelo.nitro.schedulednotifications.Utils

import android.content.Context

class HandlerLaunchActivity private constructor() {
    fun getMainActivityClass(context: Context): Class<*>? {
        val name = context.getPackageName()
        val launchIntent =
            context.getPackageManager().getLaunchIntentForPackage(name)
        val className = launchIntent!!.getComponent()!!.getClassName()

        return getClassForName(className)
    }

    private fun getClassForName(className: String?): Class<*>? {
        try {
            return Class.forName(className)
        } catch (e: ClassNotFoundException) {
            return null
        }
    }

    companion object {
        var instance: HandlerLaunchActivity = HandlerLaunchActivity()
    }
}