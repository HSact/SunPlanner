package com.hsact.sunplanner.ui.settings

import android.content.Context
import android.content.ContextWrapper
import android.content.res.Configuration
import java.util.Locale

object LocalizedContextWrapper {
    fun wrap(context: Context, locale: Locale): ContextWrapper {
        var newContext = context
        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)
        newContext = context.createConfigurationContext(config)
        return ContextWrapper(newContext)
    }
}