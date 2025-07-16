package com.hsact.sunplanner.ui.settings.modes

/**
 * Enum representing available theme modes.
 */
enum class ThemeMode { LIGHT, DARK, SYSTEM }

/**
 * Returns the index associated with the [ThemeMode].
 *
 * Index mapping:
 * - SYSTEM = 0
 * - LIGHT = 1
 * - DARK = 2
 *
 * @return Int index corresponding to the theme mode.
 */
fun ThemeMode.toIndex(): Int = when (this) {
    ThemeMode.SYSTEM -> 0
    ThemeMode.LIGHT -> 1
    ThemeMode.DARK -> 2
}

/**
 * Converts an index to the corresponding [ThemeMode].
 *
 * Defaults to [ThemeMode.SYSTEM] if the index is invalid.
 *
 * @param index Int index to convert.
 * @return Corresponding [ThemeMode].
 */
fun indexToThemeMode(index: Int): ThemeMode = when (index) {
    0 -> ThemeMode.SYSTEM
    1 -> ThemeMode.LIGHT
    2 -> ThemeMode.DARK
    else -> ThemeMode.SYSTEM
}