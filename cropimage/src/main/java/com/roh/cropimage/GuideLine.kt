package com.roh.cropimage

import androidx.compose.ui.geometry.Offset

data class GuideLine(
    val start: Offset = Offset(0.0f, 0.0f),
    val end: Offset = Offset(0.0f, 0.0f)
)