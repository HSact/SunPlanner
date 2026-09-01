package com.hsact.sunplanner.domain.model

/**
 * Enum representing available theme modes.
 */
enum class ThemeMode {
    LIGHT, DARK, SYSTEM;

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
    fun toIndex(): Int = when (this) {
        SYSTEM -> 0
        LIGHT -> 1
        DARK -> 2
    }

    companion object {
        /**
         * Converts an index to the corresponding [ThemeMode].
         *
         * Defaults to [ThemeMode.SYSTEM] if the index is invalid.
         *
         * @param index Int index to convert.
         * @return Corresponding [ThemeMode].
         */
        fun fromIndex(index: Int): ThemeMode = when (index) {
            0 -> SYSTEM
            1 -> LIGHT
            2 -> DARK
            else -> SYSTEM
        }
    }
}
