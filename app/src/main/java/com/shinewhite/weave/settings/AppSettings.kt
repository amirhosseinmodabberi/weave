package com.shinewhite.weave.settings

enum class AppLanguage(val tag: String) {
    ENGLISH("en"),
    PERSIAN("fa");

    companion object {
        fun fromTag(tag: String?): AppLanguage = entries.firstOrNull { it.tag == tag } ?: ENGLISH
    }
}

data class AppSettings(
    val language: AppLanguage = AppLanguage.ENGLISH,
    // null means the app follows the system theme until the user picks one
    val darkThemeOverride: Boolean? = null
)