package com.shinewhite.weave.ui.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Stable
import kotlinx.coroutines.delay

// Timeline: 0.0-0.2s background, 0.2-3.0s writing, 3.0-3.5s hold, 3.5-4.0s fade out
internal object SplashTimings {
    const val BACKGROUND_FADE_MS = 200
    const val DRAW_MS = 2_800
    const val HOLD_MS = 500L
    const val FADE_OUT_MS = 500
}

private val HandwritingEasing = CubicBezierEasing(0.37f, 0f, 0.63f, 1f)

@Stable
internal class SplashAnimationState {
    val backgroundAlpha = Animatable(0f)
    val drawProgress = Animatable(0f)
    val overallAlpha = Animatable(1f)

    suspend fun play(onFadeOutStart: () -> Unit) {
        backgroundAlpha.animateTo(1f, tween(SplashTimings.BACKGROUND_FADE_MS, easing = LinearEasing))
        drawProgress.animateTo(1f, tween(SplashTimings.DRAW_MS, easing = HandwritingEasing))
        delay(SplashTimings.HOLD_MS)
        onFadeOutStart()
        overallAlpha.animateTo(0f, tween(SplashTimings.FADE_OUT_MS, easing = LinearEasing))
    }
}