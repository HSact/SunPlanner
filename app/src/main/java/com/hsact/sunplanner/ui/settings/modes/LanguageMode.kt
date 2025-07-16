package com.hsact.sunplanner.ui.settings.modes

import com.hsact.sunplanner.ui.settings.modes.LanguageMode.ENGLISH
import com.hsact.sunplanner.ui.settings.modes.LanguageMode.RUSSIAN
import java.util.Locale

/**
 * Enum representing supported language modes.
 */
enum class LanguageMode {
    ENGLISH, RUSSIAN;

    /**
     * Returns the index corresponding to the language mode.
     *
     * @return Int index (0 for English, 1 for Russian).
     */
    fun toIndex(): Int = when (this) {
        ENGLISH -> 0
        RUSSIAN -> 1
    }

    /**
     * Returns the language code string for the language mode.
     *
     * @return String language code ("en" or "ru").
     */
    fun toName(): String = when (this) {
        ENGLISH -> "en"
        RUSSIAN -> "ru"
    }

    /**
     * Returns the Java [Locale] object corresponding to this language mode.
     *
     * Note: uses deprecated [Locale] constructor.
     *
     * @return Locale for the language code.
     */
    @Suppress("DEPRECATION")
    fun toLocale(): Locale = Locale(this.toName())
}

/**
 * Converts an index to the corresponding [LanguageMode].
 *
 * Defaults to English if index is invalid.
 *
 * @param index Int index.
 * @return Corresponding LanguageMode.
 */
fun indexToLanguageMode(index: Int): LanguageMode = when (index) {
    0 -> ENGLISH
    1 -> RUSSIAN
    else -> ENGLISH
}

/**
 * Converts a language code string to the corresponding [LanguageMode].
 *
 * Defaults to English if the code is unrecognized.
 *
 * @param name Language code string ("en" or "ru").
 * @return Corresponding LanguageMode.
 */
fun nameToLanguageMode(name: String): LanguageMode = when (name) {
    "en" -> ENGLISH
    "ru" -> RUSSIAN
    else -> ENGLISH
}