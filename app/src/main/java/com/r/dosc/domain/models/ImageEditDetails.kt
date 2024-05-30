package com.r.dosc.domain.models

import android.net.Uri
import com.roh.cropimage.IRect

data class ImageEditDetails(
    val index: Int,
    var imgUri: Uri,
    var isEdited: Boolean = false,
    var iRect: IRect
)
