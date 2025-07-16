package com.hsact.sunplanner.ui.utils

import android.content.Context
import android.content.ContextWrapper
import android.content.res.Configuration
import java.util.Locale

/**
 * Utility object that provides a localized [ContextWrapper].
 *
 * This is used to apply a specific [Locale] to an existing [Context],
 * allowing for dynamic language changes without restarting the entire application.
 */
object LocalizedContextWrapper {
    /**
     * Wraps the given [context] with a new [Configuration] based on the provided [locale].
     *
     * @param context The original context.
     * @param locale The target locale to apply.
     * @return A [ContextWrapper] using the updated locale configuration.
     */
    fun wrap(context: Context, locale: Locale): ContextWrapper {
        val config = Configuration(context.resources.configuration)
        val newContext = context.createConfigurationContext(config)
        config.setLocale(locale)
        return ContextWrapper(newContext)
    }
}