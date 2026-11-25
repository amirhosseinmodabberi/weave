package com.shinewhite.weave.settings

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

private const val PREFS_NAME = "weave_settings"
private const val KEY_LANGUAGE = "language"
private const val KEY_DARK_THEME = "dark_theme"

@Singleton
class SettingsRepository @Inject constructor(
    @ApplicationContext context: Context
) {
    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val _settings = MutableStateFlow(readSettings())
    val settings: StateFlow<AppSettings> = _settings.asStateFlow()

    fun setLanguage(language: AppLanguage) {
        prefs.edit().putString(KEY_LANGUAGE, language.tag).apply()
        _settings.value = _settings.value.copy(language = language)
    }

    fun setDarkTheme(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_DARK_THEME, enabled).apply()
        _settings.value = _settings.value.copy(darkThemeOverride = enabled)
    }

    private fun readSettings(): AppSettings = AppSettings(
        language = AppLanguage.fromTag(prefs.getString(KEY_LANGUAGE, null)),
        darkThemeOverride = if (prefs.contains(KEY_DARK_THEME)) prefs.getBoolean(KEY_DARK_THEME, false) else null
    )

    companion object {
        // Used by code that runs without Hilt, like the reminder receiver
        fun readLanguage(context: Context): AppLanguage =
            AppLanguage.fromTag(
                context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).getString(KEY_LANGUAGE, null)
            )
    }
}