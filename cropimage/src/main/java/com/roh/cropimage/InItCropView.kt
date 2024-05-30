package com.roh.cropimage

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp


class InItCropView(
    imgBitmap: Bitmap,
) : OnCrop {

    private var mBitmap: Bitmap = imgBitmap
    private lateinit var cropU: CropUtil


    @Composable
    fun ImageCropView(
        modifier: Modifier = Modifier,
        guideLineColor: Color = Color.LightGray,
        guideLineWidth: Dp = 1.dp,
        edgeCircleSize: Dp = 4.dp,
        onDragStart: () -> Unit = { },
        onCropEdgesChange: (IRect) -> Unit,
    ) {

        val cropUtil by remember { mutableStateOf(CropUtil(mBitmap)) }
        cropU = cropUtil

        Canvas(
            modifier = modifier
                .onSizeChanged { intSize ->
                    cropUtil.onCanvasSizeChanged(intSize = intSize)
                }
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { touchPoint ->
                            onDragStart()
                            cropUtil.onDragStart(touchPoint)
                        },
                        onDrag = { pointerInputChange, _ ->
                            // consume the drag points and update the rect
                            pointerInputChange.consume()
                            val dragPoint = pointerInputChange.position
                            cropUtil.onDrag(dragPoint)
                            /*if (cropUtil.rectEdgeTouched != RectEdge.NULL) {
                                onCropEdgesChange(cropUtil.iRect)
                            }*/
                        },
                        onDragEnd = {
                            onCropEdgesChange(cropUtil.iRect)
                            cropUtil.onDragEnd()
                        }
                    )
                },
            onDraw = {
                // Draw or Show image on rect
                val bm =
                    Bitmap.createScaledBitmap(
                        mBitmap,
                        cropUtil.canvasSize.canvasWidth.toInt(),
                        cropUtil.canvasSize.canvasHeight.toInt(),
                        false
                    )
                drawImage(image = bm.asImageBitmap())

                // Actual rect
                drawRect(
                    color = guideLineColor,
                    size = cropU.iRect.size,
                    topLeft = cropU.iRect.topLeft,
                    style = Stroke(guideLineWidth.toPx()),
                )

                // Vertical lines
                val verticalDiff = cropU.iRect.size.height / 3
                drawLine(
                    color = guideLineColor,
                    start = Offset(
                        cropU.iRect.topLeft.x,
                        (cropU.iRect.topLeft.y + verticalDiff)
                    ),
                    end = Offset(
                        (cropU.iRect.topLeft.x + cropU.iRect.size.width),
                        (cropU.iRect.topLeft.y + verticalDiff)
                    ),
                    strokeWidth = guideLineWidth.toPx(),
                )
                drawLine(
                    color = guideLineColor,
                    start = Offset(
                        cropU.iRect.topLeft.x,
                        (cropU.iRect.topLeft.y + (verticalDiff * 2))
                    ),
                    end = Offset(
                        (cropU.iRect.topLeft.x + cropU.iRect.size.width),
                        (cropU.iRect.topLeft.y + (verticalDiff * 2))
                    ),
                    strokeWidth = guideLineWidth.toPx(),
                )

                // Horizontal lines
                val horizontalDiff = cropU.iRect.size.width / 3
                drawLine(
                    color = guideLineColor,
                    start = Offset(
                        (cropU.iRect.topLeft.x + horizontalDiff),
                        cropU.iRect.topLeft.y
                    ),
                    end = Offset(
                        (cropU.iRect.topLeft.x + horizontalDiff),
                        (cropU.iRect.topLeft.y + cropU.iRect.size.height)
                    ),
                    strokeWidth = guideLineWidth.toPx(),
                )

                drawLine(
                    color = guideLineColor,
                    start = Offset(
                        (cropU.iRect.topLeft.x + (horizontalDiff * 2)),
                        cropU.iRect.topLeft.y
                    ),
                    end = Offset(
                        (cropU.iRect.topLeft.x + (horizontalDiff * 2)),
                        (cropU.iRect.topLeft.y + cropU.iRect.size.height)
                    ),
                    strokeWidth = guideLineWidth.toPx(),
                )

                // Rect edges
                // edge 1
                drawCircle(
                    color = guideLineColor,
                    center = cropU.iRect.topLeft,
                    radius = edgeCircleSize.toPx()
                )

                // edge 2
                drawCircle(
                    color = guideLineColor,
                    center = Offset(
                        (cropU.iRect.topLeft.x + cropU.iRect.size.width),
                        cropU.iRect.topLeft.y
                    ),
                    radius = edgeCircleSize.toPx()
                )


                // edge 3
                drawCircle(
                    color = guideLineColor,
                    center = Offset(
                        cropU.iRect.topLeft.x,
                        (cropU.iRect.topLeft.y + cropU.iRect.size.height)
                    ),
                    radius = edgeCircleSize.toPx()
                )

                // edge 4
                drawCircle(
                    color = guideLineColor,
                    center = Offset(
                        (cropU.iRect.topLeft.x + cropU.iRect.size.width),
                        (cropU.iRect.topLeft.y + cropU.iRect.size.height)
                    ),
                    radius = edgeCircleSize.toPx()
                )
            }
        )
    }

    override fun resetView() {
        cropU.resetCropIRect()
    }


    override fun updateCropRect(iRect: IRect) {
        cropU.updateOldRectSize(iRect)
    }

}

interface OnCrop {
    fun resetView()
    fun updateCropRect(iRect: IRect)
}