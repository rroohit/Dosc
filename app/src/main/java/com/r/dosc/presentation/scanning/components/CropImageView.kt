package com.r.dosc.presentation.scanning.components

import android.graphics.Bitmap
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize
import com.r.dosc.domain.models.ImageEditDetails
import com.roh.cropimage.InItCropView

@Composable
fun CropImageView(
    bitmap: Bitmap?,
    imageEditDetails: ImageEditDetails?,
    onCropEdgesChange: (Offset, Offset, Offset, Offset) -> Unit,
    indexSelectedImage: Int,
    cropRectSize: (IntSize) -> Unit
) {

    val showProgressBarState = remember { mutableStateOf(true) }
    val isEdgesUpdated = remember { mutableStateOf(true) }
    val index = remember { mutableStateOf(0) }


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
                modifier = Modifier.fillMaxSize(),
                onDragStart = {
                    isEdgesUpdated.value = false

                },
                onCropEdgesChange = { offset1, offset2, offset3, offset4 ->
                    onCropEdgesChange(offset1, offset2, offset3, offset4)
                }
            )
            showProgressBarState.value = false
            imageEditDetails?.let {

                if (it.isEdited && isEdgesUpdated.value || index.value != indexSelectedImage) {
                    cropView.updateCropPoints(
                        it.circleOne,
                        it.circleTwo,
                        it.circleThree,
                        it.circleFour
                    )
                }

                if (!it.isEdited && isEdgesUpdated.value || !it.isEdited && index.value != indexSelectedImage) {
                    cropView.resetView()
                }

                index.value = it.index

            }

        }.run {
            AnimatedVisibility(visible = showProgressBarState.value) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))

            }

        }

    }

}