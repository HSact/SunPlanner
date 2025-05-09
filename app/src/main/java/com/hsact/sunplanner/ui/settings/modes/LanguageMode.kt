package com.hsact.sunplanner.ui.settings.modes

import java.util.Locale

enum class LanguageMode
{ENGLISH, RUSSIAN}

fun LanguageMode.toIndex(): Int = when (this) {
    LanguageMode.ENGLISH -> 0
    LanguageMode.RUSSIAN -> 1
}
fun LanguageMode.toName(): String = when (this) {
    LanguageMode.ENGLISH -> "en"
    LanguageMode.RUSSIAN -> "ru"
}

fun LanguageMode.toLocale(): Locale = Locale(this.toName())

fun indexToLanguageMode(index: Int): LanguageMode = when (index) {
    0 -> LanguageMode.ENGLISH
    1 -> LanguageMode.RUSSIAN
    else -> LanguageMode.ENGLISH
}

fun nameToLanguageMode(name: String): LanguageMode = when (name) {
    "en" -> LanguageMode.ENGLISH
    "ru" -> LanguageMode.RUSSIAN
    else -> LanguageMode.ENGLISH
}