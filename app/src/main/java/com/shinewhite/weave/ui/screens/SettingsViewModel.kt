package com.shinewhite.weave.ui.screens

import androidx.lifecycle.ViewModel
import com.shinewhite.weave.settings.AppLanguage
import com.shinewhite.weave.settings.AppSettings
import com.shinewhite.weave.settings.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.StateFlow

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {
    val settings: StateFlow<AppSettings> = settingsRepository.settings

    fun setLanguage(language: AppLanguage) = settingsRepository.setLanguage(language)

    fun setDarkTheme(enabled: Boolean) = settingsRepository.setDarkTheme(enabled)
}