package com.shinewhite.weave.ui.splash

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlurEffect
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp

internal object SplashTransitionSpec {
    const val DURATION_MS = 700
    val MaxBlur = 14.dp
    val Easing = CubicBezierEasing(0.45f, 0f, 0.55f, 1f)
}

// Alpha and blur are read inside the layer block, so animating them never recomposes the content
// The blur needs Android 12+, older versions simply get the fade without the blur
fun Modifier.blurredFade(alpha: () -> Float, blurFraction: () -> Float): Modifier =
    graphicsLayer {
        this.alpha = alpha().coerceIn(0f, 1f)
        val radius = SplashTransitionSpec.MaxBlur.toPx() * blurFraction().coerceIn(0f, 1f)
        renderEffect = if (radius > 0.5f) BlurEffect(radius, radius, TileMode.Clamp) else null
    }