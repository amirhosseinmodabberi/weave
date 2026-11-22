package com.shinewhite.weave.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

val PrimaryIndigo = Color(0xFF6366F1)
val SoftIndigo = Color(0xFF818CF8)
val LightLavender = Color(0xFFA5B4FC)
val LavenderSurface = Color(0xFFEEF2FF)
val DeepIndigo = Color(0xFF4F46E5)
val InkIndigo = Color(0xFF1E1B4B)
val SlateText = Color(0xFF64748B)
val SuccessGreen = Color(0xFF10B981)
val WarningAmber = Color(0xFFF59E0B)

val LightColorScheme: ColorScheme = lightColorScheme(
    primary = DeepIndigo,
    onPrimary = Color.White,
    primaryContainer = LavenderSurface,
    onPrimaryContainer = InkIndigo,
    secondary = PrimaryIndigo,
    onSecondary = Color.White,
    secondaryContainer = LightLavender,
    onSecondaryContainer = InkIndigo,
    tertiary = SuccessGreen,
    onTertiary = InkIndigo,
    background = Color(0xFFF8FAFC),
    onBackground = InkIndigo,
    surface = Color.White,
    onSurface = InkIndigo,
    surfaceVariant = LavenderSurface,
    onSurfaceVariant = SlateText,
    outline = Color(0xFFCBD5E1),
    outlineVariant = Color(0xFFE2E8F0),
)

val DarkColorScheme: ColorScheme = darkColorScheme(
    primary = DeepIndigo,
    onPrimary = Color.White,
    primaryContainer = Color(0xFF25235A),
    onPrimaryContainer = LavenderSurface,
    secondary = SoftIndigo,
    onSecondary = Color(0xFF0F0E24),
    secondaryContainer = Color(0xFF25235A),
    onSecondaryContainer = LavenderSurface,
    tertiary = SuccessGreen,
    onTertiary = Color(0xFF0F0E24),
    background = Color(0xFF0F0E24),
    onBackground = LavenderSurface,
    surface = Color(0xFF1B1A3D),
    onSurface = LavenderSurface,
    surfaceVariant = Color(0xFF25235A),
    onSurfaceVariant = LightLavender,
    outline = Color(0xFF6B6AA8),
    outlineVariant = Color(0xFF34326B),
)