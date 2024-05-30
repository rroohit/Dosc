package com.roh.cropimage

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size

data class IRect(
    val topLeft: Offset = Offset(0.0f, 0.0f),
    var size: Size = Size(0.0f, 0.0f)
)