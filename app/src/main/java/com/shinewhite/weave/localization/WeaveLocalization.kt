package com.shinewhite.weave.localization

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import com.shinewhite.weave.settings.AppLanguage
import com.shinewhite.weave.ui.theme.typographyFor

@Composable
fun WeaveLocalization(language: AppLanguage, content: @Composable () -> Unit) {
    val layoutDirection = if (language == AppLanguage.PERSIAN) LayoutDirection.Rtl else LayoutDirection.Ltr

    CompositionLocalProvider(
        LocalStrings provides stringsFor(language),
        LocalLayoutDirection provides layoutDirection
    ) {
        // Keeps the colors and shapes of the app theme and only swaps the font for the chosen language
        MaterialTheme(
            colorScheme = MaterialTheme.colorScheme,
            shapes = MaterialTheme.shapes,
            typography = typographyFor(language),
            content = content
        )
    }
}