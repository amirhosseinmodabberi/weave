package com.shinewhite.weave.ui.splash

import androidx.compose.ui.graphics.Path

// One continuous pen stroke that writes "weave", drawn in a 448 x 89 coordinate space
object WeaveLogoPath {
    const val WIDTH = 448.1f
    const val HEIGHT = 89.4f

    fun create(): Path = Path().apply {
        moveTo(4f, 20f)
        cubicTo(6.9f, 4f, 22f, -2f, 25.5f, 12f)
        cubicTo(29.9f, 32f, 23.2f, 80f, 42.5f, 84f)
        cubicTo(61.8f, 88f, 70.4f, 40f, 76.7f, 16f)
        cubicTo(74.4f, 40f, 73.2f, 80f, 92.5f, 84f)
        cubicTo(111.8f, 88f, 125.9f, 32f, 130.4f, 18f)
        cubicTo(137.7f, 22f, 152.2f, 30f, 165.3f, 24f)
        cubicTo(174f, 20f, 170.9f, 4f, 152.9f, 4f)
        cubicTo(134.9f, 4f, 120.9f, 26f, 119.9f, 54f)
        cubicTo(119.6f, 78f, 137.8f, 88f, 155.6f, 78f)
        cubicTo(164.7f, 72f, 173.5f, 56f, 182.4f, 40f)
        cubicTo(192.6f, 28f, 211.1f, 14f, 223.1f, 14f)
        cubicTo(231.1f, 14f, 232.9f, 4f, 220.9f, 4f)
        cubicTo(204.9f, 4f, 182.4f, 18f, 177.3f, 46f)
        cubicTo(172.3f, 74f, 187.8f, 88f, 203.9f, 76f)
        cubicTo(213f, 70f, 219f, 48f, 223.3f, 24f)
        cubicTo(219f, 48f, 211.6f, 78f, 216.5f, 84f)
        cubicTo(225.4f, 90f, 237.9f, 76f, 247.9f, 54f)
        cubicTo(253.4f, 40f, 255.6f, 28f, 259.4f, 18f)
        cubicTo(261.8f, 10f, 267.5f, 12f, 266.3f, 24f)
        cubicTo(264f, 48f, 259.6f, 78f, 270.5f, 84f)
        cubicTo(281.8f, 88f, 296.4f, 40f, 304f, 20f)
        cubicTo(310.2f, 8f, 322.2f, 8f, 324f, 20f)
        cubicTo(331.3f, 24f, 345.9f, 32f, 358.9f, 26f)
        cubicTo(367.7f, 22f, 364.5f, 6f, 346.5f, 6f)
        cubicTo(328.5f, 6f, 314.6f, 28f, 313.5f, 56f)
        cubicTo(313.2f, 80f, 331.4f, 90f, 349.2f, 80f)
        cubicTo(369.4f, 68f, 396.8f, 38f, 444.1f, 42f)
    }
}