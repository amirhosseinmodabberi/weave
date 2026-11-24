package com.shinewhite.weave.ui.splash

import android.content.res.Configuration
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

private const val LOGO_WIDTH_FRACTION = 0.62f
private const val LOGO_MAX_WIDTH_DP = 420
private const val LOGO_STROKE_DP = 1.8f

@Composable
fun SplashScreen(
    animationState: SplashAnimationState,
    onFinished: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentOnFinished by rememberUpdatedState(onFinished)

    // Keyed on the remembered state, so ordinary recompositions never restart the animation
    LaunchedEffect(animationState) {
        animationState.play()
        currentOnFinished()
    }

    SplashContent(
        backgroundAlpha = { animationState.backgroundAlpha.value },
        drawProgress = { animationState.drawProgress.value },
        palette = splashPalette(),
        modifier = modifier.blurredFade(
            alpha = { 1f - animationState.transition.value },
            blurFraction = { animationState.transition.value }
        )
    )
}

@Composable
internal fun SplashContent(
    backgroundAlpha: () -> Float,
    drawProgress: () -> Float,
    palette: SplashPalette,
    modifier: Modifier = Modifier
) {
    val pathMeasure = remember {
        PathMeasure().apply { setPath(WeaveLogoPath.create(), false) }
    }
    val totalLength = remember(pathMeasure) { pathMeasure.length }
    val revealedPath = remember { Path() }

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .semantics { contentDescription = "Weave" }
    ) {
        drawRect(color = palette.background.copy(alpha = backgroundAlpha().coerceIn(0f, 1f)))

        val logoWidth = minOf(size.width * LOGO_WIDTH_FRACTION, LOGO_MAX_WIDTH_DP.dp.toPx())
        val scale = logoWidth / WeaveLogoPath.WIDTH
        val offsetX = (size.width - WeaveLogoPath.WIDTH * scale) / 2f
        val offsetY = (size.height - WeaveLogoPath.HEIGHT * scale) / 2f

        revealedPath.reset()
        pathMeasure.getSegment(0f, totalLength * drawProgress().coerceIn(0f, 1f), revealedPath, true)

        withTransform({
            translate(offsetX, offsetY)
            scale(scale, scale, pivot = Offset.Zero)
        }) {
            drawPath(
                path = revealedPath,
                color = palette.logo,
                style = Stroke(
                    width = LOGO_STROKE_DP.dp.toPx() / scale,
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )
            )
        }
    }
}

@Preview(name = "Light", widthDp = 360, heightDp = 640)
@Preview(name = "Dark", widthDp = 360, heightDp = 640, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun SplashScreenCompletePreview() {
    SplashContent(
        backgroundAlpha = { 1f },
        drawProgress = { 1f },
        palette = splashPalette()
    )
}

@Preview(name = "Writing", widthDp = 360, heightDp = 640)
@Composable
private fun SplashScreenWritingPreview() {
    SplashContent(
        backgroundAlpha = { 1f },
        drawProgress = { 0.55f },
        palette = splashPalette()
    )
}