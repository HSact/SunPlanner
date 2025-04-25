package com.hsact.sunplanner.ui.settings

enum class ThemeMode
{ LIGHT, DARK, SYSTEM }

fun ThemeMode.toIndex(): Int = when (this) {
    ThemeMode.SYSTEM -> 0
    ThemeMode.LIGHT -> 1
    ThemeMode.DARK -> 2
}

fun indexToThemeMode(index: Int): ThemeMode = when (index) {
    0 -> ThemeMode.SYSTEM
    1 -> ThemeMode.LIGHT
    2 -> ThemeMode.DARK
    else -> ThemeMode.SYSTEM
}