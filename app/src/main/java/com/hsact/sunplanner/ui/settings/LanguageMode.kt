package com.hsact.sunplanner.ui.settings

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

fun indexToLanguageMode(index: Int): LanguageMode = when (index) {
    0 -> LanguageMode.ENGLISH
    1 -> LanguageMode.RUSSIAN
    else -> LanguageMode.ENGLISH
}