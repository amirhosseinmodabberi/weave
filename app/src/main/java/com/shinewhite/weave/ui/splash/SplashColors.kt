package com.shinewhite.weave.ui.splash

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import com.shinewhite.weave.ui.theme.LocalDarkTheme

@Immutable
data class SplashPalette(
    val background: Color,
    val logo: Color
)

object SplashColors {
    val LightBackground = Color(0xFFF5F3FF)
    val LightLogo = Color(0xFF6366F1)
    val DarkBackground = Color(0xFF202022)
    val DarkLogo = Color(0xFFA5B4FC)
}

@Composable
fun splashPalette(darkTheme: Boolean = LocalDarkTheme.current): SplashPalette =
    if (darkTheme) {
        SplashPalette(background = SplashColors.DarkBackground, logo = SplashColors.DarkLogo)
    } else {
        SplashPalette(background = SplashColors.LightBackground, logo = SplashColors.LightLogo)
    }