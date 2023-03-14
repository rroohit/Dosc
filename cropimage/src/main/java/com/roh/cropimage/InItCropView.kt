package com.roh.cropimage

import android.graphics.Bitmap
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.*
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.abs


class InItCropView(
    imgBitmap: Bitmap,
) : OnCrop {

    private var mBitmap: Bitmap
    private lateinit var cropU: CropUtil

    init {
        mBitmap = imgBitmap
    }


    @Composable
    fun ImageCropView(
        modifier: Modifier = Modifier,
        guideLineColor: Color = Color.LightGray,
        guideLineWidth: Dp = 2.dp,
        cornerCircleSize: Dp = 8.dp,
        onDragStart: () -> Unit = { },
        onCropEdgesChange: (Offset, Offset, Offset, Offset) -> Unit,

        ) {

        var selectedCircle by remember { mutableStateOf(SelectedCircle.NULL) }
        val cropUtil by remember { mutableStateOf(CropUtil(mBitmap)) }

        cropU = cropUtil

        val circleTouchArea = 120f

        Canvas(
            modifier = modifier
                .onSizeChanged {
                    cropUtil.updateBitmapSizeChange(it.width, it.height)
                }
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = {
                            onDragStart()
                            if (it.x - cropUtil.circleOne.x < circleTouchArea &&
                                it.x - cropUtil.circleOne.x > -circleTouchArea &&
                                it.y - cropUtil.circleOne.y < circleTouchArea &&
                                it.y - cropUtil.circleOne.y > -circleTouchArea
                            ) {

                                selectedCircle = SelectedCircle.ONE

                            } else if (it.x - cropUtil.circleTwo.x < circleTouchArea &&
                                it.x - cropUtil.circleTwo.x > -circleTouchArea &&
                                it.y - cropUtil.circleTwo.y < circleTouchArea &&
                                it.y - cropUtil.circleTwo.y > -circleTouchArea
                            ) {

                                selectedCircle = SelectedCircle.TWO

                            } else if (it.x - cropUtil.circleThree.x < circleTouchArea &&
                                it.x - cropUtil.circleThree.x > -circleTouchArea &&
                                it.y - cropUtil.circleThree.y < circleTouchArea &&
                                it.y - cropUtil.circleThree.y > -circleTouchArea
                            ) {

                                selectedCircle = SelectedCircle.THREE

                            } else if (it.x - cropUtil.circleFour.x < circleTouchArea &&
                                it.x - cropUtil.circleFour.x > -circleTouchArea &&
                                it.y - cropUtil.circleFour.y < circleTouchArea &&
                                it.y - cropUtil.circleFour.y > -circleTouchArea
                            ) {
                                selectedCircle = SelectedCircle.FOUR

                            } else if (abs(it.y - cropUtil.circleOne.y) < circleTouchArea) {

                                selectedCircle = SelectedCircle.LINEONE

                            } else if (abs(it.x - cropUtil.circleOne.x) < circleTouchArea) {

                                selectedCircle = SelectedCircle.LINETWO

                            } else if (abs(it.x - cropUtil.circleTwo.x) < circleTouchArea) {

                                selectedCircle = SelectedCircle.LINETHREE

                            } else if (abs(it.y - cropUtil.circleThree.y) < circleTouchArea) {

                                selectedCircle = SelectedCircle.LINEFOUR

                            } else {
                                selectedCircle = SelectedCircle.NULL
                            }
                        },
                        onDrag = { change, _ ->
                            change.consume()
                            if (change.position.x < size.width && change.position.x > 0f
                                && change.position.y < size.height && change.position.y > 0f
                            ) {
                                when (selectedCircle) {
                                    SelectedCircle.ONE -> {
                                        val offsetChanged =
                                            Offset(change.position.x, change.position.y)
                                        cropUtil.updateCircleOne(offsetChanged)


                                    }
                                    SelectedCircle.TWO -> {
                                        val offsetChanged =
                                            Offset(change.position.x, change.position.y)
                                        cropUtil.updateCircleTwo(offsetChanged)


                                    }
                                    SelectedCircle.THREE -> {
                                        val offsetChanged =
                                            Offset(change.position.x, change.position.y)
                                        cropUtil.updateCircleThree(offsetChanged)

                                    }
                                    SelectedCircle.FOUR -> {
                                        val offsetChanged =
                                            Offset(change.position.x, change.position.y)
                                        cropUtil.updateCircleFour(offsetChanged)


                                    }
                                    SelectedCircle.LINEONE -> {
                                        cropUtil.moveLineOne(
                                            Offset(
                                                change.position.x,
                                                change.position.y
                                            )
                                        )
                                    }
                                    SelectedCircle.LINETWO -> {
                                        cropUtil.moveLineTwo(
                                            Offset(
                                                change.position.x,
                                                change.position.y
                                            )
                                        )
                                    }
                                    SelectedCircle.LINETHREE -> {
                                        cropUtil.moveLineThree(
                                            Offset(
                                                change.position.x,
                                                change.position.y
                                            )
                                        )
                                    }
                                    SelectedCircle.LINEFOUR -> {
                                        cropUtil.moveLineFour(
                                            Offset(
                                                change.position.x,
                                                change.position.y
                                            )
                                        )
                                    }
                                    else -> Unit
                                }

                            } else {
                                selectedCircle = SelectedCircle.NULL
                            }

                        },
                        onDragEnd = {
                            onCropEdgesChange(
                                cropUtil.circleOne,
                                cropUtil.circleTwo,
                                cropUtil.circleThree,
                                cropUtil.circleFour,
                            )
                            selectedCircle = SelectedCircle.NULL
                        }
                    )
                },
            onDraw = {
                val bm =
                    Bitmap.createScaledBitmap(
                        mBitmap,
                        cropUtil.canvasWidth,
                        cropUtil.canvasHeight,
                        false
                    )


                //Cropping Image
                drawImage(image = bm.asImageBitmap())

                //Corner circles
                //1
                drawCircle(
                    color = guideLineColor,
                    center = cropUtil.circleOne,
                    radius = cornerCircleSize.toPx()
                )
                //2
                drawCircle(
                    color = guideLineColor,
                    center = cropUtil.circleTwo,
                    radius = cornerCircleSize.toPx()
                )
                //3
                drawCircle(
                    color = guideLineColor,
                    center = cropUtil.circleThree,
                    radius = cornerCircleSize.toPx()
                )
                //4
                drawCircle(
                    color = guideLineColor,
                    center = cropUtil.circleFour,
                    radius = cornerCircleSize.toPx()
                )


                val path = Path().apply {

                    //Image outlines/guidelines
                    //1
                    drawLine(
                        color = guideLineColor,
                        start = cropUtil.circleOne,
                        end = cropUtil.circleTwo,
                        strokeWidth = guideLineWidth.toPx()
                    )
                    //2
                    drawLine(
                        color = guideLineColor,
                        start = cropUtil.circleOne,
                        end = cropUtil.circleThree,
                        strokeWidth = guideLineWidth.toPx()
                    )
                    //3
                    drawLine(
                        color = guideLineColor,
                        start = cropUtil.circleTwo,
                        end = cropUtil.circleFour,
                        strokeWidth = guideLineWidth.toPx()
                    )
                    //4
                    drawLine(
                        color = guideLineColor,
                        start = cropUtil.circleThree,
                        end = cropUtil.circleFour,
                        strokeWidth = guideLineWidth.toPx()
                    )

                    //guide centre lines
                    //line 1
                    drawLine(
                        color = guideLineColor,
                        start = cropUtil.guideLineOne.start,
                        end = cropUtil.guideLineOne.end,
                        strokeWidth = guideLineWidth.toPx()
                    )

                    //line 2
                    drawLine(
                        color = guideLineColor,
                        start = cropUtil.guideLineTwo.start,
                        end = cropUtil.guideLineTwo.end,
                        strokeWidth = guideLineWidth.toPx()
                    )

                    //line 3
                    drawLine(
                        color = guideLineColor,
                        start = cropUtil.guideLineThree.start,
                        end = cropUtil.guideLineThree.end,
                        strokeWidth = guideLineWidth.toPx()
                    )

                    //line 4
                    drawLine(
                        color = guideLineColor,
                        start = cropUtil.guideLineFour.start,
                        end = cropUtil.guideLineFour.end,
                        strokeWidth = guideLineWidth.toPx()
                    )

                }
                drawPath(path, guideLineColor)

            }
        )
    }


    override fun resetView() {
        cropU.resetCropView()
    }

    override fun updateCropPoints(
        circleOne: Offset,
        circleTwo: Offset,
        circleThree: Offset,
        circleFour: Offset
    ) {
        cropU.updateCropEdges(circleOne, circleTwo, circleThree, circleFour)

    }

}

interface OnCrop {
    fun resetView()
    fun updateCropPoints(
        circleOne: Offset,
        circleTwo: Offset,
        circleThree: Offset,
        circleFour: Offset
    )
}