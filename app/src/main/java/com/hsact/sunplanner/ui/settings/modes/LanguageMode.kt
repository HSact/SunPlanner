package com.hsact.sunplanner.ui.settings.modes

import com.hsact.sunplanner.ui.settings.modes.LanguageMode.ENGLISH
import com.hsact.sunplanner.ui.settings.modes.LanguageMode.RUSSIAN
import java.util.Locale

enum class LanguageMode {
    ENGLISH, RUSSIAN;

    fun toIndex(): Int = when (this) {
        ENGLISH -> 0
        RUSSIAN -> 1
    }

    fun toName(): String = when (this) {
        ENGLISH -> "en"
        RUSSIAN -> "ru"
    }

    fun toLocale(): Locale = Locale(this.toName())
}

fun indexToLanguageMode(index: Int): LanguageMode = when (index) {
    0 -> ENGLISH
    1 -> RUSSIAN
    else -> ENGLISH
}

fun nameToLanguageMode(name: String): LanguageMode = when (name) {
    "en" -> ENGLISH
    "ru" -> RUSSIAN
    else -> ENGLISH
}