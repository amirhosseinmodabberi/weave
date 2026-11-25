package com.shinewhite.weave

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.shinewhite.weave.localization.WeaveLocalization
import com.shinewhite.weave.ui.navigation.WeaveApp
import com.shinewhite.weave.ui.screens.SettingsViewModel
import com.shinewhite.weave.ui.theme.AppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val settingsViewModel: SettingsViewModel = viewModel()
            val settings by settingsViewModel.settings.collectAsStateWithLifecycle()
            val darkTheme = settings.darkThemeOverride ?: isSystemInDarkTheme()

            // System bar icons must follow the theme chosen in the app, not the system theme
            DisposableEffect(darkTheme) {
                enableEdgeToEdge(
                    statusBarStyle = SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT) { darkTheme },
                    navigationBarStyle = SystemBarStyle.auto(
                        Color.argb(0xe6, 0xFF, 0xFF, 0xFF),
                        Color.argb(0x80, 0x1b, 0x1b, 0x1b)
                    ) { darkTheme }
                )
                onDispose { }
            }

            AppTheme(darkTheme = darkTheme) {
                WeaveLocalization(language = settings.language) {
                    WeaveApp()
                }
            }
        }
    }
}