package com.r.dosc.domain.models

import android.net.Uri
import androidx.compose.ui.geometry.Offset

data class ImageEditDetails(
    val index: Int,
    var circleOne: Offset,
    var circleTwo: Offset,
    var circleThree: Offset,
    var circleFour: Offset,
    var imgUri: Uri,
    var isEdited: Boolean = false
)
