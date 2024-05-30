package com.r.dosc.presentation.scanning.components

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.r.dosc.domain.models.ImageEditDetails
import com.roh.cropimage.IRect
import com.roh.cropimage.InItCropView

@Composable
fun CropImageView(
    bitmap: Bitmap?,
    imageEditDetails: ImageEditDetails?,
    onCropEdgesChange: (IRect) -> Unit,
    indexSelectedImage: Int,
    cropRectSize: (IntSize) -> Unit
) {

    val showProgressBarState = remember { mutableStateOf(true) }
    val isEdgesUpdated = remember { mutableStateOf(true) }
    val index = remember { mutableIntStateOf(0) }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .onSizeChanged {
                cropRectSize(it)
            },
        contentAlignment = Alignment.Center
    ) {

        bitmap?.let {
            val cropView = InItCropView(bitmap)
            cropView.ImageCropView(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 8.dp, end = 8.dp, bottom = 8.dp),
                onDragStart = {
                    isEdgesUpdated.value = false
                },
                onCropEdgesChange = { iRect ->
                    onCropEdgesChange(iRect)
                }
            )
            showProgressBarState.value = false
            imageEditDetails?.let {
                if ((it.isEdited && isEdgesUpdated.value) ||
                    (index.intValue != indexSelectedImage)
                ) {
                    cropView.updateCropRect(
                        it.iRect
                    )
                }

                if ((!it.isEdited && isEdgesUpdated.value) ||
                    (!it.isEdited && index.intValue != indexSelectedImage)
                ) {
                    cropView.resetView()
                }

                index.intValue = it.index
            }

        }.run {
            AnimatedVisibility(visible = showProgressBarState.value) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }
    }
}